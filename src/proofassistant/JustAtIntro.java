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
public class JustAtIntro extends NDJustification{
    private String at = "";
    private String atText = "";
    
    public JustAtIntro(int atLine) {
        at = ""+atLine;
        setBlank(false);
        setLines();
    }
    
    public JustAtIntro(String atLine) {
        at = atLine;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (at.equals("")) {
            setTeX(atText + ", $@\\rulename{I}$");
            setJava(atText + ", " + Globals.operators.get("at") + "I");
        } else {
            setTeX(at + ", $@\\rulename{I}$");
            setJava(at + ", " + Globals.operators.get("at") + "I");
        }
    }
    
    public void setAt(int atLine) {
        at = ""+atLine;
        setLines();
    }
    
    public JustAtIntro clone() {
        JustAtIntro theClone = new JustAtIntro(at);
        
        return theClone;
    }
}
