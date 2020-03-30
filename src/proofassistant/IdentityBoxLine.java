/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Declan
 */
public class IdentityBoxLine {
    private int startLine = 0;
    private int endLine = 0;
    private int lineContentsX = 0;
    private int justificationX = 0;
    private float zoomFactor = 1f;
    
    
    
    public IdentityBoxLine(int startIndex, int endIndex, int lineContentsX, int justificationX, float zoomFactor) {
        this.startLine = startIndex + 1;
        this.endLine = endIndex + 1;
        this.lineContentsX = lineContentsX;
        this.justificationX = justificationX;
        this.zoomFactor = zoomFactor;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(zoomFactor));
        g2.drawLine((int)((lineContentsX - zoomFactor*10)), (int)(zoomFactor*(20*startLine - 10)), (int)(justificationX - zoomFactor*5), (int)(zoomFactor*(20*startLine - 10)));
        g2.drawLine((int)((lineContentsX - zoomFactor*10)), (int)(zoomFactor*(20*startLine - 10)), (int)((lineContentsX - zoomFactor*10)), (int)(zoomFactor*(20*endLine + 10)));
        g2.drawLine((int)((lineContentsX - zoomFactor*10)), (int)(zoomFactor*(20*endLine + 10)),(int)(justificationX - zoomFactor*5), (int)(zoomFactor*(20*endLine + 10 )));
    }
    

}
