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
public class JustDiaIntro extends NDJustification {
    private String pred;
    private String prop;
    private String ct;
    
    public JustDiaIntro(int predicate, int proposition, String centre) {
        pred = ""+predicate;
        prop = ""+proposition;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public JustDiaIntro(String predicate, String proposition, String centre) {
        pred = predicate;
        prop = proposition;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(pred + ", " + prop + ", $\\langle " + ct + "\\rangle\\rulename{I}$");
        setJava(pred + ", " + prop + ", " + Globals.operators.get("LEFTdia") + ct + Globals.operators.get("RIGHTdia") + "I");
    }
    
    public void setPredicate(int predicate) {
        pred = ""+predicate;
        setLines();
    }
    
    public void setProposition(int proposition) {
        prop = ""+proposition;
        setLines();
    }
    public void setCentre(String centre) {
        ct = centre;
        setLines();
    }
    
    public JustDiaIntro clone() {
        JustDiaIntro theClone = new JustDiaIntro(pred, prop, ct);
        
        return theClone;
    }
}
