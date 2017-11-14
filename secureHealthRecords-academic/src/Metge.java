import java.io.*;
import org.jdom.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * Classe <code>Metge</code>
 *
 * Aquesta classe representa un metge i hereda les propietats i mètodes de la classe Usuari
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs Ballabriga</a>
 * @version 1.0
 */

public class Metge extends Usuari {

	Metge ( String nhc, String contrasenya, String pathp12 ) {

	
	try	{

	    this.nhc = nhc;
	    this.gestorCripto = new gestorCripto ( nhc , contrasenya, pathp12 );		    

	} catch(Exception e){

	    		e.printStackTrace();
    		        System.exit(0);
	}

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
      public String pas3InserirVisita ( org.jdom.Document docProcedure2 ,
				        String nhc_pacient,
				        String codiCie,
				        String apunt ) {

	Element nodeRoot = docProcedure2.getRootElement();
	Element nodeProcedure2 = nodeRoot.getChild("Procedure2");
	
	String messageRec = gestorCripto.desxifra ( nodeProcedure2.getText() );
	if (this.debug) System.out.println("[Metge][Pas3InserirVisita] El missate desxifrat és " + messageRec);

	String strbNi = messageRec.substring ( 0, messageRec.indexOf("-") );
	int bNi = java.lang.Integer.valueOf(strbNi).intValue();
	String cadRestant = messageRec.substring ( messageRec.indexOf("-") + 1 , messageRec.length() );
	String Ng = cadRestant.substring ( 0, cadRestant.indexOf("-") );
	String Id_UsuariG = cadRestant.substring ( cadRestant.indexOf("-") + 1 , cadRestant.length() );

	if (this.debug) {
		System.out.println("[Metge][Pas3InserirVisita] Ni val " + bNi );
		System.out.println("[Metge][Pas3InserirVisita] Ng val " + Ng );
		System.out.println("[Metge][Pas3InserirVisita] Id_UsuariG val " + Id_UsuariG );
	}

	/*
		Si aNi != bNi --> ERROR!
	*/
	
	if ( aNi == bNi ) {
		if (this.debug) System.out.println("[Metge][Pas3InserirVisita] Comprovació correcta entre números aleatòris!"); 
	} else {
		if (this.debug) System.out.println("[Metge][Pas3InserirVisita] Comprovació INCORRECTA entre números aleatòris!"); 
		System.exit(0);
	}

	/*
	
	*/
	String signaturaApunt = gestorCripto.signa ( nhc_pacient + "-" + codiCie + "-" + apunt );
	String pgnginserirvisita = Ng + "-" + this.nhc + "-" + nhc_pacient + "-" + codiCie + "-" + apunt + "-" + signaturaApunt;
	//if (this.debug) System.out.println("[Metge][Pas3InserirVisita] Retornem de forma xifrada al Gestor: " + pgnginserirvisita); 

	return gestorCripto.xifra ( pgnginserirvisita, gestorCripto.getCertificatGestor() );
	
     }

    /**
     * Aquest mètode s'ha d'encarregar de signar i xifrar el nou apunt metge
     *
     * @param ccie El codi CIE-9 de la malaltia
     * @param apunt Amb l'apunt de la malaltia
     */	
	
	String signaHisto (String ccie , String apunt ) {

		String apuntSignat = gestorCripto.signa ( ccie + "-" + apunt );

		if (this.debug)System.out.println("[Metge][signaHisto] L'apunt a signar és " + ccie + " i " + apunt + " --> " + apuntSignat );

		if (gestorCripto.verificaSignatura ( apuntSignat, ccie + "-" + apunt, gestorCripto.getFingerprintSHA() )) {

			if (this.debug) System.out.println("[Metge][SignaHisto] Signatura valida");
		} else {
			if (this.debug) System.out.println("[Metge][SignaHisto] Signatura INvalida");
		}

		return apuntSignat;
	}

     /**
      * El Metge utilitza el Procedure 6 per obtenir la llista dels seus pacients
      * i verificar que ha estat generada pel Gestor G.
      *
      * Pas 1. Desxifrar Pu[Sg[]] amb la clau privada Su de U, Su[Pu[Sg[{Id_usuari1, ...., Id_usuarin}]]] i
      * 	obtenir Sg[{Id_usuari1,..., Id_usuarin}];
      *
      * Pas 2. Verificar la signatura digital Sg[{Id_usuari1, ...., Id_usuarin}] amb la clau pública Pg de G;
      *
      * Pas 3. Si la verificació anterior és correcta retornar {Id_usuari1, ...., Id_usuarin}]
      *
      */

	java.util.Vector procedure6 ( Document llistat ) { 

		Element pfchistorials = llistat.getRootElement();
		Element llistatPacients = pfchistorials.getChild("Llista_pacients");
		Element Pacient;
		java.util.Vector<String> llistatNoms = new java.util.Vector<String>();

		java.util.List llistaLlistat = llistatPacients.getChildren();
		
		for (int i=0;i<llistaLlistat.size();i++) {

			Pacient = (Element) llistaLlistat.get(i);
			Element NomPacient = Pacient.getChild("NomPacient");
			Element SignaturaNomPacient = Pacient.getChild("Signatura");
	
			String pacientXifrat = NomPacient.getText();
			String pacientDesxifrat = gestorCripto.desxifra ( pacientXifrat );
			//System.out.println( pacientDesxifrat );			

			if ( gestorCripto.verificaSignatura ( SignaturaNomPacient.getText(), pacientDesxifrat, gestorCripto.getFingerPrintSHAGestor() )) {

				if (this.debug) System.out.println("[Metge] Signatura correcta");
				llistatNoms.add ( pacientDesxifrat );
			} else {
				if (this.debug) System.out.println("[Metge] Signatura INCORRECTA");
			}

			
		}		
		return llistatNoms;

		
	}
	
}
