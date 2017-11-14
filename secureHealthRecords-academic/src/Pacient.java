import java.io.*;
import org.jdom.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * Classe <code>Pacient</code>
 *
 * Aquesta classe representa un pacient i hereda les propietats i mètodes de la classe Usuari
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs Ballabriga</a>
 * @version 1.0
 */

public class Pacient extends Usuari {

	Pacient ( String nhc , String contrasenya, String pathp12 ) {

	
	try	{

	    this.nhc = nhc;
	    this.gestorCripto = new gestorCripto ( nhc , contrasenya, pathp12 );
	    //this.gestorBBDD = new gestorBBDD();		    

	} catch(Exception e){

	    		e.printStackTrace();
    		        System.exit(0);
	}

     }
    
	
}
