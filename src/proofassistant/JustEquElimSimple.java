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
public class JustEquElimSimple extends NDJustification {
    private String equivalence;
    
    public JustEquElimSimple(int theEquivalence) {
        equivalence = ""+theEquivalence;
        setBlank(false);
        setLines();
    }
    
    public JustEquElimSimple(String theEquivalence) {
        equivalence = theEquivalence;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(equivalence + ", $=\\rulename{E}$");
        setJava(equivalence + ", " + Globals.operators.get("equ") + "E");
    }
    
    public void setTheEquivalence(int theEquivalence) {
        equivalence = ""+theEquivalence;
        setLines();
    }
    
    public JustEquElimSimple clone() {
        JustEquElimSimple theClone = new JustEquElimSimple(equivalence);
        
        return theClone;
    }
    
}
