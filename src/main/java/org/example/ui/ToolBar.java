package org.example.ui;

import org.example.Actions.FileManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToolBar extends JToolBar{
    Window parent = SwingUtilities.getWindowAncestor(this);
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

    public ToolBar(TextInput textInput, JTextArea textOutput, FileManager fileManager, Interface classParent) {
        setName("ToolBar");

        jButtonNewFile = new JButton(ICON_NEW);
        jButtonNewFile.setToolTipText("Novo arquivo");

        jButtonOpenFile = new JButton(ICON_OPEN);
        jButtonOpenFile.setToolTipText("Abrir arquivo");

        jButtonSaveFile = new JButton(ICON_SAVE);
        jButtonSaveFile.setToolTipText("Salvar arquivo");

        jButtonCutText = new JButton(ICON_CUT);
        jButtonCutText.setToolTipText("Recortar");

        jButtonCopyText = new JButton(ICON_COPY);
        jButtonCopyText.setToolTipText("Copiar");

        jButtonPasteText = new JButton(ICON_PASTE);
        jButtonPasteText.setToolTipText("Colar");

        jButtonBuildCode = new JButton(ICON_BUILD);
        jButtonBuildCode.setToolTipText("Compilar");

        jButtonRunCode = new JButton(ICON_RUN);
        jButtonRunCode.setToolTipText("Executar");

        jButtonHelp = new JButton(ICON_HELP);
        jButtonHelp.setToolTipText("Ajuda");


        jButtonNewFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile(textInput, classParent, fileManager);
            }
        });

        jButtonOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile(fileManager, textInput, classParent);
            }
        });

        jButtonSaveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(fileManager,parent,classParent,textInput);
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

        jButtonHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                help(textInput.getTextArea().getText(), textOutput);
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

    private void newFile(TextInput textInput, Interface classParent, FileManager fileManager) {
        if(!fileManager.isFileSaved()){
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "O arquivo ainda não foi salvo, deseja salvar o arquivo?",
                    "Confirmar novo arquivo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION){
                int file = fileManager.saveFile(this,textInput.getText(),classParent);
                if( file != 1){
                    newFile(textInput, classParent, fileManager);
                }
            }
        }
        fileManager.setFileSaved(true,classParent);
        fileManager.setFilePath(null);
        textInput.setInputText("");
        textInput.setRow(0);
        textInput.setCol(0);
        classParent.setWindowTitle("Compilador");

    }

    private void openFile(FileManager fileManager, TextInput textInput, Interface classParent) {
        File selectedFile = fileManager.openFileChooser(this,classParent);
        String text = "";
        if (selectedFile != null) {
            try {
                text = Files.readString(selectedFile.toPath());
            }catch(Exception e) {
                System.out.println("Erro ao abrir arquivo");
            }

            textInput.setInputText(text);
        }
    }


    private void saveFile(FileManager fileManager, Component parent, Interface classParent, TextInput textInput) {
        int file = fileManager.saveFile(parent,textInput.getText(),classParent);
    }

    private void cutText(){
        JOptionPane.showMessageDialog(parent, "cut");
    }

    private void copyText(){
        JOptionPane.showMessageDialog(parent, "copy");
    }

    private void pasteText(){
        JOptionPane.showMessageDialog(parent, "paste");
    }

    private void buildCode(){
        JOptionPane.showMessageDialog(parent, "build");
    }

    private void runCode(){
        JOptionPane.showMessageDialog(parent, "run");
    }

    private void help(String input, JTextArea output){
        output.setText(input);
    }

}
