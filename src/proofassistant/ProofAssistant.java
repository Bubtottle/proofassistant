/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;

import proofassistant.core.ProofObject;
import java.awt.event.InputEvent;
import java.io.File;

/**
 *
 * @author dtho139
 */
public class ProofAssistant {
    private static String[] arguments;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        arguments = args;
        

        
//        Globals.assist = new ProofObject(args);
//        Globals.proofArray = Globals.assist.getProofArray();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        
        
        
        
    }
    
    private static void createAndShowGUI() {
        
        
        if (arguments.length == 0){
            ProofFrame frame;
            frame = new ProofFrame(Globals.proofArray);
            frame.setVisible(true);
            Globals.createExtraLines();
            Globals.frame = frame;
        } else if (arguments.length == 1) {
            File file = new File(arguments[0]);
            ProofFrame frame;
            frame = new ProofFrame(file.toPath());
            frame.setVisible(true);
            Globals.createExtraLines();
            Globals.frame = frame;
        } else {
            ProofFrame frame;
            frame = new ProofFrame(Globals.proofArray);
            Globals.assist = new ProofObject(arguments);
            Globals.proofArray = Globals.assist.getProofArray();
            frame = new ProofFrame(Globals.proofArray);
            frame.setVisible(true);
            Globals.createExtraLines();
            Globals.frame = frame;
            
        }
        
    }
}

