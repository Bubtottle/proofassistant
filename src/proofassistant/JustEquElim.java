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
public class JustEquElim extends NDJustification {
    private String equ;
    private String ante;
    
    public JustEquElim(int implication, int antecedent) {
        equ = ""+implication;
        ante = ""+antecedent;
        setBlank(false);
        setLines();
    }
    
    public JustEquElim(String implication, String antecedent) {
        equ = implication;
        ante = antecedent;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(equ + ", " + ante + ", $\\equop\\rulename{E}$");
        setJava(equ + ", " + ante + ", " + Globals.operators.get("equ") + "E");
    }
    
    public void setImplication(int implication) {
        equ = ""+implication;
        setLines();
    }
    
    public void setAntecedent(int antecedent) {
        ante = ""+antecedent;
        setLines();
    }
    
    public JustEquElim clone() {
        JustEquElim theClone = new JustEquElim(equ, ante);
        
        return theClone;
    }
}
