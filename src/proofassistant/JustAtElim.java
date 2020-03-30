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
public class JustAtElim extends NDJustification{
    private String at = "";
    private String atText = "";
    
    public JustAtElim(int atLine) {
        at = ""+atLine;
        setBlank(false);
        setLines();
    }
    
    public JustAtElim(String atLine) {
        at = atLine;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (at.equals("")) {
            setTeX(atText + ", $@\\rulename{E}$");
            setJava(atText + ", " + Globals.operators.get("at") + "E");
        } else {
            setTeX(at + ", $@\\rulename{E}$");
            setJava(at + ", " + Globals.operators.get("at") + "E");
        }
    }
    
    public void setAt(int atLine) {
        at = ""+atLine;
        setLines();
    }
    
    public JustAtElim clone() {
        JustAtElim theClone = new JustAtElim(at);
        
        return theClone;
    }
}
