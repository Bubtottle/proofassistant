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
public class JustNomElimSimple extends NDJustification {
    private String equation;
    
    public JustNomElimSimple(int theEquation) {
        equation = ""+theEquation;
        setBlank(false);
        setLines();
    }
    
    public JustNomElimSimple(String theEquation) {
        equation = theEquation;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
            setTeX(equation + ", $:\\rulename{E}$");
            setJava(equation + ", " + Globals.operators.get("nom") + "E");
    }
    
    public void setTheEquation(int theEquation) {
        equation = ""+theEquation;
        setLines();
    }
    
    public JustNomElimSimple clone() {
        JustNomElimSimple theClone = new JustNomElimSimple(equation);
        
        return theClone;
    }
    
}
