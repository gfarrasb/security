/*
 * pfcGuiCodisCIE.java
 *
 * Created on 26 / novembre / 2007, 17:30
 */

/**
 *
 * @author  Gerard Farràs i Ballabriga <gfarrasb@uoc.edu>
 */
public class pfcGuiCodisCIE extends javax.swing.JFrame {
    
    private GestorInterRemot gestor;    
    private pfcGuiAfegeixApunt pfcGuiAfegeixApunt;
    private final static long serialVersionUID = 42L;
    
    /** Creates new form pfcGuiCodisCIE */
    public pfcGuiCodisCIE() {        
        
        llistaCodisCIE = new javax.swing.DefaultListModel();
        initComponents();
        this.centraFormulari ();
        
    }
    
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
        jlistCIES = new javax.swing.JList();
        botoOk = new javax.swing.JButton();
        botoCancela = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Llistat de Codis CIE-9");
        setAlwaysOnTop(true);
        jlistCIES.setModel(this.llistaCodisCIE);
        jScrollPane1.setViewportView(jlistCIES);

        botoOk.setText("D'acord");
        botoOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoOkActionPerformed(evt);
            }
        });

        botoCancela.setText("Cancel\u00b7la");
        botoCancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoCancelaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(botoOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botoCancela))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botoOk)
                    .addComponent(botoCancela))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botoCancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoCancelaActionPerformed
// TODO add your handling code here:
            this.setVisible(false);
    }//GEN-LAST:event_botoCancelaActionPerformed

    private void botoOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoOkActionPerformed
// TODO add your handling code here:
             
            String codiCIE = (String)this.jlistCIES.getSelectedValue ();            
            this.pfcGuiAfegeixApunt.estableixCodiCie( obteCIE(codiCIE));            
            this.setVisible(false);
    }//GEN-LAST:event_botoOkActionPerformed
    
    private String obteCIE ( String a ) {
        
        return a.substring(0, a.indexOf("-"));
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pfcGuiCodisCIE().setVisible(true);
            }
        });
    }
    
        //public void estableixDades ( String )
    public void estableixDades ( GestorInterRemot gestor, pfcGuiAfegeixApunt pfcGuiAfegeixApunt ) {              
      
        this.gestor = gestor;       
        this.pfcGuiAfegeixApunt = pfcGuiAfegeixApunt;
        this.poblaLlistaAmbCIES();
      
        
    }    
    
    public void poblaLlistaAmbCIES () {
        
        try {
        
        org.jdom.Document llistatCIEs = gestor.retornaLlistatCIE();
        
        org.jdom.Element pfchistorials = llistatCIEs.getRootElement();
        org.jdom.Element LlistatCIE = pfchistorials.getChild ("LlistatCIE9");
        org.jdom.Element CodiCIE;
        
	java.util.List llistaLlistat = LlistatCIE.getChildren();
		
            for (int i=0;i<llistaLlistat.size();i++) {

			CodiCIE = (org.jdom.Element)llistaLlistat.get(i);
                        org.jdom.Element Codi = CodiCIE.getChild ("Codi");
                        org.jdom.Element Descri = CodiCIE.getChild("Descripcio");
                        
                        String a = new String ( Codi.getText() + "-" + Descri.getText() );                 
                        this.llistaCodisCIE.addElement ( a );                        
			
            }		
		
        
        } catch ( java.rmi.RemoteException o ) {
            System.out.println("error");
        }
                
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botoCancela;
    private javax.swing.JButton botoOk;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList jlistCIES;
    // End of variables declaration//GEN-END:variables
    
    private javax.swing.DefaultListModel llistaCodisCIE;
}