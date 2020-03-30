/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

/**
 *
 * @author dtho139
 */
public class JustQeElim extends NDJustification {
    private String qe;
    private String rangeS;
    private String rangeE;
    private boolean legal;
    
    public JustQeElim(int existental, int rangeStart, int rangeEnd, boolean allowable) {
        qe = ""+existental;
        rangeS = ""+rangeStart;
        rangeE = ""+rangeEnd;
        legal = allowable;
        setBlank(false);
        setLines();
    }
    
    public JustQeElim(String existental, String rangeStart, String rangeEnd, boolean allowable) {
        qe = existental;
        rangeS = rangeStart;
        rangeE = rangeEnd;
        legal = allowable;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (legal) {
            setTeX(qe + ", " + rangeS + "-" + rangeE + ", $\\qeop\\rulename{E}$");
            setJava(qe + ", " + rangeS + "-" + rangeE + ", " + Globals.operators.get("qe") + "E");
        } else {
            setTeX(qe + ", " + rangeS + "-" + rangeE + ", $\\qeop\\rulename{E}$" + "\\illegalflag");
            setJava(qe + ", " + rangeS + "-" + rangeE + ", " + Globals.operators.get("qe") + "E" + "!");
        }
    }
    
    public void setDisjunction(int disjunction) {
        qe = ""+disjunction;
        setLines();
    }
    
    public void setRangeStart(int rangeStart) {
        rangeS = ""+rangeStart;
        setLines();
    }
    
    public void setRangeEnd(int rangeEnd) {
        rangeE = ""+rangeEnd;
        setLines();
    }
    
    public void setAllowable(boolean allowable) {
        legal = allowable;
        setLines();
    }
    
    public JustQeElim clone() {
        JustQeElim theClone = new JustQeElim(qe, rangeS, rangeE, legal);
        
        return theClone;
    }
}
