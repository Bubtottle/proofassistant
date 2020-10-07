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
 * The JustSingle class implements NDJust for justifications referring to a
 * single line.
 * 
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class JustSingle implements NDJust {
    // Type constants
    public static final int CON_ELIM = 2; // Conjunction elimination
    public static final int DIS_INTRO = 4; // Disjunction introduction
    public static final int DN_ELIM = 8; // Double negation elimination
    public static final int EQ_ELIM_S = 16; // ID Box equality elimination
    public static final int ID_BOX_INTRO = 32; // ID Box introduction
    public static final int EQU_ELIM_S = 64; // ID Box equivalence elmination
    public static final int FALSUM_ELIM = 128; // Falsum elimination
    public static final int LINE_IS_EQUAL = 256; // An equal line
    public static final int NOM_ELIM_S = 512; // ID box nominal elimination
    public static final int NOM_BOX_INTRO = 1024; // Nominal box introduction
    public static final int QA_ELIM = 2046; // Universal elimination
    public static final int QE_INTRO = 4096; // Existential introduction
    public static final int SELF_ELIM = 8192; // Self-reference elimination (hybrid logic)
    public static final int SELF_INTRO = 16384; // Self-reference introduction (hybrid logic)
    public static final int AT_ELIM = 32768; // @ elimination (hybrid logic)
    public static final int AT_INTRO = 65536; // @ introduction (hybrid logic)
    public static final int QA_INTRO = 131072; // Universal introduction
    
    
    private int type;
    private NDLine line;
    private boolean legal = true;
    
    /**
     * Class constructor.
     * 
     * @param tp    An int representing the type of this justification. 
     *              Possible types are defined as constants in this class.
     * @param ln    The NDLine which this justification references.
     */
    public JustSingle(int tp, NDLine ln) {
        this.type = tp;
        this.line = ln;
    }
    
    public JustSingle(int tp, NDLine ln, boolean allowed) {
        this.type = tp;
        this.line = ln;
        this.legal = allowed;
    }
    
    @Override
    public String getJava(){
        String out = "";
        switch(type) {
            case CON_ELIM : out = ", " + Globals.operators.get("con") + "E"; break;
            case DIS_INTRO : out = ", " + Globals.operators.get("dis") + "I"; break;
            case EQ_ELIM_S : out = ", " + Globals.operators.get("eq") + "E"; break;            
            case DN_ELIM : out = ", " + Globals.operators.get("neg") + Globals.operators.get("neg") + "E"; break;
            case ID_BOX_INTRO : out = ", " + Globals.operators.get("eq") + "I"; break;
            case EQU_ELIM_S : out = ", " + Globals.operators.get("equ") + "E"; break;
            case FALSUM_ELIM : out = ", " + Globals.operators.get("falsum") + "E"; break;
            case LINE_IS_EQUAL : out = ""; break;            
            case NOM_ELIM_S : out = ", " + Globals.operators.get("nom") + "E"; break;
            case NOM_BOX_INTRO : out = ", " + Globals.operators.get("nom") + "I"; break;            
            case QA_ELIM : out = ", " + Globals.operators.get("qa") + "E"; break;
            case QE_INTRO : out = ", " + Globals.operators.get("qe") + "I"; break;
            case SELF_ELIM : out = ", " + Globals.operators.get("self") + "E"; break;
            case SELF_INTRO : out = ", " + Globals.operators.get("self") + "I"; break;
            case AT_ELIM : out = ", " + Globals.operators.get("at") + "E"; break;
            case AT_INTRO : out = ", " + Globals.operators.get("at") + "I"; break;
            case QA_INTRO : out = ", " + Globals.operators.get("qa") + "I"; break;
        }
        return line.getLineNumOutput() + out + (legal ? "" : "!");
    }
    
    @Override
    public String getTeX(){
        String out = "";
        switch(type) {
            case CON_ELIM : out = ", $\\conop\\rulename{E}$"; break;
            case DIS_INTRO : out = ", $\\disop\\rulename{I}$"; break;
            case EQ_ELIM_S : out = ", $=\\rulename{E}$"; break;
            case DN_ELIM : out = ", $\\negop\\negop\\rulename{E}$"; break;            
            case ID_BOX_INTRO : out = ", $=\\rulename{I}$"; break;
            case EQU_ELIM_S : out = ", $=\\rulename{E}$"; break;
            case FALSUM_ELIM : out = ", $\\falsum\\rulename{E}$"; break;
            case LINE_IS_EQUAL : out = ""; break;
            case NOM_ELIM_S : out = ", $:\\rulename{E}$"; break;
            case NOM_BOX_INTRO : out = ", $:\\rulename{I}$"; break;           
            case QA_ELIM : out = ", $\\qaop\\rulename{E}$"; break;
            case QE_INTRO : out = ", $\\qeop\\rulename{I}$"; break;
            case SELF_ELIM : out = ", $\\selfop\\rulename{E}$"; break;
            case SELF_INTRO : out = ", $\\selfop\\rulename{I}$"; break;
            case AT_ELIM : out = ", $@\\rulename{E}$"; break;
            case AT_INTRO : out = ", $@\\rulename{I}$"; break;
            case QA_INTRO : out = ", $\\qaop\\rulename{I}$"; break;
        }
        return line.getLineNumOutput() + out + (legal ? "" : "\\illegalflag");
    }
    
    public boolean getBlank() {
        return false;
    }
    

}
