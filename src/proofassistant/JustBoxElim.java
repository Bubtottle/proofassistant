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
public class JustBoxElim extends NDJustification implements NDJust {
    private NDLine box;
    private NDLine ant;
    
    public JustBoxElim(NDLine boxLine, NDLine antecedent) {
        this.box = boxLine;
        this.ant = antecedent;
        setBlank(false);
    }
    
    @Override
    public String getJava() {
        return box.getLineNumOutput() + ", " + ant.getLineNumOutput() + ", " 
                + Globals.operators.get("LEFTBOX") + box.getFirstArg() 
                + Globals.operators.get("RIGHTBOX") + "E";
    }
    
    @Override
    public String getTeX() {
        return box.getLineNumOutput() + ", " + ant.getLineNumOutput() 
                + ", $[ " + box.getFirstArg() + "]\\rulename{E}$";
    }
    
    
}
