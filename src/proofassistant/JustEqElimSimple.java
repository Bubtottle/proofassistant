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
public class JustEqElimSimple extends NDJustification {
    private String equation;
    
    public JustEqElimSimple(int theEquation) {
        equation = ""+theEquation;
        setBlank(false);
        setLines();
    }
    
    public JustEqElimSimple(String theEquation) {
        equation = theEquation;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(equation + ", $=\\rulename{E}$");
            setJava(equation + ", " + Globals.operators.get("eq") + "E");
    }
    
    public void setTheEquation(int theEquation) {
        equation = ""+theEquation;
        setLines();
    }
    
    public JustEqElimSimple clone() {
        JustEqElimSimple theClone = new JustEqElimSimple(equation);
        
        return theClone;
    }
    
}
