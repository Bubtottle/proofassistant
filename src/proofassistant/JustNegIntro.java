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
public class JustNegIntro extends NDJustification {
    private String ass;
    private String fals;
    
    public JustNegIntro(int rangeStart, int rangeEnd) {
        ass = ""+rangeStart;
        fals = ""+rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public JustNegIntro(String rangeStart, String rangeEnd) {
        ass = rangeStart;
        fals = rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(ass + "-" + fals + ", $\\negop\\rulename{I}$");
        setJava(ass + "-" + fals + ", " + Globals.operators.get("neg") + "I");
    }
    
    public void setRangeStart(int rangeStart) {
        ass = ""+rangeStart;
        setLines();
    }
    
    public void setRangeEnd(int rangeEnd) {
        fals = ""+rangeEnd;
        setLines();
    }
    
    public JustNegIntro clone() {
        JustNegIntro theClone = new JustNegIntro(ass, fals);
        
        return theClone;
    }
}
