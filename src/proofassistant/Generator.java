/*
 * The MIT License
 *
 * Copyright 2014 Declan.
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
 * Generator Help Class.
 * I used this because I was lazy and didn't want to write out heaps of identical code.
 * @author Declan Thompson
 */
public class Generator {
    public static void main(String args[]) {
        String[] rules = {"conIntro", "conElim", "disIntro", "disElim",
                            "impIntro", "impElim", "equIntro", "equElim",
                            "negIntro", "negElim", "qaIntro", "qaElim",
                            "qeIntro", "qeElim", "eqIntro", "eqElim",
                            "eqIdentityBoxes", "doubleNegation", "Q", "induction",
                            "equIdentityBoxes", "autoParameters", "universalsShortcuts",
                            "showContext", "boxIntro", "boxElim", "diaIntro",
                            "diaElim", "atIntro", "atElim", "sameLine"};
        
        String[] symbols = {"dis", "imp", "equ", "qa", "qe", "time", "noteq", "self"};
        char[] replace = {'V', '.', '3', 'A', 'E', '8', '1', '\\'}; 
        String fields[] = {"lineField"};
        
//        premiseField.getInputMap().put(KeyStroke.getKeyStroke('>'), "imp");
//        premiseField.getActionMap().put("imp", new SymbolAction(premiseField, Globals.operators.get("imp")));
        
        for (int j = 0; j < fields.length; j++){
        for (int i = 0; i < symbols.length; i++) {
            System.out.println(fields[j] + ".getInputMap().put(KeyStroke.getKeyStroke(\"meta " + replace[i] + "\"), \"" + symbols[i] + "\");");
        }
        }
        
//        for (int i = 0; i < rules.length; i++) {
//            System.out.println("if ("+ rules[i]+"CheckBox.isSelected()) {\n" +
//"            preset.add(\""+ rules[i] +"\");\n" +
//"        }");
//        }
    }
}
