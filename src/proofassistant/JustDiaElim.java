/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

/**
 * The JustDiaElim class implements NDJust for diamond elimination
 * 
 * @since Proof Assistant 1.3
 * @version 2.0
 * @author Declan Thompson
 */
public class JustDiaElim implements NDJust {
    private NDLine diamond;
    private NDLine rangeS1;
    private NDLine rangeS2;
    private NDLine rangeE;
    private boolean legal;
    
    public JustDiaElim(NDLine dia, NDLine rangeStart1, NDLine rangeStart2, NDLine rangeEnd, boolean allowable) {
        diamond = dia;
        rangeS1 = rangeStart1;
        rangeS2 = rangeStart2;
        rangeE = rangeEnd;
        legal = allowable;
    }
    
    @Override
    public String getJava() {
        return diamond.getLineNumOutput() + ", " 
                + rangeS1.getLineNumOutput() + "-" + rangeE.getLineNumOutput() 
                + ", " + Globals.operators.get("LEFTdia") 
                + diamond.getFirstArg() 
                + Globals.operators.get("RIGHTdia") + "E"
                + (legal ? "" : "!");
    }
    
    @Override
    public String getTeX() {
        return diamond.getLineNumOutput() + ", " 
                + rangeS1.getLineNumOutput() + "-" + rangeE.getLineNumOutput() 
                + ", $\\langle " + diamond.getFirstArg() 
                + "\\rangle\\rulename{E}$"
                + (legal ? "" : "\\illegalflag");
    }
    
    public boolean getBlank() {
        return false;
    }
}
