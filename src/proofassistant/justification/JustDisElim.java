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
 * The JustDisElim class implements NDJust for disjunction elimination
 * 
 * @since Proof assistant 0.1
 * @version 2.0
 * @author Declan Thompson
 */
public class JustDisElim implements NDJust {
    private NDLine dis;
    private NDLine ranOneS;
    private NDLine ranOneE;
    private NDLine ranTwoS;
    private NDLine ranTwoE;
    
    public JustDisElim(NDLine disjunction, NDLine rangeOneStart, 
            NDLine rangeOneEnd, NDLine rangeTwoStart, NDLine rangeTwoEnd) {
        dis = disjunction;
        ranOneS = rangeOneStart;
        ranOneE = rangeOneEnd;
        ranTwoS = rangeTwoStart;
        ranTwoE = rangeTwoEnd;
    }
    
    @Override
    public String getJava() {
        return dis.getLineNumOutput() + ", " 
                + ranOneS.getLineNumOutput() + "-" + ranOneE.getLineNumOutput() + ", " 
                + ranTwoS.getLineNumOutput() + "-" + ranTwoE.getLineNumOutput() 
                + ", " + Globals.operators.get("dis") + "E";
    }
    
    @Override
    public String getTeX() {
        return dis.getLineNumOutput() + ", " 
                + ranOneS.getLineNumOutput() + "-" + ranOneE.getLineNumOutput() + ", " 
                + ranTwoS.getLineNumOutput() + "-" + ranTwoE.getLineNumOutput() 
                + ", $\\disop\\rulename{E}$";
    }
    
    public boolean getBlank() {
        return false;
    }
    
    public ArrayList<NDLine> getDependentNDLines() {
        return new ArrayList<NDLine>(Arrays.asList(dis, ranOneS, ranTwoS, ranOneE, ranTwoE));
    }
}
