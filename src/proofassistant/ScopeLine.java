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
    private int lineHeight = 20;
    private int topOffSet = 10;
    
    public ScopeLine(int startIndex, int endIndex, int deepestJust, int depth, int justificationX, float zoomFactor, FontMetrics currentfont) {
        this.startLine = startIndex + 1;
        this.endLine = endIndex + 1;
        this.deepestJust = deepestJust;
        this.depth = depth;
        this.justificationX = justificationX;
        this.zoomFactor = zoomFactor;
        this.currentFont = currentfont;
    }
    
    public ScopeLine(int startIndex, int endIndex, int deepestJust, int depth, int justificationX, float zoomFactor, FontMetrics currentfont, int lineH, int topOS) {
        this.startLine = startIndex + 1;
        this.endLine = endIndex + 1;
        this.deepestJust = deepestJust;
        this.depth = depth;
        this.justificationX = justificationX;
        this.zoomFactor = zoomFactor;
        this.currentFont = currentfont;
        this.lineHeight = lineH;
        this.topOffSet = topOS;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(zoomFactor));
        int leftX = (int)(zoomFactor*(depth* 10));
        int topY = (int)(zoomFactor*(lineHeight*startLine - lineHeight/2));
        int indentX = (int)(zoomFactor*(deepestJust*10 + 5));
        int rightX = (int)(justificationX - zoomFactor*5);
        int botY = (int)(zoomFactor*(lineHeight*endLine + topOffSet + lineHeight/16));
        if (Globals.proofStyle == Globals.AUCKLANDSTYLE) {
            
            // Start line horizontal 
            g2.drawLine( leftX, topY, indentX, topY );
            // Vertical line
            g2.drawLine(leftX, topY, leftX, botY);
            // Final underline
            // NOTE: don't know if topOffSet is the right variable to use here
            g2.drawLine(leftX, botY, rightX, botY);
        } else if (Globals.proofStyle == Globals.STANFORDSTYLE) {
//            g2.drawLine((int)(zoomFactor*(deepestJust*10 + 5)), (int)(zoomFactor*(20*startLine)), (int)(zoomFactor*(depth* 10)), (int)(zoomFactor*(20*startLine)));
            g2.drawLine((int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(lineHeight*startLine - topOffSet/2)), (int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(lineHeight*endLine + topOffSet)));
            g2.drawLine((int)(zoomFactor*(currentFont.stringWidth("77.") + depth*10)), (int)(zoomFactor*(lineHeight*startLine + topOffSet)),(int)(justificationX - zoomFactor*5), (int)(zoomFactor*(lineHeight*startLine + topOffSet )));
        }
        
    }
    

}
