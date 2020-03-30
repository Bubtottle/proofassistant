/*
 * The MIT License
 *
 * Copyright 2014 Declan Thompson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package proofassistant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author Declan Thompson
 */
public class FriendlyInput extends javax.swing.JPanel implements ActionListener {

    JDialog parent;
    /**
     * Creates new form FriendlyInput
     */
    public FriendlyInput() {
        initComponents();
        myInit();
    }
    
    public void setParent(JDialog parent) {
        this.parent=parent;
    }
    
    private DefaultComboBoxModel getComboContents() {
        String[] availablePresets = new String[Globals.listOfSystems.size()];
        for (int i = 0; i < availablePresets.length; i++) {
            availablePresets[i] = Globals.listOfSystems.get(i).getName().toString();
        }
        
        return new DefaultComboBoxModel(availablePresets);
    }
    
    private void myInit() {
        premiseField.addCaretListener(new BracketMatcher());
        conclusionField.addCaretListener(new BracketMatcher());
        premiseField.addAncestorListener(new RequestFocusListener());
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('V', InputEvent.ALT_MASK), "dis");
        premiseField.getActionMap().put("dis", new SymbolAction(premiseField, "dis"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('7', InputEvent.ALT_MASK), "con");
        premiseField.getActionMap().put("con", new SymbolAction(premiseField, "con"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('-', InputEvent.ALT_MASK), "neg");
        premiseField.getActionMap().put("neg", new SymbolAction(premiseField, "neg"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('.', InputEvent.ALT_MASK), "imp");
        premiseField.getActionMap().put("imp", new SymbolAction(premiseField, "imp"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('3', InputEvent.ALT_MASK), "equ");
        premiseField.getActionMap().put("equ", new SymbolAction(premiseField, "equ"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('A', InputEvent.ALT_MASK), "qa");
        premiseField.getActionMap().put("qa", new SymbolAction(premiseField, "qa"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('E', InputEvent.ALT_MASK), "qe");
        premiseField.getActionMap().put("qe", new SymbolAction(premiseField, "qe"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('8', InputEvent.ALT_MASK), "time");
        premiseField.getActionMap().put("time", new SymbolAction(premiseField, "time"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('1', InputEvent.ALT_MASK), "noteq");
        premiseField.getActionMap().put("noteq", new SymbolAction(premiseField, "noteq"));
        premiseField.getInputMap().put(KeyStroke.getKeyStroke('\\', InputEvent.ALT_MASK), "self");
        premiseField.getActionMap().put("self", new SymbolAction(premiseField, "self"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('7', InputEvent.ALT_MASK), "con");
        conclusionField.getActionMap().put("con", new SymbolAction(conclusionField, "con"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('-', InputEvent.ALT_MASK), "neg");
        conclusionField.getActionMap().put("neg", new SymbolAction(conclusionField, "neg"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('V', InputEvent.ALT_MASK), "dis");
        conclusionField.getActionMap().put("dis", new SymbolAction(conclusionField, "dis"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('.', InputEvent.ALT_MASK), "imp");
        conclusionField.getActionMap().put("imp", new SymbolAction(conclusionField, "imp"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('3', InputEvent.ALT_MASK), "equ");
        conclusionField.getActionMap().put("equ", new SymbolAction(conclusionField, "equ"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('A', InputEvent.ALT_MASK), "qa");
        conclusionField.getActionMap().put("qa", new SymbolAction(conclusionField, "qa"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('E', InputEvent.ALT_MASK), "qe");
        conclusionField.getActionMap().put("qe", new SymbolAction(conclusionField, "qe"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('8', InputEvent.ALT_MASK), "time");
        conclusionField.getActionMap().put("time", new SymbolAction(conclusionField, "time"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('1', InputEvent.ALT_MASK), "noteq");
        conclusionField.getActionMap().put("noteq", new SymbolAction(conclusionField, "noteq"));
        conclusionField.getInputMap().put(KeyStroke.getKeyStroke('\\', InputEvent.ALT_MASK), "self");
        conclusionField.getActionMap().put("self", new SymbolAction(conclusionField, "self"));
        
    }
    
    public String getPremises() {
        Globals.newProofBoxPrems = premiseField.getText();
        return premiseField.getText();
    }
    
    public String getConclusion() {
        Globals.newProofBoxConc = conclusionField.getText();
        return conclusionField.getText();
    }
    
    public String getProofSystem() {
        return "NJ";
    }
    
    public String getQuickArity() {
        return arityField.getText();
    }
    
    public String getPreset() {
        return (String)presetComboBox.getSelectedItem();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        symbolSelectorPanel1 = new proofassistant.SymbolSelectorPanel();
        conclusionField = new javax.swing.JTextField();
        premiseField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        arityField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        arrow1 = new proofassistant.Arrow();
        jLabel3 = new javax.swing.JLabel();
        presetComboBox = new javax.swing.JComboBox();

        conclusionField.setText(Globals.newProofBoxConc);

        premiseField.setText(Globals.newProofBoxPrems);
        premiseField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                premiseFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Input your sequent. Use a comma to separate each premise.");

        jLabel2.setText("Use brackets, and ensure they match.");

        jLabel5.setText("Quick change arities:");

        arityField.setToolTipText("<html>To use a as a term, put a0<br>\nTo use a as an n-ary function, put an<br>\nTo use a as a proposition, put a</html>");

        jLabel6.setForeground(new java.awt.Color(153, 153, 153));
        jLabel6.setText("Style Guide:");

        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setText("(p∨q), ~p, ~~p, ∀x∀y(Rxy), ∀x(fx=(gx+x))");

        arrow1.setPreferredSize(new java.awt.Dimension(61, 20));

        javax.swing.GroupLayout arrow1Layout = new javax.swing.GroupLayout(arrow1);
        arrow1.setLayout(arrow1Layout);
        arrow1Layout.setHorizontalGroup(
            arrow1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );
        arrow1Layout.setVerticalGroup(
            arrow1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel3.setText("Ruleset:");

        presetComboBox.setModel(getComboContents());
        presetComboBox.setSelectedItem(Globals.currentPreset);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(premiseField)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(arrow1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conclusionField))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(arityField)))
                        .addGap(6, 6, 6)))
                .addComponent(symbolSelectorPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(symbolSelectorPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(premiseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(conclusionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arrow1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(arityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        symbolSelectorPanel1.addActionListener(this);
    }// </editor-fold>//GEN-END:initComponents

    private void premiseFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_premiseFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_premiseFieldActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(FriendlyInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(FriendlyInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(FriendlyInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(FriendlyInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                FriendlyInput dialog = new FriendlyInput(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField arityField;
    private proofassistant.Arrow arrow1;
    private javax.swing.JTextField conclusionField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField premiseField;
    private javax.swing.JComboBox presetComboBox;
    private proofassistant.SymbolSelectorPanel symbolSelectorPanel1;
    // End of variables declaration//GEN-END:variables

    
    
    class SymbolAction extends AbstractAction {
        private JTextField field;
        private String symbol;
        public SymbolAction(JTextField field, String symbol) {
            this.field = field;
            this.symbol = Globals.operators.get(symbol);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            String tempText = field.getText();
            int selectStart = field.getSelectionStart();
            int selectEnd = field.getSelectionEnd();
            tempText = tempText.substring(0, selectStart) + symbol + tempText.substring(selectEnd);
            field.setText(tempText);
            field.setCaretPosition(selectStart + 1);
        }
        
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(symbolSelectorPanel1)) {
//            System.out.println(ae.getActionCommand());
            if (parent.getFocusOwner().getClass().equals(conclusionField.getClass())) {
                JTextField temp = (JTextField)parent.getFocusOwner();
                String tempText = temp.getText();
                int selectStart = temp.getSelectionStart();
                int selectEnd = temp.getSelectionEnd();
                tempText = tempText.substring(0, selectStart) + ae.getActionCommand() + tempText.substring(selectEnd);
                temp.setText(tempText);
                temp.setCaretPosition(selectStart + 1);
            }
        }
    }
}
