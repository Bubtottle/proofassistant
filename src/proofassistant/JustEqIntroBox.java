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
public class JustEqIntroBox extends NDJustification {
    private String identityBox;
    
    public JustEqIntroBox(int idBox) {
        identityBox = ""+idBox;
        setBlank(false);
        setLines();
    }
    
    public JustEqIntroBox(String idBox) {
        identityBox = idBox;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(identityBox + ", $=\\rulename{I}$");
        setJava(identityBox + ", " + Globals.operators.get("eq") + "I");
    }
    
    public void setIdBox(int idBox) {
        identityBox = ""+idBox;
        setLines();
    }
    
    public JustEqIntroBox clone() {
        JustEqIntroBox theClone = new JustEqIntroBox(identityBox);
        
        return theClone;
    }
}
