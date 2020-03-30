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
public class JustQaElim extends NDJustification {
    private String priorLine;
    
    public JustQaElim(int universalLine) {
        priorLine = "" + universalLine;
        setBlank(false);
        setLines();
    }
    
    public JustQaElim(String universalLine) {
        priorLine = universalLine;
        setBlank(false);
        setLines();
    }
    
    public void setLines() { 
            setTeX(priorLine + ", $\\qaop\\rulename{E}$");
            setJava(priorLine + ", " + Globals.operators.get("qa") + "E");
    }
    
     public void setMatchingLine(int universalLine) {
        priorLine = "" + universalLine;
        setLines();
    }
     
     public JustQaElim clone() {
        JustQaElim theClone = new JustQaElim(priorLine);
        
        return theClone;
    }
}
