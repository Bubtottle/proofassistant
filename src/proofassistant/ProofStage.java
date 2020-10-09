/*
 * The MIT License
 *
 * Copyright 2020 Declan Thompson.
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

/**
 * The ProofStage class holds an array of NDLines, and contains various mutator
 methods for this array.
 * This class supersedes aspects of the earlier ProofMethods classes (now called
 * ProofObject).
 *
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class ProofStage {
    private NDLine[] proofArray;
    
    // Metrics about the proofArray
    private int longestLineLength = 0;
    private int deepestAss = 0;
    
    
    /**
     * Class constructor
     * 
     * @param newArray An NDLine[] to be used as the proofArray
     */
    public ProofStage(NDLine[] newArray) {
        proofArray = newArray;
        updateMetrics();
    }
    
    // ACCESSOR AND PRINTING METHODS
    
    /**
     * Accessor method returning the current proofArray
     * 
     * @return An NDLine[], constituting the current state of the proofArray
     */
    public NDLine[] getProofArray() {
        return proofArray;
    }
    
    /**
     * Parses and prints the proofArray
     */
    public void printProofArray() {
        System.out.println("");
        int scopes = 0;
        int type;
        for (int i = 0; i < proofArray.length; i++) {
            type = proofArray[i].getType();
            if (type == NDLine.ASS_START || type == NDLine.ASS_ONE_LINE) {
                scopes++;
            }
            for (int j = 0; j < scopes; j++) {
                System.out.print("|");
            }
            if (type == NDLine.ASS_END || type == NDLine.ASS_ONE_LINE) {
                scopes--;
            }
            if (type == NDLine.BLANK) {
                System.out.println("");
            } else {
                System.out.println(proofArray[i].getJustLineNum() + ".  " 
                        + proofArray[i].parseLine() + "    " 
                        + proofArray[i].getJustification().getJava());
            }
        }
        System.out.println("");
        System.out.println("--------------------");
    }
    
    /**
     * Prints the lines of the proofArray in TeX format
     */
    public void printProofArrayLines() {
        System.out.println("Code");
        for (int i = 0; i < proofArray.length; i++) {
            System.out.println(proofArray[i].getLine());
        }
    }
    
    /**
     * Returns a string containing the TeX code for displaying the current proofArray.
     * @return String
     */
    public String getTeXCodeString() {
        String theCode = "";
        theCode = theCode + "\\begin{NDProof}[jdistance = "+ (int)(0.8*longestLineLength) + "em + 1]\n";
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
     * Returns a formatted unicode string displaying the current proofArray
     * 
     * @return A String encoding the current proofArray as unicode
     */
    public String getPlainTextString() {
        String theCode = "";
        int numAssumptions = 0;
        boolean inId = false; // Are we in an identity box?
        
        int lineLength;
        int lineType;
        
        
        for (int i = 0; i < proofArray.length; i++) {
            // Prepare for line input            
            lineLength = proofArray[i].getLength();
            lineType = proofArray[i].getType();
            
            // If we're starting an identity box, draw the top of the frame
            if (lineType == NDLine.ID_BOX_START) {
                for (int j = 0; j < numAssumptions; j++) {
                    theCode += "\u2502"; // Add | for assumption lines
                }
                // Add a space if we need double digits
                if (proofArray.length > 9) theCode += " ";
                theCode += "  \u250c"; // Add corner for above the ID box
                for (int j = 0; j < (longestLineLength); j++) {
                        theCode += "\u2500"; // Add - across the top of the box
                    }
                theCode += "\n";
                inId = true;
            }
            
            
            // Deal with assumption lines
            for (int j = 0; j < numAssumptions; j++) {
                theCode += "\u2502"; // Add | for assumption lines
            }
            if (lineType == NDLine.ASS_START || lineType == NDLine.ASS_ONE_LINE) {
                numAssumptions ++;
                theCode += "\u250c"; // Add assumption start corner
                for (int j = 0; j < (deepestAss - numAssumptions); j++) {
                    theCode += "\u2500"; // Add - to reach the numbers column
                }
            } else {
                for (int j = 0; j < (deepestAss - numAssumptions); j++) {
                    theCode += " ";
                }
            }
            
                
            if (lineType != NDLine.BLANK) {
                // Print the line number
                if (proofArray[i].getLineNum() < 0) {
                    if (!proofArray[i].isSpecial()) {
                        theCode += "  ";
                    } else {
                        theCode += proofArray[i].getSpecialNum() + " ";
                    }
                } else {
                    theCode += proofArray[i].getJustLineNum(i) + ".";
                }
                // Deal with spacing for double digit proofs
                if (proofArray.length > 9 && proofArray[i].getLineNum() < 10) {
                    theCode += " ";
                }
                // If we're in an identity box, add a |, otherwise a space
                theCode += inId ? "\u2502" : " ";
                // Add the line
                theCode += proofArray[i].parseLine();
                // Add the justification
                for (int j = 0; j < (longestLineLength - lineLength + 1); j++) {
                    theCode += " ";
                }
                theCode += proofArray[i].getJustification().getJava();
            } else if (inId) {
                // if it's a blank line in an ID box, add a |
                theCode += "  \u2502";
            }
            // New line
            theCode += "\n";
            
            
            // Add underlines for ID Boxes or assumptions
            if (lineType == NDLine.ASS_END 
                    || lineType == NDLine.ASS_ONE_LINE
                    || lineType == NDLine.ID_BOX_END) {
                // Decrement assumption/leave idbox
                if (lineType == NDLine.ID_BOX_END) {
                    inId = false;
                } else {
                numAssumptions --;                    
                }
                for (int j = 0; j < numAssumptions; j++) {
                    theCode += "\u2502"; // Add the | for existing assumptions
                }
                if (lineType == NDLine.ID_BOX_END) {
                    // Insert spacing for the numbers
                    theCode += (proofArray.length > 9) ? "  " : " ";
                }
                theCode += "\u2514"; // Close this assumption
                int ruleLength = longestLineLength;
                if (lineType != NDLine.ID_BOX_END) {
                    ruleLength += deepestAss - numAssumptions + 2;
                }
                for (int j = 0; j < ruleLength; j++) {
                    theCode += "\u2500"; // Rule a line
                }                
                theCode += "\n"; // New Line
            }
        }
        return theCode;
    }
    
    
    // RETRIEVAL METHODS
    
    /**
     * Finds an NDLine in scope of the goal matching a regEx.
     * If the goal does not appear in the proofArray, throws an exception.
     * 
     * @param regEx A regular expression for the line to be found
     * @param goal An NDLine that occurs in proofArray
     * @return  An NDLine in proofArray, within scope of goal and matching the
     *          supplied regular expression. If no matching line is found, 
     *          returns null.
     */
    public NDLine checkForNDLine(String regEx, NDLine goal) throws LineNotInProofArrayException { 
        int indexOfGoal = findIndexOf(goal);
        if (indexOfGoal == -1) {
            throw new LineNotInProofArrayException(goal.getLine());
        }
        int scopes = 0;
        int lineType;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
            lineType = proofArray[indexOfGoal - i].getType();
            // If we hit an assumption scope, increment scopes
            if (lineType == NDLine.ASS_END || lineType == NDLine.ASS_ONE_LINE) { 
                scopes++;
            }
            // Check line against what we want, but ignore if scopes>0
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)) { 
                return proofArray[indexOfGoal - i];
            }
            // If we hit a start-of-ass line, decrease scopes count
            if (lineType == NDLine.ASS_START || lineType == NDLine.ASS_ONE_LINE) { 
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return null;
    }
    
    /**
     * Finds an NDLine in scope of the goal matching a regEx.
     * If the goal does not appear in the proofArray, throws an exception.
     * 
     * @param regEx A regular expression for the line to be found.
     * @param goal An NDLine that occurs in proofArray.
     * @param context A string providing the context to find a line in.
     * @return  An NDLine in proofArray, within scope of goal and matching the
     *          supplied regular expression. If no matching line is found, 
     *          returns null.
     */
    public NDLine checkForNDLine(String regEx, NDLine goal, String context) throws LineNotInProofArrayException { 
        int indexOfGoal = findIndexOf(goal);
        if (indexOfGoal == -1) {
            throw new LineNotInProofArrayException(goal.getLine());
        }
        int scopes = 0;
        int lineType;
        for (int i = 1; i <= indexOfGoal; i++) { // Move from current goal up to top of proof
            lineType = proofArray[indexOfGoal - i].getType();
            // If we hit an assumption scope, increment scopes
            if (lineType == NDLine.ASS_END || lineType == NDLine.ASS_ONE_LINE) { 
                scopes++;
            }
            // Check line against what we want, but ignore if scopes>0
            if (scopes == 0 && proofArray[indexOfGoal - i].getLine().matches(regEx)
                    && proofArray[indexOfGoal-i].getIsAllowedInContext(context)) { 
                return proofArray[indexOfGoal - i];
            }
            // If we hit a start-of-ass line, decrease scopes count
            if (lineType == NDLine.ASS_START || lineType == NDLine.ASS_ONE_LINE) { 
                scopes--;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return null;
    }
    
    
    // MUTATOR METHODS
    /**
     * Justifies a line, and updates the proofArray accordingly.
     * 
     * @param line The NDLine to give a justification to.
     * @param just The justification to use.
     * @return true if everything worked successfully.
     */
    public boolean justifyLine(NDLine line, NDJust just) throws LineNotInProofArrayException {
        // First check that the line, and all the justifications, are in proofArray
        if (findIndexOf(line) == -1) {
                throw new LineNotInProofArrayException(line.getLine());
            }
        for (NDLine dependency : just.getDependentNDLines()) {
            if (findIndexOf(dependency) == -1) {
                throw new LineNotInProofArrayException(dependency.getLine());
            }
        }
        line.setJustification(just);
        collapseBlanks();
        updateMetrics();
        return true;
    }
    
    
    // HELPER METHODS
    /**
     * Updates the longestLineLength and the deepestAss
     */
    private void updateMetrics() {
        int assCounter = 0;
        int type;
        for (int i = 0; i < proofArray.length; i++) {
            type = proofArray[i].getType();
            if (proofArray[i].getLength() > longestLineLength) {
                longestLineLength = proofArray[i].getLength();
            }
            if (type == NDLine.ASS_START || type == NDLine.ASS_ONE_LINE) {
                assCounter++;
            }
            if (assCounter > deepestAss) {
                deepestAss = assCounter;
            }
            if (type == NDLine.ASS_END || type == NDLine.ASS_ONE_LINE) {
                assCounter--;
            }
        }
    }
    
    /**
     * Check to see if this proofArray has no unjustified lines
     * @return True, if every non-blank line has a justification
     */
    private boolean checkFinished() {
        boolean finished = true;
        for (int i = 0; i < proofArray.length && finished; i++) {
            if (proofArray[i].getType() != NDLine.BLANK 
                    && proofArray[i].getJustification().getBlank()) {
                finished = false;
            }
        }
        return finished;
    }
    
    /**
     * Removes unnecessary blank lines in the proofArray.
     * A blank line is necessary only directly above an unjustified (non-blank)
     * line. This method iterates through the proofArray, and removes all such
     * blank lines.
     */
    private void collapseBlanks() {
        int numUnneededBlanks = 0;
        
        for (int i = 0; i < proofArray.length - 1; i++) {
            if (proofArray[i].getType() == NDLine.BLANK
                    && proofArray[i+1].hasJustification()) {
                numUnneededBlanks ++;
            }
        }
        
        NDLine[] temp = new NDLine[proofArray.length - numUnneededBlanks];
        
        int k = 0;
        for (int i = 0; i < proofArray.length && k < temp.length; i++) {
            if (proofArray[i].getType() != NDLine.BLANK 
                    || !proofArray[i+1].hasJustification()) {
                temp[k] = proofArray[i];
                k++;
            }
        }
        proofArray = temp;
    }
    
    /**
     * Find the index of an NDLine in the proofArray.
     * More carefully, this checks that the address of line is an address in
     * proofArray, and returns that index.
     * 
     * @param line The NDLine object to find in the proofArray
     * @return The index, as an int, of the NDLine in the proofArray, or -1 if
     *          not found
     */
    private int findIndexOf(NDLine line) {
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i] == line) return i;
        }
        return -1;
    }
    
    /**
     * Find the first blank line before the specified index.
     * 
     * @param index The index of the line to search from
     * @return The index of the last blank line before the inputted index, or -1
     *          if none is found
     */
    private int findIndexOfBlank(int index) { // Returns the index of the blank line before the goal. Returns 0 if none found
        for (int i = 1; i <= index; i++) {
            if (proofArray[index - i].getType() == NDLine.BLANK) {
                return index - i;
            }
        }
        return -1;
    }
}
