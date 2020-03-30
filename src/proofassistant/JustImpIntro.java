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
public class JustImpIntro extends NDJustification {
    private String antecedent;
    private String consequent;
    
    public JustImpIntro(int rangeStart, int rangeEnd) {
        antecedent = ""+rangeStart;
        consequent = ""+rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public JustImpIntro(String rangeStart, String rangeEnd) {
        antecedent = rangeStart;
        consequent = rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(antecedent + "-" + consequent + ", $\\impop\\rulename{I}$");
        setJava(antecedent + "-" + consequent + ", " + Globals.operators.get("imp") + "I");
    }
    
    public void refreshJava() {
        setLines();
    }
    
    public void setRangeStart(int rangeStart) {
        antecedent = ""+rangeStart;
        setLines();
    }
    
    public void setRangeEnd(int rangeEnd) {
        consequent = ""+rangeEnd;
        setLines();
    }
    
    public JustImpIntro clone() {
        JustImpIntro theClone = new JustImpIntro(antecedent, consequent);
        
        return theClone;
    }
}
