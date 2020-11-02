/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant.core;

import proofassistant.justification.JustNone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import proofassistant.BracketFinisher;
import proofassistant.Globals;
import proofassistant.exception.MissingArityException;
import proofassistant.exception.WrongLineTypeException;
import proofassistant.util.SymbolHandler;

/**
 * Represents a line in a natural deduction proof
 * 
 * @since Proof Assistant 0.1
 * @version 2.0
 * @author Declan Thompson
 */
public class NDLine {
    
    public final static int NORMAL_LINE = 0;
    public final static int ASS_START = 1;
    public final static int ASS_END = 2;
    public final static int ASS_ONE_LINE = 3;
    public final static int PREMISE = 4;
    public final static int BLANK = 5;
    public final static int DUMMY_LINE = 6;
    public final static int ID_BOX_START = 7;
    public final static int ID_BOX_LINE = 8;
    public final static int ID_BOX_END = 9;
    public final static int EQU_ID_BOX_START = 10;
    public final static int AXIOM = 11;
    
    private String line;
    private NDFormula formula;
    private String mainOp;
    private String firstArg;
    private String secondArg;
    private String parsedLine;
    private String context = "";
    private int lineNum;
    private NDJust justification;
    private int type;
    private String specialLineNum = "";
    public final long id;

    /**
     * Create a new NDLine and set its type.
     * 
     * @param macro The TeX code of the line to create
     * @param type The type of line to create
     */
    public NDLine(String macro, int type) throws IndexOutOfBoundsException { 
        // Allows specification of line type
        if (type != DUMMY_LINE) {
            try {
                formula = new NDFormula(macro, new SymbolHandler());
            } catch (MissingArityException ex) {
                Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        id = System.currentTimeMillis();
        if (type == DUMMY_LINE) {
            line = macro;
        } else {
            line = processMacro(macro);
        }
        Globals.terms.processLine(line);
        Globals.terms.processLine(context);
//        System.out.println("NDLines says " + Globals.terms.getListOfUsedTerms().contains("s"));
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        if (type != DUMMY_LINE) { // Type 6 - used for creating a line solely to access first and second arguments
            parsedLine = parseLine();
        }
        
        if (type != DUMMY_LINE && type != ID_BOX_LINE && type != ID_BOX_END && type != AXIOM) { // Types 8, 9 - identity box lines,
            Globals.lineNum ++;
            this.lineNum = Globals.lineNum;
        } else {
            Globals.specialLineNum --;
            this.lineNum = Globals.specialLineNum;
        }
        
        this.type = type;
        switch (type) {
            case PREMISE:
                justification = new JustNone(JustNone.PREMISE_JUST);
                break;
            case ASS_START:
            case ASS_ONE_LINE:
                justification = new JustNone(JustNone.ASS_JUST);
                break;
            case ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_ID_BOX);
                break;
            case EQU_ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_EQU_ID_BOX);
                break;
            case AXIOM:
                justification = new JustNone(JustNone.AXIOM);
                break;
            default:
                justification = new JustNone();
                break;
        }
    }
    
    /**
     * Create a new NDLine of type 0
     * @param macro The TeX code of the line to create
     */
    public NDLine(String macro) throws IndexOutOfBoundsException { try {
        // Creates a standard line
        formula = new NDFormula(macro, new SymbolHandler());
        } catch (MissingArityException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        id = System.currentTimeMillis();
        line = processMacro(macro);
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        parsedLine = parseLine();
        Globals.lineNum ++;
        this.lineNum = Globals.lineNum;
        type = NORMAL_LINE;
        justification = new JustNone();
        Globals.terms.processLine(context);
    }
    
    /**
     * Create a new normal NDLine using an NDFormula.
     * This creates an unjustified line of type NORMAL_LINE.
     * 
     * @param form The formula to create a line with.
     */
    public NDLine(NDFormula form) {
        formula = form;
        id = System.currentTimeMillis();
        line = formula.getTeX();
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        parsedLine = parseLine();
        Globals.lineNum ++;
        this.lineNum = Globals.lineNum;
        type = NORMAL_LINE;
        justification = new JustNone();
        Globals.terms.processLine(context);
    }
    
    /**
     * Create a new NDLine of the specified type using an NDFormula.
     * This creates an unjustified NDLine of the specified type.
     * 
     * @param form The NDFormula to create the line with.
     * @param tp  The int representing the type of line.
     */
    public NDLine(NDFormula form, int tp) {
        formula = form;
        id = System.currentTimeMillis();
        line = formula.getTeX();
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        parsedLine = parseLine();
        Globals.lineNum ++;
        this.lineNum = Globals.lineNum;
        type = tp;
        justification = new JustNone();
        Globals.terms.processLine(context);
    }
    
    /**
     * Create a new NDLine of type 0 and choose whether or not to increment the Globals line numbers.
     * @param macro The TeX code of the line to create
     * @param incrementLine true - increment Globals line numbers, false - doe not increment Globals line numbers.
     */
    public NDLine(String macro, boolean incrementLine) throws IndexOutOfBoundsException { 
        // Creates a standard line, but doesn't increment line num
        try {
            formula = new NDFormula(macro, new SymbolHandler());
        } catch (MissingArityException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        id = System.currentTimeMillis();
        line = processMacro(macro);
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        parsedLine = parseLine();
        Globals.terms.processLine(context);
        if (incrementLine) {
            Globals.lineNum ++;
        }
        this.lineNum = Globals.lineNum;
        
        type = 0;
        justification = new JustNone();
    }
    
    /**
     * Create a new NDLine of type 5!
     * @param type MUST BE 5! 5 I SAY!
     */
    public NDLine(int type) { // Should only be used to create a blank line, with the argument 5
        formula = null;
        id = System.currentTimeMillis();
        if (type == NDLine.BLANK){
            line = "";
            mainOp = "";
            firstArg = "";
            secondArg = "";
            parsedLine = "";
            lineNum = 0;
            this.type = type;
            justification = new JustNone();
        }
    }
    
    /**
     * Create a new NDLine, set its type and choose whether or not to increment the Globals line numbers.
     * @param macro The TeX code of the line to create
     * @param type The type of line to create
     * @param incrementLine true - increment Globals line numbers, false - doe not increment Globals line numbers.
     */
    public NDLine(String macro, int type, boolean incrementLine) throws IndexOutOfBoundsException { try {
        // Allows specification of line type
        
        formula = new NDFormula(macro, new SymbolHandler());
        } catch (MissingArityException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        id = System.currentTimeMillis();
        if (type == DUMMY_LINE) {
            line = macro;
        } else {
            line = processMacro(macro);
        }
        Globals.terms.processLine(line);
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        if (type != DUMMY_LINE) { // Type 6 - used for creating a line solely to access first and second arguments
            parsedLine = parseLine();
        }
        Globals.terms.processLine(context);
        if (type != DUMMY_LINE && type != ID_BOX_LINE && type != ID_BOX_END && type != AXIOM) { // Types 8, 9 - identity box lines,
            if (incrementLine) {
                Globals.lineNum ++;
            }
            this.lineNum = Globals.lineNum;
        } else {
            Globals.specialLineNum --;
            this.lineNum = Globals.specialLineNum;
        }
        
        this.type = type;
        switch (type) {
            case PREMISE:
                justification = new JustNone(JustNone.PREMISE_JUST);
                break;
            case ASS_START:
                justification = new JustNone(JustNone.ASS_JUST);
                break;
            case ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_ID_BOX);
                break;
            case EQU_ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_EQU_ID_BOX);
                break;
            case AXIOM:
                justification = new JustNone(JustNone.AXIOM);
                break;
            default:
                justification = new JustNone();
                break;
        }
    }
    
    /**
     * Create a new NDLine, set its type and line number.
     * @param macro The TeX code of the line to create
     * @param type The type of line to create
     * @param lineNum The line number to create the line with
     */
    public NDLine(String macro, int type, int lineNum) throws IndexOutOfBoundsException { try {
        // Allows specification of line type
        
        formula = new NDFormula(macro, new SymbolHandler());
        } catch (MissingArityException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        id = System.currentTimeMillis();
        if (type == DUMMY_LINE) {
            line = macro;
        } else {
            line = processMacro(macro);
        }
        Globals.terms.processLine(line);
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        if (type != DUMMY_LINE) { // Type 6 - used for creating a line solely to access first and second arguments
            parsedLine = parseLine();
        }
        Globals.terms.processLine(context);
        if (type != DUMMY_LINE && type != ID_BOX_LINE && type != ID_BOX_END && type != AXIOM) { // Types 8, 9 - identity box lines,
            this.lineNum = lineNum;
        } else {
            this.lineNum = lineNum;
        }
        
        this.type = type;
        switch (type) {
            case PREMISE:
                justification = new JustNone(JustNone.PREMISE_JUST);
                break;
            case ASS_START:
                justification = new JustNone(JustNone.ASS_JUST);
                break;
            case ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_ID_BOX);
                break;
            case EQU_ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_EQU_ID_BOX);
                break;
            case AXIOM:
                justification = new JustNone(JustNone.AXIOM);
                break;
            default:
                justification = new JustNone();
                break;
        }
    }
    
    /**
     * Create a new NDLine, set its type and set a specialLineNum.
     * 
     * @param macro The TeX code of the line to create
     * @param type The type of line to create (you probably want 11 - axiom)
     * @param specialLineNum The special line number to use
     */
    public NDLine(String macro, int type, String specialLineNum) throws IndexOutOfBoundsException { try {
        // Allows specification of line type
        
        formula = new NDFormula(macro, new SymbolHandler());
        } catch (MissingArityException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        id = System.currentTimeMillis();
        if (type == DUMMY_LINE) {
            line = macro;
        } else {
            line = processMacro(macro);
        }
        Globals.terms.processLine(line);
        mainOp = findMainOp(line);
        firstArg = findFirstArg(line);
        secondArg = findSecondArg(line);
        if (type != DUMMY_LINE) { // Type 6 - used for creating a line solely to access first and second arguments
            parsedLine = parseLine();
        }
        Globals.terms.processLine(context);
        if (type != DUMMY_LINE && type != ID_BOX_START && type != ID_BOX_END && type != AXIOM) { // Types 8, 9 - identity box lines,
            Globals.lineNum ++;
            this.lineNum = Globals.lineNum;
        } else {
            Globals.specialLineNum --;
            this.lineNum = Globals.specialLineNum;
        }
        
        this.specialLineNum = specialLineNum;
        
        this.type = type;
        switch (type) {
            case PREMISE:
                justification = new JustNone(JustNone.PREMISE_JUST);
                break;
            case ASS_START:
                justification = new JustNone(JustNone.ASS_JUST);
                break;
            case ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_ID_BOX);
                break;
            case EQU_ID_BOX_START:
                justification = new JustNone(JustNone.ASS_JUST_EQU_ID_BOX);
                break;
            case AXIOM:
                justification = new JustNone(JustNone.AXIOM);
                break;
            default:
                justification = new JustNone();
                break;
        }
    }
    
    
    
    
    
  // Private Methods //
    
    private String processMacro(String macro) { // Processes the macro to ensure prefix notation for all operators
        Globals.terms.processLine(macro);
        
        // Set up underlined 1, 2, 3 for Q
        
        while (macro.contains("\\ul")) {
            int index = macro.indexOf("\\ul");
            String num = "";
            int i = index + 4;
            while (i < macro.length() && macro.charAt(i) != '}') {
                char c = macro.charAt(i);
                if (c > 47 && c < 58) {
                    num = num + macro.charAt(i);
                }
                i++;
            }

            int numberS = Integer.parseInt(num);

            String term = "0";
            for (int j = 0; j < numberS; j++) {
                term = "S" + term;
            }
            macro = macro.substring(0, index) + term + macro.substring(i+1);
        }
        
//        System.out.println(macro);
        
        
        
        // Remove =
        while (macro.contains("=")){
            int equalIndex = macro.indexOf("=");
            int equationStart = 0;
            int equationEnd = macro.length();
            
            boolean foundStart = false;
            boolean foundEnd = false;
            
            for (int i = equalIndex; !foundStart && i > 0; i--) {
                if (macro.charAt(i) == '{') {
                    equationStart = i + 1;
                    foundStart = true;
                }
            }
            for (int i = equalIndex; !foundEnd && i < macro.length(); i ++) {
                if (macro.charAt(i) == '}') {
                    equationEnd = i;
                    foundEnd = true;
                }
            }
            
            String beforeEquation = macro.substring(0, equationStart);
            String leftSide = macro.substring(equationStart, equalIndex);
            String rightSide = macro.substring(equalIndex + 1, equationEnd);
            String afterEquation = macro.substring(equationEnd);
            
            macro = beforeEquation + "\\eq{" + leftSide + "}{" + rightSide + "}" + afterEquation;
        }
        
        
        // Remove \neq
        while (macro.contains("\\neq")){
            int equalIndex = macro.indexOf("\\neq");
            int equationStart = 0;
            int equationEnd = macro.length();
            
            boolean foundStart = false;
            boolean foundEnd = false;
            
            for (int i = equalIndex; !foundStart && i > 0; i--) {
                if (macro.charAt(i) == '{') {
                    equationStart = i + 1;
                    foundStart = true;
                }
            }
            for (int i = equalIndex; !foundEnd && i < macro.length(); i ++) {
                if (macro.charAt(i) == '}') {
                    equationEnd = i;
                    foundEnd = true;
                }
            }
            
            String beforeEquation = macro.substring(0, equationStart);
            String leftSide = macro.substring(equationStart, equalIndex);
            String rightSide = macro.substring(equalIndex + 4, equationEnd);
            String afterEquation = macro.substring(equationEnd);
            
            macro = beforeEquation + "\\neg{\\eq{" + leftSide + "}{" + rightSide + "}}" + afterEquation;
        }
        
        // Remove \cdot
        macro = macro.replaceAll("\\\\cdot", "\u22c5");
        
        // Remove +
        while (macro.contains("+")){
            int plusIndex = macro.indexOf("+");
            int sumStart = 0;
            int sumEnd = macro.length();
            
            boolean foundStart = false;
            boolean foundEnd = false;
            int bracketCount = 0;
            for (int i = plusIndex; !foundStart && i > 0; i--) {
                char c = macro.charAt(i);
                if (c == ')') {
                    bracketCount++;
                } else if (c == '(') {
                    if (bracketCount == 0) {
                        sumStart = i + 1;
                        foundStart = true;
                    } else {
                        bracketCount--;
                    }
                } else if (c == '{') {
                    sumStart = i+1;
                    foundStart = true;
                }
            }
            bracketCount = 0;
            for (int i = plusIndex; !foundEnd && i < macro.length(); i ++) {
                char c = macro.charAt(i);
                if (c == '(') {
                    bracketCount++;
                } else if (c == ')') {
                    if (bracketCount == 0) {
                        sumEnd = i;
                        foundEnd = true;
                    } else {
                        bracketCount--;
                    }
                } else if (c == '}') {
                    sumEnd = i;
                    foundEnd = true;
                }
            }
            
            String beforeEquation = macro.substring(0, sumStart);
            String leftSide = macro.substring(sumStart, plusIndex);
            String rightSide = macro.substring(plusIndex + 1, sumEnd);
            String afterEquation = macro.substring(sumEnd);
            
            macro = beforeEquation + "/plus[" + leftSide + "][" + rightSide + "]" + afterEquation;
        }
        
        // Remove times
        while (macro.contains("\u22c5")){
            int equalIndex = macro.indexOf("\u22c5");
            int sumStart = 0;
            int sumEnd = macro.length();
            
            boolean foundStart = false;
            boolean foundEnd = false;
            int bracketCount = 0;
            for (int i = equalIndex; !foundStart && i > 0; i--) {
                char c = macro.charAt(i);
                if (c == ')') {
                    bracketCount++;
                } else if (c == '(') {
                    if (bracketCount == 0) {
                        sumStart = i + 1;
                        foundStart = true;
                    } else {
                        bracketCount--;
                    }
                } else if (c == '{') {
                    sumStart = i + 1;
                    foundStart = true;
                }
            }
            for (int i = equalIndex; !foundEnd && i < macro.length(); i ++) {
                char c = macro.charAt(i);
                if (c == '(') {
                    bracketCount++;
                } else if (c == ')') {
                    if (bracketCount == 0) {
                        sumEnd = i;
                        foundEnd = true;
                    } else {
                        bracketCount--;
                    }
                } else if (c == '}') {
                    sumEnd = i;
                    foundEnd = true;
                }
            }
            
            String beforeEquation = macro.substring(0, sumStart);
            String leftSide = macro.substring(sumStart, equalIndex);
            String rightSide = macro.substring(equalIndex + 1, sumEnd);
            String afterEquation = macro.substring(sumEnd);
            
            macro = beforeEquation + "/time[" + leftSide + "][" + rightSide + "]" + afterEquation;
        }
        
        // Remove all brackets
        macro = macro.replaceAll("\\(", "").replaceAll("\\)", "");
        
        // Replace numbers with SSS...
        String result = "";
        boolean inCommand = false;
        for (int i = 0; i < macro.length(); i++) {
            String currentChar = "" + macro.charAt(i);
            
            if(isInteger(currentChar)){
                while (i < macro.length() - 1 && macro.charAt(i+1) > 47
                        && macro.charAt(i+1) < 58) {
                    currentChar = currentChar + macro.charAt(i+1);
                    i++;
                }
            }
//            System.out.println("Looking at " + currentChar);
            if (currentChar.equals("\\")) {
                inCommand = true;
            } else if (currentChar.equals("/")) {
                inCommand = true;
            }
            if (!inCommand) {
                if (isInteger(currentChar)) {
                    int number = Integer.parseInt(currentChar);
                    currentChar = "0";
                    for (int p = 0; p < number; p++) {
                        currentChar = "S" + currentChar;
                    }
                }
            }
            if (currentChar.equals("{") || currentChar.equals("[")) {
                inCommand = false;
            }
            result = result + currentChar;
//            System.out.println("result is " + result);
        }
        macro = result;
        
        // Insert brackets around all terms
//        System.out.println("======================");
//        System.out.println("Brackets for " + macro);
//        System.out.println("======================");
//        System.out.println("Keyset is " + Globals.arity.keySet());
        result = "";
        inCommand = false;
        BracketFinisher brackets = new BracketFinisher();
        for (int i = 0; i < macro.length(); i++) {
            String currentChar = "" + macro.charAt(i);
            
            while (i < macro.length() - 1 && macro.charAt(i+1) == '\'') {
                currentChar = currentChar + macro.charAt(i+1);
                i++;
            }
//            System.out.println("Looking at " + currentChar);
            if (currentChar.equals("\\")) {
                inCommand = true;
            } else if (currentChar.equals("/")) {
                inCommand = true;
                brackets.increment(10);
                result = result + "(";
                brackets.add(11);
            }
            if (!inCommand && Globals.arity.containsKey(currentChar)) {
//                System.out.println("arity contains " + currentChar);
                int arity = Globals.arity.get(currentChar);
                if (arity > 0) {
                    brackets.increment(arity - 1);
                    result = result + "(" + currentChar;
                    brackets.add(arity);
                } else if (arity == 0) {
                    result = result + "(" + currentChar + ")" + brackets.decrement(")");
                }
            } else {
                result = result + currentChar + brackets.decrement(")");
            }
            if (currentChar.equals("{") || currentChar.equals("[")) {
                inCommand = false;
            }
//            System.out.println("result is " + result);
        }
        
        macro = result;
        
        // Insert brackets around all instances of x and +
        while (macro.contains("\u22c5")) {
            int index = macro.indexOf("\u22c5");
            int indexOfLeft = 0;
            int indexOfRightEnd = macro.length()-1;
            int bracketCount = 0;
            int i = index - 1;
            if (macro.charAt(i) == ')') {
                bracketCount ++;
            }
            if (bracketCount == 0) {
                indexOfLeft = i;
            }
            i--;
            while (bracketCount != 0 && i > 0) {
                if (macro.charAt(i) == ')') {
                    bracketCount ++;
                } else if (macro.charAt(i) == '(') {
                    bracketCount --;
                }
                if (bracketCount == 0) {
                    indexOfLeft = i;
                }
                i--;
            }
            
            i = index + 1;
            bracketCount = 0;
            if (macro.charAt(i) == '(') {
                bracketCount ++;
            }
            if (bracketCount == 0) {
                indexOfRightEnd = i;
            }
            i++;
            while (bracketCount != 0 && i > 0) {
                if (macro.charAt(i) == '(') {
                    bracketCount ++;
                } else if (macro.charAt(i) == ')') {
                    bracketCount --;
                }
                if (bracketCount == 0) {
                    indexOfRightEnd = i;
                }
                i++;
            }
            
            macro = macro.substring(0, indexOfLeft) + "(" + macro.substring(indexOfLeft, index) + "   " + macro.substring(index + 1, indexOfRightEnd + 1) + ")" + macro.substring(indexOfRightEnd + 1);
        }
        macro = macro.replaceAll("   ", "\u22c5");
        while (macro.contains("+")) {
            int index = macro.indexOf("+");
//            System.out.println("Index " + index);
            int indexOfLeft = 0;
            int indexOfRightEnd = macro.length()-1;
            int bracketCount = 0;
            int i = index - 1;
            if (macro.charAt(i) == ')') {
                bracketCount ++;
            }
            if (bracketCount == 0) {
//                System.out.println("hiho " + i);
                indexOfLeft = i;
            }
            i--;
            while (bracketCount != 0 && i > 0) {
                if (macro.charAt(i) == ')') {
                    bracketCount ++;
                } else if (macro.charAt(i) == '(') {
                    bracketCount --;
                }
                if (bracketCount == 0) {
                    indexOfLeft = i;
                }
                i--;
            }
            
            i = index + 1;
            bracketCount = 0;
            if (macro.charAt(i) == '(') {
                bracketCount ++;
            }
            if (bracketCount == 0) {
                indexOfRightEnd = i;
            }
            i++;
            while (bracketCount != 0 && i > 0) {
                if (macro.charAt(i) == '(') {
                    bracketCount ++;
                } else if (macro.charAt(i) == ')') {
                    bracketCount --;
                }
                if (bracketCount == 0) {
                    indexOfRightEnd = i;
                }
                i++;
            }
//            System.out.println(macro);
//            System.out.println("left: " + indexOfLeft);
//            System.out.println("right: " + indexOfRightEnd);
            macro = macro.substring(0, indexOfLeft) + "(" + macro.substring(indexOfLeft, index) + "   " + macro.substring(index + 1, indexOfRightEnd + 1) + ")" + macro.substring(indexOfRightEnd + 1);
        }
        macro = macro.replaceAll("   ", "+");
        
//        System.out.println(macro);
        macro = macro.replace("\\box{}", "\\box{" + Globals.defaultBoxDiaCharacter + "}");
        macro = macro.replace("\\dia{}", "\\dia{" + Globals.defaultBoxDiaCharacter + "}");
        return macro;
    }
    
    /**
     * Checks whether a given string is an integer
     * @param str A string to check
     * @return true if str is an integer, false otherwise
     */
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
    
    private String findMainOp(String line){ // Returns the main operator of the line
        int bracketLocation = line.indexOf("{");
//        System.out.println(line);
//        System.out.println("Line " + line + " " + bracketLocation);
        if (bracketLocation == -1) {
            return "";
        } else {
            return line.substring(1,bracketLocation);
        }
    }
    
    private String findFirstArg(String line){ // Returns the first argument of the line
        if (mainOp.equals("")){
            return "";
        } else {
//            System.out.println(line);
            int bracketCount = 0;
            boolean started = false;
            int i = 0;
            
            while (bracketCount > 0 || !started) {
                if (line.charAt(i) == '{') {
                        bracketCount++;
                        started = true;
                } else if (line.charAt(i) == '}') {
                        bracketCount--;
                }
                i++;
            }

            return line.substring(line.indexOf("{")+1, i-1);
        }
    }
       
    private String findSecondArg(String line) { // Returns the second argument of the line, or "" if the line takes one argument
//        System.out.println(line);
        if (findMainOp(line).equals("") || findMainOp(line).equals("neg")){
            return "";
        } else {
            int bracketCount = 0;
            boolean started = false;
            int i = 0;

            while (bracketCount > 0 || !started) {
                if (line.charAt(i) == '{') {
                        bracketCount++;
                        started = true;
                } else if (line.charAt(i) == '}') {
                        bracketCount--;
                }
                i++;
            }
            if (i == line.length()) {
                return "";
            } else {
                return line.substring(i+1,line.length()-1);
            }
        }
    }
    
    private String findNonQaMainOp(String line) { // Returns the first non universal main operator, or qa if there is no other operator
        if (findMainOp(line).equals("qa")) {
            if (findMainOp(findSecondArg(line)).equals("")) {
                return findMainOp(line);
            } else {
                return findNonQaMainOp(findSecondArg(line));
            }
        } else {
            return findMainOp(line);
        }
    }
    
    private String findNonQaFirstArgRegEx(String line) { 
// Returns the first non universal argument of a line as a regular expression, or the line as a regex if no first argument
//        System.out.println();
        
        boolean moreQa = true;
        ArrayList<String> variables = new ArrayList<>(); // Holds a list of the variable used in qa
        
        while (moreQa) { // Strip line of all qa, keep log of variables
            if (findMainOp(line).equals("qa")) {
                variables.add(findFirstArg(line));
//                System.out.println(findFirstArg(line));
                line = (findSecondArg(line));
            } else {
                moreQa = false;
            }
        }
        
        if (!findFirstArg(line).equals("")){
            line = findFirstArg(line); // We're just wanting the first argument
        }
        
        // Replace characters that regex doesn't like
        line = line.replace(" ", "");
        line = line.replace("\\", "\\\\");
        line = line.replace("{", "\\{").replace("}", "\\}");
        line = line.replace("[", "\\[").replace("]", "\\]");
        line = line.replace("(", "\\(").replace(")", "\\)"); // Replace ( ) with \( \) for regex's pleasure
        line = line.replace("+", "\\+");
//        line = line.replace("⋅", "\\⋅");
        
        int k = 1; // Set up a counter for the back references
        while (!variables.isEmpty()) { // While we've got variables to go through
            String currentVar = variables.get(0); // Get the first variable
            for (int i = 0; i < variables.size(); i++) { // Check through the other variables
                if (line.indexOf(variables.get(i)) == -1) { // If a variable isn't in the line, remove it
                    variables.remove(i);
                    i--;
                } else { // Check which variable occurs first, and set currentVar to that
                    if (line.indexOf(variables.get(i)) < line.indexOf(currentVar) || line.indexOf(currentVar) == -1) {
                        currentVar = variables.get(i);
                    }
                    
                }
            }
            
            line = line.replaceAll(currentVar, "\\\\" + k);
            line = line.replaceFirst("\\\\" + k, "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|\\\\w+)");
            k++;
            variables.remove(currentVar);
            
            
        }
        
//        System.out.println(line);
        return line;
    }
    
    private String findNonQaSecondArgRegEx(String line) { // Returns the second argument of line as a regex, or "" if no second argument
//        System.out.println();
        
        boolean moreQa = true;
        ArrayList<String> variables = new ArrayList<>(); // Holds a list of the variable used in qa
        
        while (moreQa) { // Strip line of all qa, keep log of variables
            if (findMainOp(line).equals("qa")) {
                variables.add(findFirstArg(line));
//                System.out.println(findFirstArg(line));
                line = (findSecondArg(line));
            } else {
                moreQa = false;
            }
        }
        
        line = findSecondArg(line); // we just want the second argument (this will be "" if there's no second argument
        
        // Replace characters that regex doesn't like
        line = line.replace("\\", "\\\\");
        line = line.replace("{", "\\{").replace("}", "\\}");
        line = line.replace("[", "\\[").replace("]", "\\]");
        line = line.replace("(", "\\(").replace(")", "\\)"); // Replace ( ) with \( \) for regex's pleasure
        line = line.replace("+", "\\+");
        
        int k = 1; // Set up a counter for the back references
        while (!variables.isEmpty()) { // While we've got variables to go through
            String currentVar = variables.get(0); // Get the first variable
            for (int i = 0; i < variables.size(); i++) { // Check through the other variables
                if (line.indexOf(variables.get(i)) == -1) { // If a variable isn't in the line, remove it
                    variables.remove(i);
                    i--;
                } else { // Check which variable occurs first, and set currentVar to that
                    if (line.indexOf(variables.get(i)) < line.indexOf(currentVar)) {
                        currentVar = variables.get(i);
                    }
                }
            }
            line = line.replace(currentVar, "\\" + k);
            line = line.replaceFirst("\\\\" + k, "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|\\\\w+)");
            k++;
            variables.remove(currentVar);
        }
        
//        System.out.println();
        return line;
    }
    
    private String findNonQaRegEx(String line) {
        
        //        System.out.println();
        boolean moreQa = true;
        ArrayList<String> variables = new ArrayList<>(); // Holds a list of the variable used in qa
        
        while (moreQa) { // Strip line of all qa, keep log of variables
            if (findMainOp(line).equals("qa")) {
                variables.add(findFirstArg(line));
//                System.out.println(findFirstArg(line));
                line = (findSecondArg(line));
            } else {
                moreQa = false;
            }
        }
        
        
        // Replace characters that regex doesn't like
        line = line.replace("\\", "\\\\");
        line = line.replace("{", "\\{").replace("}", "\\}");
        line = line.replace("[", "\\[").replace("]", "\\]");
        line = line.replace("(", "\\(").replace(")", "\\)"); // Replace ( ) with \( \) for regex's pleasure
        line = line.replace("+", "\\+");
        
        int k = 1; // Set up a counter for the back references
        while (!variables.isEmpty()) { // While we've got variables to go through
            String currentVar = variables.get(0); // Get the first variable
            for (int i = 0; i < variables.size(); i++) { // Check through the other variables
                if (line.indexOf(variables.get(i)) == -1) { // If a variable isn't in the line, remove it
                    variables.remove(i);
                    i--;
                } else { // Check which variable occurs first, and set currentVar to that
                    if (line.indexOf(variables.get(i)) < line.indexOf(currentVar)) {
                        currentVar = variables.get(i);
                    }
                }
            }
            line = line.replace(currentVar, "\\" + k);
            line = line.replaceFirst("\\\\" + k, "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|\\\\w+)");
            k++;
            variables.remove(currentVar);
        }
        
//        System.out.println();
        return line;
    }
    
    /**
     * Converts a phrase into a regular expression, by replacing a variable with
     * back references.
     * 
     * @param variable A string of the variable to use.
     * @param phrase A string of the phrase to convert.
     * @return 
     */
    private String findRegEx(String variable, String phrase) {
        String regEx = phrase.replace("\\", "\\\\"); // Replace \ with \\ to make regex feel happy
        regEx = regEx.replace("{", "\\{").replace("}", "\\}"); // Replace { and } with \{ and \} to make regex feel happy
        regEx = regEx.replace("[", "\\[").replace("]", "\\]");
        regEx = regEx.replace("(", "\\(").replace(")", "\\)"); // Do the same with ( and ) (regex happiness)
        if (!variable.equals("")) {
            regEx = replace(regEx, variable, "\\\\1"); // Replace variable with the 1st back reference
            if (Globals.allowedRules.get("secondOrder")) {
                regEx = regEx.replaceFirst("\\\\1", "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|[\\\\w\\\\(\\\\)\\\\[\\\\]/\\\\{\\\\}\\\\\\\\]+)"); // Replace the first back reference with a group to match 1 or more letters
            } else {
                regEx = regEx.replaceFirst("\\\\1", "(\\\\([\\\\w+\\\\(\\\\)\\\\[\\\\]/]+\\\\)|\\\\w+)"); // Replace the first back reference with a group to match 1 or more letters
            }
        }
        return regEx;
    }
    
    private String parseTheLine(String line){ // Recursively parses the line to logical symbols. Returns the result
        String op = findMainOp(line);
        if (!Globals.operators.containsKey(op)){
            if (op.contains("box")) {
                Globals.operators.put(op, Globals.operators.get("LEFTBOX") + op.replaceFirst("box", "") + Globals.operators.get("RIGHTBOX"));
            } else if (op.contains("dia")) {
                Globals.operators.put(op, Globals.operators.get("LEFTdia") + op.replaceFirst("dia", "") + Globals.operators.get("RIGHTdia"));
            }
        }
        
        if (!Globals.showBrackets) {
            line = line.replaceAll("\\(", "").replaceAll("\\)", "");
        }
        if (line.equals("\\falsum")) {
            return convertOp("falsum");
        } else if (op.equals("")){
            if (Globals.qShowNumbers) {
                while (line.contains("S0")) {
                    int currentIndex = line.indexOf("S0");
                    int beginIndex = currentIndex;
                    int num = 0;
                    while (beginIndex > -1 && line.charAt(beginIndex) == 'S') {
                        num++;
                        beginIndex --;
                    }
                    line = line.substring(0, beginIndex+1) + num + line.substring(currentIndex + 2);
                }
            }
            if (line.contains("/")) {
                boolean inArith = false;
                String result = "";
                String currentOp = "";
                int bracketCount = 0;
                int argStartIndex = -1;
                int numArgs = 0;
                int i = 0;
                while (i < line.length()) {
                    char c = line.charAt(i);
                    if (c == '/' && !inArith) {
                        inArith = true;
                        currentOp = line.substring(i+1, i+5);
                        if (Globals.niceBrackets) {
                            result = result + "(";
                        }
                    } else if (inArith) {
                        if (bracketCount == 0 && c == '[') {
                            argStartIndex = i+1;
                            numArgs++;
                        }
                        if (c == '[') {
                            bracketCount++;
                        } else if (c == ']') {
                            bracketCount--;
                        }
                        if (bracketCount == 0 && argStartIndex > -1) {
                            result = result + parseTheLine(line.substring(argStartIndex, i));
                            if (numArgs == 1) {
                                result = result + convertOp(currentOp);
                            }
                        }
                        if (bracketCount == 0 && numArgs == 2) {
                            inArith = false;
                            numArgs = 0;
                            if (Globals.niceBrackets) {
                                result = result + ")";
                            }
                        }
                    } else {
                        result = result + c;
                    }
                    i++;
                }
                line = result;
            }
            return line;
        } else {
            if (findSecondArg(line).equals("")){
                if (Globals.useNeq && op.equals("neg") && findMainOp(findFirstArg(line)).equals("eq")) {
                    return "" + parseTheLine(findFirstArg(findFirstArg(line))) + convertOp("noteq") + parseTheLine(findSecondArg(findFirstArg(line))) + "";
                }
                return convertOp(op) + parseTheLine(findFirstArg(line));
            } else {
                if (op.equals("qa") || op.equals("qe")){
                    return convertOp(op) + findFirstArg(line) + " " + parseTheLine(findSecondArg(line));
                } else if (op.equals("box")) {
                    return (Globals.operators.get("LEFTBOX") + findFirstArg(line) + Globals.operators.get("RIGHTBOX") + parseTheLine(findSecondArg(line))).replace(Globals.operators.get("LEFTBOX") + "\u25c7" + Globals.operators.get("RIGHTBOX"), "\u25c7");
                } else if (op.equals("dia")) {
                    return (Globals.operators.get("LEFTdia") + findFirstArg(line) + Globals.operators.get("RIGHTdia") + parseTheLine(findSecondArg(line))).replace(Globals.operators.get("LEFTdia") + "\u25c7" + Globals.operators.get("RIGHTdia"), "\u25c7");
                } else if (op.equals("eq")) {
                    return "" + parseTheLine(findFirstArg(line)) + convertOp(op) + parseTheLine(findSecondArg(line)) + "";
                } else if (op.equals("at") || op.equals("self")) {
                    return convertOp(op) + "<sub>" + findFirstArg(line) + "</sub> " + parseTheLine(findSecondArg(line));
                } else {
                    return "(" + parseTheLine(findFirstArg(line)) + convertOp(op) + parseTheLine(findSecondArg(line)) + ")";
                }
            }
        }
    }
        
    private String convertOp (String op) { // Converts TeX names to logic symbols. Returns the result
        if (Globals.operators.containsKey(op)) {
            return Globals.operators.get(op);
        } else {
            return op;
        }
    }
    
    
    
  // Mutator Methods //

    /**
     * Set this NDLine's justification.
     * @param just The justification to set
     */
    public void setJustification(NDJust just){
        justification = just;
    }
    
    /**
     * Set this NDLine's line number
     * @param newLineNum The line number to set
     */
    public void setLineNum(int newLineNum) {
        lineNum = newLineNum;
    }
    
    /**
     * Refresh the parsing of this line (in case anything has changed).
     */
    public void reparseLine() {
        parsedLine = parseTheLine(line);
    }
    
    /**
     * Reprocess the macro of this line and refresh the parsing of the line
     */
    public void refreshLine() {
        line = processMacro(line);
        parsedLine = parseTheLine(line);
    }
    
    /**
     * Set the context of this line
     * @param newContext The new context to set
     */
    public void setContext(String newContext) {
        context = processMacro(newContext);
        Globals.terms.processLine(context);
    }
    
    /**
     * Sets the context of this line to the same as that of another.
     * 
     * @param anotherLine The other NDLine which supplies the context to adopt.
     */
    public void setSameContextAs(NDLine anotherLine) {
        context = processMacro(anotherLine.getContext());
        Globals.terms.processLine(context);        
    }
    
    
    

    /**
     * Get this line's context
     * @return This line's context
     */
    public String getContext() {
        return context;
    }
    
    /**
     * Parses this line's context to TeX format.
     * @return This line's context in TeX format
     */
    public String getTeXContext() {
        if (Globals.allowedRules.get("showContext")) {
            if (context != "") {
                return context + ": ";
            }
        }
        return "";
    }
    
    /**
     * Parses the line to TeX code format
     * @return The TeX code for this line
     */    
    public String getTeXLine() {
        String arithLine;
        if (Globals.showBrackets) {
            arithLine =  getTeXContext() + getLine().replace("box", "bx");
        } else {
            arithLine =  getTeXContext().replace("(", "").replace(")", "") + getLine().replace("box", "bx").replace("(", "").replace(")", "");
        }
        
        int currentChar;
        int bracketCount;
        
        int secondArgStart;
        while (arithLine.contains("/plus")) {
            currentChar = arithLine.indexOf("/plus") + 6;
            bracketCount = 1;
            while (bracketCount > 0) {
                if (arithLine.charAt(currentChar) == '[') {
                    bracketCount ++;
                } else if (arithLine.charAt(currentChar) == ']') {
                    bracketCount --;
                }
                currentChar++;
            }
            secondArgStart = currentChar;
            currentChar++;
            bracketCount = 1;
            while (bracketCount > 0) {
                if (arithLine.charAt(currentChar) == '[') {
                    bracketCount ++;
                } else if (arithLine.charAt(currentChar) == ']') {
                    bracketCount --;
                }
                currentChar++;
            }
            arithLine = arithLine.substring(0, arithLine.indexOf("/plus"))
                        + "(" + arithLine.substring(arithLine.indexOf("/plus") + 6, secondArgStart - 1)
                        + "+" + arithLine.substring(secondArgStart + 1, currentChar - 1)
                        + ")" + arithLine.substring(currentChar);
        }
        
        while (arithLine.contains("/time")) {
            currentChar = arithLine.indexOf("/time") + 6;
            bracketCount = 1;
            while (bracketCount > 0) {
                if (arithLine.charAt(currentChar) == '[') {
                    bracketCount ++;
                } else if (arithLine.charAt(currentChar) == ']') {
                    bracketCount --;
                }
                currentChar++;
            }
            secondArgStart = currentChar;
            currentChar++;
            bracketCount = 1;
            while (bracketCount > 0) {
                if (arithLine.charAt(currentChar) == '[') {
                    bracketCount ++;
                } else if (arithLine.charAt(currentChar) == ']') {
                    bracketCount --;
                }
                currentChar++;
            }
            arithLine = arithLine.substring(0, arithLine.indexOf("/time"))
                        + "(" + arithLine.substring(arithLine.indexOf("/time") + 6, secondArgStart - 1)
                        + "{\\cdot}" + arithLine.substring(secondArgStart + 1, currentChar - 1)
                        + ")" + arithLine.substring(currentChar);
        }
        
        return arithLine;
    }
    
    /**
     * Check whether this line has a special line number
     * @return true if this line has a special line number, false otherwise
     */
    public boolean isSpecial() {
        return !specialLineNum.equals("");
    }
    
    /**
     * Returns this line's special line number. Returns the empty string if this line has no special line number.
     * @return This line's special line number, or the empty string.
     */
    public String getSpecialNum() {
        return specialLineNum;
    }
    
    /**
     * Set the special line number for this line. If you wish to remove a line's special line number, set this to ""
     * @param specialLineNum The special line number to set
     */
    public void setSpecialLineNum(String specialLineNum) {
        this.specialLineNum = specialLineNum;
        return;
    }
    
    
  // Accessor Methods //

    /**
     * Create a duplicate of this line
     * @return A duplicate of this line
     */
        @Override
    public NDLine clone() {
        try {
            NDLine theClone = new NDLine(line, type, false);
            theClone.setJustification(justification);
            theClone.setLineNum(lineNum);
            theClone.setContext(context);
            theClone.setSpecialLineNum(specialLineNum);
//        Globals.lineNum --;
            return theClone;
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(NDLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     *
     * @return The macro for this line
     */
    public String getLine() {
        return line;
    }
    
    /**
     * Get the formula for this line.
     * 
     * @return An NDFormula containing this line's formula.
     */
    public NDFormula getFormula() {
        return formula;
    }
    
    /**
     * Parses the line to Java text and returns it, with context if required
     * @return The parsed line
     */
    public String parseLine() {
        if (formula != null) {
            System.out.println("Get Parse: "  + formula.getParse());
            System.out.println("Get TeX " + formula.getTeX());            
            System.out.println("Base Line " + line);
        }
        if (Globals.allowedRules.get("showContext") && !context.equals("")) {
//            System.out.println(parseTheLine(context));
            return parseTheLine(context) + ": " + parseTheLine(line).replaceAll("\\<.*?>","").replaceAll("\\<.*?>","");
        } else {
            return parseTheLine(line).replaceAll("\\<.*?>","").replaceAll("\\<.*?>","");
        }
    }
    
    public String parseLineContextless() {
        return parseTheLine(line).replaceAll("\\<.*?>","").replaceAll("\\<.*?>","");
    }
    
    public String parseContext() {
        return parseTheLine(context);
    }
    
    public String parseLineHTML() {
        if (Globals.allowedRules.get("showContext") && !context.equals("")) {
            return "<html>" + parseTheLine(context) + ": " + parseTheLine(line) + "</html>";
        } else {
            return "<html>" + parseTheLine(line) + "</html>";
        }
    }
    
    public String getMainOp() {
        return mainOp;
    }
    
    public String getSMainOp() {
        if (mainOp.contains("box")) {
            return "box";
        } else if (mainOp.contains("dia")) {
            return "dia";
        } else if (mainOp.equals("") && line.length() > 0 && line.charAt(0) == '(') {
            return "nom";
        } else if (line.equals("\\falsum")) {
            return "falsum";
        }
        return mainOp;
    }
    
    public String parseMainOp() {
        if (line.equals("\\falsum")) {
            return convertOp("falsum");
        } else {
            return convertOp(getSMainOp());
        }
    }
    
    public String getLineNumOutput() {
        if (specialLineNum.equals("")) {
            return "" + lineNum;
        } else {
            return specialLineNum;
        }
    }
    
    public String getLineNumOutput(int i) {
        if (specialLineNum.equals("")) {
            if (Globals.numberTopDown) {
                return "" + (i + 1 + Globals.lineIncrement);
            }
            return "" + (lineNum + Globals.lineIncrement);
        } else {
            return specialLineNum;
        }
    }
    
    public String getArgAsString(){
        return firstArg;
    }
    
    public String getArgAsString(int argument) {
        switch (argument) {
            case 1 : return firstArg;
            case 2 : return secondArg;
        }
        return "";
    }
    
    /** Gets the first argument from the formula of this NDLine.
     * 
     * @return An NDFormula of the first (or only) argument of this NDLine.
     */
    public NDFormula getArg() {
        return formula.getArg();
    }
    
    /** Gets an argument from the formula of this NDLine.
     * 
     * @param argument The number of the argument to retrieve. Indexing starts
     *                  at 1.
     * @return An NDFormula of the requested argument of this NDLine.
     */
    public NDFormula getArg(int argument) {
        return formula.getArg(argument);
    }
    
    public String parseFirstArg() {
        return parseTheLine(firstArg);
    }
    
    public String parseSecondArg() {
        return parseTheLine(secondArg);
    }
    
    public int getLineNum() {
        return lineNum;
    }
    
    public NDJust getJustification() {
        return justification;
    }
    
    public boolean hasJustification() { // Returns true if this line is justified
        return !justification.getBlank();
    }
    
    public int getType() {
        return type;
    }
    
    public String getTeX() { // Returns the line, converted to TeX code conforming to the ndproof.sty package
        if (type == 0 || type == 4){
            return "\\NDLine{" + lineNum + ".}{$" + line + "$}{" + justification + "}";
        } else if (type == 1){
            return "\\NDAssStart{" + lineNum + ".}{$" + line + "$}";
        } else if (type == 2){
            return "\\NDAssEnd{" + lineNum + ".}{$" + line + "$}{" + justification + "}";
        } else if (type == 3){
            return "\\NDOneLineAss{" + lineNum + ".}{$" + line + "$}";
        } else if (type == 5) {
            return "";
        }
        return "";
    }
    
    public boolean isInScopeOf(NDLine anotherLine, NDLine[] anArray) { // Returns true if current line can "see" anotherLine in anArray
        int indexOfLine = indexIn(anArray);
        int scopes = 0;
        boolean inScope = false;
        
        for (int i = 1; i <= indexOfLine && !inScope; i++) { // Move from current goal up to top of proof until found in scope
            if (anArray[indexOfLine - i].getType() == 2 || anArray[indexOfLine - i].getType() == 3) { // If we hit an end-of-assumption line, increase scopes count
                scopes ++;
            }
//            if (scopes == 0 && anArray[indexOfLine - i].getLineNum() == anotherLine.getLineNum()
//                    && (getContext().equals(anotherLine.getContext())
//                    || getContext().equals("")
//                    || anotherLine.getContext().equals(""))) { // Check if we've found anotherLine, but ignore if scopes>0
//                inScope = true;
//            }
            if (scopes == 0 && anArray[indexOfLine - i].getLineNum() == anotherLine.getLineNum()) { // Check if we've found anotherLine, but ignore if scopes>0
                inScope = true;
            }
            if (anArray[indexOfLine - i].getType() == 1 || anArray[indexOfLine - i].getType() == 3) { // If we hit a start-of-ass line, decrease scopes count
                scopes --;
                if (scopes < 0) { // Make sure scopes doesn't go below 0
                    scopes = 0;
                }
            }
        }
        return inScope;
    }
    
    public boolean usesTerm(NDAtom term) {
        // NB: formula == null iff type == BLANK
        return (type == BLANK) ? false : formula.usesTerm(term);
    }
    
    public int indexIn(NDLine[] anArray) { // Returns the index of this line in an array of NDLines
        int i = 0;
        while (i < anArray.length) {
            if (this == anArray[i]) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public String getNonUniMainOp() {
        return findNonQaMainOp(line);
    }
    
    public String getNonUniSMainOp() {
        String value = findNonQaMainOp(line);
        if (value.contains("box")) {
            return "box";
        } else if (value.contains("dia")){
            return "dia";
        }else if (value.equals("") && line.charAt(0) == '(') {
            return "nom";
        }
        return value;
    }
    
    public String parseNonUniMainOp() {
        return convertOp(findNonQaMainOp(line));
    }
    
    public String parseNonUniSMainOp() {
        return convertOp(getNonUniSMainOp());
    }
    
    public String getNonQaFirstArgRegEx() {
        return findNonQaFirstArgRegEx(line);
    }
    
    public String getNonQaSecondArgRegEx() {
        return findNonQaSecondArgRegEx(line);
    }
    
    public String getNonQaRegEx() {
        return findNonQaRegEx(line);
    }
    
    public String getRegEx(String variable) {
        return findRegEx(variable, line);
    }
    
    public String getRegEx(String variable, int arg) {
        return findRegEx(variable, NDLine.this.getArgAsString(arg));
    }
    
    /**
     * Returns the line of a quantified formula as a regular expression.
     * @return The second argument, regExed using the first, if the main operator
     *          is qa or qe, otherwise "".
     * @throws proofassistant.exception.WrongLineTypeException
     */
    public String getQuantifierRegEx() throws WrongLineTypeException {
        if (getMainOp().equals("qa") || getMainOp().equals("qe")) {
            return findRegEx(NDLine.this.getArgAsString(1), NDLine.this.getArgAsString(2));
        } else {
            throw new WrongLineTypeException("Must be a quantified line.");
        }
    }
    
    /**
     * Checks whether this NDLine matches the argument of a quantified line
     * @param quantifiedLine A quantified line to compare to.
     * @return True, if this argument matches the quantified line.
     * @throws WrongLineTypeException if quantifiedLine does not have a quantifier
     *          as its main operation.
     */
    public boolean matchesQuantifiedNDLine(NDLine quantifiedLine) 
            throws WrongLineTypeException {
        System.out.println("New method says " + formula.isInstanceOf(quantifiedLine.formula));
        if (quantifiedLine.getMainOp().equals("qa") 
                || quantifiedLine.getMainOp().equals("qe")) {
            return formula.isInstanceOf(quantifiedLine.formula);
        } else {
            throw new WrongLineTypeException("Must be a quantified line.");
        }
    }
    
    public String getQuantifierInstanceUsing(String term) throws WrongLineTypeException {
        if (getMainOp().equals("qa") || getMainOp().equals("qe")) {
            return replace(NDLine.this.getArgAsString(2), NDLine.this.getArgAsString(1), term);
        } else {
            throw new WrongLineTypeException("Must be a quantified line.");
        }
    }
    
    public NDFormula getQuantifierInstanceUsing(NDAtom term) throws WrongLineTypeException {
        if (formula.isQuantifier()) {
            return formula.instantiateWith(term);
        } else {
            throw new WrongLineTypeException("Must be a quantified line.");
        }
    }
    
    
    /**
     * Checks if this NDLine matches the regExed line of another.
     * 
     * @param otherLine The line to check the arguments of.
     * @return True, if the indicated argument matches otherLine, otherwise false.
     */
    public boolean matchesNonUniRegEx(NDLine otherLine) {
        return getLine().matches(otherLine.getNonQaRegEx());
    }
    
    /**
     * Checks if this NDLine matches the regExed argument of another.
     * 
     * @param otherLine The line to check the arguments of.
     * @param arg The argument to check (1st or 2nd)
     * @return True, if the indicated argument matches otherLine, otherwise false.
     */
    public boolean matchesNonUniRegEx(NDLine otherLine, int arg) {
        switch (arg) {
            case 1 : return getLine().matches(otherLine.getNonQaFirstArgRegEx());
            case 2 : return getLine().matches(otherLine.getNonQaSecondArgRegEx());
            default : return false;
        }
    }
    
    /**
     * Checks if this NDLine equals the argument of another.
     * 
     * @param otherLine The line to compare to.
     * @param arg The argument of the other line to compare to.
     * @return True, if the other line's arg argument matches this line.
     */
    public boolean equalsArgOf(NDLine otherLine, int arg) {
        switch (arg) {
            case 1 : return getLine().equals(otherLine.getArgAsString(1));
            case 2 : return getLine().equals(otherLine.getArgAsString(2));
            default : return false;
        }
    }
    
    public int getLength() {
        return parsedLine.length();
    }
    
    /**
     * Replaces all instances of phrase in substance with replacement, ignoring TeX commands
     * 
     * If substance = "hello", phrase="l" and replacement="p", the output will be "heppo".
     * @param substance The string to apply substitutions to
     * @param phrase The string in substance to be replaced
     * @param replacement The string to replace phrase with
     * @return
     */
    public static String replace(String substance, String phrase, String replacement) { // Replace all instances of phrase in substance with replacement
//        System.out.println("     -- Replace --");
        
        boolean inCommand = false;
        ArrayList<String> partsOfLine = new ArrayList<>();
        
        phrase = phrase.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\+", "\\\\+"); // set up regex to deal with brackets
        phrase = phrase.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\+", "\\\\+"); // set up regex to deal with brackets
//        replacement = replacement.replace("\\", "\\\\");
        
        String currentPart = "";
        boolean ignorePlusTimes = true;
        if (phrase.contains("/")) {
            ignorePlusTimes = false;
        }
        
        for (int i = 0; i < substance.length(); i++) {
            if (substance.charAt(i) == '\\' && (i == (substance.length()-1) || (substance.charAt(i+1) != '(' && substance.charAt(i+1) != ')' ))) {
                inCommand = true;
                partsOfLine.add(currentPart);
                currentPart = "";
            } else if (substance.charAt(i) == '/' && ignorePlusTimes) {
                inCommand = true;
                partsOfLine.add(currentPart);
                currentPart = "";
            }
            if (!inCommand) {
                currentPart = currentPart + substance.charAt(i);
            }
            if (substance.charAt(i) == '{') {
                inCommand = false;
            } else if (substance.charAt(i) == '[') {
                inCommand = false;
            }
        }
        partsOfLine.add(currentPart);
        
        
        
        for (int i = 0; i < partsOfLine.size(); i++) {
//            System.out.println("     current: " + partsOfLine.get(i));
//            System.out.println("      replace " + phrase + " with " + replacement);
            partsOfLine.set(i, partsOfLine.get(i).replaceAll(phrase, replacement));
//            System.out.println("        result: " + partsOfLine.get(i));
        }
        
        String result = "";
        int k = 0;
        for (int i = 0; i < substance.length(); i++) {
            if (substance.charAt(i) == '\\' && (i == (substance.length()-1) || (substance.charAt(i+1) != '(' && substance.charAt(i+1) != ')' ))) {
                result = result + partsOfLine.get(k);
                k++;
                inCommand = true;
            } else if (substance.charAt(i) == '/' && ignorePlusTimes) {
                result = result + partsOfLine.get(k);
                k++;
                inCommand = true;
            }
            if (inCommand) {
                result = result + substance.charAt(i);
            }
            if (substance.charAt(i) == '{' || substance.charAt(i) == '[') {
                inCommand = false;
            }
        }
        if (partsOfLine.size() > k){
            result = result + partsOfLine.get(k);
        }
        
//        System.out.println("           returning " + result);
        return result;
    }
    
    public ArrayList<String> getAllTerms() {
//        System.out.println("GET ALL TERMS");
//        System.out.println(line);
        String[] arrayOfLine = line.split("");
        ArrayList<String> termsInLine = new ArrayList<>();
        boolean inCommand = false;
        
        for (int i = 0; i < arrayOfLine.length; i++) {
            if (arrayOfLine[i].equals("\\") || arrayOfLine[i].equals("/")) { // We're going into a tex command
                inCommand = true;
            } else if (arrayOfLine[i].equals("{") || arrayOfLine[i].equals("[")) { // We've come out of a tex command
                inCommand = false;
            } else if (arrayOfLine[i].equals("}") || arrayOfLine[i].equals("]")){ // Ignore }, they're everywhere
                
            } else if (!inCommand){ // If we're not in a tex command
                String termToCheck = arrayOfLine[i]; // Prepare a term to check
                boolean checkingForPrimes = true;
                while (checkingForPrimes && i < arrayOfLine.length - 1) { // Check to see if this term is followed by any '. If so, add them to the term
                    if (arrayOfLine[i+1].equals("'")) {
                        termToCheck = termToCheck + "'";
                        i++;
                    } else {
                        checkingForPrimes = false;
                    }
                }
                
                if (!termsInLine.contains(termToCheck)) {
                    termsInLine.add(termToCheck);
//                    System.out.println("added " + termToCheck);
                }
            }
        }
        
        return termsInLine;
    }
    
    public boolean isContextless() {
        return context.equals("");
    }
    
    public boolean isIdBoxLine() {
        return type == ID_BOX_START || type == ID_BOX_END 
                || type == ID_BOX_LINE || type == EQU_ID_BOX_START;
    }
    
    public String getJustLineNum() {
        if (isSpecial()) {
            return specialLineNum;
        } else {
            return "" + lineNum;
        }
    }
    
    public String getJustLineNum(int i) {
        if (isSpecial()) {
            return specialLineNum;
        } else {
            if (Globals.numberTopDown) {
                return "" + (i + 1 + Globals.lineIncrement);
            }
            return "" + (lineNum + Globals.lineIncrement);
        }
    }
    
    public boolean getIsAllowedInContext(String context) {
        return getContext().equals(context) || isContextless() || context.equals("");
    }
    
    /**
     * Check if this NDLine has the same context as another.
     * @param otherLine The NDLine to compare.
     * @return true if otherLine has the same context, or either this or 
     *          otherLine are contextless. Otherwise returns false.
     */
    public boolean hasSameContextAs(NDLine otherLine) {
        return getContext().equals(otherLine.getContext()) 
                || isContextless() 
                || otherLine.isContextless();
    }
    
//    public String replace(String phrase, String replacement, int argToReturn) { // Replace all instances of phrase in the argToReturn of THIS NDLine with replacement
//        if (argToReturn == 1) {
//            return line;
//        } else if (argToReturn == 2) {
//            return replace(secondArg, phrase, replacement);
//        }
//        return line;
//    }
}
