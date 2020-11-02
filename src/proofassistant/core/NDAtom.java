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
package proofassistant.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import proofassistant.exception.MissingArityException;
import proofassistant.exception.WrongLineTypeException;
import proofassistant.util.SymbolHandler;

/**
 * The NDAtom class provides a container for natural deduction terms.
 * Terms are taken to cover predicates and propositions, as well as function 
 * symbols.
 *
 * @author Declan Thompson
 */
public class NDAtom {
    // Atom types
    static final public int PROPOSITION = 1;
    static final public int PREDICATE = 2;
    static final public int TERM = 3;
    
    private final String operator;
    private final List<NDAtom> arguments;
    private int type;
    
    /**
     * Create a new NDAtom from a LaTeX macro.
     * Input macros must have all terms surrounded by ( ). If a term is not 
     * surrounded by ( ) it will be treated as either a predicate (in case brackets
     * are found elsewhere in the macro) or as a proposition.
     * 
     * @param macro A string in bracket format (see description).
     * @param syms
     * @throws MissingArityException 
     */
    public NDAtom(String macro) throws MissingArityException {
        arguments = new ArrayList<>();
        
        macro = macro.replace("[", "").replace("]", "");
        
        // Find the first set of brackets
        int bracketLocation = macro.indexOf("("); // Find the first brackets
        if (bracketLocation == -1) {
            // There are no brackets, so this must be a proposition
            type = PROPOSITION;
            operator = macro;
        } else {
            // There are brackets
            if (bracketLocation == 0) {
                // This is surrounded by ( ), so it's a term
                type = TERM;
                macro = macro.substring(1, macro.length() - 1); // Remove the outer brackets.
                bracketLocation = macro.indexOf("("); // Find the next brackets
            } else {
                // It must be a predicate!
                type = PREDICATE;
            }
            if (bracketLocation == -1) {
                // The formula has no arguments - it's a constant
                operator = macro;
            } else {
                // So: we have either a predicate or a term, and it has some arguments
                // Add operator, then the arguments one by one
                operator = macro.substring(0,bracketLocation); 
                // Add the arguments
                int i = bracketLocation;
                int start;
                while (i < macro.length()) {
                    start = i;
                    i++;
                    int bracketCount = 1;
                    while (bracketCount > 0) {
                        switch (macro.charAt(i)) {
                            case '(' : bracketCount++; break;
                            case ')' : bracketCount--; break;
                        }
                        i++;
                    }
                    arguments.add(new NDAtom(macro.substring(start, i)));
                    i++;
                }         
            }
        }
    }
    
    /**
     * Default constructor (fully explicit).
     * @param op
     * @param args
     * @param tp
     * @param syms 
     */
    public NDAtom(String op, List<NDAtom> args, int tp) {
        operator = op;
        arguments = args;
        type = tp;
    }
            
    // PREDICATES

    /**
     * Checks if this atom is an instance of otherAtom, using a variable assignment.
     * If any variables in the assignment are unassigned this method does its best
     * to find an assignment that works. In particular, if otherAtom is a variable
     * currently assigned to null, then it is reassigned to this atom, and the 
     * method returns true.
     * 
     * @param otherAtom The NDAtom to comapare to. This is the atom that has variables
     *          in it.
     * @param assignments A HashMap, taking NDAtom variables to assignments. Initially,
     *          each variable is assigned to null. 
     * @return True iff this NDAtom corresponding to replacing the variables in otherAtom,
     *          as per assignments.
     */
    public boolean isInstanceUsing(NDAtom otherAtom, 
            Map<NDAtom, NDAtom> assignments) {
        // Check that the atoms are the same type, and not propositions
        if (type != otherAtom.type || type == PROPOSITION) return false;
        // Check if the otherAtom is a variable we're looking for
        if (assignments.containsKey(otherAtom)) {
            // So the otherAtom is a variable.
            if (assignments.get(otherAtom) == null) {
                // If that variable hasn't yet been assigned, we can assign it
                // to the current atom and return true
                assignments.put(otherAtom, this);
                return true;
            } else {
                // That variable has already been assigned to something else,
                // so we must return false.
                return this.equals(assignments.get(otherAtom));
            }
        }
        // So otherAtom is not a variable we're looking for.
        // Check that these two atoms look the same
        if (!hasSameImmediateFormAs(otherAtom)) return false;
        // Check each argument is an instance using the assignments
        boolean out = true;
        for (int i = 0; i < arguments.size(); i++) {
            out &= arguments.get(i).isInstanceUsing(otherAtom.arguments.get(i), 
                    assignments);
        }
        return out;
    }
    
    /**
     * Checks to see if this atom makes use of a given term.
     * 
     * @param term An NDAtom term to look for.
     * @return True, if this atom contains the term, and false otherwise.
     */
    public boolean containsTerm(NDAtom term) {
        if (equals(term)) return true;
        for (NDAtom arg : arguments) {
            if (arg.containsTerm(term)) return true;
        }
        return false;
    }
    
    /**
     * Checks to see that this atom is the same sort of atom as another.
     * In particular, this checks that
     * - The two atoms are of the same type
     * - The operator of the two atoms matches
     * - The two atoms have the same number of arguments.
     * This method does not check whether the arguments of the atoms are also
     * the same.
     * 
     * @param otherAtom
     * @return 
     */
    private boolean hasSameImmediateFormAs(NDAtom otherAtom) {
        return type == otherAtom.type && operator.equals(otherAtom.operator)
                && arguments.size() == otherAtom.arguments.size();
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof NDAtom)) return false;
        NDAtom otherAtom = (NDAtom)otherObject;
        if (!hasSameImmediateFormAs(otherAtom)) return false;
        return arguments.equals(otherAtom.arguments);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.operator);
        hash = 43 * hash + Objects.hashCode(this.arguments);
        hash = 43 * hash + this.type;
        return hash;
    }
    
    // ACCESSOR METHODS
    
    public NDAtom getInstanceUsing(Map<NDAtom, NDAtom> assignments) 
            throws WrongLineTypeException {
        if (type == PROPOSITION) throw new WrongLineTypeException("Cannot instantiate " + getParse());
        if (assignments.containsKey(this)) {
            return assignments.get(this);
        } else {
            ArrayList<NDAtom> newArgs = new ArrayList<>();
            for (NDAtom arg : arguments) {
                newArgs.add(arg.getInstanceUsing(assignments));
            }
            return new NDAtom(operator, newArgs, type);
        }
    }
    
    public String getTeX() {
        String tex = operator;
        for (NDAtom arg : arguments) {
            tex += arg.getTeX();
        }
        return tex;
    }
    
    public String getParse() {
        String out = operator;
        for (NDAtom arg : arguments) {
            out += arg.getParse();
        }
        return out;
    }
    
    /**
     * Get the number of distinct times a given atom is used inside this atom.
     * 
     * @param at The NDAtom to search for.
     * @return The number of times at is used in this NDAtom.
     */
    public int numberOfUsesOf(NDAtom at) {
        if (equals(at)) return 1;
        int total = 0;
        for (NDAtom arg : arguments) {
            total += arg.numberOfUsesOf(at);
        }
        return total;
    }
}
