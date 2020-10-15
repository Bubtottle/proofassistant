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
package proofassistant.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import proofassistant.exception.MissingArityException;
import proofassistant.exception.WrongLineTypeException;
import proofassistant.util.SymbolHandler;

/** The NDFormula class representing a formula in a formal language.
 *
 * @serial Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class NDFormula {
    private final String mainOp;
    private final NDAtom atom;
    private final ArrayList<NDFormula> arguments;
    private final NDAtom mainOpArg;
    private final String context;
    private SymbolHandler symbols;
    
    /**
     * Create a new NDFormula from a LaTeX macro
     * @param macro A String containing a LaTeX macro.
     * @param syms A SymbolHandler to deal with parsing symbols.
     * @throws IndexOutOfBoundsException if the { } brackets don't match in the
     *          macro.
     * @throws proofassistant.exception.MissingArityException
     */
    public NDFormula(String macro, SymbolHandler syms) throws IndexOutOfBoundsException, MissingArityException {
        symbols = syms;
        // Create the argument list
        arguments = new ArrayList<>();
        // Determine if the formula is an operator, or is atomic
        if (macro.indexOf("\\") != 0) {
            // This formula is atomic
            mainOp = null;
            mainOpArg = null;
            atom = new NDAtom(macro, syms);
        } else {
            // This formula is an operator
            atom = null;
            int bracketLocation = macro.indexOf("{");
            if (bracketLocation == -1) {
                // The formula has no arguments
                mainOp = macro.substring(1);
                mainOpArg = null;
            } else {
                // The formula is not an atom
                mainOp = macro.substring(1,bracketLocation);      
                if (mainOp.equals("qa") || mainOp.equals("qe")) {
                    // If we have a quantifier, we take the first argument as 
                    // the mainOpArg
                    // The macro looks like \qa{ ___ }{ ___ } or \qe{ ___ }{ ___ }
                    mainOpArg = new NDAtom("(" + 
                            macro.substring(bracketLocation + 1, macro.indexOf("}"))
                            + ")", syms);
                    bracketLocation = macro.indexOf("}") + 1;
                } else {
                    // The mainOp doesn't take an argument
                    mainOpArg = null;
                }
                // Add the (remaining) arguments
                int i = bracketLocation + 1;
                int start;
                while (i < macro.length()) {
                    start = i;
                    int bracketCount = 1;
                    while (bracketCount > 0) {
                        switch (macro.charAt(i)) {
                            case '{' : bracketCount++; break;
                            case '}' : bracketCount--; break;
                        }
                        i++;
                    }
                    arguments.add(new NDFormula(macro.substring(start, i-1), syms));
                    i++;
                }            
            }
        }
        context = "";
    }
    
    /**
     * Wrap an atom to be an NDFormula.
     * @param anAtom
     * @param syms 
     */
    public NDFormula(NDAtom anAtom, SymbolHandler syms) {
        mainOp = null;
        atom = anAtom;
        arguments = new ArrayList<>();
        mainOpArg = null;
        context = "";
        symbols = syms;
    }
    
    /**
     * Standard constructor method.
     * @param op
     * @param atm
     * @param args
     * @param opArg
     * @param cntxt
     * @param syms 
     */
    public NDFormula(String op, NDAtom atm, ArrayList<NDFormula> args, 
            NDAtom opArg, String cntxt, SymbolHandler syms) {
        mainOp = op;
        atom = atm;
        arguments = args;
        mainOpArg = opArg;
        context = cntxt;
        symbols = syms;
    }
    
    // PREDICATES
    
    public boolean isAtomic() {
        return atom != null;
    }
    
    public boolean isQuantifier() {
        return mainOp.equals("qa") || mainOp.equals("qe");
    }
    
    /**
     * Checks whether this formula is an instance of another quantified formula.
     * This only removes one variable from the line.
     * 
     * @param qform
     * @return
     * @throws WrongLineTypeException 
     */
    public boolean isInstanceOf(NDFormula qform) throws WrongLineTypeException {
        if (!qform.isQuantifier()) {
            throw new WrongLineTypeException();
        }
        HashMap<NDAtom, NDAtom> assignments = new HashMap<>();
        assignments.put(qform.mainOpArg, null);
        return isInstanceUsing(qform.getArg(), assignments);
    }
    
    /**
     * Checks if this formula is an instance of otherFormula, using a variable assignment.
     * If any variables in the assignment are unassigned this method does its best
     * to find an assignment that works. This makes use of the NDAtom.isInstanceUsing()
     * method.
     * 
     * @param otherFormula The NDAFormula to comapare to. This is the formula that has variables
     *          in it.
     * @param assignments A HashMap, taking NDAtom variables to assignments. Initially,
     *          each variable is assigned to null. 
     * @return True iff this NDFormula corresponding to replacing the variables in otherFormula,
     *          as per assignments.
     */
    private boolean isInstanceUsing(NDFormula otherFormula, 
            HashMap<NDAtom, NDAtom> assignments) {
        // First, check that the formulas, overall, look the same.
        if (!hasSameImmediateFormAs(otherFormula)) {
            return false;
        }
        if (isAtomic()) {
            return atom.isInstanceUsing(otherFormula.atom, assignments);
        } else {
            // Since it's not atomic, there must be arguments.
            boolean out = true;
            if (otherFormula.isQuantifier() 
                    && assignments.containsKey(otherFormula.mainOpArg)) {
                // If the other formula is a quantifier, we need to remove and store
                // the current value of its variable.
                NDAtom tempVar = assignments.remove(otherFormula.mainOpArg);
                for (int i = 0; i < arguments.size(); i++) {
                    out &= arguments.get(i).isInstanceUsing(otherFormula.arguments.get(i), assignments);
                }
                assignments.put(otherFormula.mainOpArg, tempVar);
            } else {
                for (int i = 0; i < arguments.size(); i++) {
                    out &= arguments.get(i).isInstanceUsing(otherFormula.arguments.get(i), assignments);
                }
            }            
            return out;
        }
    }
    
    /**
     * Checks to see that this formula is the same sort of formula as another.
     * In particular, this checks that
     * - The two formulas are of the same type (atomic/non-atomic)
     * - If they are non-atomic, the two formulas share the same main operator
     * - If the operator has an argument, the arguments are the same
     * - The formulas have the same number of arguments
     * - The formulas have the same context.
     * This method does not check whether the arguments of the formulas are also
     * the same.
     * 
     * @param otherFormula
     * @return 
     */
    private boolean hasSameImmediateFormAs(NDFormula otherFormula) {
        if (isAtomic()) return otherFormula.isAtomic();
        if (!context.equals(otherFormula.context)) return false;
        // It's not atomic, so it must have arguments and a main operator.
        if (arguments.size() != otherFormula.arguments.size()) return false;
        if (mainOp.equals(otherFormula.mainOp)) {
            if (mainOpArg == null) {
                return otherFormula.mainOpArg == null;
            } else if (otherFormula.mainOpArg == null) {
                return false;
            } else {
                return mainOpArg.equals(otherFormula.mainOpArg);
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof NDFormula)) return false;
        NDFormula otherFormula = (NDFormula)otherObject;
        if (!hasSameImmediateFormAs(otherFormula)) return false;
        return arguments.equals(otherFormula.arguments);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.mainOp);
        hash = 23 * hash + Objects.hashCode(this.atom);
        hash = 23 * hash + Objects.hashCode(this.mainOpArg);
        hash = 23 * hash + Objects.hashCode(this.context);
        return hash;
    }
    
    
    // ACCESSOR METHODS
    
    /**
     * Returns a version of this formula, changing variables using assignments.
     * 
     * @param assignments A HashMap mapping NDAtom variables to NDAtoms
     * @return This NDFormula, but with the variables replaced by their assignemnts.
     */
    public NDFormula getInstanceUsing(Map<NDAtom, NDAtom> assignments) {
        if (isAtomic()) {
            try {
                return new NDFormula(atom.getInstanceUsing(assignments), symbols);
            } catch (WrongLineTypeException ex) {
                Logger.getLogger(NDFormula.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ArrayList<NDFormula> newArgs = new ArrayList<>();
            for (NDFormula arg : arguments) {
                newArgs.add(arg.getInstanceUsing(assignments));
            }
            return new NDFormula(mainOp, atom, newArgs, mainOpArg, context, symbols);
        }
        return null;
    }
    
    public NDFormula instantiateWith(NDAtom term) throws WrongLineTypeException {
        if (!isQuantifier()) throw new WrongLineTypeException(mainOp + " isn't a quantifier");
        
        return getArg().getInstanceUsing(Collections.singletonMap(mainOpArg, term));
    }
    
    /**
     * Get the main operator of this NDFormula.
     * If this NDFormula is atomic, this returns the atom.
     * 
     * @return A string, the main operator of this NDFormula.
     */
    public String getMainOp() {
        return mainOp;
    }
    
    /**
     * Get the (first) argument of this NDFormula.
     * 
     * @return The first argument of this formula
     */
    public NDFormula getArg() {
        return getArg(1);
    }
    
    /**
     * Get an argument of this NDFormula.
     * @param arg The argument to get. Argument indexing starts at 1.
     * @return The argth argument of this formula.
     */
    public NDFormula getArg(int arg) {
        try {
            return arguments.get(arg - 1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            throw new IndexOutOfBoundsException("Trying to get index " + (arg - 1)
                    + " in NDFormula with " + arguments.size() + " elements.");
        }
    }
    
    /**
     * Convert this formula to LaTeX code.
     * 
     * @return A String containing a LaTeX macro for this NDFormula.
     */
    public String getTeX() {
        String tex = isAtomic() ? atom.getTeX() : getMainOperatorTeX();
        for (NDFormula arg : arguments) {
            tex += "{" + arg.getTeX() + "}";
        }
        return tex;
    }
    
    /**
     * Parse this formula, so it looks pretty.
     * 
     * @return A string of the formula, prettified.
     */
    public String getParse() {
        switch (arguments.size()) {
            case 0 : 
                return isAtomic() ? atom.getParse() : getMainOperatorParse();
            case 2 : 
                return "(" + getArg(1).getParse() + getMainOperatorParse()
                        + getArg(2).getParse() + ")";
            default : 
                String out = getMainOperatorParse() + "(";
                for (NDFormula arg : arguments) {
                    out += arg.getParse() + ",";
                }
                return out.substring(0, out.length() - 1) + ")";
        }
    }
    
    // HELPER METHODS
    
    /**
     * Returns a parse of the main operator.
     * If the main operator takes an argument (like qa or qe), this parses the 
     * main operator and its argument correctly. If the main operator doesn't take
     * an argument, this just calls symbols.getParse().
     * 
     * @return A string, the nicely formatted main operator for this formula. 
     */
    private String getMainOperatorParse() {
        switch (mainOp) {
            case "qa" :
            case "qe" : return symbols.getParse(mainOp) + mainOpArg.getParse();
            default : return symbols.getParse(mainOp);
        }
    }
    
    /**
     * Returns a TeX macro for the main operator.
     * If the main operator takes an argument (like qa or qe), this parses the 
     * main operator and its argument correctly. If the main operator doesn't take
     * an argument, this just calls symbols.getTeX().
     * 
     * @return A string, the TeX macro of the main operator for this formula. 
     */
    private String getMainOperatorTeX() {
        switch (mainOp) {
            case "qa" :
            case "qe" : return symbols.getTeX(mainOp) + "{" +  mainOpArg.getTeX() + "}";
            default : return symbols.getTeX(mainOp);
        }
    }
}
