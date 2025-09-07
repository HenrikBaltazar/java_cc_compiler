package org.example.ui;

import org.example.Actions.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;

public class ToolBar extends JToolBar{
    private static final String resourcesPath = "src/main/resources/";
    private static final ImageIcon ICON_NEW = new ImageIcon(resourcesPath+"icon_new.png");
    private static final ImageIcon ICON_OPEN = new ImageIcon(resourcesPath+"icon_open.png");
    private static final ImageIcon ICON_SAVE = new ImageIcon(resourcesPath+"icon_save.png");
    private static final ImageIcon ICON_CUT = new ImageIcon(resourcesPath+"icon_cut.png");
    private static final ImageIcon ICON_COPY = new ImageIcon(resourcesPath+"icon_copy.png");
    private static final ImageIcon ICON_PASTE = new ImageIcon(resourcesPath+"icon_paste.png");
    private static final ImageIcon ICON_BUILD = new ImageIcon(resourcesPath+"icon_build.png");
    private static final ImageIcon ICON_RUN = new ImageIcon(resourcesPath+"icon_run.png");
    private static final ImageIcon ICON_HELP = new ImageIcon(resourcesPath+"icon_help.png");
    private static final ImageIcon ICON_CLEAR = new ImageIcon(resourcesPath+"icon_clear.png");
    private JButton jButtonNewFile,jButtonOpenFile,jButtonSaveFile,jButtonCutText,jButtonCopyText,jButtonPasteText,jButtonBuildCode,jButtonRunCode,jButtonClear, jButtonHelp;
    private Interface parent;
    public ToolBar(Interface parent) {
        setName("ToolBar");
        this.parent = parent;
        jButtonNewFile = new JButton(ICON_NEW);
        jButtonNewFile.setToolTipText("Novo arquivo (CTRL+N)");

        jButtonOpenFile = new JButton(ICON_OPEN);
        jButtonOpenFile.setToolTipText("Abrir arquivo (CTRL+SHIFT+A)");

        jButtonSaveFile = new JButton(ICON_SAVE);
        jButtonSaveFile.setToolTipText("Salvar arquivo (CTRL+S)");

        jButtonCutText = new JButton(ICON_CUT);
        jButtonCutText.setToolTipText("Recortar (CTRL+X)");

        jButtonCopyText = new JButton(ICON_COPY);
        jButtonCopyText.setToolTipText("Copiar (CTRL+C)");

        jButtonPasteText = new JButton(ICON_PASTE);
        jButtonPasteText.setToolTipText("Colar (CTRL+V)");

        jButtonBuildCode = new JButton(ICON_BUILD);
        jButtonBuildCode.setToolTipText("Compilar");

        jButtonRunCode = new JButton(ICON_RUN);
        jButtonRunCode.setToolTipText("Executar");

        jButtonClear = new JButton(ICON_CLEAR);
        jButtonClear.setToolTipText("Limpar saída");

        jButtonHelp = new JButton(ICON_HELP);
        jButtonHelp.setToolTipText("Ajuda");


        jButtonNewFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });

        jButtonOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        jButtonSaveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        jButtonCutText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cutText();
            }
        });

        jButtonCopyText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyText();
            }
        });

        jButtonPasteText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pasteText();
            }
        });

        jButtonBuildCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buildCode();
            }
        });

        jButtonRunCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runCode();
            }
        });

        jButtonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.getTextOutput().setText("");
            }
        });

        jButtonHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                help();
            }
        });


        add(jButtonNewFile);
        add(jButtonOpenFile);
        add(jButtonSaveFile);
        addSeparator();
        add(jButtonCutText);
        add(jButtonCopyText);
        add(jButtonPasteText);
        addSeparator();
        add(jButtonBuildCode);
        add(jButtonRunCode);
        add(jButtonClear);
        addSeparator();
        add(jButtonHelp);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                parent.getShortcut().Detect(parent,e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
    }

    public void newFile() {
        parent.getFileManager().saveInSecondChance();
        parent.getFileManager().setFileSaved(true);
        parent.getFileManager().setFilePath(null);
        parent.getTextInput().setInputText("");
        parent.getTextInput().setRow(0);
        parent.getTextInput().setCol(0);
        parent.setWindowTitle("Compilador");
    }

    public void openFile() {
        File selectedFile = parent.getFileManager().openFileChooser();
        String text = "";
        if (selectedFile != null) {
            try {
                text = Files.readString(selectedFile.toPath());
            }catch(Exception e) {
                System.out.println("Erro ao abrir arquivo");
            }

            parent.getTextInput().setInputText(text);
        }
    }


    public void saveFile() {
        int file = parent.getFileManager().saveFile();
    }

    public void cutText() {
        parent.getTextInput().getTextArea().cut();
    }

    public void copyText() {
        parent.getTextInput().getTextArea().copy();
    }

    public void pasteText() {
        parent.getTextInput().getTextArea().paste();
    }

    public void buildCode(){
        if(parent.getFileManager().saveInSecondChance()){
            parent.build.buildCode();
        }else{
            JOptionPane.showMessageDialog(parent, "Utilize o botão 'executar' para compilar sem salvar");
        }

    }

    public void runCode(){
        parent.build.buildCode();
    }

    public void help() {
        parent.openHelpWindow();
    }

}
