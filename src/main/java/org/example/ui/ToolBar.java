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
    private JButton jButtonNewFile,jButtonOpenFile,jButtonSaveFile,jButtonCutText,jButtonCopyText,jButtonPasteText,jButtonBuildCode,jButtonRunCode,jButtonHelp;

    public ToolBar(Interface parent) {
        setName("ToolBar");

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

        jButtonHelp = new JButton(ICON_HELP);
        jButtonHelp.setToolTipText("Ajuda");


        jButtonNewFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile(parent);
            }
        });

        jButtonOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile(parent);
            }
        });

        jButtonSaveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(parent);
            }
        });

        jButtonCutText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cutText(parent);
            }
        });

        jButtonCopyText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyText(parent);
            }
        });

        jButtonPasteText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pasteText(parent);
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

        jButtonHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                help(parent);
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
        addSeparator();
        add(jButtonHelp);
    }

    public void newFile(Interface parent) {
        if(!parent.getFileManager().isFileSaved()){
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "O arquivo ainda não foi salvo, deseja salvar o arquivo?",
                    "Confirmar novo arquivo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION){
                int file = parent.getFileManager().saveFile(parent);
                if( file != 1){
                    newFile(parent);
                }
            }
        }
        parent.getFileManager().setFileSaved(true,parent);
        parent.getFileManager().setFilePath(null);
        parent.getTextInput().setInputText("");
        parent.getTextInput().setRow(0);
        parent.getTextInput().setCol(0);
        parent.setWindowTitle("Compilador");

    }

    public void openFile(Interface parent) {
        File selectedFile = parent.getFileManager().openFileChooser(this,parent);
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


    public void saveFile(Interface parent) {
        int file = parent.getFileManager().saveFile(parent);
    }

    public void cutText(Interface parent) {
        parent.getTextInput().getTextArea().cut();
    }

    public void copyText(Interface parent) {
        parent.getTextInput().getTextArea().copy();
    }

    public void pasteText(Interface parent) {
        parent.getTextInput().getTextArea().paste();
    }

    public void buildCode(){
        //JOptionPane.showMessageDialog(parent, "build");
    }

    public void runCode(){
        //JOptionPane.showMessageDialog(parent, "run");
    }

    public void help(Interface parent) {
        JTextArea output = parent.getTextOutput().getTextArea();
        String input = parent.getTextInput().getText();
        output.setText(input);
    }

}
