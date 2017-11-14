import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * La classe <code>gestorBBDD</code> s'encarrega de les gestions amb la base de dades MySQL
 *
 * @author <a href="mailto:gfarrasb@uoc.edu">Gerard Farràs i Ballabriga</a>
 * @version 1.0
 */

public class gestorBBDD {

	Connection connexio;
	private String host;
	private String bbdd;
	private String user;
	private String passwd;

    /**
     * Constructor de <code>gestorBBDD</code>.
     *
     * @exception ClassNotFountException Si no es troba la classe
     * @exception SQLException Si hi ha un error en la connexió al servidor de base de dades MySQL.
     */
	public gestorBBDD () {

	try {

      		Class.forName("com.mysql.jdbc.Driver");
		llegeixFitxerConfig();
		this.connexio = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.bbdd, this.user, this.passwd );		

	} catch (ClassNotFoundException e ) {
				System.out.println(e);
	

	} catch (java.sql.SQLException e ) {
				System.out.println(e);
	
	} catch (Exception e) {
			      System.out.println(e);
    		}

	}
	

    /**
     * Executem una consulta del tipus INSERT, UPDATE o DELETE
     *
     * @param sql Sentència SQL.
     * @exception SQLException Si ocorre un error de SQL
     */
	public int executaCanvisSQL ( String sql ) {

	try {

         	Statement stmt = this.connexio.createStatement();		
	        int rs = stmt.executeUpdate( sql );
		return rs;

	} catch (SQLException e) {

      while (e != null) {
        System.out.println( "Estat   : " + e.getSQLState());
        System.out.println( "Missatge: " + e.getMessage());
        System.out.println( "Error   : " + e.getErrorCode());
        e = e.getNextException();
	return 0;
      }

	} catch (Exception e) {
			      System.out.println(e);
				return 0;
    		}
	    
	return 0;
    }

    /**
     * Executem una consulta del tipus SELECT
     *
     * @param sql <code>Sentència SQL</code>
     * @exception SQLException si ocorre un error de SQL
     */
     public ResultSet executaSelectSQL ( String sql ) {

	try {
	      
	      Statement stmt = this.connexio.createStatement();			      
	      ResultSet rs = stmt.executeQuery( sql );

	      return rs;

	} catch (SQLException e) {

      while (e != null) {
        System.out.println( "Estat   : " + e.getSQLState());
        System.out.println( "Missatge: " + e.getMessage());
        System.out.println( "Error   : " + e.getErrorCode());
        e = e.getNextException();
	return null;
      }

	} catch (Exception e) {
			      System.out.println(e);
				return null;
    		}
	    
	return null;
    }

	
	/**
	* Aquest mètode llegeix la configuració per accedir al servidor de BBDD.
        *
        * @exception IOException si ocorre algun error accedint al fitxer cfgBBDD.txt
        */
	private void llegeixFitxerConfig() {

			java.io.DataInputStream dis;

		try { 

		           java.io.File f = new java.io.File("cfgBBDD.txt"); 
		           java.io.FileInputStream fis = new java.io.FileInputStream(f); 
		           
    			   java.io.BufferedReader d = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
			   String record;
			   int recCount = 0;
		           while ( (record=d.readLine()) != null ) { 			              

				      switch (recCount) {

						case 0:
							this.host = record;
							break;

						case 1:
							this.bbdd = record;
							break;
					
						case 2:
							this.user = record;
							break;
	
						case 3:
							this.passwd = record;
							break;	
		
					}

				      recCount++; 
			              
		           } 

			fis.close();

	        } catch (java.io.IOException e) { 

		           System.out.println("Error accedint al fitxer de configuració de la BBDD: " + e.getMessage()); 
		    
	        } 

	}

}
