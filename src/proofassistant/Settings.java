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

import java.util.Iterator;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Declan Thompson
 */
public class Settings extends javax.swing.JDialog {
    
    private String usedLine;
    private String unusedLine;
    private String badTerm = "";
    /**
     * Creates new form Settings
     */
    public Settings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setArityLists();
        initComponents();
        setLocationRelativeTo(Globals.frame);
    }
    
    class ArityVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent jc) {
            JTextArea input = (JTextArea) jc;
            String[] inputValues = input.getText().split(",");
            for (int i = 0; i < inputValues.length; i++){
                String term = "";
                for (int j = 0; j < inputValues[i].length(); j++) {
                    char c = inputValues[i].charAt(j);
                    if ((c > 64 && c < 91) || (c > 96 && c < 123) || c == '\'') {
                        term = term + c;
                    }
                }
                if (Globals.termsUsed.contains(term)) {
                    badTerm = term;
                    return false;
                }
            }
            return true;
        }
        
    }
    
    private void setArityLists() {
        usedLine = "";
        unusedLine = "";
//        System.out.println(Globals.termsUsed.contains("s"));
        Iterator<String> terate = Globals.arity.keySet().iterator();
        while (terate.hasNext()) {
            String current = terate.next();
            if (Globals.termsUsed.contains(current)) {
                usedLine = usedLine + current + Globals.arity.get(current) + ", ";
            } else {
                unusedLine = unusedLine + current + Globals.arity.get(current) + ", ";
            }
        }
        if (usedLine.contains(", "))
        usedLine = usedLine.substring(0, usedLine.lastIndexOf(", "));
        if (unusedLine.contains(", "))
        unusedLine = unusedLine.substring(0, unusedLine.lastIndexOf(", "));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        arityTextArea = new javax.swing.JTextArea();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        showBrackets = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        symbolComboBox = new javax.swing.JComboBox();
        showNumbersCheckBox = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        usedTextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        defaultBoxDiaSymbolTextField = new javax.swing.JTextField();
        NumberTopBottomCheckbox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        NumberOffsetTextbox = new javax.swing.JTextField();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setModal(true);
        setName("settingsDialog"); // NOI18N
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Set Arities for Terms");

        jLabel2.setText("Input term arities as [term][arity],");

        arityTextArea.setColumns(20);
        arityTextArea.setLineWrap(true);
        arityTextArea.setRows(3);
        arityTextArea.setText(unusedLine);
        arityTextArea.setToolTipText("<html>To use a as a term, put a0<br>\nTo use a as an n-ary function, put an<br>\nTo use a as a proposition, put a</html>");
        arityTextArea.setInputVerifier(new ArityVerifier());
        jScrollPane1.setViewportView(arityTextArea);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        showBrackets.setSelected(Globals.showBrackets);
        showBrackets.setText("Show brackets around terms");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Other Settings");

        jLabel4.setText("Choose symbols:");

        symbolComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "⊃, ≡, ~, &, ∨", "→, ↔, ¬, ∧, ∨" }));
        symbolComboBox.setSelectedIndex(Globals.currentOpsIndex);
        symbolComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symbolComboBoxActionPerformed(evt);
            }
        });

        showNumbersCheckBox.setSelected(Globals.qShowNumbers);
        showNumbersCheckBox.setText("Show numbers in Robinson Arithmetic");

        jScrollPane2.setPreferredSize(new java.awt.Dimension(166, 35));

        usedTextArea.setEditable(false);
        usedTextArea.setBackground(java.awt.SystemColor.control);
        usedTextArea.setColumns(20);
        usedTextArea.setRows(2);
        usedTextArea.setText(usedLine);
        usedTextArea.setEnabled(false);
        usedTextArea.setFocusable(false);
        jScrollPane2.setViewportView(usedTextArea);

        jLabel3.setText("Used:");

        jLabel7.setText("Unused:");

        jLabel6.setText("Default [], <> predicate:");

        defaultBoxDiaSymbolTextField.setText(Globals.defaultBoxDiaCharacter);

        NumberTopBottomCheckbox.setSelected(Globals.numberTopDown);
        NumberTopBottomCheckbox.setText("Number top to bottom");

        jLabel8.setText("Numbering offset:");

        NumberOffsetTextbox.setText("" + Globals.lineIncrement);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NumberOffsetTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5)
                            .addComponent(showNumbersCheckBox)
                            .addComponent(showBrackets)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(symbolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(defaultBoxDiaSymbolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(NumberTopBottomCheckbox))
                        .addGap(0, 4, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(saveButton)
                                    .addComponent(cancelButton)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 83, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(symbolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showBrackets)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showNumbersCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(defaultBoxDiaSymbolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NumberTopBottomCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(NumberOffsetTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        JComponent c = (JComponent) evt.getSource();
        if (c.getVerifyInputWhenFocusTarget()) {
            c.requestFocusInWindow();
            if (!c.hasFocus()) {
                JOptionPane.showMessageDialog(this, "You cannot change " + badTerm + " because it has already been used.\nRemove it and undo to an earlier point.", "Error: Cannot change used term", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }  

        Globals.aritiyS = usedTextArea.getText() + ", " + arityTextArea.getText();
        Globals.setArities();
        Globals.status.setArityButtonToolTip();
        Globals.showBrackets = showBrackets.isSelected();
        Globals.qShowNumbers = showNumbersCheckBox.isSelected();
        Globals.defaultBoxDiaCharacter = defaultBoxDiaSymbolTextField.getText();
        Globals.numberTopDown = NumberTopBottomCheckbox.isSelected();
        int offset;
        try {
            offset = Integer.parseInt(NumberOffsetTextbox.getText());
            Globals.lineIncrement = offset;
        } catch (NumberFormatException e) {}
        
        
        String opsToUse = symbolComboBox.getSelectedItem().toString();
        if (opsToUse.equals("⊃, ≡, ~, &, ∨")) {
            Globals.setDefaultOps();
        } else if (opsToUse.equals("→, ↔, ¬, ∧, ∨")) {
            Globals.setNonAucklandOps();
        }
        
        dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void symbolComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symbolComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_symbolComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Settings dialog = new Settings(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField NumberOffsetTextbox;
    private javax.swing.JCheckBox NumberTopBottomCheckbox;
    private javax.swing.JTextArea arityTextArea;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField defaultBoxDiaSymbolTextField;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox showBrackets;
    private javax.swing.JCheckBox showNumbersCheckBox;
    private javax.swing.JComboBox symbolComboBox;
    private javax.swing.JTextArea usedTextArea;
    // End of variables declaration//GEN-END:variables
}