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
public class JustDiaElim extends NDJustification {
    private String di;
    private String ct;
    private String rangeS1;
    private String rangeS2;
    private String rangeE;
    private boolean legal;
    
    public JustDiaElim(int dia, String centre, int rangeStart1, int rangeStart2, int rangeEnd, boolean allowable) {
        di = ""+dia;
        ct = centre;
        rangeS1 = ""+rangeStart1;
        rangeS2 = ""+rangeStart2;
        rangeE = ""+rangeEnd;
        legal = allowable;
        setBlank(false);
        setLines();
    }
    
    public JustDiaElim(String dia, String centre, String rangeStart1, String rangeStart2, String rangeEnd, boolean allowable) {
        di = dia;
        ct = centre;
        rangeS1 = rangeStart1;
        rangeS2 = rangeStart2;
        rangeE = rangeEnd;
        legal = allowable;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (legal) {
            setTeX(di + ", " + rangeS1 + "-" + rangeE + ", $\\langle " + ct + "\\rangle\\rulename{E}$");
            setJava(di + ", " + rangeS1 + "-" + rangeE + ", " + Globals.operators.get("LEFTdia") + ct + Globals.operators.get("RIGHTdia") + "E");
        } else {
            setTeX(di + ", " + rangeS1 + "-" + rangeE + ", $\\langle " + ct + "\\rangle\\rulename{E}$" + "\\illegalflag");
            setJava(di + ", " + rangeS1 + "-" + rangeE + ", " + Globals.operators.get("LEFTdia") + ct + Globals.operators.get("RIGHTdia") + "E" + "!");
        }
    }
    
    public void setdia(int dia) {
        di = ""+dia;
        setLines();
    }
    
    public void setCenter(String centre) {
        ct = centre;
        setLines();
    }
    
    public void setRange1Start(int range1Start) {
        rangeS1 = ""+range1Start;
        setLines();
    }
    
    public void setRange2Start(int range2Start) {
        rangeS2 = ""+range2Start;
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
    
    public JustDiaElim clone() {
        JustDiaElim theClone = new JustDiaElim(di, ct, rangeS1, rangeS2, rangeE, legal);
        
        return theClone;
    }
}
