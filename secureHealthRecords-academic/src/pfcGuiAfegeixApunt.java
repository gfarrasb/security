/*
 * pfcGuiAfegeixApunt.java
 *
 * Created on 16 / novembre / 2007, 19:40
 */

/**
 *
 * @author  Gerard Farràs i Ballabriga <gfarrasb@uoc.edu>
 */
public class pfcGuiAfegeixApunt extends javax.swing.JDialog {
    
    private String nhcPacient;
    private String ncolMetge;    
    private String contrasenyaMetge;
    private String pathP12;
    private GestorInterRemot gestor;
    private Metge metge;
    private pfcHistorialGuiMetge pfcHistorialGuiMetge;
    private final static long serialVersionUID = 42L;
    
    /** Creates new form pfcGuiAfegeixApunt */
    public pfcGuiAfegeixApunt() {
        initComponents();        
        centraFormulari();
    }
    
    private void centraFormulari() {
        
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width; 
        setLocation(screenWidth / 4, screenHeight / 4);
        
    }
     
     //public void estableixDades ( String )
    public void estableixDades (String idMetge, String contrasenyaMetge, String pathp12, GestorInterRemot gestor, String nhcPacient, pfcHistorialGuiMetge pfcHistorialGuiMetge ) {
        
        this.ncolMetge = idMetge;
        this.contrasenyaMetge = contrasenyaMetge;
        this.pathP12 = pathp12;
        this.metge = new Metge ( this.ncolMetge, this.contrasenyaMetge, this.pathP12 );
        this.gestor = gestor;
        this.nhcPacient = nhcPacient;
        this.pfcHistorialGuiMetge = pfcHistorialGuiMetge;
        
    }    
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaApunt = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        botoOK = new javax.swing.JButton();
        botoCancel = new javax.swing.JButton();
        textCodiCIE = new javax.swing.JTextField();
        botoObreCIE = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Afegeix apunt a l'HC");
        setAlwaysOnTop(true);
        setName("afegeixApuntJFrame");
        textAreaApunt.setColumns(20);
        textAreaApunt.setRows(5);
        jScrollPane1.setViewportView(textAreaApunt);

        jLabel1.setText("Apunt:");

        jLabel2.setText("CIE-9:");

        jLabel3.setText("Afegeix apunt pel pacient");

        botoOK.setText("D'acord");
        botoOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoOKActionPerformed(evt);
            }
        });

        botoCancel.setText("Cancel\u00b7la");
        botoCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoCancelActionPerformed(evt);
            }
        });

        botoObreCIE.setText("Cerca codi");
        botoObreCIE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoObreCIEActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(220, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(textCodiCIE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botoObreCIE)))
                        .addContainerGap(94, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(botoOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(botoCancel)
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(botoObreCIE)
                    .addComponent(textCodiCIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botoOK)
                    .addComponent(botoCancel))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botoObreCIEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoObreCIEActionPerformed
// TODO add your handling code here:
        
        pfcGuiCodisCIE pfcGuiCodisCIE = new pfcGuiCodisCIE();
        pfcGuiCodisCIE.estableixDades ( this.gestor , this );        
        pfcGuiCodisCIE.setVisible( true );
        
    }//GEN-LAST:event_botoObreCIEActionPerformed

    private void botoCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoCancelActionPerformed
// TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_botoCancelActionPerformed

    private void botoOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoOKActionPerformed
// TODO add your handling code here:
        String cieSeleccionat = this.textCodiCIE.getText();
        String apuntEscrit = this.textAreaApunt.getText();
               
        
       if (cieSeleccionat.compareTo("") == 0) {
            
            javax.swing.JOptionPane.showMessageDialog ( null , "Escriviu un codi CIE-9", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            this.textCodiCIE.requestFocus ();
            return ;
       }     
        
        if (apuntEscrit.compareTo("") == 0 ) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "Escriviu l'apunt a afegir", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            this.textAreaApunt.requestFocus(); 
            return;
            
        }
     
                
       try {
                    
             org.jdom.Document a = metge.procedure1();
             org.jdom.Document b = gestor.procedure2(a);                            
             String operacio = new String ("Inserir_Visita");
             String c = metge.pas3InserirVisita ( b , this.nhcPacient , cieSeleccionat , apuntEscrit);                                 
             gestor.pas4InserirVisita( c );                    
                     
             this.pfcHistorialGuiMetge.consultaHistorial ( this.nhcPacient );                     
             this.setVisible ( false );
                    
                } catch (java.rmi.RemoteException o) {
                    System.out.println("Error: o ");
             }         
    
        
            
            
    }//GEN-LAST:event_botoOKActionPerformed
    
    public void estableixCodiCie ( String codiCIE ) {
        this.textCodiCIE.setText ( codiCIE );
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botoCancel;
    private javax.swing.JButton botoOK;
    private javax.swing.JButton botoObreCIE;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textAreaApunt;
    private javax.swing.JTextField textCodiCIE;
    // End of variables declaration//GEN-END:variables
    
}
