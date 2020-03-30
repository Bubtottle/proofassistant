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
public class JustSelfElim extends NDJustification {
    private String self;
    
    public JustSelfElim(int selfLine) {
        self = ""+selfLine;
        setBlank(false);
        setLines();
    }
    
    public JustSelfElim(String selfLine) {
        self = selfLine;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(self + ", $\\selfop\\rulename{E}$");
        setJava(self + ", " + Globals.operators.get("self") + "E");
    }
    
    public void setSelfLine(int selfLine) {
        self = ""+selfLine;
        setLines();
    }
    
    public JustSelfElim clone() {
        JustSelfElim theClone = new JustSelfElim(self);
        
        return theClone;
    }
}
