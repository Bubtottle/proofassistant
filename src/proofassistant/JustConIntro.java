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
public class JustConIntro extends NDJustification {
    private NDLine conOne;
    private NDLine conTwo;
    private String conOneText = "";
    private String conTwoText = "";
    
    public JustConIntro(NDLine conjunctOne, NDLine conjunctTwo) {
        conOne = conjunctOne;
        conTwo = conjunctTwo;
        setBlank(false);
        setLines();
    }
    
    public String getJava(){
        return conOne.getLineNumOutput() +", " + conTwo.getLineNumOutput() + ", " + Globals.operators.get("con") + "I";
    }
    
    // Legacy
    public void setLines() {
        setTeX(conOneText + ", " + conTwoText + ", $\\conop\\rulename{I}$");
        setJava(conOneText + ", " + conTwoText + ", " + Globals.operators.get("con") + "I");
    }
    
    public void setConjunctOne(NDLine conjunctOne) {
        conOne = conjunctOne;
        setLines();
    }
    
    public void setLineTwo(NDLine conjunctTwo) {
        conTwo = conjunctTwo;
        setLines();
    }
    
    public JustConIntro clone() {
        JustConIntro theClone = new JustConIntro(conOne, conTwo);
        
        return theClone;
    }
}
