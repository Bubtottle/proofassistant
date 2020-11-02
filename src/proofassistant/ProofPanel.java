/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proofassistant;
import proofassistant.core.NDLine;
import proofassistant.exception.LineNotInProofArrayException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import proofassistant.exception.MissingArityException;
import proofassistant.exception.WrongLineTypeException;

/**
 *
 * @author Declan
 */
public class ProofPanel extends JPanel implements MouseListener,  ActionListener, KeyListener {
    
    private ScopeLine[] theScopes;
    private IdentityBoxLine[] theIdentityBoxes;
    private int lineNumX = 0;
    private int justificationX = 0;
    private NDLine[] proofArray;
    private JButton introButton = new ProofButton("hello", false);
    private JButton elimButton = new ProofButton("hello", false);
    private JButton sameLineButton = new ProofButton("hello", false);
    private JButton DNButton = new ProofButton("hello", false);
    private JButton shortcutButton = new ProofButton("hello", false);
    private JButton indButton = new ProofButton("hello", false);
    private final Insets buttonInsets = new Insets(2,2,2,2);
    private boolean showButtons = true;
    private double scaleFactor = 1;
    private float zoomFactor = Globals.zoomFactor;
    
    private int lineHeight = 25;
    private int topOffSet = 10;
    
    public ProofPanel(NDLine[] proofArray) {
        this.proofArray = proofArray;
        setLayout(null);
//        setPreferredSize(new Dimension(Globals.proofWidth, Globals.proofHeight + 200));
        revalidate();
        
        printLines();
        addMouseListener(this);
        addKeyListener(this);
    }
    
    public void setZoomFactor(float zoom) {
        zoomFactor = zoom;
    }
    
    public float getZoomFactor() {
        return zoomFactor;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.scale(scaleFactor, scaleFactor);
        super.paintComponent(g);
        
        for (int i = 0; i < theScopes.length; i++) {
            theScopes[i].draw(g);
        }
        for (int i = 0; i < theIdentityBoxes.length; i++) {
            theIdentityBoxes[i].draw(g);
        }
    }
    
    public void setProofArray(NDLine[] newArray) {
        this.proofArray = newArray;
    }
    
    public void printLines() {
        // Preparation for printing lines //
        removeAll();
//        System.out.println(Thread.currentThread().getStackTrace()[2]);
//        System.out.println(proofArray.length + ", " + Globals.proofArray.length + ", " + Globals.assist.getProofArray().length);
        // Find the deepest assumption in the proof (to see where the lines should go)
        // Find the longest line in the proof (to see where the justifications should go)
        // Count the number of scope lines needed
        // Count the number of identity boxes needed
        int deepestAss = 0;
        int assCounter = 0;
        int longestLine = 0;
        int numScopes = 0;
        int numIdentityBoxes = 0;
        JLabel findFontLabel = new JLabel("hello");
        FontMetrics currentFont = findFontLabel.getFontMetrics(findFontLabel.getFont());
//        System.out.println();
        for (NDLine proofArray1 : proofArray) {
//            System.out.println(proofArray[i].parseLine());
//            System.out.println("setLines:");
//            proofArray1.getJustification().setnies();
            if (currentFont.stringWidth(proofArray1.parseLine()) > longestLine) {
                longestLine = currentFont.stringWidth(proofArray1.parseLine());
            }
            if (proofArray1.getType() == 1 || proofArray1.getType() == 3) {
                assCounter++;
                numScopes++;
            }
            if (assCounter > deepestAss) {
                deepestAss = assCounter;
            }
            if (proofArray1.getType() == 2 || proofArray1.getType() == 3) {
                assCounter--;
            }
            if (proofArray1.getType() == 7 || proofArray1.getType() == 10) {
                numIdentityBoxes++;
            }
        }

        // Create the array to hold the ScopeLine objects and the array to hold the IdentityBoxLine objects
        theScopes = new ScopeLine[numScopes];
        theIdentityBoxes = new IdentityBoxLine[numIdentityBoxes];
        
        // Set the x position for lineNum, lineContents and justification
        lineNumX = (int)(zoomFactor*(deepestAss*10 + topOffSet));
        int lineContentsX;
        lineContentsX = lineNumX + (int)(zoomFactor*(currentFont.stringWidth("77.") + topOffSet));
        justificationX = lineContentsX + (int)(zoomFactor*(longestLine + 10));
        // In StanfordStyle, line numbers come before scope lines, and the lineContents is closer
        if (Globals.proofStyle == Globals.STANFORDSTYLE){
            lineContentsX = lineNumX + (int)(zoomFactor*(currentFont.stringWidth("77.")));
            lineNumX = (int)(zoomFactor * topOffSet);
        }
        
        // Create a stack to store the indices of scope beginnings and a variable to store the beginnings of the identity boxes
        Stack<Integer> scopeStartIndex = new Stack<>();
        int idBoxStart = 0;
        int scopeDepth = 0;
        
        
        // Print the Lines //
        
        int j = 0; // counter for scopelines
        int k = 0; // counter for identityboxes
        int xExtension = 135; // set the x extension to the end border of the shortcut button (the last button)
//        System.out.println();
        

        
        int linesY;
        
        for (int i = 0; i < proofArray.length; i++) {
            if (proofArray[i].getType() != 5) {  
                Color textColour;
                linesY = (int)(zoomFactor*(lineHeight*i + topOffSet));
                if (Globals.currentGoalIndex == i) { // If we're printing the goal line, add the buttons
                    
                    textColour = Color.GREEN.darker(); // Green for goal line
                    
                    // Set the dimensions/positioinig of the buttons
                    int buttonWidth = (int)(zoomFactor*(currentFont.stringWidth("===")));
                    int buttonHeight = (int)(zoomFactor*lineHeight);
                    int buttonSep = (int)(zoomFactor*5);
                    // Buttons X coordinate
                    int buttonPosition = justificationX;
                    // if we're in an id box, move the buttons to the right a bit
                    if (proofArray[i].getType() > 6 && proofArray[i].getType() < 11) {
                        buttonPosition = buttonPosition + (int)(zoomFactor*10);
                    }
                    // Buttons Y coordinate
                    int buttonY = linesY;
                    
                    
                    // Add the intro rule button
                    if (!proofArray[i].parseMainOp().equals("") && !proofArray[i].parseMainOp().equals("\u22a5") // If the main op is not " " or falsum
                            && !(proofArray[i].getType() > 6 && proofArray[i].getType() < 11) //                     and we're not in an id box
                            && (!proofArray[i].parseMainOp().equals("=") //                                          and if it's not =
                                  || proofArray[i].getArgAsString(1).equals(proofArray[i].getArgAsString(2)) //                   or both arguments are the same
                                  || Globals.allowedRules.get("eqIdentityBoxes")) //                                                           or we're using identity boxes
                            && Globals.allowedRules.containsKey(proofArray[i].getSMainOp() + "Intro")
                            && Globals.allowedRules.get(proofArray[i].getSMainOp() + "Intro")
                            && Globals.editable) { 
                        introButton = new ProofButton(proofArray[i].parseMainOp() + "I", showButtons);
                        introButton.setLocation(buttonPosition, buttonY);
                        introButton.setMargin(buttonInsets);
                        introButton.setSize(buttonWidth,buttonHeight);
                        introButton.addActionListener(this);
                        introButton.setActionCommand("introRule");
                        introButton.setFont(introButton.getFont().deriveFont(zoomFactor*introButton.getFont().getSize2D()));
                        introButton.addKeyListener(this);
                        add(introButton);
                    }
                    
                    
                    // Add the induction button
                    if (proofArray[i].getMainOp().equals("qa") && Globals.allowedRules.get("induction")
                            && Globals.editable) {
                        indButton = new ProofButton("IND", showButtons);
                        indButton.setLocation(buttonPosition + 2*(buttonWidth + buttonSep), buttonY);
                        indButton.setMargin(buttonInsets);
                        indButton.setSize(buttonWidth,buttonHeight);
                        indButton.addActionListener(this);
                        indButton.setFont(indButton.getFont().deriveFont(zoomFactor*indButton.getFont().getSize2D()));
                        indButton.setActionCommand("inductionRule");
                        indButton.addKeyListener(this);
                        add(indButton);
                    }
                    
                    
                    // Add the elim rule button
                    if (Globals.currentResourceIndex > -1 && Globals.currentResourceIndex < proofArray.length && Globals.editable) {                 // If the current resource is above the current goal
                        if (!(proofArray[i].getType() > 6 && proofArray[i].getType() < 11) ||                                    // If we're not in an identity
                                proofArray[Globals.currentResourceIndex].getMainOp().equals("eq") ||                             // or if the resource is \eq
                                proofArray[Globals.currentResourceIndex].getMainOp().equals("equ") ||                            // or if the resource is \equ
                                proofArray[Globals.currentResourceIndex].getSMainOp().equals("nom") ||                           // or if the resource is nom
                                (Globals.allowedRules.get("universalsShortcuts") && (proofArray[Globals.currentResourceIndex].getNonUniMainOp().equals("eq") ||   // or ( if we're using shortcuts AND ( the resource is \eq
                                proofArray[Globals.currentResourceIndex].getNonUniMainOp().equals("equ"))) ) {                         //                                         or \equ ) )
                            
                            if (!proofArray[Globals.currentResourceIndex].parseMainOp().equals("")){                                 // If the current resource has a main op
                                if (Globals.allowedRules.get("universalsShortcuts") && Globals.allowedRules.containsKey(proofArray[Globals.currentResourceIndex].getNonUniSMainOp() + "Elim")
                                        && Globals.allowedRules.get(proofArray[Globals.currentResourceIndex].getNonUniSMainOp() + "Elim")){
                                    elimButton = new ProofButton(proofArray[Globals.currentResourceIndex].parseNonUniSMainOp() + "E", showButtons);
                                    elimButton.setLocation(buttonPosition + buttonWidth + buttonSep, buttonY);
                                    elimButton.setMargin(buttonInsets);
                                    elimButton.setSize(buttonWidth,buttonHeight);
                                    elimButton.setEnabled(proofArray[i].isInScopeOf(proofArray[Globals.currentResourceIndex], proofArray));
                                    elimButton.addActionListener(this);
                                    elimButton.setFont(elimButton.getFont().deriveFont(zoomFactor*elimButton.getFont().getSize2D()));
                                    elimButton.setActionCommand("elimRule");
                                    elimButton.addKeyListener(this);
                                    add(elimButton);
                                } else if (Globals.allowedRules.containsKey(proofArray[Globals.currentResourceIndex].getSMainOp() + "Elim")
                                        && (Globals.allowedRules.get(proofArray[Globals.currentResourceIndex].getSMainOp() + "Elim"))){
                                    elimButton = new ProofButton(proofArray[Globals.currentResourceIndex].parseMainOp() + "E", showButtons);
                                    elimButton.setLocation(buttonPosition + buttonWidth + buttonSep, buttonY);
                                    elimButton.setMargin(buttonInsets);
                                    elimButton.setSize(buttonWidth,buttonHeight);
                                    elimButton.setEnabled(proofArray[i].isInScopeOf(proofArray[Globals.currentResourceIndex], proofArray));
                                    elimButton.addActionListener(this);
                                    elimButton.setFont(elimButton.getFont().deriveFont(zoomFactor*elimButton.getFont().getSize2D()));
                                    elimButton.setActionCommand("elimRule");
                                    elimButton.addKeyListener(this);
                                    add(elimButton);
                                }
                                
                            }
                        }
                        
                        
                            
                            
                            // Add the same line button
                        if (proofArray[i].getLine().equals(proofArray[Globals.currentResourceIndex].getLine()) && !(proofArray[i].getType() > 6 && proofArray[i].getType() < 11)
                                && Globals.allowedRules.get("sameLine")) {
                            remove(indButton);
                            sameLineButton = new ProofButton("==", showButtons);
                            sameLineButton.setLocation(buttonPosition + 2*(buttonWidth + buttonSep), buttonY);
                            sameLineButton.setMargin(buttonInsets);
                            sameLineButton.setSize(buttonWidth,buttonHeight);
                            sameLineButton.setEnabled(proofArray[i].isInScopeOf(proofArray[Globals.currentResourceIndex], proofArray)
                                                        && proofArray[i].getContext().equals(proofArray[Globals.currentResourceIndex].getContext()));
                            sameLineButton.addActionListener(this);
                            sameLineButton.setFont(sameLineButton.getFont().deriveFont(zoomFactor*sameLineButton.getFont().getSize2D()));
                            sameLineButton.setActionCommand("sameLineRule");
                            sameLineButton.addKeyListener(this);
                            add(sameLineButton);
                            
                            // Add the universals shortcut button. This allows us to use a universal quantifier INSTEAD of the inner operator
                        } else if (Globals.allowedRules.get("universalsShortcuts") && proofArray[Globals.currentResourceIndex].getMainOp().equals("qa") && !proofArray[Globals.currentResourceIndex].getNonUniMainOp().equals("qa")) {
                            shortcutButton = new ProofButton(proofArray[Globals.currentResourceIndex].parseMainOp() + "E", showButtons);
                            shortcutButton.setLocation(buttonPosition + 2*(buttonWidth + buttonSep), buttonY);
                            shortcutButton.setMargin(buttonInsets);
                            shortcutButton.setSize(buttonWidth,buttonHeight);
                            shortcutButton.addActionListener(this);
                            shortcutButton.setFont(shortcutButton.getFont().deriveFont(zoomFactor*shortcutButton.getFont().getSize2D()));
                            shortcutButton.setActionCommand("shortcutRule");
                            shortcutButton.addKeyListener(this);
                            add(shortcutButton);
                        }
                    }
                    
                    
                    
                    
                    // Add the double negation button
                    if (Globals.allowedRules.get("doubleNegation") && !proofArray[i].getLine().equals("\\falsum") 
                            && !(proofArray[i].getType() > 6 && proofArray[i].getType() < 11)
                            && Globals.editable) {
                        DNButton = new ProofButton(Globals.operators.get("neg") + Globals.operators.get("neg") + "E", showButtons);
                        DNButton.setLocation(buttonPosition + 3*(buttonWidth + buttonSep), buttonY);
                        DNButton.setMargin(buttonInsets);
                        DNButton.setSize(buttonWidth,buttonHeight);
                        DNButton.addActionListener(this);
                        DNButton.setFont(DNButton.getFont().deriveFont((float)0.7*zoomFactor*DNButton.getFont().getSize2D()));
                        DNButton.setActionCommand("doubleNegationRule");
                        DNButton.addKeyListener(this);
                        add(DNButton);
                        
                    }
                    
                    
                    
                    
                } else if (Globals.currentResourceIndex == i) {
                    textColour = Color.RED;
                } else if (Globals.outOfScopeIsGrey 
                        && Globals.currentGoalIndex > -1 
                        && Globals.currentGoalIndex < proofArray.length 
                        && !proofArray[Globals.currentGoalIndex].isInScopeOf(proofArray[i], proofArray)) {
                    textColour = Color.GRAY;
                } else {
                    textColour = Color.BLACK;
                }
                
                if (proofArray[i].getLineNum() > 0) {
                    if (Globals.numberTopDown) {
                        proofArray[i].setLineNum(i+1);
                    }
                    JLabel lineNum = new JLabel((proofArray[i].getLineNum() + Globals.lineIncrement) + ".");
                    lineNum.setLocation(lineNumX, linesY);
                    lineNum.setSize((int)(zoomFactor*50),(int)(zoomFactor*lineHeight));
                    lineNum.setFont(lineNum.getFont().deriveFont(zoomFactor*lineNum.getFont().getSize2D()));
                    lineNum.setForeground(textColour);
                    add(lineNum);
                } else if (proofArray[i].isSpecial()) {
                    JLabel lineNum = new JLabel(proofArray[i].getSpecialNum());
                    lineNum.setLocation(lineNumX, linesY);
                    lineNum.setSize((int)(zoomFactor*50),(int)(zoomFactor*lineHeight));
                    lineNum.setFont(lineNum.getFont().deriveFont(zoomFactor*lineNum.getFont().getSize2D()));
                    lineNum.setForeground(textColour);
                    lineNum.setBorder(null);
                    add(lineNum);
                }
                
                JLabel lineContents = new JLabel(proofArray[i].parseLineHTML());
                lineContents.setLocation(lineContentsX, linesY);
                lineContents.setSize((int)(zoomFactor*10*longestLine),(int)(zoomFactor*lineHeight));
                lineContents.setFont(lineContents.getFont().deriveFont(zoomFactor*lineContents.getFont().getSize2D()));
                lineContents.setForeground(textColour);
                add(lineContents);
                
//                proofArray[i].getJustification().setLines();
//                System.out.println(proofArray[i].getJustification().getJava());
                JLabel justification = new JLabel(proofArray[i].getJustification().getJava());
//                System.out.println(proofArray[i].getJustification().getJava());
                justification.setLocation(justificationX, linesY);
//                System.out.println(justification.getPreferredSize());
//                justification.setSize((int)(zoomFactor*currentFont.stringWidth(proofArray[i].getJustification().getJava())*1.05),(int)(zoomFactor*20));
                justification.setSize((int)((zoomFactor+1)*justification.getPreferredSize().width),(int)(zoomFactor*lineHeight));
                justification.setFont(justification.getFont().deriveFont(zoomFactor*justification.getFont().getSize2D()));
                justification.setForeground(textColour);
                FontMetrics thisJust = justification.getFontMetrics(justification.getFont());
                int justWidth = thisJust.stringWidth(justification.getText());
                if (justWidth > xExtension) {
                    xExtension = justWidth;
                }
                add(justification);
                
                
                // Deal with assumptions
                if (proofArray[i].getType() == 1 || proofArray[i].getType() == 3) {
                    // We have an assumption. We need to add this index to scopeStartIndex and increment the scopeDepth
                    scopeStartIndex.push(i);
                    scopeDepth++;
                }
                if (proofArray[i].getType() == 2 || proofArray[i].getType() == 3) {
                    // We are closing an assumption. We need to create a ScopeLine object and decrement the scopeDepth
                    theScopes[j] = new ScopeLine((int)(scopeStartIndex.pop()), i, deepestAss, scopeDepth, justificationX, zoomFactor, currentFont, lineHeight, topOffSet);
                    scopeDepth--;
                    j++;
                }
                
                // Deal with identity boxes
                if (proofArray[i].getType() == 7 || proofArray[i].getType() == 10) {
                    idBoxStart = i;
                } else if (proofArray[i].getType() == 9) {
                    theIdentityBoxes[k] = new IdentityBoxLine(idBoxStart, i, lineContentsX, justificationX, zoomFactor, lineHeight);
                    k++;
                }
            }
        }
        
        Globals.proofHeight = (int)(zoomFactor*(lineHeight*(proofArray.length) + topOffSet));
        Globals.proofWidth = justificationX + (int)(zoomFactor*xExtension);
        
        
        setPreferredSize(new Dimension((int)(Globals.proofWidth + 10), (int)(Globals.proofHeight + 10)));
        revalidate();
        repaint();
//        introButton.setSize((int)scaleFactor*30,(int)scaleFactor*20);
    }
    
    
    public BufferedImage getImage() {
        BufferedImage bi = new BufferedImage((int)(2*Globals.proofWidth/zoomFactor), (int)(2*Globals.proofHeight/zoomFactor), BufferedImage.TYPE_INT_ARGB); 
        Graphics g = bi.createGraphics();
        this.setOpaque(false);
//        Color prior = this.getBackground();
//        this.setBackground(Color.WHITE);
        showButtons = false;
        float temp = zoomFactor;
        zoomFactor = 1f;
        scaleFactor = 2;
        printLines();
        setSize(new Dimension(2*getWidth(), 2*getHeight()));
        
        this.paint(g);  //this == JComponent
        
        g.dispose();
        this.setOpaque(true);
//        this.setBackground(prior);
        showButtons = true;
        zoomFactor = temp;
        scaleFactor = 1;
        printLines();
        setSize(new Dimension(getWidth()/2, getHeight()/2));
        return bi;
    }
    
    public BufferedImage getImage(int width, int height) {
        BufferedImage bi = new BufferedImage((int)(width), (int)(height), BufferedImage.TYPE_INT_ARGB); 
        Graphics g = bi.createGraphics();
//        this.setOpaque(false);
        Color prior = this.getBackground();
        this.setBackground(Color.WHITE);
        showButtons = false;
        float temp = zoomFactor;
        Dimension tempDim = getSize();
        zoomFactor = 1f;
        scaleFactor = 2;
        printLines();
        setSize(new Dimension(width, height));
        
        this.paint(g);  //this == JComponent
        
        g.dispose();
        this.setOpaque(true);
        this.setBackground(prior);
        showButtons = true;
        zoomFactor = temp;
        scaleFactor = 1;
        printLines();
        setSize(tempDim);
        return bi;
    }
    
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (!Globals.editable) {
            return;
        }
        if (Globals.proofsForUndo.isEmpty() && Globals.assist != null) {
            // Prepare for Undo
            NDLine[] tempArray = new NDLine[proofArray.length];
            for (int i = 0; i < proofArray.length; i++) {
//                tempArray[i] = proofArray[i].clone();
                tempArray[i] = proofArray[i].clone();
            }
            Globals.proofsForUndo.push(tempArray);
            Globals.goalsForUndo.push(Globals.currentGoalIndex);
//            System.out.println("Push " + Globals.currentGoalIndex);
            Globals.resourcesForUndo.push(Globals.currentResourceIndex);
            Globals.lineNumsForUndo.push(Globals.lineNum);
            Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
            Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
            Globals.frame.setUndoable(!Globals.proofsForUndo.isEmpty());
        }
        
        removeAll();
        int mouseX = e.getX();
        int mouseY = e.getY();
        
        if (mouseX > lineNumX && mouseX < justificationX + 100) {
            int lineNum = (int)((((mouseY/zoomFactor) - topOffSet)/lineHeight));
            if (lineNum < proofArray.length && proofArray[lineNum].getType() != 5) {
                if (proofArray[lineNum].getJustification().getBlank()) { // Let us select blank lines as goals
                    Globals.currentGoalIndex = lineNum;
                } else if (proofArray[lineNum].getType() > 6 && proofArray[lineNum].getType() < 11) { // If we have a line in an identity box
                    if (lineNum > 0 && proofArray[lineNum-1].getType() == 5) { // ... let it be the current goal, as long as it's next to a blank
                        Globals.currentGoalIndex = lineNum;
                    }
                    if (lineNum+1 < proofArray.length && proofArray[lineNum+1].getType() == 5) {
                        Globals.currentGoalIndex = lineNum;
                    }
                } else if (lineNum < proofArray.length && lineNum < Globals.currentGoalIndex) {
                    Globals.currentResourceIndex = lineNum;
                }
            }
            
        }
        
//        proofArray = Globals.assist.conElim(proofArray[2],proofArray[0]);

        
        
        printLines();
        repaint();
        Globals.status.setDogsBodyText("");
        requestFocusInWindow();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void actionPerformed(ActionEvent e) {
        removeAll();
        try {
        if (e.getActionCommand().equals("introRule")){
            
            int goal = Globals.currentGoalIndex;
            int resource;
            if (Globals.currentResourceIndex > -1 && Globals.currentResourceIndex < proofArray.length) {
                resource = Globals.currentResourceIndex;
            } else {
                resource = goal;
            }
            
            String op = proofArray[goal].getSMainOp();
            introActions(op, goal, resource);
            Globals.clearRedo();
        } else if (e.getActionCommand().equals("elimRule")) {
            
            int goal = Globals.currentGoalIndex;
            int resource = Globals.currentResourceIndex;
            
            String op;
            if (Globals.allowedRules.get("universalsShortcuts")) {
                op = proofArray[resource].getNonUniSMainOp();
            } else {
                op = proofArray[resource].getSMainOp();
            }
            elimActions(op, goal, resource);
            Globals.clearRedo();
        } else if (e.getActionCommand().equals("sameLineRule")) {
            
            int goal = Globals.currentGoalIndex;
            int resource = Globals.currentResourceIndex;
            
            // Prepare for Undo
            NDLine[] tempArray = new NDLine[proofArray.length];
            for (int i = 0; i < proofArray.length; i++) {
                tempArray[i] = proofArray[i].clone();
            }
            Globals.proofsForUndo.push(tempArray);
            Globals.goalsForUndo.push(goal);
            if (resource == goal) {
                Globals.resourcesForUndo.push(-1);
            } else {
                Globals.resourcesForUndo.push(resource);
            }
            Globals.lineNumsForUndo.push(Globals.lineNum);
            Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
            Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
            
            Globals.termsUsed.addAll(proofArray[goal].getAllTerms());
            Globals.termsUsed.addAll(proofArray[resource].getAllTerms());
            Globals.status.setArityButtonToolTip();
            
            // Main Stuff
            
            proofArray = Globals.assist.sameLine(proofArray[goal],proofArray[resource]);
            
            
            
            if (Globals.reverseUndo) {
                Globals.proofsForUndo.pop();
                Globals.goalsForUndo.pop();
                Globals.resourcesForUndo.pop();
                Globals.lineNumsForUndo.pop();
                Globals.rulesUsedForUndo.pop();
                Globals.termsUsed = Globals.termsUsedForUndo.pop();
                Globals.reverseUndo = false;
            }
            Globals.clearRedo();
        } else if (e.getActionCommand().equals("doubleNegationRule")) {
            
            int goal = Globals.currentGoalIndex;
            int resource;
            if (Globals.currentResourceIndex > -1 && Globals.currentResourceIndex < proofArray.length) {
                resource = Globals.currentResourceIndex;
            } else {
                resource = goal;
            }
            
            // Prepare for Undo
            NDLine[] tempArray = new NDLine[proofArray.length];
            for (int i = 0; i < proofArray.length; i++) {
                tempArray[i] = proofArray[i].clone();
            }
            Globals.proofsForUndo.push(tempArray);
            Globals.goalsForUndo.push(goal);
            if (resource == goal) {
                Globals.resourcesForUndo.push(-1);
            } else {
                Globals.resourcesForUndo.push(resource);
            }
            Globals.lineNumsForUndo.push(Globals.lineNum);
            Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
            Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
            
            
            Globals.termsUsed.addAll(proofArray[goal].getAllTerms());
            Globals.termsUsed.addAll(proofArray[resource].getAllTerms());
            Globals.status.setArityButtonToolTip();
            
            // Main Stuff
            
            proofArray = Globals.assist.doubleNegation(proofArray[goal], proofArray[resource]);
            
            
            
            if (Globals.reverseUndo) {
                Globals.proofsForUndo.pop();
                Globals.goalsForUndo.pop();
                Globals.resourcesForUndo.pop();
                Globals.lineNumsForUndo.pop();
                Globals.rulesUsedForUndo.pop();
                Globals.termsUsed = Globals.termsUsedForUndo.pop();
                Globals.reverseUndo = false;
            }
            Globals.clearRedo();
        } else if (e.getActionCommand().equals("shortcutRule")) {
            
            int goal = Globals.currentGoalIndex;
            int resource = Globals.currentResourceIndex;
            
            String op = proofArray[resource].getMainOp();
            
            elimActions(op, goal, resource);
            Globals.clearRedo();
        } else if (e.getActionCommand().equals("inductionRule")) {
            int goal = Globals.currentGoalIndex;
            int resource = Globals.currentResourceIndex;
            if (resource < 0) {
                resource = 0;
            }
            String op = "IND";
            
            introActions(op, goal, resource);
            Globals.clearRedo();
        }
        
        if (Globals.assist.checkFinished()) {
            Globals.currentGoalIndex = -1;
            Globals.currentResourceIndex = -1;
        }
        
        printLines();
        repaint();
//        System.out.println("---------------------------------------------------------wo");
//        System.out.println("---------------------------------------------------------" + Globals.rulesUsed.contains("Q"));
        Globals.frame.checkTitle();
        Globals.frame.setUndoable(!Globals.proofsForUndo.isEmpty());
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this, exc.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            exc.printStackTrace();
        }
        if (Globals.status != null){
            Globals.status.setArityButtonToolTip();
        }
        requestFocusInWindow();
    }
    
    private void introActions(String op, int goal, int resource) throws LineNotInProofArrayException {
        // Prepare for Undo
        NDLine[] tempArray = new NDLine[proofArray.length];
        for (int i = 0; i < proofArray.length; i++) {
            tempArray[i] = proofArray[i].clone();
        }
        Globals.proofsForUndo.push(tempArray);
        Globals.goalsForUndo.push(goal);
        if (resource == goal) {
            Globals.resourcesForUndo.push(-1);
        } else {
            Globals.resourcesForUndo.push(resource);
        }
        Globals.lineNumsForUndo.push(Globals.lineNum);
        Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
        Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
        
//        System.out.println("=========================================================");
        Globals.termsUsed.addAll(proofArray[goal].getAllTerms());
        Globals.termsUsed.addAll(proofArray[resource].getAllTerms());
//        System.out.println(Globals.termsUsed.contains("s"));
        Globals.status.setArityButtonToolTip();
        
        // Main Stuff
        if (op.equals("con")){
            proofArray = Globals.assist.conIntro(proofArray[goal],proofArray[resource]);
        } else if (op.equals("dis")) {
            proofArray = Globals.assist.disIntro(proofArray[goal],proofArray[resource]);
        } else if (op.equals("imp")) {
            proofArray = Globals.assist.impIntro(proofArray[goal],proofArray[resource]);
        } else if (op.equals("equ")) {
            proofArray = Globals.assist.equIntro(proofArray[goal],proofArray[resource]);
        } else if (op.equals("neg")) {
            proofArray = Globals.assist.negIntro(proofArray[goal],proofArray[resource]);
        } else if (op.equals("qa")) {
            try {
                proofArray = Globals.assist.qaIntro(proofArray[goal], proofArray[resource]);
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrongLineTypeException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equals("qe")) {
            try {
                proofArray = Globals.assist.qeIntro(proofArray[goal], proofArray[resource]);
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrongLineTypeException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equals("eq")) {
            proofArray = Globals.assist.eqIntro(proofArray[goal], proofArray[resource]);
        } else if (op.equals("IND")) {
            proofArray = Globals.assist.induction(proofArray[goal], proofArray[resource]);
        } else if (op.equals("at")) {
            proofArray = Globals.assist.atIntro(proofArray[goal], proofArray[resource]);
        } else if (op.equals("box")) {
            try {
                proofArray = Globals.assist.boxIntro(proofArray[goal], proofArray[resource]);
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equals("dia")) {
            proofArray = Globals.assist.diaIntro(proofArray[goal], proofArray[resource]);
        } else if (op.equals("nom")) {
            proofArray = Globals.assist.nomIntro(proofArray[goal], proofArray[resource]);
        } else if (op.equals("self")) {
            proofArray = Globals.assist.selfIntro(proofArray[goal], proofArray[resource]);
        } else {
            Globals.reverseUndo = true;
        }
        
        if (Globals.reverseUndo) {
            Globals.proofsForUndo.pop();
            Globals.goalsForUndo.pop();
            Globals.resourcesForUndo.pop();
            Globals.lineNumsForUndo.pop();
            Globals.rulesUsedForUndo.pop();
            Globals.termsUsed = Globals.termsUsedForUndo.pop();
            Globals.reverseUndo = false;
        }
    }
    
    private void elimActions(String op, int goal, int resource) throws LineNotInProofArrayException, WrongLineTypeException {
        
        // Prepare for Undo
        NDLine[] tempArray = new NDLine[proofArray.length];
        for (int i = 0; i < proofArray.length; i++) {
            tempArray[i] = proofArray[i].clone();
        }
        Globals.proofsForUndo.push(tempArray);
        Globals.goalsForUndo.push(goal);
        if (resource == goal) {
            Globals.resourcesForUndo.push(-1);
        } else {
            Globals.resourcesForUndo.push(resource);
        }
        Globals.lineNumsForUndo.push(Globals.lineNum);
        Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
        Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
        
        if (proofArray[resource].getLineNum() < 0 && proofArray[resource].getLineNum() > -10) {
            Globals.rulesUsed.add("Q");
        }
        
        Globals.termsUsed.addAll(proofArray[goal].getAllTerms());
        Globals.termsUsed.addAll(proofArray[resource].getAllTerms());
        Globals.status.setArityButtonToolTip();
        
        // Main Stuff
        if (op.equals("con")){
            proofArray = Globals.assist.conElim(proofArray[goal],proofArray[resource]);
        } else if (op.equals("dis")) {
            proofArray = Globals.assist.disElim(proofArray[goal],proofArray[resource]);
        } else if (op.equals("imp")) {
            proofArray = Globals.assist.impElim(proofArray[goal],proofArray[resource]);
        } else if (op.equals("equ")) {
            if (proofArray[goal].getType() > 6 && proofArray[goal].getType() < 11) { // If we're in an identity box
                proofArray = Globals.assist.idBoxEquElim(proofArray[goal], proofArray[resource]);
            } else {
                proofArray = Globals.assist.equElim(proofArray[goal], proofArray[resource]);
            }
        } else if (op.equals("neg")) {
            proofArray = Globals.assist.negElim(proofArray[goal],proofArray[resource]);
        } else if (proofArray[resource].getLine().equals("\\falsum")){
            proofArray = Globals.assist.falsumElim(proofArray[goal],proofArray[resource]);
        } else if (op.equals("qa")) {
            proofArray = Globals.assist.qaElim(proofArray[goal],proofArray[resource]);
        } else if (op.equals("qe")) {
            try {
                proofArray = Globals.assist.qeElim(proofArray[goal],proofArray[resource]);
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equals("eq")) {
            if (proofArray[goal].getType() > 6 && proofArray[goal].getType() < 11) { // If we're in an identity box
                proofArray = Globals.assist.idBoxEqElim(proofArray[goal], proofArray[resource]);
            } else {
                try {
                    proofArray = Globals.assist.eqElim(proofArray[goal], proofArray[resource]);
                } catch (IndexOutOfBoundsException ex) {
                    Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MissingArityException ex) {
                    Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (op.equals("at")) {
            proofArray = Globals.assist.atElim(proofArray[goal], proofArray[resource]);
        } else if (op.equals("box")) {
            proofArray = Globals.assist.boxElim(proofArray[goal], proofArray[resource]);
        } else if (op.equals("dia")) {
            try {
                proofArray = Globals.assist.diaElim(proofArray[goal], proofArray[resource]);
            } catch (MissingArityException ex) {
                Logger.getLogger(ProofPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equals("nom")) {
            if (proofArray[goal].getType() > 6 && proofArray[goal].getType() < 11) { // If we're in an identity box
                proofArray = Globals.assist.idBoxNomElim(proofArray[goal], proofArray[resource]);
            } else {
                proofArray = Globals.assist.nomElim(proofArray[goal], proofArray[resource]);
            }
        } else if (op.equals("self")) {
            proofArray = Globals.assist.selfElim(proofArray[goal], proofArray[resource]);
        } else {
            Globals.reverseUndo = true;
        }
        
        
        
        if (Globals.reverseUndo) {
            Globals.proofsForUndo.pop();
            Globals.goalsForUndo.pop();
            Globals.resourcesForUndo.pop();
            Globals.lineNumsForUndo.pop();
            Globals.rulesUsedForUndo.pop();
            Globals.termsUsed = Globals.termsUsedForUndo.pop();
            Globals.reverseUndo = false;
        }
        
    }
    
    public void magicMode() throws LineNotInProofArrayException, WrongLineTypeException {
        if (!Globals.editable) {
            JOptionPane.showMessageDialog(this, "Proof is not editable");
            return;
        }
        // Prepare for Undo
        NDLine[] tempArray = new NDLine[proofArray.length];
        for (int i = 0; i < proofArray.length; i++) {
            tempArray[i] = proofArray[i].clone();
        }
        Globals.proofsForUndo.push(tempArray);
        Globals.goalsForUndo.push(Globals.currentGoalIndex);
        if (Globals.currentResourceIndex == Globals.currentGoalIndex) {
            Globals.resourcesForUndo.push(-1);
        } else {
            Globals.resourcesForUndo.push(Globals.currentResourceIndex);
        }
        Globals.lineNumsForUndo.push(Globals.lineNum);
        Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
        Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
        
        proofArray = Globals.assist.runMagicMode(10);
        printLines();
        repaint();
        
        Globals.frame.setUndoable(!Globals.proofsForUndo.isEmpty());
        
        if (Globals.reverseUndo) {
//            Globals.proofsForUndo.pop();
//            Globals.goalsForUndo.pop();
//            Globals.resourcesForUndo.pop();
//            Globals.lineNumsForUndo.pop();
            Globals.reverseUndo = false;
        }
    }
    
    public void cutALine(String newLine) {
        if (!Globals.editable) {
            JOptionPane.showMessageDialog(this, "Proof is not editable");
            return;
        }
        // Prepare for Undo
        NDLine[] tempArray = new NDLine[proofArray.length];
        for (int i = 0; i < proofArray.length; i++) {
            tempArray[i] = proofArray[i].clone();
        }
        Globals.proofsForUndo.push(tempArray);
        Globals.goalsForUndo.push(Globals.currentGoalIndex);
        Globals.resourcesForUndo.push(Globals.currentResourceIndex);
        Globals.lineNumsForUndo.push(Globals.lineNum);
        Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
        Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
        
        // Main Stuff
        proofArray = Globals.assist.cut(proofArray[Globals.currentGoalIndex],newLine);
        
        
        if (Globals.reverseUndo) {
            Globals.proofsForUndo.pop();
            Globals.goalsForUndo.pop();
            Globals.resourcesForUndo.pop();
            Globals.lineNumsForUndo.pop();
            Globals.rulesUsedForUndo.pop();
            Globals.termsUsedForUndo.pop();
            Globals.reverseUndo = false;
        }
//        System.out.println("proofArray.length =" + proofArray.length);
        printLines();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
//        System.out.println("================");
        if (proofArray != null && ke.getKeyChar() > 47 && ke.getKeyChar() < 58 && proofArray.length > 6 && Globals.currentGoalIndex > -1) {
            int toSelect = Integer.parseInt("" + ke.getKeyChar());
            for (int i = 0; i < 6; i++) {
                if (proofArray[i].getLineNum() == -(toSelect) && Globals.editable) {
                    Globals.currentResourceIndex = i;
                    Globals.status.setDogsBodyText("Q"+(i+1));
                }
            }
            printLines();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        
    }
} 
