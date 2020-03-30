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
public class JustBoxIntro extends NDJustification {
    private String aStart = "";
    private String aEnd = "";
    private String aStartText = "";
    private String aEndText = "";
    private String ct;
    
    public JustBoxIntro(int assStart, int assEnd, String centre) {
        aStart = ""+assStart;
        aEnd = ""+assEnd;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public JustBoxIntro(String assStart, String assEnd, String centre) {
        aStartText = assStart;
        aEndText = assEnd;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (aStart.equals("") && aEnd.equals("")) {
            setTeX(aStartText + "-" + aEndText + ", $[ " + ct + "]\\rulename{I}$");
            setJava(aStartText + "-" + aEndText + ", " + Globals.operators.get("LEFTBOX") + ct + Globals.operators.get("RIGHTBOX") + "I");
        } else {
            setTeX(aStart + "-" + aEnd + ", $[ " + ct + "]\\rulename{I}$");
            setJava(aStart + "-" + aEnd + ", " + Globals.operators.get("LEFTBOX") + ct + Globals.operators.get("RIGHTBOX") + "I");
        }
    }
    
    public void setAssStart(int assStart) {
        aStart = ""+assStart;
        setLines();
    }
    
    public void setAssEnd(int proposition) {
        aEnd = ""+proposition;
        setLines();
    }
    public void setCentre(String centre) {
        ct = centre;
        setLines();
    }
    
    public JustBoxIntro clone() {
        JustBoxIntro theClone;
        
        if (aStart.equals("") || aEnd.equals("")) {
            theClone = new JustBoxIntro(aStartText, aEndText, ct);
        } else {
            theClone = new JustBoxIntro(aStart, aEnd, ct);
        }
        
        return theClone;
    }
}
