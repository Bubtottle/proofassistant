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
public class JustNomIntroBox extends NDJustification {
    private String identityBox;
    
    public JustNomIntroBox(int idBox) {
        identityBox = ""+idBox;
        setBlank(false);
        setLines();
    }
    
    public JustNomIntroBox(String idBox) {
        identityBox = idBox;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(identityBox + ", $:\\rulename{I}$");
        setJava(identityBox + ", " + Globals.operators.get("nom") + "I");
    }
    
    public void setIdBox(int idBox) {
        identityBox = ""+idBox;
        setLines();
    }
    
    public JustNomIntroBox clone() {
        JustNomIntroBox theClone = new JustNomIntroBox(identityBox);
        
        return theClone;
    }
}
