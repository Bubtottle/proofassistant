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
import proofassistant.exception.MissingArityException;

/** The SymbolHandler class deals with parsing symbols.
 * It contains methods for converting symbols to TeX and Java format, and keeps
 * track of arities.
 *
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class SymbolHandler {
    private HashMap<String, String> operators;   
    private HashMap<String, Integer> arities;
    
    /**
     * Class constructor.
     * This constructor uses the default operators.
     */
    public SymbolHandler() {
        operators  = new HashMap<>();
        setDefaultOps();
        arities = new HashMap<>();
        startArities();
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
    
    public int getArity(String term) throws MissingArityException {
        if (arities.containsKey(term)) {
            return arities.get(term);
        } else {
            throw new MissingArityException("Cannot determine arity of " + term);
        }
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
    
    public static String aritiyS = "a0, b0, c0, d0, e0, f1, g1, h2, S1, s0, t0, u0";
    public void startArities() {
//        System.out.println("arityS is " + arityS);
        String currentLetter = "";
        String currentArity = "";
        for (int i = 0; i < aritiyS.length(); i++) {
            char c = aritiyS.charAt(i);
            if (c == ',' && !currentLetter.equals("")) {
                if (currentArity.equals("")) {
                    arities.remove(currentLetter);
                } else {
//                System.out.println(currentLetter + Integer.parseInt(currentArity));
                    arities.put(currentLetter, Integer.parseInt(currentArity));
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
                arities.remove(currentLetter);
            } else {
//            System.out.println(currentLetter + Integer.parseInt(currentArity));
                arities.put(currentLetter, Integer.parseInt(currentArity));
            }
        }
    }
}
