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
 * The JustQeElim class implements NDJust for existential elimination
 *
 * @since Proof Assistant 0.1
 * @version 2.0
 * @author Declan Thompson
 */
public class JustQeElim implements NDJust {
    private NDLine qe;
    private NDLine rangeS;
    private NDLine rangeE;
    private boolean legal;
    
    public JustQeElim(NDLine existental, NDLine rangeStart, NDLine rangeEnd, boolean allowable) {
        qe = existental;
        rangeS = rangeStart;
        rangeE = rangeEnd;
        legal = allowable;
    }
    
    @Override
    public String getJava() {
        return qe.getLineNumOutput() + ", " 
                + rangeS.getLineNumOutput() + "-" + rangeE.getLineNumOutput() 
                + ", " + Globals.operators.get("qe") + "E"
                + (legal ? "" : "!");
    }
    
    @Override
    public String getTeX() {
        return qe.getLineNumOutput() + ", " 
                + rangeS.getLineNumOutput() + "-" + rangeE.getLineNumOutput() 
                + ", $\\qeop\\rulename{E}$"
                + (legal ? "" : "\\illegalflag");
    }
    
    
    public boolean getBlank() {
        return false;
    }
    
    public ArrayList<NDLine> getDependentNDLines() {
        return new ArrayList<NDLine>(Arrays.asList(qe, rangeS, rangeE));
    }
}
