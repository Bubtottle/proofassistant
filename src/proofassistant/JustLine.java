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
public class JustLine extends NDJustification{
    private String line;
    
    public JustLine(int identicalLine) {
        line = ""+identicalLine;
        setBlank(false);
        setLines();
    }
    
    public JustLine(String identicalLine) {
        line = identicalLine;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("" + line);
        setJava("" + line);
    }
    
    public void setLine(int identicalLine) {
        line = ""+identicalLine;
        setLines();
    }
    
    public JustLine clone() {
        JustLine theClone = new JustLine(line);
        
        return theClone;
    }
}
