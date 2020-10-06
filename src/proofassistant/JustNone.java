/*
 * The MIT License
 *
 * Copyright 2020 bubto.
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
 * The JustNone class implements NDJust for justifications that do not reference
 * another line
 * 
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 * 
 */
public class JustNone extends NDJustification implements NDJust {
    // Type constants
    public static final int PREMISE_JUST = 1;
    public static final int ASS_JUST = 5;
    public static final int ASS_JUST_ID_BOX = 25;
    public static final int ASS_JUST_EQU_ID_BOX = 125;
    public static final int NOM_INTRO = 625;
    public static final int AXIOM = 3125;
    
    private int type;
    
    /**
     * Class constructor.
     * 
     * @param tp    An int representing the type of this justification. 
     *              Possible types are defined as constants in this class.
     * @param ln    The NDLine which this justification references.
     */
    public JustNone(int tp) {
        this.type = tp;
        setBlank(false);
    }

    @Override
    public String getJava(){
        String out = "";
        switch(type) {
            case PREMISE_JUST : out = "Prem"; break;
            case ASS_JUST : out = "Ass"; break;
            case ASS_JUST_ID_BOX : out = "Ass (" + Globals.operators.get("eq") + ")"; break;            
            case ASS_JUST_EQU_ID_BOX : out = "Ass (" + Globals.operators.get("equ") + ")"; break;      
            case NOM_INTRO : out = Globals.operators.get("nom") + "I"; break;
            case AXIOM : out = ""; break;
        }
        return out;
    }
    
    @Override
    public String getTeX(){
        String out = "";
        switch(type) {
            case PREMISE_JUST : out = "$\\rulename{Prem}$"; break;
            case ASS_JUST : out = "Ass"; break;
            case ASS_JUST_ID_BOX : out = "$\\rulename{Ass (\u003d)}$"; break;            
            case ASS_JUST_EQU_ID_BOX : out = "$\\rulename{Ass (\u2261)}$"; break;      
            case NOM_INTRO : out = "$:\\rulename{I}$"; break;
            case AXIOM : out = ""; break;
        }
        return out;
    }
    
}
