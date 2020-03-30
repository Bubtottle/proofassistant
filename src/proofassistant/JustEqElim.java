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
public class JustEqElim extends NDJustification {
    private String equation;
    private String subd;
    
    public JustEqElim(int theEquation, int substituted) {
        equation = ""+theEquation;
        subd = ""+substituted;
        setBlank(false);
        setLines();
    }
    
    public JustEqElim(String theEquation, String substituted) {
        equation = theEquation;
        subd = substituted;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(equation + ", " + subd + ", $=\\rulename{E}$");
            setJava(equation + ", " + subd + ", " + Globals.operators.get("eq") + "E");
    }
    
    public void setTheEquation(int theEquation) {
        equation = ""+theEquation;
        setLines();
    }
    
    public void setSubstituted(int substituted) {
        subd = ""+substituted;
        setLines();
    }
    
    public JustEqElim clone() {
        JustEqElim theClone = new JustEqElim(equation, subd);
        
        return theClone;
    }
}
