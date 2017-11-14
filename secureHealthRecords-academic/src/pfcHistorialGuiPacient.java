/*
 * pfcHistorialGuiPacient.java
 *
 * Created on 19 / novembre / 2007, 17:59
 */

/**
 *
 * @author  Gerard Farràs i Ballabriga <gfarrasb@uoc.edu>
 */
public class pfcHistorialGuiPacient extends javax.swing.JFrame {
    
    private final static long serialVersionUID = 42L;
    private String nhcPacient;
    private String contrasenyaPacient;    
    private String pathP12;
    private GestorInterRemot gestor;
    
    /** Creates new form pfcHistorialGuiPacient */
    public pfcHistorialGuiPacient() {        
         
        arrelArbreCIE = new javax.swing.tree.DefaultMutableTreeNode("CIE");
        this.llistatArbreCIE = new javax.swing.tree.DefaultTreeModel( arrelArbreCIE);
        
        initComponents();     
        this.centraFormulari();
       
       
    }
    
    void centraFormulari () {
        
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;                
        setLocation(screenWidth / 4, screenHeight / 4);
        
    }
    
    private String retFieldXifAdmin ( String camp , Pacient pac ) {
        
        if (camp.compareTo("") == 0) {
            return "";
        } else {
            return pac.gestorCripto.desxifra ( camp );
        }
    }
    
    public void consultaDadesAdministrativesPac () {
        
        try {
        
        org.jdom.Document dadesAdminPac = gestor.getDadesAdminPacient ( this.nhcPacient, this.nhcPacient );
        org.jdom.Element pfchistorials = dadesAdminPac.getRootElement();
        org.jdom.Element nodeDadesAdminPac = pfchistorials.getChild("Dades_Admin_Pacient");
        
        Pacient objPac = new Pacient ( this.nhcPacient, this.contrasenyaPacient, this.pathP12 );   
        
        String nhcPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("nhc").getText() , objPac );
        String dniPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("dni").getText(), objPac );
        String tisPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("tis").getText(), objPac );
        String nssPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("nss").getText(), objPac );        
        String nomPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("nom").getText(), objPac );
        String cognom1Pac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("cognom1").getText(), objPac );
        String cognom2Pac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("cognom2").getText(), objPac );
        String correuPac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("correue").getText(), objPac );
        String telefonPac = this.retFieldXifAdmin ( nodeDadesAdminPac.getChild("telefon").getText(), objPac );
        String direccioPac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("direccio").getText(), objPac );
        String codipostalPac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("codipostal").getText(), objPac );
        String poblacioPac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("poblacio").getText(), objPac );
        String datanaixementPac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("datanaixement").getText(), objPac );
        String sexePac = this.retFieldXifAdmin( nodeDadesAdminPac.getChild("sexe").getText(), objPac );        
        
        textAreaDades.append( nhcPac + "-" + dniPac + '\n' );
        textAreaDades.append(tisPac + '\n');
        textAreaDades.append(cognom1Pac + " " + cognom2Pac + " ," + nomPac + '\n');
        textAreaDades.append( direccioPac + " (" + codipostalPac + ")" + '\n');
        textAreaDades.append( poblacioPac );    
        
        } catch ( Exception o ) { 
                System.out.println("Error RMI" + o);
            }
        
    }
    
    public void consultaHistorial () {
        
        try {
		
		Pacient objPac = new Pacient ( this.nhcPacient, this.contrasenyaPacient, this.pathP12 );
		org.jdom.Document a = objPac.procedure1 () ;
		org.jdom.Document b = gestor.procedure2 ( a ); 			
		org.jdom.Document c = objPac.pas3 ( b , "Consulta" );			
		boolean autentificacio = gestor.pas4 ( c );

		if (autentificacio) {				
				
			org.jdom.Document histoClinic = gestor.procedure3 ( nhcPacient , nhcPacient );
                        
                        java.util.Vector vectorHClinic = objPac.procedure4 ( histoClinic );
                        mostraHistorialClinic ( vectorHClinic );                                            

			}

		 }   catch (Exception e)     {

			       e.printStackTrace();
		     }

        
    }
    
     public void mostraHistorialClinic ( java.util.Vector historiaClinica ) {
        
        textAreaHC.setText(""); 
        
        //Recorrem vector per anar creant nodes en l'arbreCIE        
        //javax.swing.tree.DefaultMutableTreeNode fillNode = new javax.swing.tree.DefaultMutableTreeNode("802");
        //llistatArbreCIE.insertNodeInto( fillNode , arrelArbreCIE, arrelArbreCIE.getChildCount());       '
        
        for (int i=0;i<historiaClinica.size(); i++) {
            
            textAreaHC.append ( (String) historiaClinica.get(i) + '\n');
            
            String codiCIE = obtenirCIEfromApunt ( (String) historiaClinica.get(i) );
            
            javax.swing.tree.DefaultMutableTreeNode CIENode = new javax.swing.tree.DefaultMutableTreeNode( codiCIE );
            llistatArbreCIE.insertNodeInto ( CIENode , arrelArbreCIE, arrelArbreCIE.getChildCount());
            
        }
        
        
        
    }
     
    private String obtenirCIEfromApunt ( String apunt ) {        
        
        String apuntAux = apunt.substring( apunt.indexOf ("-") + 1 , apunt.length());        
        return ( new String (apuntAux.substring( 0 , apuntAux.indexOf("-")) ));
        
    } 
    
    public void estableixDades ( String usuari , String contrasenya, String pathP12, GestorInterRemot gestor ) {
        
        this.nhcPacient = usuari;
        this.contrasenyaPacient = contrasenya;
        this.pathP12 = pathP12;
        this.gestor = gestor;
        
         this.consultaDadesAdministrativesPac();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaHC = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        arbreCIE = new javax.swing.JTree();
        botoMostraHC = new javax.swing.JButton();
        botoSortir = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaDades = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("pfcHistorials - Pacients");
        setAlwaysOnTop(true);
        textAreaHC.setColumns(20);
        textAreaHC.setRows(5);
        jScrollPane1.setViewportView(textAreaHC);

        //arbreCIE.setModel((javax.swing.tree.DefaultTreeModel)this.llistatArbreCIE);
	arbreCIE.setModel(this.llistatArbreCIE);
        jScrollPane2.setViewportView(arbreCIE);

        botoMostraHC.setText("Mostra HC");
        botoMostraHC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoMostraHCActionPerformed(evt);
            }
        });

        botoSortir.setText("Sortir");
        botoSortir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoSortirActionPerformed(evt);
            }
        });

        textAreaDades.setColumns(20);
        textAreaDades.setEditable(false);
        textAreaDades.setRows(5);
        jScrollPane3.setViewportView(textAreaDades);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addComponent(botoSortir))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(botoMostraHC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botoSortir)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(botoMostraHC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botoMostraHCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoMostraHCActionPerformed
// TODO add your handling code here:
        
        consultaHistorial();
        
       
    }//GEN-LAST:event_botoMostraHCActionPerformed

    private void botoSortirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoSortirActionPerformed
// TODO add your handling code here:
        System.exit(-1);
    }//GEN-LAST:event_botoSortirActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pfcHistorialGuiPacient().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbreCIE;
    private javax.swing.JButton botoMostraHC;
    private javax.swing.JButton botoSortir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea textAreaDades;
    private javax.swing.JTextArea textAreaHC;
    // End of variables declaration//GEN-END:variables
    
    private javax.swing.tree.DefaultTreeModel llistatArbreCIE;   
    private javax.swing.tree.DefaultMutableTreeNode arrelArbreCIE;
}
