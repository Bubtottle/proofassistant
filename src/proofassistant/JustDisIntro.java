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
public class JustDisIntro extends NDJustification {
    private String dis;
    
    public JustDisIntro(int disjunct) {
        dis = ""+disjunct;
        setBlank(false);
        setLines();
    }
    
    public JustDisIntro(String disjunct) {
        dis = disjunct;
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX(dis + ", $\\disop\\rulename{I}$");
        setJava(dis + ", " + Globals.operators.get("dis") + "I");
    }
    
    public void setDisjunct(int disjunct) {
        dis = ""+disjunct;
        setLines();
    }
    
    public JustDisIntro clone() {
        JustDisIntro theClone = new JustDisIntro(dis);
        
        return theClone;
    }
}
