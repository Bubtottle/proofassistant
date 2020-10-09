/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proofassistant;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

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

    /**
     * Create a new ProofMethods object with a supplied sequent
     * @param args The sequent to start with, in TeX code format
     */
    public ProofObject(String[] args) {
        proofArray = readInArgs(args);
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
        
    private NDLine[] readInArgs(String[] args) { // Returns an NDLine array containing the premises and conclusion (with blank between), taken from the args array
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
     */
    @Override
    public ProofObject clone() {
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

    private void removeAllBlanks() { // Removes all the blank lines from proofArray
        int numBlanks = 0;
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getType() == 5) {
                numBlanks++;
            }
        }
        NDLine[] temp = new NDLine[proofArray.length - numBlanks];

        int j = 0;
        for (int k = 0; k < proofArray.length; k++) {
            if (proofArray[k].getType() != 5) {
                temp[j] = proofArray[k];
                j++;
            }
        }

        proofArray = temp;
    }

    private boolean checkScopeFinished(NDLine goal) { // Returns true if the scope of the current goal is entirely justified
        boolean scopeFinished = true;
        boolean hitTop = false;
        boolean hitBottom = false;
        
        

        int indexOfGoal = goal.indexIn(proofArray);
        int i = 0;
        while (scopeFinished && i < indexOfGoal && !hitTop) { // Check everything from the goal up to the lowest assumption is justified
            if (proofArray[indexOfGoal - i].getType() != 5 && proofArray[indexOfGoal - i].getJustification().getBlank()) { // if the line has no justification
                scopeFinished = false;
            }
            if (proofArray[indexOfGoal - i].getType() == 1) {
                hitTop = true;
            }
            i++;
        }
        

        int j = indexOfGoal;
        while (scopeFinished && j < proofArray.length && !hitBottom) { // Check everything from the goal down to the highest assumption close or the next blank down is justified
            if (proofArray[j].getType() != 5 && proofArray[j].getJustification().getBlank()) {
                scopeFinished = false;
            }
            if (proofArray[j].getType() == 2 || proofArray[j].getType() == 5) {
                hitBottom = true;
            }
            j++;
        }
        
        return scopeFinished;
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
    
    private String findRegExProp(String variable, String phrase) {
        
        String regEx = phrase.replace("\\", "\\\\"); // Replace \ with \\ to make regex feel happy
        regEx = regEx.replace("{", "\\{").replace("}", "\\}"); // Replace { and } with \{ and \} to make regex feel happy
        regEx = regEx.replace("[", "\\[").replace("]", "\\]");
        regEx = regEx.replace("(", "\\(").replace(")", "\\)"); // Do the same with ( and )
        if (!variable.equals("")) {
            regEx = regEx.replace(variable, "\\1"); // Replace variable with the 1st back reference
            regEx = regEx.replaceFirst("\\\\1", "([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+|\\\\w+)"); // Replace the first back reference with a group to match 1 or more letters
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
    
    private int findIndexOfAssEnd(int indexOfGoal) { // Returns the index of the assumption end before the goal. Returns -1 if none found
        int indexOfAssEnd = -1;

        int i = 1;
        while (indexOfAssEnd == -1 && i <= indexOfGoal) {
            if (proofArray[indexOfGoal - i].getType() == 2) {
                indexOfAssEnd = indexOfGoal - i;
            }
            i++;
        }


        return indexOfAssEnd;
        
    }
    
    private int findTopOfIdBox(int indexOfGoal) {
        int indexOfIdStart = -1;
        if (proofArray[indexOfGoal].getType() > 6 && proofArray[indexOfGoal].getType() < 11) {
            int i = indexOfGoal;
            while (i > -1 && indexOfIdStart == -1) {
                if (proofArray[i].getType() == 7) {
                    indexOfIdStart = i;
                }
                i--;
            }
        }
        
        return indexOfIdStart;
    }
    
    private String findContext(NDLine goal, NDLine resource) {
        if (goal.getContext().equals("") && resource.getContext().equals("")) {
            return "";
        } else if (goal.getContext().equals("")) {
            return resource.getContext();
        } else {
            return goal.getContext();
        }
    }
    
    private String findElimContext(NDLine goal, NDLine resource) {
        return findContext(goal, resource);
    }
    
    private String findIntroContext(NDLine goal, NDLine resource) {
        return findContext(goal, resource);
    }
    
    private String getGoalContext(NDLine goal, NDLine resource) { // In case we shouldn't use this, for easy changing
        return goal.getContext();
    }
    
    private String getResourceContext(NDLine goal, NDLine resource) { // In case we shouldn't use this, for easy changing
        return resource.getContext();
    }
    

    // NJ Rules //

    /**
     * Applies conjunction elimination to the current proofArray.
     * @param goal the current goal.
     * @param resource the current resource.
     * @return The resulting proofArray.
     * @throws proofassistant.LineNotInProofArrayException
     */
    public NDLine[] conElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException { // Applies conjunction elimination to the supplied goal and resource, in proofArray
        if (resource.getMainOp().equals("qa")) {
            if ((goal.getLine().matches(resource.getNonUniFirstArgRegEx()) 
                    || goal.getLine().matches(resource.getNonUniSecondArgRegEx()))
                    && goal.getIsSameContextAs(resource)) {
                currentProof.justifyLine(goal, new JustSingle(JustSingle.CON_ELIM, resource));
            }
            proofArray = currentProof.getProofArray();
            Globals.rulesUsed.add("conElim");
            return proofArray;
        }
        
        
        // Checks if the goal is a conjunct of the resource. If so, justifies the goal
        if ((goal.getLine().equals(resource.getFirstArg()) || goal.getLine().equals(resource.getSecondArg()))
                && goal.getIsSameContextAs(resource)) { // If the goal is one of the conjuncts and they have the same context
            goal.setJustification(new JustSingle(JustSingle.CON_ELIM, resource));
            Globals.currentGoalIndex = -1;
            
            
        } else { // So goal is not a conjunct of resource. We will ask which (or both) of two new resources to append before the blank space before the goal or after the current resource
            magicMode = false; // turn off magic mode
            
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            // Ask the user what to create - first argument, second argument or both
            Object[] options = {resource.parseFirstArg(), resource.parseSecondArg(), "Both", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to create " + options[0] + ", " + options[1] + " or both?",
                    "Conjunction Elimination", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

            // Expand proofArray to handle two new resources (the conjuncts of the resource), and justify them by the resource
            if (n == 0) {
                // Expand proofArray to handle one new resource (the first arg of the resource), and justify it by the resource
                NDLine[] temp = new NDLine[proofArray.length + 1];

                int k = 0;
                for (int j = 0; j < indexOfBlank; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }
                temp[k] = new NDLine(resource.getFirstArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustSingle(JustSingle.CON_ELIM, resource));

                k++;
                for (int j = indexOfBlank; j < proofArray.length; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }

                proofArray = temp;
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            } else if (n == 1) {
                // Expand proofArray to handle one new resource (the second arg of the resource), and justify it by the resource
                NDLine[] temp = new NDLine[proofArray.length + 1];

                int k = 0;
                for (int j = 0; j < indexOfBlank; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getSecondArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustSingle(JustSingle.CON_ELIM, resource));

                k++;
                for (int j = indexOfBlank; j < proofArray.length; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }

                proofArray = temp;
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            } else if (n == 2) {
                // Expand proofArray to handle two new resources (the conjuncts of the resource), and justify them by the resource
                NDLine[] temp = new NDLine[proofArray.length + 2];

                int k = 0;
                for (int j = 0; j < indexOfBlank; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }
                temp[k] = new NDLine(resource.getFirstArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustSingle(JustSingle.CON_ELIM, resource));

                k++;
                temp[k] = new NDLine(resource.getSecondArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustSingle(JustSingle.CON_ELIM, resource));

                k++;
                for (int j = indexOfBlank; j < proofArray.length; j++) {
                    temp[k] = proofArray[j];
                    k++;
                }

                proofArray = temp;
                Globals.currentGoalIndex = goal.indexIn(proofArray);
            } else {
                Globals.reverseUndo = true;
            }
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("conElim");
        return proofArray;

    }

    /**
     * Applies conjunction introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] conIntro(NDLine goal, NDLine resource) { // Applies conjunction introduction to the supplied goal and resource, in proofArray
        NDLine firstJustLine = checkForNDLine(findRegEx("", goal.getFirstArg()), goal);
        NDLine secondJustLine = checkForNDLine(findRegEx("", goal.getSecondArg()), goal);
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine[] tempCon = proofArray;
        
        Globals.currentGoalIndex = -1;
        
        // If a conjunct hasn't been found, prepends it as a new goal.
        if (!Globals.reverse2PremIntro && firstJustLine == null) {
            NDLine[] temp = new NDLine[tempCon.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            temp[k] = new NDLine(goal.getFirstArg());
            temp[k].setContext(getGoalContext(goal, resource));
            firstJustLine = temp[k];
            Globals.currentGoalIndex = k;

            k++;
            for (int j = indexOfBlank + 1; j < tempCon.length; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            tempCon = temp;
            indexOfBlank++;
        }

        if (secondJustLine == null) {
            NDLine[] temp;
            if (tempCon[indexOfBlank].getType() == 5) { // So we're just creating a second argument
                temp = new NDLine[tempCon.length + 1];
            } else { // So we've already created a first argument, and need to select a blank between it and the second argument
                temp = new NDLine[tempCon.length + 2];
            }
            

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            if (tempCon[indexOfBlank].getType() != 5) { // If we've already created a first argument
                temp[k] = new NDLine(5); // Create a Blank
            k++;
            }
            
            temp[k] = new NDLine(goal.getSecondArg());
            temp[k].setContext(getGoalContext(goal, resource));
            secondJustLine = temp[k];
            if (Globals.currentGoalIndex != -1) {
                Globals.currentGoalIndex = -1;
            } else {
                Globals.currentGoalIndex = k;
            }

            k++;
            for (int j = indexOfBlank + 1; j < tempCon.length; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            tempCon = temp;
            indexOfBlank ++;
        }
        
        if (Globals.reverse2PremIntro && firstJustLine == null) {
            NDLine[] temp;
            if (tempCon[indexOfBlank].getType() == 5) { // So we're just creating a second argument
                temp = new NDLine[tempCon.length + 1];
            } else { // So we've already created a first argument, and need to select a blank between it and the second argument
                temp = new NDLine[tempCon.length + 2];
            }
            

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            if (tempCon[indexOfBlank].getType() != 5) { // If we've already created a first argument
                temp[k] = new NDLine(5); // Create a Blank
            k++;
            }
            
            temp[k] = new NDLine(goal.getFirstArg());
            temp[k].setContext(getGoalContext(goal, resource));
            firstJustLine = temp[k];
            if (Globals.currentGoalIndex != -1) {
                Globals.currentGoalIndex = -1;
            } else {
                Globals.currentGoalIndex = k;
            }

            k++;
            for (int j = indexOfBlank + 1; j < tempCon.length; j++) {
                temp[k] = tempCon[j];
                k++;
            }
            tempCon = temp;
        }

        // Justifies the goal with the found or created conjunct lines
        NDJust just = new JustDouble(JustDouble.CON_INTRO, firstJustLine, secondJustLine);
        goal.setJustification(just);
        
        proofArray = tempCon;

        collapseBlanks();
        
        Globals.rulesUsed.add("conIntro");
        return proofArray;
    }

    /**
     * Applies disjunction elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] disElim(NDLine goal, NDLine resource) {
        if (resource.getMainOp().equals("qa")) {
            Globals.reverseUndo = true;
            return proofArray;
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        int indexOfAssEnd = findIndexOfAssEnd(indexOfGoal);
        String context = findElimContext(goal, resource);
        
        
        NDLine assLineOne;
        NDLine assEndOne;
        NDLine assLineTwo;
        NDLine assEndTwo;
        int extraSpaces;
        
        if (indexOfBlank > indexOfAssEnd) {
            extraSpaces = 5;
        } else {
            extraSpaces = 6;
        }
        NDLine[] temp = new NDLine[proofArray.length + extraSpaces];
        
        if (indexOfBlank > indexOfAssEnd) {
            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (resource.getFirstArg().equals(goal.getLine())
                    && resource.getContext().equals(goal.getContext())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getFirstArg(), 3);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineOne = temp[k];
                assEndOne = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(resource.getFirstArg(), 1);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineOne = temp[k];
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getLine(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEndOne = temp[k];
                k++;
            }
            
            if (resource.getSecondArg().equals(goal.getLine())
                    && resource.getContext().equals(goal.getContext())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getSecondArg(), 3);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineTwo = temp[k];
                assEndTwo = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(resource.getSecondArg(), 1);
                assLineTwo = temp[k];
                temp[k].setContext(getResourceContext(goal, resource));
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getLine(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEndTwo = temp[k];
                k++;
            }
                

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
        } else {
            int k = 0;
            for (int j = 0; j < indexOfAssEnd + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            if (resource.getFirstArg().equals(goal.getLine())
                    && resource.getContext().equals(goal.getContext())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getFirstArg(), 3);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineOne = temp[k];
                assEndOne = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(resource.getFirstArg(), 1);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineOne = temp[k];
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getLine(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEndOne = temp[k];
                k++;
            }
            
            if (resource.getSecondArg().equals(goal.getLine())
                    && resource.getContext().equals(goal.getContext())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getSecondArg(), 3);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineTwo = temp[k];
                assEndTwo = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(resource.getSecondArg(), 1);
                temp[k].setContext(getResourceContext(goal, resource));
                assLineTwo = temp[k];
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getLine(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEndTwo = temp[k];
                k++;
            }

            for (int j = indexOfAssEnd + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
        }
        
        
        NDJust just = new JustDisElim(resource, assLineOne, assEndOne, assLineTwo, assEndTwo);
        goal.setJustification(just);
        proofArray = temp;

        collapseBlanks();

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
     */
    public NDLine[] disIntro(NDLine goal, NDLine resource) {
        // First, check to see if either disjunct appears in scope
        NDLine firstJustLine = checkForNDLine(findRegEx("", goal.getFirstArg()), goal);
        NDLine secondJustLine = checkForNDLine(findRegEx("", goal.getSecondArg()), goal);
        
        
        if (!Globals.requireDisSelect && firstJustLine != null) { // If the first disjunct is found
            goal.setJustification(new JustSingle(JustSingle.DIS_INTRO, firstJustLine));
            Globals.currentGoalIndex = -1;
        } else if (!Globals.requireDisSelect && secondJustLine != null) {
            goal.setJustification(new JustSingle(JustSingle.DIS_INTRO, secondJustLine));
            Globals.currentGoalIndex = -1;
        } else if ((goal.getFirstArg().equals(resource.getLine()) || goal.getSecondArg().equals(resource.getLine())) && goal.isInScopeOf(resource, proofArray)
                && goal.getIsSameContextAs(resource)) { // If the goal is one of the disjuncts
            goal.setJustification(new JustSingle(JustSingle.DIS_INTRO, resource));
            Globals.currentGoalIndex = -1;
        }  else {
            magicMode = false; // turn off magic mode
            boolean chooseFirstDisjunct;

            // Get user input. Ask whether they wish to choose goal.getFirstArg() or goal.getSecondArg()
            Object[] options = {goal.parseFirstArg(), goal.parseSecondArg(), "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "Would you like to justify from " + options[0] + " or " + options[1] + "?",
                    "Disjunction Introduction", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (n == 0) {
                chooseFirstDisjunct = true;
            } else if (n == 1) {
                chooseFirstDisjunct = false;
            } else {
                Globals.reverseUndo = true;
                return proofArray;
            }

            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            if (chooseFirstDisjunct) {
                temp[k] = new NDLine(goal.getFirstArg());
                temp[k].setContext(getGoalContext(goal, resource));
            } else {
                temp[k] = new NDLine(goal.getSecondArg());
                temp[k].setContext(getGoalContext(goal, resource));
            }
            resource = temp[k];
            Globals.currentGoalIndex = k;

            k++;
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            goal.setJustification(new JustSingle(JustSingle.DIS_INTRO, resource));

            proofArray = temp;
        }

        collapseBlanks();

        Globals.rulesUsed.add("disIntro");
        return proofArray;
    }

    /**
     * Applies implication elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] impElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        if (resource.getMainOp().equals("qa")) {
            proofArray = universalsImpElim(goal, resource);
            Globals.rulesUsed.add("impElim");
            return proofArray;
        }
        
        
        NDLine antecedentLine = checkForNDLine(findRegEx("", resource.getFirstArg()), goal, resource.getContext());
        NDLine resourceLine = resource;
        

        if (antecedentLine != null && goal.getLine().equals(resource.getSecondArg())
                && goal.getIsSameContextAs(resource)) { // If the antecedent is in the resources and the consequent = the goal
            goal.setJustification(new JustDouble(JustDouble.IMP_ELIM, resourceLine, antecedentLine));
            Globals.currentGoalIndex = -1;
            
        } else if (antecedentLine != null && 
                (!goal.getLine().equals(resource.getSecondArg()) || !goal.getIsSameContextAs(resource))) { // If the antecedent is in the resources but the consequent is not the goal
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            // Expand proofArray to handle one new resource (the consequent of the resource), and justify it
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getSecondArg());
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.IMP_ELIM, resourceLine, antecedentLine));
            Globals.currentResourceIndex = k;

            k++;
            for (int j = indexOfBlank; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        } else if (antecedentLine == null && goal.getLine().equals(resource.getSecondArg())
                && goal.getIsSameContextAs(resource)) { // If the antecedent is not in the resources but the consequent IS the goal
            int indexOfGoal = goal.indexIn(proofArray);
            NDLine[] temp = new NDLine[proofArray.length + 1];
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            antecedentLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            goal.setJustification(new JustDouble(JustDouble.IMP_ELIM, resourceLine, antecedentLine));

            proofArray = temp;
        } else if (antecedentLine == null && 
                (!goal.getLine().equals(resource.getSecondArg()) || !goal.getIsSameContextAs(resource))) { // If the antecedent is not in the resources and the consequent is not the goal
            if (magicMode) {
                magicMode = false; // turn off magic mode
                return proofArray;
            }
            
            
            int indexOfGoal = goal.indexIn(proofArray);
            NDLine[] temp = new NDLine[proofArray.length + 3];
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            antecedentLine = temp[k];
            k++;

            temp[k] = new NDLine(resource.getSecondArg());
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.IMP_ELIM, resourceLine, antecedentLine));
            k++;

            temp[k] = new NDLine(5);
            k++;

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            Globals.currentGoalIndex = goal.indexIn(proofArray);
            proofArray = temp;
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("impElim");
        return proofArray;
    }

    /**
     * Applies implication introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] impIntro(NDLine goal, NDLine resource) {
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        int indexOfAssEnd = findIndexOfAssEnd(indexOfGoal);
        
        NDLine assLine;
        NDLine assEnd;
        int extraSpaces;
        
        
        if (indexOfBlank > indexOfAssEnd) {
            extraSpaces = 2;
        } else {
            extraSpaces = 3;
        }
        NDLine[] temp = new NDLine[proofArray.length + extraSpaces];
        
        if (indexOfBlank > indexOfAssEnd) {
            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (goal.getFirstArg().equals(goal.getSecondArg())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getFirstArg(), 3);
                temp[k].setContext(getGoalContext(goal, resource));
                assLine = temp[k];
                assEnd = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(goal.getFirstArg(), 1);
                temp[k].setContext(getGoalContext(goal, resource));
                assLine = temp[k];
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getSecondArg(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEnd = temp[k];
                Globals.currentGoalIndex = k;
                k++;
            }


            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
        } else {
            int k = 0;
            for (int j = 0; j < indexOfAssEnd + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            if (goal.getFirstArg().equals(goal.getSecondArg())) {
                NDLine[] temporary = new NDLine[temp.length - 2];
                for (int i = 0; i< k; i++) {
                    temporary[i] = temp[i];
                }
                temp = temporary;
                
                temp[k] = new NDLine(resource.getFirstArg(), 3);
                temp[k].setContext(getGoalContext(goal, resource));
                assLine = temp[k];
                assEnd = temp[k];
                k++;
            } else {
                temp[k] = new NDLine(goal.getFirstArg(), 1);
                temp[k].setContext(getGoalContext(goal, resource));
                assLine = temp[k];
                k++;
                temp[k] = proofArray[indexOfBlank];
                k++;
                temp[k] = new NDLine(goal.getSecondArg(), 2);
                temp[k].setContext(getGoalContext(goal, resource));
                assEnd = temp[k];
                Globals.currentGoalIndex = k;
                k++;
            }

            for (int j = indexOfAssEnd + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
        }

        goal.setJustification(new JustDouble(JustDouble.IMP_INTRO, assLine, assEnd));

        proofArray = temp;

        collapseBlanks();
        Globals.rulesUsed.add("impIntro");
        return proofArray;
    }

    /**
     * Applies equivalence elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] equElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        if (resource.getMainOp().equals("qa")) {
            proofArray = universalsEquElim(goal, resource);
            Globals.rulesUsed.add("equElim");
            return proofArray;
        }
        
        NDLine leftSideLine = checkForNDLine(findRegEx("", resource.getFirstArg()), goal, resource.getContext());
        NDLine rightSideLine = checkForNDLine(findRegEx("", resource.getSecondArg()), goal, resource.getContext());
//        System.out.println(findRegEx("", resource.getFirstArg()));
//        System.out.println(leftSideLineNum);

        if (leftSideLine != null && goal.getLine().equals(resource.getSecondArg())
                && goal.getIsSameContextAs(resource)) { // If the left side is in the resources and the right side = the goal
            goal.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
            Globals.currentGoalIndex = -1;
            
        } else if (rightSideLine != null && goal.getLine().equals(resource.getFirstArg())
                && goal.getIsSameContextAs(resource)) { // If the right side is in the resources and the left side = the goal
            goal.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
            Globals.currentGoalIndex = -1;
            
        } else if (leftSideLine != null && 
                (!goal.getLine().equals(resource.getSecondArg()) || !goal.getIsSameContextAs(resource))) { // If the left side is in the resources but the right side is not the goal
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            // Expand proofArray to handle one new resource (the right side of the resource), and justify it
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getSecondArg());
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
            Globals.currentResourceIndex = k;

            k++;
            for (int j = indexOfBlank; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        } else if (rightSideLine != null && 
                (!goal.getLine().equals(resource.getFirstArg()) || !goal.getIsSameContextAs(resource))) { // If the right side is in the resources but the left side is not the goal
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            // Expand proofArray to handle one new resource (the left side of the resource), and justify it
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
            Globals.currentResourceIndex = k;

            k++;
            for (int j = indexOfBlank; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        } else if (leftSideLine == null && goal.getLine().equals(resource.getSecondArg())
                && goal.getIsSameContextAs(resource)) { // If the left side is not found in the resources but the right side IS the goal
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            leftSideLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            goal.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
            

            proofArray = temp;
        } else if (rightSideLine == null && goal.getLine().equals(resource.getFirstArg())
                && goal.getIsSameContextAs(resource)) { // If the right side is not found in the resources but the left side IS the goal
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            temp[k] = new NDLine(resource.getSecondArg());
            temp[k].setContext(getResourceContext(goal, resource));
            rightSideLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;
            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            goal.setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));

            proofArray = temp;
        } else { // Otherwise. i.e. If neither the right nor left side is found in the resources and neither is the goal.
            magicMode = false; // turn off magic mode
            
            boolean leftToRight;

            Object[] options = {"Left-to-right", "Right-to-left", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "You are about to make a bold step. \n Would you like to move left-to-right or right-to-left?",
                    "Equivalence Elimination - Bold Step", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (n == 0) {
                leftToRight = true;
            } else if (n == 1) {
                leftToRight = false;
            } else {
                Globals.reverseUndo = true;
                return proofArray;
            }

            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);

            NDLine[] temp = new NDLine[proofArray.length + 3];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            if (leftToRight) {
                temp[k] = new NDLine(resource.getFirstArg());
                temp[k].setContext(getResourceContext(goal, resource));
                leftSideLine = temp[k];
                k++;

                temp[k] = new NDLine(resource.getSecondArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, leftSideLine));
                k++;
            } else {
                temp[k] = new NDLine(resource.getSecondArg());
                temp[k].setContext(getResourceContext(goal, resource));
                rightSideLine = temp[k];
                k++;

                temp[k] = new NDLine(resource.getFirstArg());
                temp[k].setContext(getResourceContext(goal, resource));
                temp[k].setJustification(new JustDouble(JustDouble.EQU_ELIM, resource, rightSideLine));
                k++;
            }
            temp[k] = new NDLine(5);
            k++;

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            proofArray = temp;
            Globals.currentGoalIndex = goal.indexIn(proofArray);
        }
        
        collapseBlanks();
        Globals.rulesUsed.add("equElim");
        return proofArray;
    }

    /**
     * Applies equivalence introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] equIntro(NDLine goal, NDLine resource) {
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        
        if (Globals.allowedRules.get("equIdentityBoxes")) {
            boolean createAnIdBox;
            
            Object[] options = {"Create " + "\u2261" +" identity box", "Standard " + "\u2261" + "I", "Cancel"};
            int n = JOptionPane.showOptionDialog(Globals.frame, "You have selected \u2261" + "I under proof system NK=.\n" + "Create an identity box or standard \u2261" + "I?",
                    "Equivalence Introduction - NK=", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (n == 0) {
                createAnIdBox = true;
            } else if (n == 1) {
                createAnIdBox = false;
            } else {
                Globals.reverseUndo = true;
                return proofArray;
            }
            
            if (createAnIdBox) {
                NDLine idStart;

                NDLine[] temp = new NDLine[proofArray.length + 2];

                int k = 0;
                for (int i = 0; i < indexOfBlank; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }

                temp[k] = new NDLine(goal.getFirstArg(), 10);
                temp[k].setContext(getGoalContext(goal, resource));
                idStart = temp[k];
                k++;

                temp[k] = proofArray[indexOfBlank];
                k++;

                temp[k] = new NDLine(goal.getSecondArg(), 9);
                temp[k].setContext(getGoalContext(goal, resource));
                Globals.currentGoalIndex = k;
                k++;

                for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }

                goal.setJustification(new JustSingle(JustSingle.ID_BOX_INTRO, idStart));

                proofArray = temp;
                collapseBlanks();
                Globals.rulesUsed.add("idBoxEquIntro");
                return proofArray;
            }
        }
        
        int indexOfAssEnd = findIndexOfAssEnd(indexOfGoal);
        
        NDLine assLineOne;
        NDLine assEndOne;
        NDLine assLineTwo;
        NDLine assEndTwo;
        int extraSpaces;
        
        if (indexOfBlank > indexOfAssEnd) {
            extraSpaces = 5;
        } else {
            extraSpaces = 6;
        }
        NDLine[] temp = new NDLine[proofArray.length + extraSpaces];
        

        int k = 0;
        for (int j = 0; j < indexOfBlank; j++) {
            temp[k] = proofArray[j];
            k++;
        }

        if (goal.getFirstArg().equals(goal.getSecondArg())) {
            NDLine[] temporary = new NDLine[temp.length - 4];
            for (int i = 0; i< k; i++) {
                temporary[i] = temp[i];
            }
            temp = temporary;

            temp[k] = new NDLine(goal.getFirstArg(), 3);
            temp[k].setContext(getGoalContext(goal, resource));
            assLineOne = temp[k];
            assEndOne = temp[k];
            k++;

            temp[k] = new NDLine(goal.getSecondArg(), 3);
            temp[k].setContext(getGoalContext(goal, resource));
            assLineTwo = temp[k];
            assEndTwo = temp[k];
            k++;
        } else {
            temp[k] = new NDLine(goal.getFirstArg(), 1);
            temp[k].setContext(getGoalContext(goal, resource));
            assLineOne = temp[k];
            k++;
            temp[k] = proofArray[indexOfBlank];
            k++;
            temp[k] = new NDLine(goal.getSecondArg(), 2);
            temp[k].setContext(getGoalContext(goal, resource));
            assEndOne = temp[k];
            k++;

            temp[k] = new NDLine(goal.getSecondArg(), 1);
            temp[k].setContext(getGoalContext(goal, resource));
            assLineTwo = temp[k];
            k++;
            temp[k] = proofArray[indexOfBlank];
            k++;
            temp[k] = new NDLine(goal.getFirstArg(), 2);
            temp[k].setContext(getGoalContext(goal, resource));
            assEndTwo = temp[k];
            k++;
        }



        for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
            temp[k] = proofArray[j];
            k++;
        }
        

        NDJust just = new JustEquIntro(assLineOne, assEndOne, assLineTwo, assEndTwo);
        goal.setJustification(just);

        proofArray = temp;

        collapseBlanks();
        Globals.currentGoalIndex = -1;
        Globals.rulesUsed.add("equIntro");
        return proofArray;
    }

    /**
     * Applies negation elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] negElim(NDLine goal, NDLine resource) { // When we have a negation selected as a resource
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("negElim");
            return universalsNegElim(goal, resource);
        }
        String prop = resource.getFirstArg(); // The opposite of the negation. If the negation is ~p, this is p
        NDLine propLine = checkForNDLine(findRegEx("", prop), goal, resource.getContext());

        if (propLine != null && goal.getLine().equals("\\falsum")
                && goal.getIsSameContextAs(resource)) { // If prop is in the resources and the goal is falsum
//            System.out.println("" + resource.getJustLineNum());
            goal.setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, propLine));
            Globals.currentGoalIndex = -1;
            
        } else if (propLine != null && 
                (!goal.getLine().equals("\\falsum") || !goal.getIsSameContextAs(resource))) { // If prop is in the resources but the goal is not falsum
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 1];
            NDLine falsumLine;

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            temp[k] = new NDLine("\\falsum");
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, propLine));
            falsumLine= temp[k];
            k++;

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (goal.getIsSameContextAs(resource)) {
                goal.setJustification(new JustSingle(JustSingle.FALSUM_ELIM, falsumLine));
                Globals.currentGoalIndex = -1;
            } else {
                Globals.currentGoalIndex = goal.indexIn(temp);
            }

            proofArray = temp;
            
        } else if (propLine == null && goal.getLine().equals("\\falsum")
                && goal.getIsSameContextAs(resource)) { // If prop is not in the resources but the goal IS falsum
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            propLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            goal.setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, propLine));
            

            proofArray = temp;
            

        } else if (propLine == null && 
                (!goal.getLine().equals("\\falsum") || !goal.getIsSameContextAs(resource))) { // If prop is not in the resources and the goal is not falsum
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 2];
            NDLine falsumLine;

            int k = 0;
            for (int j = 0; j < indexOfBlank + 1; j++) {
                temp[k] = proofArray[j];
                k++;
            }

            temp[k] = new NDLine(resource.getFirstArg());
            temp[k].setContext(getResourceContext(goal, resource));
            propLine = temp[k];
            if (goal.getIsSameContextAs(resource)){
                Globals.currentGoalIndex = k;
            }
            k++;
            temp[k] = new NDLine("\\falsum");
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustDouble(JustDouble.NEG_ELIM, resource, propLine));
            falsumLine = temp[k];
            k++;

            for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
                temp[k] = proofArray[j];
                k++;
            }
            
            if (goal.getIsSameContextAs(resource)) {
                goal.setJustification(new JustSingle(JustSingle.FALSUM_ELIM, falsumLine));
            } else {
                Globals.currentGoalIndex = goal.indexIn(temp);
            }

            proofArray = temp;
            

        }
        
        collapseBlanks();
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
     */
    public NDLine[] negIntro(NDLine goal, NDLine resource) {
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        int indexOfAssEnd = findIndexOfAssEnd(indexOfGoal);
        NDLine assLine;
        NDLine assEnd;
        int extraSpaces;
        
        if (indexOfBlank > indexOfAssEnd) {
            extraSpaces = 2;
        } else {
            extraSpaces = 3;
        }
        NDLine[] temp = new NDLine[proofArray.length + extraSpaces];
        
        
        int k = 0;
        for (int j = 0; j < indexOfBlank; j++) {
            temp[k] = proofArray[j];
            k++;
        }

        temp[k] = new NDLine(goal.getFirstArg(), 1);
        temp[k].setContext(getGoalContext(goal, resource));
        assLine = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        temp[k] = proofArray[indexOfBlank];
        k++;
        temp[k] = new NDLine("\\falsum", 2);
        temp[k].setContext(getGoalContext(goal, resource));
        assEnd = temp[k];
        Globals.currentGoalIndex = k;
        k++;

        for (int j = indexOfBlank + 1; j < proofArray.length; j++) {
            temp[k] = proofArray[j];
            k++;
        }
        

        goal.setJustification(new JustDouble(JustDouble.NEG_INTRO, assLine, assEnd));

        proofArray = temp;

        collapseBlanks();
        Globals.rulesUsed.add("negIntro");
        return proofArray;
    }

    /**
     * Applies universal elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] qaElim(NDLine goal, NDLine resource) {
        if (Globals.allowedRules.get("universalsShortcuts") && goal.getLine().matches(resource.getNonUniRegEx())
                && goal.getIsSameContextAs(resource)) {
            goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
            Globals.currentGoalIndex = -1;
            collapseBlanks();
            Globals.rulesUsed.add("qaElim");
            return proofArray;
        }
        
//        System.out.println("first: " + resource.getFirstArg());
//        System.out.println("second: " + resource.getSecondArg());
        String regEx = findRegEx(resource.getFirstArg(), resource.getSecondArg());
//        System.out.println(goal.getLine() +" - " + regEx);
        if (goal.getLine().matches(regEx) && goal.getIsSameContextAs(resource)) {
//            System.out.println(goal.getLine() + regEx);
            goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
            Globals.currentGoalIndex = -1;
        } else {
            magicMode = false; // turn off magic mode
            
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            
            // If we're in an identity box, put the result above the box
            boolean inIdBox = goal.getType() > 6 && goal.getType() < 11;
            if (inIdBox && findTopOfIdBox(indexOfGoal) != 0) {
                indexOfBlank = findTopOfIdBox(indexOfGoal);
            }
            
            NDLine[] temp = new NDLine[proofArray.length + 1];
            String term;
            if (Globals.allowedRules.get("secondOrder")) {
                term = MyOptionPane.showFriendlyLineInputDialog("Universal Elimination (Second-Order Logic)").replace("\\", "\\\\");
            } else {
                term = (String)JOptionPane.showInputDialog(Globals.frame, "The goal does not match.\nPlease input a term", "Universal Elimination", JOptionPane.PLAIN_MESSAGE, null, null, "a");
            }
            
            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            } else if (resource.replace(resource.getSecondArg(), resource.getFirstArg(), term).equals(goal.getLine())
                    && goal.getIsSameContextAs(resource)) {
                goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
                collapseBlanks();
                Globals.rulesUsed.add("qaElim");
                return proofArray;
            }
            
            int k = 0;
            
            for (int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(resource.replace(resource.getSecondArg(), resource.getFirstArg(), term));
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
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
        Globals.rulesUsed.add("qaElim");
        return proofArray;
    }
    
    /**
     * NOT IMPLEMENTED! Applies universal elimination to the current proofArray, using second order logic. NOT IMPLEMENTED!
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] qaElimProp(NDLine goal, NDLine resource) {
        if (Globals.allowedRules.get("universalsShortcuts") && goal.getLine().matches(resource.getNonUniRegEx())
                && goal.getIsSameContextAs(resource)) {
            goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
            Globals.currentGoalIndex = -1;
            collapseBlanks();
            Globals.rulesUsed.add("qaElim");
            return proofArray;
        }
        
        String regEx = findRegExProp(resource.getFirstArg(), resource.getSecondArg());
        if (goal.getLine().matches(regEx) && goal.getIsSameContextAs(resource)) {
            goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
            Globals.currentGoalIndex = -1;
        } else {
            magicMode = false; // turn off magic mode
            
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            
            // If we're in an identity box, put the result above the box
            boolean inIdBox = goal.getType() > 6 && goal.getType() < 11;
            if (inIdBox && findTopOfIdBox(indexOfGoal) != 0) {
                indexOfBlank = findTopOfIdBox(indexOfGoal);
            }
            
            NDLine[] temp = new NDLine[proofArray.length + 1];
            String term = (String)JOptionPane.showInputDialog(Globals.frame, "The goal does not match.\nPlease input a proposition", "LEM Elimination", JOptionPane.PLAIN_MESSAGE, null, null, "a");
            
            if (term == null) {
                Globals.reverseUndo = true;
                return proofArray;
            } else if (resource.replace(resource.getSecondArg(), resource.getFirstArg(), term).equals(goal.getLine())
                    && goal.getIsSameContextAs(resource)) {
                goal.setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
                collapseBlanks();
                Globals.rulesUsed.add("qaElim");
                return proofArray;
            }
            
            int k = 0;
            
            for (int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(resource.replace(resource.getSecondArg(), resource.getFirstArg(), term));
            temp[k].setContext(getResourceContext(goal, resource));
            temp[k].setJustification(new JustSingle(JustSingle.QA_ELIM, resource));
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
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine[] temp = new NDLine[proofArray.length + 1];
        boolean allowable;
        String term;
        
        if (Globals.allowedRules.get("autoParameters")) {
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getFirstArg().equals("p") || resource.getFirstArg().equals("q") || resource.getFirstArg().equals("r"))) {
                term = Globals.terms.getNewProposition();
            } else {
                term = Globals.terms.getNewTerm();
            }
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getFirstArg().equals("p") || resource.getFirstArg().equals("q") || resource.getFirstArg().equals("r"))) {
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

        temp[k] = new NDLine(goal.replace(goal.getSecondArg(), goal.getFirstArg(), term));
        temp[k].setContext(getGoalContext(goal, resource));
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
                    && (resource.getFirstArg().equals("p") || resource.getFirstArg().equals("q") || resource.getFirstArg().equals("r"))) {
                term = Globals.terms.getNewProposition();
            } else {
                term = Globals.terms.getNewTerm();
            }
            allowable = true;
        } else {
            magicMode = false; // turn off magic mode
            if (Globals.allowedRules.get("secondOrder")
                    && (resource.getFirstArg().equals("p") || resource.getFirstArg().equals("q") || resource.getFirstArg().equals("r"))) {
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
        
        temp[k] = new NDLine(resource.replace(resource.getSecondArg(), resource.getFirstArg(), term), 1);
        temp[k].setContext(getResourceContext(goal, resource));
        assStartLine = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.getLine(), 2);
        temp[k].setContext(getGoalContext(goal, resource));
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
        NDLine matchLine = checkForNDLine(findRegEx(goal.getFirstArg(), goal.getSecondArg()), goal);
        
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
            temp[k] = new NDLine(goal.replace(goal.getSecondArg(), goal.getFirstArg(), term));
            temp[k].setContext(getGoalContext(goal, resource));
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
     */
    public NDLine[] falsumElim(NDLine goal, NDLine resource) {
        goal.setJustification(new JustSingle(JustSingle.FALSUM_ELIM, resource));
        collapseBlanks();
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
        
        String firstRegEx = resource.getNonUniFirstArgRegEx();
//        System.out.println("regEx: " + firstRegEx);
        
        if (goal.getLine().equals("\\falsum") && goal.getIsSameContextAs(resource)) {
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
    
    private NDLine[] universalsImpElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonUniFirstArgRegEx();
        String secondRegEx = resource.getNonUniSecondArgRegEx();

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
        Pattern wholePattern = Pattern.compile(resource.getNonUniRegEx());
        
        
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
                        if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            
                            if (antecedent.getLine().matches(test.getNonUniFirstArgRegEx())) { // If the firstArg of test matches
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
                    if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.getLine().matches(test.getNonUniSecondArgRegEx())) { // If the secondArg of test matches
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
    
    private NDLine[] universalsEquElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonUniFirstArgRegEx();
        String secondRegEx = resource.getNonUniSecondArgRegEx();

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
                        if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            if (antecedent.getLine().matches(test.getNonUniFirstArgRegEx())) { // If the firstArg of test matches
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
                        if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                            matchedArray.remove(matchedArray.get(i)); //               remove it
                        } else { // Otherwise, check if test works with any variables
                            test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
                            if (consequent.getLine().matches(test.getNonUniSecondArgRegEx())) { // If the firstArg of test matches
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
                    if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.getLine().matches(test.getNonUniSecondArgRegEx())) { // If the secondArg of test matches
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
                    if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.getLine().matches(test.getNonUniFirstArgRegEx())) { // If the secondArg of test matches
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
            String first = possibleLines.get(q).getFirstArg();
            String second = possibleLines.get(q).getSecondArg();
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println(lineR.getLine());
//                System.out.println(lineAtQ);
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getFirstArg().equals(second) && lineR.getSecondArg().equals(first)) {
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
        if (!goal.getIsSameContextAs(resource)) {
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "These lines have different contexts!");
            return proofArray;
        }
        
        // 1. Find the unquantified expression //
        NDLine tempLine = new NDLine(5);
        // Find regular expressions for the arguments
        String firstRegEx = resource.getNonUniFirstArgRegEx();
        String secondRegEx = resource.getNonUniSecondArgRegEx();

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
                if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                    matchedArray.remove(matchedArray.get(i)); //               remove it
                } else { // Otherwise, check if test works with any variables
                    test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
                    if (otherIdBoxLine.matches(test.getNonUniFirstArgRegEx())) { // If the firstArg of test matches
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
                if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                    matchedArray.remove(matchedArray.get(i)); //               remove it
                } else { // Otherwise, check if test works with any variables
                    test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
                    if (otherIdBoxLine.matches(test.getNonUniSecondArgRegEx())) { // If the firstArg of test matches
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
                    if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.getLine().matches(test.getNonUniSecondArgRegEx())) { // If the secondArg of test matches
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
                    if (tempLine.getFirstArg().contains(matchedArray.get(i))) { // If the variable isn't in the firstArg of tempLine
                        matchedArray.remove(matchedArray.get(i)); //               remove it
                    } else { // Otherwise, check if test works with any variables
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
                        if (goal.getLine().matches(test.getNonUniFirstArgRegEx())) { // If the firstArg of test matches
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
            String first = possibleLines.get(q).getFirstArg();
            String second = possibleLines.get(q).getSecondArg();
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getFirstArg().equals(second) && lineR.getSecondArg().equals(first)) {
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
    
    private NDLine[] universalsEqElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
        if (!goal.getIsSameContextAs(resource)) {
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
        
        String firstRegEx = resource.getNonUniFirstArgRegEx();
        String secondRegEx = resource.getNonUniSecondArgRegEx();
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
                    && resource.getIsSameContextAs(proofArray[k])) {
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
                            test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
    //                        System.out.println("     3. test is " + test.getLine());
                            Matcher attempt = Pattern.compile(test.getNonUniFirstArgRegEx()).matcher(proofArray[k].getLine());
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
                    && goal.getIsSameContextAs(resource)) {
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
                            test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                            test.setContext(tempLine.getContext());
    //                        System.out.println("     3. test is " + test.getLine());
                            Matcher attempt = Pattern.compile(test.getNonUniSecondArgRegEx()).matcher(proofArray[k].getLine());
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
        

        if (goal.getIsSameContextAs(resource)) {
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
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
        //                System.out.println("     3. test is " + test.getLine());
                        Matcher attempt = Pattern.compile(test.getNonUniFirstArgRegEx()).matcher(goal.getLine());
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
                        test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                        test.setContext(tempLine.getContext());
        //                System.out.println("     3. test is " + test.getLine());
                        Matcher attempt = Pattern.compile(test.getNonUniSecondArgRegEx()).matcher(goal.getLine());
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
            String first = possibleLines.get(q).getFirstArg();
            String second = possibleLines.get(q).getSecondArg();
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println("== Match? ==");
//                System.out.println("q " + lineAtQ);
//                System.out.println("r " + lineR.getLine());
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getFirstArg().equals(second) && lineR.getSecondArg().equals(first)) {
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
        if (!goal.getIsSameContextAs(resource)) {
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
        
        String firstRegEx = resource.getNonUniFirstArgRegEx();
        String secondRegEx = resource.getNonUniSecondArgRegEx();
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
//                test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
//                System.out.println("test is " + test.getLine());
//                Matcher attempt = Pattern.compile(test.getNonUniFirstArgRegEx()).matcher(otherIdBoxLine);
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
//                test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
//                System.out.println("test is " + test.getLine());
//                Matcher attempt = Pattern.compile(test.getNonUniSecondArgRegEx()).matcher(otherIdBoxLine);
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
                    test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
//                    System.out.println("     3. test is " + test.getLine());
                    Matcher attempt = Pattern.compile(test.getNonUniFirstArgRegEx()).matcher(goal.getLine());
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
                    test = new NDLine(tempLine.replace(tempLine.getSecondArg(), tempLine.getFirstArg(), matchedArray.get(i)), false);
                    test.setContext(tempLine.getContext());
    //                System.out.println("     3. test is " + test.getLine());
                    Matcher attempt = Pattern.compile(test.getNonUniSecondArgRegEx()).matcher(goal.getLine());
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
            String first = possibleLines.get(q).getFirstArg();
            String second = possibleLines.get(q).getSecondArg();
            for (int r = q+1; r < possibleLines.size(); r++) {
                NDLine lineR = possibleLines.get(r);
//                System.out.println("== Match? ==");
//                System.out.println("q " + lineAtQ);
//                System.out.println("r " + lineR.getLine());
                if (lineR.getLine().equals(lineAtQ)) {
                    possibleLines.remove(r);
                    r--;
                } else if (lineR.getFirstArg().equals(second) && lineR.getSecondArg().equals(first)) {
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
                    && goal.getIsSameContextAs(proofArray[indexOfGoal-i])) { // Check line against what we want, but ignore if scopes>0
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
                    && resource.getIsSameContextAs(proofArray[indexOfGoal-i])) { // Check line against what we want, but ignore if scopes>0
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
        
    public NDLine[] doubleNegation(NDLine goal, NDLine resource) {

        
        NDLine dNJustLine = checkForNDLine(findRegEx("", "\\neg{\\neg{" + goal.getLine() + "}}"), goal);
        if (dNJustLine != null) {
            goal.setJustification(new JustSingle(JustSingle.DN_ELIM, dNJustLine));
            Globals.currentGoalIndex = -1;
        } else {

            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine[] temp = new NDLine[proofArray.length + 1];

            int k = 0;

            for (int i = 0; i < indexOfBlank + 1; i++) {
                temp[k] = proofArray[i];
                k++;
            }

            temp[k] = new NDLine("\\neg{\\neg{" + goal.getLine() + "}}");
            temp[k].setContext(getGoalContext(goal, resource));
            dNJustLine = temp[k];
            Globals.currentGoalIndex = k;
            k++;

            for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }

            proofArray = temp;
            goal.setJustification(new JustSingle(JustSingle.DN_ELIM, dNJustLine)); 
        }
        collapseBlanks();
        
        Globals.rulesUsed.add("doubleNegation");
        return proofArray;
    }
    
    // NK= Rules //
    
    /**
     * Applies identity introduction to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
        
    public NDLine[] eqIntro(NDLine goal, NDLine resource) {
        if (goal.getFirstArg().equals(goal.getSecondArg())) { // If the first arg is the second arg, justify
            goal.setJustification(new JustNone(JustNone.EQ_INTRO));
            Globals.currentGoalIndex = -1;
            Globals.rulesUsed.add("eqIntro");
        } else if (Globals.allowedRules.get("eqIdentityBoxes")) { // Otherwise, if we're using identity boxes
            int indexOfGoal = goal.indexIn(proofArray);
            int indexOfBlank = findIndexOfBlank(indexOfGoal);
            NDLine idStart;
            
            NDLine[] temp = new NDLine[proofArray.length + 2];
            
            int k = 0;
            for (int i = 0; i < indexOfBlank; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            temp[k] = new NDLine(goal.getFirstArg(), NDLine.ID_BOX_START);
            temp[k].setContext(getGoalContext(goal, resource));
            idStart = temp[k];
            k++;
            
            temp[k] = proofArray[indexOfBlank];
            k++;
            
            temp[k] = new NDLine(goal.getSecondArg(), NDLine.ID_BOX_END);
            temp[k].setContext(getGoalContext(goal, resource));
            Globals.currentGoalIndex = k;
            k++;
            
            for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
                temp[k] = proofArray[i];
                k++;
            }
            
            goal.setJustification(new JustSingle(JustSingle.ID_BOX_INTRO, idStart));
            
            proofArray = temp;
//            Globals.rulesUsed.add("idBoxEqIntro");
            Globals.rulesUsed.add("eqIntro");
        } else {
            magicMode = false;
        }
        
        collapseBlanks();
        return proofArray;
    }
    
    /**
     * Applies identity elimination to the current proofArray
     * @param goal the current goal
     * @param resource the current resource
     * @return The resulting proofArray.
     */
    public NDLine[] eqElim(NDLine goal, NDLine resource) throws LineNotInProofArrayException {
//        System.out.println("EQElim");
        if (resource.getMainOp().equals("qa")) {
            Globals.rulesUsed.add("eqElim");
            return universalsEqElim(goal, resource);
        }
        
        int indexOfGoal = goal.indexIn(proofArray);
        String leftSide = resource.getFirstArg();
        String rightSide = resource.getSecondArg();
        
        
        String leftSideParse = resource.parseFirstArg();
        String rightSideParse = resource.parseSecondArg();
        
        if (!goal.getLine().contains(leftSide) && !goal.getLine().contains(rightSide)) { // If the identity elimination doesn't apply at all, return the proof array
            magicMode = false; // turn off magic mode
            Globals.reverseUndo = true;
            JOptionPane.showMessageDialog(Globals.frame, "This rule is not applicable!");
            return proofArray;
        }
        if (!goal.getIsSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
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
                    temp[k].setContext(getGoalContext(goal, resource));
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
                        temp[k].setContext(getGoalContext(goal, resource));
                    }
                }
            } else {
                if (goal.getLine().indexOf(rightSide) == goal.getLine().lastIndexOf(rightSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), rightSide, leftSide));
                    temp[k].setContext(getGoalContext(goal, resource));
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
                        temp[k].setContext(getGoalContext(goal, resource));
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
        
        String leftSide = resource.getFirstArg();
        String rightSide = resource.getSecondArg();
        
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
        if (!goal.getIsSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
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
                temp[k].setContext(getGoalContext(goal, resource));
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setContext(getGoalContext(goal, resource));
            }
            Globals.currentGoalIndex = k;
            goal.setJustification(just);
            k++;
        } else {
            if (goLtR) {
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setContext(getGoalContext(goal, resource));
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setContext(getGoalContext(goal, resource));
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
        if (!goal.getIsSameContextAs(resource)) { // If the identity elimination doesn't apply at all, return the proof array
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
        
        String leftSide = resource.getFirstArg();
        String rightSide = resource.getSecondArg();
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
                
                temp[k] = new NDLine(resource.getSecondArg(), 8);
                temp[k].setContext(getResourceContext(goal, resource));
                Globals.currentGoalIndex = k;
                goal.setJustification(just);
                k++;
            } else {
                temp[k] = new NDLine(resource.getSecondArg(), 8);
                temp[k].setContext(getResourceContext(goal, resource));
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
                
                temp[k] = new NDLine(resource.getFirstArg(), 8);
                temp[k].setContext(getResourceContext(goal, resource));
                Globals.currentGoalIndex = k;
                goal.setJustification(just);
                k++;
            } else {
                temp[k] = new NDLine(resource.getFirstArg(), 8);
                temp[k].setContext(getResourceContext(goal, resource));
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
     */
        
    public NDLine[] induction(NDLine goal, NDLine resource) { // Used with a universally quantified goal. Resource is ignored, and is argument for uniformity
        String term = Globals.terms.getNewTerm();
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        
        NDLine zeroLine;
        NDLine assStartLine;
        NDLine assEndLine;
        
        NDLine[] temp = new NDLine[proofArray.length + 4];
        int k = 0;
        
        for (int i = 0; i <= indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        temp[k] = new NDLine(goal.replace(goal.getSecondArg(), goal.getFirstArg(), "0"));
        temp[k].setContext(getGoalContext(goal, resource));
        zeroLine = temp[k];
        Globals.currentGoalIndex = k;
        k++;
        
        temp[k] = new NDLine(goal.replace(goal.getSecondArg(), goal.getFirstArg(), term), 1);
        temp[k].setContext(getGoalContext(goal, resource));
        assStartLine = temp[k];
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.replace(goal.getSecondArg(), goal.getFirstArg(), "S" + term), 2);
        temp[k].setContext(getGoalContext(goal, resource));
        assEndLine = temp[k];
        k++;
        
        for (int i = indexOfBlank + 1; i < proofArray.length; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        goal.setJustification(new JustInduction(zeroLine, assStartLine, assEndLine));
        
        proofArray = temp;
        collapseBlanks();
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
        int indexOfGoal = goal.indexIn(proofArray);
        int indexOfBlank = findIndexOfBlank(indexOfGoal);
        NDLine toInsert = new NDLine(newLine);
        
        NDLine[] temp = new NDLine[proofArray.length + 2];
        
        if (goal.getType() > 6 && goal.getType() < 11) {
            indexOfBlank = findTopOfIdBox(indexOfGoal);
        }
        
        int k = 0;
        for (int i = 0; i < indexOfBlank; i++) {
            temp[k] = proofArray[i];
            k++;
        }
        
        temp[k] = new NDLine(5);
        k++;
        
        temp[k] = toInsert;
        k++;
        
        for (int i = indexOfBlank; i < proofArray.length; i++) {
//            System.out.println("k " + k);
//            System.out.println("temp.length " + temp.length);
            temp[k] = proofArray[i];
            k++;
        }
//        System.out.println("temp.length " + temp.length);
        proofArray = temp;
        Globals.currentGoalIndex = goal.indexIn(proofArray);
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
        
        temp[k] = new NDLine(goal.getFirstArg() + term, 1);
        temp[k].setContext(goal.getContext());
        assStart = temp[k];
        Globals.currentResourceIndex = k;
        k++;
        
        temp[k] = proofArray[indexOfBlank];
        k++;
        
        temp[k] = new NDLine(goal.getSecondArg(), 2);
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
        if (goal.getLine().equals(resource.getSecondArg())) {
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
        NDLine antecedentLine = checkForNDLine(findRegEx("",resource.getFirstArg() + "(" + term + ")"), goal, resource.getContext());
        
        
        if (antecedentLine != null) {
            NDJust just = new JustBoxElim(resource, antecedentLine);
            if (goal.getLine().equals(resource.getSecondArg()) && goal.getContext().equals(term)) {
                goal.setJustification(just);
                Globals.currentGoalIndex = -1;
            } else {
                NDLine[] temp = new NDLine[proofArray.length + 1];
                int k = 0;
                
                for (int i = 0; i < indexOfBlank; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getSecondArg());
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
            if (goal.getLine().equals(resource.getSecondArg()) && goal.getContext().equals(term)) {
                NDLine[] temp = new NDLine[proofArray.length + 1];
                int k = 0;
                
                for (int i = 0; i < indexOfBlank+1; i++) {
                    temp[k] = proofArray[i];
                    k++;
                }
                
                temp[k] = new NDLine(resource.getFirstArg() + term);
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
                
                temp[k] = new NDLine(resource.getFirstArg() + term);
                temp[k].setContext(resource.getContext());
                antecedentLine = temp[k];
                Globals.currentGoalIndex = k;
                k++;
                NDJust just = new JustBoxElim(resource, antecedentLine);
                
                temp[k] = new NDLine(resource.getSecondArg());
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
        ArrayList<Integer> matchingPredLines = checkForAllMatching(goal.getFirstArg() + "(\\([\\w\\(\\)]+\\))", goal);
        ArrayList<Integer> matchingPropLines = checkForAllMatchingIgnoreContext(goal.getNonUniSecondArgRegEx(), goal);
        
        NDLine matchingPredLine = null;
        NDLine matchingPropLine = null;
        
        Pattern pattern = Pattern.compile(goal.getFirstArg() + "(\\([\\w\\(\\)]+\\))");
        
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
            
            matchingPredLine = checkForNDLine(findRegEx("", goal.getFirstArg() + "(" + term + ")"), goal, goal.getContext());
            matchingPropLine = checkForNDLine(findRegEx("", goal.getSecondArg()), goal, term);
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
                    temp[k] = new NDLine(goal.getSecondArg());
                    temp[k].setContext(term);
                    matchingPropLine = temp[k];
                    k++;

                    temp[k] = new NDLine(5);
                    k++;
                }

                temp[k] = new NDLine(goal.getFirstArg() + term);
                temp[k].setContext(goal.getContext());
                matchingPredLine = temp[k];
                k++;

                if (!Globals.reverse2PremIntro) {
                    temp[k] = new NDLine(5);
                    k++;

                    temp[k] = new NDLine(goal.getSecondArg());
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

                temp[k] = new NDLine(goal.getFirstArg() + term);
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

                temp[k] = new NDLine(goal.getSecondArg());
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
        
        temp[k] = new NDLine(resource.getFirstArg() + term, 1);
        temp[k].setContext(resource.getContext());
        assStart1 = temp[k];
        k++;
        
        temp[k] = new NDLine(resource.getSecondArg());
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
        NDLine prop = checkForNDLine(findRegEx("",goal.getSecondArg()), goal, goal.getFirstArg());
        
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
            
            temp[k] = new NDLine(goal.getSecondArg());
            temp[k].setContext(goal.getFirstArg());
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
        if (goal.getContext().equals(resource.getFirstArg())
                && goal.getLine().equals(resource.getSecondArg())) {
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
            
            temp[k] = new NDLine(resource.getSecondArg());
            temp[k].setJustification(just);
            temp[k].setContext(resource.getFirstArg());
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
            temp[k].setContext(getGoalContext(goal, resource));
            idStart = temp[k];
            k++;
            
            temp[k] = proofArray[indexOfBlank];
            k++;
            
            temp[k] = new NDLine(goal.getLine(), 9);
            temp[k].setContext(getGoalContext(goal, resource));
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
        String leftSide = eqLine.getFirstArg();
        String rightSide = eqLine.getSecondArg();
        
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
                    temp[k].setContext(getGoalContext(goal, resource));
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
                    temp[k].setContext(getGoalContext(goal, resource));
                }
            } else {
                if (goal.getLine().indexOf(rightSide) == goal.getLine().lastIndexOf(rightSide)) { // If there's only one instance of leftSide in goal
                    temp[k] = new NDLine(goal.replace(goal.getLine(), rightSide, leftSide));
                    temp[k].setContext(getGoalContext(goal, resource));
                } else { //                                                                     // Otherwise, ask the user which instance(s) to replace
                    magicMode = false;
                    
                    String replacement = MyOptionPane.showTermSelectorDialog(goal.getLine(), rightSide, leftSide);
                    if (replacement.equals("")) {
                        Globals.reverseUndo = true;
                        return proofArray;
                    }
                    temp[k] = new NDLine(replacement);
                    temp[k].setContext(getGoalContext(goal, resource));
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
        
        String leftSide = eqLine.getFirstArg();
        String rightSide = eqLine.getSecondArg();
        
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
                temp[k].setContext(getGoalContext(goal, resource));
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setContext(getGoalContext(goal, resource));
            }
            Globals.currentGoalIndex = k;
            goal.setJustification(just);
            k++;
        } else {
            if (goLtR) {
                temp[k] = new NDLine(subLtR, 8);
                temp[k].setContext(getGoalContext(goal, resource));
            } else {
                temp[k] = new NDLine(subRtL, 8);
                temp[k].setContext(getGoalContext(goal, resource));
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
        
        String result = goal.replace(goal.getSecondArg(), goal.getFirstArg(), goal.getContext());
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
        String result = resource.replace(resource.getSecondArg(), resource.getFirstArg(), resource.getContext());
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
                && goal.getIsSameContextAs(resource)) {
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
     */
        
    public NDLine[] runMagicMode(int max) throws LineNotInProofArrayException { // Automatically expend all possible automatic rules from the current goal and then from the current resource
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
    private void mMIntroActions(String op, NDLine goal, NDLine resource) {
        
        
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
    
    private void mMElimActions(String op, NDLine goal, NDLine resource) throws LineNotInProofArrayException {
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
