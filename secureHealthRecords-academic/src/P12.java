import iaik.pkcs.pkcs12.PKCS12;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import iaik.x509.X509Certificate;
import java.security.PrivateKey;
import java.security.PublicKey;
import iaik.pkcs.pkcs12.KeyBag;
import iaik.pkcs.pkcs12.CertificateBag;
import iaik.pkcs.PKCSParsingException;
import iaik.pkcs.PKCSException;
import java.io.IOException; 
import java.io.FileNotFoundException; 
import iaik.security.provider.IAIK; 
import java.security.*; 


/**
 * Aquesta classe representa un fitxer de Personal Information Exchange.
 * 
 * És un arxiu que conté la informació definida per l'estàndard PKCS#12 (Personal Information Exchange Syntax Standard)
 *  i en el format que aquest també defineix. Concretament, aquests fitxers contenen un certificat digital, juntament
 * amb la clau privada corresponent i els certificats de totes les autoritats de certificació fins a la que és arrel
 * (o, com sol dir-se, la cadena de certificació). Els fitxers ".p12" estan xifrats amb una contrasenya.
 *
 * @author <a href="mailto:jcastellar@uoc.edu">Jordi Castella-Roca</a>
 * @version 1.0
 */
public class P12{
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509Certificate[] chain;
    
    /**
     * Creates a new <code>P12</code> instance.
     *
     * @param fileName Un <code>String</code> que representa el nom del fitxer.
     * @param password Un <code>String</code> amb la contrasenya per a obrir el fitxer
     * @exception FileNotFoundException si ocorre un error.
     * @exception PKCSParsingException si ocorre un error.
     * @exception IOException si ocorre un error.
     * @exception PKCSException si ocorre un error.
     * @exception FileNotFoundException si ocorre un error.
     */
    public P12(String fileName,String password) throws FileNotFoundException, 
							      PKCSParsingException,
							      IOException,
							      PKCSException,
							      FileNotFoundException 
    {

	try {

	String friendlyName;
	byte[] localKeyID;
	String slocalKeyID;

	Security.insertProviderAt(new IAIK(), 2);

	FileInputStream fileInput = new FileInputStream(fileName);

	PKCS12 pkcs12 = new PKCS12(fileInput);

	//Verifiquem la integritat, es a dir, calculem el MAC
	pkcs12.verify(password.toCharArray());
	
	//Desxifrem el PKCS12
	pkcs12.decrypt(password.toCharArray());

	//Private Key
	KeyBag keyBag = pkcs12.getKeyBag();
	privateKey = keyBag.getPrivateKey();
	
	//Public Key
	//El locakKeyID ens vincula la clau privada amb la publica. Obtenim el localKeyID de la clau privada.
	//A continuacio cerquem a la llista de certificats el certificat que te el mateix localKeyID.
	friendlyName = keyBag.getFriendlyName();
	localKeyID = keyBag.getLocalKeyID();
	slocalKeyID = new String(localKeyID);

	CertificateBag[] certificateBag = pkcs12.getCertificateBags();

	chain = CertificateBag.getCertificates(certificateBag);

	setPublicKey(certificateBag,friendlyName,slocalKeyID);

	} catch ( FileNotFoundException o ) {
		
		javax.swing.JOptionPane.showMessageDialog(null, "Error obrint certificat en el fitxer P12", 
								"Error d'accés al fitxer P12",
								javax.swing.JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
  
    }
    
    /**
     * Aquest mètode retorna el certificat digital.
     *
     * @return a <code>X509Certificate[]</code> que representa el certificat digital.
     */
    public X509Certificate[] getCertificates(){
	return chain;
    }

    //Busca el certificat amb el slocalKeyID indicat com a parametre. 
    //El friendlyName es de propina, per imprimir-lo.
    private void setPublicKey(CertificateBag[] certificateBag, 
			      String friendlyName, 
			      String slocalKeyID)
    {
	int i;
	String friendlyNameCRT;
	byte[] localKeyIDCRT;
	String slocalKeyIDCRT;


	for(i=0;i<certificateBag.length;i++){
	    friendlyNameCRT = (certificateBag[i]).getFriendlyName();
	    localKeyIDCRT =  (certificateBag[i]).getLocalKeyID();
	    slocalKeyIDCRT = new String(localKeyIDCRT);
	    if(slocalKeyIDCRT.equals(slocalKeyID)){
		certificate = certificateBag[i].getCertificate();
		//System.out.println("ID: "+friendlyNameCRT+" -/- "+friendlyName);
		break;
	    }
	}

	publicKey = certificate.getPublicKey();
    }


    /**
     * Retorna la clau privada.
     *
     * @return <code>PrivateKey</code> amb la clau privada del P12.
     */
    public PrivateKey getPrivateKey(){
	return privateKey;
    }

    /**
     * Retorna la clau pública.
     *
     * @return <code>PublicKey</code> amb la clau pública del P12.
     */
    public PublicKey getPublicKey(){
	return publicKey;
    }

    /**
     * Retorna el certificat.
     *
     * @return Un <code>X509Certificate</code> que representa el certificat digital.
     */
    public X509Certificate getCertificate(){
	return certificate;
    }

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
	/*
    public static void main(String[] args){
	Security.insertProviderAt(new IAIK(), 2);	
	if(args.length == 2){	
	    try{
		P12 test = new P12(args[0],args[1]);
	    }catch(Exception e){
		e.printStackTrace();
	    }
	}else{
	    System.out.println("Usage: java P12 FILENAME PASSWORD");
	}
    }
	*/

}
