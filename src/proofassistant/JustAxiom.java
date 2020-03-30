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
public class JustAxiom extends NDJustification {

    
    public JustAxiom() {
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("");
        setJava("");
    }
    
    public JustAxiom clone() {
        JustAxiom theClone = new JustAxiom();
        
        return theClone;
    }
}
