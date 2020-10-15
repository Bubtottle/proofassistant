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

Adapted from http://www.java2s.com/Tutorial/Java/0240__Swing/UsingJOptionPanewithaJSlider.htm
*/

package proofassistant;

import proofassistant.line.NDLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static proofassistant.Globals.assist;
import static proofassistant.Globals.createExtraLines;
import static proofassistant.Globals.currentGoalIndex;
import static proofassistant.Globals.currentResourceIndex;
import static proofassistant.Globals.extraLines;
import static proofassistant.Globals.frame;
import static proofassistant.Globals.proofArray;

/**
 *
 * @author Declan Thompson
 */
public class MyOptionPane implements ActionListener, ChangeListener {
    static int input;
    private static JPanel fun;
    
    public MyOptionPane() {
        
        
        
    }
    
    public static int showJSliderDialog(String message, String title, int sliderStart, int sliderEnd, int sliderCurrent) {
        input = sliderCurrent;
        if (sliderCurrent < sliderStart) {
            sliderCurrent = sliderStart;
        } else if (sliderCurrent > sliderEnd) {
            sliderCurrent = sliderEnd;
        }
        final JSlider slider = new JSlider(sliderStart, sliderEnd, sliderCurrent);
        slider.setMajorTickSpacing((sliderEnd-sliderStart)/10);
        slider.setSnapToTicks(false);
        slider.setPaintTicks(true);
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JSlider theSlider = (JSlider) changeEvent.getSource();
                if (!theSlider.getValueIsAdjusting()) {
                    input = theSlider.getValue();
                    
                }
                Globals.frame.setZoom((float)theSlider.getValue()/100);
            }
        };
        
        slider.addChangeListener(changeListener);
        
        JButton resetButton = new JButton("Reset");
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                slider.setValue(100);
            }
        };
        
        resetButton.addActionListener(actionListener);
        
        JOptionPane pane = new JOptionPane();
        
        pane.setMessage(new Object[] { message, slider, resetButton });
        
        JDialog dialog = pane.createDialog(Globals.frame, title);
        dialog.setVisible(true);
        
        
        
        return input;
    }
    
    public static String showTermSelectorDialog(String line, String term, String replacement) {
        
        
        TermSelectorPanel panel = new TermSelectorPanel();
        panel.setUpForInstances(line, term, replacement);
        JOptionPane pane = new JOptionPane();
        
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        pane.setMessage(new Object[] {"There are multiple matches for " + term + ".", "Choose which instance(s) to replace.", panel});
        JDialog dialog = pane.createDialog(Globals.frame, "Identity Elimination");
        dialog.setVisible(true);
        
        if (pane.getValue() == null || (Integer)pane.getValue() == JOptionPane.CANCEL_OPTION) {
            return "";
        } else {
            return panel.getReplacedLine();
        }
    }
    
    public static String showFriendlyInputDialog() {
        
        
        JOptionPane pane = new JOptionPane();
        FriendlyInput fun = new FriendlyInput();
//        fun.addActionListener(this);
        
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        pane.setMessage(new Object[] {fun});
        JDialog dialog = pane.createDialog(Globals.frame, "New Proof");
        dialog.pack();
        fun.setParent(dialog);
//        System.out.println("hello");
        dialog.setVisible(true);
        
        
        
        if (pane.getValue() == null || (Integer)pane.getValue() == JOptionPane.CANCEL_OPTION) {
            return null;
        } else {
//            String proofSystem = fun.getProofSystem();
//            if (proofSystem.equals("NJ")) {
//                Globals.setProofSystem(0);
//            } else if (proofSystem.equals("NK")) {
//                Globals.setProofSystem(1);
//            } else if (proofSystem.equals("Q")) {
//                Globals.setProofSystem(2);
//            } else if (proofSystem.equals("PA")) {
//                Globals.setProofSystem(3);
//            }
            Globals.aritiyS = Globals.aritiyS + "," + fun.getQuickArity();
            Globals.setArities();
            
            String preset = fun.getPreset();
            ProofSystem toUse = null;
//            System.out.println("CURRENTLY " + extraLines.length);
            for (int i = 0; i < Globals.listOfSystems.size() && toUse == null; i++) {
                if (Globals.listOfSystems.get(i).getName().equals(preset)) {
                    toUse = Globals.listOfSystems.get(i);
                }
            }
            
            if (toUse != null) {
                Globals.setRulesAllows(toUse);
                
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
                
                
            }
            System.out.println(processSequent(fun.getPremises(), fun.getConclusion()));
            return processSequent(fun.getPremises(), fun.getConclusion());
        }
    }
    
    
    public static String showFriendlyLineInputDialog() {
        
        
        JOptionPane pane = new JOptionPane();
        FriendlyLineInput fun = new FriendlyLineInput();
//        fun.addActionListener(this);
        
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        pane.setMessage(new Object[] {fun});
        JDialog dialog = pane.createDialog(Globals.frame, "Cut Line");
        dialog.pack();
        fun.setParent(dialog);
//        System.out.println("hello");
        dialog.setVisible(true);
        
        
        
        if (pane.getValue() == null || (Integer)pane.getValue() == JOptionPane.CANCEL_OPTION) {
            return null;
        } else if (fun.usingTex()) {
            return fun.getLine();
        } else {
            return processLine(fun.getLine());
        }
    }
    
     public static String showFriendlyLineInputDialog(String title) {
        
        
        JOptionPane pane = new JOptionPane();
        FriendlyLineInput fun = new FriendlyLineInput();
//        fun.addActionListener(this);
        
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        pane.setMessage(new Object[] {fun});
        JDialog dialog = pane.createDialog(Globals.frame, title);
        dialog.pack();
        fun.setParent(dialog);
//        System.out.println("hello");
        dialog.setVisible(true);
        
        
        
        if (pane.getValue() == null || (Integer)pane.getValue() == JOptionPane.CANCEL_OPTION) {
            return null;
        } else if (fun.usingTex()) {
            return fun.getLine();
        } else {
            return processLine(fun.getLine());
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
//        System.out.println(ae.getActionCommand());
    }
    
    private static String processLine(String line) {
        int bracketCount = 0;
        String currentSequent = "";
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '(') {
                bracketCount++;
            } else if (c == ')') {
                bracketCount--;
            }
            if (bracketCount == 0 && c == ',') {
                return "ERROR: Out of place comma at index " + i + ";";
            } else {
                currentSequent = currentSequent + c;
            }
        }
        if (bracketCount != 0) {
            return "ERROR: BAD BRACKETS;";
        }
        HashMap<String, String> binOps = new HashMap<>();
        HashMap<String, String> preOps = new HashMap<>();
        
        String conOp = Globals.operators.get("con");
        binOps.put(conOp, "con");
        String disOp = Globals.operators.get("dis");
        binOps.put(disOp, "dis");
        String impOp = Globals.operators.get("imp");
        binOps.put(impOp, "imp");
        String equOp = Globals.operators.get("equ");
        binOps.put(equOp, "equ");
        String qaOp = Globals.operators.get("qa");
        preOps.put(qaOp, "qa");
        String qeOp = Globals.operators.get("qe");
        preOps.put(qeOp, "qe");
        String atOp = Globals.operators.get("at");
        preOps.put(atOp, "at");
        String negOp = Globals.operators.get("neg");
        String falsOp = Globals.operators.get("falsum");
        String noteqOp = Globals.operators.get("noteq");
        String selfOp = Globals.operators.get("self");
        preOps.put(selfOp, "self");
        
        line = line.replace(falsOp, "\\falsum");
        line = line.replace(noteqOp, "\\neq");
        line = parseLine(line, binOps, preOps, negOp);
        return line.replace(" ", ""); 
    }
    
    private static String processSequent(String premises, String conclusion) {
        int bracketCount = 0;
        ArrayList<String> sequents = new ArrayList<>();
        String currentSequent = "";
        for (int i = 0; i < premises.length(); i++) {
            char c = premises.charAt(i);
            if (c == '(') {
                bracketCount++;
            } else if (c == ')') {
                bracketCount--;
            }
            if (bracketCount == 0 && c == ',') {
                sequents.add(currentSequent);
                currentSequent = "";
            } else {
                currentSequent = currentSequent + c;
            }
        }
        if (currentSequent.length() > 0) {
            sequents.add(currentSequent);
        }
        if (bracketCount != 0) {
            return "ERROR: Bad bracket matching;";
        }
        
        HashMap<String, String> binOps = new HashMap<>();
        HashMap<String, String> preOps = new HashMap<>();
        
        String conOp = Globals.operators.get("con");
        binOps.put(conOp, "con");
        String disOp = Globals.operators.get("dis");
        binOps.put(disOp, "dis");
        String impOp = Globals.operators.get("imp");
        binOps.put(impOp, "imp");
        String equOp = Globals.operators.get("equ");
        binOps.put(equOp, "equ");
        String qaOp = Globals.operators.get("qa");
        preOps.put(qaOp, "qa");
        String qeOp = Globals.operators.get("qe");
        preOps.put(qeOp, "qe");
        String negOp = Globals.operators.get("neg");
        String falsOp = Globals.operators.get("falsum");
        String noteqOp = Globals.operators.get("noteq");
        String atOp = Globals.operators.get("at");
        preOps.put(atOp, "at");
        String selfOp = Globals.operators.get("self");
        preOps.put(selfOp, "self");
        
        String finalPrems = "";
        for (int i = 0; i < sequents.size(); i++) {
            String s = sequents.get(i);
            s = s.replace(falsOp, "\\falsum");
            s = s.replace(noteqOp, "\\neq");
            s = parseLine(s, binOps, preOps, negOp);
            s = s.replace(" ", "");
            finalPrems += s + ",";
        }
        
        if (conclusion.equals("")) {
            return "ERROR: No conclusion;";
        }
        conclusion = conclusion.replace(falsOp, "\\falsum");
        conclusion = conclusion.replace(noteqOp, "\\neq");
        conclusion = parseLine(conclusion, binOps, preOps, negOp);
//        System.out.println("CONC" + conclusion);
        conclusion = conclusion.replace(" ", "");
        
        return finalPrems + "-c," + conclusion;
    }
    
    private static String parseLine(String line, HashMap<String, String> binOps, HashMap<String, String> preOps, String negOp) { // Recursively parse line
        // find out the first character - this will give us some clues
        line = line.trim();
        String f = "" + line.charAt(0);
//        System.out.println(line);
//        System.out.println(f + "\u0040" + f.equals("\u0040"));
        while (f.equals(" ") && !line.isEmpty()) {
            line = line.substring(1);
            f = "" + line.charAt(0);
        }
        
        if (line.contains(":")) {
//            System.out.println(line);
//            System.out.println(line.substring(line.indexOf(":") + 1));
            return line.substring(0, line.indexOf(":") + 1) + parseLine(line.substring(line.indexOf(":") + 1), binOps, preOps, negOp);
        }
        
        if (f.equals("[")) {
            return "\\box{" + line.substring(1, line.indexOf("]")) + "}{" + parseLine(line.substring(line.indexOf("]")+1), binOps, preOps, negOp) + "}";
        }
        if (f.equals("<")) {
            return "\\dia{" + line.substring(1, line.indexOf(">")) + "}{" + parseLine(line.substring(line.indexOf(">")+1), binOps, preOps, negOp) + "}";
        }
        
        
        if (negOp.equals(f)) {// if we have a negation, return 
            if (line.charAt(1) == '(') {
                return "\\neg{" + parseLine(line.substring(1, line.length()), binOps, preOps, negOp) + "}";
            } else {
                return "\\neg{" + parseLine(line.substring(1, line.length()), binOps, preOps, negOp) + "}";
            }
        } else if (preOps.containsKey(f)) { // if we have a prefix op
            if (line.charAt(1) == '(') { // if the first arg has brackets
                String firstArg = "";
                String secondArg = "";
                int bracketCount = 1;
                int i = 2;
                while (i < line.length() && bracketCount != 0) {
                    char c = line.charAt(i);
                    if (c == '(') {
                        bracketCount ++;
                    } else if (c == ')') {
                        bracketCount --;
                    } 
                    if (bracketCount > 0) {
                        firstArg = firstArg + c;
                    }
                    i++;
                }
                return "\\" + preOps.get(f) + "{" + parseLine(firstArg, binOps, preOps, negOp) + "}{" + parseLine(line.substring(i, line.length()), binOps, preOps, negOp) + "}";
            } else {
                int argIndex = 2; // the first index of the main part of the quantifier (not the x)
                if (argIndex == -1) {
                    argIndex = line.length()-1;
                }
                if (line.substring(1).indexOf("\u2200") > -1 && line.substring(1).indexOf("\u2200") < argIndex-1) {
                    argIndex = line.substring(1).indexOf("\u2200")+1;
                }
                if (line.substring(1).indexOf("\u2203") > -1 && line.substring(1).indexOf("\u2203") < argIndex-1) {
                    argIndex = line.substring(1).indexOf("\u2203")+1;
                }
                if (line.substring(1).indexOf(" ") > -1 && line.substring(1).indexOf(" ") < argIndex-1) {
                    argIndex = line.substring(1).indexOf(" ")+1;
                }
//                System.out.println(line + " " + argIndex);
                return "\\" + preOps.get(f) + "{" + parseLine(line.substring(1, argIndex), binOps, preOps, negOp) + "}{" + parseLine(line.substring(argIndex, line.length()), binOps, preOps, negOp) + "}";
            }
        } else if (lineContainsKey(line, binOps)) { // If we have a binary op in the line then we're still on binops
            if (f.equals("(")) {
                String firstArg = "";
                String secondArg = "";
                int bracketCount = 0;
                int i = 1;
                boolean foundOp = false;
                while (i < line.length() && !foundOp) {
                    char c = line.charAt(i);
                    if (c == '(') {
                        firstArg = firstArg + c;
                        bracketCount ++;
                    } else if (c == ')') {
                        bracketCount --;
                        firstArg = firstArg + c;
                    } else {
                        if (bracketCount == 0 && binOps.containsKey("" + c)) {
                            foundOp = true;
                        } else {
                            firstArg = firstArg + c;
                        }
                    }
                    i++;
                }
                i--;
                f = "" + line.charAt(i);
                bracketCount = 1;
//                System.out.println(line.length() + " " + i);
                return "\\" + binOps.get(f) + "{" + parseLine(firstArg, binOps, preOps, negOp) + "}{" + parseLine(line.substring(i+1, line.length()-1), binOps, preOps, negOp) + "}";
            } else {
                System.out.println(line);
                System.out.println(f);
                System.out.println("err");
                return "ERROR: MISSING BRACKETS;";
            }
            
        }
        return line;
    }
    
    public static boolean isInteger(String str) {
        // Taken from http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
        // Jonas Klemming's comment
	if (str == null) {
		return false;
	}
	int length = str.length();
	if (length == 0) {
		return false;
	}
	int i = 0;
	if (str.charAt(0) == '-') {
		if (length == 1) {
			return false;
		}
		i = 1;
	}
	for (; i < length; i++) {
		char c = str.charAt(i);
		if (c <= '/' || c >= ':') {
			return false;
		}
	}
	return true;
}
    
    private static boolean lineContainsKey(String line, HashMap<String, String> hashy) {
        for (int i = 0; i < line.length(); i++) {
            if (hashy.containsKey("" + line.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() instanceof JSlider) {
            
        }
    }
    
}
