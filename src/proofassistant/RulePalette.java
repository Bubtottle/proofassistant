/*
 * The MIT License
 *
 * Copyright 2014 Declan.
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

import proofassistant.line.NDLine;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import static proofassistant.Globals.assist;
import static proofassistant.Globals.createExtraLines;
import static proofassistant.Globals.currentGoalIndex;
import static proofassistant.Globals.currentResourceIndex;
import static proofassistant.Globals.extraLines;
import static proofassistant.Globals.frame;
import static proofassistant.Globals.proofArray;

/**
 *
 * @author Declan
 */
public class RulePalette extends javax.swing.JDialog {

    private JButton ruleSystemButton;
    /**
     * Creates new form RulePalette
     */
    public RulePalette(Frame owner, JButton ruleSystemButton) {
        super(owner);
        this.ruleSystemButton = ruleSystemButton;
        initComponents();
        myInit();
    }
    
    private void myInit() {
        Point loc = Globals.frame.getLocation();
        loc.x = Math.min(loc.x + Globals.frame.getWidth(), java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - getWidth());
        
        loc.y = loc.y - (getHeight() - Globals.frame.getHeight())/2;
        setLocation(loc);
        updateResults();
    }
    
    private DefaultComboBoxModel getComboContents() {
        String[] availablePresets = new String[Globals.listOfSystems.size()];
        for (int i = 0; i < availablePresets.length; i++) {
            availablePresets[i] = Globals.listOfSystems.get(i).getName().toString();
        }
        
        return new DefaultComboBoxModel(availablePresets);
    }
    
    public void updateResults() {
        String system = "";
        ArrayList<String> allowedRules = new ArrayList<>();
        Iterator<String> terate = Globals.allowedRules.keySet().iterator();
        while (terate.hasNext()) {
            String currentRule = terate.next();
            if (Globals.allowedRules.get(currentRule)) {
                allowedRules.add(currentRule);
//                System.out.println(currentRule);
            }
        }
        for (int i = 0; i < Globals.listOfSystems.size() && system.equals(""); i++) {
//            System.out.println();
//            System.out.println("System " + Globals.listOfSystems.get(i).getName());
//            System.out.println(Globals.listOfSystems.get(i).size() == allowedRules.size());
//            System.out.println(Globals.listOfSystems.get(i).containsAll(allowedRules));
//            for (int j = 0; j < Globals.listOfSystems.get(i).size(); j++) {
//                System.out.println(Globals.listOfSystems.get(i).get(j));
//            }
//            System.out.println(allowedRules.containsAll(Globals.listOfSystems.get(i)));
            if (Globals.listOfSystems.get(i).containsAll(allowedRules) && allowedRules.containsAll(Globals.listOfSystems.get(i))) {
                system = Globals.listOfSystems.get(i).getName().toString();
//                System.out.println("success " + system);
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
                
                
            }
        }
        
        if (system.equals("")) {
            presetComboBox.setSelectedItem(null);
            if (ruleSystemButton != null)
            ruleSystemButton.setText("Custom");
            Globals.currentPreset = null;
//            System.out.println("&&&&&&&&&&&&&&&&&&&&&");
        } else {
            presetComboBox.setSelectedItem(system);
            if (ruleSystemButton != null)
            ruleSystemButton.setText(system);
//            System.out.println("&&&&&&&&&&&&&&&&&&&&&");
            Globals.currentPreset = system;
        }
        frame.updatePanel();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox9 = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jLabel1 = new javax.swing.JLabel();
        presetComboBox = new javax.swing.JComboBox();
        savePresetButton = new javax.swing.JButton();
        jXTaskPane1 = new org.jdesktop.swingx.JXTaskPane();
        jPanel1 = new javax.swing.JPanel();
        conIntroCheckBox = new javax.swing.JCheckBox();
        conElimCheckBox = new javax.swing.JCheckBox();
        disIntroCheckBox = new javax.swing.JCheckBox();
        disElimCheckBox = new javax.swing.JCheckBox();
        impIntroCheckBox = new javax.swing.JCheckBox();
        impElimCheckBox = new javax.swing.JCheckBox();
        equIntroCheckBox = new javax.swing.JCheckBox();
        equElimCheckBox = new javax.swing.JCheckBox();
        negIntroCheckBox = new javax.swing.JCheckBox();
        negElimCheckBox = new javax.swing.JCheckBox();
        jXTaskPane2 = new org.jdesktop.swingx.JXTaskPane();
        jPanel2 = new javax.swing.JPanel();
        qaIntroCheckBox = new javax.swing.JCheckBox();
        qaElimCheckBox = new javax.swing.JCheckBox();
        qeIntroCheckBox = new javax.swing.JCheckBox();
        qeElimCheckBox = new javax.swing.JCheckBox();
        jXTaskPane3 = new org.jdesktop.swingx.JXTaskPane();
        jPanel3 = new javax.swing.JPanel();
        eqIntroCheckBox = new javax.swing.JCheckBox();
        eqElimCheckBox = new javax.swing.JCheckBox();
        jXTaskPane6 = new org.jdesktop.swingx.JXTaskPane();
        jPanel7 = new javax.swing.JPanel();
        atIntroCheckBox = new javax.swing.JCheckBox();
        atElimCheckBox = new javax.swing.JCheckBox();
        boxIntroCheckBox = new javax.swing.JCheckBox();
        boxElimCheckBox = new javax.swing.JCheckBox();
        diaIntroCheckBox = new javax.swing.JCheckBox();
        diaElimCheckBox = new javax.swing.JCheckBox();
        showContextCheckBox = new javax.swing.JCheckBox();
        jXTaskPane7 = new org.jdesktop.swingx.JXTaskPane();
        jPanel8 = new javax.swing.JPanel();
        nomIntroCheckBox = new javax.swing.JCheckBox();
        nomElimCheckBox = new javax.swing.JCheckBox();
        selfIntroCheckBox = new javax.swing.JCheckBox();
        selfElimCheckBox = new javax.swing.JCheckBox();
        jXTaskPane4 = new org.jdesktop.swingx.JXTaskPane();
        jPanel4 = new javax.swing.JPanel();
        doubleNegationCheckBox = new javax.swing.JCheckBox();
        inductionCheckBox = new javax.swing.JCheckBox();
        showQCheckBox = new javax.swing.JCheckBox();
        sameLineCheckBox = new javax.swing.JCheckBox();
        secondOrderCheckBox = new javax.swing.JCheckBox();
        jXTaskPane5 = new org.jdesktop.swingx.JXTaskPane();
        jPanel5 = new javax.swing.JPanel();
        universalsShortcutsCheckBox = new javax.swing.JCheckBox();
        autoParametersCheckBox = new javax.swing.JCheckBox();
        eqIdBoxesCheckBox = new javax.swing.JCheckBox();
        equIdBoxCheckBox = new javax.swing.JCheckBox();

        jCheckBox9.setText("jCheckBox9");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Palette");

        jXTaskPaneContainer1.setBackground(java.awt.SystemColor.control);
        jXTaskPaneContainer1.setMinimumSize(new Dimension(presetComboBox.getMinimumSize().width, 600));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Rule Preset");

        presetComboBox.setModel(getComboContents());
        presetComboBox.setSelectedItem(null);
        presetComboBox.setToolTipText("Select a preset");
        presetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetComboBoxActionPerformed(evt);
            }
        });

        savePresetButton.setText("Save to new preset...");
        savePresetButton.setToolTipText("Save the current setup as a new preset");
        savePresetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePresetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(savePresetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(presetComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(savePresetButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPaneContainer1.add(jXPanel1);

        jXTaskPane1.setTitle("Basic Rules");

        conIntroCheckBox.setSelected(Globals.allowedRules.get("conIntro")
        );
        conIntroCheckBox.setText(Globals.operators.get("con") + "I"
        );
        conIntroCheckBox.setToolTipText("Conjunction introduction");
        conIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                conIntroCheckBoxItemStateChanged(evt);
            }
        });

        conElimCheckBox.setSelected(Globals.allowedRules.get("conElim"));
        conElimCheckBox.setText(Globals.operators.get("con") + "E");
        conElimCheckBox.setToolTipText("Conjunction elimination");
        conElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                conElimCheckBoxItemStateChanged(evt);
            }
        });

        disIntroCheckBox.setSelected(Globals.allowedRules.get("disIntro"));
        disIntroCheckBox.setText(Globals.operators.get("dis") + "I");
        disIntroCheckBox.setToolTipText("Disjunction introduction");
        disIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                disIntroCheckBoxItemStateChanged(evt);
            }
        });

        disElimCheckBox.setSelected(Globals.allowedRules.get("disElim"));
        disElimCheckBox.setText(Globals.operators.get("dis") + "E");
        disElimCheckBox.setToolTipText("Disjunction elimination");
        disElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                disElimCheckBoxItemStateChanged(evt);
            }
        });

        impIntroCheckBox.setSelected(Globals.allowedRules.get("impIntro"));
        impIntroCheckBox.setText(Globals.operators.get("imp") + "I");
        impIntroCheckBox.setToolTipText("Implication introduction");
        impIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                impIntroCheckBoxItemStateChanged(evt);
            }
        });

        impElimCheckBox.setSelected(Globals.allowedRules.get("impElim"));
        impElimCheckBox.setText(Globals.operators.get("imp") + "E");
        impElimCheckBox.setToolTipText("Implication elimination");
        impElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                impElimCheckBoxItemStateChanged(evt);
            }
        });

        equIntroCheckBox.setSelected(Globals.allowedRules.get("equIntro"));
        equIntroCheckBox.setText(Globals.operators.get("equ") + "I");
        equIntroCheckBox.setToolTipText("Equivalence introduction");
        equIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                equIntroCheckBoxItemStateChanged(evt);
            }
        });

        equElimCheckBox.setSelected(Globals.allowedRules.get("equElim"));
        equElimCheckBox.setText(Globals.operators.get("equ") + "E");
        equElimCheckBox.setToolTipText("Equivalence elimination");
        equElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                equElimCheckBoxItemStateChanged(evt);
            }
        });

        negIntroCheckBox.setSelected(Globals.allowedRules.get("negIntro"));
        negIntroCheckBox.setText(Globals.operators.get("neg") + "I");
        negIntroCheckBox.setToolTipText("Negation introduction");
        negIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                negIntroCheckBoxItemStateChanged(evt);
            }
        });

        negElimCheckBox.setSelected(Globals.allowedRules.get("negElim"));
        negElimCheckBox.setText(Globals.operators.get("neg") + "E");
        negElimCheckBox.setToolTipText("Negation elimination");
        negElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                negElimCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(conIntroCheckBox)
                    .addComponent(disIntroCheckBox)
                    .addComponent(impIntroCheckBox)
                    .addComponent(equIntroCheckBox)
                    .addComponent(negIntroCheckBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(disElimCheckBox)
                    .addComponent(conElimCheckBox)
                    .addComponent(equElimCheckBox)
                    .addComponent(negElimCheckBox)
                    .addComponent(impElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conIntroCheckBox)
                    .addComponent(conElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(disIntroCheckBox)
                    .addComponent(disElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(impIntroCheckBox)
                    .addComponent(impElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(equIntroCheckBox)
                    .addComponent(equElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(negIntroCheckBox)
                    .addComponent(negElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane1.getContentPane().add(jPanel1);

        jXTaskPaneContainer1.add(jXTaskPane1);

        jXTaskPane2.setCollapsed(true);
        jXTaskPane2.setTitle("Quantifier Rules");

        qaIntroCheckBox.setSelected(Globals.allowedRules.get("qaIntro"));
        qaIntroCheckBox.setText("∀I");
        qaIntroCheckBox.setToolTipText("Universal introduction");
        qaIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                qaIntroCheckBoxItemStateChanged(evt);
            }
        });

        qaElimCheckBox.setSelected(Globals.allowedRules.get("qaElim"));
        qaElimCheckBox.setText("∀E");
        qaElimCheckBox.setToolTipText("Universal Elimination");
        qaElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                qaElimCheckBoxItemStateChanged(evt);
            }
        });

        qeIntroCheckBox.setSelected(Globals.allowedRules.get("qeIntro"));
        qeIntroCheckBox.setText("∃I");
        qeIntroCheckBox.setToolTipText("Existential introduction");
        qeIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                qeIntroCheckBoxItemStateChanged(evt);
            }
        });

        qeElimCheckBox.setSelected(Globals.allowedRules.get("qeElim"));
        qeElimCheckBox.setText("∃E");
        qeElimCheckBox.setToolTipText("Existential elimination");
        qeElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                qeElimCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(qaIntroCheckBox)
                    .addComponent(qeIntroCheckBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(qaElimCheckBox)
                    .addComponent(qeElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qaIntroCheckBox)
                    .addComponent(qaElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qeIntroCheckBox)
                    .addComponent(qeElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane2.getContentPane().add(jPanel2);

        jXTaskPaneContainer1.add(jXTaskPane2);

        jXTaskPane3.setCollapsed(true);
        jXTaskPane3.setTitle("Identity Rules");

        eqIntroCheckBox.setSelected(Globals.allowedRules.get("eqIntro"));
        eqIntroCheckBox.setText("=I");
        eqIntroCheckBox.setToolTipText("Identity introduction");
        eqIntroCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        eqIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                eqIntroCheckBoxItemStateChanged(evt);
            }
        });

        eqElimCheckBox.setSelected(Globals.allowedRules.get("eqElim"));
        eqElimCheckBox.setText("=E");
        eqElimCheckBox.setToolTipText("Identity elimination");
        eqElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                eqElimCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(eqIntroCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(eqElimCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eqIntroCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eqElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane3.getContentPane().add(jPanel3);

        jXTaskPaneContainer1.add(jXTaskPane3);

        jXTaskPane6.setCollapsed(true);
        jXTaskPane6.setTitle("Modal Logic Rules");

        atIntroCheckBox.setSelected(Globals.allowedRules.get("atIntro")
        );
        atIntroCheckBox.setText("@I");
        atIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                atIntroCheckBoxItemStateChanged(evt);
            }
        });

        atElimCheckBox.setSelected(Globals.allowedRules.get("atElim"));
        atElimCheckBox.setText("@E");
        atElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                atElimCheckBoxItemStateChanged(evt);
            }
        });

        boxIntroCheckBox.setSelected(Globals.allowedRules.get("boxIntro"));
        boxIntroCheckBox.setText("☐ I");
        boxIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxIntroCheckBoxItemStateChanged(evt);
            }
        });

        boxElimCheckBox.setSelected(Globals.allowedRules.get("boxElim"));
        boxElimCheckBox.setText("☐ E");
        boxElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxElimCheckBoxItemStateChanged(evt);
            }
        });

        diaIntroCheckBox.setSelected(Globals.allowedRules.get("diaIntro"));
        diaIntroCheckBox.setText("◇I");
        diaIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                diaIntroCheckBoxItemStateChanged(evt);
            }
        });

        diaElimCheckBox.setSelected(Globals.allowedRules.get("diaElim"));
        diaElimCheckBox.setText("◇E");
        diaElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                diaElimCheckBoxItemStateChanged(evt);
            }
        });

        showContextCheckBox.setSelected(Globals.allowedRules.get("showContext"));
        showContextCheckBox.setText("Show contexts");
        showContextCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showContextCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(atIntroCheckBox)
                            .addComponent(boxIntroCheckBox)
                            .addComponent(diaIntroCheckBox))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boxElimCheckBox)
                            .addComponent(atElimCheckBox)
                            .addComponent(diaElimCheckBox)))
                    .addComponent(showContextCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(atIntroCheckBox)
                    .addComponent(atElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxIntroCheckBox)
                    .addComponent(boxElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(diaElimCheckBox)
                    .addComponent(diaIntroCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showContextCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane6.getContentPane().add(jPanel7);

        jXTaskPaneContainer1.add(jXTaskPane6);

        jXTaskPane7.setCollapsed(true);
        jXTaskPane7.setTitle("Hybrid Logic Rules");

        nomIntroCheckBox.setSelected(Globals.allowedRules.get("nomIntro"));
        nomIntroCheckBox.setText(":I");
        nomIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nomIntroCheckBoxItemStateChanged(evt);
            }
        });

        nomElimCheckBox.setSelected(Globals.allowedRules.get("nomElim"));
        nomElimCheckBox.setText(":E");
        nomElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nomElimCheckBoxItemStateChanged(evt);
            }
        });

        selfIntroCheckBox.setSelected(Globals.allowedRules.get("selfIntro"));
        selfIntroCheckBox.setText("↓I");
        selfIntroCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selfIntroCheckBoxItemStateChanged(evt);
            }
        });

        selfElimCheckBox.setSelected(Globals.allowedRules.get("selfIntro")
        );
        selfElimCheckBox.setText("↓E");
        selfElimCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selfElimCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(nomIntroCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(nomElimCheckBox))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(selfIntroCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(selfElimCheckBox)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nomIntroCheckBox)
                    .addComponent(nomElimCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selfIntroCheckBox)
                    .addComponent(selfElimCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane7.getContentPane().add(jPanel8);

        jXTaskPaneContainer1.add(jXTaskPane7);

        jXTaskPane4.setCollapsed(true);
        jXTaskPane4.setTitle("Special Rules");

        jPanel4.setPreferredSize(new java.awt.Dimension(190, 175));

        doubleNegationCheckBox.setSelected(Globals.allowedRules.get("doubleNegation"));
        doubleNegationCheckBox.setText("Double Negation");
        doubleNegationCheckBox.setToolTipText("Allow double negation");
        doubleNegationCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                doubleNegationCheckBoxItemStateChanged(evt);
            }
        });

        inductionCheckBox.setSelected(Globals.allowedRules.get("induction"));
        inductionCheckBox.setText("Induction");
        inductionCheckBox.setToolTipText("Allow induction (useful for Peano Arithmetic)");
        inductionCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                inductionCheckBoxItemStateChanged(evt);
            }
        });

        showQCheckBox.setSelected(Globals.allowedRules.get("Q"));
        showQCheckBox.setText("Show Q Axioms");
        showQCheckBox.setToolTipText("Show the axioms for Robinson Arithmetic");
        showQCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showQCheckBoxItemStateChanged(evt);
            }
        });

        sameLineCheckBox.setSelected(Globals.allowedRules.get("sameLine"));
        sameLineCheckBox.setText("Same Line Rule");
        sameLineCheckBox.setToolTipText("Justify a line by an identical line");
        sameLineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sameLineCheckBoxActionPerformed(evt);
            }
        });

        secondOrderCheckBox.setSelected(Globals.allowedRules.get("secondOrder"));
        secondOrderCheckBox.setText("2nd Order Logic");
        secondOrderCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                secondOrderCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inductionCheckBox)
                    .addComponent(showQCheckBox)
                    .addComponent(doubleNegationCheckBox)
                    .addComponent(sameLineCheckBox)
                    .addComponent(secondOrderCheckBox))
                .addContainerGap(149, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(doubleNegationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showQCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inductionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sameLineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secondOrderCheckBox)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jXTaskPane4.getContentPane().add(jPanel4);

        jXTaskPaneContainer1.add(jXTaskPane4);

        jXTaskPane5.setCollapsed(true);
        jXTaskPane5.setTitle("Shortcut Options");

        universalsShortcutsCheckBox.setSelected(Globals.allowedRules.get("universalsShortcuts"));
        universalsShortcutsCheckBox.setText("Shortcuts with ∀E");
        universalsShortcutsCheckBox.setToolTipText("Bypass universal elimination and access the underlying operator");
        universalsShortcutsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                universalsShortcutsCheckBoxItemStateChanged(evt);
            }
        });

        autoParametersCheckBox.setSelected(Globals.allowedRules.get("autoParameters"));
        autoParametersCheckBox.setText("Auto-Parameters");
        autoParametersCheckBox.setToolTipText("Automatically generate parameters when a new one is needed");
        autoParametersCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoParametersCheckBoxItemStateChanged(evt);
            }
        });

        eqIdBoxesCheckBox.setSelected(Globals.allowedRules.get("eqIdentityBoxes"));
        eqIdBoxesCheckBox.setText("= Identity Boxes");
        eqIdBoxesCheckBox.setToolTipText("Allow identity boxes for identity introduction");
        eqIdBoxesCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                eqIdBoxesCheckBoxItemStateChanged(evt);
            }
        });

        equIdBoxCheckBox.setSelected(Globals.allowedRules.get("equIdentityBoxes"));
        equIdBoxCheckBox.setText("≡ Identity Boxes");
        equIdBoxCheckBox.setToolTipText("Give the option of creating an identity box for equivalence introduction");
        equIdBoxCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                equIdBoxCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(equIdBoxCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(universalsShortcutsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(autoParametersCheckBox)
                            .addComponent(eqIdBoxesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(139, 139, 139))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(universalsShortcutsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoParametersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(eqIdBoxesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(equIdBoxCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXTaskPane5.getContentPane().add(jPanel5);

        jXTaskPaneContainer1.add(jXTaskPane5);

        jScrollPane1.setViewportView(jXTaskPaneContainer1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void presetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetComboBoxActionPerformed
        if (evt.getSource() instanceof JComboBox && presetComboBox.getSelectedItem() != null) {
            String preset = presetComboBox.getSelectedItem().toString();
            ProofSystem toUse = null;
//            System.out.println("CURRENTLY " + extraLines.length);
            for (int i = 0; i < Globals.listOfSystems.size() && toUse == null; i++) {
                if (Globals.listOfSystems.get(i).getName().equals(preset)) {
                    toUse = Globals.listOfSystems.get(i);
                }
            }
            
            if (toUse != null) {
                
                
                // Set My Axioms ..
                Globals.myFunLineNums.clear();
                Globals.myFunLines.clear();
                String text = (String)toUse.getAxioms();
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
                if (assist!=null) {
                    proofArray = assist.getProofArray();
//                                System.out.println("Currently " + extraLines.length);
                    if (proofArray.length > extraLines.length) {
                        NDLine[] temp = new NDLine[proofArray.length - extraLines.length];
//                                        System.out.println("temp length " + temp.length);
                        for (int i = 0; i < temp.length; i++) {
                            temp[i] = proofArray[i+extraLines.length];
                        }
                        Globals.proofArray = temp;
//                                        System.out.println("pA length " + proofArray.length);
                        currentGoalIndex -= extraLines.length;
                        currentResourceIndex -= extraLines.length;
                    }
//                                System.out.println("pA length " + proofArray.length);
                    createExtraLines();
//                                System.out.println("pA length " + proofArray.length);
//                                System.out.println("Now " + extraLines.length);
                    NDLine[] temp = new NDLine[proofArray.length + extraLines.length];
//                                System.out.println("temp length " + temp.length);
                    int k = 0;
                    for (int i = 0; i < extraLines.length; i++) {
                        temp[k] = extraLines[i];
                        k++;
                    }
                    for (int i = 0; i < proofArray.length; i++) {
                        temp[k] = proofArray[i];
//                                        System.out.println(proofArray[i].getLine());
                        k++;
                    }
                    proofArray = temp;

                    if (currentGoalIndex > -1) {
                        currentGoalIndex += extraLines.length;
                    }
                    if (currentResourceIndex > -1){
                        currentResourceIndex += extraLines.length;
                    }
                }

                if (assist != null){
                    assist.setProofArray(proofArray);
                }
                frame.updatePanel();
                
                Globals.currentPreset = toUse.getName().toString();
                
                conIntroCheckBox.setSelected(toUse.contains("conIntro"));
                conElimCheckBox.setSelected(toUse.contains("conElim"));
                disIntroCheckBox.setSelected(toUse.contains("disIntro"));
                disElimCheckBox.setSelected(toUse.contains("disElim"));
                impIntroCheckBox.setSelected(toUse.contains("impIntro"));
                impElimCheckBox.setSelected(toUse.contains("impElim"));
                equIntroCheckBox.setSelected(toUse.contains("equIntro"));
                equElimCheckBox.setSelected(toUse.contains("equElim"));
                negIntroCheckBox.setSelected(toUse.contains("negIntro"));
                negElimCheckBox.setSelected(toUse.contains("negElim"));
                qaIntroCheckBox.setSelected(toUse.contains("qaIntro"));
                qaElimCheckBox.setSelected(toUse.contains("qaElim"));
                
                qeIntroCheckBox.setSelected(toUse.contains("qeIntro"));
                qeElimCheckBox.setSelected(toUse.contains("qeElim"));
                eqIntroCheckBox.setSelected(toUse.contains("eqIntro"));
                
                eqElimCheckBox.setSelected(toUse.contains("eqElim"));
                doubleNegationCheckBox.setSelected(toUse.contains("doubleNegation"));
                
                showQCheckBox.setSelected(toUse.contains("Q"));
                inductionCheckBox.setSelected(toUse.contains("induction"));
                equIdBoxCheckBox.setSelected(toUse.contains("equIdentityBoxes"));
                eqIdBoxesCheckBox.setSelected(toUse.contains("eqIdentityBoxes"));
                universalsShortcutsCheckBox.setSelected(toUse.contains("universalsShortcuts"));
                autoParametersCheckBox.setSelected(toUse.contains("autoParameters"));
                sameLineCheckBox.setSelected(toUse.contains("sameLine"));
                showContextCheckBox.setSelected(toUse.contains("showContext"));
                boxIntroCheckBox.setSelected(toUse.contains("boxIntro"));
                boxElimCheckBox.setSelected(toUse.contains("boxElim"));
                diaIntroCheckBox.setSelected(toUse.contains("diaIntro"));
                diaElimCheckBox.setSelected(toUse.contains("diaElim"));
                atIntroCheckBox.setSelected(toUse.contains("atIntro"));
                atElimCheckBox.setSelected(toUse.contains("atElim"));
                nomIntroCheckBox.setSelected(toUse.contains("nomIntro"));
                nomElimCheckBox.setSelected(toUse.contains("nomElim"));
                selfIntroCheckBox.setSelected(toUse.contains("nomIntro"));
                selfElimCheckBox.setSelected(toUse.contains("nomElim"));
                secondOrderCheckBox.setSelected(toUse.contains("secondOrder"));
            }
            presetComboBox.setSelectedItem(preset);
            if (ruleSystemButton != null) {
                ruleSystemButton.setText(preset);
//                System.out.println("&&&&&&&&&&&&&&&&&&&&&");
            }
            
            
        }
    }//GEN-LAST:event_presetComboBoxActionPerformed

    
    private void conIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_conIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("conIntro", conIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_conIntroCheckBoxItemStateChanged

    private void conElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_conElimCheckBoxItemStateChanged
        Globals.allowedRules.put("conElim", conElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_conElimCheckBoxItemStateChanged

    private void disIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_disIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("disIntro", disIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_disIntroCheckBoxItemStateChanged

    private void disElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_disElimCheckBoxItemStateChanged
        Globals.allowedRules.put("disElim", disElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_disElimCheckBoxItemStateChanged

    private void impIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_impIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("impIntro", impIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_impIntroCheckBoxItemStateChanged

    private void impElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_impElimCheckBoxItemStateChanged
        Globals.allowedRules.put("impElim", impElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_impElimCheckBoxItemStateChanged

    private void equIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_equIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("equIntro", equIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_equIntroCheckBoxItemStateChanged

    private void equElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_equElimCheckBoxItemStateChanged
        Globals.allowedRules.put("equElim", equElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_equElimCheckBoxItemStateChanged

    private void negIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_negIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("negIntro", negIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_negIntroCheckBoxItemStateChanged

    private void negElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_negElimCheckBoxItemStateChanged
        Globals.allowedRules.put("negElim", negElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_negElimCheckBoxItemStateChanged

    private void qaIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_qaIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("qaIntro", qaIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_qaIntroCheckBoxItemStateChanged

    private void qaElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_qaElimCheckBoxItemStateChanged
        Globals.allowedRules.put("qaElim", qaElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_qaElimCheckBoxItemStateChanged

    private void qeIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_qeIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("qeIntro", qeIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_qeIntroCheckBoxItemStateChanged

    private void qeElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_qeElimCheckBoxItemStateChanged
        Globals.allowedRules.put("qeElim", qeElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_qeElimCheckBoxItemStateChanged

    private void eqIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_eqIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("eqIntro", eqIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_eqIntroCheckBoxItemStateChanged

    private void eqElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_eqElimCheckBoxItemStateChanged
        Globals.allowedRules.put("eqElim", eqElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_eqElimCheckBoxItemStateChanged

    private void eqIdBoxesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_eqIdBoxesCheckBoxItemStateChanged
        Globals.allowedRules.put("eqIdentityBoxes", eqIdBoxesCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_eqIdBoxesCheckBoxItemStateChanged

    
    private void savePresetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePresetButtonActionPerformed
        String presetName = JOptionPane.showInputDialog(this, "Name your preset\nNOTE: My Axioms are saved with preset!", "Save new preset", JOptionPane.PLAIN_MESSAGE);
        if (presetName == null) {
            return;
        }
        ProofSystem preset = new ProofSystem(presetName);
        
        if (conIntroCheckBox.isSelected()) {
            preset.add("conIntro");
        }
        if (conElimCheckBox.isSelected()) {
            preset.add("conElim");
        }
        if (disIntroCheckBox.isSelected()) {
            preset.add("disIntro");
        }
        if (disElimCheckBox.isSelected()) {
            preset.add("disElim");
        }
        if (impIntroCheckBox.isSelected()) {
            preset.add("impIntro");
        }
        if (impElimCheckBox.isSelected()) {
            preset.add("impElim");
        }
        if (equIntroCheckBox.isSelected()) {
            preset.add("equIntro");
        }
        if (equElimCheckBox.isSelected()) {
            preset.add("equElim");
        }
        if (negIntroCheckBox.isSelected()) {
            preset.add("negIntro");
        }
        if (negElimCheckBox.isSelected()) {
            preset.add("negElim");
        }
        if (qaIntroCheckBox.isSelected()) {
            preset.add("qaIntro");
        }
        if (qaElimCheckBox.isSelected()) {
            preset.add("qaElim");
        }
        if (qeIntroCheckBox.isSelected()) {
            preset.add("qeIntro");
        }
        if (qeElimCheckBox.isSelected()) {
            preset.add("qeElim");
        }
        if (eqIntroCheckBox.isSelected()) {
            preset.add("eqIntro");
        }
        if (eqElimCheckBox.isSelected()) {
            preset.add("eqElim");
        }
        if (eqIdBoxesCheckBox.isSelected()) {
            preset.add("eqIdBoxes");
        }
        if (doubleNegationCheckBox.isSelected()) {
            preset.add("doubleNegation");
        }
        if (showQCheckBox.isSelected()) {
            preset.add("Q");
        }
        if (inductionCheckBox.isSelected()) {
            preset.add("induction");
        }
        if (equIdBoxCheckBox.isSelected()) {
            preset.add("equIdentityBoxes");
        }
        if (eqIdBoxesCheckBox.isSelected()) {
            preset.add("eqIdentityBoxes");
        }
        if (universalsShortcutsCheckBox.isSelected()) {
            preset.add("universalsShortcuts");
        }
        if (autoParametersCheckBox.isSelected()) {
            preset.add("autoParameters");
        }
        if (showContextCheckBox.isSelected()) {
            preset.add("showContext");
        }
        if (boxIntroCheckBox.isSelected()) {
            preset.add("boxIntro");
        }
        if (boxElimCheckBox.isSelected()) {
            preset.add("boxElim");
        }
        if (diaIntroCheckBox.isSelected()) {
            preset.add("diaIntro");
        }
        if (diaElimCheckBox.isSelected()) {
            preset.add("diaElim");
        }
        if (atIntroCheckBox.isSelected()) {
            preset.add("atIntro");
        }
        if (atElimCheckBox.isSelected()) {
            preset.add("atElim");
        }
        if (nomIntroCheckBox.isSelected()) {
            preset.add("nomIntro");
        }
        if (nomElimCheckBox.isSelected()) {
            preset.add("nomElim");
        }
        if (nomIntroCheckBox.isSelected()) {
            preset.add("selfIntro");
        }
        if (nomElimCheckBox.isSelected()) {
            preset.add("selfElim");
        }
        if (secondOrderCheckBox.isSelected()) {
            preset.add("secondOrder");
        }
        if (sameLineCheckBox.isSelected()) {
            preset.add("sameLine");
        }
        
        String result = "";
        for (int i = 0; i < Globals.myFunLines.size(); i++) {
            result = result + Globals.myFunLineNums.get(i) + ", " + Globals.myFunLines.get(i) + "\u001f";
        }
//        System.out.println(result);
        preset.setAxioms(result);
        
        
        Globals.listOfSystems.add(preset);
        presetComboBox.setModel(getComboContents());
        updateResults();
    }//GEN-LAST:event_savePresetButtonActionPerformed

    
    private void universalsShortcutsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_universalsShortcutsCheckBoxItemStateChanged
        Globals.allowedRules.put("universalsShortcuts", universalsShortcutsCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_universalsShortcutsCheckBoxItemStateChanged

    private void autoParametersCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoParametersCheckBoxItemStateChanged
        Globals.allowedRules.put("autoParameters", autoParametersCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_autoParametersCheckBoxItemStateChanged

    private void equIdBoxCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_equIdBoxCheckBoxItemStateChanged
        Globals.allowedRules.put("equIdentityBoxes", equIdBoxCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_equIdBoxCheckBoxItemStateChanged

    private void showContextCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showContextCheckBoxItemStateChanged
        Globals.allowedRules.put("showContext", showContextCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_showContextCheckBoxItemStateChanged

    private void atIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_atIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("atIntro", atIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_atIntroCheckBoxItemStateChanged

    private void atElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_atElimCheckBoxItemStateChanged
        Globals.allowedRules.put("atElim", atElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_atElimCheckBoxItemStateChanged

    private void boxIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("boxIntro", boxIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_boxIntroCheckBoxItemStateChanged

    private void boxElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxElimCheckBoxItemStateChanged
        Globals.allowedRules.put("boxElim", boxElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_boxElimCheckBoxItemStateChanged

    private void diaIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_diaIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("diaIntro", diaIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_diaIntroCheckBoxItemStateChanged

    private void diaElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_diaElimCheckBoxItemStateChanged
        Globals.allowedRules.put("diaElim", diaElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_diaElimCheckBoxItemStateChanged

    private void nomIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nomIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("nomIntro", nomIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_nomIntroCheckBoxItemStateChanged

    private void nomElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nomElimCheckBoxItemStateChanged
        Globals.allowedRules.put("nomElim", nomElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_nomElimCheckBoxItemStateChanged

    private void selfIntroCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selfIntroCheckBoxItemStateChanged
        Globals.allowedRules.put("selfIntro", selfIntroCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_selfIntroCheckBoxItemStateChanged

    private void selfElimCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selfElimCheckBoxItemStateChanged
        Globals.allowedRules.put("selfElim", selfElimCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_selfElimCheckBoxItemStateChanged

    private void secondOrderCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_secondOrderCheckBoxItemStateChanged
        Globals.allowedRules.put("secondOrder", secondOrderCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_secondOrderCheckBoxItemStateChanged

    private void sameLineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sameLineCheckBoxActionPerformed
        Globals.allowedRules.put("sameLine", sameLineCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_sameLineCheckBoxActionPerformed

    private void showQCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showQCheckBoxItemStateChanged
        createExtraLines();
        Globals.allowedRules.put("Q", showQCheckBox.isSelected());
        if (assist!=null) {
            proofArray = assist.getProofArray();
            //            System.out.println("Currently " + extraLines.length);
            if (proofArray.length > extraLines.length) {
                NDLine[] temp = new NDLine[proofArray.length - extraLines.length];
                //                System.out.println("temp length " + temp.length);
                for (int i = 0; i < temp.length; i++) {
                    temp[i] = proofArray[i+extraLines.length];
                }
                Globals.proofArray = temp;
                //                System.out.println("pA length " + proofArray.length);
                currentGoalIndex -= extraLines.length;
                currentResourceIndex -= extraLines.length;
            }
            //            System.out.println("pA length " + proofArray.length);
            createExtraLines();
            //            System.out.println("pA length " + proofArray.length);
            //            System.out.println("Now " + extraLines.length);
            NDLine[] temp = new NDLine[proofArray.length + extraLines.length];
            //            System.out.println("temp length " + temp.length);
            int k = 0;
            for (int i = 0; i < extraLines.length; i++) {
                temp[k] = extraLines[i];
                k++;
            }
            for (int i = 0; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                //                System.out.println(proofArray[i].getLine());
                k++;
            }
            proofArray = temp;

            if (currentGoalIndex > -1) {
                currentGoalIndex += extraLines.length;
            }
            if (currentResourceIndex > -1){
                currentResourceIndex += extraLines.length;
            }
        }

        if (assist != null){
            assist.setProofArray(proofArray);
        }
        frame.updatePanel();
        updateResults();
    }//GEN-LAST:event_showQCheckBoxItemStateChanged

    private void inductionCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_inductionCheckBoxItemStateChanged
        Globals.allowedRules.put("induction", inductionCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_inductionCheckBoxItemStateChanged

    private void doubleNegationCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_doubleNegationCheckBoxItemStateChanged
        Globals.allowedRules.put("doubleNegation", doubleNegationCheckBox.isSelected());
        updateResults();
    }//GEN-LAST:event_doubleNegationCheckBoxItemStateChanged

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
            java.util.logging.Logger.getLogger(RulePalette.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RulePalette.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RulePalette.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RulePalette.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox atElimCheckBox;
    private javax.swing.JCheckBox atIntroCheckBox;
    private javax.swing.JCheckBox autoParametersCheckBox;
    private javax.swing.JCheckBox boxElimCheckBox;
    private javax.swing.JCheckBox boxIntroCheckBox;
    private javax.swing.JCheckBox conElimCheckBox;
    private javax.swing.JCheckBox conIntroCheckBox;
    private javax.swing.JCheckBox diaElimCheckBox;
    private javax.swing.JCheckBox diaIntroCheckBox;
    private javax.swing.JCheckBox disElimCheckBox;
    private javax.swing.JCheckBox disIntroCheckBox;
    private javax.swing.JCheckBox doubleNegationCheckBox;
    private javax.swing.JCheckBox eqElimCheckBox;
    private javax.swing.JCheckBox eqIdBoxesCheckBox;
    private javax.swing.JCheckBox eqIntroCheckBox;
    private javax.swing.JCheckBox equElimCheckBox;
    private javax.swing.JCheckBox equIdBoxCheckBox;
    private javax.swing.JCheckBox equIntroCheckBox;
    private javax.swing.JCheckBox impElimCheckBox;
    private javax.swing.JCheckBox impIntroCheckBox;
    private javax.swing.JCheckBox inductionCheckBox;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane2;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane3;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane4;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane5;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane6;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane7;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    private javax.swing.JCheckBox negElimCheckBox;
    private javax.swing.JCheckBox negIntroCheckBox;
    private javax.swing.JCheckBox nomElimCheckBox;
    private javax.swing.JCheckBox nomIntroCheckBox;
    private javax.swing.JComboBox presetComboBox;
    private javax.swing.JCheckBox qaElimCheckBox;
    private javax.swing.JCheckBox qaIntroCheckBox;
    private javax.swing.JCheckBox qeElimCheckBox;
    private javax.swing.JCheckBox qeIntroCheckBox;
    private javax.swing.JCheckBox sameLineCheckBox;
    private javax.swing.JButton savePresetButton;
    private javax.swing.JCheckBox secondOrderCheckBox;
    private javax.swing.JCheckBox selfElimCheckBox;
    private javax.swing.JCheckBox selfIntroCheckBox;
    private javax.swing.JCheckBox showContextCheckBox;
    private javax.swing.JCheckBox showQCheckBox;
    private javax.swing.JCheckBox universalsShortcutsCheckBox;
    // End of variables declaration//GEN-END:variables
}
