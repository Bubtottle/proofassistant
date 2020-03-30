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
public class ScopeLine {
    private int startLine = 0;
    private int endLine = 0;
    private int deepestJust = 0;
    private int depth = 0;
    private int justificationX = 0;
    private float zoomFactor = 1f;
    private FontMetrics currentFont;
    
    public ScopeLine(int startIndex, int endIndex, int deepestJust, int depth, int justificationX, float zoomFactor, FontMetrics currentfont) {
        this.startLine = startIndex + 1;
        this.endLine = endIndex + 1;
        this.deepestJust = deepestJust;
        this.depth = depth;
        this.justificationX = justificationX;
        this.zoomFactor = zoomFactor;
        this.currentFont = currentfont;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(zoomFactor));
        if (Globals.proofStyle == Globals.AUCKLANDSTYLE) {
            g2.drawLine((int)(zoomFactor*(deepestJust*10 + 5)), (int)(zoomFactor*(20*startLine)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine)));
            g2.drawLine((int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*endLine + 10)));
            g2.drawLine((int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*endLine + 10)),(int)(justificationX - zoomFactor*5), (int)(zoomFactor*(20*endLine + 10 )));
        } else if (Globals.proofStyle == Globals.STANFORDSTYLE) {
//            g2.drawLine((int)(zoomFactor*(deepestJust*10 + 5)), (int)(zoomFactor*(20*startLine)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine)));
            g2.drawLine((int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(20*startLine - 5)), (int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(20*endLine + 10)));
            g2.drawLine((int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(20*startLine + 10)),(int)(justificationX - zoomFactor*5), (int)(zoomFactor*(20*startLine + 10 )));
        }
        
    }
    

}
