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
public class JustSelfIntro extends NDJustification {
    private NDLine self;
    
    public JustSelfIntro(NDLine selfLine) {
        self = selfLine;
        setBlank(false);
        setLines();
    }
    
    
    public String getJava() {
        return self.getLineNumOutput() + ", " + Globals.operators.get("self") + "I";
    }
    
    public String getTeX() {
        return self.getLineNumOutput() + ", $\\selfop\\rulename{I}$";
    }
    
    // Legacy
    public void setLines() {
        setTeX(self + ", $\\selfop\\rulename{I}$");
        setJava(self + ", " + Globals.operators.get("self") + "I");
    }
    
    public void setSelfLine(NDLine selfLine) {
        self = selfLine;
        setLines();
    }
    
    public JustSelfIntro clone() {
        JustSelfIntro theClone = new JustSelfIntro(self);
        
        return theClone;
    }
}
