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
public class JustDisElim extends NDJustification {
    private String dis;
    private String ranOneS;
    private String ranOneE;
    private String ranTwoS;
    private String ranTwoE;
    
    public JustDisElim(int disjunction, int rangeOneStart, int rangeOneEnd, int rangeTwoStart, int rangeTwoEnd) {
        dis = ""+disjunction;
        ranOneS = ""+rangeOneStart;
        ranOneE = ""+rangeOneEnd;
        ranTwoS = ""+rangeTwoStart;
        ranTwoE = ""+rangeTwoEnd;
        setBlank(false);
        setLines();
    }
    
    public JustDisElim(String disjunction, String rangeOneStart, String rangeOneEnd, String rangeTwoStart, String rangeTwoEnd) {
        dis = disjunction;
        ranOneS = rangeOneStart;
        ranOneE = rangeOneEnd;
        ranTwoS = rangeTwoStart;
        ranTwoE = rangeTwoEnd;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(dis + ", " + ranOneS + "-" + ranOneE + ", " + ranTwoS + "-" + ranTwoE + ", $\\disop\\rulename{E}$");
        setJava(dis + ", " + ranOneS + "-" + ranOneE + ", " + ranTwoS + "-" + ranTwoE + ", " + Globals.operators.get("dis") + "E");
    }
    
    public void setDisjunction(int disjunction) {
        dis = ""+disjunction;
        setLines();
    }
    
    public void setRangeOneStart(int rangeOneStart) {
        ranOneS = ""+rangeOneStart;
        setLines();
    }
    
    public void setRangeOneEnd(int rangeOneEnd) {
        ranOneE = ""+rangeOneEnd;
        setLines();
    }
    
    public void setRangeTwoStart(int rangeTwoStart) {
        ranTwoS = ""+rangeTwoStart;
        setLines();
    }
    
    public void setRangeTwoEnd(int rangeTwoEnd) {
        ranTwoE = ""+rangeTwoEnd;
        setLines();
    }
    
    public JustDisElim clone() {
        JustDisElim theClone = new JustDisElim(dis, ranOneS, ranOneE, ranTwoS, ranTwoE);
        
        return theClone;
    }
}
