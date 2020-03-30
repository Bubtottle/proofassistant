/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

import java.util.Iterator;

/**
 *
 * @author dtho139
 */
public class NDJustification {
    private String teXJustification;
    private String javaJustification;
    private boolean blank = true;
    
    public NDJustification() {
        teXJustification = "";
        javaJustification = "";
    }
    
    
    // Accessor Methods //
    
    public NDJustification clone() {
        NDJustification theClone = new NDJustification();
        theClone.setBlank(blank);
        theClone.setTeX(teXJustification);
        theClone.setJava(javaJustification);
        
        return theClone;
    }
    
    public String getTeX() {
        return teXJustification;
    }
    
    public String getJava() {
        return javaJustification;
    }
    
    public boolean getBlank() { // Returns true if this justification is empty
        return blank;
    }
    
    
    // Mutator Methods //
    
    public void setTeX(String just) {
        teXJustification = just;
    }
    
    public void setJava(String just) {
        javaJustification = just;
    }
    
    public void setLines() {
        if (teXJustification == null) {
            setTeX("");
        }
        if (javaJustification == null) {
            setJava("");
        } else {
            Globals.invertOps();
            Iterator<String> it = Globals.operatorsInv.keySet().iterator();
            String op;
            while (it.hasNext()) {
                op = it.next();
                if (javaJustification.contains(op)) {
                    javaJustification = javaJustification.replace(op, Globals.operators.get(Globals.operatorsInv.get(op)));
                }
            }
        }
    }
    
    
    public void setBlank(boolean isBlank) {
        blank = isBlank;
    }
}
