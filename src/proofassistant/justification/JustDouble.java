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
package proofassistant.justification;

import java.util.ArrayList;
import java.util.Arrays;
import proofassistant.Globals;
import proofassistant.Globals;
import proofassistant.core.NDJust;
import proofassistant.core.NDJust;
import proofassistant.core.NDLine;
import proofassistant.core.NDLine;

/**
 * The JustDouble class implements NDJust for justifications requiring 2 args
 * This class replaces dedicated classes for the types of justification contained.
 * 
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class JustDouble implements NDJust {
    // Type Constants
    public static final int CON_INTRO = 3; // Conjunction introduction
    public static final int IMP_INTRO = 9; // Implication introduction
    public static final int IMP_ELIM = 27; // Implication elimination
    public static final int EQU_ELIM = 81; // Equivalence elimination
    public static final int EQ_ELIM = 243; // Equality elimination
    public static final int NEG_ELIM = 729; // Negation elimination
    public static final int NEG_INTRO = 2187; // Negation introduction
    public static final int NOM_ELIM = 6561; // Nominal elimination (hybrid logic)
    
    
    private int type;
    private NDLine firstLine;
    private NDLine secondLine;
    
    public JustDouble(int tp, NDLine one, NDLine two) {
        this.type = tp;
        this.firstLine = one;
        this.secondLine = two;
    }
    
    @Override
    public String getJava() {
        String gap = "";
        String ending = "";
        switch(type) {
            case CON_INTRO :    gap = ", ";
                                ending = ", " + Globals.operators.get("con") + "I"; 
                                break;
            case IMP_INTRO :    gap = "-";
                                ending = ", " + Globals.operators.get("imp") + "I"; 
                                break;
            case IMP_ELIM :     gap = ", ";
                                ending = ", " + Globals.operators.get("imp") + "E"; 
                                break;
            case EQU_ELIM :     gap = ", ";
                                ending = ", " + Globals.operators.get("equ") + "E"; 
                                break;
            case EQ_ELIM :      gap = ", ";
                                ending = ", " + Globals.operators.get("eq") + "E"; 
                                break;
            case NEG_ELIM :     gap = ", ";
                                ending = ", " + Globals.operators.get("neg") + "E"; 
                                break;
            case NEG_INTRO:     gap = "-";
                                ending = ", " + Globals.operators.get("neg") + "I"; 
                                break;
            case NOM_ELIM:      gap = ", ";
                                ending = ", " + Globals.operators.get("neg") + "E"; 
                                break;
        }
        return firstLine.getLineNumOutput() + gap + secondLine.getLineNumOutput() + ending;
    }
    
    @Override
    public String getTeX() {
        String gap = "";
        String ending = "";
        switch(type) {
            case CON_INTRO :    gap = ", ";
                                ending = ", $\\conop\\rulename{I}$"; 
                                break;
            case IMP_INTRO :    gap = "-";
                                ending = ", $\\impop\\rulename{I}$"; 
                                break;
            case IMP_ELIM :     gap = ", ";
                                ending = ", $\\impop\\rulename{E}$"; 
                                break;
            case EQU_ELIM :     gap = ", ";
                                ending = ", $\\equop\\rulename{E}$"; 
                                break;
            case EQ_ELIM :      gap = ", ";
                                ending = ", $=\\rulename{E}$"; 
                                break;
            case NEG_ELIM :     gap = ", ";
                                ending = ", $negop\\rulename{E}$"; 
                                break;
            case NEG_INTRO :    gap = "-";
                                ending = ", $negop\\rulename{I}$"; 
                                break;
            case NOM_ELIM:      gap = ", ";
                                ending = ", $:\\rulename{E}$"; 
                                break;
        }
        return firstLine.getLineNumOutput() + gap + secondLine.getLineNumOutput() + ending;
    }
    
    public boolean getBlank() {
        return false;
    }
    
    public ArrayList<NDLine> getDependentNDLines() {
        return new ArrayList<NDLine>(Arrays.asList(firstLine, secondLine));
    }
}
