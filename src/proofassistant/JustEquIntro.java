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
public class JustEquIntro extends NDJustification {
    private String antecedentOne;
    private String consequentOne;
    private String antecedentTwo;
    private String consequentTwo;
    
    public JustEquIntro(int rangeOneStart, int rangeOneEnd, int rangeTwoStart, int rangeTwoEnd) {
        antecedentOne = ""+rangeOneStart;
        consequentOne = ""+rangeOneEnd;
        antecedentTwo = ""+rangeTwoStart;
        consequentTwo = ""+rangeTwoEnd;
        setBlank(false);
        setLines();
    }
    
    public JustEquIntro(String rangeOneStart, String rangeOneEnd, String rangeTwoStart, String rangeTwoEnd) {
        antecedentOne = rangeOneStart;
        consequentOne = rangeOneEnd;
        antecedentTwo = rangeTwoStart;
        consequentTwo = rangeTwoEnd;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(antecedentOne + "-" + consequentOne + "," + antecedentTwo + "-" + consequentTwo + ", $\\equop\\rulename{I}$");
        setJava(antecedentOne + "-" + consequentOne + "," + antecedentTwo + "-" + consequentTwo + ", " + Globals.operators.get("equ") + "I");
    }
    
     public void setRangeOneStart(int rangeOneStart) {
        antecedentOne = ""+rangeOneStart;
        setLines();
    }
    
    public void setRangeOneEnd(int rangeOneEnd) {
        consequentOne = ""+rangeOneEnd;
        setLines();
    }
    
    public void setRangeTwoStart(int rangeTwoStart) {
        antecedentTwo = ""+rangeTwoStart;
        setLines();
    }
    
    public void setRangeTwoEnd(int rangeTwoEnd) {
        consequentTwo = ""+rangeTwoEnd;
        setLines();
    }
    
    public JustEquIntro clone() {
        JustEquIntro theClone = new JustEquIntro(antecedentOne, consequentOne, antecedentTwo, consequentTwo);
        
        return theClone;
    }
}
