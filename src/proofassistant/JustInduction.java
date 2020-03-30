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
public class JustInduction extends NDJustification {
    private String zeroBit;
    private String rangeS;
    private String rangeE;
    
    public JustInduction(int zeroPart, int rangeStart, int rangeEnd) {
        zeroBit = ""+zeroPart;
        rangeS = ""+rangeStart;
        rangeE = ""+rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public JustInduction(String zeroPart, String rangeStart, String rangeEnd) {
        zeroBit = zeroPart;
        rangeS = rangeStart;
        rangeE = rangeEnd;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(zeroBit + ", " + rangeS + "-" + rangeE + ", $\\rulename{IND}$");
        setJava(zeroBit + ", " + rangeS + "-" + rangeE + ", IND");
    }
    
    public void setZeroPart(int zeroPart) {
        zeroBit = ""+zeroPart;
        setLines();
    }
    
    public void setRangeStart(int rangeStart) {
        rangeS = ""+rangeStart;
        setLines();
    }
    
    public void setRangeEnd(int rangeEnd) {
        rangeE = ""+rangeEnd;
        setLines();
    }
    
    public JustInduction clone() {
        JustInduction theClone = new JustInduction(zeroBit, rangeS, rangeE);
        
        return theClone;
    }
}
