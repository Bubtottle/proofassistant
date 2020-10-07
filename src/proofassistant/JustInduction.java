/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

/**
 * The JustInduction class implements NDJust for induction
 *
 * @since Proof Assistant 0.2
 * @version 2.0
 * @author Declan Thompson
 */
public class JustInduction implements NDJust {
    private NDLine zeroLine;
    private NDLine rangeS;
    private NDLine rangeE;
    
    public JustInduction(NDLine zeroPart, NDLine rangeStart, NDLine rangeEnd) {
        zeroLine = zeroPart;
        rangeS = rangeStart;
        rangeE = rangeEnd;
    }
    
    @Override
    public String getJava() {
        return zeroLine.getLineNumOutput() + ", " 
                + rangeS.getJustLineNum() + "-" + rangeE.getJustLineNum() 
                + ", IND";
    }
    
    @Override
    public String getTeX() {
        return zeroLine.getLineNumOutput() + ", " 
                + rangeS.getJustLineNum() + "-" + rangeE.getJustLineNum() 
                + ", $\\rulename{IND}$";
    }
    
    
    public boolean getBlank() {
        return false;
    }
    
}
