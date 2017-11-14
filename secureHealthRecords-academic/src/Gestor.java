import java.security.cert.Certificate;
import iaik.x509.X509Certificate;
import iaik.security.provider.IAIK; 
import java.security.*;
import java.util.Random; 
import java.io.*;
import org.jdom.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;

/**
 *
 * Aquesta classe representa el gestor del sistema.
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs Ballabriga</a>
 * @version 1.0
 */
public class Gestor extends java.rmi.server.UnicastRemoteObject implements GestorInterRemot {

	String idUsuariG = null;
	gestorCripto gestorCripto;
	gestorBBDD gestorBBDD;
	boolean debug;
	private final static long serialVersionUID = 42L;

	//int aNg = 0;

    /**
     * Constructor de la classe <code>Gestor</code>.
     *
     */
	public Gestor ( String pathP12, String passwd ) throws java.rmi.RemoteException {
	
	try	{
  
	    //this.gestorCripto = new gestorCripto ("0", "uoc07" , "/home/gerard/pfc-historials/entrega/pki/gestor.p12" );
	    this.gestorCripto = new gestorCripto ("0", passwd , pathP12 );		    		    
	    this.idUsuariG = gestorCripto.getFingerprintSHA();	    
	    this.gestorBBDD = new gestorBBDD();
	    this.debug = false;

	} catch(Exception e){

	    		e.printStackTrace();
    		        System.exit(0);
	}

     }

	public void estableixConnexioMySQL ( String hostMysql, String bbddMysql, String usrMysql, String passwdMySQL ) {

		this.gestorBBDD = new gestorBBDD ( );
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
     * El mètode <code>obtenirAleatori</code> genera un <code>int</code> aleatori entre 0 i 99999.
     * 
     * @return <code>int</code> value
     */
      public int obtenirAleatori () {

		Random vAleatoriRandom = new Random ();
		return vAleatoriRandom.nextInt ( 99999 );		
     }
    

   /**
     * Procedure 2 (Pu).<br /><br />
     *
     * S'executen els passos següents:<br /><br />
     *
     * <ul>
     *	<li><b>1.</b> Desxifrar Pg[Ni,Id_Usuariu] amb Sg i obtenir; Ni i IdUsuariu.</li>
     *
     *	<li><b>2.</b> Obtenim el certificat de U a partir de Id_usuari u. Suposem que el sistema 
     *	  disposa d'una base de dades on, per a cada Id_Usuari trobem el seu certificat
     *	  corresponent. A partir del certificat es pot obtenir la clau pública Pu.</li>
     *
     *  <li><b>3.</b> Obtenir un valor de forma aleatòria Ng.</li>
     *
     *  <li><b>4.</b> Guardar a la BBDD els valors Ni i Ng associats amb U;
     *
     *  <li><b>5.</b> Xifrar Ni,Ng,Id_usuariG, amb la clau pública Pu de U, Pu[Ni,Ng,Id_usuariG].
     * 
     *  <li><b>6.</b> Retornar Pu[Ni, Ng, Id_usuariG].
     *
     * @param xmlProcedure1 Un <code>Document</code> amb l'Xml del Procedure1
     * 
     * @return Un <code>string</code> que representa Pu[Ni, Ng, Id_usuariG].
     */
 
	public org.jdom.Document procedure2 ( Document xmlProcedure1 ) throws java.rmi.RemoteException {

		org.jdom.Element PfchistorialsElement = xmlProcedure1.getRootElement();
		org.jdom.Element procedure1Element = PfchistorialsElement.getChild("Procedure1");
		String pgniidusuariu = procedure1Element.getText();

		/* Desxifrem el missatge PG[ Ni, Id_Usuariu] amb Sg i obtenim Ni i IdUsuari	*/
		String messageRec = gestorCripto.desxifra ( pgniidusuariu );
		String Ni = messageRec.substring ( 0, messageRec.indexOf("-") );
		String Id_UsuariU = messageRec.substring ( messageRec.indexOf("-") + 1 , messageRec.length() );
	
		/* 

		  Obtenim el certificat de U a partir de Id_usuari u. Suposem que el sistema 
		  disposa d'una base de dades on, per a cada Id_Usuari trobem el seu certificat
		  corresponent. A partir del certificat es pot obtenir la clau pública Pu.

        	*/

		X509Certificate[] certificatU = gestorCripto.getCertificatUsuari( Id_UsuariU );	
	
		/*
		   Obtenir un valor de forma aleatòria Ng;
		*/
		int aNg = this.obtenirAleatori();

		/*
		   Guardar a la BBDD els valors aNg i Ni
		*/
		desaDataBaseValorsAleatoris ( Ni, aNg, Id_UsuariU );
	
		/*
		   Xifrar Ni, Ng, Id_UsuariG, amb la clau pública de Pu de U PU[ Ni, Ng, Id_UsuariG ]
		*/
		String mssXifrar = new String ( Ni + "-" + aNg + "-" + idUsuariG );
		//return gestorCripto.xifra ( mssXifrar , certificatU );

		Element nodePfc  = new Element("Pfchistorials");
		Element nodeProcedure2 = new Element("Procedure2");
		nodeProcedure2.addContent ( gestorCripto.xifra ( mssXifrar , certificatU ) );
		nodePfc.addContent ( nodeProcedure2 );

		/*
		FileOutputStream fileOut = null;

		try{

		    fileOut = new FileOutputStream("procedure2.xml");
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
		*/

	
		return new org.jdom.Document ( nodePfc );

     }

	/**
	* 	Pas 4 del protocol 2.<br /><br />
	*
	*
	*	El Gestor realitza les operacions següents:<br />
	*
	*	<ul>
	*	<li><b>(a)</b> Desxifrar Pg[Ng, Consulta, Id_Usuari] amb la seva clau privada Sg i obtenim bNg, Consulta i Id_usuari.</li>
	*	<li><b>(b)</b> Recuperem Ng de la BBDD. En el pas 4 del procedure 4, Ng i Ni han d'estar desats en la base de dades.</li>
	*	<li><b>(c)</b> si bNg == aNg fem:</li>
	*	<li>
	*	<ul>
	*		
	*		<li>i. Si ( Id_usuariu == Id_usuari) o (Id_usuariU és metge i Id_usuari és un pacient de Id_usuari) fer:
	*			<ul>
	*			//!! NO! A. Executar el Procedure 3 amb Id_usuari i Pu i obtenir Pu[H].
	*			<li>A. Executar l'operació que ens envïi l'usuari.</li>
	*			<li>B. Enviar Pu[H] a U.</li>
	*			</ul>
	*		</li>
	*
	*		<li>ii. Sino, retornar error.</li>
	*	</ul>
	*
	*
	*	<li>(d) Sino retornar error.</li>
	*	
	* 	<li>(e) Suprimir Ng i Ni de la BBDD.</li>
	*	</ul>
	*
	**/
      public boolean pas4 ( org.jdom.Document docPas3 ) throws java.rmi.RemoteException {

		boolean vRetorn;

		Element nodeRoot = docPas3.getRootElement();
		Element nodePas3 = nodeRoot.getChild("Pas3");		

		/* Pas a */
		String messageRec = gestorCripto.desxifra ( nodePas3.getText() );
		String strbNg = messageRec.substring ( 0, messageRec.indexOf("-")  );
		int bNg = java.lang.Integer.valueOf(strbNg).intValue();
		String cadRestant = messageRec.substring ( messageRec.indexOf("-") + 1 , messageRec.length() );
		String operacio = cadRestant.substring ( 0, cadRestant.indexOf("-") );
		String id_usuari = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );

		if (this.debug) {
			System.out.println("[Gestor-Pas4] strbNg val " + strbNg );
			System.out.println("[Gestor-Pas4] operacio val " + operacio );
			System.out.println("[Gestor-Pas4] id_usuari val " + id_usuari );
		}
		
		/* Pas b */
		int aNg = recuperaDataBaseNg ( id_usuari ) ;

		/* Pas c */

		if (aNg == bNg) {
			vRetorn = true;
		} else {
			vRetorn = false;
		}

		/* Pas e */
		this.suprimeixDataBaseValorsAleatoris ( id_usuari );

		return vRetorn;
		
      }

	/**
	* 	Pas 4 del protocol 3.<br /><br />
	*
	*
	*	El Gestor realitza les operacions següents:<br />
	*
	*	<ul>
	*	<li><b>(a)</b> Desxifrar Pg[Ng, Inserir_Visita, V, Sm[V]] amb la seva clau privada Sg i obtenim bNg, Inserir_Visita, V i Sm[V].</li>
	*	<li><b>(b)</b> Recuperem Ng de la BBDD. En el pas 4 del procedure 4, Ng i Ni han d'estar desats en la base de dades.</li>
	*	<li><b>(c)</b> si bNg == aNg fem:</li>
	*	<li>
	*	<ul>
	*		
	*		<li>i. Obtenir Id_Usuarip a partir de V.</li>
	*		<li>ii. Verificar que Id_Usuarim és un metge.</li>
	*		<li>iii. Verificar que Id_Usuarip és un pacient assignat a Id_usuariM.</li>
	*		<li>iv. Si les verificacions anteriors son correctes fer:</li>
	*		<li>
	*			<ul>
	*				<li>A. Verificar la signatura digital Sm[V] amb la clau pública Pm</li>
	*				<li>B. Obtenir l'instant de temps actual T;</li>
	*				<li>C. Obtenir el número de sèrie X de la última visita de l'historial H del pacient Id_usuarip.</li>
	*				<li>D. Incrementar en una unitat X, X+1.</li>
	*				<li>E. Signar V, Sm[V], T, X, X+1, amb la clau privada Sg de G, Sg[V,Sm[V], T, X+1].</li>
	*				<li>F. Xifrar V i Sm[V] amb la clau pública Sg de G, Pg[V, Sm[V]].</li>
	*				<li>G. Signar Id_usuarip i X+1 amb la clau privada Sg de G, Sg[X+1, Id_usuarip].</li>
	*				<li>H. Guardar a la BD Pg[V, Sm[V]], X+1, T, Sg[V,Sm[V], T, X+1] i Sg[X+1, Id_usuarip].</li>
	*			<ul>
	*		</li>
	*		<li>v. Sino retornar error.</li>
	*	<li><b>(d)</b> Sino retornar error.</li>
	*	</ul>
	*	</ul>
	*
	**/
      public boolean pas4InserirVisita ( String a ) throws java.rmi.RemoteException {

		boolean vRetorn;

		/* Pas a */
		String messageRec = gestorCripto.desxifra ( a );
		String strbNg = messageRec.substring ( 0, messageRec.indexOf("-")  );
		int bNg = java.lang.Integer.valueOf(strbNg).intValue();
		String cadRestant = messageRec.substring ( messageRec.indexOf("-") + 1 , messageRec.length() );
		String nhc_metge = cadRestant.substring ( 0, cadRestant.indexOf("-") );
		cadRestant = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );
		String nhc_pacient = cadRestant.substring ( 0, cadRestant.indexOf("-") );
		cadRestant = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );
		String codiCie = cadRestant.substring ( 0, cadRestant.indexOf("-") );
		cadRestant = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );
		String apunt = cadRestant.substring ( 0, cadRestant.indexOf("-") );
		cadRestant = cadRestant.substring ( cadRestant.indexOf("-") + 1, cadRestant.length() );
		String signatura = cadRestant.substring ( 0, cadRestant.length() );		

		if (this.debug) {
			System.out.println("[Gestor-Pas4] strbNg val " + strbNg );
			System.out.println("[Gestor-Pas4] nhc_metge val " + nhc_metge );
			System.out.println("[Gestor-Pas4] nhc_pacient val " + nhc_pacient );
			System.out.println("[Gestor-Pas4] codiCie val " + codiCie );
			System.out.println("[Gestor-Pas4] apunt val " + apunt );
			//System.out.println("[Gestor-Pas4] signatura val " + signatura );
		}
		
		/* Pas b */
		int aNg = recuperaDataBaseNg ( nhc_metge ) ;

		/* Pas c */		
		if (aNg == bNg) {
			vRetorn = true;			
			pas4InserirVisitaPuntC(nhc_pacient, nhc_metge, codiCie, apunt, signatura);
		} else {
			vRetorn = false;
		}

		/* Pas e */
		this.suprimeixDataBaseValorsAleatoris ( nhc_metge );

		return true;
		
      }

	/**	
	*
	*	Punt c del pas 4 del protocol 3.<br /><br />
	*
	*	El Gestor realitza les operacions següents:<br />
	*		<ul>
	*		
	*		<li>i. Obtenir Id_Usuarip a partir de V.</li>
	*		<li>ii. Verificar que Id_Usuarim és un metge.</li>
	*		<li>iii. Verificar que Id_Usuarip és un pacient assignat a Id_usuariM.</li>
	*		<li>iv. Si les verificacions anteriors son correctes fer:</li>
	*		<li>
	*			<ul>
	*				<li>A. Verificar la signatura digital Sm[V] amb la clau pública Pm</li>
	*				<li>B. Obtenir l'instant de temps actual T;</li>
	*				<li>C. Obtenir el número de sèrie X de la última visita de l'historial H del pacient Id_usuarip.</li>
	*				<li>D. Incrementar en una unitat X, X+1.</li>
	*				<li>E. Signar V, Sm[V], T, X, X+1, amb la clau privada Sg de G, Sg[V,Sm[V], T, X+1].</li>
	*				<li>F. Xifrar V i Sm[V] amb la clau pública Sg de G, Pg[V, Sm[V]].</li>
	*				<li>G. Signar Id_usuarip i X+1 amb la clau privada Sg de G, Sg[X+1, Id_usuarip].</li>
	*				<li>H. Guardar a la BD Pg[V, Sm[V]], X+1, T, Sg[V,Sm[V], T, X+1] i Sg[X+1, Id_usuarip].</li>
	*			<ul>
	*		</li>
	*		<li>v. Sino retornar error.</li>
	*	<li><b>(d)</b> Sino retornar error.</li>
	*	</ul>
	*/

void pas4InserirVisitaPuntC( String nhc_pacient, String nhc_metge, String codiCie, String apunt, String signatura)  throws java.rmi.RemoteException {

		//Verifiquem que el Id_usuarim és un metge
		verEsMetge ( nhc_metge );

		//Verificar que Id_Usuarip és un pacient assignat a Id_usuariM.
	
		if ( verPacMetge ( nhc_pacient, nhc_metge ) == false ) {
			System.out.println("Error: Aquest pacient NO està assignat a aquest metge");	
			System.exit(-1);
		}

	

		//Punt A. Verificar la signatura digital Sm[V] amb la clau pública Pm
 if ( gestorCripto.verificaSignatura (signatura, nhc_pacient + "-" + codiCie + "-" + apunt, gestorCripto.getFingerPrintSHAUsuari(nhc_metge)  )) {

		} else {

			System.out.println("[Error] La signatura digital associada a aquest apunt és INCORRECTA");
			System.exit(-1);

		}

		//Punt B. Obtenir l'instant de temps actual T;
		String tActual = getTempsActual();

		//Punt C. Obtenir el número de sèrie X de la última visita de l'historial H del pacient Id_usuarip;
		//Punt D. Incrementar en una unitat X, X+1
		String numSerieX = getNumSerieX ( nhc_pacient, codiCie );		

		//Punt E. Signar V, Sm[V], T, X, X+1, amb la clau privada Sg de G, Sg[V,Sm[V], T, X+1].
		String V = nhc_pacient + "-" + codiCie + "-" + apunt;
		String puntExSignar = V + "-" + signatura + "-" + tActual + "-" + numSerieX;
		String puntExSignat = gestorCripto.signa ( puntExSignar );

		//Punt F. Xifrar V i Sm[V] amb la clau pública Sg de G, Pg[V, Sm[V]].
		String puntFaXifrar = codiCie + "-" + apunt + "-" + signatura;
		//System.out.println("[Pas4InserirVisitaPuntC] PuntFaXifrar val " + puntFaXifrar );
		String puntFXifrat = gestorCripto.xifra ( puntFaXifrar, this.gestorCripto.getCertificatGestor() );

		//Punt G. Signar Id_usuarip i X+1 amb la clau privada Sg de G, Sg[X+1, Id_usuarip].
		String puntGxSignar = nhc_pacient + "-" + numSerieX;
		String puntGxSignat = gestorCripto.signa ( puntGxSignar );

		//Punt H. Guardar a la BD Pg[V, Sm[V]], X+1, T, Sg[V,Sm[V], T, X+1] i Sg[X+1, Id_usuarip].
		String sql = "INSERT INTO `dtdiagnostics` ( `nhcpacient` , `numseriex` , `instanttemps` , `pgvsmv` , `Sgvsmvtx` , `sgxidusuarip` ) VALUES ( '" + nhc_pacient + "', '"+numSerieX+"', '"+tActual+"', '"+puntFXifrat+"', '"+puntExSignat+"', '"+puntGxSignat+"' )";
		//System.out.println ( sql );
		gestorBBDD.executaCanvisSQL ( sql );
		

	}

	/**
	*	Aquest mètode mira si un determinat pacient està assignat a un determinat metge.
	*
	* @return <code>True</code> si és cert.
	* @return <code>False</code> si és fals.
	**/
	public boolean verPacMetge (String nhc_pacient, String nhc_metge ) throws java.rmi.RemoteException {
		String sql = "SELECT COUNT(*) AS TOTAL FROM `dtpacients` WHERE nhc='"+nhc_pacient+"' AND metgeassignat='" + nhc_metge + "'";
		boolean vRetorn = false;
		try {
				java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;
				rs.next();
				int a = new java.lang.Integer( rs.getString("TOTAL") ).intValue();				
				if (a > 0) {
					vRetorn = true;
				}

		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}
		
		return vRetorn;
	}

	/**
	*	Aquest mètode mira si un determinat usuari és un metge o no.
	*
	* @return <code>True</code> si és cert.
	* @return <code>False</code> si és fals.
	**/
	public boolean verEsMetge (String nhc_metge ) {

		boolean vRetorn;

		X509Certificate c[] = gestorCripto.getCertificatUsuari ( nhc_metge );
		Name name = (Name)c[0].getSubjectDN();	
		String organizationalUnit = name.getRDN(ObjectID.organizationalUnit);

		if (organizationalUnit.compareTo ("Metges") == 0) {
			vRetorn = true;
		} else {
			vRetorn = false;
			System.out.println("[Error] L'usuari amb id " + nhc_metge + " NO disposa del certificat d'un metge");
			System.exit(-1);
		}
		
		return vRetorn;
	}

	/**
	*	Aquest mètode retorna l'instant de temps actual T.
	*
	*	@return Retorna un string amb l'instant de temps actual T.
	*
	**/
	String getTempsActual() {

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		java.util.Date data = new java.util.Date();
		return formatter.format(data);
		//java.util.Date b = new java.util.Date();		
		//java.sql.Timestamp a = new java.sql.Timestamp( b.getTime() );
		//return a.toString();
	}

	/**
	*	Aquest mètode retorna el número de sèrie X de la última visita de l'historial H del pacient Id_usuarip;
	*
	**/
	String getNumSerieX ( String nhc_pacient, String codiCie ) {

		String sql = "SELECT MAX(numseriex) AS maxnumseriex FROM `dtdiagnostics` WHERE nhcpacient='" + nhc_pacient + "'";
			
		try {
				java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;
				rs.next();
				int a = new java.lang.Integer( rs.getString("maxnumseriex") ).intValue();				
				return java.lang.Integer.toString( a + 1);

		} catch (java.lang.NumberFormatException e ) {
		
			return "0";

		} catch (java.lang.NullPointerException e ) {
		
			return "0";

		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}
		return "0";	
	
	}
	

	/**
	*	El procedure3 és l'encarregat de retornar la història clínica d'un
	*	determinat pacient a partir del id_usuari i la seva clau pública.<br /><br />
	*
	*	<ul>
	*		<li>Pas 1. Buscar l'historial H corresponent a id_usuari</li>
	*		<li>Pas 2. Desxifrar la part de H que està xifrada utilitzant la clau privada Sg de G.</li>
	*		<li>Pas 3. Xifrar H amb la clau pública Pu, Pu[H].</li>
	*		<li>Pas 4. Retornar Pu[H].</li>
	*	</ul>
	*
	*	<br /><br />Farem servir l'esquema següent:<br /><br />
	*
	*	<i>&lt;?xml version="1.0" encoding="UTF-8"?&gt;<br />
	*	&lt;Pfchistorials&gt;<br />
	*		&lt;HistoriaClinica&gt;<br />
	*		&lt;Pacient&gt;CodiPacient&lt;/Pacient&gt;<br />
	*			&lt;Diagnostic&gt;<br />
	*				&lt;CIE&gt;CodiCIE&lt;/CIE&gt;<br />
	*				&lt;DataDiag&gt;Data&lt;/DataDiag&gt;<br />
	*				&lt;Apunts&gt;<br />
	*					&lt;Apunt&gt;<br />
	*						&lt;TextApunt&gt;TextApunt&lt;/TextApunt&gt;<br />
	*						&lt;DataApunt&gt;DataApunt&lt;/DataApunt&gt;<br />
	*					&lt;/Apunt&gt;<br />
	*				&lt;/Apunts&gt;<br />
	*			&lt;/Diagnostic&gt;<br />
	*		&lt;/HistoriaClinica&gt;<br />
	*	&lt;/Pfchistorials&gt;<br /></i>
	*
	* @param id_usuari Amb l'identificador de l'usuari de l'historial clínic a retornar.
        * @param id_usuari_destinatari Amb l'identificador de l'usuari al qui se li entrega l'historial clínic.
	* @return Un <code>Document</code> amb l'historial clínic.
	*
	**/
	public Document procedure3 ( String id_usuari , String id_usuari_destinatari ) throws java.rmi.RemoteException {

		X509Certificate[] certificatU = gestorCripto.getCertificatUsuari ( id_usuari );

		/* Pas 1 */
		
		Element nodePfc  = new Element("Pfchistorials");
		Element nodeHistoClinic = new Element("HistoriaClinica");
		nodePfc.addContent ( nodeHistoClinic );

		Element nodeIdUsuari = new Element ( "Pacient");
		nodeIdUsuari.addContent ( gestorCripto.xifra ( id_usuari,
							       gestorCripto.getCertificatUsuari ( id_usuari_destinatari) ));

		nodePfc.addContent ( nodeIdUsuari );	
			
		
	String sql = new String ("SELECT * FROM `dtdiagnostics` WHERE nhcpacient = '" + id_usuari + "' ORDER BY numseriex");				

		try {

			java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;
			
			while ( rs.next() ) {

				//System.out.println("Desxifrem pgvsmv i obtenim " + gestorCripto.desxifra ( rs.getString("pgvsmv") ));
			
				Element nodeDiagnostic = new Element ("Diagnostic");
				
				Element nDiagX = new Element("X");
				Element nDiagT = new Element("T");	

		nDiagX.addContent ( gestorCripto.xifra ( rs.getString ("numseriex"),gestorCripto.getCertificatUsuari ( id_usuari_destinatari) ));	

				nDiagT.addContent ( gestorCripto.xifra ( rs.getString ("instanttemps"),
						    gestorCripto.getCertificatUsuari ( id_usuari_destinatari) ));
	
				Element nDiagPgvsmv = new Element ("Pgvsmv");
				Element nDiagSgvsmvtx = new Element ("Sgvsmvtx");
				Element nDiagSgxidusuarip = new Element ("Sgxidusuarip");

				nDiagPgvsmv.addContent ( gestorCripto.xifra ( gestorCripto.desxifra( rs.getString("pgvsmv")) ,
							gestorCripto.getCertificatUsuari (id_usuari_destinatari ) ));

				nDiagSgvsmvtx.addContent ( gestorCripto.xifra ( rs.getString("Sgvsmvtx") ,
							gestorCripto.getCertificatUsuari (id_usuari_destinatari )));

				nDiagSgxidusuarip.addContent ( gestorCripto.xifra ( rs.getString("sgxidusuarip"),
							gestorCripto.getCertificatUsuari ( id_usuari_destinatari )));

				nodeDiagnostic.addContent ( nDiagPgvsmv );
				nodeDiagnostic.addContent ( nDiagSgvsmvtx );
				nodeDiagnostic.addContent ( nDiagSgxidusuarip );
				
				nodeDiagnostic.addContent ( nDiagX );
				nodeDiagnostic.addContent ( nDiagT );

				nodeHistoClinic.addContent ( nodeDiagnostic );				
			}
			
			
		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}


		/* Escrivim la historia clinica en un fitxer */
		
		FileOutputStream fileOut = null;
	/*
	try{

	    fileOut = new FileOutputStream("histoclinic.xml");
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}*/
	
//	try{
	    //Nou
	    Document finalDoc = new Document(nodePfc);
	   // XMLOutputter xmlout = new XMLOutputter();

	    //Nou
	    //xmlout.output(finalDoc, fileOut);
	   // fileOut.close();

	    return finalDoc;
/*
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}
		
		return null;
		*/

	}

	/**
	*	El procedure5 és l'encarregat de retornar el llistat de pacients
	*	d'un determinat metge a partir del seu identificador.<br /><br />
	*
	*	El resultat s'ha de signar primer i després xifrar.<br /><br />	
	*
	* 	Farem servir l'esquema següent:<br /><br />
	*
	*	&lt;?xml version="1.0" encoding="UTF-8"?&gt;<br />
	*	&lt;Pfchistorials&gt;<br />
	*	&lt;Llista_pacients&gt;<br />
	*	&lt;Pacient&gt;<br />
	*		&lt;NomPacient&gt;Nom del pacient XIFRAT&lt;/NomPacient&gt;<br />
	*		&lt;Signatura&gt;Signatura amb el nom del pacient&lt;/Signatura&gt;<br />
	*	&lt;/Pacient&gt;<br />
	*	&lt;/Llista_pacients&gt;<br />
	*	&lt;/Pfchistorials&gt;<br />
	*
	* @param id_usuari Identificador del metge al qual se li ha d'enviar el llistat dels seus pacients.
	* @return Un document en XML amb el seu llistat de pacients.
	*
	**/
	public Document procedure5 ( String id_usuari ) throws java.rmi.RemoteException {

		X509Certificate[] certificatU = gestorCripto.getCertificatUsuari ( id_usuari );

		/* Pas 1 */
		String sql = new String ("SELECT * FROM `dtpacients` WHERE metgeassignat='" + id_usuari + "' ORDER BY nom, cognom1, cognom2");
		
		Element nodePfc  = new Element("Pfchistorials");
		Element nodeLlistaPac = new Element("Llista_pacients");
		nodePfc.addContent ( nodeLlistaPac );

		try {

			java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;
			
			while ( rs.next() ) {
					
			Element nodePac = new Element("Pacient");
			Element nodeNomPac = new Element("NomPacient");
			String nomPacient = new String ( rs.getString("cognom1") + " " + rs.getString("cognom2") + ", " + rs.getString("nom") +
					                 "  (" + rs.getString("nhc") + ")");
			String signNomPacient = gestorCripto.signa ( nomPacient );
			String xifraNomPacient = gestorCripto.xifra ( nomPacient, certificatU );
			//nodePac.addContent ( nomPacient );
			nodeNomPac.addContent ( xifraNomPacient );

			Element nodeSignatura = new Element("Signatura");
			nodeSignatura.addContent ( signNomPacient );
			nodePac.addContent ( nodeNomPac );
			nodePac.addContent ( nodeSignatura );
			nodeLlistaPac.addContent ( nodePac );
			}
			
			
		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}		

		return new Document(nodePfc);					


	/*	

	FileOutputStream fileOut = null;

	try{

	    fileOut = new FileOutputStream("llistatPacients.xml");
	    Document finalDoc = new Document(nodePfc);
	    XMLOutputter xmlout = new XMLOutputter();	    
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
	*	El mètode getDadesAdministratives s'encarrega de retornar les dades
	*	administratives d'un determinat pacient a partir del seu identificador.<br /><br />
	*
	*	El resultat simplement es xifrarà<br /><br />	
	*
	* 	Farem servir l'esquema següent:<br /><br />
	*
	*	&lt;?xml version="1.0" encoding="UTF-8"?&gt;<br />
	*	&lt;Pfchistorials&gt;<br />
	*	&lt;Dades_Admin_Pacient&gt;<br />
	*		&lt;nhc&gt;Nhc pacient&lt;/nhc&gt;<br />
	*	&lt;/Dades_Admin_Pacient&gt;<br />
	*	&lt;/Pfchistorials&gt;<br />
	*
	* @param id_usuari_vull Identificador de l'usuari del qual volem les dades administratives
	* @param id_usuari_peticio Identificador de l'usuari que demana les dades administratives
	* @return Un document en XML amb les dades administratives
	*
	**/
	public Document getDadesAdminPacient ( String id_usuari_vull , String id_usuari_peticio ) throws java.rmi.RemoteException {

		X509Certificate[] certificatU = gestorCripto.getCertificatUsuari ( id_usuari_peticio );

		String sql = new String ("SELECT * FROM `dtpacients` WHERE nhc='" + id_usuari_vull + "' ORDER BY nom, cognom1, cognom2");
		
		Element nodePfc  = new Element("Pfchistorials");
		Element nodeDadesAdminPac = new Element("Dades_Admin_Pacient");
		nodePfc.addContent ( nodeDadesAdminPac );

		try {

			java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;
			rs.next();					

			Element nodeNhcPac = new Element("nhc");
			nodeNhcPac.addContent ( gestorCripto.xifra ( rs.getString("nhc") , certificatU ) );
			nodeDadesAdminPac.addContent ( nodeNhcPac );			

			Element nodeDniPac = new Element("dni");
			if (((rs.getString("dni") != null)) && (rs.getString("dni").compareTo("") != 0)) {
				nodeDniPac.addContent ( gestorCripto.xifra ( rs.getString("dni") , certificatU ) );
			} else {
				nodeDniPac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeDniPac );

			Element nodeTisPac = new Element("tis");
			if (((rs.getString("tis") != null)) && (rs.getString("tis").compareTo("") != 0)) {
					nodeTisPac.addContent ( gestorCripto.xifra ( rs.getString("tis") , certificatU ) );
			} else {
					nodeTisPac.addContent ("");

			}
			nodeDadesAdminPac.addContent ( nodeTisPac );

			
			Element nodeNssPac = new Element("nss");
			if (((rs.getString("nss") != null)) && (rs.getString("nss").compareTo("") != 0)) {
				nodeNssPac.addContent ( gestorCripto.xifra ( rs.getString("nss") , certificatU ));
			} else {
				nodeNssPac.addContent ("");
			}
			nodeDadesAdminPac.addContent ( nodeNssPac );
		

			Element nodeNomPac = new Element("nom");
			if (((rs.getString("nom") != null)) && (rs.getString("nom").compareTo("") != 0)) {

				nodeNomPac.addContent ( gestorCripto.xifra ( rs.getString("nom") , certificatU ));

			} else {
				nodeNomPac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeNomPac );
		

			Element nodeCognom1Pac = new Element("cognom1");

			if (((rs.getString("cognom1") != null)) && (rs.getString("cognom1").compareTo("") != 0)) {

				nodeCognom1Pac.addContent ( gestorCripto.xifra ( rs.getString("cognom1") , certificatU ));
			} else {

				nodeCognom1Pac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeCognom1Pac );


			Element nodeCognom2Pac = new Element("cognom2");
			if (((rs.getString("cognom2") != null)) && (rs.getString("cognom2").compareTo("") != 0)) {

				nodeCognom2Pac.addContent ( gestorCripto.xifra ( rs.getString("cognom2"), certificatU ));

			} else {
				nodeCognom2Pac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeCognom2Pac );

					

			Element nodeCorreuePac = new Element("correue");
			if (((rs.getString("correue") != null)) && (rs.getString("correue").compareTo("") != 0)) {
				nodeCorreuePac.addContent ( gestorCripto.xifra ( rs.getString("correue") , certificatU ));
			} else {
				nodeCorreuePac.addContent ("");
			}
			nodeDadesAdminPac.addContent ( nodeCorreuePac );

			
			Element nodeTelfPac = new Element("telefon");
			if (((rs.getString("telf") != null)) && (rs.getString("telf").compareTo("") != 0)) {
				nodeTelfPac.addContent ( gestorCripto.xifra ( rs.getString("telf") , certificatU ));
			} else {
				nodeTelfPac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeTelfPac );

			
			Element nodeDireccioPac = new Element("direccio");
			if (((rs.getString("direccio") != null)) && (rs.getString("direccio").compareTo("") != 0)) {
				nodeDireccioPac.addContent ( gestorCripto.xifra ( rs.getString("direccio") , certificatU ));
			} else {
				nodeDireccioPac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeDireccioPac );

			
			Element nodeCodipostalPac = new Element("codipostal");
			if (((rs.getString("codipostal") != null)) && (rs.getString("codipostal").compareTo("") != 0)) {
			nodeCodipostalPac.addContent ( gestorCripto.xifra ( rs.getString("codipostal") , certificatU ));
			} else {
				nodeCodipostalPac.addContent ("");
			}
			nodeDadesAdminPac.addContent ( nodeCodipostalPac );

			
			Element nodePoblacioPac = new Element("poblacio");
			if (((rs.getString("poblacio") != null)) && (rs.getString("poblacio").compareTo("") != 0)) {
				nodePoblacioPac.addContent ( gestorCripto.xifra ( rs.getString("poblacio") , certificatU ));
			} else {
				nodePoblacioPac.addContent ("");
			}
			nodeDadesAdminPac.addContent ( nodePoblacioPac );

			
			Element nodeDatanaixementPac = new Element("datanaixement");
			if (((rs.getString("datanaixement") != null)) && (rs.getString("datanaixement").compareTo("") != 0)) {
				nodeDatanaixementPac.addContent ( gestorCripto.xifra ( rs.getString("datanaixement") , certificatU ));
			} else {
				nodeDatanaixementPac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeDatanaixementPac );

			Element nodeSexePac = new Element("sexe");
			if (((rs.getString("sexe") != null)) && (rs.getString("sexe").compareTo("") != 0)) {
				nodeSexePac.addContent ( gestorCripto.xifra ( rs.getString("sexe"), certificatU ));			
			} else {
				nodeSexePac.addContent("");
			}
			nodeDadesAdminPac.addContent ( nodeSexePac );		
			
			
		} catch (java.sql.SQLException e) {
			System.out.println("Error en getDadesAdminPacient Gestor.java: " + e );
			
		}		
		

	FileOutputStream fileOut = null;

	try{

	    fileOut = new FileOutputStream("dadesPacient.xml");
	    Document finalDoc = new Document(nodePfc);
	    XMLOutputter xmlout = new XMLOutputter();	    
	    xmlout.output(finalDoc, fileOut);
	    fileOut.close();
	    return finalDoc;			

	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(0);
	}
		//return null;
	
		return new Document(nodePfc);					

	

	

	}

	public void desaDataBaseValorsAleatoris ( String ni, int ng, String nhc ) {
		
		gestorBBDD.executaCanvisSQL ( "INSERT INTO `dtsessionsges` ( `nhc` , `ni` , `ng` ) VALUES ('" + nhc + "', '" + ni + "', '" + ng + "');" );
	}		
	
     /**
      * El mètode <code>recuperaDataBaseNg</code> recupera de la base de dades
      * el valor ng emprat per l'autentificació.
      *
      * @param nhc <code>String</code> nhc
      *
      * @return <code>int</code> amb el valor ng associat a aquest nhc
      *
      *
      */
	public int recuperaDataBaseNg ( String nhc ) {		

		try {

			java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( "SELECT * FROM `dtsessionsges` WHERE nhc='" + nhc + "'" ) ;
			rs.next ();		
			return rs.getInt ( "ng");
		} catch (java.sql.SQLException e) {
			System.out.println(e);
			return -1;
		}

	}

     /**
     * Retorna un document en format XML amb un llistat de malalties amb les descripcions i codificacions segons el CIE-9.<br /><br />
     *
     * 		Emprarem un esquema similar al següent:<br /><br />
     *
     *		&lt;?xml version="1.0" encoding="UTF-8"?&gt;<br />
     *		&lt;Pfchistorials&gt;<br />
     *		&lt;LlistatCIE9&gt;<br />
     *		&lt;CodiCIE&gt;<br />
     *		&lt;Codi&gt;Codi&lt;/Codi&gt;<br />
     *		&lt;Descripcio&gt;Descripcio&lt;/Descripcio&gt;<br />
     *		&lt;/CodiCIE&gt;<br />
     *		&lt;/LlistatCIE9&gt;<br />
     *		&lt;/Pfchistorials&gt;<br />
     *
     * @return Un <code>org.jdom.Document</code> que conté el document en format XML
     */
 
	public org.jdom.Document retornaLlistatCIE ( ) throws java.rmi.RemoteException {
	
		Element nodePfc  = new Element("Pfchistorials");
		Element nodeLlistatCIE = new Element("LlistatCIE9");

		String sql = "SELECT * FROM `dtcodCIE9` ORDER BY descrip";
			
		try {
			java.sql.ResultSet rs = gestorBBDD.executaSelectSQL( sql ) ;

			while (rs.next()) {

				Element nodeCodiCIE = new Element("CodiCIE");
				Element nodeCodi = new Element("Codi");
				nodeCodi.addContent( rs.getString("codicie") );
				Element nodeDescrip = new Element("Descripcio");
				nodeDescrip.addContent ( rs.getString("descrip"));				

				nodeCodiCIE.addContent ( nodeCodi );
				nodeCodiCIE.addContent ( nodeDescrip );

				nodeLlistatCIE.addContent ( nodeCodiCIE );
				
			}	


		} catch (java.sql.SQLException e) {
			System.out.println(e);
			
		}		
		
		nodePfc.addContent ( nodeLlistatCIE );

		/*
		FileOutputStream fileOut = null;

		try{

		    fileOut = new FileOutputStream("llistatCIE.xml");
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

		*/
		return new org.jdom.Document ( nodePfc );

     } 
	

     /**
     * El mètode <code>suprimeixDataBaseValorsAleatoris</code> suprimeix de la base de dades
     * els números aleatoris generats per l'autentificació amb els pacients.
     *
     * @param nhc Identificador d'usuari
     * 
     */
	public void suprimeixDataBaseValorsAleatoris ( String nhc ) {
		
		gestorBBDD.executaCanvisSQL( "DELETE FROM `dtsessionsges` WHERE nhc='" + nhc + "'" ) ;
		
	}


	public static void main(String[] args) throws java.lang.ClassNotFoundException {

	   try   {
		     Security.insertProviderAt(new IAIK(), 2);
		     Gestor mir = new Gestor("/home/gerard/pfc-historials/pki/Certificats/Gestor.p12" , "uoc0506");
		     //Gestor mir = new Gestor();
    		     String a = new String ( "//" + java.net.InetAddress.getLocalHost().getHostAddress() +  ":" + args[0] + "/GestorRMI");	
		     java.rmi.Naming.rebind( a , mir);

	   } catch ( java.rmi.UnexpectedException eio ) {

			System.out.println("Error en RMI: " + eio );
			eio.printStackTrace();
			System.exit ( -1 );
	/*
	   } catch ( java.lang.ClassNotFoundException ei ) {

			System.out.println("Error en RMI : " + ei ) ;
			ei.printStackTrace();
			System.exit ( -1 );
	*/
	   } catch (Exception e)    {  

			System.out.println("Error en main rmi" + e );  
			e.printStackTrace();
			System.exit ( -1 );
	   }

	 }
	

}
