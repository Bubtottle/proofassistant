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
public class JustQeIntro extends NDJustification {
    private NDLine priorLine;
    
    public JustQeIntro(NDLine matchingLine) {
        priorLine = matchingLine;
        setBlank(false);
        setLines();
    }
    
    public String getJava() {
        return priorLine.getLineNumOutput() + ", " + Globals.operators.get("qe") + "I";
    }
    
    public String getTeX() {
        return priorLine.getLineNumOutput() + ", $\\qeop\\rulename{I}$";
    }
    
    
    // Legacy
    public void setLines() {
        setTeX(priorLine + ", $\\qeop\\rulename{I}$");
        setJava(priorLine + ", " + Globals.operators.get("qe") + "I");
    }
    
     public void setMatchingLine(NDLine matchingLine) {
        priorLine = matchingLine;
        setLines();
    }
     
    public JustQeIntro clone() {
        JustQeIntro theClone = new JustQeIntro(priorLine);
        
        return theClone;
    }
}
