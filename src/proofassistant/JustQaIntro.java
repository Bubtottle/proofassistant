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
public class JustQaIntro extends NDJustification {
    private String priorLine;
    private boolean legal;
    
    public JustQaIntro(int matchingLine, boolean allowed) {
        priorLine = ""+matchingLine;
        legal = allowed;
        setBlank(false);
        setLines();
    }
    
    public JustQaIntro(String matchingLine, boolean allowed) {
        priorLine = matchingLine;
        legal = allowed;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (legal) {
            setTeX(priorLine + ", $\\qaop\\rulename{I}$");
            setJava(priorLine + ", " + Globals.operators.get("qa") + "I");
        } else {
            setTeX(priorLine + ", $\\qaop\\rulename{I}$" + "\\illegalflag");
            setJava(priorLine + ", " + Globals.operators.get("qa") + "I" + "!");
        }
    }
    
     public void setMatchingLine(int matchingLine) {
        priorLine = ""+matchingLine;
        setLines();
    }
     
     public void setAllowed(boolean allowed) {
         legal = allowed;
         setLines();
     }
     
     public JustQaIntro clone() {
        JustQaIntro theClone = new JustQaIntro(priorLine, legal);
        
        return theClone;
    }
}
