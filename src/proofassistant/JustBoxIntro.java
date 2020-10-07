/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

/**
 * The JustBoxIntro class implements NDJust for box introduction
 *
 * @since Proof Assistant 1.3
 * @version 2.0
 * @author Declan Thompson
 */
public class JustBoxIntro implements NDJust {
    private NDLine aStart;
    private NDLine aEnd;
    private NDLine goal;
    
    public JustBoxIntro(NDLine assStart, NDLine assEnd, NDLine gl) {
        aStart = assStart;
        aEnd = assEnd;
        goal = gl;
    }
    
    @Override
    public String getJava() {
        return aStart.getLineNumOutput() + "-" + aEnd.getLineNumOutput() + ", " 
                + Globals.operators.get("LEFTBOX") + goal.getFirstArg() 
                + Globals.operators.get("RIGHTBOX") + "I";
    }
    
    @Override
    public String getTeX() {
        return aStart.getLineNumOutput() + "-" + aEnd.getLineNumOutput() 
                + ", $[ " + goal.getFirstArg() + "]\\rulename{I}$";
    }
    
    public boolean getBlank() {
        return false;
    }
    
}
