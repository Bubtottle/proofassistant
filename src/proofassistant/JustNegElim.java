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
public class JustNegElim extends NDJustification {
    private String negation;
    private String prop;
    
    public JustNegElim(int negation, int proposition) {
        this.negation = ""+negation;
        prop = ""+proposition;
        setBlank(false);
        setLines();
    }
    
    public JustNegElim(String negation, String proposition) {
        this.negation = negation;
        prop = proposition;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(negation + ", " + prop + ", $\\negop\\rulename{E}$");
            setJava(negation + ", " + prop + ", " + Globals.operators.get("neg") + "E");
    }
    
    public void setNegation(int implication) {
        negation = ""+implication;
        setLines();
    }
    
    public void setProposition(int antecedent) {
        prop = ""+antecedent;
        setLines();
    }
    
    public JustNegElim clone() {
        JustNegElim theClone = new JustNegElim(negation, prop);
        
        return theClone;
    }
}
