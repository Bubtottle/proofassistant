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
public class JustPrem extends NDJustification {

    
    public JustPrem() {
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("$\\rulename{Prem}$");
        setJava("Prem");
    }
    
    public JustPrem clone() {
        JustPrem theClone = new JustPrem();
        
        return theClone;
    }
}
