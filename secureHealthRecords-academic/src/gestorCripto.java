import java.security.cert.Certificate;
import iaik.x509.X509Certificate;
import iaik.security.provider.IAIK; 
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import iaik.pkcs.pkcs7.*;
import iaik.asn1.*;
import iaik.utils.*;
import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.PKCSException;

/**
 * La classe <code>gestorCripto</code> s'encarrega de tots els tràmits criptogràfics (xifratge i signatura).
 *
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs i Ballabriga</a>
 * @version 1.0
 */

public class gestorCripto {

	private X509Certificate[] _x509Chain;
    	private PrivateKey clauPrivada;
    	private PublicKey clauPublica;    	
	private P12 p12 = null;
	//private String pathCerts = "/home/gerard/pfc-historials/pki/Certificats/";
	//private String extCerts = "_PEM.crt";
	private boolean debug = false;

	
    /**
     * Constructor de <code>gestorCripto</code>.
     *
     * @param usuari Nom del certificat de l'usuari.
     * @param contrasenya Contrasenya per a obrir el fitxer en format P12.
     */
	public gestorCripto ( String usuari , String contrasenya, String pathP12 ) {

		try {
			String certfitxer =   usuari + ".p12";                        
			this.p12 = new P12( pathP12 , contrasenya );	
			this.clauPrivada = p12.getPrivateKey();

		} catch(Exception e){

	    		e.printStackTrace();
    		        System.exit(0);
		}

    	}

     /**
     *
     * Aquest mètode obté la clau pública d'aquest usuari a partir del seu certificat
     * 
     * @return a <code>X509Certificate[]</code> value
     */
     public PublicKey getClauPublica () {	
	
		return p12.getPublicKey();

     }

     /**
      *
      * Aquest mètode estableix el mode <code>Debug</code>
      *
      */
	public void setDebug (boolean a) {	
		this.debug = a;
	}

    /**
     *
     * Aquest mètode obté la clau pública del gestor del sistema
     * 
     * @return a <code>X509Certificate[]</code> value
     */
     public X509Certificate[] getCertificatGestor ( ) {	

		return getCertificatUsuari ("0" );	

     }

    /**
     * 
     * Aquest mètode retorna en format Base64 el FingerPrintSHA del certificat del Gestor.
     *
     * @return <code>String</code> amb el fingerPrintSHA en base64 del certificat del Gestor.
    */
     public String getFingerPrintSHAGestor() {

		X509Certificate[] a = this.getCertificatGestor();
		return Base64.encodeBytes( a[0].getFingerprintSHA() );
     }


    /**
     * 
     * Aquest mètode retorna en format Base64 el FingerPrintSHA del certificat d'un determinat usuari
     *
     * @return <code>String</code> amb el fingerPrintSHA en base64 del certificat de l'usuari especificat.
    */
     public String getFingerPrintSHAUsuari( String nhc ) {

		X509Certificate[] a = this.getCertificatUsuari ( nhc );
		return Base64.encodeBytes( a[0].getFingerprintSHA() );
     }




    /**
     *
     * Mètode per a xifrar una determinada cadena per a uns determinats receptors.
     *
     * @param msgtocrypt amb la cadena a xifrar
     * @param x509Chain amb els certificats digitals dels receptors
     * 
     * @return <code>String</code> amb la cadena xifrada en format base64.
     */
     public String xifra ( String msgtocrypt , X509Certificate[] x509Chain ) {			

		try{

			//Utilitzarem el TripleDES en el mode CBC per a xifrar el contingut
			EnvelopedData enveloped_data = new EnvelopedData( msgtocrypt.getBytes() , AlgorithmID.des_EDE3_CBC);
     
			RecipientInfo[] recipients = new RecipientInfo[x509Chain.length];
	    		int i;
			for(i=0; i < x509Chain.length;i++){
				    recipients[i] = new RecipientInfo(x509Chain[i], AlgorithmID.rsaEncryption);
			}

			enveloped_data.setRecipientInfos(recipients);

			String msgXifrat = Base64.encodeBytes( enveloped_data.getEncoded() );
			return msgXifrat;

		}catch(Exception e){

	    		e.printStackTrace();
	    		System.exit(0);
		}
		return null;
	
	}

    /**
     *
     * Aquest mètode desxifra una determinada cadena amb la clau privada del certificat digital de la classe.
     *
     * @param msgtodescrypt Amb la cadena a desxifrar en format Base64.
     * 
     * @return String Amb la cadena desxifrada en clar.
     */
     public String desxifra ( String msgtodescrypt ) {
		
	
		try{
			byte bytestodescript[] = Base64.decode( msgtodescrypt );
			ASN1Object obj = DerCoder.decode( bytestodescript );
			EnvelopedData enveloped_data = new EnvelopedData(obj);
			EncryptedContentInfo eci = (EncryptedContentInfo)enveloped_data.getEncryptedContentInfo();
	
			RecipientInfo[] recipients = enveloped_data.getRecipientInfos();

			int recipientInfoIndex = 0;
			enveloped_data.setupCipher(clauPrivada, recipientInfoIndex);			
			return new String ( enveloped_data.getContent());		    

		}catch(Exception e){
		    e.printStackTrace();
		    System.exit(0);
		}

		return null;
	}

	

	/**
    	 * El mètode <code>signa</code> signarà una determinada cadena
     	*
     	* @param dataIn a <code>String</code> per a signar
     	* @return <code>String</code> que conté, en format base64, una cadena signada
     	*/
    	public String signa(String dataIn){

		X509Certificate[] _chain = p12.getCertificates();
		PrivateKey clauPrivada = p12.getPrivateKey();
		byte[] p7Enc = null;	
		SignedData p7 = new SignedData(dataIn.getBytes(), SignedData.EXPLICIT);	
		p7.setCertificates( _chain );             
		SignerInfo signer = new SignerInfo(new IssuerAndSerialNumber( _chain[0] ), 
					   AlgorithmID.sha1, 
					   clauPrivada);
	try{
	    	p7.addSignerInfo(signer);
		p7Enc = p7.getEncoded();
	}catch(Exception e){
	    	e.printStackTrace();
	    	System.exit(0);
	}
	
	return Base64.encodeBytes ( p7Enc );
    }


	/**
	*	Aquest mètode signa i xifra una cadena i retorna la seva signatura.
	*
	*	@param missatge La cadena amb el missatge a signar i xifrar.
	* 	@param destinatari Un array amb els certificats digitals dels destinataris.
	*
	*	@return <code>String</code> amb la cadena en format Base64 signada i xifrada.
	*
	*/	
	public String signaXifra( String missatge , X509Certificate[] destinatari ) throws NoSuchAlgorithmException, PKCSException {
		
		int i;

		//Creem un objecte del tipus SignedAndEnvelopedData amb el missatge en clar i l'algoritme de xifrat
		SignedAndEnvelopedData saed = new SignedAndEnvelopedData( missatge.getBytes() , AlgorithmID.des_EDE3_CBC);

		//Afegim els certificats dels qui signen
		saed.setCertificates( p12.getCertificates() );
		
		X509Certificate[] _x509certificates = p12.getCertificates();
		IssuerAndSerialNumber issuer_and_serialNr = new IssuerAndSerialNumber ( _x509certificates[0] );
		SignerInfo signerinfo = new SignerInfo ( issuer_and_serialNr , AlgorithmID.sha, clauPrivada );
		saed.addSignerInfo ( signerinfo );
		
		RecipientInfo[] recipient = new RecipientInfo[destinatari.length];
		for(i=0;i<destinatari.length;i++){
	    		recipient[i] = new RecipientInfo(destinatari[i], AlgorithmID.rsaEncryption);
	    		saed.addRecipientInfo(recipient[i]);
		}

		return Base64.encodeBytes ( saed.getEncoded() );
		
	}  

	/**
	*	Retorna en <code>fingerPrint</code> en format SHA del certificat digital.
	*
	*	@return FingerPrint en format SHA.
	*/
	public String getFingerprintSHA() {

		X509Certificate[] c = p12.getCertificates();
	    	return Base64.encodeBytes ( c[0].getFingerprintSHA() );
	}

    /**
     *
     * @param nhc_pacient Un<code>String</code> amb l'identificador del client a cercar      
     * @return a <code>X509Certificate[]</code> value
     */
     public X509Certificate[] getCertificatUsuari (String nhc_pacient ) {	

	try	{

		


		//InputStream is = new FileInputStream( this.pathCerts + id + this.extCerts);
		//X509Certificate cert = new X509Certificate(is);

		String sql = "SELECT certificat FROM `dtcertificats` WHERE idusuari='"+ nhc_pacient +"'";
		//System.out.println ( sql );
		X509Certificate certi = null;

		if (this.debug) {
			System.out.println ( "[gestorCripto]" + sql );
		}
		
		try {
				gestorBBDD a = new gestorBBDD();
				java.sql.ResultSet rs = a.executaSelectSQL( sql ) ;
				rs.next();
				String cert = rs.getString("certificat");				
				certi = new X509Certificate ( cert.getBytes() ) ;

		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = certi;
		return chain;

	} catch(Exception e){

    		e.printStackTrace();
 		System.exit(0);
		return null;
	}

     }

     /**
     * Aquest mètode verifica una signatura digital.
     *
     * @param signatura La cadena en format Base64 que representa la signatura.
     * @param missatge El missatge font amb el que s'ha generat la signatura.
     * @param fingerPrint Un <code>string</code> en format base64 amb el fingerPrintSHA del certificat amb el que volem validar la signatura
     *
     * @return Retorna un <code>boolean</code>: <b>True</b> si la signatura és correcta. False si la signatura és incorrecta.
     */
    public boolean verificaSignatura( String signatura, String missatge, String fingerPrint) {
	
	AlgorithmID[] algIDs = { AlgorithmID.sha1, AlgorithmID.md5 };	
	SignedData signature = null;
	
	try{
	    signature = new SignedData( missatge.getBytes() , algIDs);
	    ASN1Object objExpP7 = null;
	    objExpP7 = DerCoder.decode( Base64.decode (signatura) );
	    signature.decode(objExpP7);
	    SignerInfo[] signerInfos = signature.getSignerInfos();

	    for (int i=0; i < signerInfos.length; i++){

		X509Certificate signer_cert = signature.verify(i);
		X509Certificate certGestor[] = this.getCertificatGestor();

		String fingerBase64A = Base64.encodeBytes ( signer_cert.getFingerprintSHA() );						
		if ( fingerBase64A.compareTo( fingerPrint ) > 0  ) return false;
			
			
		}

		return true;

	} catch(java.security.SignatureException e) {
			return false;
	

	} catch(Exception e)	{

		    e.printStackTrace();
		    System.exit(0);
		    return false;
	}
	}

	
}
