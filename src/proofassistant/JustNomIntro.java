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
public class JustNomIntro extends NDJustification {

    
    public JustNomIntro() {
        
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("$:\\rulename{I}$");
        setJava(Globals.operators.get("nom") + "I");
    }
    
    public JustNomIntro clone() {
        JustNomIntro theClone = new JustNomIntro();
        
        return theClone;
    }
}
