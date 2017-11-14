/*
 * pfcHistorialGuiMetge.java
 *
 * Created on 16 / novembre / 2007, 19:31
 */

/**
 *
 * @author  Gerard Farràs i Ballabriga <gfarrasb@uoc.edu>
 */
public class pfcHistorialGuiMetge extends javax.swing.JFrame {
    
    private String ncolMetge;
    private String contrasenyaMetge;    
    private String pathP12;
    private GestorInterRemot gestor;
    private Metge metge;  
    private final static long serialVersionUID = 42L;  
    
    
    /**
     *Constructor de la interfície gràfica per a professionals sanitaris.
     */
    public pfcHistorialGuiMetge() {        
       
        arrelArbreCIE = new javax.swing.tree.DefaultMutableTreeNode("CIE");
        this.llistatArbreCIE = new javax.swing.tree.DefaultTreeModel( arrelArbreCIE);              
        
        initComponents();   
        this.centraFormulari();           
        
        
    }
    
    /**
     *Aquest mètode centra el formulari.
     */
    void centraFormulari () {
        
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;        
        setLocation(screenWidth / 4, screenHeight / 4);  
        
    }
    
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        arbreCIE = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaHC = new javax.swing.JTextArea();
        botoSortir = new javax.swing.JToggleButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        textNHC = new javax.swing.JTextPane();
        botoAfegeixApunt = new javax.swing.JButton();
        botoLlistaPacients = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaDades = new javax.swing.JTextArea();
        botoMostraHC = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pfc Historials - Metge");
        setName("pfcHistorialGuiMetgeFrame");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        arbreCIE.setToolTipText("Arbre CIE del pacient");
        //arbreCIE.setModel((javax.swing.tree.DefaultTreeModel)this.llistatArbreCIE);
	arbreCIE.setModel(this.llistatArbreCIE);
        arbreCIE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                arbreCIEMousePressed(evt);
            }
        });
        arbreCIE.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                arbreCIEValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(arbreCIE);

        textAreaHC.setColumns(20);
        textAreaHC.setEditable(false);
        textAreaHC.setRows(5);
        jScrollPane2.setViewportView(textAreaHC);

        botoSortir.setText("Sortir");
        botoSortir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoSortirActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(textNHC);

        botoAfegeixApunt.setText("Afegeix apunt");
        botoAfegeixApunt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoAfegeixApuntActionPerformed(evt);
            }
        });

        botoLlistaPacients.setText("Llista pacients");
        botoLlistaPacients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoLlistaPacientsActionPerformed(evt);
            }
        });

        textAreaDades.setColumns(20);
        textAreaDades.setRows(5);
        jScrollPane3.setViewportView(textAreaDades);

        botoMostraHC.setText("Mostra HC:");
        botoMostraHC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoMostraHCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(botoLlistaPacients)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botoSortir))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(botoMostraHC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(botoAfegeixApunt)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(botoSortir)
                        .addComponent(botoLlistaPacients))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botoMostraHC)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(botoAfegeixApunt)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void arbreCIEMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_arbreCIEMousePressed
// TODO add your handling code here:

    }//GEN-LAST:event_arbreCIEMousePressed

    private void arbreCIEValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_arbreCIEValueChanged
// TODO add your handling code here:

    }//GEN-LAST:event_arbreCIEValueChanged

    /**
     *Aquest mètode s'emprarà per a afegir un nou apunt en la història clínica d'un determinat pacient.
     */
    private void botoAfegeixApuntActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoAfegeixApuntActionPerformed
// TODO add your handling code here:
        
        if (this.textNHC.getText().compareTo("") != 0) {
            
            pfcGuiAfegeixApunt a = new pfcGuiAfegeixApunt();
            a.estableixDades ( this.ncolMetge , contrasenyaMetge, this.pathP12, gestor, textNHC.getText(), this );       
            a.setVisible(true);        
            
        } else {
            
            javax.swing.JOptionPane.showMessageDialog(null, "Escriviu el número d'història clínica que volgueu consultar", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            textNHC.requestFocus();
            
        }
            
        
        
    }//GEN-LAST:event_botoAfegeixApuntActionPerformed

    /**
     *
     */
    private void netejaGui() {
        
          textAreaHC.setText("");                 
          this.arrelArbreCIE = new javax.swing.tree.DefaultMutableTreeNode("CIE");
          this.llistatArbreCIE = new javax.swing.tree.DefaultTreeModel( arrelArbreCIE);                         
    }
    
    private void botoMostraHCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoMostraHCActionPerformed

          if (this.textNHC.getText().compareTo("") != 0) {
              netejaGui();       
              consultaHistorial (this.textNHC.getText());              
            
          } else {
              
                javax.swing.JOptionPane.showMessageDialog(null, "Escriviu el número d'història clínica que volgueu consultar", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                textNHC.requestFocus();
          }
                        
    }//GEN-LAST:event_botoMostraHCActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
// TODO add your handling code here:
     
    }//GEN-LAST:event_formWindowOpened

    private void botoSortirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoSortirActionPerformed
// TODO add your handling code here:            
            System.exit(0);
    }//GEN-LAST:event_botoSortirActionPerformed

    private void botoLlistaPacientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoLlistaPacientsActionPerformed
// TODO add your handling code here:
        pfcGuiLlistaPacients pfcGuiLlistaPacientsGui = new pfcGuiLlistaPacients ();
        pfcGuiLlistaPacientsGui.estableixDades(this.ncolMetge, this.contrasenyaMetge , this.pathP12, this.gestor, this);        
        pfcGuiLlistaPacientsGui.setVisible ( true );
        
         
    }//GEN-LAST:event_botoLlistaPacientsActionPerformed
    
    public void estableixDades (String idMetge, String contrasenyaMetge, String pathP12, GestorInterRemot gestor ) {
        
        this.ncolMetge = idMetge;
        this.contrasenyaMetge = contrasenyaMetge;
        this.pathP12 = pathP12;
        this.metge = new Metge ( this.ncolMetge, this.contrasenyaMetge, pathP12 );
        this.gestor = gestor;
        
    }
    
    public void estableixPacient ( String nhcPacient ) {
        
        this.textNHC.setText ( nhcPacient );
        
    }
    
    private String obtenirCIEfromApunt ( String apunt ) {        
 
        String apuntAux = apunt.substring( apunt.indexOf ("-") + 1 , apunt.length());        
        return ( new String (apuntAux.substring( 0 , apuntAux.indexOf("-")) ));
        
    }
/*
	private String retFieldXifAdmin ( String camp , Pacient pac ) {
        
        if (camp.compareTo("") == 0) {
            return "";
        } else {
            return pac.gestorCripto.desxifra ( camp );
        }
    }
	
        public void consultaDadesAdministrativesPac ( String nhcPacient ) {
        
        try {
        
        org.jdom.Document dadesAdminPac = gestor.getDadesAdminPacient ( nhcPacient, ncolMetge );
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
        
    }*/
    
    public void mostraHistorialClinic ( java.util.Vector historiaClinica ) {
        
        netejaGui();
        
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
    
    public void consultaHistorial ( String nhcPacient ) {
        
        try {		
		
		org.jdom.Document a = metge.procedure1 () ;
		org.jdom.Document b = gestor.procedure2 ( a ); 			
		org.jdom.Document c = metge.pas3 ( b , "Consulta" );			
		boolean autentificacio = gestor.pas4 ( c );

		if (autentificacio) {	
                    
                    if (gestor.verPacMetge(nhcPacient , ncolMetge )) {
                        
                        org.jdom.Document histoClinic = gestor.procedure3 ( nhcPacient , this.ncolMetge );
			java.util.Vector vectorHClinic = metge.procedure4 ( histoClinic );
                        mostraHistorialClinic ( vectorHClinic );
                        
                        
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(null, "Aquest pacient NO us pertany", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }                                
				
			

		 }   catch (Exception e)     {

			       e.printStackTrace();
		     }
                     
      
    }
      
  
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbreCIE;
    private javax.swing.JButton botoAfegeixApunt;
    private javax.swing.JButton botoLlistaPacients;
    private javax.swing.JButton botoMostraHC;
    private javax.swing.JToggleButton botoSortir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea textAreaDades;
    private javax.swing.JTextArea textAreaHC;
    private javax.swing.JTextPane textNHC;
    // End of variables declaration//GEN-END:variables
    private javax.swing.tree.DefaultTreeModel llistatArbreCIE;   
    private javax.swing.tree.DefaultMutableTreeNode arrelArbreCIE;
}
