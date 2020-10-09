/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The JustDiaIntro class implements NDJust for diamond introduction
 *
 * @since Proof Assistant 1.3
 * @version 2.0
 * @author Declan Thompson
 */
public class JustDiaIntro implements NDJust {
    private NDLine pred;
    private NDLine prop;
    private NDLine goal;
    
    public JustDiaIntro(NDLine predicate, NDLine proposition, NDLine gl) {
        pred = predicate;
        prop = proposition;
        goal = gl;
    }
    
    @Override
    public String getJava() {
        return pred.getLineNumOutput() + ", " + prop.getLineNumOutput() + ", " 
                + Globals.operators.get("LEFTdia") + goal.getFirstArg() 
                + Globals.operators.get("RIGHTdia") + "I";
    }
    
    @Override
    public String getTeX() {
        return pred.getLineNumOutput() + ", " + prop.getLineNumOutput() 
                + ", $\\langle " + goal.getFirstArg() + "\\rangle\\rulename{I}$";
    }
    
    public boolean getBlank() {
        return false;
    }
    
    public ArrayList<NDLine> getDependentNDLines() {
        return new ArrayList<NDLine>(Arrays.asList(pred, prop, goal));
    }
}
