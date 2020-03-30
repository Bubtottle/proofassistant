/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 *
 * @author dtho139
 */
public class Globals {
    
    // General use globals
    public static int lineNum = 0;
    public static int specialLineNum = -10;
    public static int proofHeight = 0;
    public static int proofWidth = 500;
    public static int currentGoalIndex = -1;
    public static int currentResourceIndex = -1;
    public static ProofMethods assist;
    public static NDLine[] proofArray;
    public static TermStore terms = new TermStore();
    public static JScrollPane scrollpane;
    public static String newProofBoxPrems = "";
    public static String newProofBoxConc = "";
    public static RulePalette rulePal;
    public static StatusBar status;
    public static String defaultContext;
    public static String defaultBoxDiaCharacter = "\u25c7";
    
    // Line number modifiers
    public static boolean numberTopDown = false;
    public static int lineIncrement = 0;
    
    public static String getDefaultContext() {
        if (terms != null
                && terms.isEmpty() || defaultContext == null) {
            defaultContext = terms.getNewContext();
            return defaultContext;
        } else if (defaultContext != null) {
            return defaultContext;
        }
        return "";
    }
    
    public static boolean reverseUndo = false;
    public static boolean editable = true;
    
    public static ProofFrame frame;
    
    // Stuff for keeping track of rules used
    public static HashSet<String> rulesUsed = new HashSet<>();
    public static ArrayList<ProofSystem> listOfSystems = new ArrayList<>();
    public static HashMap<String, Boolean> allowedRules = new HashMap<>();
    public static String currentPreset = "";
    public static HashSet<String> termsUsed = new HashSet<>();
    
    public static void setDefaultRulesAllowed() {
        allowedRules.put("Elim", false); // for lines with no main operator
        allowedRules.put("Intro", false);
        allowedRules.put("conElim", true);
        allowedRules.put("conIntro", true);
        allowedRules.put("disElim", true);
        allowedRules.put("disIntro", true);
        allowedRules.put("impElim", true);
        allowedRules.put("impIntro", true);
        allowedRules.put("equElim", true);
        allowedRules.put("equIntro", true);
        allowedRules.put("negElim", true);
        allowedRules.put("negIntro", true);
        allowedRules.put("qaElim", true);
        allowedRules.put("qaIntro", true);
        allowedRules.put("qeElim", true);
        allowedRules.put("qeIntro", true);
        allowedRules.put("falsumElim", false);
        allowedRules.put("falsNeElim", false);
        allowedRules.put("doubleNegation", false);
        allowedRules.put("eqElim", true);
        allowedRules.put("eqIntro", true);
        allowedRules.put("Q", false);
        allowedRules.put("induction", false);
        allowedRules.put("equIdentityBoxes", false);
        allowedRules.put("autoParameters", true);
        allowedRules.put("universalsShortcuts", false);
        allowedRules.put("eqIdentityBoxes", false);
        allowedRules.put("sameLine", true);
        allowedRules.put("showContext", false);
        allowedRules.put("diaIntro", false);
        allowedRules.put("diaElim", false);
        allowedRules.put("boxIntro", false);
        allowedRules.put("boxElim", false);
        allowedRules.put("atIntro", false);
        allowedRules.put("atElim", false);
        allowedRules.put("nomIntro", false);
        allowedRules.put("nomElim", false);
        allowedRules.put("selfIntro", false);
        allowedRules.put("selfElim", false);
        allowedRules.put("secondOrder", false);
    }
    
    public static void setRulesAllows(ProofSystem toUse) {
        allowedRules.put("conIntro", toUse.contains("conIntro"));
        allowedRules.put("conElim", toUse.contains("conElim"));
        allowedRules.put("disIntro", toUse.contains("disIntro"));
        allowedRules.put("disElim", toUse.contains("disElim"));
        allowedRules.put("impIntro", toUse.contains("impIntro"));
        allowedRules.put("impElim", toUse.contains("impElim"));
        allowedRules.put("equIntro", toUse.contains("equIntro"));
        allowedRules.put("equElim", toUse.contains("equElim"));
        allowedRules.put("negIntro", toUse.contains("negIntro"));
        allowedRules.put("negElim", toUse.contains("negElim"));
        allowedRules.put("qaIntro", toUse.contains("qaIntro"));
        allowedRules.put("qaElim", toUse.contains("qaElim"));
        allowedRules.put("qeIntro", toUse.contains("qeIntro"));
        allowedRules.put("qeElim", toUse.contains("qeElim"));
        allowedRules.put("eqIntro", toUse.contains("eqIntro"));
        allowedRules.put("eqElim", toUse.contains("eqElim"));
        allowedRules.put("eqIdentityBoxes", toUse.contains("eqIdentityBoxes"));
        allowedRules.put("doubleNegation", toUse.contains("doubleNegation"));
        allowedRules.put("Q", toUse.contains("Q"));
        allowedRules.put("induction", toUse.contains("induction"));
        allowedRules.put("equIdentityBoxes", toUse.contains("equIdentityBoxes"));
        allowedRules.put("autoParameters", toUse.contains("autoParameters"));
        allowedRules.put("universalsShortcuts", toUse.contains("universalsShortcuts"));
        allowedRules.put("showContext", toUse.contains("showContext"));
        allowedRules.put("boxIntro", toUse.contains("boxIntro"));
        allowedRules.put("boxElim", toUse.contains("boxElim"));
        allowedRules.put("diaIntro", toUse.contains("diaIntro"));
        allowedRules.put("diaElim", toUse.contains("diaElim"));
        allowedRules.put("atIntro", toUse.contains("atIntro"));
        allowedRules.put("atElim", toUse.contains("atElim"));
        allowedRules.put("sameLine", toUse.contains("sameLine"));
        allowedRules.put("nomIntro", toUse.contains("nomIntro"));
        allowedRules.put("nomElim", toUse.contains("nomElim"));
        allowedRules.put("selfIntro", toUse.contains("selfIntro"));
        allowedRules.put("selfElim", toUse.contains("selfElim"));
        allowedRules.put("secondOrder", toUse.contains("secondOrder"));
    }
    
    // Stacks for Undo
    public static Stack<NDLine[]> proofsForUndo = new Stack<>();
    public static Stack<Integer> goalsForUndo = new Stack<>();
    public static Stack<Integer> resourcesForUndo = new Stack<>();
    public static Stack<Integer> lineNumsForUndo = new Stack<>();
    public static Stack<HashSet<String>> rulesUsedForUndo = new Stack<>();
    public static Stack<HashSet<String>> termsUsedForUndo = new Stack<>();
    
    // Stacks for Redo
    public static Stack<NDLine[]> proofsForRedo = new Stack<>();
    public static Stack<Integer> goalsForRedo = new Stack<>();
    public static Stack<Integer> resourcesForRedo = new Stack<>();
    public static Stack<Integer> lineNumsForRedo = new Stack<>();
    public static Stack<HashSet<String>> rulesUsedForRedo = new Stack<>();
    public static Stack<HashSet<String>> termsUsedForRedo = new Stack<>();
    
    public static void clearUndo() {
        proofsForUndo.clear();
        goalsForUndo.clear();
        resourcesForUndo.clear();
        lineNumsForUndo.clear();
        rulesUsedForUndo.clear();
        termsUsedForUndo.clear();
        if (frame != null) {
            frame.setUndoable(false);
        }
    }
    
    public static void clearRedo() {
        proofsForRedo.clear();
        goalsForRedo.clear();
        resourcesForRedo.clear();
        lineNumsForRedo.clear();
        rulesUsedForRedo.clear();
        termsUsedForRedo.clear();
        if (frame != null) {
            frame.setRedoable(false);
        }
    }
    
    // Booleans for changing the proof system
    public static boolean runMagicModeWithQa = false;
    public static boolean reverse2PremIntro = false;
    
    // Booleans for visual tweaks
    public static boolean useNeq = true;
    public static boolean showBrackets = false;
    public static boolean outOfScopeIsGrey = false;
    public static float zoomFactor = 1;
    public static boolean niceBrackets = true;
    
    
    // Prep for Robinson
    public static String[] qAxioms = {"\\qa{x}{Sx\\neq0}", "\\qa{x}{\\qa{y}{\\imp{Sx=Sy}{x=y}}}", "\\qa{x}{(x+0)=x}", "\\qa{x}{\\qa{y}{(x+Sy)=S(x+y)}}", "\\qa{x}{(x" + "\u22c5" + "0)=0}", "\\qa{x}{\\qa{y}{(x" + "\u22c5" + "Sy)=x+(x" + "\u22c5" + "y)}}"};
    public static String[] qAxiomsLineNums = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6"};
//    private static final NDLine q1 = new NDLine("\\qa{x}{sx\\neqx}", 11, -1);
//    private static final NDLine q2 = new NDLine("\\qa{x}{\\qa{y}{\\imp{sx=sy}{x=y}}}", 11, -2);
//    private static final NDLine q3 = new NDLine("\\qa{x}{(x+0)=x}", 11, -3);
//    private static final NDLine q4 = new NDLine("\\qa{x}{\\qa{y}{(x+sy)=s(x+y)}}", 11, -4);
//    private static final NDLine q5 = new NDLine("\\qa{x}{(x" + "\u22c5" + "0)=0}", 11, -5);
//    private static final NDLine q6 = new NDLine("\\qa{x}{\\qa{y}{(x" + "\u22c5" + "sy)=x+(x" + "\u22c5" + "y)}}", 11, -6);
    public static ArrayList<String> myFunLines = new ArrayList<>();
    public static ArrayList<String> myFunLineNums = new ArrayList<>();
    
    public static ArrayList<String> extraNDLines = new ArrayList<>();
    public static ArrayList<String> extraLineNums = new ArrayList<>();
    public static NDLine[] extraLines;
    public static void createExtraLines() {
        startArities();
        extraNDLines.clear();
        extraLineNums.clear();
        if (allowedRules.get("Q")) {
            for (int i = 0; i < qAxioms.length; i++) {
                extraNDLines.add(qAxioms[i]);
                extraLineNums.add(qAxiomsLineNums[i]);
            }
        }
        extraNDLines.addAll(myFunLines);
        extraLineNums.addAll(myFunLineNums);
        
        extraLines = new NDLine[extraNDLines.size()];
        for (int i = 0; i < extraLines.length; i++) {
            extraLines[i] = new NDLine(extraNDLines.get(i), 11, extraLineNums.get(i));
            extraLines[i].setContext("");
        }
        
//        NDLine q1 = new NDLine("\\qa{x}{Sx\\neqx}", 11, -1);
//        q1.setContext("");
//        NDLine q2 = new NDLine("\\qa{x}{\\qa{y}{\\imp{Sx=Sy}{x=y}}}", 11, -2);
//        q2.setContext("");
//        NDLine q3 = new NDLine("\\qa{x}{(x+0)=x}", 11, -3);
//        q3.setContext("");
//        NDLine q4 = new NDLine("\\qa{x}{\\qa{y}{(x+Sy)=S(x+y)}}", 11, -4);
//        q4.setContext("");
//        NDLine q5 = new NDLine("\\qa{x}{(x" + "\u22c5" + "0)=0}", 11, -5);
//        q5.setContext("");
//        NDLine q6 = new NDLine("\\qa{x}{\\qa{y}{(x" + "\u22c5" + "Sy)=x+(x" + "\u22c5" + "y)}}", 11, -6);
//        q6.setContext("");
//        NDLine[] result = {q1, q2, q3, q4, q5, q6};
//        extraLines = result;
    }
    public static boolean qShowNumbers = false;
    
    
    // Hashmap for arity lists
    public static HashMap<String, Integer> arity = new HashMap<>();
    public static String aritiyS = "a0, b0, c0, d0, e0, f1, g1, h2, S1, s0, t0, u0";
    public static void setArities() {
//        System.out.println("arityS is " + arityS);
        String currentLetter = "";
        String currentArity = "";
        for (int i = 0; i < aritiyS.length(); i++) {
            char c = aritiyS.charAt(i);
            if (c == ',' && !currentLetter.equals("")) {
                if (currentArity.equals("")) {
                    arity.remove(currentLetter);
                } else {
//                System.out.println(currentLetter + Integer.parseInt(currentArity));
                    arity.put(currentLetter, Integer.parseInt(currentArity));
                }
                currentLetter = "";
                currentArity = "";
            } else if ((c > 64 && c < 91) || (c > 96 && c < 123) || c == '\'') {
                currentLetter = currentLetter + c;
            } else if (c > 47 && c < 58) {
                currentArity = currentArity + c;
            }
        }
        if (!currentLetter.equals("")){
            if (currentArity.equals("")) {
                arity.remove(currentLetter);
            } else {
//            System.out.println(currentLetter + Integer.parseInt(currentArity));
                arity.put(currentLetter, Integer.parseInt(currentArity));
            }
        }
        if (assist != null) {
            proofArray = assist.getProofArray();
            for (int i = 0; i < proofArray.length; i++) {
//                System.out.println(proofArray[i].getLine());
                proofArray[i].refreshLine();
            }
            assist.setProofArray(proofArray);
        }
    }
    public static void startArities() {
//        System.out.println("arityS is " + arityS);
        String currentLetter = "";
        String currentArity = "";
        for (int i = 0; i < aritiyS.length(); i++) {
            char c = aritiyS.charAt(i);
            if (c == ',' && !currentLetter.equals("")) {
                if (currentArity.equals("")) {
                    arity.remove(currentLetter);
                } else {
//                System.out.println(currentLetter + Integer.parseInt(currentArity));
                    arity.put(currentLetter, Integer.parseInt(currentArity));
                }
                currentLetter = "";
                currentArity = "";
            } else if ((c > 64 && c < 91) || (c > 96 && c < 123) || c == '\'') {
                currentLetter = currentLetter + c;
            } else if (c > 47 && c < 58) {
                currentArity = currentArity + c;
            }
        }
        if (!currentLetter.equals("")){
            if (currentArity.equals("")) {
                arity.remove(currentLetter);
            } else {
//            System.out.println(currentLetter + Integer.parseInt(currentArity));
                arity.put(currentLetter, Integer.parseInt(currentArity));
            }
        }
    }
    
    // Hashmap for operators
    public static HashMap<String, String> operators = new HashMap<>();   
    //    public static String[][] operators = new String[2][10];
//        operators[0][0] = "neg"; operators[1][0] = "\u007e";
//        operators[0][1] = "dis"; operators[1][1] = "\u2228";
//        operators[0][2] = "con"; operators[1][2] = "\u0026";
//        operators[0][3] = "imp"; operators[1][3] = "\u2283";
//        operators[0][4] = "equ"; operators[1][4] = "\u2261";
//        operators[0][5] = "qa"; operators[1][5] = "\u2200";
//        operators[0][6] = "qe"; operators[1][6] = "\u2203";
//        operators[0][7] = "falsum"; operators[1][7] = "u22a5";
//        operators[0][8] = "eq"; operators[1][8] = "\u003d";
//        operators[0][9] = "noteq"; operators[1][9] = "\u2260";
    
    public static int currentOpsIndex = 0;
    
    public static void setDefaultOps() {
        operators.put("neg", "\u007e");
        operators.put("dis", "\u2228");
        operators.put("con", "\u0026");
        operators.put("imp", "\u2283");
        operators.put("equ", "\u2261");
        operators.put("qa", "\u2200");
        operators.put("qe", "\u2203");
        operators.put("falsum", "\u22a5");
        operators.put("eq", "\u003d");
        operators.put("noteq", "\u2260");
        operators.put("plus", "\u002b");
        operators.put("time", "\u22c5");
        operators.put("LEFTBOX", "\u005b");
        operators.put("RIGHTBOX", "\u005d");
        operators.put("LEFTdia", "\u3008");
        operators.put("RIGHTdia", "\u3009");
        operators.put("at", "\u0040");
        operators.put("box", "\u2610");
        operators.put("dia", "\u25c7");
        operators.put("nom", ":");
        operators.put("self", "\u2193");
        
        currentOpsIndex = 0;
    }
    
    public static void setNonAucklandOps() {
        operators.put("neg", "\u00AC");
        operators.put("dis", "\u2228");
        operators.put("con", "\u2227");
        operators.put("imp", "\u2192");
        operators.put("equ", "\u2194");
        operators.put("qa", "\u2200");
        operators.put("qe", "\u2203");
        operators.put("falsum", "\u22a5");
        operators.put("eq", "\u003d");
        operators.put("noteq", "\u2260");
        operators.put("plus", "\u002b");
        operators.put("time", "\u22c5");
        operators.put("LEFTBOX", "\u005b");
        operators.put("RIGHTBOX", "\u005d");
        operators.put("LEFTdia", "\u3008");
        operators.put("RIGHTdia", "\u3009");
        operators.put("at", "\u0040");
        operators.put("box", "\u2610");
        operators.put("dia", "\u25c7");
        operators.put("nom", ":");
        operators.put("self", "\u2193");
        
        currentOpsIndex = 1;
    }
    
    // Inverted Operators.
    // Used in setLines() of the base NDJustification class, to set any operators
    // correctly when read in from file
    public static HashMap<String, String> operatorsInv = new HashMap<>();    
    private static void invertOperators(String a, String b) {
        operatorsInv.put(b, a);
    }
    public static void invertOps() {
        invertOperators("neg", "\u007e");
        invertOperators("dis", "\u2228");
        invertOperators("con", "\u0026");
        invertOperators("imp", "\u2283");
        invertOperators("equ", "\u2261");
        invertOperators("qa", "\u2200");
        invertOperators("qe", "\u2203");
        invertOperators("falsum", "\u22a5");
        invertOperators("eq", "\u003d");
        invertOperators("noteq", "\u2260");
        invertOperators("plus", "\u002b");
        invertOperators("time", "\u22c5");
        invertOperators("LEFTBOX", "\u005b");
        invertOperators("RIGHTBOX", "\u005d");
        invertOperators("LEFTdia", "\u3008");
        invertOperators("RIGHTdia", "\u3009");
        invertOperators("at", "\u0040");
        invertOperators("box", "\u2610");
        invertOperators("dia", "\u25c7");
        invertOperators("nom", ":");
        invertOperators("self", "\u2193");
        invertOperators("neg", "\u00AC");
        invertOperators("con", "\u2227");
        invertOperators("imp", "\u2192");
        invertOperators("equ", "\u2194");
    }
    
    // Determine which style proof to use
    public static final int AUCKLANDSTYLE = 1;
    public static final int STANFORDSTYLE = 2;
    
    public static int proofStyle = AUCKLANDSTYLE;
    
    // Should disjunction introduction require you to select a disjunct that's 
    // available?
    public static boolean requireDisSelect = false;
    
}
