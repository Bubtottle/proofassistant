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
public class JustEqIntro extends NDJustification {

    
    public JustEqIntro() {
        
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("$\\rulename{=I}$");
        setJava(Globals.operators.get("eq") + "I");
    }
    
    public JustEqIntro clone() {
        JustEqIntro theClone = new JustEqIntro();
        
        return theClone;
    }
}
