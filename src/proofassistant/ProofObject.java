/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proofassistant;

import proofassistant.line.NDLine;
import proofassistant.line.NDJust;
import proofassistant.justification.JustInduction;
import proofassistant.justification.JustBoxElim;
import proofassistant.justification.JustNone;
import proofassistant.justification.JustQeElim;
import proofassistant.justification.JustDiaElim;
import proofassistant.justification.JustDiaIntro;
import proofassistant.justification.JustBoxIntro;
import proofassistant.justification.JustSingle;
import proofassistant.justification.JustDisElim;
import proofassistant.justification.JustDouble;
import proofassistant.justification.JustEquIntro;
import proofassistant.exception.LineNotInProofArrayException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import proofassistant.exception.MissingArityException;
import proofassistant.exception.WrongLineTypeException;
import proofassistant.line.NDAtom;
import proofassistant.util.SymbolHandler;

/**
 * The ProofObject class holds a proofArray, and provides methods for modifying it
 *
 * @since Proof Assistant 0.1
 * @version 2.0
 * @author Declan Thompson
 */
public class ProofObject {

    private NDLine[] proofArray;
    private ProofStage currentProof;
    private boolean magicMode = false;
    private boolean usingExtraLines = false;
    
    private SymbolHandler symbols = new SymbolHandler();

    /**
     * Create a new ProofMethods object with a supplied sequent
     * @param args The sequent to start with, in TeX code format
     */
    public ProofObject(String[] args) {
        try {
            proofArray = readInArgs(args);
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(ProofObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        currentProof = new ProofStage(proofArray);
//        setArities();
    }
    
    /**
     * Create a new ProofMethods object with a supplied initial proofArray
     * @param newArray A proofArray to start with
     */
    public ProofObject(NDLine[] newArray) {
        proofArray = newArray;
        currentProof = new ProofStage(proofArray);
    }

    /**
     * Returns this instance's proofArray
     * @return NDLine[]
     */
    public NDLine[] getProofArray() {
        return proofArray;
    }

    /**
     * Activates the setArities() method in Globals. This reads the current arity list and sets the global arities accordingly.
     */
    public void setArities() {
        Globals.setArities();
    }
    
    /**
     * Replaces the current proofArray
     * @param array the new proofArray
     */
    public void setProofArray(NDLine[] array) {
        proofArray = array;
    }
        
    private NDLine[] readInArgs(String[] args) 
            throws IndexOutOfBoundsException { // Returns an NDLine array containing the premises and conclusion (with blank between), taken from the args array
        NDLine[] argsArray;
//        System.out.println("Read in args");
        Globals.createExtraLines();
//        System.out.println(Globals.extraLines.length);
        argsArray = new NDLine[args.length + Globals.extraLines.length];
//        System.out.println(argsArray.length);
        for (int i = 0; i < Globals.extraLines.length; i++) {
            argsArray[i] = Globals.extraLines[i];
            Globals.terms.processLine(Globals.extraLines[i].getLine());
        }
        int k = Globals.extraLines.length;
        for (int i = 0; i < args.length - 2; i++) {
            if (args[i].contains(":")){
                if (args[i].charAt(args[i].length() - 1) == ',') {
                    argsArray[k] = new NDLine(args[i].substring(args[i].indexOf(":") + 1,args[i].length() - 1), 4);
                    argsArray[k].setContext(args[i].substring(0, args[i].indexOf(":")));
                } else {
                    argsArray[k] = new NDLine(args[i].substring(args[i].indexOf(":")+1), 4);
                    argsArray[k].setContext(args[i].substring(0, args[i].indexOf(":")));
                }
            } else {
                if (args[i].charAt(args[i].length() - 1) == ',') {
                    argsArray[k] = new NDLine(args[i].substring(0,args[i].length() - 1), 4);
                } else {
                    argsArray[k] = new NDLine(args[i], 4);
                }
            }
            k++;
        }
        argsArray[args.length - 2 + Globals.extraLines.length] = new NDLine(5);
        if (args[args.length - 1].contains(":")) {

            argsArray[args.length - 1 + Globals.extraLines.length] = new NDLine(args[args.length - 1].substring(args[args.length-1].indexOf(":")+1));
            argsArray[args.length - 1 + Globals.extraLines.length].setContext(args[args.length-1].substring(0, args[args.length-1].indexOf(":")));
        } else {
            argsArray[args.length - 1 + Globals.extraLines.length] = new NDLine(args[args.length - 1]);
        }
//            System.out.println("proofmethods says " + Globals.terms.getListOfUsedTerms().contains("s"));
        
        for (int i = Globals.extraLines.length; i < argsArray.length; i++) {
            if (argsArray[i].isContextless()) {
                argsArray[i].setContext(Globals.getDefaultContext());
            }
        }
        return argsArray;
    }

    public void printProofArray() { // Parses and prints the proofArray
        System.out.println("");
        int scopes = 0;
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getType() == 1 || proofArray[i].getType() == 3) {
                scopes++;
            }
            for (int j = 0; j < scopes; j++) {
                System.out.print("|");
            }
            if (proofArray[i].getType() == 2 || proofArray[i].getType() == 3) {
                scopes--;
            }
            if (proofArray[i].getType() == 5) {
                System.out.println("");
            } else {
                System.out.println(proofArray[i].getJustLineNum() + ".  " + proofArray[i].parseLine() + "    " + proofArray[i].getJustification().getJava());
            }
        }
        System.out.println("");
        System.out.println("--------------------");
    }
    
    public void printProofArrayLines() {
        System.out.println("Code");
        for (int i = 0; i < proofArray.length; i++) {
            System.out.println(proofArray[i].getLine());
        }
    }

    private void printTeXCode() {
        System.out.println("\\begin{NDProof}");
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getType() == 1) {
                System.out.println("\\NDAssStart{" + proofArray[i].getJustLineNum() + ".}{" + proofArray[i].getLine() + "}");
            } else if (proofArray[i].getType() == 2) {
                System.out.println("\\NDAssEnd{" + proofArray[i].getJustLineNum() + ".}{" + proofArray[i].getLine() + "}{" + proofArray[i].getJustification().getTeX() + "}");
            } else if (proofArray[i].getType() == 3) {
                System.out.println("\\NDOneLineAss{" + proofArray[i].getJustLineNum() + ".}{" + proofArray[i].getLine() + "}");
            } else if (proofArray[i].getType() == 5) {
                System.out.println("\\NDLine{}{}{}");
            } else {
                System.out.println("\\NDLine{" + proofArray[i].getJustLineNum() + ".}{" + proofArray[i].getLine() + "}{" + proofArray[i].getJustification().getTeX() + "}");
            }
        }
        System.out.println("\\end{NDProof}");
    }
    
    
    /**
     * Clone this ProofObject
     * @return 
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public ProofObject clone() throws CloneNotSupportedException {
        NDLine[] cloned = new NDLine[proofArray.length];
        
        return new ProofObject(cloned);
    }

    /**
     * Returns a string containing the TeX code for displaying the current proofArray.
     * @return String
     */
    public String getTeXCodeString() {
        // Find the longest line
        int longestLine = 0;
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getLength() > longestLine) {
                longestLine = proofArray[i].getLength();
            }
        }
        String theCode = "";
        theCode = theCode + "\\begin{NDProof}[jdistance = "+ (int)(0.8*longestLine) + "em + 1]\n";
        for (int i = 0; i < proofArray.length; i++) {
            switch(proofArray[i].getType()) {
                case NDLine.ASS_START : 
                    theCode += "\\NDAssStart{" + proofArray[i].getLineNumOutput(i) + ".}{$" + proofArray[i].getTeXLine() + "$}\n";
                    break;
                case NDLine.ASS_END :   
                    theCode += "\\NDAssEnd{" + proofArray[i].getLineNumOutput(i) + ".}{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                    break;
                case NDLine.ASS_ONE_LINE :  
                    theCode += "\\NDOneLineAss{" + proofArray[i].getLineNumOutput(i) + ".}{$" + proofArray[i].getTeXLine() + "$}\n";
                    break;
                case NDLine.BLANK :         
                    theCode += "\\NDLine{}{}{}\n";
                    break;
                case NDLine.ID_BOX_START :  
                    theCode += "\\IBoxStart{" + proofArray[i].getLineNumOutput(i) + ".}{$" + proofArray[i].getTeXLine() + "$}\n";
                    break;
                case NDLine.ID_BOX_LINE :
                    theCode += "\\IBoxLine{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                    break;
                case NDLine.ID_BOX_END :
                    theCode += "\\IBoxEnd{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                    break;
                default :
                    if (proofArray[i].getLineNum() < 0) {
                        if (!proofArray[i].isSpecial()) {
                            theCode = theCode + "\\NDLine{}{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                        } else {
                            theCode = theCode + "\\NDLine{" +proofArray[i].getLineNumOutput(i) + "}{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                        }
                    } else {
                        theCode = theCode + "\\NDLine{" + proofArray[i].getLineNumOutput(i) + ".}{$" + proofArray[i].getTeXLine() + "$}{" + proofArray[i].getJustification().getTeX() + "}\n";
                    }
            }
        }
        theCode = theCode + "\\end{NDProof}\n";
        theCode = theCode.replace("\u25c7", "\\diamond");
        return theCode;
    }
    
    /**
     * Returns a string containing the plain text for displaying the current proofArray.
     * @return String
     */
    public String getPlainTextString() {
        // Find the longest line
        int longestLine = 0;
        int deepestAss = 0;
        int assCounter = 0;
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getLength() > longestLine) {
                longestLine = proofArray[i].getLength();
            }
            if (proofArray[i].getType() == 1 || proofArray[i].getType() == 3) {
                assCounter++;
            }
            if (assCounter > deepestAss) {
                deepestAss = assCounter;
            }
            if (proofArray[i].getType() == 2 || proofArray[i].getType() == 3) {
                assCounter--;
            }
        }
        String theCode = "";
        int numAssumptions = 0;
        boolean inId = false;
        for (int i = 0; i < proofArray.length; i++) {
            // Prepare for line input
            
            int lineLength = proofArray[i].getLength();
            // Do Id box
            if (proofArray[i].getType() == 7) {
                for (int j = 0; j < numAssumptions; j++) {
                    theCode = theCode + "\u2502";
                }
                if (proofArray.length > 9) {
                    theCode = theCode + " ";
                    theCode = theCode + "  \u250c";
                    for (int j = 0; j < (longestLine); j++) {
                        theCode = theCode + "\u2500";
                    }
                } else {
                    theCode = theCode + "  \u250c";
                    for (int j = 0; j < (longestLine); j++) {
                        theCode = theCode + "\u2500";
                    }
                }
                theCode = theCode + "\n";
                inId = true;
            }
            
            
            // Write line
            for (int j = 0; j < numAssumptions; j++) {
                theCode = theCode + "\u2502";
            }
            if (proofArray[i].getType() == 1 || proofArray[i].getType() == 3) {
                numAssumptions ++;
                theCode = theCode + "\u250c";
                for (int j = 0; j < (deepestAss - numAssumptions); j++) {
                    theCode = theCode + "\u2500";
                }
            } else {
                for (int j = 0; j < (deepestAss - numAssumptions); j++) {
                    theCode = theCode + " ";
                }
            }
            
                
            if (proofArray[i].getType() != 5) {
                if (proofArray[i].getLineNum() < 0) {
                    if (!proofArray[i].isSpecial()) {
                        theCode = theCode + "  ";
                    } else {
                        theCode = theCode + proofArray[i].getSpecialNum() + " ";
                    }
                } else {
                    theCode = theCode + proofArray[i].getJustLineNum(i) + ".";
                }
                if (proofArray.length > 9 && proofArray[i].getLineNum() < 10) {
                    theCode = theCode + " ";
                }
                if (inId) {
                    theCode = theCode + "\u2502";
                } else {
                    theCode = theCode + " ";
                }
                theCode = theCode + proofArray[i].parseLine();
                for (int j = 0; j < (longestLine - lineLength + 1); j++) {
                    theCode = theCode + " ";
                }
                theCode = theCode + proofArray[i].getJustification().getJava();
            } else if (inId) {
                theCode = theCode + "  \u2502";
            }
            theCode = theCode + "\n";
            
            
            // Finish up line input
            if (proofArray[i].getType() == 2 || proofArray[i].getType() == 3) {
                numAssumptions --;
                for (int j = 0; j < numAssumptions; j++) {
                    theCode = theCode + "\u2502";
                }
                theCode = theCode + "\u2514";
                if (proofArray.length > 9) {
                    theCode = theCode + "\u2500";
                    for (int j = 0; j <= (longestLine + deepestAss - numAssumptions); j++) {
                        theCode = theCode + "\u2500";
                    }
                } else {
                    theCode = theCode + "\u2500";
                    for (int j = 0; j <= (longestLine + deepestAss - numAssumptions); j++) {
                        theCode = theCode + "\u2500";
                    }
                }
                
                theCode = theCode + "\n";
            } else if (proofArray[i].getType() == 9) {
                inId = false;
                for (int j = 0; j < numAssumptions; j++) {
                    theCode = theCode + "\u2502";
                }
                if (proofArray.length > 9) {
                    theCode = theCode + " ";
                    theCode = theCode + "  \u2514";
                    for (int j = 0; j < (longestLine); j++) {
                        theCode = theCode + "\u2500";
                    }
                } else {
                    theCode = theCode + "  \u2514";
                    for (int j = 0; j < (longestLine); j++) {
                        theCode = theCode + "\u2500";
                    }
                }
                theCode = theCode + "\n";
            }
        }
        return theCode;
    }

    /**
     * Checks if the current proofArray has no unjustified lines (i.e. if the proof is finished).
     * @return true, if there are no unjustified lines and 
     * 
     *  false otherwise
     */
    public boolean checkFinished() { // Returns true if every nonblank line in proofArray has a justification
        boolean finished = true;
        for (int i = 0; i < proofArray.length && finished; i++) {
            if (proofArray[i].getType() != 5 && proofArray[i].getJustification().getBlank()) {
                finished = false;
            }
        }
        return finished;
    }
    
    private void collapseBlanks() {
        int numUnneededBlanks = 0;
        
        for (int i = 0; i < proofArray.length - 1; i++) {
            if (proofArray[i].getType() == 5 && proofArray[i+1].hasJustification()) {
                numUnneededBlanks ++;
            }
        }
        
        NDLine[] temp = new NDLine[proofArray.length - numUnneededBlanks];
        
        int k = 0;
        for (int i = 0; i < proofArray.length && k < temp.length; i++) {
            if (proofArray[i].getType() == 5 && proofArray[i+1].hasJustification()) {
                
            } else {
                temp[k] = proofArray[i];
                k++;
            }
        }
        
        if (checkFinished()) {
            Globals.currentGoalIndex = -1;
            Globals.currentResourceIndex = -1;
        }
        
        proofArray = temp;
    }

    private NDLine checkForNDLine(String regEx, NDLine goal, String context) { // Returns the the first line in scope with contents matching lineContents. Returns -1 if none found
        int indexOfGoal = goal.indexIn(proofArray);
        NDLine lineOfWant = null;
        int scopes = 0;
        
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)
                    && proofArray[indexOfGoal-i].getIsAllowedInContext(context)) { // Check line against what we want, but ignore if scopes>0
                lineOfWant = proofArray[indexOfGoal - i];
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return lineOfWant;
    }
    
    private NDLine checkForNDLine(String regEx, NDLine goal) { // Returns the the first line in scope with contents matching lineContents. Returns -1 if none found
        int indexOfGoal = goal.indexIn(proofArray);
        NDLine lineOfWant = null;
        int scopes = 0;
        
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)) { // Check line against what we want, but ignore if scopes>0
                lineOfWant = proofArray[indexOfGoal - i];
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return lineOfWant;
    }
    
    private String findRegEx(String variable, String phrase) {
        
        String regEx = phrase.replace("\\", "\\\\"); // Replace \ with \\ to make regex feel happy
        regEx = regEx.replace("{", "\\{").replace("}", "\\}"); // Replace { and } with \{ and \} to make regex feel happy
        regEx = regEx.replace("[", "\\[").replace("]", "\\]");
        regEx = regEx.replace("(", "\\(").replace(")", "\\)"); // Do the same with ( and ) (regex happiness)
        if (!variable.equals("")) {
//            System.out.println(regEx);
            regEx = NDLine.replace(regEx, variable, "\\\\1"); // Replace variable with the 1st back reference
            
//            System.out.println(regEx);
            if (Globals.allowedRules.get("secondOrder")) {
                regEx = regEx.replaceFirst("\\\\1", "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|[\\\\w\\\\(\\\\)\\\\[\\\\]/\\\\{\\\\}\\\\\\\\]+)"); // Replace the first back reference with a group to match 1 or more letters
            } else {
                regEx = regEx.replaceFirst("\\\\1", "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|\\\\w+)"); // Replace the first back reference with a group to match 1 or more letters
            }
        }
//        System.out.println("findRegEx " + phrase + " " + regEx);
        return regEx;
    }
    
    private int findIndexOfBlank(int indexOfGoal) { // Returns the index of the blank line before the goal. Returns 0 if none found
        int indexOfBlank = -1;

        int i = 1;
        while (indexOfBlank == -1 && i <= indexOfGoal) {
            if (proofArray[indexOfGoal - i].getType() == 5) {
                indexOfBlank = indexOfGoal - i;
            }
            i++;
        }

        if (indexOfBlank == -1) {
            indexOfBlank = 0;
        }

        return indexOfBlank;
    }
   

    // NJ Rules //

    /**
     * Applies conjunction elimination to the current proofArray.
     * @param goal the current goal.
     * @param resource the current resource.
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] conElim(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException, IndexOutOfBoundsException { 
        if (resource.getMainOp().equals("qa")) {
            if ((goal.matchesNonUniRegEx(resource, 1)
                    || goal.matchesNonUniRegEx(resource, 2) )
                    && goal.hasSameContextAs(resource)) {
                currentProof.justifyLine(goal, new JustSingle(JustSingle.CON_ELIM, resource));
            }
            proofArray = currentProof.getProofArray();
            Globals.rulesUsed.add("conElim");
            return proofArray;
        }
        
        
        // Checks if the goal is a conjunct of the resource. If so, justifies the goal
        if ((goal.equalsArgOf(resource, 1) || goal.equalsArgOf(resource, 2))
                && goal.hasSameContextAs(resource)) { // If the goal is one of the conjuncts and they have the same context
            currentProof.justifyLine(goal, new JustSingle(JustSingle.CON_ELIM, resource));
            proofArray = currentProof.getProofArray();
            Globals.currentGoalIndex = -1;
            
            
        } else { // So goal is not a conjunct of resource. We will ask which (or both) of two new resources to append before the blank space before the goal or after the current resource
            magicMode = false; // turn off magic mode
            
            // Ask the user what to create - first argument, second argument or both
            Object[] options = {resource.parseFirstArg(), resource.parseSecondArg(), "Both", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to create " + options[0] + ", " + options[1] + " or both?",
                    "Conjunction Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

            // Expand proofArray to handle two new resources (the conjuncts of the resource), and justify them by the resource
            if (n == 0 || n == 2) {
                // Expand proofArray to handle one new resource (the first arg of the resource), and justify it by the resource
                NDLine firstArg = new NDLine(resource.getArg(1));
                firstArg.setSameContextAs(resource);
                firstArg.setJustification(new JustSingle(JustSingle.CON_ELIM, resource));
                
                currentProof.addNDLineResource(firstArg, goal);

                proofArray = currentProof.getProofArray();
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            } 
            if (n == 1 || n == 2) {
                NDLine secondArg = new NDLine(resource.getArg(2));
                secondArg.setSameContextAs(resource);
                secondArg.setJustification(new JustSingle(JustSingle.CON_ELIM, resource));
                
                currentProof.addNDLineResource(secondArg, goal);
                
                proofArray = currentProof.getProofArray();
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            } 
            if (n != 0 && n != 1 && n != 2) {
                Globals.reverseUndo = true;
            }
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("conElim");
        return proofArray;

    }

    /**
     * Applies conjunction introduction to the current proofArray
     * @param currentGoal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] conIntro(NDLine currentGoal, NDLine resource) throws LineNotInProofArrayException { // Applies conjunction introduction to the supplied goal and resource, in proofArray
        NDLine firstGoal = currentProof.checkForNDLine(findRegEx("", currentGoal.getArg(1)), currentGoal);
        NDLine secondGoal = currentProof.checkForNDLine(findRegEx("", currentGoal.getArg(2)), currentGoal);
        
        Globals.currentGoalIndex = -1;
        
        if (firstGoal == null && secondGoal == null) {
            // If we need to create 2 new goals
            firstGoal = new NDLine(currentGoal.getArg(1));
            firstGoal.setSameContextAs(currentGoal);
            secondGoal = new NDLine(currentGoal.getArg(2));
            secondGoal.setSameContextAs(currentGoal);
            
            // Add the two goals, in the relevant order
            if (!Globals.reverse2PremIntro) {
                currentProof.addNDLineGoal(firstGoal, currentGoal);
                currentProof.addNDLineGoal(secondGoal, currentGoal);
            } else {
                
                currentProof.addNDLineGoal(secondGoal, currentGoal);
                currentProof.addNDLineGoal(firstGoal, currentGoal);
            }            
        } else if (firstGoal != null) {
            // We have to add the second goal
            secondGoal = new NDLine(currentGoal.getArg(2));
            secondGoal.setSameContextAs(currentGoal);
            currentProof.addNDLineGoal(secondGoal, currentGoal);
        } else if (secondGoal != null) {
            // We have to add the first goal
            firstGoal = new NDLine(currentGoal.getArg(1));
            firstGoal.setSameContextAs(currentGoal);
            currentProof.addNDLineGoal(firstGoal, currentGoal);
        }

        // Justifies the goal with the found or created conjunct lines
        currentProof.justifyLine(currentGoal, 
                new JustDouble(JustDouble.CON_INTRO, firstGoal, secondGoal));
        
        proofArray = currentProof.getProofArray();
        
        Globals.rulesUsed.add("conIntro");
        return proofArray;
    }

    /**
     * Applies disjunction elimination to the current proofArray
     * @param goal the current goal.
     * @param resource the current resource.
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] disElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        
        NDLine assLineOne;
        NDLine assEndOne;
        NDLine assLineTwo;
        NDLine assEndTwo;
        
        if (goal.equalsArgOf(resource, 1) && goal.hasSameContextAs(resource)) {
            assLineOne = new NDLine(resource.getArg(1), NDLine.ASS_ONE_LINE);
            assLineOne.setSameContextAs(resource);
            assEndOne = assLineOne;
            
            currentProof.addNDLineResource(assLineOne, goal);
        } else {
            assLineOne = new NDLine(resource.getArg(1), NDLine.ASS_START);
            assLineOne.setSameContextAs(resource);
            assEndOne = new NDLine(goal.getLine(), NDLine.ASS_END);
            assEndOne.setSameContextAs(goal);
            
            currentProof.addNDLineResource(assLineOne, goal);
            currentProof.addNDLineGoal(assEndOne, goal);
        }
        
        if (goal.equalsArgOf(resource, 2) && goal.hasSameContextAs(resource)) {
            assLineTwo = new NDLine(resource.getArg(2), NDLine.ASS_ONE_LINE);
            assLineTwo.setSameContextAs(resource);
            assEndTwo = assLineTwo;
            
            currentProof.addNDLineResource(assLineTwo, goal);
        } else {
            assLineTwo = new NDLine(resource.getArg(2), NDLine.ASS_START);
            assLineTwo.setSameContextAs(resource);
            assEndTwo = new NDLine(goal.getLine(), NDLine.ASS_END);
            assEndTwo.setSameContextAs(goal);
            
            currentProof.addNDLineResource(assLineTwo, goal);
            currentProof.addNDLineGoal(assEndTwo, goal);
        }
        
        currentProof.justifyLine(goal, 
                new JustDisElim(resource, assLineOne, assEndOne, assLineTwo, assEndTwo));
        
        proofArray = currentProof.getProofArray();

        Globals.currentResourceIndex = -1;
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("disElim");
        return proofArray;
    }

    /**
     * Applies disjunction introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] disIntro(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException {
        // First, check to see if either disjunct appears in scope
        NDLine firstJustLine = currentProof.checkForNDLine(findRegEx("", goal.getArg(1)), goal);
        NDLine secondJustLine = currentProof.checkForNDLine(findRegEx("", goal.getArg(2)), goal);
        
        
        if (!Globals.requireDisSelect && firstJustLine != null) { // If the first disjunct is found
            currentProof.justifyLine(goal, 
                    new JustSingle(JustSingle.DIS_INTRO, firstJustLine));
            Globals.currentGoalIndex = -1;
        } else if (!Globals.requireDisSelect && secondJustLine != null) {
            currentProof.justifyLine(goal, 
                    new JustSingle(JustSingle.DIS_INTRO, secondJustLine));
            Globals.currentGoalIndex = -1;
        } else if ((goal.equalsArgOf(resource, 1) || goal.equalsArgOf(resource, 2)) 
                && currentProof.scopesAllowAccess(goal, resource)
                && goal.hasSameContextAs(resource)) { // If the goal is one of the disjuncts
            currentProof.justifyLine(goal, 
                    new JustSingle(JustSingle.DIS_INTRO, resource));
            Globals.currentGoalIndex = -1;
        }  else {
            magicMode = false; // turn off magic mode
            boolean chooseFirstDisjunct;

            // Get user input. Ask whether they wish to choose goal.getArg(1) or goal.getArg(2)
            Object[] options = {goal.parseFirstArg(), goal.parseSecondArg(), "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to justify from " + options[0] + " or " + options[1] + "?",
                    "Disjunction Introduction", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            switch (n) {
                case 0:
                    chooseFirstDisjunct = true;
                    break;
                case 1:
                    chooseFirstDisjunct = false;
                    break;
                default:
                    Globals.reverseUndo = true;
                    return proofArray;
            }
            
            NDLine newGoal = new NDLine(goal.getArg(chooseFirstDisjunct ? 1 : 2));
            newGoal.setSameContextAs(goal);
            
            currentProof.addNDLineGoal(newGoal, goal);
            currentProof.justifyLine(goal, 
                    new JustSingle(JustSingle.DIS_INTRO, resource));
            
            Globals.currentGoalIndex = -1;
        }
        
        proofArray = currentProof.getProofArray();

        Globals.rulesUsed.add("disIntro");
        return proofArray;
    }

    /**
     * Applies implication elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] impElim(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException, WrongLineTypeException {
        if (resource.getMainOp().equals("qa")) {
            proofArray = universalsImpElim(goal, resource);
            Globals.rulesUsed.add("impElim");
            return proofArray;
        }
        
        NDLine antecedent = currentProof.checkForNDLine(findRegEx("", resource.getArg(1)), goal, resource.getContext());
        
        if (antecedent != null) { // if antecedent is found
            if (goal.equalsArgOf(resource, 2) && goal.hasSameContextAs(resource)) {
                // If the goal is the consequent, justify it and we're done
                currentProof.justifyLine(goal, 
                        new JustDouble(JustDouble.IMP_ELIM, resource, antecedent));
                
                Globals.currentGoalIndex = -1;                
            } else { // otherwise, the goal isn't the consequent.
                // Add the consequent as a new resource.
                NDLine consequent = new NDLine(resource.getArg(2));
                consequent.setSameContextAs(resource);
                consequent.setJustification(new JustDouble(JustDouble.IMP_ELIM, resource, antecedent));
                currentProof.addNDLineResource(consequent, goal);
            }
        } else { // antecedent was not found
            if (goal.equalsArgOf(resource, 2) && goal.hasSameContextAs(resource)) {
                // If the goal is the consequent, add the antecedent as a new goal
                antecedent = new NDLine(resource.getArg(1));
                antecedent.setSameContextAs(goal); // NB: goal.hasSameContextAs(resource)
                currentProof.addNDLineGoal(antecedent, goal);
                currentProof.justifyLine(goal,
                        new JustDouble(JustDouble.IMP_ELIM, resource, antecedent));
            } else { // antecedent not found and goal isn't the consequent
                // Add a leap-of-faith: antecedent as a new goal above the current goal,
                // and consequent as a resource directly below it.
                if (magicMode) {
                    magicMode = false; // turn off magic mode
                    return proofArray;
                }   
                antecedent = new NDLine(resource.getArg(1));
                antecedent.setSameContextAs(resource);
                currentProof.addNDLineGoal(antecedent, goal);
                
                NDLine consequent = new NDLine(resource.getArg(2));
                consequent.setSameContextAs(resource);
                consequent.setJustification(new JustDouble(JustDouble.IMP_ELIM, resource, antecedent));
                currentProof.addNDLineResource(consequent, goal);
            }
        }
        
        proofArray = currentProof.getProofArray();
        
        Globals.rulesUsed.add("impElim");
        Globals.currentGoalIndex = 1;
        return proofArray;
    }

    /**
     * Applies implication introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] impIntro(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException {
        NDLine assStart;
        NDLine assEnd;
        if (goal.getArg(1).equals(goal.getArg(2))) { 
            // If the implication is trivial, create a one line assumption
            assStart = new NDLine(resource.getArg(1), NDLine.ASS_ONE_LINE);
            assStart.setSameContextAs(goal);
            assEnd = assStart;
            currentProof.addNDLineResource(assStart, goal);
        } else {
            // Otherwise, create a standard assumption scope
            assStart = new NDLine(resource.getArg(1), NDLine.ASS_START);
            assStart.setSameContextAs(goal);
            currentProof.addNDLineResource(assStart, goal);
            
            assEnd = new NDLine(goal.getArg(2), NDLine.ASS_END);
            assEnd.setSameContextAs(goal);
            currentProof.addNDLineGoal(assEnd, goal);
        }
        currentProof.justifyLine(goal,
                new JustDouble(JustDouble.IMP_INTRO, assStart, assEnd));

        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("impIntro");
        return proofArray;
    }

    /**
     * Applies equivalence elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] equElim(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException, WrongLineTypeException {
        if (resource.getMainOp().equals("qa")) {
            proofArray = universalsEquElim(goal, resource);
            Globals.rulesUsed.add("equElim");
            return proofArray;
        }
        
        NDLine leftSideLine = currentProof.checkForNDLine(findRegEx("", resource.getArg(1)), goal, resource.getContext());
        NDLine rightSideLine = currentProof.checkForNDLine(findRegEx("", resource.getArg(2)), goal, resource.getContext());
//        System.out.println(findRegEx("", resource.getArg(1)));
//        System.out.println(leftSideLineNum);


        if (leftSideLine != null && goal.equalsArgOf(resource, 2)
                && goal.hasSameContextAs(resource)) { 
            // If the left side is in the resources and the right side = the goal
            currentProof.justifyLine(goal,
                    new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
            
        } else if (rightSideLine != null && goal.equalsArgOf(resource, 1)
                && goal.hasSameContextAs(resource)) { 
            // If the right side is in the resources and the left side = the goal
            currentProof.justifyLine(goal,
                    new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
            
        } else if (leftSideLine != null) { 
            // If the left side is in the resources but the right side is not the goal
            // Add the right side as a new resource
            rightSideLine = new NDLine(resource.getArg(2));
            rightSideLine.setSameContextAs(resource);
            rightSideLine.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
            
            currentProof.addNDLineResource(rightSideLine, goal);
            
        } else if (rightSideLine != null) { 
            // If the right side is in the resources but the left side is not the goal
            // Add the left side as a new resource
            leftSideLine = new NDLine(resource.getArg(1));
            leftSideLine.setSameContextAs(resource);
            leftSideLine.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
            
            currentProof.addNDLineResource(leftSideLine, goal);
            
        } else if (leftSideLine == null && goal.equalsArgOf(resource, 2)
                && goal.hasSameContextAs(resource)) { 
            // If the left side is not found in the resources but the right side IS the goal
            // Add the left side as a new goal
            leftSideLine = new NDLine(resource.getArg(1));
            leftSideLine.setSameContextAs(goal); // NB: goal.hasSameContextAs(resource)
            
            currentProof.addNDLineGoal(leftSideLine, goal);
            currentProof.justifyLine(goal,
                    new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
        } else if (rightSideLine == null && goal.equalsArgOf(resource, 1)
                && goal.hasSameContextAs(resource)) { 
            // If the right side is not found in the resources but the left side IS the goal
            // Add the right side as a new goal
            rightSideLine = new NDLine(resource.getArg(2));
            rightSideLine.setSameContextAs(goal); // NB: goal.hasSameContextAs(resource)
            
            currentProof.addNDLineGoal(rightSideLine, goal);
            currentProof.justifyLine(goal,
                    new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
            
        } else { 
            // Otherwise. i.e. both:
            //      - neither the right nor left side is found in the resources 
            //      - neither the right nor left side is the goal
            magicMode = false; // turn off magic mode
            
            // We will make a leap-of-faith.
            
            // Ask the user which direction to move in            
            boolean leftToRight;
            Object[] options = {"Left-to-right", "Right-to-left", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "You are about to make a bold step. \n Would you like to move left-to-right or right-to-left?",
                    "Equivalence Elimination - Bold Step", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            switch (n) {
                case 0:
                    leftToRight = true;
                    break;
                case 1:
                    leftToRight = false;
                    break;
                default:
                    Globals.reverseUndo = true;
                    return proofArray;
            }
            
            // NB: the order of creation of leftSideLine and rightSideLine has 
            // an effect on their line numberings. As a result, those initialisations
            // can't easily be moved outside this conditional.
            if (leftToRight) {
                // Justify the right line by the left, and add a leap-of-faith:
                // Add leftLine as new floating goal, with rightLine a resource
                // directly below it.
                leftSideLine = new NDLine(resource.getArg(1));
                leftSideLine.setSameContextAs(resource);
                currentProof.addNDLineGoal(leftSideLine, goal);
                
                rightSideLine = new NDLine(resource.getArg(2));
                rightSideLine.setSameContextAs(resource);
                rightSideLine.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
                currentProof.addNDLineResource(rightSideLine, goal);
            } else {
                // Justify the left line by the right, and add a leap-of-faith.
                // As above.
                rightSideLine = new NDLine(resource.getArg(2));
                rightSideLine.setSameContextAs(resource);
                currentProof.addNDLineGoal(rightSideLine, goal);
                
                leftSideLine = new NDLine(resource.getArg(1));
                leftSideLine.setSameContextAs(resource);
                leftSideLine.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
                currentProof.addNDLineResource(leftSideLine, goal);
            }
        }
        
        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("equElim");
        return proofArray;
    }

    /**
     * Applies equivalence introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] equIntro(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        boolean createAnIdBox = false;
        
        if (Globals.allowedRules.get("equIdentityBoxes")) {
            // If we can make idBoxes, ask if we should.
            
            Object[] options = {"Create " + "\u2261" +" identity box", "Standard " + "\u2261" + "I", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "You have selected \u2261" + "I under proof system NK=.\n" + "Create an identity box or standard \u2261" + "I?",
                    "Equivalence Introduction - NK=", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            switch (n) {
                case 0:
                    createAnIdBox = true;
                    break;
                case 1:
                    createAnIdBox = false;
                    break;
                default:
                    Globals.reverseUndo = true;
                    return proofArray;
            }
        }
        
        
        if (createAnIdBox) {
            NDLine idStart = new NDLine(goal.getArg(1), NDLine.EQU_ID_BOX_START);
            idStart.setSameContextAs(goal);
            currentProof.addNDLineResource(idStart, goal);
            
            NDLine idEnd = new NDLine(goal.getArg(2), NDLine.ID_BOX_END);
            idEnd.setSameContextAs(goal);
            currentProof.addNDLineGoal(idEnd, goal);
            
            currentProof.justifyLine(goal, 
                    new JustSingle(JustSingle.ID_BOX_INTRO, idStart));
            
        } else {
            NDLine assStartOne;
            NDLine assEndOne;
            NDLine assStartTwo;
            NDLine assEndTwo;
            
            if (goal.getArg(1).equals(goal.getArg(2))) {
                // If the equivalence is trivial, create one line assumptions
                assStartOne = new NDLine(goal.getArg(1), NDLine.ASS_ONE_LINE);
                assStartOne.setSameContextAs(goal);
                assEndOne = assStartOne;
                
                assStartTwo = new NDLine(goal.getArg(2), NDLine.ASS_ONE_LINE);
                assStartTwo.setSameContextAs(goal);
                assEndTwo = assStartTwo;
            } else {
                // Create two new standard assumption scopes
                assStartOne = new NDLine(goal.getArg(1), NDLine.ASS_START);
                assStartOne.setSameContextAs(goal);
                currentProof.addNDLineResource(assStartOne, goal);
                assEndOne = new NDLine(goal.getArg(2), NDLine.ASS_END);
                assEndOne.setSameContextAs(goal);
                currentProof.addNDLineGoal(assEndOne, goal);
                
                assStartTwo = new NDLine(goal.getArg(2), NDLine.ASS_START);
                assStartTwo.setSameContextAs(goal);
                currentProof.addNDLineResource(assStartTwo, goal);
                assEndTwo = new NDLine(goal.getArg(1), NDLine.ASS_END);
                assEndTwo.setSameContextAs(goal);
                currentProof.addNDLineGoal(assEndTwo, goal);
            }
            currentProof.justifyLine(goal,
                    new JustEquIntro(assStartOne, assEndOne, assStartTwo, assEndTwo));
        }
        
        proofArray = currentProof.getProofArray();

        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("equIntro");
        return proofArray;
    }

    /**
     * Applies negation elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] negElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException { // When we have a negation selected as a resource
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("negElim");
            return universalsNegElim(goal, resource);
        }
        NDLine negandLine = currentProof.checkForNDLine(findRegEx("", resource.getArg(1)), goal, resource.getContext());
        NDLine falsumLine;
        
        if (negandLine != null) { // i.e. the negand appears above
            // We have a contradiction
            if (goal.getLine().equals("\\falsum") && goal.hasSameContextAs(resource)) {
                // If the current goal is falsum, justify it
                currentProof.justifyLine(goal,
                    new JustDouble(JustDouble.NEG_ELIM, resource, negandLine));
            } else {
                // The current goal is not falsum
                // Add falsum as a new resource
                falsumLine = new NDLine("\\falsum");
                falsumLine.setSameContextAs(resource);
                falsumLine.setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, negandLine));
                currentProof.addNDLineResource(falsumLine, goal);
                // if the goal is the same context, justify it
                if (goal.hasSameContextAs(falsumLine)) {
                    currentProof.justifyLine(goal,
                            new JustSingle(JustSingle.FALSUM_ELIM, falsumLine));
                }
            }            
        } else { // i.e. the negand is not a resource
            // We're trying to make a contradiction
            negandLine = new NDLine(resource.getArg(1));
            negandLine.setSameContextAs(goal); // NB: goal.hasSameContextAs(resource)
            currentProof.addNDLineGoal(negandLine, goal);
            if (goal.getLine().equals("\\falsum") && goal.hasSameContextAs(resource)) {
                // If the current goal is falsum, justify it
                currentProof.justifyLine(goal,
                        new JustDouble(JustDouble.NEG_ELIM, resource, negandLine));
            } else {
                // The current goal isn't falsum, so add
                falsumLine = new NDLine("\\falsum");
                falsumLine.setSameContextAs(resource);
                falsumLine.setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, negandLine));
                currentProof.addNDLineResource(falsumLine, goal);
                if (goal.hasSameContextAs(falsumLine)) {
                        currentProof.justifyLine(goal,
                            new JustDouble(JustDouble.NEG_ELIM, resource, falsumLine));
                }
            }
        }
        
        proofArray = currentProof.getProofArray();
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("negElim");
        return proofArray;
    }
    
    /**
     * Applies falsum negation elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] falsNegElim(NDLine goal, NDLine resource) { // When we have falsum selected as the goal, and a line to be negated selected as resource
        Globals.rulesUsed.add("falsNegElim");
        return proofArray;
    }

    /**
     * Applies negation introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] negIntro(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        NDLine assStart;
        NDLine assEnd;
        if (goal.getArg(1).equals("\\falsum")) {
            // If we proving a negated falsum, do a one line assumption
            assStart = new NDLine(goal.getArg(1), NDLine.ASS_ONE_LINE);
            assStart.setSameContextAs(goal);
            assEnd = assStart;
            currentProof.addNDLineResource(assStart, goal);
        } else {
            assStart = new NDLine(goal.getArg(1), NDLine.ASS_START);
            assStart.setSameContextAs(goal);
            currentProof.addNDLineResource(assStart, goal);

            assEnd = new NDLine("\\falsum", NDLine.ASS_END);
            assEnd.setSameContextAs(goal);
            currentProof.addNDLineGoal(assEnd, goal);
        }
        
        currentProof.justifyLine(goal,
                new JustDouble(JustDouble.NEG_INTRO, assStart, assEnd));

        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("negIntro");
        return proofArray;
    }

    /**
     * Applies universal elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     * @throws proofassistant.exception.WrongLineTypeException
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] qaElim(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException, WrongLineTypeException {
        if (Globals.allowedRules.get("universalsShortcuts") 
                && goal.matchesNonUniRegEx(resource) && goal.hasSameContextAs(resource)) {
            // If the goal matches the resource, minus some quantifiers, justify
            // the goal immediately.
            currentProof.justifyLine(goal,
                    new JustSingle(JustSingle.QA_ELIM, resource));
            proofArray = currentProof.getProofArray();
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("qaElim");
            return proofArray;
        }
        
        if (goal.matchesQuantifiedNDLine(resource) && goal.hasSameContextAs(resource)) {
            // If the goal is an instance of the resource, we can justify the goal
            currentProof.justifyLine(goal,
                    new JustSingle(JustSingle.QA_ELIM, resource));
        } else {
            // The goal is not an instance of the resource. We need to ask the
            // user what term to use.
            magicMode = false; // turn off magic mode
            String term;
            if (Globals.allowedRules.get("secondOrder")) {
                term = MyOptionPane.showFriendlyLineInputDialog("Universal Elimination (Second-Order Logic)").replace("\\", "\\\\");
            } else {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "The goal does not match.\nPlease input a term", "Universal Elimination", JOptionPane.PLAIN_MESSAGE, null, null, "a");
            }
            if (term == null) {
                // If they entered nothing, or cancelled, do nothing
                Globals.reverseUndo = true;
                return proofArray;
            }
            
            NDLine instance = new NDLine(resource.getQuantifierInstance(term));
            try {
                System.out.println(resource.getQuantifierInstance(new NDAtom("(" + term + ")", symbols)));
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofObject.class.getName()).log(Level.SEVERE, null, ex);
            }
            instance.setSameContextAs(resource);
            instance.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
            currentProof.addNDLineResource(instance, goal);            
        }
        
        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("qaElim");
        return proofArray;
    }
    

    /**
     * Applies universal introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] qaIntro(NDLine goal, NDLine resource) {
        boolean allowable;
        String term;
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine[] temp = new NDLine[proofArray.length + 1];
        
        
        if (Globals.allowedRules.get("autoParameters")) {
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getArg(1).equals("p") || resource.getArg(1).equals("q") || resource.getArg(1).equals("r"))) {
                term = Globals.terms.getNewProposition();
            } else {
                term = Globals.terms.getNewTerm();
            }
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getArg(1).equals("p") || resource.getArg(1).equals("q") || resource.getArg(1).equals("r"))) {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a proposition", "Universal Introduction", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewProposition());
            } else {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a term", "Universal Introduction", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewTerm());
            }

            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            allowable = !termIsInScopeOfLine(term, goal);
            if (!allowable) {
                JOptionPane.showMessageDialog(Globals.frame, "Illegal parameter used!");
            }
            Globals.terms.processLine(term);
        }
        int k = 0;
        for (int j = 0; j < indexOfBlank + 1; j++) {
            temp[k] = proofArray[j];
            k++;
        }

        temp[k] = new NDLine(goal.replace(goal.getArg(2), goal.getArg(1), term));
        temp[k].setSameContextAs(goal);
        NDLine newLine = temp[k];
        Globals.currentGoalIndex = k;
        k++;

        for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
            temp[k] = proofArray[j];
            k++;
        }
        proofArray = temp;
        goal.setJustification(new JustSingle(JustSingle.QA_INTRO, newLine, allowable));
        collapseBlanks();
        Globals.rulesUsed.add("qaIntro");
        return proofArray;
    }

    /**
     * Applies existential elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] qeElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine assStartLine;
        NDLine assEndLine;
        NDLine[] temp = new NDLine[proofArray.length + 2];
        boolean allowable;
        String term; 
        
        if (Globals.allowedRules.get("autoParameters")) {
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getArg(1).equals("p") || resource.getArg(1).equals("q") || resource.getArg(1).equals("r"))) {
                term = Globals.terms.getNewProposition();
            } else {
                term = Globals.terms.getNewTerm();
            }
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getArg(1).equals("p") || resource.getArg(1).equals("q") || resource.getArg(1).equals("r"))) {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a proposition", "Existential Elimination", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewProposition());
            } else {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a term", "Existential Elimination", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewTerm());
            }

            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            allowable = !termIsInScopeOfLine(term, goal);
            if (!allowable) {
                JOptionPane.showMessageDialog(Globals.frame, "Illegal parameter used!");
            }
            Globals.terms.processLine(term);
        }
        int k = 0;
        
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        temp[k] = new NDLine(resource.replace(resource.getArg(2), resource.getArg(1), term), 1);
        temp[k].setSameContextAs(resource);
        assStartLine = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.getLine(), 2);
        temp[k].setSameContextAs(goal);
        assEndLine = temp[k];
        Globals.currentGoalIndex = k;
        k++;
        
        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        goal.setJustification(new JustQeElim(resource, assStartLine, assEndLine, allowable));
        proofArray = temp;
        
        collapseBlanks();
        Globals.rulesUsed.add("qeElim");
        return proofArray;
    }

    /**
     * Applies existential introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] qeIntro(NDLine goal, NDLine resource) {
        NDLine matchLine = checkForNDLine(findRegEx(goal.getArg(1), goal.getArg(2)), goal);
        
        if (matchLine != null) {
            Globals.currentGoalIndex = -1;
        } else {
            magicMode = false; // turn off magic mode
            
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 1];
            String term;
            if (Globals.allowedRules.get("secondOrder")) {
                term = MyOptionPane.showFriendlyLineInputDialog("Existential Introduction (Second-Order Logic)").replace("\\", "\\\\");
            } else {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "The goal does not match.\nPlease input a term", "Existential Introduction", JOptionPane.PLAIN_MESSAGE, null, null, "a");
            }
            
            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            
            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
//            System.out.println(term);
            temp[k] = new NDLine(goal.replace(goal.getArg(2), goal.getArg(1), term));
            temp[k].setSameContextAs(goal);
            matchLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;
            
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            proofArray = temp;
        }
        
        goal.setJustification(new JustSingle(JustSingle.QE_INTRO, matchLine));
        collapseBlanks();
        Globals.rulesUsed.add("qeIntro");
        return proofArray;
    }

    /**
     * Applies falsum elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     */
    public NDLine[] falsumElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        currentProof.justifyLine(goal,
                new JustSingle(JustSingle.FALSUM_ELIM, resource));
        proofArray = currentProof.getProofArray();
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("falsumElim");
        return proofArray;
    }
    
    // Quantifier Helpers //
    
    private boolean termIsInScopeOfLine(String term, NDLine goal) {
        int indexOfGoal = goal.indexIn(proofArray);
        TermStore temp = new TermStore();
        
        int scopes = 0;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            if (scopes == 0) { // Ignore if scopes>0
                temp.processLine(proofArray[indexOfGoal - i].getLine()); // Process the current line
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        
        for (int i = indexOfGoal; i < proofArray.length; i++) { // Move from current goal down to bottom of proof
            if (proofArray[i].getType() == 1 || proofArray[i].getType() == 3) { // If we hit a start-of-assumption line, increase scopes count
                scopes++;
            }
            if (scopes == 0) { // Ignore if scopes>0
                temp.processLine(proofArray[i].getLine()); // Process the current line
            }
            if (proofArray[i].getType() == 2 || proofArray[i].getType() == 3) { // If we hit an end-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        
        // So temp now contains all the terms in scope
        
        
        return temp.containsTerm(term);
    }
    
    private NDLine[] universalsNegElim(NDLine goal, NDLine resource) {
//        System.out.println("=============================");
//        System.out.println("Running universalsNegElim");
//        System.out.println("=============================");
        int indexOfGoal = goal.indexIn(proofArray);
//        System.out.println("indexOfGoal is " + indexOfGoal);
        
        String firstRegEx = resource.getNonQaFirstArgRegEx();
//        System.out.println("regEx: " + firstRegEx);
        
        if (goal.getLine().equals("\\falsum") && goal.hasSameContextAs(resource)) {
//            System.out.println("here");
            ArrayList<Integer> matchingLines = checkForAllMatching(firstRegEx, goal, resource);
            if (matchingLines.isEmpty()) {
                magicMode = false;
//                System.out.println("nolines");
                Globals.reverseUndo = true;
                JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
                return proofArray;
            } else { // If we've found one matching line, justify the falsum with it
                goal.setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, proofArray[matchingLines.get(0)]));
                collapseBlanks();
                return proofArray;
            }
        } else {
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            Globals.reverseUndo = true;
        }
        return proofArray;
    }
    
    private NDLine[] universalsImpElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException, WrongLineTypeException {
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonQaFirstArgRegEx();
        String secondRegEx = resource.getNonQaSecondArgRegEx();

        // See if the antecedent is matched in the resources
        // Prepare for multiple matches
        ArrayList<Integer> matchingIndexes = checkForAllMatching(firstRegEx, goal, resource);
        ArrayList<NDLine> possibleLines = new ArrayList<>();
        int antecedentIndex = -1;
//        if (matchingIndexes.isEmpty()) {
//            return proofArray;
//        }

        // Create a pattern for matching
        Pattern firstPattern = Pattern.compile(firstRegEx);
        Pattern secondPattern = Pattern.compile(secondRegEx);
        Pattern wholePattern = Pattern.compile(resource.getNonQaRegEx());
        
        
        for (int k = 0; k < matchingIndexes.size(); k++) { // Deal with any matching resources
            antecedentIndex = matchingIndexes.get(k);
            if (antecedentIndex != -1) { // So a match for the antecedent has been found
                NDLine antecedent = proofArray[antecedentIndex];

                Matcher matcher = firstPattern.matcher(antecedent.getLine()); // Initiate a matcher for finding matching groups

                // For each matching group, add it to an arraylist
                ArrayList<String> matchedArray = new ArrayList<>();
                while (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        matchedArray.add(matcher.group(i));
                    }
                }

                tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
                tempLine.setContext(resource.getContext());
                while (!matchedArray.isEmpty()) { // while matchedArray has elements
                    NDLine test = new NDLine(5);
                    int i = 0;
                    boolean replacedVariable = false;
                    while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                        if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            
                            if (antecedent.matchesNonUniRegEx(test, 1)) { // If the firstArg of test matches
                                tempLine = test; //                                             make tempLine = test
                                replacedVariable = true; // Escape from this while loop
                                matchedArray.remove(matchedArray.get(i)); // remove this variable
                            }
                            i++; // increment i
                        }
                    }
                    if (tempLine != test) { // So none of the variable options worked
                        break;
                    }
                }
                
                possibleLines.add(tempLine);
            } else {
                Globals.reverseUndo = true;
                JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
                return proofArray;
            }
        }
        
        
        if (goal.getLine().matches(secondRegEx)) { // Deal with the goal, if it matches
            Matcher matcher = secondPattern.matcher(goal.getLine());
            ArrayList<String> matchedArray = new ArrayList<>();
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matchedArray.add(matcher.group(i));
                }
            }
            
            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the goal
            tempLine.setContext(resource.getContext());
            while (!matchedArray.isEmpty()) { // while matchedArray has elements
                NDLine test = new NDLine(5);
                int i = 0;
                boolean replacedVariable = false;
                while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                    if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.matchesNonUniRegEx(test, 2)) { // If the secondArg of test matches
                            tempLine = test; //                                             make tempLine = test
                            replacedVariable = true; // Escape from this while loop
                            matchedArray.remove(matchedArray.get(i)); // remove this variable
                        }
                        i++; // increment i
                    }
                }
                if (tempLine != test) { // So none of the variable options worked
                    break;
                }
            }
            
            
            possibleLines.add(0, tempLine);
        }
        
        for (int q = 0; q < possibleLines.size(); q++) {
            String lineAtQ = possibleLines.get(q).getLine();
            for (int r = q+1; r < possibleLines.size(); r++) {
                if (possibleLines.get(r).getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                }
            }
        }
        
        NDLine lineToUse = new NDLine(5);
        if (possibleLines.isEmpty()) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        } else if (possibleLines.size() == 1) {
            lineToUse = possibleLines.get(0);
        } else {
            magicMode = false; // turn off magic mode
            
            Object[] possibilities = new Object[possibleLines.size()];
            for (int i = 0; i < possibleLines.size(); i++) {
                possibilities[i] = possibleLines.get(i).parseLine();
            }
            String s = (String)JOptionPane.showInputDialog(
                    Globals.frame,
                    "Multiple matches found\n"
                    + "Use which possible line?",
                    "Implication Elimination",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
            for (int p = 0; p < possibilities.length; p++) {
                if (possibilities[p].equals(s)) {
                    lineToUse = possibleLines.get(p);
                }
            }
        }
        
        
        
        
        // 2. Check that the main op is now imp //
        if (!lineToUse.getMainOp().equals("imp")) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
        lineToUse.setLineNum(resource.getLineNum());
        lineToUse.setContext(resource.getContext());
        lineToUse.setSpecialLineNum(resource.getSpecialNum());
        
        // 3. Run impElim on the line
        impElim(goal, lineToUse);
        if (Globals.runMagicModeWithQa) {
            runMagicMode(2);
        }
        return proofArray;
    }
    
    private NDLine[] universalsEquElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException, WrongLineTypeException {
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonQaFirstArgRegEx();
        String secondRegEx = resource.getNonQaSecondArgRegEx();

        // See if the antecedent or consequent is matched in the resources
        // Prepare for multiple matches
        ArrayList<Integer> matchingAnteIndexes = checkForAllMatching(firstRegEx, goal, resource);
        ArrayList<Integer> matchingConseIndexes = checkForAllMatching(secondRegEx, goal, resource);
        ArrayList<NDLine> possibleLines = new ArrayList<>();
        int antecedentIndex = -1;
        int consequentIndex = -1;

        // Create a pattern for matching
        Pattern firstPattern = Pattern.compile(firstRegEx);
        Pattern secondPattern = Pattern.compile(secondRegEx);
        
        for (int k = 0; k < matchingAnteIndexes.size(); k++) { // Deal with any matching resources for antecedent
            antecedentIndex = matchingAnteIndexes.get(k);
            if (antecedentIndex != -1) { // So a match for the antecedent has been found
                NDLine antecedent = proofArray[antecedentIndex];

                Matcher matcher = firstPattern.matcher(antecedent.getLine()); // Initiate a matcher for finding matching groups

                // For each matching group, add it to an arraylist
                ArrayList<String> matchedArray = new ArrayList<>();
                while (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        matchedArray.add(matcher.group(i));
                    }
                }

                tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
                tempLine.setContext(resource.getContext());
                while (!matchedArray.isEmpty()) { // while matchedArray has elements
                    NDLine test = new NDLine(5);
                    int i = 0;
                    boolean replacedVariable = false;
                    while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                        if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            if (antecedent.matchesNonUniRegEx(test, 1)) { // If the firstArg of test matches
                                tempLine = test; //                                             make tempLine = test
                                replacedVariable = true; // Escape from this while loop
                                matchedArray.remove(matchedArray.get(i)); // remove this variable
                            }
                            i++; // increment i
                        }
                    }
                    if (tempLine != test) { // So none of the variable options worked
                        break;
                    }
                }
                
                possibleLines.add(tempLine);
            } else {
                Globals.reverseUndo = true;
                JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
                return proofArray;
            }
        }
        for (int k = 0; k < matchingConseIndexes.size(); k++) { // Deal with any matching resources for antecedent
            consequentIndex = matchingConseIndexes.get(k);
            if (consequentIndex != -1) { // So a match for the antecedent has been found
                NDLine consequent = proofArray[consequentIndex];

                Matcher matcher = secondPattern.matcher(consequent.getLine()); // Initiate a matcher for finding matching groups

                // For each matching group, add it to an arraylist
                ArrayList<String> matchedArray = new ArrayList<>();
                while (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        matchedArray.add(matcher.group(i));
                    }
                }

                tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
                tempLine.setContext(resource.getContext());
                while (!matchedArray.isEmpty()) { // while matchedArray has elements
                    NDLine test = new NDLine(5);
                    int i = 0;
                    boolean replacedVariable = false;
                    while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                        if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            if (consequent.matchesNonUniRegEx(test, 2)) { // If the firstArg of test matches
                                tempLine = test; //                                             make tempLine = test
                                replacedVariable = true; // Escape from this while loop
                                matchedArray.remove(matchedArray.get(i)); // remove this variable
                            }
                            i++; // increment i
                        }
                    }
                    if (tempLine != test) { // So none of the variable options worked
                        break;
                    }
                }
                
                possibleLines.add(tempLine);
            } else {
                Globals.reverseUndo = true;
                JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
                return proofArray;
            }
        }
        
        
        if (goal.getLine().matches(secondRegEx)) { // Deal with the goal, if it matches
            Matcher matcher = secondPattern.matcher(goal.getLine());
            ArrayList<String> matchedArray = new ArrayList<>();
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matchedArray.add(matcher.group(i));
                }
            }
            
            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the goal
            tempLine.setContext(resource.getContext());
            while (!matchedArray.isEmpty()) { // while matchedArray has elements
                NDLine test = new NDLine(5);
                int i = 0;
                boolean replacedVariable = false;
                while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                    if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.matchesNonUniRegEx(test, 2)) { // If the secondArg of test matches
                            tempLine = test; //                                             make tempLine = test
                            replacedVariable = true; // Escape from this while loop
                            matchedArray.remove(matchedArray.get(i)); // remove this variable
                        }
                        i++; // increment i
                    }
                }
                if (tempLine != test) { // So none of the variable options worked
                    break;
                }
            }
            
            
            possibleLines.add(0, tempLine);
        }
        
        if (goal.getLine().matches(firstRegEx)){
            
            Matcher matcher = firstPattern.matcher(goal.getLine());
            ArrayList<String> matchedArray = new ArrayList<>();
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matchedArray.add(matcher.group(i));
                }
            }
            
            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the goal
            tempLine.setContext(resource.getContext());
            while (!matchedArray.isEmpty()) { // while matchedArray has elements
                NDLine test = new NDLine(5);
                int i = 0;
                boolean replacedVariable = false;
                while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                    if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.matchesNonUniRegEx(test, 1)) { // If the secondArg of test matches
                            tempLine = test; //                                             make tempLine = test
                            replacedVariable = true; // Escape from this while loop
                            matchedArray.remove(matchedArray.get(i)); // remove this variable
                        }
                        i++; // increment i
                    }
                }
                if (tempLine != test) { // So none of the variable options worked
                    break;
                }
            }
            
            
            possibleLines.add(0, tempLine);
        }
        
        for (int q = 0; q < possibleLines.size(); q++) {
            String lineAtQ = possibleLines.get(q).getLine();
            String first = possibleLines.get(q).getArg(1);
            String second = possibleLines.get(q).getArg(2);
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println(lineR.getLine());
//                System.out.println(lineAtQ);
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getArg(1).equals(second) && lineR.getArg(2).equals(first)) {
                    possibleLines.remove(r);
                    r--;
                }
            }
        }
        
        NDLine lineToUse = new NDLine(5);
        if (possibleLines.isEmpty()) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        } else if (possibleLines.size() == 1) {
            lineToUse = possibleLines.get(0);
        } else {
            magicMode = false; // turn off magic mode
            
            Object[] possibilities = new Object[possibleLines.size()];
            for (int i = 0; i < possibleLines.size(); i++) {
                possibilities[i] = possibleLines.get(i).parseLine();
            }
            String s = (String)JOptionPane.showInputDialog(
                    Globals.frame,
                    "Multiple matches found\n"
                    + "Use which possible line?",
                    "Implication Elimination",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
            for (int p = 0; p < possibilities.length; p++) {
                if (possibilities[p].equals(s)) {
                    lineToUse = possibleLines.get(p);
                }
            }
        }
        
        
        
        
        // 2. Check that the main op is now imp //
        if (!lineToUse.getMainOp().equals("equ")) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
        lineToUse.setLineNum(resource.getLineNum());
        lineToUse.setContext(resource.getContext());
        lineToUse.setSpecialLineNum(resource.getSpecialNum());
        // 3. Run impElim on the line
        equElim(goal, lineToUse);
        if (Globals.runMagicModeWithQa) {
            runMagicMode(2);
        }
        return proofArray;
    }
    
    private NDLine[] universalsIdBoxEquElim(NDLine goal, NDLine resource) {
        if (!goal.hasSameContextAs(resource)) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
        
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonQaFirstArgRegEx();
        String secondRegEx = resource.getNonQaSecondArgRegEx();

        // See if the antecedent or consequent is matched in the resources
        // Prepare for multiple matches
        int indexOfGoal = goal.indexIn(proofArray);
        String otherIdBoxLine;
        if (indexOfGoal > 0 && proofArray[indexOfGoal-1].getJustification().getBlank()) { // Detect if we're at the bottom or top of an id box
//            atBottom = true;
            otherIdBoxLine = proofArray[indexOfGoal-2].getLine();
//            indexOfBlank = indexOfGoal - 1;
        } else {
//            atBottom = false;
            otherIdBoxLine = proofArray[indexOfGoal+2].getLine();
//            indexOfBlank = indexOfGoal + 1;
        }
        
        ArrayList<NDLine> possibleLines = new ArrayList<>();

        // Create a pattern for matching
        Pattern firstPattern = Pattern.compile(firstRegEx);
        Pattern secondPattern = Pattern.compile(secondRegEx);
        
        // Check otherIdBoxLine for antecedent
        Matcher matcher = firstPattern.matcher(otherIdBoxLine); // Check to see if the other id box line matches the the first argument
        
        ArrayList<String> matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matchedArray.add(matcher.group(i));
            }
        }
        
        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
        tempLine.setContext(resource.getContext());
        while (!matchedArray.isEmpty()) { // while matchedArray has elements
            NDLine test = new NDLine(5);
            int i = 0;
            boolean replacedVariable = false;
            while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                    matchedArray.remove(matchedArray.get(i)); //               remove it
                } else { // Otherwise, check if test works with any variables
                    test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
                    if (otherIdBoxLine.matches(test.getNonQaFirstArgRegEx())) { // If the firstArg of test matches
                        tempLine = test; //                                             make tempLine = test
                        replacedVariable = true; // Escape from this while loop
                        matchedArray.remove(matchedArray.get(i)); // remove this variable
                    }
                    i++; // increment i
                }
            }
            if (tempLine != test) { // So none of the variable options worked
                break;
            }
        }

        possibleLines.add(tempLine);
        
        // Check other id Box Line for consequent
        matcher = secondPattern.matcher(otherIdBoxLine); // Check to see if the other id box line matches the the first argument
        
        matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matchedArray.add(matcher.group(i));
            }
        }
        
        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
        tempLine.setContext(resource.getContext());
        while (!matchedArray.isEmpty()) { // while matchedArray has elements
            NDLine test = new NDLine(5);
            int i = 0;
            boolean replacedVariable = false;
            while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                    matchedArray.remove(matchedArray.get(i)); //               remove it
                } else { // Otherwise, check if test works with any variables
                    test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
                    if (otherIdBoxLine.matches(test.getNonQaSecondArgRegEx())) { // If the firstArg of test matches
                        tempLine = test; //                                             make tempLine = test
                        replacedVariable = true; // Escape from this while loop
                        matchedArray.remove(matchedArray.get(i)); // remove this variable
                    }
                    i++; // increment i
                }
            }
            if (tempLine != test) { // So none of the variable options worked
                break;
            }
        }

        possibleLines.add(tempLine);
        
        
        
        
        if (goal.getLine().matches(secondRegEx)) { // Deal with the goal, if it matches
            matcher = secondPattern.matcher(goal.getLine());
            matchedArray = new ArrayList<>();
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matchedArray.add(matcher.group(i));
                }
            }
            
            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the goal
            tempLine.setContext(resource.getContext());
            while (!matchedArray.isEmpty()) { // while matchedArray has elements
                NDLine test = new NDLine(5);
                int i = 0;
                boolean replacedVariable = false;
                while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                    if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.matchesNonUniRegEx(test, 2)) { // If the secondArg of test matches
                            tempLine = test; //                                             make tempLine = test
                            replacedVariable = true; // Escape from this while loop
                            matchedArray.remove(matchedArray.get(i)); // remove this variable
                        }
                        i++; // increment i
                    }
                }
                if (tempLine != test) { // So none of the variable options worked
                    break;
                }
            }
            
            
            possibleLines.add(0, tempLine);
        }
        
        if (goal.getLine().matches(firstRegEx)){
            matcher = firstPattern.matcher(goal.getLine());
            matchedArray = new ArrayList<>();
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    matchedArray.add(matcher.group(i));
                }
            }
            
            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the goal
            tempLine.setContext(resource.getContext());
            while (!matchedArray.isEmpty()) { // while matchedArray has elements
                NDLine test = new NDLine(5);
                int i = 0;
                boolean replacedVariable = false;
                while (!replacedVariable && i < matchedArray.size()) { // Loop through every possible variable
                    if (tempLine.getArg(1).contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.matchesNonUniRegEx(test, 1)) { // If the firstArg of test matches
                            tempLine = test; //                                             make tempLine = test
                            replacedVariable = true; // Escape from this while loop
                            matchedArray.remove(matchedArray.get(i)); // remove this variable
                        }
                        i++; // increment i
                    }
                }
                if (tempLine != test) { // So none of the variable options worked
                    break;
                }
            }
            
            
            possibleLines.add(0, tempLine);
        }
        
        for (int q = 0; q < possibleLines.size(); q++) {
            String lineAtQ = possibleLines.get(q).getLine();
            String first = possibleLines.get(q).getArg(1);
            String second = possibleLines.get(q).getArg(2);
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getArg(1).equals(second) && lineR.getArg(2).equals(first)) {
                    possibleLines.remove(r);
                    r--;
                }
            }
        }
        
        NDLine lineToUse = new NDLine(5);
        if (possibleLines.isEmpty()) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        } else if (possibleLines.size() == 1) {
            lineToUse = possibleLines.get(0);
        } else {
            magicMode = false; // turn off magic mode
            
            Object[] possibilities = new Object[possibleLines.size()];
            for (int i = 0; i < possibleLines.size(); i++) {
                possibilities[i] = possibleLines.get(i).parseLine();
            }
            String s = (String)JOptionPane.showInputDialog(
                    Globals.frame,
                    "Multiple matches found\n"
                    + "Use which possible line?",
                    "Implication Elimination",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
            for (int p = 0; p < possibilities.length; p++) {
                if (possibilities[p].equals(s)) {
                    lineToUse = possibleLines.get(p);
                }
            }
        }
        
        
        
        
        // 2. Check that the main op is now imp //
        if (!lineToUse.getMainOp().equals("equ")) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
        lineToUse.setLineNum(resource.getLineNum());
        lineToUse.setContext(resource.getContext());
        lineToUse.setSpecialLineNum(resource.getSpecialNum());
        // 3. Run impElim on the line
        return idBoxEquElim(goal, lineToUse);
    }
    
    private NDLine[] universalsEqElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException, WrongLineTypeException {
        if (!goal.hasSameContextAs(resource)) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
//        System.out.println("=============================");
//        System.out.println("Running universalsEqElim");
//        System.out.println("=============================");
        int indexOfGoal = goal.indexIn(proofArray);
//        System.out.println("indexOfGoal is " + indexOfGoal);
        
        String firstRegEx = resource.getNonQaFirstArgRegEx();
        String secondRegEx = resource.getNonQaSecondArgRegEx();
        ArrayList<NDLine> possibleLines = new ArrayList<>();
        NDLine tempLine;
        
//        System.out.println("firstRegEx is " + firstRegEx);
//        System.out.println("seconRegEx is " + secondRegEx);
        
        
        
        
        // Check for matches for firstRegEx in the resources
//        System.out.println(">> Check for matches for firstRegEx in resources <<");
        Pattern firstPattern = Pattern.compile(firstRegEx);
        
        ArrayList<String> listOfTerms;
        ArrayList<String> matchedArray;
        
        for (int k = indexOfGoal - 1; k > -1; k--) { // move from the current goal up
//            System.out.println("looking at " + proofArray[k].getLine());
            if (goal.isInScopeOf(proofArray[k], proofArray) && proofArray[k].getLineNum() != resource.getLineNum() && proofArray[k].getType() != 11
                    && resource.hasSameContextAs(proofArray[k])) {
                listOfTerms = getAllTermsIn(proofArray[k].getLine());
                matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
                while (!listOfTerms.isEmpty()) {
                    Matcher matcher = firstPattern.matcher(listOfTerms.get(0));
        
                        while (matcher.find()) {
                            for (int i = 1; i <= matcher.groupCount(); i++) {
                                matchedArray.add(matcher.group(i));
//                                System.out.println(matcher.group(i));
                            }
                        }
                        listOfTerms.remove(0);
                }
        //        
                tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
                tempLine.setContext(resource.getContext());
                ArrayList<NDLine> tempLinesList = new ArrayList<>();
                tempLinesList.add(tempLine);
//                System.out.println("+++++++++++++++++");
                while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
                    NDLine test = new NDLine(5);
                    int i = 0;
                    tempLine = tempLinesList.get(0);
//                    System.out.println("tempLine is " + tempLine.getLine());
//                    System.out.println("---------------------------------------");
                    while (i < matchedArray.size()) { // Loop through every possible variable
                                                      // Remove the quantifier, substituting for matchedArray.get(i)
//                        System.out.println("|| test with " + matchedArray.get(i) + " ||");
                        if (checkBracketBalance(matchedArray.get(i))) {
                            test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
    //                        System.out.println("     3. test is " + test.getLine());
                            Matcher attempt = Pattern.compile(test.getNonQaFirstArgRegEx()).matcher(proofArray[k].getLine());
                            if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
    //                            System.out.println("     found");
                                if (!test.getMainOp().equals("qa")) {
                                    possibleLines.add(test);
    //                                System.out.println("     3. Added to possibleLines: " + test.getLine());
                                } else {
                                    tempLinesList.add(test);
                                }
                            }
                        }
                        i++; // increment i
                    }
                    tempLinesList.remove(0);
                }
//        System.out.println("+++++++++++++++++");
            }
        }

//        
//        // Check for matches for secondtRegEx in resources
//        System.out.println(">> Check for matches for secondRegEx in resources <<");
        Pattern secondPattern = Pattern.compile(secondRegEx);
        
        for (int k = indexOfGoal - 1; k > -1; k--) {
            if (goal.isInScopeOf(proofArray[k], proofArray) && proofArray[k].getLineNum() != resource.getLineNum() && proofArray[k].getType() != 11
                    && goal.hasSameContextAs(resource)) {
                listOfTerms = getAllTermsIn(proofArray[k].getLine());
                matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
                while (!listOfTerms.isEmpty()) {
                    Matcher matcher = secondPattern.matcher(listOfTerms.get(0));
        
                        while (matcher.find()) {
                            for (int i = 1; i <= matcher.groupCount(); i++) {
                                matchedArray.add(matcher.group(i));
//                                System.out.println(matcher.group(i));
                            }
                        }
                        listOfTerms.remove(0);
                }
                
                tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
                tempLine.setContext(resource.getContext());
                ArrayList<NDLine> tempLinesList = new ArrayList<>();
                tempLinesList.add(tempLine);
//                System.out.println("+++++++++++++++++");
                while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
                    NDLine test = new NDLine(5);
                    int i = 0;
                    tempLine = tempLinesList.get(0);
//                    System.out.println("tempLine is " + tempLine.getLine());
//                    System.out.println("---------------------------------------");
                    while (i < matchedArray.size()) { // Loop through every possible variable
                                                      // Remove the quantifier, substituting for matchedArray.get(i)
//                        System.out.println("|| test with " + matchedArray.get(i) + " ||");
                        if (checkBracketBalance(matchedArray.get(i))) {
                            test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
    //                        System.out.println("     3. test is " + test.getLine());
                            Matcher attempt = Pattern.compile(test.getNonQaSecondArgRegEx()).matcher(proofArray[k].getLine());
                            if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
    //                            System.out.println("     found");
                                if (!test.getMainOp().equals("qa")) {
                                    possibleLines.add(test);
    //                                System.out.println("     3. Added to possibleLines: " + test.getLine());
                                } else {
                                    tempLinesList.add(test);
                                }
                            }
                        }
                        i++; // increment i
                    }
                    tempLinesList.remove(0);
                }
//                System.out.println("+++++++++++++++++");
            }
        }
        

        if (goal.hasSameContextAs(resource)) {
            // Check for matches for firstRegEx in goal
    //        System.out.println(">> Check for matches for firstRegEx in goal <<");
    //        System.out.println("firstRegEx " + firstRegEx);
            listOfTerms = getAllTermsIn(goal.getLine());
    //        System.out.println("number of terms in goal: " + listOfTerms.size());
            matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
            while (!listOfTerms.isEmpty()) {
                Matcher matcher = firstPattern.matcher(listOfTerms.get(0));
    //            System.out.println("Consider term " + listOfTerms.get(0));
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            matchedArray.add(matcher.group(i));
    //                        System.out.println(matcher.group(i));
                        }
                    }
                    listOfTerms.remove(0);
            }

            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
            tempLine.setContext(resource.getContext());
            ArrayList<NDLine> tempLinesList = new ArrayList<>();
            tempLinesList.add(tempLine);
    //        System.out.println("+++++++++++++++++");
            while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
                NDLine test = new NDLine(5);
                int i = 0;
                tempLine = tempLinesList.get(0);
    //            System.out.println("tempLine is " + tempLine.getLine());
    //            System.out.println("---------------------------------------");
                while (i < matchedArray.size()) { // Loop through every possible variable
                                                  // Remove the quantifier, substituting for matchedArray.get(i)
    //                System.out.println("|| test with " + matchedArray.get(i) + " ||");
                    if (checkBracketBalance(matchedArray.get(i))){
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
        //                System.out.println("     3. test is " + test.getLine());
                        Matcher attempt = Pattern.compile(test.getNonQaFirstArgRegEx()).matcher(goal.getLine());
                        if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
    //                        System.out.println("     found");
                            if (!test.getMainOp().equals("qa")) {
                                possibleLines.add(test);
    //                            System.out.println("     3. Added to possibleLines: " + test.getLine());
                            } else {
                                tempLinesList.add(test);
                            }
                        }
                    }
                    i++; // increment i
                }
                tempLinesList.remove(0);
            }
    //        System.out.println("+++++++++++++++++");



            // Check for matches for secondRegEx in goal
    //        System.out.println(">> Check for matches for secondRegEx in goal <<");
            listOfTerms = getAllTermsIn(goal.getLine());
            matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
            while (!listOfTerms.isEmpty()) {
                Matcher matcher = secondPattern.matcher(listOfTerms.get(0));

                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            matchedArray.add(matcher.group(i));
    //                        System.out.println("4. matched " + matcher.group(i));
                        }
                    }
                    listOfTerms.remove(0);
            }

            tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
            tempLine.setContext(resource.getContext());
            tempLinesList = new ArrayList<>();
            tempLinesList.add(tempLine);
    //        System.out.println("+++++++++++++++++");
            while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
                NDLine test = new NDLine(5);
                int i = 0;
                tempLine = tempLinesList.get(0);
    //            System.out.println("tempLine is " + tempLine.getLine());
    //            System.out.println("---------------------------------------");
                while (i < matchedArray.size()) { // Loop through every possible variable
                                                  // Remove the quantifier, substituting for matchedArray.get(i)
    //                System.out.println("|| test with " + matchedArray.get(i) + " ||");
                    if (checkBracketBalance(matchedArray.get(i))) {
                        test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
        //                System.out.println("     3. test is " + test.getLine());
                        Matcher attempt = Pattern.compile(test.getNonQaSecondArgRegEx()).matcher(goal.getLine());
                        if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
        //                    System.out.println("     found");
                            if (!test.getMainOp().equals("qa")) {
                                possibleLines.add(test);
        //                        System.out.println("     3. Added to possibleLines: " + test.getLine());
                            } else {
                                tempLinesList.add(test);
                            }
                        }
                    }
                    i++; // increment i
                }
                tempLinesList.remove(0);
            }
    //        System.out.println("+++++++++++++++++");
        }
        
        
        for (int q = 0; q < possibleLines.size(); q++) {
            String lineAtQ = possibleLines.get(q).getLine();
            String first = possibleLines.get(q).getArg(1);
            String second = possibleLines.get(q).getArg(2);
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println("== Match? ==");
//                System.out.println("q " + lineAtQ);
//                System.out.println("r " + lineR.getLine());
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getArg(1).equals(second) && lineR.getArg(2).equals(first)) {
                    possibleLines.remove(r);
                    r--;
                }
            }
        }
        
        NDLine lineToUse = new NDLine(5);
        if (possibleLines.isEmpty()) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        } else if (possibleLines.size() == 1) {
            lineToUse = possibleLines.get(0);
        } else {
            magicMode = false; // turn off magic mode
            
            Object[] possibilities = new Object[possibleLines.size()];
            for (int i = 0; i < possibleLines.size(); i++) {
                possibilities[i] = possibleLines.get(i).parseLine();
            }
            String s = (String)JOptionPane.showInputDialog(
                    Globals.frame,
                    "Multiple matches found\n"
                    + "Use which possible line?",
                    "Identity Elimination",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
            if (s == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            for (int p = 0; p < possibilities.length; p++) {
                if (possibilities[p].equals(s)) {
                    lineToUse = possibleLines.get(p);
                }
            }
        }
        
        
        
        
        // 2. Check that the main op is now imp //
        if (!lineToUse.getMainOp().equals("eq")) {
            magicMode = false;
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
        lineToUse.setLineNum(resource.getLineNum());
        lineToUse.setContext(resource.getContext());
        lineToUse.setSpecialLineNum(resource.getSpecialNum());
        // 3. Run eqElim on the line
        eqElim(goal, lineToUse);
        if (Globals.runMagicModeWithQa) {
            runMagicMode(2);
        }
        return proofArray;
    }
    
    private NDLine[] universalsIdBoxEqElim(NDLine goal, NDLine resource) { // NOTE: THE STRATEGY USED IN THIS METHOD COULD BE CRAP
        if (!goal.hasSameContextAs(resource)) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
//        System.out.println("=============================");
//        System.out.println("Running universalsIdBoxEqElim");
//        System.out.println("=============================");
        int indexOfGoal = goal.indexIn(proofArray);
//        System.out.println("indexOfGoal is " + indexOfGoal);
//        String otherIdBoxLine;
//        if (indexOfGoal > 0 && proofArray[indexOfGoal-1].getJustification().getBlank()) { // Detect if we're at the bottom or top of an id box
////            atBottom = true;
//            otherIdBoxLine = proofArray[indexOfGoal-2].getLine();
////            indexOfBlank = indexOfGoal - 1;
//        } else {
////            atBottom = false;
//            otherIdBoxLine = proofArray[indexOfGoal+2].getLine();
////            indexOfBlank = indexOfGoal + 1;
//        }
        
        String firstRegEx = resource.getNonQaFirstArgRegEx();
        String secondRegEx = resource.getNonQaSecondArgRegEx();
        ArrayList<NDLine> possibleLines = new ArrayList<>();
        NDLine tempLine;
        
        
        
        
        // Check for matches for firstRegEx in otherIdBoxLine - NOTE: ON SECOND THOUGHTS, WE PROBABLY DON'T WANT TO CHECK FOR MATCHES WITH OTHERIDBOXLINE
        Pattern firstPattern = Pattern.compile(firstRegEx);
        
        ArrayList<String> listOfTerms;
        ArrayList<String> matchedArray;
        
//        listOfTerms = getAllTermsIn(otherIdBoxLine);
//        matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
//        while (!listOfTerms.isEmpty()) {
//            Matcher matcher = firstPattern.matcher(listOfTerms.get(0));
//
//                while (matcher.find()) {
//                    for (int i = 1; i <= matcher.groupCount(); i++) {
//                        matchedArray.add(matcher.group(i));
//                        System.out.println(matcher.group(i));
//                    }
//                }
//                listOfTerms.remove(0);
//        }
//        
//        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
//        while (!matchedArray.isEmpty()) { // while matchedArray has elements
//            NDLine test = new NDLine(5);
//            int i = 0;
//            while (i < matchedArray.size()) { // Loop through every possible variable
//                test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
//                System.out.println("test is " + test.getLine());
//                Matcher attempt = Pattern.compile(test.getNonQaFirstArgRegEx()).matcher(otherIdBoxLine);
//                if (attempt.find()) { // If the firstArg of test matches something in otherIdBoxLine
//                    tempLine = test; //                                             make tempLine = test
//                    matchedArray.remove(matchedArray.get(i)); // remove this variable
//                    if (!tempLine.getMainOp().equals("qa")) {
//                        possibleLines.add(tempLine);
//                        System.out.println("1. Added " + tempLine.getLine());
//                        tempLine = new NDLine(resource.getLine());
//                    }
//                } else {
//                    System.out.println("removed " + matchedArray.get(i));
//                    matchedArray.remove(matchedArray.get(i));
//                }
//                i++; // increment i
//            }
//        }
//        
//        // Check for matches for secondtRegEx in otherIdBoxLine
        Pattern secondPattern = Pattern.compile(secondRegEx);
        
        
//        listOfTerms = getAllTermsIn(otherIdBoxLine);
//        matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
//        while (!listOfTerms.isEmpty()) {
//            Matcher matcher = secondPattern.matcher(listOfTerms.get(0));
//
//                while (matcher.find()) {
//                    for (int i = 1; i <= matcher.groupCount(); i++) {
//                        matchedArray.add(matcher.group(i));
//                        System.out.println(matcher.group(i));
//                    }
//                }
//                listOfTerms.remove(0);
//        }
//        
//        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
//        while (!matchedArray.isEmpty()) { // while matchedArray has elements
//            NDLine test = new NDLine(5);
//            int i = 0;
//            while (i < matchedArray.size()) { // Loop through every possible variable
//                test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
//                System.out.println("test is " + test.getLine());
//                Matcher attempt = Pattern.compile(test.getNonQaSecondArgRegEx()).matcher(otherIdBoxLine);
//                if (attempt.find()) { // If the firstArg of test matches something in otherIdBoxLine
//                    tempLine = test; //                                             make tempLine = test
//                    matchedArray.remove(matchedArray.get(i)); // remove this variable
//                    if (!tempLine.getMainOp().equals("qa")) {
//                        possibleLines.add(tempLine);
//                        System.out.println("2. Added " + tempLine.getLine());
//                        tempLine = new NDLine(resource.getLine());
//                    }
//                } else {
//                    System.out.println("removed " + matchedArray.get(i));
//                    matchedArray.remove(matchedArray.get(i));
//                }
//                i++; // increment i
//            }
//        }
        
        // Check for matches for firstRegEx in goal
        
        listOfTerms = getAllTermsIn(goal.getLine());
//        System.out.println("number of terms in goal: " + listOfTerms.size());
        System.out.println("FIRST REGEX: " + firstRegEx);
        matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
        while (!listOfTerms.isEmpty()) {
            System.out.println("Consider term " + listOfTerms.get(0));
            Matcher matcher = firstPattern.matcher(listOfTerms.get(0));

                while (matcher.find()) {
                    System.out.println("Matched term " + listOfTerms.get(0));
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        matchedArray.add(matcher.group(i));
                        System.out.println(matcher.group(i));
                    }
                }
                listOfTerms.remove(0);
        }
        
        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
        tempLine.setContext(resource.getContext());
        ArrayList<NDLine> tempLinesList = new ArrayList<>();
        tempLinesList.add(tempLine);
//        System.out.println("+++++++++++++++++");
        while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
            NDLine test = new NDLine(5);
            int i = 0;
            tempLine = tempLinesList.get(0);
//            System.out.println("tempLine is " + tempLine.getLine());
//            System.out.println("---------------------------------------");
            while (i < matchedArray.size()) { // Loop through every possible variable
                                              // Remove the quantifier, substituting for matchedArray.get(i)
//                System.out.println("|| test with " + matchedArray.get(i) + " ||");
                if (checkBracketBalance(matchedArray.get(i))) { // If the brackets balance
                    test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
//                    System.out.println("     3. test is " + test.getLine());
                    Matcher attempt = Pattern.compile(test.getNonQaFirstArgRegEx()).matcher(goal.getLine());
                    if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
//                        System.out.println("     found");
                        if (!test.getMainOp().equals("qa")) {
                            possibleLines.add(test);
//                            System.out.println("     2. Added to possibleLines: " + test.getLine());
                        } else {
                            tempLinesList.add(test);
                        }
                    }
                }
                i++; // increment i
            }
            tempLinesList.remove(0);
        }
//        System.out.println("+++++++++++++++++");
        
        
        // Check for matches for secondRegEx in goal
        
        listOfTerms = getAllTermsIn(goal.getLine());
        matchedArray = new ArrayList<>(); // Create an arrayList holding all the matching groups
        while (!listOfTerms.isEmpty()) {
//            System.out.println("Consider term " + listOfTerms.get(0));
            Matcher matcher = secondPattern.matcher(listOfTerms.get(0));

                while (matcher.find()) {
//                    System.out.println("Matched term " + listOfTerms.get(0));
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        matchedArray.add(matcher.group(i));
//                        System.out.println("4. matched " + matcher.group(i));
                    }
                }
                listOfTerms.remove(0);
        }
        
        tempLine = new NDLine(resource.getLine(), false); // Create a temporary NDLine from the resource
        tempLine.setContext(resource.getContext());
        tempLinesList = new ArrayList<>();
        tempLinesList.add(tempLine);
//        System.out.println("+++++++++++++++++");
        while (!tempLinesList.isEmpty()) { // while tempLinesList has elements
            NDLine test = new NDLine(5);
            int i = 0;
            tempLine = tempLinesList.get(0);
//            System.out.println("tempLine is " + tempLine.getLine());
//            System.out.println("---------------------------------------");
            while (i < matchedArray.size()) { // Loop through every possible variable
                                              // Remove the quantifier, substituting for matchedArray.get(i)
//                System.out.println("|| test with " + matchedArray.get(i) + " ||");
                if (checkBracketBalance(matchedArray.get(i))) {
                    test = new NDLine(tempLine.replace(tempLine.getArg(2), tempLine.getArg(1), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
    //                System.out.println("     3. test is " + test.getLine());
                    Matcher attempt = Pattern.compile(test.getNonQaSecondArgRegEx()).matcher(goal.getLine());
                    if (attempt.find()) { // If the firstArg of test matches something in goal.getLine(), i.e. if using that term here is correct 
    //                    System.out.println("     found");
                        if (!test.getMainOp().equals("qa")) {
                            possibleLines.add(test);
//                            System.out.println("     3. Added to possibleLines: " + test.getLine());
                        } else {
                            tempLinesList.add(test);
                        }
                    }
                }
                i++; // increment i
            }
            tempLinesList.remove(0);
        }
//        System.out.println("+++++++++++++++++");
        
        for (int q = 0; q < possibleLines.size(); q++) {
            String lineAtQ = possibleLines.get(q).getLine();
            String first = possibleLines.get(q).getArg(1);
            String second = possibleLines.get(q).getArg(2);
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println("== Match? ==");
//                System.out.println("q " + lineAtQ);
//                System.out.println("r " + lineR.getLine());
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getArg(1).equals(second) && lineR.getArg(2).equals(first)) {
                    possibleLines.remove(r);
                    r--;
                }
            }
        }
        
        NDLine lineToUse = new NDLine(5);
        if (possibleLines.isEmpty()) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        } else if (possibleLines.size() == 1) {
            lineToUse = possibleLines.get(0);
        } else {
            magicMode = false; // turn off magic mode
            
            Object[] possibilities = new Object[possibleLines.size()];
            for (int i = 0; i < possibleLines.size(); i++) {
                possibilities[i] = possibleLines.get(i).parseLine();
            }
            String s = (String)JOptionPane.showInputDialog(
                    Globals.frame,
                    "Multiple matches found\n"
                    + "Use which possible line?",
                    "Identity Elimination",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
            for (int p = 0; p < possibilities.length; p++) {
                if (possibilities[p].equals(s)) {
                    lineToUse = possibleLines.get(p);
                }
            }
        }
        
        
        
        
        // 2. Check that the main op is now imp //
        if (!lineToUse.getMainOp().equals("eq")) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
        lineToUse.setLineNum(resource.getLineNum());
        lineToUse.setContext(resource.getContext());
        lineToUse.setSpecialLineNum(resource.getSpecialNum());
        // 3. Run impElim on the line
        return idBoxEqElim(goal, lineToUse);
    }
    
    private boolean checkBracketBalance(String phrase) {
        int rBracketCount = 0;
        int cBracketCount = 0;
        
        for (int i = 0; i < phrase.length(); i++) {
            char c = phrase.charAt(i);
            if (c == '{') {
                cBracketCount++;
            } else if (c == '}') {
                cBracketCount--;
            } else if (c == '(') {
                rBracketCount++;
            } else if (c == ')') {
                rBracketCount--;
            }
        }
        return cBracketCount == 0 && rBracketCount == 0;
    }
    
    private ArrayList<Integer> checkForAllMatching(String regEx, NDLine goal) { // Returns an arraylist containing all lines that match regEx
        int indexOfGoal = goal.indexIn(proofArray);
        ArrayList<Integer> indexesOfWant = new ArrayList<>();
        int scopes = 0;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
//            System.out.println("Consider " + proofArray[indexOfGoal-i].getLine());
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)
                    && goal.hasSameContextAs(proofArray[indexOfGoal-i])) { // Check line against what we want, but ignore if scopes>0
                indexesOfWant.add(indexOfGoal - i);
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return indexesOfWant;
    }
    
    private ArrayList<Integer> checkForAllMatchingIgnoreContext(String regEx, NDLine goal) { // Returns an arraylist containing all lines that match regEx
        int indexOfGoal = goal.indexIn(proofArray);
        ArrayList<Integer> indexesOfWant = new ArrayList<>();
        int scopes = 0;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
//            System.out.println("Consider " + proofArray[indexOfGoal-i].getLine());
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)) { // Check line against what we want, but ignore if scopes>0
                indexesOfWant.add(indexOfGoal - i);
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return indexesOfWant;
    }
    /*
    Like the second checkFor
    */
    private ArrayList<Integer> checkForAllMatching(String regEx, NDLine goal, NDLine resource) { // Returns an arraylist containing all lines that match regEx
        int indexOfGoal = goal.indexIn(proofArray);
        ArrayList<Integer> indexesOfWant = new ArrayList<>();
        int scopes = 0;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
//            System.out.println("Consider " + proofArray[indexOfGoal-i].getLine());
            if (proofArray[indexOfGoal - i].getType() == 2 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes++;
            }
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)
                    && resource.hasSameContextAs(proofArray[indexOfGoal-i])) { // Check line against what we want, but ignore if scopes>0
                indexesOfWant.add(indexOfGoal - i);
            }
            if (proofArray[indexOfGoal - i].getType() == 1 || proofArray[indexOfGoal - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return indexesOfWant;
    }
    
    private ArrayList<String> getAllTermsIn(String aLine) {
//        System.out.println("===================");
//        System.out.println("getAllTermsIn Start");
//        System.out.println("===================");
        ArrayList<String> listOfTerms = new ArrayList<>();
        Stack<Integer> bracketStartIndexes = new Stack<>();
        
        for (int i = 0; i < aLine.length(); i++) {
            if (aLine.charAt(i) == '(') {
                bracketStartIndexes.push(i);
            } else if (aLine.charAt(i) == ')') {
                System.out.println("Added " + aLine.substring(bracketStartIndexes.peek(), i+1));
                listOfTerms.add(aLine.substring(bracketStartIndexes.pop(), i+1));
                
            }
        }
//        System.out.println("CONTINUE PREVIOUS");
//        System.out.println("=================");
        return listOfTerms;
    }
    
    // NK Rules //
    
    /**
     * Applies double negation to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
        
    public NDLine[] doubleNegation(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        NDLine dNJustLine = currentProof.checkForNDLine(findRegEx("", "\\neg{\\neg{" + goal.getLine() + "}}"), goal);
        if (dNJustLine == null) {
            dNJustLine = new NDLine("\\neg{\\neg{" + goal.getLine() + "}}");
            dNJustLine.setSameContextAs(goal);
            currentProof.addNDLineGoal(dNJustLine, goal);
        }
        currentProof.justifyLine(goal,
                    new JustSingle(JustSingle.DN_ELIM, dNJustLine));
        
        Globals.currentGoalIndex = -1;
        
        proofArray = currentProof.getProofArray();
        
        Globals.rulesUsed.add("doubleNegation");
        return proofArray;
    }
    
    // NK= Rules //
    
    /**
     * Applies identity introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     */
        
    public NDLine[] eqIntro(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        if (goal.getArg(1).equals(goal.getArg(2))) { 
            // If the first arg is the second arg, justify
            currentProof.justifyLine(goal,
                    new JustNone(JustNone.EQ_INTRO));
        } else if (Globals.allowedRules.get("eqIdentityBoxes")) { 
            // Otherwise, if we're using identity boxes
            NDLine idStart = new NDLine(goal.getArg(1), NDLine.ID_BOX_START);
            idStart.setSameContextAs(goal);
            currentProof.addNDLineResource(idStart, goal);
            
            NDLine idEnd = new NDLine(goal.getArg(2), NDLine.ID_BOX_END);
            idEnd.setSameContextAs(goal);
            currentProof.addNDLineGoal(idEnd, goal);
            
            currentProof.justifyLine(goal,
                    new JustSingle(JustSingle.ID_BOX_INTRO, idStart));
        } else {
            magicMode = false;
        }
        
        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("eqIntro");
        
        return proofArray;
    }
    
    /**
     * Applies identity elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] eqElim(NDLine goal, NDLine resource) 
            throws LineNotInProofArrayException, WrongLineTypeException {
//        System.out.println("EQElim");
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("eqElim");
            return universalsEqElim(goal, resource);
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        String leftSide = resource.getArg(1);
        String rightSide = resource.getArg(2);
        
        
        String leftSideParse = resource.parseFirstArg();
        String rightSideParse = resource.parseSecondArg();
        
        if (!goal.getLine().contains(leftSide) && !goal.getLine().contains(rightSide)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        if (!goal.hasSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
        if (goal.getLine().equals("\\falsum")) { // If the goal's falsum, ignore it
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        
//        System.out.println("=======");
//        System.out.println("EquElim");
//        System.out.println("=======");
        
        boolean foundIt = false;
        
        // Substitute for each side
        String subLtR = goal.replace(goal.getLine(), leftSide, rightSide);
        String subRtL = goal.replace(goal.getLine(), rightSide, leftSide);
        
        
        
        int i = 0;
        while (!foundIt && i < indexOfGoal) {
            if (goal.isInScopeOf(proofArray[i], proofArray) && (proofArray[i].getLine().equals(subLtR) || proofArray[i].getLine().equals(subRtL))) {
                goal.setJustification(new JustDouble(JustDouble.EQ_ELIM, resource, proofArray[i]));
                foundIt = true;
                Globals.currentGoalIndex = -1;
            }
            i++;
        }
        
        if (!foundIt) {
            boolean goLtR = true;
            if (!goal.getLine().contains(rightSide)) {
                goLtR = true;
            } else if (!goal.getLine().contains(leftSide)) {
                goLtR = false;
            } else {
                magicMode = false; // turn off magic mode
            
                // Get user input. Ask whether they wish to substitute left to right or right to left
                Object[] options = {leftSideParse + " with " + rightSideParse, rightSideParse + " with " + leftSideParse, "Cancel"};
                int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to replace " + options[0] + " or " + options[1] + "?",
                        "Identity Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (n == 0) {
                    goLtR = true;
                } else if (n == 1) {
                    goLtR = false;
                } else {
                    Globals.reverseUndo = true;
                    return proofArray;
                }
            }
            
            NDLine[] temp = new NDLine[proofArray.length + 1];
            
            int k = 0;
            for (int j = 0; j < indexOfGoal; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (goLtR) {
                if (goal.getLine().indexOf(leftSide) == goal.getLine().lastIndexOf(leftSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), leftSide, rightSide));
                    temp[k].setSameContextAs(goal);
//                    System.out.println("Goal: " + goal.getLine());
//                    System.out.println("Replace " + leftSide + " with " + rightSide);
//                    System.out.println("Result: " + temp[k].getLine());
                } else { //                                                                     // Otherwise, ask the user which instance(s) to replace
                    magicMode = false;
                    
//                    System.out.println("Goal: " + goal.getLine());
//                    System.out.println("Replace " + leftSide + " with " + rightSide);
                    String replacement = MyOptionPane.showTermSelectorDialog(goal.getLine(), leftSide, rightSide);
//                    System.out.println("Result: " + replacement);
                    if (replacement.equals("")) {
                        Globals.reverseUndo = true;
                        return proofArray;
                    }
                    
                    i = 0;
                    foundIt = false;
                    while (!foundIt && i < indexOfGoal) {
                        if (goal.isInScopeOf(proofArray[i], proofArray) && (proofArray[i].getLine().equals(replacement) || proofArray[i].getLine().equals(replacement))) {
                            goal.setJustification(new JustDouble(JustDouble.EQ_ELIM, resource, proofArray[i]));
                            foundIt = true;
                            Globals.currentGoalIndex = -1;
                        }
                        i++;
                    }
                    if (!foundIt) {
                        temp[k] = new NDLine(replacement);
                        temp[k].setSameContextAs(goal);
                    }
                }
            } else {
                if (goal.getLine().indexOf(rightSide) == goal.getLine().lastIndexOf(rightSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), rightSide, leftSide));
                    temp[k].setSameContextAs(goal);
                } else { //                                                                     // Otherwise, ask the user which instance(s) to replace
                    magicMode = false;
                    
                    String replacement = MyOptionPane.showTermSelectorDialog(goal.getLine(), rightSide, leftSide);
                    if (replacement.equals("")) {
                        Globals.reverseUndo = true;
                        return proofArray;
                    }
                    
                    
                    i = 0;
                    foundIt = false;
                    while (!foundIt && i < indexOfGoal) {
                        if (goal.isInScopeOf(proofArray[i], proofArray) && (proofArray[i].getLine().equals(replacement) || proofArray[i].getLine().equals(replacement))) {
                            goal.setJustification(new JustDouble(JustDouble.EQ_ELIM, resource, proofArray[i]));
                            foundIt = true;
                            Globals.currentGoalIndex = -1;
                        }
                        i++;
                    }
                    if (!foundIt) {
                        temp[k] = new NDLine(replacement);
                        temp[k].setSameContextAs(goal);
                    }
                }
            }
            if (!foundIt) {
                goal.setJustification(new JustDouble(JustDouble.EQ_ELIM, resource, temp[k]));
                Globals.currentGoalIndex = k;
                k++;

                for (int j = indexOfGoal; j < proofArray.length; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }

                proofArray = temp;
            }
        }
        
        collapseBlanks();
        
        Globals.rulesUsed.add("eqElim");
        return proofArray;
    }
    
    /**
     * Applies identity elimination to the current proofArray, inside an identity box
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] idBoxEqElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("eqElim");
            return universalsIdBoxEqElim(goal, resource);
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank;
        boolean atBottom;
        String otherIdBoxLine;
        if (indexOfGoal > 0 && proofArray[indexOfGoal-1].getJustification().getBlank()) { // Detect if we're at the bottom or top of an id box
            atBottom = true;
            otherIdBoxLine = proofArray[indexOfGoal-2].getLine();
            indexOfBlank = indexOfGoal - 1;
        } else {
            atBottom = false;
            otherIdBoxLine = proofArray[indexOfGoal+2].getLine();
            indexOfBlank = indexOfGoal + 1;
        }
        
        String leftSide = resource.getArg(1);
        String rightSide = resource.getArg(2);
        
        String leftSideParse = resource.parseFirstArg();
        String rightSideParse = resource.parseSecondArg();
        
        String subLtR = goal.replace(goal.getLine(), leftSide, rightSide);
        String subRtL = goal.replace(goal.getLine(), rightSide, leftSide);
        
        
        NDJust just = new JustSingle(JustSingle.EQ_ELIM_S, resource);
        
        if (!goal.getLine().contains(leftSide) && !goal.getLine().contains(rightSide)) { // If the goal doesn't match the resource, ignore
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule does not apply!");
            return proofArray;
        }
        if (!goal.hasSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
        if (otherIdBoxLine.equals(subLtR) || otherIdBoxLine.equals(subRtL)) { // If a substitution matches the otherIdBoxLine, justify
            if (atBottom) {
                goal.setJustification(just);
            } else {
                proofArray[indexOfGoal + 2].setJustification(just);
            }
            collapseBlanks();
            
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("eqElim");
            return proofArray;
        }
        
        
        // Henceforth, we know that a substitution won't immediately match the otherIdBoxLine
        
        
        boolean goLtR = true;
        if (!goal.getLine().contains(rightSide)) {
            goLtR = true;
        } else if (!goal.getLine().contains(leftSide)) {
            goLtR = false;
        } else {
            magicMode = false; // turn off magic mode
            
            // Get user input. Ask whether they wish to substitute left to right or right to left
            Object[] options = {leftSideParse + " with " + rightSideParse, rightSideParse + " with " + leftSideParse, "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to replace " + options[0] + " or " + options[1] + "?",
                    "Identity Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (n == 0) {
                goLtR = true;
            } else if (n == 1) {
                goLtR = false;
            } else {
                Globals.reverseUndo = true;
                return proofArray;
            }
        }
        
        NDLine[] temp = new NDLine[proofArray.length + 1];
                
        int k = 0;
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        if (goLtR && (goal.getLine().indexOf(leftSide) != goal.getLine().lastIndexOf(leftSide))) { // If there's two instances of leftSide in goal
            magicMode = false;

            subLtR = MyOptionPane.showTermSelectorDialog(goal.getLine(), leftSide, rightSide);
            if (subLtR.equals("")) {
                Globals.reverseUndo = true;
                return proofArray;
            }
        } else if (!goLtR && goal.getLine().indexOf(rightSide) != goal.getLine().lastIndexOf(rightSide)) { // If there's two instances of rightSide in goal
            magicMode = false;

            subRtL = MyOptionPane.showTermSelectorDialog(goal.getLine(), rightSide, leftSide);
            if (subRtL.equals("")) {
                Globals.reverseUndo = true;
                return proofArray;
            }
        }
        
        if ((goLtR && subLtR.equals(otherIdBoxLine)) || (!goLtR && subRtL.equals(otherIdBoxLine))) {
            goal.setJustification(just);
            collapseBlanks();
            Globals.rulesUsed.add("eqElim");
            return proofArray;
        }

        if (atBottom) {
            temp[k] = proofArray[indexOfBlank];
            k++;
            
            if (goLtR) {
                
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setSameContextAs(goal);
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setSameContextAs(goal);
            }
            Globals.currentGoalIndex = k;
            goal.setJustification(just);
            k++;
        } else {
            if (goLtR) {
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setSameContextAs(goal);
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setSameContextAs(goal);
            }
            temp[k].setJustification(just);
            Globals.currentGoalIndex = k;
            k++;

            temp[k] = proofArray[indexOfBlank];
            k++;
        }

        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }

        proofArray = temp;
        collapseBlanks();
        Globals.rulesUsed.add("eqElim");
        return proofArray;
        
    }
    
    /**
     * Applies equivalence elimination to the current proofArray, inside an identity box
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] idBoxEquElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("equElim");
            return universalsIdBoxEquElim(goal, resource);
        }
        if (!goal.hasSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank;
        boolean atBottom;
        String otherIdBoxLine;
        
        
        if (indexOfGoal > 0 && proofArray[indexOfGoal-1].getJustification().getBlank()) { // Detect if we're at the bottom or top of an id box
            atBottom = true;
            otherIdBoxLine = proofArray[indexOfGoal-2].getLine();
            indexOfBlank = indexOfGoal - 1;
        } else {
            atBottom = false;
            otherIdBoxLine = proofArray[indexOfGoal+2].getLine();
            indexOfBlank = indexOfGoal + 1;
        }
        
        String leftSide = resource.getArg(1);
        String rightSide = resource.getArg(2);
        NDJust just = new JustSingle(JustSingle.EQU_ELIM_S, resource);
        
        if ((goal.getLine().equals(leftSide) && otherIdBoxLine.equals(rightSide)) || (goal.getLine().equals(rightSide) && otherIdBoxLine.equals(leftSide))){ // If this equivalence will solve the identity box
            if (atBottom) {
                goal.setJustification(just);
            } else {
                proofArray[indexOfGoal + 2].setJustification(just);
            }
            collapseBlanks();
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("equElim");
            return proofArray;
        } else if (goal.getLine().equals(leftSide)) { // If the goal is on the left side, and the otherIdBoxLine doesn't match
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (atBottom) {
                temp[k] = proofArray[indexOfBlank];
                k++;
                
                temp[k] = new NDLine(resource.getArg(2), 8);
                temp[k].setSameContextAs(resource);
                Globals.currentGoalIndex = k;
                goal.setJustification(just);
                k++;
            } else {
                temp[k] = new NDLine(resource.getArg(2), 8);
                temp[k].setSameContextAs(resource);
                Globals.currentGoalIndex = k;
                temp[k].setJustification(just);
                k++;
                
                temp[k] = proofArray[indexOfBlank];
                k++;
            }
            
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            

            proofArray = temp;
            collapseBlanks();
            Globals.rulesUsed.add("equElim");
            return proofArray;
        } else if (goal.getLine().equals(rightSide)) { // If the goal is on the left side, and the otherIdBoxLine doesn't match
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (atBottom) {
                temp[k] = proofArray[indexOfBlank];
                k++;
                
                temp[k] = new NDLine(resource.getArg(1), 8);
                temp[k].setSameContextAs(resource);
                Globals.currentGoalIndex = k;
                goal.setJustification(just);
                k++;
            } else {
                temp[k] = new NDLine(resource.getArg(1), 8);
                temp[k].setSameContextAs(resource);
                Globals.currentGoalIndex = k;
                temp[k].setJustification(just);
                k++;
                
                temp[k] = proofArray[indexOfBlank];
                k++;
            }
            
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            

            proofArray = temp;
            collapseBlanks();
            Globals.rulesUsed.add("equElim");
            return proofArray;
        } else {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule does not apply!");
        }
        
        collapseBlanks();
        return proofArray;
    }
    
    // Induction, and helpers //
    
    /**
     * Applies induction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     */
        
    public NDLine[] induction(NDLine goal, NDLine resource) throws LineNotInProofArrayException { // Used with a universally quantified goal. Resource is ignored, and is argument for uniformity
        String term = Globals.terms.getNewTerm();    
        
        NDLine zeroLine = new NDLine(NDLine.replace(goal.getArg(2), goal.getArg(1), "0"));
        zeroLine.setSameContextAs(goal);
        currentProof.addNDLineGoal(zeroLine, goal);
        
        NDLine assStartLine = new NDLine(NDLine.replace(goal.getArg(2), goal.getArg(1), term), 1);
        assStartLine.setSameContextAs(goal);
        currentProof.addNDLineResource(assStartLine, goal);
        
        NDLine assEndLine = new NDLine(NDLine.replace(goal.getArg(2), goal.getArg(1), "S" + term), 2);
        assEndLine.setSameContextAs(goal);
        currentProof.addNDLineGoal(assEndLine, goal);
        
        currentProof.justifyLine(goal,
                new JustInduction(zeroLine, assStartLine, assEndLine));
        
        proofArray = currentProof.getProofArray();
        
        Globals.currentGoalIndex = -1;
        
        Globals.rulesUsed.add("induction");
        return proofArray;
    }
    
    // Cut Rule //
    
    /**
     * Applies cut to the current proofArray
     * @param goal the current goal
     * @param newLine the new line to be inserted
     * @return The resulting proofArray.
     */
        
    public NDLine[] cut(NDLine goal, String newLine) {
        currentProof.addNDLineGoal(new NDLine(newLine), goal);
        
        proofArray = currentProof.getProofArray();
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("cut");
        return proofArray;
    }

    // Modal Logic Rules //
    
    /**
     * Applies box introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
        
    public NDLine[] boxIntro(NDLine goal, NDLine resource) {
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine assStart;
        NDLine assEnd;
        
        NDLine[] temp = new NDLine[proofArray.length + 2];
        boolean allowable;
        String term;
        
        if (Globals.allowedRules.get("autoParameters")) {
            term = Globals.terms.getNewContext();
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            
            term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a term", "Box Introduction", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewContext());

            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            allowable = !termIsInScopeOfLine(term, goal);
            if (!allowable) {
                JOptionPane.showMessageDialog(Globals.frame, "Illegal parameter used!");
            }
            Globals.terms.processLine(term);
        }
        int k = 0;
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        temp[k] = new NDLine(goal.getArg(1) + term, 1);
        temp[k].setContext(goal.getContext());
        assStart = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.getArg(2), 2);
        temp[k].setContext(term);
        assEnd = temp[k];
        Globals.currentGoalIndex = k;
        k++;
        
        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        proofArray = temp;
        NDJust just = new JustBoxIntro(assStart, assEnd, goal);
        goal.setJustification(just);
        
        collapseBlanks();
        
        Globals.rulesUsed.add("boxIntro");
        return proofArray;
    }
    
    /**
     * Applies box elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] boxElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        String term;
        if (goal.equalsArgOf(resource, 2)) {
            term = goal.getContext();
        } else {
            magicMode = false;
            term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a term", "Box Elimination", JOptionPane.PLAIN_MESSAGE, null, null, "a");
        }

        if (term == null) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine antecedentLine = checkForNDLine(findRegEx("",resource.getArg(1) + "(" + term + ")"), goal, resource.getContext());
        
        
        if (antecedentLine != null) {
            NDJust just = new JustBoxElim(resource, antecedentLine);
            if (goal.equalsArgOf(resource, 2) && goal.getContext().equals(term)) {
                goal.setJustification(just);
                Globals.currentGoalIndex = -1;
            } else {
                NDLine[] temp = new NDLine[proofArray.length + 1];
                int k = 0;
                
                for (int i = 0; i < indexOfBlank; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getArg(2));
                temp[k].setContext(term);
                temp[k].setJustification(just);
                Globals.currentResourceIndex = k;
                k++;
                
                for (int i = indexOfBlank; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                proofArray = temp;
                Globals.currentGoalIndex = goal.indexIn(proofArray);
                
            }
        } else {
            if (goal.equalsArgOf(resource, 2) && goal.getContext().equals(term)) {
                NDLine[] temp = new NDLine[proofArray.length + 1];
                int k = 0;
                
                for (int i = 0; i < indexOfBlank+1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getArg(1) + term);
                temp[k].setContext(resource.getContext());
                antecedentLine = temp[k];
                Globals.currentGoalIndex = k;
                k++;
                
                for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                proofArray = temp;
                NDJust just = new JustBoxElim(resource, antecedentLine);
                goal.setJustification(just);
                
            } else {
                NDLine[] temp = new NDLine[proofArray.length + 3];
                int k = 0;
                
                for (int i = 0; i < indexOfBlank + 1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getArg(1) + term);
                temp[k].setContext(resource.getContext());
                antecedentLine = temp[k];
                Globals.currentGoalIndex = k;
                k++;
                NDJust just = new JustBoxElim(resource, antecedentLine);
                
                temp[k] = new NDLine(resource.getArg(2));
                temp[k].setContext(term);
                temp[k].setJustification(just);
                Globals.currentResourceIndex = k;
                k++;
                
                for (int i = indexOfBlank; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                proofArray = temp;
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            }
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("boxElim");
        return proofArray;
    }
    
    /**
     * Applies diamond introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] diaIntro(NDLine goal, NDLine resource) {
        ArrayList<Integer> matchingPredLines = checkForAllMatching(goal.getArg(1) + "(\\([\\w\\(\\)]+\\))", goal);
        ArrayList<Integer> matchingPropLines = checkForAllMatchingIgnoreContext(goal.getNonQaSecondArgRegEx(), goal);
        
        NDLine matchingPredLine = null;
        NDLine matchingPropLine = null;
        
        Pattern pattern = Pattern.compile(goal.getArg(1) + "(\\([\\w\\(\\)]+\\))");
        
        for (int i = 0; i < matchingPredLines.size() && matchingPredLine == null; i++) {
//            System.out.println(pattern.pattern());
//            System.out.println(proofArray[matchingPredLines.get(i)].getLine());
            Matcher matcher = pattern.matcher(proofArray[matchingPredLines.get(i)].getLine());
            matcher.find();
//            System.out.println(matcher.group());
            String term = matcher.group(1);
            
            for (int j = 0; j < matchingPropLines.size() && matchingPropLine == null; j++) {
                if (proofArray[matchingPropLines.get(j)].getContext().equals(term)) {
                    matchingPredLine = proofArray[matchingPredLines.get(i)];
                    matchingPropLine = proofArray[matchingPropLines.get(j)];
                }
            }
        }
        
        if (matchingPredLine == null || matchingPropLine == null) {
            magicMode = false;
            String term = (String)JOptionPane.showInputDialog(Globals.frame, "Matching terms not found.\nPlease input a term", "dia Introduction", JOptionPane.PLAIN_MESSAGE, null, null, "a");
            
            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            
            matchingPredLine = checkForNDLine(findRegEx("", goal.getArg(1) + "(" + term + ")"), goal, goal.getContext());
            matchingPropLine = checkForNDLine(findRegEx("", goal.getArg(2)), goal, term);
//            System.out.println(matchingPredLineNum);
//            System.out.println(matchingPropLineNum);
            
            if (matchingPredLine == null && matchingPropLine == null) {
                int indexOfGoal = goal.indexIn(proofArray);
                int indexOfBlank = findIndexOfBlank(indexOfGoal);
                NDLine[] temp = new NDLine[proofArray.length+3];

                int k = 0;
                for (int i = 0; i < indexOfBlank + 1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }

                if (Globals.reverse2PremIntro) {
                    temp[k] = new NDLine(goal.getArg(2));
                    temp[k].setContext(term);
                    matchingPropLine = temp[k];
                    k++;

                    temp[k] = new NDLine(5);
                    k++;
                }

                temp[k] = new NDLine(goal.getArg(1) + term);
                temp[k].setContext(goal.getContext());
                matchingPredLine = temp[k];
                k++;

                if (!Globals.reverse2PremIntro) {
                    temp[k] = new NDLine(5);
                    k++;

                    temp[k] = new NDLine(goal.getArg(2));
                    temp[k].setContext(term);
                    matchingPropLine = temp[k];
                    k++;
                }

                for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                proofArray = temp;
                Globals.currentGoalIndex = -1;
            } else if (matchingPredLine == null && matchingPropLine != null) {
                int indexOfGoal = goal.indexIn(proofArray);
                int indexOfBlank = findIndexOfBlank(indexOfGoal);
                NDLine[] temp = new NDLine[proofArray.length+1];

                int k = 0;
                for (int i = 0; i < indexOfBlank + 1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }

                temp[k] = new NDLine(goal.getArg(1) + term);
                temp[k].setContext(goal.getContext());
                matchingPredLine = temp[k];
                Globals.currentGoalIndex = k;
                k++;

                for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                proofArray = temp;
            } else if (matchingPredLine != null && matchingPropLine == null) {
                int indexOfGoal = goal.indexIn(proofArray);
                int indexOfBlank = findIndexOfBlank(indexOfGoal);
                NDLine[] temp = new NDLine[proofArray.length+1];

                int k = 0;
                for (int i = 0; i < indexOfBlank + 1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }

                temp[k] = new NDLine(goal.getArg(2));
                temp[k].setContext(term);
                matchingPropLine = temp[k];
                Globals.currentGoalIndex = k;
                k++;

                for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                proofArray = temp;
            }
        } else {
            Globals.currentGoalIndex = -1;
        }
        
        goal.setJustification(new JustDiaIntro(matchingPredLine, matchingPropLine, goal));
        
        
        collapseBlanks();
        Globals.rulesUsed.add("diaIntro");
        return proofArray;
    }
    
    /**
     * Applies diamond elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] diaElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        
        NDLine assStart1;
        NDLine assStart2;
        NDLine assEnd;
        
        NDLine[] temp = new NDLine[proofArray.length+3];
        boolean allowable;
        String term; 
        
        if (Globals.allowedRules.get("autoParameters")) {
            term = Globals.terms.getNewContext();
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            
            term = (String)JOptionPane.showInputDialog(Globals.frame, "Please input a term", "Diamond Elimination", JOptionPane.PLAIN_MESSAGE, null, null, Globals.terms.getNewContext());

            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            }
            allowable = !termIsInScopeOfLine(term, goal);
            if (!allowable) {
                JOptionPane.showMessageDialog(Globals.frame, "Illegal parameter used!");
            }
            Globals.terms.processLine(term);
        }
        int k = 0;
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        temp[k] = new NDLine(resource.getArg(1) + term, 1);
        temp[k].setContext(resource.getContext());
        assStart1 = temp[k];
        k++;
        
        temp[k] = new NDLine(resource.getArg(2));
        temp[k].setJustification(new JustNone(JustNone.ASS_JUST));
        temp[k].setContext(term);
        assStart2 = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.getLine(), 2);
        temp[k].setContext(goal.getContext());
        assEnd = temp[k];
        Globals.currentGoalIndex = k;
        k++;
        
        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        NDJust just = new JustDiaElim(resource, assStart1, assStart2, assEnd, allowable);
        goal.setJustification(just);
        proofArray = temp;
        
        collapseBlanks();
        Globals.rulesUsed.add("diaElim");
        return proofArray;
    }
    
    /**
     * Applies @ introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] atIntro(NDLine goal, NDLine resource) {
        NDLine prop = checkForNDLine(findRegEx("",goal.getArg(2)), goal, goal.getArg(1));
        
        if (prop != null) {
            NDJust just = new JustSingle(JustSingle.AT_INTRO, prop);
            goal.setJustification(just);
            Globals.currentGoalIndex = -1;
        } else {
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            
            NDLine[] temp = new NDLine[proofArray.length +1];
            int k = 0;
            for (int i = 0; i < indexOfGoal; i++) {
                temp[k] = proofArray[k];
                k++;
            }
            
            temp[k] = new NDLine(goal.getArg(2));
            temp[k].setContext(goal.getArg(1));
            NDJust just = new JustSingle(JustSingle.AT_INTRO, temp[k]);
            goal.setJustification(just);
            Globals.currentGoalIndex = k;
            k++;
            
            for (int i = indexOfGoal; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            proofArray = temp;
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("atIntro");
        return proofArray;
    }
    
    /**
     * Applies @ elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] atElim(NDLine goal, NDLine resource) {
        NDJust just = new JustSingle(JustSingle.AT_ELIM, resource);
        if (goal.getContext().equals(resource.getArg(1))
                && goal.equalsArgOf(resource, 2)) {
            goal.setJustification(just);
            Globals.currentGoalIndex = -1;
        } else {
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            
            NDLine[] temp = new NDLine[proofArray.length + 1];
            int k = 0;
            for (int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(resource.getArg(2));
            temp[k].setJustification(just);
            temp[k].setContext(resource.getArg(1));
            Globals.currentResourceIndex = k;
            k++;
            
            for (int i = indexOfBlank; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        }
        
        Globals.rulesUsed.add("atElim");
        collapseBlanks();
        return proofArray;
    }
    
    // Hybrid Logic Rules //
    
    /**
     * Applies nominal introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
        
    public NDLine[] nomIntro(NDLine goal, NDLine resource) {
        if (goal.getLine().equals(goal.getContext())) { // If the first arg is the second arg, justify
            goal.setJustification(new JustNone(JustNone.NOM_INTRO));
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("nomIntro");
        } else if (Globals.allowedRules.get("eqIdentityBoxes")) { // Otherwise, if we're using identity boxes
//            System.out.println("hello");
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine idStart;
            
            NDLine[] temp = new NDLine[proofArray.length + 2];
            
            int k = 0;
            for (int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(goal.getContext(), 7);
            temp[k].setSameContextAs(goal);
            idStart = temp[k];
            k++;
            
            temp[k] = proofArray[indexOfBlank];
            k++;
            
            temp[k] = new NDLine(goal.getLine(), 9);
            temp[k].setSameContextAs(goal);
            Globals.currentGoalIndex = k;
            k++;
            
            for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            NDJust just = new JustSingle(JustSingle.NOM_BOX_INTRO, idStart);
            goal.setJustification(just);
            
            proofArray = temp;
//            Globals.rulesUsed.add("idBoxEqIntro");
            Globals.rulesUsed.add("nomIntro");
        } else {
            magicMode = false;
        }
        
        collapseBlanks();
        return proofArray;
    }
    
    /**
     * Applies nominal elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] nomElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        
        NDLine eqLine = new NDLine("\\eq{" + resource.getContext() + "}{" + resource.getLine() + "}", false);
        eqLine.setContext("");
        eqLine.setLineNum(resource.getLineNum());
        eqLine.setSpecialLineNum(resource.getSpecialNum());
        String leftSide = eqLine.getArg(1);
        String rightSide = eqLine.getArg(2);
        
        String leftSideParse = eqLine.parseFirstArg();
        String rightSideParse = eqLine.parseSecondArg();
        
        if (!goal.getLine().contains(leftSide) && !goal.getLine().contains(rightSide)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        if (goal.getLine().equals("\\falsum")) { // If the goal's falsum, ignore it
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
//        System.out.println("=======");
//        System.out.println("EquElim");
//        System.out.println("=======");
        
        boolean foundIt = false;
        
        // Substitute for each side
        String subLtR = goal.replace(goal.getLine(), leftSide, rightSide);
        String subRtL = goal.replace(goal.getLine(), rightSide, leftSide);
        
        
        int i = 0;
        while (!foundIt && i < indexOfGoal) {
            if (goal.isInScopeOf(proofArray[i], proofArray) && (proofArray[i].getLine().equals(subLtR) || proofArray[i].getLine().equals(subRtL))
                    && goal.getIsAllowedInContext(proofArray[i].getContext())) {
                goal.setJustification(new JustDouble(JustDouble.NOM_ELIM, resource, proofArray[i]));
                foundIt = true;
                Globals.currentGoalIndex = -1;
            }
            i++;
        }
        if (!foundIt) {
            boolean goLtR = true;
            if (!goal.getLine().contains(rightSide)) {
                goLtR = true;
            } else if (!goal.getLine().contains(leftSide)) {
                goLtR = false;
            } else {
                magicMode = false; // turn off magic mode
            
                // Get user input. Ask whether they wish to substitute left to right or right to left
                Object[] options = {leftSideParse + " with " + rightSideParse, rightSideParse + " with " + leftSideParse, "Cancel"};
                int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to replace " + options[0] + " or " + options[1] + "?",
                        "Nominal Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (n == 0) {
                    goLtR = true;
                } else if (n == 1) {
                    goLtR = false;
                } else {
                    Globals.reverseUndo = true;
                    return proofArray;
                }
            }
            NDLine[] temp = new NDLine[proofArray.length + 1];
            
            int k = 0;
            for (int j = 0; j < indexOfGoal; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (goLtR) {
                if (goal.getLine().indexOf(leftSide) == goal.getLine().lastIndexOf(leftSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), leftSide, rightSide));
                    temp[k].setSameContextAs(goal);
//                    System.out.println("Goal: " + goal.getLine());
//                    System.out.println("Replace " + leftSide + " with " + rightSide);
//                    System.out.println("Result: " + temp[k].getLine());
                } else { //                                                                     // Otherwise, ask the user which instance(s) to replace
                    magicMode = false;
                    
//                    System.out.println("Goal: " + goal.getLine());
//                    System.out.println("Replace " + leftSide + " with " + rightSide);
                    String replacement = MyOptionPane.showTermSelectorDialog(goal.getLine(), leftSide, rightSide);
//                    System.out.println("Result: " + replacement);
                    if (replacement.equals("")) {
                        Globals.reverseUndo = true;
                        return proofArray;
                    }
                    temp[k] = new NDLine(replacement);
                    temp[k].setSameContextAs(goal);
                }
            } else {
                if (goal.getLine().indexOf(rightSide) == goal.getLine().lastIndexOf(rightSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), rightSide, leftSide));
                    temp[k].setSameContextAs(goal);
                } else { //                                                                     // Otherwise, ask the user which instance(s) to replace
                    magicMode = false;
                    
                    String replacement = MyOptionPane.showTermSelectorDialog(goal.getLine(), rightSide, leftSide);
                    if (replacement.equals("")) {
                        Globals.reverseUndo = true;
                        return proofArray;
                    }
                    temp[k] = new NDLine(replacement);
                    temp[k].setSameContextAs(goal);
                }
            }
            goal.setJustification(new JustDouble(JustDouble.NOM_ELIM, resource, temp[k]));
            Globals.currentGoalIndex = k;
            k++;
            
            for (int j = indexOfGoal; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            proofArray = temp;
        }
        
        collapseBlanks();
        
        Globals.rulesUsed.add("nomElim");
        return proofArray;
    }
    
    /**
     * Applies nominal elimination to the current proofArray, inside an identity box
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] idBoxNomElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank;
        boolean atBottom;
        String otherIdBoxLine;
        if (indexOfGoal > 0 && proofArray[indexOfGoal-1].getJustification().getBlank()) { // Detect if we're at the bottom or top of an id box
            atBottom = true;
            otherIdBoxLine = proofArray[indexOfGoal-2].getLine();
            indexOfBlank = indexOfGoal - 1;
        } else {
            atBottom = false;
            otherIdBoxLine = proofArray[indexOfGoal+2].getLine();
            indexOfBlank = indexOfGoal + 1;
        }
        
        NDLine eqLine = new NDLine("\\eq{" + resource.getContext() + "}{" + resource.getLine() + "}", false);
        eqLine.setContext("");
        eqLine.setLineNum(resource.getLineNum());
        eqLine.setSpecialLineNum(resource.getSpecialNum());
        
        String leftSide = eqLine.getArg(1);
        String rightSide = eqLine.getArg(2);
        
        String leftSideParse = eqLine.parseFirstArg();
        String rightSideParse = eqLine.parseSecondArg();
        
        String subLtR = goal.replace(goal.getLine(), leftSide, rightSide);
        String subRtL = goal.replace(goal.getLine(), rightSide, leftSide);
        
        
        NDJust just = new JustSingle(JustSingle.NOM_ELIM_S, resource);
        
        if (!goal.getLine().contains(leftSide) && !goal.getLine().contains(rightSide)) { // If the goal doesn't match the resource, ignore
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule does not apply!");
            return proofArray;
        }
        if (otherIdBoxLine.equals(subLtR) || otherIdBoxLine.equals(subRtL)) { // If a substitution matches the otherIdBoxLine, justify
            if (atBottom) {
                goal.setJustification(just);
            } else {
                proofArray[indexOfGoal + 2].setJustification(just);
            }
            collapseBlanks();
            
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("nomElim");
            return proofArray;
        }
        
        
        // Henceforth, we know that a substitution won't immediately match the otherIdBoxLine
        
        
        boolean goLtR = true;
        if (!goal.getLine().contains(rightSide)) {
            goLtR = true;
        } else if (!goal.getLine().contains(leftSide)) {
            goLtR = false;
        } else {
            magicMode = false; // turn off magic mode
            
            // Get user input. Ask whether they wish to substitute left to right or right to left
            Object[] options = {leftSideParse + " with " + rightSideParse, rightSideParse + " with " + leftSideParse, "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to replace " + options[0] + " or " + options[1] + "?",
                    "Nominal Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (n == 0) {
                goLtR = true;
            } else if (n == 1) {
                goLtR = false;
            } else {
                Globals.reverseUndo = true;
                return proofArray;
            }
        }
        
        NDLine[] temp = new NDLine[proofArray.length + 1];
                
        int k = 0;
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        if (goLtR && (goal.getLine().indexOf(leftSide) != goal.getLine().lastIndexOf(leftSide))) { // If there's two instances of leftSide in goal
            magicMode = false;

            subLtR = MyOptionPane.showTermSelectorDialog(goal.getLine(), leftSide, rightSide);
            if (subLtR.equals("")) {
                Globals.reverseUndo = true;
                return proofArray;
            }
        } else if (!goLtR && goal.getLine().indexOf(rightSide) != goal.getLine().lastIndexOf(rightSide)) { // If there's two instances of rightSide in goal
            magicMode = false;

            subRtL = MyOptionPane.showTermSelectorDialog(goal.getLine(), rightSide, leftSide);
            if (subRtL.equals("")) {
                Globals.reverseUndo = true;
                return proofArray;
            }
        }
        
        if ((goLtR && subLtR.equals(otherIdBoxLine)) || (!goLtR && subRtL.equals(otherIdBoxLine))) {
            goal.setJustification(just);
            collapseBlanks();
            Globals.rulesUsed.add("nomElim");
            return proofArray;
        }

        if (atBottom) {
            temp[k] = proofArray[indexOfBlank];
            k++;
            
            if (goLtR) {
                
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setSameContextAs(goal);
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setSameContextAs(goal);
            }
            Globals.currentGoalIndex = k;
            goal.setJustification(just);
            k++;
        } else {
            if (goLtR) {
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setSameContextAs(goal);
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setSameContextAs(goal);
            }
            temp[k].setJustification(just);
            Globals.currentGoalIndex = k;
            k++;

            temp[k] = proofArray[indexOfBlank];
            k++;
        }

        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }

        proofArray = temp;
        collapseBlanks();
        Globals.rulesUsed.add("nomElim");
        return proofArray;
    }
    
    /**
     * Applies self-reference introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] selfIntro(NDLine goal, NDLine resource) {
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        
        String result = goal.replace(goal.getArg(2), goal.getArg(1), goal.getContext());
        NDLine refLine = checkForNDLine(findRegEx("", result), goal);
        
        if (refLine != null) {
            goal.setJustification(new JustSingle(JustSingle.SELF_INTRO, refLine));
            Globals.currentGoalIndex = -1;
        } else {
            NDLine[] temp = new NDLine[proofArray.length+1];
            
            int k = 0;
            for(int i = 0; i < indexOfBlank + 1; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(result);
            temp[k].setContext(goal.getContext());
            refLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;
            
            for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            proofArray = temp;
        }
        collapseBlanks();
        Globals.rulesUsed.add("selfIntro");
        return proofArray;
    }
    
    /**
     * Applies self-reference elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] selfElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        String result = resource.replace(resource.getArg(2), resource.getArg(1), resource.getContext());
        NDJust just = new JustSingle(JustSingle.SELF_ELIM, resource);
        
        if (goal.getContext().equals(resource.getContext())
                && goal.getLine().equals(result)) {
            goal.setJustification(just);
            Globals.currentGoalIndex = -1;
        } else {
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length+1];
            
            int k = 0;
            for(int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(result);
            temp[k].setContext(resource.getContext());
            temp[k].setJustification(just);
            Globals.currentResourceIndex = k;
            k++;
            
            for (int i = indexOfBlank; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("selfElim");
        return proofArray;
    }
    
    // Other Rules //

    /**
     * Applies the same line rule to the current proofArray
     * @param goal the current goal
     * @param resource the current resource (identical to the current goal)
     * @return The resulting proofArray.
     */
    public NDLine[] sameLine(NDLine goal, NDLine resource) {
        if (goal.isInScopeOf(resource, proofArray) && goal.getLine().equals(resource.getLine())
                && goal.hasSameContextAs(resource)) {
            goal.setJustification(new JustSingle(JustSingle.LINE_IS_EQUAL, resource));
        }
        collapseBlanks();
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("sameLine");
        return proofArray;
    }
    
    // Other Public Methods //
    
    /**
     * EXPERIMENTAL! Starting with the current goal and current resource, expends every automatic rule it can until it hits a choice or the maximum number of steps.
     * @param max The maximum number of steps to complete
     * @return The resulting proofArray.
     * @throws proofassistant.exception.LineNotInProofArrayException
     */
        
    public NDLine[] runMagicMode(int max) throws LineNotInProofArrayException, WrongLineTypeException { // Automatically expend all possible automatic rules from the current goal and then from the current resource
        magicMode = true;
        boolean tempEqIdBoxes = Globals.allowedRules.get("eqIdentityBoxes");
        boolean tempEquIdBoxes = Globals.allowedRules.get("equIdentityBoxes");
        Globals.allowedRules.put("eqIdentityBoxes", false);
        Globals.allowedRules.put("equIdentityBoxes", false);
        
        int i = 0;
        while (magicMode && i < max) {
            if (Globals.currentGoalIndex < 0 || Globals.currentGoalIndex > proofArray.length-1) {                        // if there's no current goal, leave magic mode
                magicMode = false;
            } else { //                                                                                                  // otherwise,
                NDLine goal = proofArray[Globals.currentGoalIndex];
                if (Globals.currentResourceIndex < 0 || Globals.currentResourceIndex > Globals.currentGoalIndex) {       // if there's no current resource, or if it's below the current goal
                    
                } else { //                                                                                              // otherwise
                    NDLine resource = proofArray[Globals.currentResourceIndex];
                    if (resource.getLine().equals(goal.getLine())) {
                        sameLine(goal, resource);
                    } 
                    if (!magicMode || !resource.getLine().equals(goal.getLine())) {
                        magicMode = true;
                        String goalOp = goal.getMainOp();
                        if (!goalOp.equals("")) { //                                                                     // if the main operator of the goal exists
                            mMIntroActions(goalOp, goal, resource);
                        } 
                        if (!magicMode || goalOp.equals("")) { //                                                        // if the main operator of the goal doesn't exist
                            magicMode = true;
                            String resourceOp = resource.getMainOp();
                            if (!resourceOp.equals("")) { //                                                             // if the main operator of the resource exists
                                mMElimActions(resourceOp, goal, resource);
                            } else { //                                                                                  // if the main operator of the resource doesn't exist
                                magicMode = false; //                                                                          leave magicMode
                            }
                        }
                    }
                    
                }
            }
            i++;
        }
        if (i == max-1) {
            System.out.println("ALERT: Magic Mode ran " + max + " times");
        }
        
        Globals.allowedRules.put("eqIdentityBoxes", tempEqIdBoxes);
        Globals.allowedRules.put("equIdentityBoxes", tempEquIdBoxes);
        return proofArray;
    }
    
    // Magic Mode Helpers //
    private void mMIntroActions(String op, NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        
        
        // Main Stuff
        if (op.equals("con")){
            conIntro(goal,resource);
        } else if (op.equals("dis")) {
            disIntro(goal,resource);
        } else if (op.equals("imp")) {
            impIntro(goal,resource);
        } else if (op.equals("equ")) {
            equIntro(goal,resource);
        } else if (op.equals("neg")) {
            negIntro(goal,resource);
        } else if (op.equals("qa")) {
            qaIntro(goal, resource);
        } else if (op.equals("qe")) {
            qeIntro(goal, resource);
        } else if (op.equals("eq")) {
            eqIntro(goal, resource);
        } else if (op.equals("at")) {
            atIntro(goal, resource);
        } else if (op.contains("box")) {
            boxIntro(goal, resource);
        } else if (op.contains("dia")) {
            diaIntro(goal, resource);
        } else if (op.contains("nom")) {
            nomIntro(goal, resource);
        } else if (op.contains("self")) {
            selfIntro(goal, resource);
        }
    }
    
    private void mMElimActions(String op, NDLine goal, NDLine resource) throws LineNotInProofArrayException, WrongLineTypeException {
        if (Globals.allowedRules.get("universalsShortcuts")) {
            op = resource.getNonUniMainOp();
        }
        
        // Main Stuff
        if (op.equals("con")){
            conElim(goal,resource);
        } else if (op.equals("dis")) {
            disElim(goal,resource);
        } else if (op.equals("imp")) {
            impElim(goal,resource);
        } else if (op.equals("equ")) {
            equElim(goal,resource);
        } else if (op.equals("neg")) {
            negElim(goal,resource);
        } else if (resource.getLine().equals("\\falsum")){
            falsumElim(goal,resource);
        } else if (op.equals("qa")) {
            qaElim(goal,resource);
        } else if (op.equals("qe")) {
            qeElim(goal,resource);
        } else if (op.equals("eq")) {
            eqElim(goal, resource);
        } else if (op.equals("at")) {
            atElim(goal, resource);
        } else if (op.contains("box")) {
            boxElim(goal, resource);
        } else if (op.contains("dia")) {
            diaElim(goal, resource);
        } else if (op.contains("nom")) {
            nomElim(goal, resource);
        } else if (op.contains("self")) {
            selfElim(goal, resource);
        }
    }

}
