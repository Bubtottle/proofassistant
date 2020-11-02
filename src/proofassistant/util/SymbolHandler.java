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
package proofassistant.util;

import java.util.HashMap;
import java.util.Map;
import proofassistant.exception.MissingArityException;
import proofassistant.core.NDAtom;

/** The SymbolHandler class deals with parsing symbols.
 * It contains methods for converting symbols to TeX and Java format, and keeps
 * track of arities.
 *
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class SymbolHandler {
    private Map<String, String> operators;   
    private TermStore terms;
    
    /**
     * Class constructor.
     * This constructor uses the default operators.
     */
    public SymbolHandler() {
        operators  = new HashMap<>();
        setDefaultOps();
        terms = new TermStore();
    }
    
    
    // ACCESSOR METHODS
    
    /**
     * Get the parsed output for the given symbol.
     * @param symbol The symbol to parse.
     * @return The parsed symbol.
     */
    public String getParse(String symbol) {
        String out = operators.get(symbol);
        if (out == null) {
            throw new ArrayIndexOutOfBoundsException("SymbolHandler cannot find this symbol: " + symbol);
        }
        return out;
    }
    
    /**
     * Get the symbol in TeX code format.
     * Currently, this just returns the symbol.
     * 
     * @param symbol The symbol to parse.
     * @return The TeX code.
     */
    public String getTeX(String symbol) {
        return "\\" + symbol;
    }
    
    /**
     * Get the arity of a term.
     * @param term a string, the term to find the arity of.
     * @return An integer, the arity of the term.
     * @throws MissingArityException if terms doesn't know this term.
     */
    public int getArity(String term) throws MissingArityException {
        return terms.getArity(term);
    }
    
    public NDAtom getNewTerm() {
        return terms.getNewTerm();
    }
    
    // MUTATOR METHODS
    
    public void setDefaultOps() {
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
    }
    
    public void processTerm(NDAtom term) {
        terms.processLine(term.getTeX());
    }
    
    
}
