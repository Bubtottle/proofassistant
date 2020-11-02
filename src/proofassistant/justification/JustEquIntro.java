/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * The JustEquIntro class implements NDJust for equivalence introduction
 *
 * @since Proof Assistant 0.1
 * @version 2.0
 * @author Declan Thompson
 */
public class JustEquIntro implements NDJust {
    private NDLine antecedentOne;
    private NDLine consequentOne;
    private NDLine antecedentTwo;
    private NDLine consequentTwo;
    
    public JustEquIntro(NDLine rangeOneStart, NDLine rangeOneEnd, NDLine rangeTwoStart, NDLine rangeTwoEnd) {
        antecedentOne = rangeOneStart;
        consequentOne = rangeOneEnd;
        antecedentTwo = rangeTwoStart;
        consequentTwo = rangeTwoEnd;
    }
    
    @Override
    public String getJava() {
        return antecedentOne.getLineNumOutput() + "-" 
                + consequentOne.getLineNumOutput() + "," 
                + antecedentTwo.getLineNumOutput() + "-" 
                + consequentTwo.getLineNumOutput() + ", " 
                + Globals.operators.get("equ") + "I";
    }
    
    
    public String getTeX() {
        return antecedentOne.getLineNumOutput() + "-" 
                + consequentOne.getLineNumOutput() + "," 
                + antecedentTwo.getLineNumOutput() + "-" 
                + consequentTwo.getLineNumOutput() + ", $\\equop\\rulename{I}$";
    }
    
    public boolean getBlank() {
        return false;
    }
    
    public ArrayList<NDLine> getDependentNDLines() {
        return new ArrayList<NDLine>(Arrays.asList(antecedentOne, 
                                                    antecedentTwo, 
                                                    consequentOne, 
                                                    consequentTwo));
    }
}
