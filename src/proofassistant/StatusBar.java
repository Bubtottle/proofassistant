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
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author Declan Thompson
 */
public class StatusBar extends javax.swing.JPanel {
JFrame frame;
    /**
     * Creates new form StatusBar
     */
    public StatusBar(JFrame frame) {
        this.frame = frame;
        initComponents();
        myInit();
    }
    
    private void myInit() {
        ruleSystemButton.setOpaque(false);
        ruleSystemButton.setContentAreaFilled(false);
        ruleSystemButton.setBorderPainted(false);
        arityButton.setOpaque(false);
        arityButton.setContentAreaFilled(false);
        arityButton.setBorderPainted(false);
        setArityButtonToolTip();
        updateRuleSystem();
    }
    
    public void setDogsBodyText(String text) {
        dogsbodyLabel.setText(text);
    }
    
    public void setArityButtonToolTip() {
        String usedLine = "";
        String unusedLine = "";
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
        arityButton.setToolTipText("<html>Used: " + usedLine + "<br>Unused: " + unusedLine + "</html>");
    }
    
    public void updateRuleSystem() {
        if (Globals.rulePal != null) {
            Globals.rulePal.updateResults();
        } else {
            String system = "";
            ArrayList<String> allowedRules = new ArrayList<>();
            Iterator<String> terate = Globals.allowedRules.keySet().iterator();
            while (terate.hasNext()) {
                String currentRule = terate.next();
                if (Globals.allowedRules.get(currentRule)) {
                    allowedRules.add(currentRule);
//                    System.out.println(currentRule);
                }
            }
            for (int i = 0; i < Globals.listOfSystems.size() && system.equals(""); i++) {
//                System.out.println();
//                System.out.println("System " + Globals.listOfSystems.get(i).getName());
//                System.out.println(Globals.listOfSystems.get(i).size() == allowedRules.size());
//                System.out.println(Globals.listOfSystems.get(i).containsAll(allowedRules));
//                for (int j = 0; j < Globals.listOfSystems.get(i).size(); j++) {
//                    System.out.println(Globals.listOfSystems.get(i).get(j));
//                }
//                System.out.println(allowedRules.containsAll(Globals.listOfSystems.get(i)));
                if (Globals.listOfSystems.get(i).containsAll(allowedRules) && allowedRules.containsAll(Globals.listOfSystems.get(i))) {
                    system = Globals.listOfSystems.get(i).getName().toString();
//                    System.out.println("success " + system);
                    // Set My Axioms ..
                    Globals.myFunLineNums.clear();
                    Globals.myFunLines.clear();
                    String text = (String)Globals.listOfSystems.get(i).getAxioms();
                    text = text.replace("\u001f", "\n");
                    String specialLineNum;
                    String axiom;
                    while (text.contains("\n")) {
                        specialLineNum = text.substring(0, text.indexOf(","));
                        axiom = text.substring(text.indexOf(",") + 1, text.indexOf("\n"));
                        Globals.myFunLineNums.add(specialLineNum.trim());
                        Globals.myFunLines.add(axiom.trim());
            //            System.out.println("added " + axiom.trim());
                        text = text.substring(text.indexOf("\n") + 1);
                    }
                    if (text.contains(",")) {
                        specialLineNum = text.substring(0, text.indexOf(","));
                        axiom = text.substring(text.indexOf(",") + 1);
                        Globals.myFunLineNums.add(specialLineNum.trim());
                        Globals.myFunLines.add(axiom.trim());
                    }
                    Globals.createExtraLines();

                    Globals.currentPreset = Globals.listOfSystems.get(i).getName().toString();
                }
            }

            if (system.equals("")) {
                ruleSystemButton.setText("Custom");
            } else {
                ruleSystemButton.setText(system);
            }
        }
    }
    
    public void setProofSystem(String system) {
        proofSystemLabel.setText(system);
        revalidate();
    }
    
    public void setRuleSystem(String system) {
        ruleSystemButton.setText(system);
        revalidate();
    }
    
    public String getRuleSystem() {
        return ruleSystemButton.getText();
    }
    
    public JButton getRuleSystemButton() {
        return ruleSystemButton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        ruleSystemButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        proofSystemLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        arityButton = new javax.swing.JButton();
        dogsbodyLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Current Ruleset:");

        ruleSystemButton.setText("Custom");
        ruleSystemButton.setToolTipText("Click to change ruleset");
        ruleSystemButton.setFocusPainted(false);
        ruleSystemButton.setFocusable(false);
        ruleSystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruleSystemButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Current Proof System:");

        proofSystemLabel.setText("None");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        arityButton.setText("Arities");
        arityButton.setFocusPainted(false);
        arityButton.setFocusable(false);
        arityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arityButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proofSystemLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ruleSystemButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(arityButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(dogsbodyLabel))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(ruleSystemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(proofSystemLabel)
                .addComponent(jLabel1))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(arityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(dogsbodyLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ruleSystemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruleSystemButtonActionPerformed
        if (Globals.rulePal != null && Globals.rulePal.isVisible()) {
            Globals.rulePal.dispose();
        } else {
            Globals.rulePal = new RulePalette(frame, ruleSystemButton);
            Globals.rulePal.setVisible(true);
        }
    }//GEN-LAST:event_ruleSystemButtonActionPerformed

    private void arityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arityButtonActionPerformed
        for (int i = 0; i < actionListeners.size(); i++) {
            actionListeners.get(i).actionPerformed(new ActionEvent(this, 0, "openSettings"));
        }
    }//GEN-LAST:event_arityButtonActionPerformed
    
    private final ArrayList<ActionListener> actionListeners = new ArrayList<>();
    
    public void addActionListener(ActionListener al) {
        actionListeners.add(al);
    }
    
    public void removeActionListener(ActionListener al) {
        actionListeners.remove(al);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton arityButton;
    private javax.swing.JLabel dogsbodyLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel proofSystemLabel;
    private javax.swing.JButton ruleSystemButton;
    // End of variables declaration//GEN-END:variables
}
