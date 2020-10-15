/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package proofassistant;
import proofassistant.line.NDLine;
import proofassistant.line.NDJust;
import proofassistant.exception.LineNotInProofArrayException;
import proofassistant.justification.JustFromString;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import static proofassistant.Globals.frame;
import proofassistant.exception.WrongLineTypeException;

/**
 *
 * @author Declan
 */
public class ProofFrame extends JFrame implements ActionListener, ItemListener, ClipboardOwner {
    private JCheckBoxMenuItem greyScopesItem;
    private JCheckBoxMenuItem automaticParametersItem;
    private JCheckBoxMenuItem reverseConIntroItem;
    private JCheckBoxMenuItem useNeqItem;
    private JCheckBoxMenuItem useUniversalShortcuts;
    private JCheckBoxMenuItem useBrackets;
    private JRadioButtonMenuItem njItem;
    private JRadioButtonMenuItem nkItem;
    private JRadioButtonMenuItem qItem;
    private JRadioButtonMenuItem pAItem;
    private ButtonGroup proofSystem;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private ProofPanel panel;
    private String newProofBoxContents = "";
    private JFileChooser pngFileChooser;
    private JFileChooser myFileChooser;
    private JFileChooser gifFileChooser;
    private StatusBar status;
    
    private String versionNum = "1.5";
    private String date = "11/10/18";
    
    private int scrollspeed = 16;
    
    
    
    public ProofFrame(NDLine[] proofArray){
        super("Natural Deduction Planner");
        myInit();
        
        if (proofArray != null) {
            panel = new ProofPanel(proofArray);
            Globals.scrollpane = new JScrollPane(panel);
            Globals.scrollpane.setBorder(null);
            Globals.scrollpane.getVerticalScrollBar().setUnitIncrement(scrollspeed);
            getContentPane().removeAll();
            getContentPane().add(Globals.scrollpane);
            getContentPane().add(status, BorderLayout.SOUTH);
            status.updateRuleSystem();
            //setSize(Globals.proofWidth,Globals.proofHeight + 200);
            revalidate();
        }
    }
    
    public ProofFrame(Path ndpFile){
        super("Natural Deduction Planner");
        myInit();
        
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(ndpFile, charset)) {
            openProof(reader);
        } catch (IOException ex) {
            System.err.format("IOException: %s%n", 1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "File is corrupted!", "Open Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
    private void myInit() {
        setLAF();
        
        pngFileChooser = new JFileChooser();
        FileFilter pngFilter = new FileNameExtensionFilter("PNG file (*.png)", "png");
        pngFileChooser.addChoosableFileFilter(pngFilter);
        pngFileChooser.setFileFilter(pngFilter);
        
        myFileChooser = new JFileChooser();
        FileFilter anyFilter = new FileNameExtensionFilter("Proof Assistant file (*.ndp, *.ndu)", "ndp", "ndu");
        myFileChooser.addChoosableFileFilter(anyFilter);
        FileFilter ndpFilter = new FileNameExtensionFilter("Proof file (*.ndp)", "ndp");
        myFileChooser.addChoosableFileFilter(ndpFilter);
        FileFilter nduFilter = new FileNameExtensionFilter("Uneditable proof file (*.ndu)", "ndu");
        myFileChooser.addChoosableFileFilter(nduFilter);
        myFileChooser.setFileFilter(anyFilter);
        
        gifFileChooser = new JFileChooser();
        FileFilter gifFilter = new FileNameExtensionFilter("GIF file (*.gif)", "gif");
        gifFileChooser.addChoosableFileFilter(gifFilter);
        gifFileChooser.setFileFilter(gifFilter);
        
        
        
        
        setSize(Math.max(Globals.proofWidth, 500),Globals.proofHeight + 200);
        setMinimumSize(new Dimension(1000,600));
        setMaximumSize(new Dimension(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height));
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screen.getWidth() - getWidth()) /2);
        int y = (int) ((screen.getHeight() -getHeight()) /2);
        setLocation(x, y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMenus();
        Globals.setDefaultOps();
        
        reviewSetup();
        status = new StatusBar(this);
        status.addActionListener(this);
        Globals.status = status;
        getContentPane().add(status, BorderLayout.SOUTH);
        
        // From here: http://www.daniweb.com/software-development/java/threads/69039/customize-jframe-exit
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //create custom close operation
        this.addWindowListener(new WindowAdapter()
        {
              public void windowClosing(WindowEvent e)
              {
                  saveSetup();
                  dispose();
              }
        });
    }
    
    
    private void setListOfSystems() {
//        System.out.println("hiho");
        ProofSystem nj = new ProofSystem("NJ");
        nj.add("conIntro");
        nj.add("conElim");
        nj.add("disIntro");
        nj.add("disElim");
        nj.add("negIntro");
        nj.add("negElim");
        nj.add("impIntro");
        nj.add("impElim");
        nj.add("equIntro");
        nj.add("equElim");
        nj.add("qaIntro");
        nj.add("qaElim");
        nj.add("qeIntro");
        nj.add("qeElim");
        nj.add("eqIntro");
        nj.add("eqElim");
        nj.add("autoParameters");
        nj.add("sameLine");
//        nj.add("falsumElim");
//        nj.add("falsNeElim");
        Globals.listOfSystems.add(nj);
        
        ProofSystem nk = new ProofSystem("NK", nj);
        nk.add("doubleNegation");
        Globals.listOfSystems.add(nk);
        
        ProofSystem nkPl = new ProofSystem("NK+", nk);
        nkPl.add("eqIdentityBoxes");
        nkPl.add("equIdentityBoxes");
        nkPl.add("universalsShortcuts");
        Globals.listOfSystems.add(nkPl);
        
        ProofSystem q = new ProofSystem("Q", nkPl);
        q.add("Q");
        Globals.listOfSystems.add(q);
        
        ProofSystem pa = new ProofSystem("PA", q);
        pa.add("induction");
        Globals.listOfSystems.add(pa);
        
        ProofSystem h = new ProofSystem("H", nkPl);
        h.add("showContext");
        h.add("diaIntro");
        h.add("diaElim");
        h.add("boxIntro");
        h.add("boxElim");
        h.add("atIntro");
        h.add("atElim");
        h.add("nomIntro");
        h.add("nomElim");
        h.add("selfIntro");
        h.add("selfElim");
        Globals.listOfSystems.add(h);
        
    }
    
    public void checkTitle() {
        ArrayList<ProofSystem> possibleSystems = new ArrayList<>();
        int i = 0;
//        System.out.println(Globals.listOfSystems.size());
        while (i < Globals.listOfSystems.size()) {
            if (Globals.listOfSystems.get(i).supportsRuleSet(Globals.rulesUsed)) {
                possibleSystems.add(Globals.listOfSystems.get(i));
//                System.out.println("Added " + Globals.listOfSystems.get(i).getName());
            }
            i++;
        }
        for (int q = 0; q < possibleSystems.size(); q++) {
//            System.out.println("testing " + possibleSystems.get(q).getName());
            for (int r = 0; r < possibleSystems.size(); r++) {
//                System.out.println("r " + r + " possibles size + " + possibleSystems.size());
//                System.out.println("    against " + possibleSystems.get(r).getName());
                if (r != q && possibleSystems.get(r).containsAll(possibleSystems.get(q))) {
//                    System.out.println("        removed");
                    possibleSystems.remove(r);
                    if (r < q) {
                        q--;
                    }
                    r--;
                }
            }
        }
        
        if (possibleSystems.isEmpty()) {
        } else {
            String title = possibleSystems.get(0).getName().toString();
            for (int j = 1; j < possibleSystems.size(); j++) {
                title = title + ", " + possibleSystems.get(j).getName().toString();
            }
            if (status != null)
            status.setProofSystem(title);
        }
    }
    
    public void setUndoable(boolean undoable) {
        undoItem.setEnabled(undoable);
    }
    
    public void setRedoable(boolean redoable) {
        redoItem.setEnabled(redoable);
    }
    
    public void setProofSystem(int pS) {
        proofSystem.clearSelection();
        if (pS == 0) {
            proofSystem.setSelected(njItem.getModel(), true);
        } else if (pS == 1) {
            proofSystem.setSelected(nkItem.getModel(), true);
        } else if (pS == 2) {
            proofSystem.setSelected(qItem.getModel(), true);
        } else if (pS == 3) {
            proofSystem.setSelected(pAItem.getModel(), true);
        }
    }
    
    public void setZoom(float zoom) {
        if (panel != null) {
            panel.setZoomFactor(zoom);
            panel.printLines();
            Globals.zoomFactor = zoom;
        }
    }
    
    
    public void reviewSetup(){
        File file = new File("pa.config");
        if (file.exists()) {
            Charset charset = Charset.forName("UTF-8");
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
                String line;
                ProofSystem current = null;
                boolean inProofSystem = false;
                boolean inRuleSet = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("") || line.charAt(0) == '%') {
                        
                    } else if (line.charAt(0) == ':' || inProofSystem) {
                        if (line.charAt(0) == ':') {
                            if (line.length() > 1) {
                                current = new ProofSystem(line.substring(1));
                                inProofSystem = true;
                            } else {
                                inProofSystem = false;
                                Globals.listOfSystems.add(current);
                            }
                        } else {
                            if (line.contains("\u001f")) {
                                current.setAxioms(line.substring(line.indexOf("\u001f") + 1));
                            } else {
                                current.add(line);
                            }
                        }
                    } else if (line.charAt(0) == ';' || inRuleSet) {
                        if (line.charAt(0) == ';') {
                            if (line.length() > 1) {
                                inRuleSet = true;
                            } else {
                                inRuleSet = false;
                            }
                        } else {
                            Globals.allowedRules.put(line.substring(0, line.indexOf("=")), Boolean.parseBoolean(line.substring(line.indexOf("=")+1)));
//                            System.out.println("put " + line.substring(0, line.indexOf("=")) + ", " + Boolean.parseBoolean(line.substring(line.indexOf("=")+1)));
                        }
                    } else {
                        String key = line.substring(0, line.indexOf("="));
                        if (key.equals("runMagicModeWithQa")) {
                            Globals.runMagicModeWithQa = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("reverseConIntro")) {
                            Globals.reverse2PremIntro = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("aritiyS")) {
                            Globals.aritiyS = line.substring(line.indexOf("=")+1);
                        } else if (key.equals("useNeq")) {
                            Globals.useNeq = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("showBrackets")) {
                            Globals.showBrackets = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("outOfScopeIsGrey")) {
                            Globals.outOfScopeIsGrey = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("zoomFactor")) {
                            Globals.zoomFactor = Float.parseFloat(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("currentOpsIndex")) {
                            Globals.currentOpsIndex = Integer.parseInt(line.substring(line.indexOf("=")+1));
                            if (Globals.currentOpsIndex == 0) {
                                Globals.setDefaultOps();
                            } else if (Globals.currentOpsIndex == 1) {
                                Globals.setNonAucklandOps();
                            }
                        } else if (key.equals("qShowNumbers")) {
                            Globals.qShowNumbers = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("numberTopDown")) {
                            Globals.numberTopDown = Boolean.parseBoolean(line.substring(line.indexOf("=")+1));
                        } else if (key.equals("numberOffset")) {
                            Globals.lineIncrement = Integer.parseInt(line.substring(line.indexOf("=")+1));
                        }
                    }
                }
                revalidate();
                reader.close();;
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
                setListOfSystems();
                Globals.setDefaultRulesAllowed();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Config file is corrupted!", "Open Error", JOptionPane.ERROR_MESSAGE);
                setListOfSystems();
                Globals.setDefaultRulesAllowed();
            }
        } else {
            setListOfSystems();
            Globals.setDefaultRulesAllowed();
        }
        
    }
    
    public void saveSetup() {
        File file = new File("pa.config");
        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
            writer.write("% == Settings File ==");
            writer.newLine();
            writer.write("% Start a line with % to comment");
            writer.newLine();
            writer.write("% This file is rewritten every time ProofAssistant closes by selecting file, close");
            writer.newLine();
            writer.write("% Editing values will launch the assistant with the values supplied");
            writer.newLine();
            writer.newLine();
            writer.write("%% Booleans affecting the proof system %%");
            writer.newLine();
            writer.write("% Run Magic Mode after instances of universal elimination");
            writer.newLine();
            writer.write("runMagicModeWithQa="+Globals.runMagicModeWithQa);
            writer.newLine();
            writer.write("% Reverse the order for conjunction introduction");
            writer.newLine();
            writer.write("reverseConIntro="+Globals.reverse2PremIntro);
            writer.newLine();
            writer.newLine();
            
            writer.write("%% Non-boolean proof system settings");
            writer.newLine();
            writer.write("% Set the arities to use");
            writer.newLine();
            writer.write("aritiyS="+Globals.aritiyS);
            writer.newLine();
            writer.newLine();
            
            writer.write("%% Controls for visual tweaks");
            writer.newLine();
            writer.write("% Use ≠ for ~( = )");
            writer.newLine();
            writer.write("useNeq="+Globals.useNeq);
            writer.newLine();
            writer.write("% Show brackets around all terms");
            writer.newLine();
            writer.write("showBrackets="+Globals.showBrackets);
            writer.newLine();
            writer.write("% Make out of scope lines appear grey");
            writer.newLine();
            writer.write("outOfScopeIsGrey="+Globals.outOfScopeIsGrey);
            writer.newLine();
            writer.write("% Set the zoom factor (1 is 100%)");
            writer.newLine();
            writer.write("zoomFactor="+Globals.zoomFactor);
            writer.newLine();
            writer.write("% Choose which symbols to use. 0 is Auckland style, 1 uses arrows");
            writer.newLine();
            writer.write("currentOpsIndex="+Globals.currentOpsIndex);
            writer.newLine();
            writer.write("% Show numbers rather than ss0 etc.");
            writer.newLine();
            writer.write("qShowNumbers="+Globals.qShowNumbers);
            writer.newLine();
            writer.write("% Number lines top to bottom");
            writer.newLine();
            writer.write("numberTopDown="+Globals.numberTopDown);
            writer.newLine();
            writer.write("% Amount to offset line numbers");
            writer.newLine();
            writer.write("numberOffset="+Globals.lineIncrement);
            writer.newLine();
            
            writer.write("%% Current Preset");
            writer.newLine();
            writer.write("% The following constitutes the current rule settings");
            writer.newLine();
            writer.write(";startruleset");
            writer.newLine();
            Iterator<String> terate = Globals.allowedRules.keySet().iterator();
            while (terate.hasNext()) {
                String currentRule = terate.next();
                writer.write(currentRule + "=" + Globals.allowedRules.get(currentRule));
                writer.newLine();
            }
            writer.write(";");
            writer.newLine();
            
            writer.write("%% List of Presets");
            writer.newLine();
            writer.write("% Each preset starts with :Name followed by the name");
            writer.newLine();
            writer.write("% Each following line is a rule to include in the preset");
            writer.newLine();
            writer.write("% Preset ends at :");
            writer.newLine();
            for (int i = 0; i < Globals.listOfSystems.size(); i++) {
                ProofSystem current = Globals.listOfSystems.get(i);
                writer.write(":" + current.getName());
                writer.newLine();
                for (int j = 0; j < current.size(); j++) {
                    writer.write(current.get(j).toString());
                    writer.newLine();
                }
                writer.write("\u001f" + current.getAxioms());
                writer.newLine();
                writer.write(":");
                writer.newLine();
            }
            
            writer.close();
        } catch (IOException ex) {
            System.err.format("IOException: %s%n", ex);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Settings file is corrupted!", "Open Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
    private void setLAF() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }   catch (Exception e) {
            System.out.println("setLookAndFeel failed");
        }
        setLayout(new BorderLayout());
    }
    
    private void setMenus() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newItem = new JMenuItem("New...");
        newItem.addActionListener(this);
        newItem.setActionCommand("newProof");
        newItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(newItem);
        
        JMenu advanced = new JMenu("Advanced");
        
        JMenuItem newFromTeXItem = new JMenuItem("New Proof from TeX Code...");
        newFromTeXItem.addActionListener(this);
        newFromTeXItem.setActionCommand("newProofFromTeX");
        newFromTeXItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+java.awt.event.InputEvent.SHIFT_MASK));
        advanced.add(newFromTeXItem);
        
        file.add(advanced);
        
        JMenuItem saveItem = new JMenuItem("Save...");
        saveItem.addActionListener(this);
        saveItem.setActionCommand("saveProof");
        saveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(saveItem);
        
        JMenuItem openItem = new JMenuItem("Open...");
        openItem.addActionListener(this);
        openItem.setActionCommand("openProof");
        openItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(openItem);
        
        JMenu export = new JMenu("Export");
        
        JMenuItem exportTeXItem = new JMenuItem("To TeX code...");
        exportTeXItem.addActionListener(this);
        exportTeXItem.setActionCommand("exportTeXProof");
        exportTeXItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+java.awt.event.InputEvent.SHIFT_MASK));
        export.add(exportTeXItem);
        
        JMenuItem exportTextItem = new JMenuItem("To plain text...");
        exportTextItem.addActionListener(this);
        exportTextItem.setActionCommand("exportTextProof");
        export.add(exportTextItem);
        
        JMenuItem exportPNGItem = new JMenuItem("To .png...");
        exportPNGItem.addActionListener(this);
        exportPNGItem.setActionCommand("exportPNGProof");
        exportPNGItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        export.add(exportPNGItem);
        
        JMenuItem exportGIFItem = new JMenuItem("To .gif...");
        exportGIFItem.addActionListener(this);
        exportGIFItem.setActionCommand("exportGIFProof");
        export.add(exportGIFItem);
        
        JMenuItem exportClipBoardPNGItem = new JMenuItem("Copy proof to clipboard");
        exportClipBoardPNGItem.addActionListener(this);
        exportClipBoardPNGItem.setActionCommand("exportClipboardProof");
        exportClipBoardPNGItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        export.add(exportClipBoardPNGItem);
        
        file.add(export);
        
        
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(this);
        closeItem.setActionCommand("closeAll");
        closeItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(closeItem);
        
        menuBar.add(file);
        
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        
        undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(this);
        undoItem.setActionCommand("undoStep");
        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        undoItem.setEnabled(!Globals.proofsForUndo.isEmpty());
        edit.add(undoItem);
        
        redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(this);
        redoItem.setActionCommand("redoStep");
        redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        redoItem.setEnabled(!Globals.proofsForRedo.isEmpty());
        edit.add(redoItem);
        
        edit.addSeparator();
        
        JMenuItem cutLineItem = new JMenuItem("Cut Line");
        cutLineItem.addActionListener(this);
        cutLineItem.setActionCommand("cutALine");
        edit.add(cutLineItem);
        
        JMenuItem magicModeItem = new JMenuItem("Magic Mode");
        magicModeItem.addActionListener(this);
        magicModeItem.setActionCommand("runMagicMode");
        edit.add(magicModeItem);
        
        menuBar.add(edit);
        
        JMenu options = new JMenu("Options");
        options.setMnemonic(KeyEvent.VK_O);
        
//        proofSystem = new ButtonGroup();
//        
//        njItem = new JRadioButtonMenuItem("NJ", true);
//        njItem.addActionListener(this);
//        njItem.setActionCommand("changeToNJ");
//        proofSystem.add(njItem);
////        options.add(njItem);
//        
//        nkItem = new JRadioButtonMenuItem("NK", false);
//        nkItem.addActionListener(this);
//        nkItem.setActionCommand("changeToNK");
//        proofSystem.add(nkItem);
////        options.add(nkItem);
//        
//        qItem = new JRadioButtonMenuItem("Q", false);
//        qItem.addActionListener(this);
//        qItem.setActionCommand("changeToQ");
//        proofSystem.add(qItem);
////        options.add(qItem);
//        
//        pAItem = new JRadioButtonMenuItem("PA", false);
//        pAItem.addActionListener(this);
//        pAItem.setActionCommand("changeToPA");
//        proofSystem.add(pAItem);
////        options.add(pAItem);
        
        JMenuItem rulePaletteItem = new JMenuItem("Rule Palette");
        rulePaletteItem.addActionListener(this);
        rulePaletteItem.setActionCommand("showRulePalette");
        options.add(rulePaletteItem);
        
        JMenuItem myAxiomsItem = new JMenuItem("My axioms...");
        myAxiomsItem.addActionListener(this);
        myAxiomsItem.setActionCommand("openAxioms");
        options.add(myAxiomsItem);
        
        options.addSeparator();
        
        JMenuItem zoomIn = new JMenuItem("Zoom in");
        zoomIn.addActionListener(this);
        zoomIn.setActionCommand("increaseZoom");
        zoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        options.add(zoomIn);
        
        JMenuItem zoomOut = new JMenuItem("Zoom out");
        zoomOut.addActionListener(this);
        zoomOut.setActionCommand("decreaseZoom");
        zoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        options.add(zoomOut);
        
        JMenuItem zoomItem = new JMenuItem("Zoom...");
        zoomItem.addActionListener(this);
        zoomItem.setActionCommand("theZoom");
        options.add(zoomItem);
        
        
        options.addSeparator();
        
        
        greyScopesItem = new JCheckBoxMenuItem("Show lines in scope of goal", false);
        greyScopesItem.addItemListener(this);
        greyScopesItem.setActionCommand("changeScopes");
        options.add(greyScopesItem);
        
        reverseConIntroItem = new JCheckBoxMenuItem("Reverse the order of 2-place introductions", false);
        reverseConIntroItem.addItemListener(this);
        reverseConIntroItem.setActionCommand("changeConIntroPolarity");
        options.add(reverseConIntroItem);
        
        useNeqItem = new JCheckBoxMenuItem("Use \u2260 for negated identities", true);
        useNeqItem.addItemListener(this);
        useNeqItem.setActionCommand("changeNeqUse");
        options.add(useNeqItem);
  
        
        
        
        options.addSeparator();
        
        JMenuItem settingsItem = new JMenuItem("Settings...");
        settingsItem.addActionListener(this);
        settingsItem.setActionCommand("openSettings");
        options.add(settingsItem);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        aboutItem.setActionCommand("aboutApp");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        options.add(aboutItem);
        
        menuBar.add(options);
        
        setJMenuBar(menuBar);
    }
    
    public void updatePanel() {
        if (Globals.proofArray != null) {
            Globals.proofArray = Globals.assist.getProofArray();
            panel.setProofArray(Globals.proofArray);
            panel.printLines();
        }
    }
    
    

    
    private boolean inputIsGood(String input) {
        
        // Check brackets are balanced
        int bracketCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '{') {
                bracketCount ++;
            } else if (input.charAt(i) == '}') {
                bracketCount --;
            }
        }
        if (bracketCount != 0) {
            JOptionPane.showMessageDialog(this, "Warning: Unbalanced brackets: {}", "Parsing Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        bracketCount = 0;
        System.out.println(input);
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                bracketCount ++;
            } else if (input.charAt(i) == ')') {
                bracketCount --;
            }
        }
        if (bracketCount != 0) {
            JOptionPane.showMessageDialog(this, "Warning: Unbalanced brackets: ()", "Parsing Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (input.length() > 8 && input.substring(1,8).equals("sequent")) { // Check things specific to the \sequent input
            
            // Check that there are two arguments and that the conclusion is not blank
            try {
                NDLine temp = new NDLine(input,6);
                if (temp.getArg(2).equals("")) {
                    JOptionPane.showMessageDialog(this, "Conclusion is missing", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (StringIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(this, "Badly formed \\sequent. Is an argument missing?", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } else { // Check things specific to the other input
            
            // Check that there are enough arguments
            if (input.split(",").length == 1) {
                JOptionPane.showMessageDialog(this, "Only found one argument.\nRemember to flag your conclusion with \"->\".", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        
        return true;
    }
    
    private boolean lineInputIsGood(String input) {
        
        // Check brackets are balanced
        int bracketCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '{') {
                bracketCount ++;
            } else if (input.charAt(i) == '}') {
                bracketCount --;
            }
        }
        if (bracketCount != 0) {
            JOptionPane.showMessageDialog(this, "Warning: Unbalanced brackets: {}", "Parsing Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        bracketCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                bracketCount ++;
            } else if (input.charAt(i) == ')') {
                bracketCount --;
            }
        }
        if (bracketCount != 0) {
            JOptionPane.showMessageDialog(this, "Warning: Unbalanced brackets: ()", "Parsing Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        
        return true;
    }
    
    private String parseInput(String input) {
        input = input.replaceAll("(\\r|\\n)", "").replaceAll("\\\\\\!","");
        
        // Figure out written lines //
        Pattern pattern;
        Matcher matcher;
        
        pattern = Pattern.compile("(\\w+) is injective");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = input.replaceAll("(\\w+) is injective", "\\\\qa{x}{\\\\qe{y}{" + matcher.group(1) + "x=y}}");
        }
        
        pattern = Pattern.compile("(\\w+) is reflexive");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = input.replaceAll("(\\w+) is reflexive", "\\\\qa{x}{" + matcher.group(1) + "xx}");
        }
        
        pattern = Pattern.compile("(\\w+) is symmetric");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = input.replaceAll("(\\w+) is symmetric", "\\\\qa{x}{\\\\qa{y}{\\\\imp{" + matcher.group(1) + "xy}{" + matcher.group(1) +"yx}}}");
        }
        
        pattern = Pattern.compile("(\\w+) is transitive");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = input.replaceAll("(\\w+) is transitive", "\\\\qa{x}{\\\\qa{y}{\\\\qa{z}{\\\\imp{\\\\con{" + matcher.group(1) + "xy}{" + matcher.group(1) + "yz}}{" + matcher.group(1) +"xz}}}}");
        }
        
        pattern = Pattern.compile("(\\w+) is irreflexive");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = input.replaceAll("(\\w+) is irreflexive", "\\\\qa{x}{\\\\neg{" + matcher.group(1) + "xx}}");
        }
        
        
        
        
        input = input.replaceAll("\\s+", "");
        
        input = input.replaceAll("\\\\cdot", "⋅");
        
        
        
        if (input.length() > 8 && input.substring(1,8).equals("sequent")) {
            NDLine temp = new NDLine(input, 6);
            String premises = temp.getArg(1);
            String conclusion = temp.getArg(2);
            
            
            // Remove wayward commas
            int bracketCount = 0;
            String result = "";
            for (int i = 0; i < premises.length(); i++) {
                char c = premises.charAt(i);
                if (c == '(' || c == '{') {
                    bracketCount ++;
                } else if (c == ')' || c == '}') {
                    bracketCount --;
                }
                if (!(c == ',' && bracketCount > 0)) { // Remove commas that are inside brackets
                    result += c;
                }
            }
            premises = result;
            bracketCount = 0;
            result = "";
            for (int i = 0; i < conclusion.length(); i++) {
                char c = conclusion.charAt(i);
                if (c == '(' || c == '{') {
                    bracketCount ++;
                } else if (c == ')' || c == '}') {
                    bracketCount --;
                }
                if (!(c == ',' && bracketCount > 0)) { // Remove commas that are inside brackets
                    result += c;
                }
            }
            conclusion = result;
            
            if (premises.equals("\\rulename{Q}")) {
                Globals.allowedRules.put("Q", true);
                input = "-c," + conclusion;
            } else if (premises.equals("\\rulename{PA}")) {
                Globals.allowedRules.put("Q", true);
                Globals.allowedRules.put("induction", true);
                input = "-c," + conclusion;
            } else if (premises.length() > 0){
                input = premises + ",-c," + conclusion;
            } else {
                input = "-c," + conclusion;
            }
            
            
        } else {
            
            // Remove wayward commas
            int bracketCount = 0;
            String result = "";
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '(' || c == '{') {
                    bracketCount ++;
                } else if (c == ')' || c == '}') {
                    bracketCount --;
                }
                if (!(c == ',' && bracketCount > 0)) { // Remove commas that are inside brackets
                    result += c;
                }
            }
            input = result;
        }
        
        
        
        return input;
    }
    
    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();
        
        if (source.equals("closeAll")) {
//            funTime = System.currentTimeMillis();
            saveSetup();
//            System.out.println("" + (System.currentTimeMillis()-funTime));
            
            dispose();
        } else if (source.equals("openSettings")) {
            Settings sett = new Settings(this, true);
            sett.setVisible(true);
            if (panel != null) {
                panel.printLines();
            }
        } else if (source.equals("openAxioms")) {
            AxiomInputDialog axioms = new AxiomInputDialog(this, true);
            axioms.setVisible(true);
        } else if (source.equals("runMagicMode")) {
            if (panel != null) {
                try {
                    panel.magicMode();
                } catch (LineNotInProofArrayException ex) {
                    Logger.getLogger(ProofFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (WrongLineTypeException ex) {
                    Logger.getLogger(ProofFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (source.equals("newProofFromTeX")) {
            boolean finished = false;
            while (!finished) {
                
                String s = (String)JOptionPane.showInputDialog(this, "Enter your sequent. \nUse commas to separate each premise. \nWrite \"->\" before the conclusion. \nAlternatively, use the form \\sequent{[premises]}{[conclusion]}",
                        "New Proof", JOptionPane.PLAIN_MESSAGE, null, null, newProofBoxContents);
                newProofBoxContents = s;
                if (s != null && s.equals("open the pod bay doors")) {
                    JOptionPane.showMessageDialog(this, "I'm sorry Dave, I'm afraid I can't do that");
                    finished = true;
                } else if ( s!= null && inputIsGood(s)) {
                    finished = true;
                    s = parseInput(s);
                    System.out.println("problem " + s);
                    try {
                    if (s.length() > 8 && s.substring(1,8).equals("sequent")) { // If we're using the form \sequent{p, q}{r}
                        
                        NDLine temp = new NDLine(s.replaceAll("\\\\,", ""),6);
                        
                        String[] tempArray = temp.getArg(1).split(",");
                        
                        String[] argumentArray;
                        
                        int i = 0;
                        if (tempArray[0].equals("")) {
                            argumentArray = new String[2];
                        } else {
                            argumentArray = new String[tempArray.length+2];
                            while (i< tempArray.length) {
                                argumentArray[i] = tempArray[i].replaceAll("\\s+","");
                                i++;
                            }
                        }
                        
                        argumentArray[i] = "-c";
                        i++;
                        argumentArray[i] = temp.getArg(2).replaceAll("\\s+","");
                        
                        Globals.lineNum = 0;
                        Globals.editable = true;
                        Globals.specialLineNum = -10;
                        resetStacks(); // Empty all the stacks
                        Globals.terms.empty();
                        Globals.currentGoalIndex = -1;
                        Globals.currentResourceIndex = -1;
                        Globals.assist = new ProofObject(argumentArray);
                        Globals.proofArray = Globals.assist.getProofArray();
                        panel = new ProofPanel(Globals.proofArray);
                        Globals.scrollpane = new JScrollPane(panel);
                        Globals.scrollpane.setBorder(null);
                        Globals.scrollpane.getVerticalScrollBar().setUnitIncrement(scrollspeed);
                        getContentPane().removeAll();
                        getContentPane().add(Globals.scrollpane);
                        getContentPane().add(status, BorderLayout.SOUTH);
                        status.updateRuleSystem();
                        status.setArityButtonToolTip();
                        status.setDogsBodyText("");
                        int isMaximised = this.getExtendedState();
                        revalidate();
                        this.setTitle("Natural Deduction Planner");
                    } else if (s.length() > 0) {
                        Globals.lineNum = 0;
                        Globals.editable = true;
                        Globals.specialLineNum = -10;
                        resetStacks(); // Empty all the stacks
                        Globals.terms.empty();
                        Globals.currentGoalIndex = -1;
                        Globals.currentResourceIndex = -1;
                        Globals.assist = new ProofObject(s.split(","));
                        Globals.proofArray = Globals.assist.getProofArray();
                        panel = new ProofPanel(Globals.proofArray);
                        Globals.scrollpane = new JScrollPane(panel);
                        Globals.scrollpane.setBorder(null);
                        Globals.scrollpane.getVerticalScrollBar().setUnitIncrement(scrollspeed);
                        getContentPane().removeAll();
                        getContentPane().add(Globals.scrollpane);
                        getContentPane().add(status, BorderLayout.SOUTH);
                        status.updateRuleSystem();
                        status.setArityButtonToolTip();
                        status.setDogsBodyText("");
                        revalidate();
                        this.setTitle("Natural Deduction Planner");
                    }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(this, exc.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                        exc.printStackTrace();
                    }
                } else if (s == null) {
                    finished = true;
                }
            }
            checkTitle();
        } else if (source.equals("newProof")) {
            boolean finished = false;
            while (!finished) {
                try {
                String s = MyOptionPane.showFriendlyInputDialog();
                if (s!= null && s.contains("ERROR:")) {
                    throw new Exception(s.substring(s.indexOf("ERROR:"), s.indexOf(";")));
                } else if  ( s!= null && inputIsGood(s)) {
                    finished = true;
                    s = parseInput(s);
                    try {
                    Globals.editable = true;
                    Globals.lineNum = 0;
                    Globals.specialLineNum = -10;
                    resetStacks(); // Empty all the stacks
//                    System.out.println("proofframe says " + Globals.terms.getListOfUsedTerms().contains("s"));
                    Globals.terms.empty();
//                    System.out.println("proofframe says " + Globals.terms.getListOfUsedTerms().contains("s"));
                    Globals.currentGoalIndex = -1;
                    Globals.currentResourceIndex = -1;
                    Globals.assist = new ProofObject(s.split(","));
                    Globals.proofArray = Globals.assist.getProofArray();
                    panel = new ProofPanel(Globals.proofArray);
                    Globals.scrollpane = new JScrollPane(panel);
                    Globals.scrollpane.setBorder(null);
                    Globals.scrollpane.getVerticalScrollBar().setUnitIncrement(scrollspeed);
                    getContentPane().removeAll();
                    getContentPane().add(Globals.scrollpane);
                    getContentPane().add(status, BorderLayout.SOUTH);
                    status.updateRuleSystem();
//                    System.out.println("hi");
                    status.setArityButtonToolTip();
                    status.setDogsBodyText("");
//                    setContentPane(Globals.scrollpane);
                    
                    
                    revalidate();
                    this.setTitle("Natural Deduction Planner");
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(this, exc.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                        exc.printStackTrace();
                    }
                } else if (s == null) {
                    finished = true;
                }
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(this, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    exc.printStackTrace();
                }
            }
            checkTitle();
        } else if (source.equals("exportTeXProof")) {
            JFrame export;
            if (Globals.assist != null) {
                export = new ExportFrame(Globals.assist.getTeXCodeString(),"Export to TeX","The TeX code below makes use of ndproof.sty.");
                export.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to export!", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("exportTextProof")) {
            JFrame export;
            if (Globals.assist != null) {
                export = new ExportFrame(Globals.assist.getPlainTextString(),"Export to Plain Text","The plain text below will look best in a fixed-width font.");
                export.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to export!", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("exportPNGProof")) {
            if (Globals.assist != null) {
                
                BufferedImage bi = panel.getImage();
                
                
                int returnVal = pngFileChooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (pngFileChooser.getFileFilter().accept(pngFileChooser.getSelectedFile())) {
                        try{ImageIO.write(bi,"png",pngFileChooser.getSelectedFile());}catch (Exception exception) {}
                    } else {
                        try{ImageIO.write(bi,"png",new File(pngFileChooser.getSelectedFile() + ".png"));}catch (Exception exception) {}
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to export!", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("exportGIFProof")) {
            if (Globals.assist != null) {
                try {
                BufferedImage bi = panel.getImage();
                ImageOutputStream output = null;
                
                int returnVal = gifFileChooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (gifFileChooser.getFileFilter().accept(gifFileChooser.getSelectedFile())) {
                        try{output = new FileImageOutputStream(gifFileChooser.getSelectedFile());}catch (Exception exception) {}
                    } else {
                        try{output = new FileImageOutputStream(new File(gifFileChooser.getSelectedFile() + ".gif"));}catch (Exception exception) {}
                    }
                }
                
                if (output!= null ) {
                    GifSequenceWriter writer = new GifSequenceWriter(output, bi.getType(), 1000, true); 

                    resetProof();
                    
                    ArrayList<BufferedImage> frames = new ArrayList<>();
                    for (int i = 0; !Globals.proofsForRedo.isEmpty(); i++) {
                        frames.add(panel.getImage());
                        actionPerformed(new ActionEvent(this, 1, "redoStep"));
                    }
                    int maxWidth = 0;
                    int maxHeight = 0;
                    for (int i = 0; i < frames.size(); i++) {
                        if (frames.get(i).getWidth() > 0) {
                            maxWidth = frames.get(i).getWidth();
                        }
                        if (frames.get(i).getHeight() > 0) {
                            maxHeight = frames.get(i).getHeight();
                        }
                    }
                    
                    resetProof();
                    int i = 0;
                    while (!Globals.proofsForRedo.isEmpty()) {
                        bi = panel.getImage(maxWidth, maxHeight);
                        ImageIO.write(bi,"png",new File(i + ".png"));
                        writer.writeToSequence(panel.getImage(maxWidth, maxHeight));
                        actionPerformed(new ActionEvent(this, 1, "redoStep"));
                        i++;
                    }
                    
                    
                    bi = panel.getImage(maxWidth, maxHeight);
//                    System.out.println(Globals.assist.getProofArray().length);
                    writer.writeToSequence(bi);
                    writer.writeToSequence(bi);
                    writer.writeToSequence(bi);
                    writer.close();


                    output.close();
                }
                } catch (IOException except) {
                    JOptionPane.showMessageDialog(this, "Export to GIF failed!", "Export Error", JOptionPane.ERROR_MESSAGE);
                    except.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to export!", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("exportClipboardProof")) {
            if (Globals.assist != null) {
                Image bi = panel.getImage();
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                
                TransferableImage trans = new TransferableImage(bi);
                clip.setContents(trans, this);
                
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to export!", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("undoStep")) {
            if (!Globals.proofsForUndo.isEmpty()) {
                // Push to Redo
                if (Globals.assist != null) {
                    NDLine[] proofArray = Globals.assist.getProofArray();
                    NDLine[] tempArray = new NDLine[proofArray.length];
                    for (int i = 0; i < proofArray.length; i++) {
                        tempArray[i] = proofArray[i].clone();
                    }
                    Globals.proofsForRedo.push(tempArray);
                    Globals.goalsForRedo.push(Globals.currentGoalIndex);
                    Globals.resourcesForRedo.push(Globals.currentResourceIndex);
                    Globals.lineNumsForRedo.push(Globals.lineNum);
                    Globals.rulesUsedForRedo.push((HashSet<String>)Globals.rulesUsed.clone());
                    Globals.termsUsedForRedo.push((HashSet<String>)Globals.termsUsed.clone());
                }
                
                // Pop from Undo
                NDLine[] prior = (NDLine[])Globals.proofsForUndo.pop();
                Globals.assist = new ProofObject(prior);
                Globals.setArities();
                Globals.currentGoalIndex = (int)Globals.goalsForUndo.pop();
//                System.out.println("popped " + Globals.currentGoalIndex);
                Globals.currentResourceIndex = (int)Globals.resourcesForUndo.pop();
                Globals.lineNum = (int)Globals.lineNumsForUndo.pop();
//                System.out.println(Globals.rulesUsed.size());
//                Iterator fun = Globals.rulesUsed.iterator();
//            while(fun.hasNext()) {
////                System.out.println(fun.next());
//            }
                Globals.rulesUsed = (HashSet<String>)Globals.rulesUsedForUndo.pop();
                Globals.termsUsed = (HashSet<String>)Globals.termsUsedForUndo.pop();
//                System.out.println("popped");
                
                // Set up proof
                Globals.terms.empty();
                panel.setProofArray(Globals.assist.getProofArray());
                Globals.proofArray = Globals.assist.getProofArray();
                Globals.terms.processNDLineArray(Globals.proofArray);
                panel.printLines();
            }
            undoItem.setEnabled(!Globals.proofsForUndo.isEmpty());
            redoItem.setEnabled(!Globals.proofsForRedo.isEmpty());
            status.setArityButtonToolTip();
//            System.out.println("_____________________________________");
//            Iterator fun = Globals.rulesUsed.iterator();
//            while(fun.hasNext()) {
////                System.out.println(fun.next());
//            }
            
            checkTitle();
        } else if (source.equals("redoStep")) {
            if (!Globals.proofsForRedo.isEmpty()) {
                // Push to Undo
                if (Globals.assist != null) {
                    NDLine[] proofArray = Globals.assist.getProofArray();
                    NDLine[] tempArray = new NDLine[proofArray.length];
                    for (int i = 0; i < proofArray.length; i++) {
                        tempArray[i] = proofArray[i].clone();
                    }
                    Globals.proofsForUndo.push(proofArray);
                    proofArray = tempArray;
                    Globals.goalsForUndo.push(Globals.currentGoalIndex);
                    Globals.resourcesForUndo.push(Globals.currentResourceIndex);
                    Globals.lineNumsForUndo.push(Globals.lineNum);
                    Globals.rulesUsedForUndo.push((HashSet<String>)Globals.rulesUsed.clone());
                    Globals.termsUsedForUndo.push((HashSet<String>)Globals.termsUsed.clone());
                }
                
                // Pop from Redo
                NDLine[] prior = (NDLine[])Globals.proofsForRedo.pop();
                Globals.assist = new ProofObject(prior);
                Globals.setArities();
                Globals.currentGoalIndex = (int)Globals.goalsForRedo.pop();
                Globals.currentResourceIndex = (int)Globals.resourcesForRedo.pop();
                Globals.lineNum = (int)Globals.lineNumsForRedo.pop();
                Globals.rulesUsed = Globals.rulesUsedForRedo.pop();
                Globals.termsUsed = Globals.termsUsedForRedo.pop();
                
                // Set up proof
                Globals.terms.empty();
                panel.setProofArray(Globals.assist.getProofArray());
                Globals.proofArray = Globals.assist.getProofArray();
                Globals.terms.processNDLineArray(Globals.proofArray);
                panel.printLines();
            }
            undoItem.setEnabled(!Globals.proofsForUndo.isEmpty());
            redoItem.setEnabled(!Globals.proofsForRedo.isEmpty());
            status.setArityButtonToolTip();
            checkTitle();
        } else if (source.equals("aboutApp")) {
            JOptionPane.showMessageDialog(this, "Natural Deduction Planner\n"
                    + "Version " + versionNum + "\n" + date + "\n\n" + "Author: Declan Thompson", "About", JOptionPane.INFORMATION_MESSAGE);
            if (System.currentTimeMillis()-funTime < 2000) {
                (new Thread(new Test())).start();
            } else {
                funTime = System.currentTimeMillis();
            }
            if (Globals.assist != null) {
                Globals.assist.printProofArray();
                Globals.assist.printProofArrayLines();
            }
        } else if (source.equals("debugApp")) {
            if (Globals.assist != null) {
                Globals.assist.printProofArray();
                Globals.assist.printProofArrayLines();
            }
        } else if (source.equals("theZoom")) {
            if (panel != null) {
                float zoom = (float)(MyOptionPane.showJSliderDialog("Choose your zoom level", "Zoom", 50, 550, (int)(panel.getZoomFactor()*100)))/100;
                panel.setZoomFactor(zoom);
                panel.printLines();
                Globals.zoomFactor = zoom;
            } else {
                Globals.zoomFactor = (float)(MyOptionPane.showJSliderDialog("Choose your zoom level", "Zoom", 50, 550, (int)(Globals.zoomFactor*100)))/100;
            }
        } else if (source.equals("increaseZoom")) {
            if (panel != null) {
                float zoom = Globals.zoomFactor + (float)0.2;
                panel.setZoomFactor(zoom);
                panel.printLines();
                Globals.zoomFactor = zoom;
            } else {
                Globals.zoomFactor = (float)(MyOptionPane.showJSliderDialog("Choose your zoom level", "Zoom", 50, 550, (int)(Globals.zoomFactor*100)))/100;
            }
        } else if (source.equals("decreaseZoom")) {
            if (panel != null) {
                float zoom = Globals.zoomFactor - (float)0.2;
                if (zoom < (float)0.05) {
                    zoom = (float)0.05;
                }
                panel.setZoomFactor(zoom);
                panel.printLines();
                Globals.zoomFactor = zoom;
            } else {
                Globals.zoomFactor = (float)(MyOptionPane.showJSliderDialog("Choose your zoom level", "Zoom", 50, 550, (int)(Globals.zoomFactor*100)))/100;
            }
        } else if (source.equals("cutALine")) {
            if (panel != null) {
                if (Globals.currentGoalIndex < 0) {
                    JOptionPane.showMessageDialog(this, "Please select a current goal first!", "Cut Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String newLine = MyOptionPane.showFriendlyLineInputDialog();
                    if (newLine != null && lineInputIsGood(newLine)) {
                        newLine = parseInput(newLine);
                        panel.cutALine(newLine);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "There is no proof to cut in!", "Cut Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals("saveProof")) {
            int returnVal = myFileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Charset charset = Charset.forName("UTF-8");
                boolean makeItSo = true;
                if (myFileChooser.getSelectedFile().exists()) {
                    int result = JOptionPane.showConfirmDialog(this, "File already exists!\nOverwrite?", "Save Proof", JOptionPane.YES_NO_OPTION);
                    if (result != JOptionPane.YES_OPTION) {
                        makeItSo = false;
                    }
                }
                if (myFileChooser.getFileFilter().accept(myFileChooser.getSelectedFile()) && makeItSo) {
                    try (BufferedWriter writer = Files.newBufferedWriter(myFileChooser.getSelectedFile().toPath(), charset)) {
                        saveProof(writer);
                        writer.close();
                    } catch (IOException x) {
                        System.err.format("IOException: %s%n", x);
                        x.printStackTrace();
                    }
                } else if (makeItSo) {
                    String extension = ((FileNameExtensionFilter)myFileChooser.getFileFilter()).getExtensions()[0];
                    try (BufferedWriter writer = Files.newBufferedWriter((new File (myFileChooser.getSelectedFile() + "." + extension)).toPath(), charset)) {
                        saveProof(writer);
                        writer.close();
                    } catch (IOException x) {
                        System.err.format("IOException: %s%n", x);
                        x.printStackTrace();
                    }
                }
            }
        } else if (source.equals("openProof")) {
            int returnVal = myFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Charset charset = Charset.forName("UTF-8");
                try (BufferedReader reader = Files.newBufferedReader(myFileChooser.getSelectedFile().toPath(), charset)) {
                    openProof(reader);
                    reader.close();
                    this.setTitle(myFileChooser.getSelectedFile().getName());
                    if (myFileChooser.getSelectedFile().getName().substring(myFileChooser.getSelectedFile().getName().lastIndexOf(".") + 1).equals("ndu")) {
                        Globals.editable = false;
                        
                        // Push to Redo
                        if (Globals.assist != null) {
                            NDLine[] proofArray = Globals.assist.getProofArray();
                            NDLine[] tempArray = new NDLine[proofArray.length];
                            for (int i = 0; i < proofArray.length; i++) {
                                tempArray[i] = proofArray[i].clone();
                            }
                            Globals.proofsForRedo.push(tempArray);
                            Globals.goalsForRedo.push(Globals.currentGoalIndex);
                            Globals.resourcesForRedo.push(Globals.currentResourceIndex);
                            Globals.lineNumsForRedo.push(Globals.lineNum);
                            Globals.rulesUsedForRedo.push((HashSet<String>)Globals.rulesUsed.clone());
                            Globals.termsUsedForRedo.push((HashSet<String>)Globals.termsUsed.clone());
                        }
                        
                        // Push all undo to redo
                        while (!Globals.proofsForUndo.isEmpty()) {
                            Globals.proofsForRedo.push(Globals.proofsForUndo.pop());
                            Globals.goalsForRedo.push(Globals.goalsForUndo.pop());
                            Globals.resourcesForRedo.push(Globals.resourcesForUndo.pop());
                            Globals.lineNumsForRedo.push(Globals.lineNumsForUndo.pop());
                            Globals.rulesUsedForRedo.push(Globals.rulesUsedForUndo.pop());
                            Globals.termsUsedForRedo.push(Globals.termsUsedForUndo.pop());
                        }
                        
                        
                        // Pop from Redo
                        NDLine[] prior = (NDLine[])Globals.proofsForRedo.pop();
                        Globals.assist = new ProofObject(prior);
                        Globals.setArities();
                        Globals.currentGoalIndex = (int)Globals.goalsForRedo.pop();
                        Globals.currentResourceIndex = (int)Globals.resourcesForRedo.pop();
                        Globals.lineNum = (int)Globals.lineNumsForRedo.pop();
                        Globals.rulesUsed = Globals.rulesUsedForRedo.pop();
                        Globals.termsUsed = Globals.termsUsedForRedo.pop();

                        // Set up proof
                        Globals.terms.empty();
                        panel.setProofArray(Globals.assist.getProofArray());
                        Globals.proofArray = Globals.assist.getProofArray();
                        Globals.terms.processNDLineArray(Globals.proofArray);
                        panel.printLines();
                        
                        setUndoable(!Globals.proofsForUndo.isEmpty());
                        setRedoable(!Globals.proofsForRedo.isEmpty());
                        
                        JPanel controlPanel = new JPanel();
                        JButton nextButton = new JButton("Step forward");
                        nextButton.addActionListener(this);
                        nextButton.setActionCommand("redoStep");
                        JButton backButton = new JButton("Step back");
                        backButton.setSize(nextButton.getSize());
                        backButton.addActionListener(this);
                        backButton.setActionCommand("undoStep");
                        controlPanel.add(backButton);
                        controlPanel.add(nextButton);
                        
                        getContentPane().add(controlPanel, BorderLayout.NORTH);
                    } else {
                        Globals.editable = true;
                    }
                } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(this, "File is corrupted!", "Open Error", JOptionPane.ERROR_MESSAGE);
                    x.printStackTrace();
                }
            }
            
        } else if (source.equals("showRulePalette")) {
            if (Globals.rulePal != null && Globals.rulePal.isVisible()) {
                Globals.rulePal.dispose();
            } else {
                if (status != null) {
                    Globals.rulePal = new RulePalette(frame, status.getRuleSystemButton());
                } else {
                    Globals.rulePal = new RulePalette(frame, null);
                }
                Globals.rulePal.setVisible(true);
            }
        }
    }
    
    private void resetProof() {
        // Push to Redo
                    if (Globals.assist != null) {
                        NDLine[] proofArray = Globals.assist.getProofArray();
                        NDLine[] tempArray = new NDLine[proofArray.length];
                        for (int i = 0; i < proofArray.length; i++) {
                            tempArray[i] = proofArray[i].clone();
                        }
                        Globals.proofsForRedo.push(tempArray);
                        Globals.goalsForRedo.push(Globals.currentGoalIndex);
                        Globals.resourcesForRedo.push(Globals.currentResourceIndex);
                        Globals.lineNumsForRedo.push(Globals.lineNum);
                        Globals.rulesUsedForRedo.push((HashSet<String>)Globals.rulesUsed.clone());
                        Globals.termsUsedForRedo.push((HashSet<String>)Globals.termsUsed.clone());
                    }

                    // Push all undo to redo
                    while (!Globals.proofsForUndo.isEmpty()) {
                        Globals.proofsForRedo.push(Globals.proofsForUndo.pop());
                        Globals.goalsForRedo.push(Globals.goalsForUndo.pop());
                        Globals.resourcesForRedo.push(Globals.resourcesForUndo.pop());
                        Globals.lineNumsForRedo.push(Globals.lineNumsForUndo.pop());
                        Globals.rulesUsedForRedo.push(Globals.rulesUsedForUndo.pop());
                        Globals.termsUsedForRedo.push(Globals.termsUsedForUndo.pop());
                    }


                    // Pop from Redo
                    NDLine[] prior = (NDLine[])Globals.proofsForRedo.pop();
                    Globals.assist = new ProofObject(prior);
                    Globals.setArities();
                    Globals.currentGoalIndex = (int)Globals.goalsForRedo.pop();
                    Globals.currentResourceIndex = (int)Globals.resourcesForRedo.pop();
                    Globals.lineNum = (int)Globals.lineNumsForRedo.pop();
                    Globals.rulesUsed = Globals.rulesUsedForRedo.pop();
                    Globals.termsUsed = Globals.termsUsedForRedo.pop();

                    // Set up proof
                    Globals.terms.empty();
                    panel.setProofArray(Globals.assist.getProofArray());
                    Globals.proofArray = Globals.assist.getProofArray();
                    Globals.terms.processNDLineArray(Globals.proofArray);
                    panel.printLines();
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == greyScopesItem) {
            Globals.outOfScopeIsGrey = greyScopesItem.getState();
            if (panel != null) {
                panel.printLines();
            }
        } else if (e.getSource() == reverseConIntroItem) {
            Globals.reverse2PremIntro = reverseConIntroItem.getState();
        } else if (e.getSource() == useNeqItem) {
            Globals.useNeq = useNeqItem.getState();
            if (Globals.proofArray != null) {
                for (int i = 0; i < Globals.proofArray.length; i++) {
                    Globals.proofArray[i].reparseLine();
                }
            }
            if (panel != null) {
                panel.printLines();
            }
        } else if (e.getSource() == useBrackets) {
            Globals.showBrackets = useBrackets.getState();
        }
        
    }
    
    public void stateChanged(ChangeEvent e) {
        
    }
    
    private void resetStacks() {
        while (!Globals.proofsForUndo.isEmpty()) {
            Globals.proofsForUndo.pop();
            Globals.goalsForUndo.pop();
            Globals.resourcesForUndo.pop();
            Globals.lineNumsForUndo.pop();
            Globals.rulesUsedForUndo.pop();
            Globals.termsUsedForUndo.pop();
        }
        while (!Globals.proofsForRedo.isEmpty()) {
            Globals.proofsForRedo.pop();
            Globals.goalsForRedo.pop();
            Globals.resourcesForRedo.pop();
            Globals.lineNumsForRedo.pop();
            Globals.rulesUsedForRedo.pop();
            Globals.termsUsedForRedo.pop();
        }
        Globals.rulesUsed.clear();
        Globals.termsUsed.clear();
        Globals.terms.empty();
    }
    
    private void saveProof(BufferedWriter writer) throws IOException{
        if (Globals.assist != null) {
            //Settings
            writer.write("%%Settings");
            writer.newLine();
            writer.write("\u001f");
            writer.write("" + Globals.runMagicModeWithQa);
            writer.write("\u001f");
            writer.write(Globals.aritiyS);
            writer.write("\u001f");
            writer.write(Globals.newProofBoxPrems);
            writer.write("\u001f");
            writer.write(Globals.newProofBoxConc);
            writer.write("\u001f");
            writer.newLine();
            
            // Current proof
            writer.write("%%Current");
            writer.newLine();
            
            // Stuff like undo
            writer.write("" + Globals.currentGoalIndex);
            writer.write("\u001f");
            writer.write("" + Globals.currentResourceIndex);
            writer.write("\u001f");
            writer.write("" + Globals.lineNum);
            Iterator<String> terate = Globals.rulesUsed.iterator();
            while (terate.hasNext()) {
                String currentRule = terate.next();
                writer.write("\u001f");
                writer.write(currentRule);
            }
            writer.write("\u001e");
            terate = Globals.termsUsed.iterator();
            while(terate.hasNext()){
                writer.write("\u001f");
                writer.write(terate.next());
            }
            writer.newLine();
            
            writer.write("%Ruleset");
            writer.newLine();
            Iterator<String> terat = Globals.allowedRules.keySet().iterator();
            while(terat.hasNext()) {
                String currentRule = terat.next();
                if (Globals.allowedRules.get(currentRule)){
                    writer.write("\u001f");
                    writer.write(currentRule);
                }
            }
            if (!status.getRuleSystem().equals("Custom")) {
                writer.write("\u001e" + status.getRuleSystem());
            }
            writer.newLine();
            
            
            // Proof Array
            writer.write("%ProofArray");
            writer.newLine();
            
            
            NDLine[] proofArray = Globals.assist.getProofArray();
            for (int i = 0; i < proofArray.length; i++) {
                writer.write("" + proofArray[i].getLineNum());
                writer.write("\u001f");
                writer.write(proofArray[i].getLine());
                writer.write("\u001f");
                writer.write("" + proofArray[i].getType());
                writer.write("\u001f");
                writer.write(proofArray[i].getJustification().getJava());
                writer.write("\u001f");
                writer.write(proofArray[i].getJustification().getTeX());
                writer.write("\u001f");
                writer.write(proofArray[i].getContext());
                writer.write("\u001f");
                writer.write(proofArray[i].getSpecialNum());
                writer.newLine();
            }
            
            Stack<NDLine[]> proofsForUndo = new Stack<>();
            Stack<Integer> goalsForUndo = new Stack<>();
            Stack<Integer> resourcesForUndo = new Stack<>();
            Stack<Integer> lineNumsForUndo = new Stack<>();
            Stack<HashSet<String>> rulesUsedForUndo = new Stack<>();
            Stack<HashSet<String>> termsUsedForUndo = new Stack<>();
            
            if (Globals.proofsForUndo.clone() instanceof Stack) {
                proofsForUndo = (Stack<NDLine[]>)Globals.proofsForUndo.clone();
            }
            goalsForUndo = (Stack<Integer>)Globals.goalsForUndo.clone();
            resourcesForUndo = (Stack<Integer>)Globals.resourcesForUndo.clone();
            lineNumsForUndo = (Stack<Integer>)Globals.lineNumsForUndo.clone();
            rulesUsedForUndo = (Stack<HashSet<String>>)Globals.rulesUsedForUndo.clone();
            termsUsedForUndo = (Stack<HashSet<String>>)Globals.termsUsedForUndo.clone();
            
            while (!proofsForUndo.isEmpty()) {
                // Undo proof
                writer.write("%%Undo");
                writer.newLine();

                // Stuff like undo
                writer.write("" + goalsForUndo.pop());
                writer.write("\u001f");
                writer.write("" + resourcesForUndo.pop());
                writer.write("\u001f");
                writer.write("" + lineNumsForUndo.pop());
                Iterator<String> terator = rulesUsedForUndo.pop().iterator();
                while (terator.hasNext()) {
                    String currentRule = terator.next();
                    writer.write("\u001f");
                    writer.write(currentRule);
                }
                writer.write("\u001e");
                Iterator<String> teratoo = termsUsedForUndo.pop().iterator();
                while(teratoo.hasNext()) {
                    writer.write("\u001f");
                    writer.write(teratoo.next());
                }
                writer.newLine();

                // Proof Array
                writer.write("%ProofArray");
                writer.newLine();

                NDLine[] tempArray = proofsForUndo.pop();
                for (int i = 0; i < tempArray.length; i++) {
                    writer.write("" + tempArray[i].getLineNum());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getLine());
                    writer.write("\u001f");
                    writer.write("" + tempArray[i].getType());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getJustification().getJava());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getJustification().getTeX());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getContext());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getSpecialNum());
                    writer.newLine();
                }
            }
            
            Stack<NDLine[]> proofsForRedo = (Stack<NDLine[]>)Globals.proofsForRedo.clone();
            Stack<Integer> goalsForRedo = (Stack<Integer>)Globals.goalsForRedo.clone();
            Stack<Integer> resourcesForRedo = (Stack<Integer>)Globals.resourcesForRedo.clone();
            Stack<Integer> lineNumsForRedo = (Stack<Integer>)Globals.lineNumsForRedo.clone();
            Stack<HashSet<String>> rulesUsedForRedo = (Stack<HashSet<String>>)Globals.rulesUsedForRedo.clone();
            Stack<HashSet<String>> termsUsedForRedo = (Stack<HashSet<String>>)Globals.termsUsedForRedo.clone();
            
            while (!proofsForRedo.isEmpty()) {
                // Undo proof
                writer.write("%%Redo");
                writer.newLine();

                // Stuff like undo
                writer.write("" + goalsForRedo.pop());
                writer.write("\u001f");
                writer.write("" + resourcesForRedo.pop());
                writer.write("\u001f");
                writer.write("" + lineNumsForRedo.pop());
                Iterator<String> terator = rulesUsedForRedo.pop().iterator();
                while (terator.hasNext()) {
                    String currentRule = terator.next();
                    writer.write("\u001f");
                    writer.write(currentRule);
                }
                writer.write("\u001e");
                Iterator<String> teratoo = termsUsedForRedo.pop().iterator();
                while(teratoo.hasNext()) {
                    writer.write("\u001f");
                    writer.write(teratoo.next());
                }
                writer.newLine();

                // Proof Array
                writer.write("%ProofArray");
                writer.newLine();

                NDLine[] tempArray = proofsForRedo.pop();
                for (int i = 0; i < tempArray.length; i++) {
                    writer.write("" + tempArray[i].getLineNum());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getLine());
                    writer.write("\u001f");
                    writer.write("" + tempArray[i].getType());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getJustification().getJava());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getJustification().getTeX());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getContext());
                    writer.write("\u001f");
                    writer.write(tempArray[i].getSpecialNum());
                    writer.newLine();
                }
            }
            writer.write("%%End");
        }
    }
    
    private void openProof(BufferedReader reader) throws IOException {
        Globals.terms.empty();
        String premisesForBox = "";
        String concForBox = "";
        
        String line = null;
        String setUpFor = "";
        boolean inArray = false;
        boolean inRuleSet = false;
        ArrayList<NDLine> proofArrayList = new ArrayList<>();
        ArrayList<NDLine> proofArrayListUndo = new ArrayList<>();
        ArrayList<NDLine> proofArrayListRedo = new ArrayList<>();
        HashSet<String> rulesUsed = new HashSet<>();
        HashSet<String> termsUsed = new HashSet<>();
        HashSet<String> rulesForXDo = new HashSet<>();
        HashSet<String> termsForXDo = new HashSet<>();
        HashSet<String> ruleSet = new HashSet<>();
        
        Stack<NDLine[]> proofsForRedo = new Stack<>();
        Stack<Integer> goalsForRedo = new Stack<>();
        Stack<Integer> resourcesForRedo = new Stack<>();
        Stack<Integer> lineNumsForRedo = new Stack<>();
        Stack<HashSet<String>> rulesUsedForRedo = new Stack<>();
        Stack<HashSet<String>> termsUsedForRedo = new Stack<>();
        
        Stack<NDLine[]> proofsForUndo = new Stack<>();
        Stack<Integer> goalsForUndo = new Stack<>();
        Stack<Integer> resourcesForUndo = new Stack<>();
        Stack<Integer> lineNumsForUndo = new Stack<>();
        Stack<HashSet<String>> rulesUsedForUndo = new Stack<>();
        Stack<HashSet<String>> termsUsedForUndo = new Stack<>();
        
        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
            if (line.equals("")) {
                
            } else if (line.length() > 2 && line.substring(0,2).equals("%%")) {
                if (setUpFor.equals("Undo")) {
                    proofsForUndo.push(proofArrayListUndo.toArray(new NDLine[1]));
                    proofArrayListUndo.clear();
                    rulesUsedForUndo.push((HashSet<String>)rulesForXDo.clone());
                    rulesForXDo.clear();
                    termsUsedForUndo.push((HashSet<String>)termsForXDo.clone());
                    termsForXDo.clear();
                } else if (setUpFor.equals("Redo")) {
                    proofsForRedo.push(proofArrayListRedo.toArray(new NDLine[1]));
                    proofArrayListRedo.clear();
                    rulesUsedForRedo.push((HashSet<String>)rulesForXDo.clone());
                    rulesForXDo.clear();
                    termsUsedForRedo.push((HashSet<String>)termsForXDo.clone());
                    termsForXDo.clear();
                }
                setUpFor = line.substring(2);
                inArray = false;
            } else if (setUpFor.equals("Settings")) {
//                Globals.setProofSystem(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                line = line.substring(line.indexOf("\u001f") + 1);
                Globals.runMagicModeWithQa = Boolean.parseBoolean(line.substring(0,line.indexOf("\u001f")));
                line = line.substring(line.indexOf("\u001f") + 1);
                Globals.aritiyS = line.substring(0,line.indexOf("\u001f"));
                line = line.substring(line.indexOf("\u001f") + 1);
                if (line.contains("\u001f")) {
                    Globals.newProofBoxPrems = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    Globals.newProofBoxConc = line.substring(0,line.indexOf("\u001f"));
                }
                Globals.setArities();
            } else if (setUpFor.equals("Current")) {
                if (line.length() > 1 && line.substring(0,1).equals("%")) {
                    if (line.equals("%ProofArray")) {
                        inArray = true;
                    } else if (line.equals("%Ruleset")) {
                        inRuleSet = true;
                    }
                } else if (inRuleSet) {
                    line = line.substring(1);
                    while (line.contains("\u001f")) {
                        ruleSet.add(line.substring(0, line.indexOf("\u001f")));
//                        System.out.println("==================== Added " + line.substring(0, line.indexOf("\u001f")) + "to ruleset");
                        line = line.substring(line.indexOf("\u001f") + 1);
                    }
                    
                    if (line.contains("\u001e")) {
                           ruleSet.add(line.substring(0,line.indexOf("\u001e")));
                           line = line.substring(line.indexOf("\u001e")+1);
                           boolean unique = true;
                           for (int i = 0; i < Globals.listOfSystems.size() && unique; i++) {
                               if (Globals.listOfSystems.get(i).getName().toString().equals(line)) {
                                   if (!(Globals.listOfSystems.get(i).containsAll(ruleSet) && ruleSet.containsAll(Globals.listOfSystems.get(i))))
                                   unique = false;
                               }
                           }
                           if (unique) {
                               ProofSystem newSystem = new ProofSystem(line, ruleSet);
                               Globals.listOfSystems.add(newSystem);
                               Globals.setRulesAllows(newSystem);
                           } else {
                               String systemName = JOptionPane.showInputDialog(this, "The saved proof uses ruleset " + line + ".\n" + line + " already exists. You have three options."
                                       + "\n- Input a new name"+ "\n" + "- Replace the current " + line + "\n- Cancel" , line, JOptionPane.QUESTION_MESSAGE);
                               if (systemName != null) {
                                   for (int i = 0; i < Globals.listOfSystems.size(); i++) {
                                       if (Globals.listOfSystems.get(i).getName().toString().equals(line)){
                                           Globals.listOfSystems.remove(i);
                                           i--;
                                       }
                                   }
                                   ProofSystem newSystem = new ProofSystem(systemName, ruleSet);
                                   Globals.listOfSystems.add(newSystem);
                                   Globals.setRulesAllows(newSystem);
                               }
                           }
                       } else {
                        ruleSet.add(line);
                    }
                    inRuleSet = false;
                } else if (inArray) {
                    int lineNum = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String lineContents = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    int lineType = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String javaJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String texJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String context = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String specialLineNum = line;

                    NDLine out = new NDLine(lineContents, lineType, false);
                    out.setLineNum(lineNum);
                    NDJust just = new JustFromString(javaJust, texJust, 
                            ((lineNum < 0 && lineNum > -10) ? false : javaJust.equals("") ) );
                    out.setJustification(just);
                    out.setContext(context);
                    if (javaJust.equals("Prem")){
                        premisesForBox = premisesForBox + out.parseLine() + ", ";
                    }
                    if (!specialLineNum.equals("")) {
                        out.setSpecialLineNum(specialLineNum);
                        if (!specialLineNum.matches("Q\\d") &&
                                (!Globals.myFunLines.contains(lineContents)
                                || !Globals.myFunLineNums.contains(specialLineNum)
                                || Globals.myFunLines.indexOf(lineContents) != Globals.myFunLineNums.indexOf(specialLineNum))) {
                            Globals.myFunLines.add(lineContents);
                            Globals.myFunLineNums.add(specialLineNum);
                        }
                    }
                    Globals.terms.processLine(lineContents);
                    proofArrayList.add(out);
                } else {
                    Globals.currentGoalIndex = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    Globals.currentResourceIndex = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    if (line.contains("\u001f") && line.indexOf("\u001f") < line.indexOf("\u001e")) {
                        Globals.lineNum = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    } else {
                        Globals.lineNum = Integer.parseInt(line.substring(0, line.indexOf("\u001e")));
                    }
                    while (line.length() > 0 && line.charAt(0) == ('\u001f')) {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            rulesUsed.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            rulesUsed.add(line.substring(0,line.indexOf("\u001e")));
                        }
                    }
                    line = line.substring(line.indexOf("\u001e") + 1);
                    while (line.length() > 0 && line.charAt(0) == ('\u001f')) {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            termsUsed.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            termsUsed.add(line);
                        }
                    }
                }
            } else if (setUpFor.equals("Undo")) {
                if (line.length() > 1 && line.substring(0,1).equals("%")) {
                    if (line.equals("%ProofArray")) {
                        inArray = true;
                    }
                } else if (inArray) {
                    int lineNum = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String lineContents = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    int lineType = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String javaJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String texJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String context = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String specialLineNum = line;

                    NDLine out = new NDLine(lineContents, lineType, false);
                    out.setLineNum(lineNum);
                    out.setJustification(getJust(texJust, javaJust));
                    out.setContext(context);
                    if (!specialLineNum.equals("")) {
                        out.setSpecialLineNum(specialLineNum);
                    }
                    Globals.terms.processLine(lineContents);
                    proofArrayListUndo.add(out);
                } else {
                    goalsForUndo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    resourcesForUndo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    if (line.contains("\u001f") && line.indexOf("\u001f") < line.indexOf("\u001e")) {
                        lineNumsForUndo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    } else {
                        lineNumsForUndo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001e"))));
                    }
                    while (line.length() > 0 && line.charAt(0) == '\u001f') {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            rulesForXDo.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            rulesForXDo.add(line.substring(0, line.indexOf("\u001e")));
                        }
                    }
                    line = line.substring(line.indexOf("\u001e")+1);
                    while (line.length() > 0 && line.charAt(0) == '\u001f') {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            termsForXDo.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            termsForXDo.add(line);
                        }
                    }
                }
            } else if (setUpFor.equals("Redo")) {
                if (line.length() > 1 && line.substring(0,1).equals("%")) {
                    if (line.equals("%ProofArray")) {
                        inArray = true;
                    }
                } else if (inArray) {
                    int lineNum = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String lineContents = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    int lineType = Integer.parseInt(line.substring(0,line.indexOf("\u001f")));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String javaJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String texJust = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String context = line.substring(0,line.indexOf("\u001f"));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    String specialLineNum = line;

                    NDLine out = new NDLine(lineContents, lineType, false);
                    out.setLineNum(lineNum);
                    out.setJustification(new JustFromString(javaJust, texJust, javaJust.equals("")));
                    out.setContext(context);
                    if (!specialLineNum.equals("")) {
                        out.setSpecialLineNum(specialLineNum);
                    }
                    Globals.terms.processLine(lineContents);
                    proofArrayListRedo.add(out);
                } else {
                    goalsForRedo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    resourcesForRedo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    line = line.substring(line.indexOf("\u001f") + 1);
                    if (line.contains("\u001f") && line.indexOf("\u001f") < line.indexOf("\u001e")) {
                        lineNumsForRedo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001f"))));
                    } else {
                        lineNumsForRedo.push(Integer.parseInt(line.substring(0,line.indexOf("\u001e"))));
                    }
                    while (line.length() > 0 && line.charAt(0) == '\u001f') {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            rulesForXDo.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            rulesForXDo.add(line.substring(0, line.indexOf("\u001e")));
                        }
                    }
                    line = line.substring(line.indexOf("\u001e")+1);
                    while (line.length() > 0 && line.charAt(0) == '\u001f') {
                        line = line.substring(line.indexOf("\u001f") + 1);
                        if (line.contains("\u001f")) {
                            termsForXDo.add(line.substring(0,line.indexOf("\u001f")));
                        } else {
                            termsForXDo.add(line);
                        }
                    }
                }
            }
        }
        
        
        Globals.clearUndo();
        while (!proofsForUndo.isEmpty()) {
            Globals.proofsForUndo.push(proofsForUndo.pop());
            Globals.goalsForUndo.push(goalsForUndo.pop());
            Globals.resourcesForUndo.push(resourcesForUndo.pop());
            Globals.lineNumsForUndo.push(lineNumsForUndo.pop());
            Globals.rulesUsedForUndo.push(rulesUsedForUndo.pop());
            Globals.termsUsedForUndo.push(termsUsedForUndo.pop());
        }
        setUndoable(!Globals.proofsForUndo.isEmpty());
        
        
        Globals.clearRedo();
        while (!proofsForRedo.isEmpty()) {
            Globals.proofsForRedo.push(proofsForRedo.pop());
            Globals.goalsForRedo.push(goalsForRedo.pop());
            Globals.resourcesForRedo.push(resourcesForRedo.pop());
            Globals.lineNumsForRedo.push(lineNumsForRedo.pop());
            Globals.rulesUsedForRedo.push(rulesUsedForRedo.pop());
            Globals.termsUsedForRedo.push(termsUsedForRedo.pop());
        }
        setRedoable(!Globals.proofsForRedo.isEmpty());
        
        
        NDLine[] proofArray = proofArrayList.toArray(new NDLine[1]);
        concForBox = proofArray[proofArray.length - 1].parseLine();
        
        Globals.assist = new ProofObject(proofArray);
//        Globals.assist.printProofArrayLines();
//        Globals.assist.printProofArray();
        Globals.setArities();
        Globals.createExtraLines();
//        System.out.println(Globals.extraLines.length);
        Globals.proofArray = Globals.assist.getProofArray();
        panel = new ProofPanel(Globals.proofArray);
        Globals.rulesUsed = rulesUsed;
        Globals.termsUsed = termsUsed;
        Globals.scrollpane = new JScrollPane(panel);
        Globals.scrollpane.setBorder(null);
        Globals.scrollpane.getVerticalScrollBar().setUnitIncrement(scrollspeed);
        getContentPane().removeAll();
        getContentPane().add(Globals.scrollpane);
        getContentPane().add(status, BorderLayout.SOUTH);
        status.updateRuleSystem();
        status.setArityButtonToolTip();
        status.setDogsBodyText("");
        
        if (!premisesForBox.equals("")){
            Globals.newProofBoxPrems = premisesForBox.substring(0, premisesForBox.lastIndexOf(", "));
        } else {
            Globals.newProofBoxPrems = "";
        }
        Globals.newProofBoxConc = proofArray[proofArray.length-1].parseLine();
        checkTitle();
        
        revalidate();
    }
    
    private NDJust getJust(String texJust, String javaJust) {
        return new JustFromString(javaJust, texJust, javaJust.equals(""));
    }
    
    private long funTime = 0;

    @Override
    public void lostOwnership(Clipboard clpbrd, Transferable t) {
        
    }
}
