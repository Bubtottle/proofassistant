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
public class JustBoxElim extends NDJustification {
    private String box = "";
    private String ant = "";
    private String boxText = "";
    private String antText = "";
    private String ct;
    
    public JustBoxElim(int boxLine, int antecedent, String centre) {
        box = ""+boxLine;
        ant = ""+antecedent;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public JustBoxElim(String boxLine, String antecedent, String centre) {
        boxText = boxLine;
        antText = antecedent;
        ct = centre;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        if (box.equals("") && ant.equals("")) {
            setTeX(boxText + ", " + antText + ", $[ " + ct + "]\\rulename{E}$");
            setJava(boxText + ", " + antText + ", " + Globals.operators.get("LEFTBOX") + ct + Globals.operators.get("RIGHTBOX") + "E");
        } else {
        setTeX(box + ", " + ant + ", $[ " + ct + "]\\rulename{E}$");
        setJava(box + ", " + ant + ", " + Globals.operators.get("LEFTBOX") + ct + Globals.operators.get("RIGHTBOX") + "E");
        }
    }
    
    public void setBoxLine(int boxLine) {
        box = ""+boxLine;
        setLines();
    }
    
    public void setPropositionAntecedent(int antecedent) {
        ant = ""+antecedent;
        setLines();
    }
    public void setCentre(String centre) {
        ct = centre;
        setLines();
    }
    
    public JustBoxElim clone() {
        JustBoxElim theClone;
        if (box.equals("") && ant.equals("")) {
            theClone = new JustBoxElim(boxText, antText, ct);
        } else {
            theClone = new JustBoxElim(box, ant, ct);
        }
        
        return theClone;
    }
}
