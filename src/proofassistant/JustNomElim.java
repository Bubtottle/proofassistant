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
public class JustNomElim extends NDJustification {
    private String equation;
    private String subd;
    
    public JustNomElim(int theEquation, int substituted) {
        equation = ""+theEquation;
        subd = ""+substituted;
        setBlank(false);
        setLines();
    }
    
    public JustNomElim(String theEquation, String substituted) {
        equation = theEquation;
        subd = substituted;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(equation + ", " + subd + ", $:\\rulename{E}$");
            setJava(equation + ", " + subd + ", " + Globals.operators.get("nom") + "E");
    }
    
    public void setTheEquation(int theEquation) {
        equation = ""+theEquation;
        setLines();
    }
    
    public void setSubstituted(int substituted) {
        subd = ""+substituted;
        setLines();
    }
    
    public JustNomElim clone() {
        JustNomElim theClone = new JustNomElim(equation, subd);
        
        return theClone;
    }
}
