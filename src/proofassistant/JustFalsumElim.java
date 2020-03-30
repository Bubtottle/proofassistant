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
public class JustFalsumElim extends NDJustification{
    private String fals;
    
    public JustFalsumElim(int falsum) {
        fals = ""+falsum;
        setBlank(false);
        setLines();
    }
    
    public JustFalsumElim(String falsum) {
        fals = falsum;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(fals + ", $\\falsum\\rulename{E}$");
        setJava(fals + ", " + Globals.operators.get("falsum") + "E");
    }
    
    public void setFalsum(int falsum) {
        fals = ""+falsum;
        setLines();
    }
    
    public JustFalsumElim clone() {
        JustFalsumElim theClone = new JustFalsumElim(fals);
        
        return theClone;
    }
}
