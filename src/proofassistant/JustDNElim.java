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
public class JustDNElim extends NDJustification{
    private String dn;
    
    public JustDNElim(int doubleNegation) {
        dn = "" + doubleNegation;
        setBlank(false);
        setLines();
    }
    
    public JustDNElim(String doubleNegation) {
        dn = "" + doubleNegation;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(dn + ", $\\negop\\negop\\rulename{E}$");
        setJava(dn + ", " + Globals.operators.get("neg") + Globals.operators.get("neg") + "E");
    }
    
    public void setDoubleNegation(int doubleNegation) {
        dn = "" + doubleNegation;
        setLines();
    }
    
    public JustDNElim clone() {
        JustDNElim theClone = new JustDNElim(dn);
        
        return theClone;
    }
}
