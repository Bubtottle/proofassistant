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

import proofassistant.line.NDLine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;

/**
 *
 * @author Declan Thompson
 * 
 * Finds the indexes selected from the parsedLine, converts them to the indexes
 * of the normal line, applies the substitution and returns.
 */
public class TermSelectorPanel extends javax.swing.JPanel implements MouseListener {
    
    private String term = "";
    private String line = "";
    private String parsedLine = "";
    private String replacement = "";
    private int lineWidth = 0;
    private TreeMap<Integer, Integer> termIndexes = new TreeMap<>();
    private TreeMap<Integer, Boolean> termsHighlighted = new TreeMap<>();
    private TreeMap<Integer, Integer> termLocations = new TreeMap<>();
    private TreeMap<Integer, Integer> parsedToLine = new TreeMap<>();
    /**
     * Creates new form TermSelectedPanel
     */
    public TermSelectorPanel() {
        initComponents();
        setLayout(null);
        setPreferredSize(new Dimension(250, 50));
        addMouseListener(this);
        printLine();
    }
    
    private void printLine() {
        removeAll();
        termLocations.clear();
        termLocations.put(0, -1);
        int previousX = 10;
        int previousIndex = 0;
        int nextTermIndex = 0;
        for (int i = 0; i < termIndexes.size(); i++) {
//            System.out.println("previousX " + previousX);
            while (!termIndexes.containsKey(nextTermIndex) && nextTermIndex < parsedLine.length()) {
                nextTermIndex ++;
            }
//            System.out.println(line.substring(previousIndex, nextTermIndex));
            JLabel nonTerm = new JLabel(parsedLine.substring(previousIndex, nextTermIndex));
            nonTerm.setLocation(previousX,1);
            termLocations.put(previousX, -1);
            nonTerm.setFont(nonTerm.getFont().deriveFont(3*nonTerm.getFont().getSize2D()));
            FontMetrics nonTermFM = nonTerm.getFontMetrics(nonTerm.getFont());
            int nonTermButtonWidth = nonTermFM.stringWidth(nonTerm.getText());
            nonTerm.setSize(nonTermButtonWidth, 50);
            nonTerm.setForeground(Color.GRAY);
            this.add(nonTerm);
            
            
//            System.out.println(line.substring(nextTermIndex, termIndexes.get(nextTermIndex)));
            JLabel term = new JLabel(parsedLine.substring(nextTermIndex, termIndexes.get(nextTermIndex)));
            term.setLocation(previousX + nonTermButtonWidth,1);
            termLocations.put(previousX + nonTermButtonWidth, nextTermIndex);
            term.setFont(term.getFont().deriveFont(3*term.getFont().getSize2D()));
            FontMetrics termFM = term.getFontMetrics(term.getFont());
            int termButtonWidth = termFM.stringWidth(term.getText());
            term.setSize(termButtonWidth, 50);
            if (termsHighlighted.get(nextTermIndex)) {
                term.setBackground(Color.CYAN);
                term.setOpaque(true);
            }
            this.add(term);
            
            
            previousX = previousX + nonTermButtonWidth+termButtonWidth;
            previousIndex = termIndexes.get(nextTermIndex);
            nextTermIndex++;
//            System.out.println("nonTermButtonWidth " + nonTermButtonWidth);
//            System.out.println("termButtonWidth " + termButtonWidth);
//            System.out.println("previousX " + previousX);
        }
//        System.out.println(line.substring(previousIndex));
        JLabel nonTerm = new JLabel(parsedLine.substring(previousIndex));
        nonTerm.setLocation(previousX,1);
        termLocations.put(previousX, -1);
        nonTerm.setFont(nonTerm.getFont().deriveFont(3*nonTerm.getFont().getSize2D()));
        FontMetrics nonTermFM = nonTerm.getFontMetrics(nonTerm.getFont());
        int nonTermButtonWidth = nonTermFM.stringWidth(nonTerm.getText());
        nonTerm.setSize(nonTermButtonWidth, 50);
        nonTerm.setForeground(Color.GRAY);
        this.add(nonTerm);
        
        lineWidth = previousX + nonTermButtonWidth;
        
        setPreferredSize(new Dimension(lineWidth + 10, 60));
        revalidate();
        repaint();
    }
    
    public void setUpForGeneralCase(String line) {
        
    }
    
    public void setUpForInstances(String line, String term, String replacement) {
//        System.out.println("=== Term Selector Panel ===");
//        System.out.println("line " + line);
//        System.out.println("term " + term);
//        System.out.println("replacement " + replacement);
        // Make sure brackets are showing, and get the parsed line
        boolean showBracketsTemp = Globals.showBrackets;
        boolean qShowNumbersTemp = Globals.qShowNumbers;
        Globals.showBrackets = true;
        Globals.qShowNumbers = false;
        NDLine temp = new NDLine(line, 6);
        parsedLine = temp.parseLine();
        Globals.showBrackets = showBracketsTemp;
        Globals.qShowNumbers = qShowNumbersTemp;
        
        this.line = line;
        this.term = term;
        this.replacement = replacement;
        
        NDLine regexLine = new NDLine(term, 6);
        Pattern pattern = Pattern.compile(regexLine.getRegEx(""));
        Matcher matcherP = pattern.matcher(parsedLine);
        Matcher matcherL = pattern.matcher(line);
//        System.out.println("P: " + parsedLine + " "+ matcherP.find());
//        System.out.println("L: " + line + " " + matcherL.find());
        matcherP.reset();
        matcherL.reset();

        int previousIndex = 0;
        while (matcherP.find() && matcherL.find()) {
            termIndexes.put(matcherP.start(), matcherP.end());
            termsHighlighted.put(matcherP.start(), false);
            parsedToLine.put(matcherP.start(),matcherL.start());
            parsedToLine.put(matcherP.end(), matcherL.end());
//            System.out.println("P " + matcherP.start());
//            System.out.println("L " + matcherL.start());
        }
        parsedToLine.put(0, 0);
        printLine();
    }
    
    public String getReplacedLine() {
        String result = "";
        
        int previousIndex = 0;
        int nextTermIndex = 0;
        for (int i = 0; i < termIndexes.size(); i++) {
            while (!termIndexes.containsKey(nextTermIndex) && nextTermIndex < parsedLine.length()) {
                nextTermIndex ++;
            }
            result += line.substring(parsedToLine.get(previousIndex), parsedToLine.get(nextTermIndex));
            
            if (termsHighlighted.get(nextTermIndex)) {
                result += replacement;
            } else {
                result += term;
            }
            
            previousIndex = termIndexes.get(nextTermIndex);
            nextTermIndex ++;
//            System.out.println("result        " + i + ": " + result);
//            System.out.println("previousindex " + i + ": " + previousIndex);
//            System.out.println("nextTermIndex " + i + ": " + nextTermIndex);
        }
        
        result += line.substring(parsedToLine.get(previousIndex));
        
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent me) {
        
    }

    @Override
    public void mousePressed(MouseEvent me) {
        int mouseX = me.getX();
        int mouseY = me.getY();
        if (mouseY > 0 && mouseY < 52) {
            if (mouseX > 9 && mouseX < lineWidth) {
                int termToChange = termLocations.get(termLocations.floorKey(mouseX));
//                System.out.println(termToChange);
                if (termToChange != -1) {
                    termsHighlighted.put(termToChange, !termsHighlighted.get(termToChange));
//                    System.out.println(termsHighlighted.get(termToChange));
                }
                printLine();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
}
