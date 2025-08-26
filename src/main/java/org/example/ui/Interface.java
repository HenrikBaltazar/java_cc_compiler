package org.example.ui;
import org.example.Actions.FileManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Interface extends JFrame {
    private static final int W = 1920, H = 1080;
    private TextInput textInput;
    private TextOutput textOutput;
    private FileManager fileManager;
    private ToolBar toolBar;
    private String windowTitle = "Compilador";
    public Interface(){
        fileManager = new FileManager();
        textOutput = new TextOutput();
        textInput = new TextInput(this);
        toolBar = new ToolBar(this);
        setMinimumSize(new Dimension(W,H));
        setTitle(windowTitle);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int action = checkExit();
                if (action == EXIT_ON_CLOSE) {
                    dispose();
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                textInput, textOutput
        );
        splitPane.setDividerLocation(1200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);

        setJMenuBar(new WindowBar(this));
        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        getContentPane().add(splitPane, BorderLayout.CENTER);

       }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
        setTitle(windowTitle);
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    private int checkExit(){
        if(!fileManager.isFileSaved()){
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja salvar o arquivo antes de sair?",
                    "Confirmar saída",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION){
                int file = fileManager.saveFile(this);
                if( file == 1){
                    return DO_NOTHING_ON_CLOSE;
                }
            }
        }
            return EXIT_ON_CLOSE;
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public TextInput getTextInput() {
        return textInput;
    }

    public TextOutput getTextOutput() {
        return textOutput;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}
