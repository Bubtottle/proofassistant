/*
 * The MIT License
 *
 * Copyright 2014 Declan Thompson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package proofassistant;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Declan Thompson
 */
public class SymbolSelectorPanel extends JPanel implements MouseListener{
    private final JLabel conLabel = new JLabel(Globals.operators.get("con"));
    private final JLabel disLabel = new JLabel(Globals.operators.get("dis"));
    private final JLabel impLabel = new JLabel(Globals.operators.get("imp"));
    private final JLabel equLabel = new JLabel(Globals.operators.get("equ"));
    private final JLabel negLabel = new JLabel(Globals.operators.get("neg"));
    private final JLabel falsumLabel = new JLabel(Globals.operators.get("falsum"));
    private final JLabel qaLabel = new JLabel(Globals.operators.get("qa"));
    private final JLabel qeLabel = new JLabel(Globals.operators.get("qe"));
    private final JLabel timesLabel = new JLabel("\u22c5");
    private final JLabel noteqLabel = new JLabel(Globals.operators.get("noteq"));
    
    private final HashMap<Rectangle, JLabel> areasToOps = new HashMap<>(); 
    
    private final ArrayList<ActionListener> actionListeners = new ArrayList<>();
    
    public SymbolSelectorPanel() {
        super();
        setLayout(null);
        addMouseListener(this);
        setPreferredSize(new Dimension(70, 140));
        printPanel();
    }
    
    private void printPanel() {
        
        Font font = conLabel.getFont().deriveFont(2*conLabel.getFont().getSize2D());
        
        int maxWidth = 0;
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("con")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("con"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("dis")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("dis"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("imp")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("imp"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("equ")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("equ"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("neg")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("neg"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("falsum")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("falsum"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("qa")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("qa"));
        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("qe")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("qe"));
        }
//        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("\u22c5")) > maxWidth) {
//            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("\u22c5"));
//        }
        if (conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("noteq")) > maxWidth) {
            maxWidth = conLabel.getFontMetrics(font).stringWidth(Globals.operators.get("noteq"));
        }
        
        font = font.deriveFont((float)Math.min(font.getSize()*20/maxWidth, 24));
        
        conLabel.setLocation(10, 10);
        conLabel.setSize(25, 25);
        conLabel.setFont(font);
        conLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        conLabel.setBackground(Color.WHITE);
        conLabel.setOpaque(true);
        conLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(conLabel);
        areasToOps.put(conLabel.getBounds(), conLabel);
        
        
        disLabel.setLocation(35, 10);
        disLabel.setSize(25, 25);
        disLabel.setFont(font);
        disLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        disLabel.setBackground(Color.WHITE);
        disLabel.setOpaque(true);
        disLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(disLabel);
        areasToOps.put(disLabel.getBounds(), disLabel);
        
        
        impLabel.setLocation(10, 35);
        impLabel.setSize(25, 25);
        impLabel.setFont(font);
        impLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        impLabel.setBackground(Color.WHITE);
        impLabel.setOpaque(true);
        impLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(impLabel);
        areasToOps.put(impLabel.getBounds(), impLabel);
        
        
        equLabel.setLocation(35, 35);
        equLabel.setSize(25, 25);
        equLabel.setFont(font);
        equLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        equLabel.setBackground(Color.WHITE);
        equLabel.setOpaque(true);
        equLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(equLabel);
        areasToOps.put(equLabel.getBounds(), equLabel);
        
        negLabel.setLocation(10, 60);
        negLabel.setSize(25, 25);
        negLabel.setFont(font);
        negLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        negLabel.setBackground(Color.WHITE);
        negLabel.setOpaque(true);
        negLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(negLabel);
        areasToOps.put(negLabel.getBounds(), negLabel);
        
        falsumLabel.setLocation(35, 60);
        falsumLabel.setSize(25, 25);
        falsumLabel.setFont(font);
        falsumLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        falsumLabel.setBackground(Color.WHITE);
        falsumLabel.setOpaque(true);
        falsumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(falsumLabel);
        areasToOps.put(falsumLabel.getBounds(), falsumLabel);
        
        qaLabel.setLocation(10, 85);
        qaLabel.setSize(25, 25);
        qaLabel.setFont(font);
        qaLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        qaLabel.setBackground(Color.WHITE);
        qaLabel.setOpaque(true);
        qaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(qaLabel);
        areasToOps.put(qaLabel.getBounds(), qaLabel);
        
        qeLabel.setLocation(35, 85);
        qeLabel.setSize(25, 25);
        qeLabel.setFont(font);
        qeLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        qeLabel.setBackground(Color.WHITE);
        qeLabel.setOpaque(true);
        qeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(qeLabel);
        areasToOps.put(qeLabel.getBounds(), qeLabel);
        
        timesLabel.setLocation(10, 110);
        timesLabel.setSize(25, 25);
        timesLabel.setFont(font);
        timesLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        timesLabel.setBackground(Color.WHITE);
        timesLabel.setOpaque(true);
        timesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timesLabel);
        areasToOps.put(timesLabel.getBounds(), timesLabel);
        
        noteqLabel.setLocation(35, 110);
        noteqLabel.setSize(25, 25);
        noteqLabel.setFont(font);
        noteqLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        noteqLabel.setBackground(Color.WHITE);
        noteqLabel.setOpaque(true);
        noteqLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(noteqLabel);
        areasToOps.put(noteqLabel.getBounds(), noteqLabel);
    }
    
    public void addActionListener(ActionListener al) {
        actionListeners.add(al);
    }
    
    public void removeActionListener(ActionListener al) {
        actionListeners.remove(al);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        String toAdd = "";
        
        Point p = me.getPoint();
//        Rectangle[] areas = (Rectangle[])areasToOps.keySet().toArray();
        Iterator<Rectangle> areas = areasToOps.keySet().iterator();
        while (areas.hasNext()) {
            Rectangle currentR = areas.next();
            if (currentR.contains(p)) {
                toAdd = areasToOps.get(currentR).getText();
            }
        }
        
        if (!toAdd.equals("")) {
//            System.out.println(toAdd);
            for (int i = 0; i < actionListeners.size(); i++) {
                actionListeners.get(i).actionPerformed(new ActionEvent(this, 0, toAdd));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
        
        Point p = me.getPoint();
//        Rectangle[] areas = (Rectangle[])areasToOps.keySet().toArray();
        Iterator<Rectangle> areas = areasToOps.keySet().iterator();
        while (areas.hasNext()) {
            Rectangle currentR = areas.next();
            if (currentR.contains(p)) {
                areasToOps.get(currentR).setBackground(Color.yellow);
            }
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
        
        Point p = me.getPoint();
//        Rectangle[] areas = (Rectangle[])areasToOps.keySet().toArray();
        Iterator<Rectangle> areas = areasToOps.keySet().iterator();
        while (areas.hasNext()) {
            Rectangle currentR = areas.next();
            if (currentR.contains(p)) {
                areasToOps.get(currentR).setBackground(Color.WHITE);
            }
        }
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
       
    }

    @Override
    public void mouseExited(MouseEvent me) {
       
    }
    
}
