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
public class JustImpElim extends NDJustification {
    private String imp;
    private String ante;
    
    public JustImpElim(int implication, int antecedent) {
        imp = ""+implication;
        ante = ""+antecedent;
        setBlank(false);
        setLines();
    }
    
    public JustImpElim(String implication, String antecedent) {
        imp = implication;
        ante = antecedent;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(imp + ", " + ante + ", $\\impop\\rulename{E}$");
            setJava(imp + ", " + ante + ", " + Globals.operators.get("imp") + "E");
    }
    
    public void setImplication(int implication) {
        imp = ""+implication;
        setLines();
    }
    
    public void setAntecedent(int antecedent) {
        ante = ""+antecedent;
        setLines();
    }
    
    public JustImpElim clone() {
        JustImpElim theClone = new JustImpElim(imp, ante);
        
        return theClone;
    }
}
