import iaik.security.provider.IAIK; 
import java.security.*; 
import org.jdom.Document;

/**
 * La classe <code>AutenticaUsuari</code> conté les instruccions per a que un usuari s'autentiqui en el sistema
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs i Ballabriga</a>
 * @version 1.0
 */

public class AutenticaUsuari {	

		boolean debug = false;
		String usuari = "";
		String contrasenya = "";
                String pathP12 = "";
		GestorInterRemot gestor;

   public void usage () {

		System.out.println("Aquesta classe autentica un usuari amb el sistema.\n");
		System.out.println("Utilització:");
		System.out.println("\t--usuari Amb l'identificador de l'usuari (sigui metge o pacient).");		
		System.out.println("\t--passwd Amb la contrasenya del certificat de l'usuari.");
		System.out.println("\t--rmi Amb el port RMI.");						
		System.out.println("\t--debug (opcional) Per a debugar el programari.");
		System.out.println("\nEx. java AutenticaUsuari --usuari=8006 --passwd=uoc0506.");				
		System.exit(-1);

   }

   public AutenticaUsuari( String argUsuari, String argContrasenya, String pathP12, GestorInterRemot gestor ) {

		this.usuari = argUsuari;	
		this.contrasenya = argContrasenya;
                this.pathP12 = pathP12;
                this.gestor = gestor;
	
   }
   
   public void mostraErrorMarxa( String error ) {
       
       javax.swing.JOptionPane.showMessageDialog(null, error , "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	System.exit(-1);
       
   }

   public boolean validaUsuari() {

		Security.insertProviderAt(new IAIK(), 2);

		boolean autentificacio = false;

		/*
			jcasserras és un usuari de tipus metge i demana el seu llistat de pacients.
		*/
		Metge jcasserras = new Metge( this.usuari, this.contrasenya, this.pathP12 );
		//jcasserras.setDebug( debug );

		/*.
			Usuari gestor
		*/		
		//Gestor gestor = new Gestor ();
		//gestor.setDebug ( debug );

		/*
			Implementem el protocol 1
		*/
		//String a = jcasserras.procedure1 () ;
                
                try {
                    
		Document a = jcasserras.procedure1 ();
		Document b = gestor.procedure2 ( a );				
		
			/*
				JCasserras ha de desxifrar el missatge amb la seva clau privada Su i obtenir Ng, Ni i Id_Usuarig
			*/
			Document c = jcasserras.pas3 ( b , "Autenticacio" );

			/*
				JCasserras envia al gestor Pg[Ng, Consulta, Id_usuari] a G;
			*/
			autentificacio = gestor.pas4 ( c );

			if (autentificacio) {
				
				return true;

			} else {	
				
			}

		 }   catch (java.rmi.ConnectException o) {

                                            this.mostraErrorMarxa("Error en la connexió RMI");

		 } catch (java.rmi.RemoteException o )  {
                     
                                        this.mostraErrorMarxa("Error en la connexió RMI");
                 }

		return autentificacio;
			
		
   }


	/*
   public void main(String[] args){		

		if (args.length == 0) {
			usage ();			
		}

		for (int i=0;i<args.length;i++) {

			if ( (args[i].substring(0,2)).compareTo("--") != 0) {
				usage();
				System.exit(-1);
			} else {
	
				if ( args[i].substring(2).startsWith("debug") ) {
						debug = true;
				}
	
				if ( args[i].substring(2).startsWith("usuari") ) {
						int pos = args[i].indexOf("=");
						if (pos <= 0) usage();
						usuari = args[i].substring( pos + 1 );
				}

				if ( args[i].substring(2).startsWith("passwd") ) {
						int pos = args[i].indexOf("=");
						if (pos <= 0) usage();
						contrasenya = args[i].substring( pos + 1);
				}

				if ( args[i].substring(2).startsWith("rmi") ) {
						int pos = args[i].indexOf("=");
						if (pos <= 0) usage();
						rmi = args[i].substring( pos + 1);
				}
			}	
			
		}

		if ((usuari.compareTo("") == 0) || (contrasenya.compareTo("") == 0) || (rmi.compareTo("") == 0) ) {
			usage();
		}

		if (debug) {
			System.out.println("[AutenticaUsuari] Usuari --> " + usuari );
			System.out.println("[AutenticaUsuari] Passwd --> " + contrasenya );
			System.out.println("[AutenticaUsuari] RMI --> " + rmi );
		}

		AutenticaUsuari autentica = new AutenticaUsuari ( usuari, contrasenya, rmi );
	        autentica.validaUsuari ();

		
     }
	*/

}
