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
public class JustConElim extends NDJustification{
    private NDLine con;
    
    public JustConElim(NDLine conjunction) {
        con = conjunction;
        setBlank(false);
        setLines();
    }
    
    @Override
    public String getJava(){
        return con.getLineNumOutput() + ", " + Globals.operators.get("con") + "E";
    }
    
    @Override
    public String getTeX(){
        return con.getLineNumOutput() + ", $\\conop\\rulename{E}$";
    }
    
    // Legacy
    public void setLines() {
        if (con.equals("")) {
            setTeX(con + ", $\\conop\\rulename{E}$");
            setJava(con + ", " + Globals.operators.get("con") + "E");
        } else {
            setTeX(con + ", $\\conop\\rulename{E}$");
            setJava(con + ", " + Globals.operators.get("con") + "E");
        }
    }
    
    public void setConjunction(NDLine conjunction) {
        con = conjunction;
        setLines();
    }
    
    public JustConElim clone() {
        JustConElim theClone = new JustConElim(con);
        
        return theClone;
    }
}
