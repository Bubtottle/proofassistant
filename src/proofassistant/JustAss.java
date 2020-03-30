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
public class JustAss extends NDJustification {
    private String additional;
    
    public JustAss() {
        additional = "";
        setBlank(false);
        setLines();
    }
    
    public JustAss(int type) {
        if (type == 0) { // Type 0 - normal assumption
            additional = "";
        } else if (type == 1) { // Type 1 - Ass (=)
            additional = " (" + "\u003d" + ")";
        } else if (type == 2) { // Type 2 - Ass (tribar)
            additional = " (" + "\u2261" + ")";
        }
        setBlank(false);
        setLines();
    }
    
    public void setLines() {
        setTeX("$\\rulename{Ass" + additional + "}$");
        setJava("Ass" + additional);
    }
}
