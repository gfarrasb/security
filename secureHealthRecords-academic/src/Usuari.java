import java.security.cert.Certificate;
import iaik.x509.X509Certificate;
import iaik.security.provider.IAIK; 
import java.security.*; 
import java.util.Random;
import java.io.*;
import iaik.pkcs.pkcs7.*;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.DerCoder;
import iaik.asn1.ASN1Object;
import iaik.utils.Util;
import org.jdom.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;

/**
 *
 * Aquesta classe representa un usuari genèric amb mètodes iguals a metges i pacients. Serà una classe abstracta.
 *
 * @author <a href="mailto:jcastellar@uoc.edu">Gerard Farràs Ballabriga</a>
 * @version 1.0
 */
public abstract class Usuari {

	String nhc = null;
	int aNi = 0; 
	gestorCripto gestorCripto;	
	boolean debug;

    /**
     * Contructor de la clases <code>Usuari</code>.
     *
     */

	public Usuari () {

		//this ( new String("8006") );
	}


    /**
     * Contructor de la clases <code>Usuari</code>.
     *
     * @param nhc Un <code>String</code> amb l'identificador de l'usuari.
     */	
	public Usuari ( String nhc, String passwd, String pathp12 ) {
	
	try {

	    this.nhc = nhc;
	    this.gestorCripto = new gestorCripto ( nhc , passwd, pathp12);
	    //this.gestorBBDD = new gestorBBDD();		    
	    this.debug = false;

	} catch(Exception e){

	    		e.printStackTrace();
    		        System.exit(0);
	}

     }

	/**
	*
	* Aquest mètode estableix el mode <code>Debug</code>
	*
	*/
	public void setDebug (boolean a) {	
		this.debug = a;
		this.gestorCripto.setDebug ( a );
	}


    /**
     * Mètode que genera un número aleatori entre 0 i 99999.
     * 
     * @return <code>int</code> amb el número aleatori.
     */
      public int obtenirAleatori () {

		Random vAleatoriRandom = new Random ();
		return vAleatoriRandom.nextInt ( 99999 );		
     }

    /**
     * Mètode que implementa el <code>Procedure1</code>.<br /><br />
     *
     * Executa els passos següents:</br>
     *
     * <ul>
     * <li><b>Pas 1.</b> Obtenir un valor de forma aleatòria, Ni.   </li>
     * <li><b>Pas 2.</b> Xifrar Ni i Id_usuariU amb la clau pública de G, Pg[Ni,Id_usuariu].</li>
     * <li><b>Pas 3.</b> Enviar Pg[Ni,Id_usuariu] a G.</li>
     * </ul> 
     *
     * 
     * @return <code>String</code> en format <b>Base64</b> amb el resultat de Pg[Ni,Id_usuariu].
     */
 
     public Document procedure1 () {

	this.aNi = this.obtenirAleatori();
	String c = aNi + "-" + this.nhc;
	if (this.debug) System.out.println("[Usuari][Procedure1] Xifrem: " + c );
	//return gestorCripto.xifra ( c , gestorCripto.getCertificatGestor());

	Element nodePfc  = new Element("Pfchistorials");
	Element nodeProcedure1 = new Element("Procedure1");
	nodePfc.addContent ( nodeProcedure1 );
	nodeProcedure1.addContent ( gestorCripto.xifra ( c, gestorCripto.getCertificatGestor() ));
	return new Document ( nodePfc );

	/*
	FileOutputStream fileOut = null;

	try{

	    fileOut = new FileOutputStream("procedure1.xml");
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}
	
	try{
	    //Nou
	    Document finalDoc = new Document(nodePfc);
	    XMLOutputter xmlout = new XMLOutputter();

	    //Nou
	    xmlout.output(finalDoc, fileOut);
	    fileOut.close();

	    return finalDoc;

	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}

	    return null;
	*/
     }   

     /**
       * Mètode que implementa el <code>Pas3</code> del Procedure 1.<br /><br />
       *
       * Executa els passos següents:</br>
       *
       * <ul>
       * <li><b>(a)</b> Desxifrem Pu[Ni, Ng, Id_UsuariG] amb la clau privada Su i obtenim Ng, Ni i Id_UsuariG</li>
       * <li><b>(b)</b> Si Ni' == Ni fer:
       * 	<ul>
       *		<li>i. Xifrar Ng, Consulta, Id_Usuari amb la clau pública Pg de G.<br />
       *		       Consulta indica que es vol consultar l'historial de l'usuari identificat amb Id_usuari
       *		</li>
       *		<li>ii. Enviar Pg[Ng, Consulta, Id_Usuari] a G.</li>
       * 	</ul>
       * </li>
       * <li><b>(c)</b> Sino, retornar error.
       * </ul>
       *
       *
     */
      org.jdom.Document pas3 ( org.jdom.Document a , String strOperacio ) {

	Element nodeRoot = a.getRootElement ();
	Element nodeProcedure2 = nodeRoot.getChild("Procedure2");

	String messageRec = gestorCripto.desxifra ( nodeProcedure2.getText() );
	if (this.debug) System.out.println("[Usuari][Pas 3] El missate desxifrat es " + messageRec);

	String strbNi = messageRec.substring ( 0, messageRec.indexOf("-") );
	int bNi = java.lang.Integer.valueOf(strbNi).intValue();
	String cadRestant = messageRec.substring ( messageRec.indexOf("-") + 1 , messageRec.length() );
	String Ng = cadRestant.substring ( 0, cadRestant.indexOf("-") );
	String Id_UsuariG = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );

	if (this.debug) {
		System.out.println("[Usuari][Pas 3] Ni val " + bNi );
		System.out.println("[Usuari][Pas 3] Ng val " + Ng );
		System.out.println("[Usuari][Pas 3] Id_UsuariG val " + Id_UsuariG );
	}

	/*
		Si aNi != bNi --> ERROR!
	*/
	
	if ( aNi == bNi ) {
		if (this.debug) System.out.println("[Usuari][Pas 3] Comprovació correcta entre números aleatòris!"); 
	} else {
		if (this.debug) System.out.println("[Usuari][Pas 3] Comprovació INCORRECTA entre números aleatòris!"); 
		System.exit(0);
	}

	/*
	
	*/
	String il = Ng + "-" + strOperacio + "-" + this.nhc;
	if (this.debug) System.out.println("[Usuari][Pas 3] Retornem de forma xifrada al Gestor: " + il);

	Element nodePfc  = new Element("Pfchistorials");
	Element nodePas3 = new Element("Pas3");
	nodePfc.addContent ( nodePas3 );
	nodePas3.addContent ( gestorCripto.xifra ( il, gestorCripto.getCertificatGestor() ));
	//return new Document ( nodePfc );
	//return gestorCripto.xifra ( il, gestorCripto.getCertificatGestor() );
	return new Document ( nodePfc );

	/*
	FileOutputStream fileOut = null;

	try{

	    fileOut = new FileOutputStream("pas3.xml");
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}
	
	try{
	    //Nou
	    Document finalDoc = new Document(nodePfc);
	    XMLOutputter xmlout = new XMLOutputter();

	    //Nou
	    xmlout.output(finalDoc, fileOut);
	    fileOut.close();

	    return finalDoc;
		//return new Document ( nodePfc );

	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}

	    return null;
	*/	
	
     }

	/**
	*	Aquest procediment l'executa un pacient/metge per a desxifrar el seu historial.<br /><br />
        *
	*	<ul>
	*	<li><b>Pas 1.</b> Desxifrar Pu[H] amb la clau privada Su de U, Su[Pu[H]].</li>
	*	<li><b>Pas 2.</b> Per a cada entrada de l'historia H que està signada fer:</li>
	*	<li>
	*	<ul>
	*		<li>(a) Verificar la signatura digital del metge</li>
	*		<li>(b) Verificar la signatura digital del gestor</li>
	*		<li>(c) Verificar la seqüència</li>
	*	</ul>
	*	</li>
	*	<li><b>Pas 3.</b> Retornem M.</li>
	*
	*	@param historiaXifrada 
	*
	**/
	public java.util.Vector procedure4 ( org.jdom.Document historiaXifrada ) {

		org.jdom.Element PfchistorialsElement = historiaXifrada.getRootElement();
		org.jdom.Element pacientElement = PfchistorialsElement.getChild("Pacient");
		String pacient = gestorCripto.desxifra ( pacientElement.getText() );		
		org.jdom.Element historiaClinicaElement = PfchistorialsElement.getChild("HistoriaClinica");
		java.util.List lDiag = historiaClinicaElement.getChildren ( "Diagnostic" );

		//String historiaClinica = new String("");
		java.util.Vector<String> historiaClinica = new java.util.Vector<String>();
		boolean apuntCorrecte;

		for (int i=0; i< lDiag.size(); i++ ) {

			apuntCorrecte = true;
			org.jdom.Element diagnostic = (org.jdom.Element) lDiag.get ( i );

			String X = (diagnostic.getChild("X")).getText();
			X = gestorCripto.desxifra ( X );

			String T = (diagnostic.getChild("T")).getText();
			T = gestorCripto.desxifra ( T );

			String pgvsmv = (diagnostic.getChild("Pgvsmv")).getText();		
			pgvsmv = gestorCripto.desxifra ( pgvsmv );

			//El text anterior hauria d'estar xifrat 
			String cie = pgvsmv.substring ( 0, pgvsmv.indexOf("-") );
			
			pgvsmv = pgvsmv.substring ( pgvsmv.indexOf("-") + 1 , pgvsmv.length() );
			String apunt = pgvsmv.substring ( 0, pgvsmv.indexOf("-") );

			pgvsmv = pgvsmv.substring ( pgvsmv.indexOf("-") + 1 , pgvsmv.length() );
			String signaturaCieApunt = pgvsmv.substring ( 0, pgvsmv.length() );

		/*
		if (gestorCripto.verificaSignatura ( signaturaCieApunt , pacient + "-" + cie + "-" + apunt , )) {
			System.out.println("Verificacio de l'apunt correcta! ");
		} else {
			System.out.println("Verificacio de l'apunt INCORRECTA!");
		}*/
			

			//Validem la signatura anterior amb Sg[V, Sm[V], T, X+1] amb la clau pública del gestor
			String Sgvsmvtx = (diagnostic.getChild("Sgvsmvtx")).getText();
			Sgvsmvtx = gestorCripto.desxifra ( Sgvsmvtx );

			String SgvAValidar = pacient + "-" + cie + "-" + apunt + "-" + signaturaCieApunt + "-" + T + "-" + X;
			if (!gestorCripto.verificaSignatura( Sgvsmvtx, SgvAValidar , gestorCripto.getFingerPrintSHAGestor() )) {
				//System.out.println("Signatura de l'apunt INCORRECTA!");
				//System.exit(-1);
				apuntCorrecte = false;
			}

			//Validem la signatura  Sg[X+1, Id_usuarip] amb la clau pública del gestor
			String Sgxidusuarip = (diagnostic.getChild("Sgxidusuarip")).getText();
			Sgxidusuarip = gestorCripto.desxifra ( Sgxidusuarip );

			String SgxidusuaripxValidar = pacient + "-" + X;

			if (!gestorCripto.verificaSignatura ( Sgxidusuarip , SgxidusuaripxValidar , gestorCripto.getFingerPrintSHAGestor () )) {
				//System.out.println("Signatura de l'apunt INCORRECTA!");
				//System.exit(-1);
				apuntCorrecte = false;
			}

			//System.out.println("[" + T + "]" + " CIE: " + cie + " - " + apunt );	
			//historiaClinica = historiaClinica.concat ( "[" + T + "]" + " CIE: " + cie + " - " + apunt + '\n');
			//historiaClinica = historiaClinica.concat ( "[" + T + "]" + "-" + cie + "-" + apunt + '\n' );

			if ( apuntCorrecte == true ) {
				historiaClinica.add ( new String ( T + "-" + cie + "-" + apunt ));
			}
		}
		
		//return gestorCripto.desxifra ( historiaXifrada );		
		//return null;
		return historiaClinica;
	
	}


	
	/**
	*	Aquest mètode mira si un determinat usuari és un metge o no.
	*
	* @return <code>True</code> si és cert.
	* @return <code>False</code> si és fals.
	**/
	public boolean somMetge () {

		boolean vRetorn;

		X509Certificate c[] = gestorCripto.getCertificatUsuari ( this.nhc );
		Name name = (Name)c[0].getSubjectDN();	
		String organizationalUnit = name.getRDN(ObjectID.organizationalUnit);

		if (organizationalUnit.compareTo ("Metges") == 0) {
			vRetorn = true;
		} else {
			vRetorn = false;
			//System.out.println("[Error] L'usuari amb id " + nhc_metge + " NO disposa del certificat d'un metge");
			//System.exit(-1);
		}
		
		return vRetorn;
	}


	
}
