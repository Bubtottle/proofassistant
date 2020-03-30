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
public class ContextBoxLine {
    private int startLine = 0;
    private int endLine = 0;
    private int deepestJust = 0;
    private int depth = 0;
    private int justificationX = 0;
    private float zoomFactor = 1f;
    
    public ContextBoxLine(int startIndex, int endIndex, int deepestJust, int depth, int justificationX, float zoomFactor) {
        this.startLine = startIndex + 1;
        this.endLine = endIndex + 1;
        this.deepestJust = deepestJust;
        this.depth = depth;
        this.justificationX = justificationX;
        this.zoomFactor = zoomFactor;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(zoomFactor));
        g2.drawLine((int)(zoomFactor*(deepestJust*10 + 5)), (int)(zoomFactor*(20*startLine-10)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine-10)));
        g2.drawLine((int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine-10)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*endLine + 10)));
        g2.drawLine((int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*endLine + 10)),(int)(justificationX - zoomFactor*5), (int)(zoomFactor*(20*endLine + 10 )));
    }
    

}
