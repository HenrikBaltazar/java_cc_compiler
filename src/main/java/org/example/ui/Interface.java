package org.example.ui;
import org.example.Actions.Build;
import org.example.Actions.FileManager;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Interface extends JFrame {
    private static final int W = 800, H = 600;
    private TextInput textInput;
    private TextOutput textOutput;
    private FileManager fileManager;
    private ToolBar toolBar;
    private String windowTitle = "Compilador";
    private Shortcut shortcut;
    public Build build;
    private JDialog jHelpDialog;
    private static String ajuda =
            "<html><body style='font-family: sans-serif;'>" +
                    "<h3>ATALHOS:</h3>" +
                    "CTRL+S: SALVAR<br>" +
                    "CTRL+SHIFT+A: ABRIR ARQUIVO<br>" +
                    "CTRL+N: NOVO ARQUIVO<br>" +
                    "</body></html>";
    public Interface(Build build) {
        this.build = build;
        fileManager = new FileManager(this);
        textOutput = new TextOutput();
        textInput = new TextInput(this);
        toolBar = new ToolBar(this);
        shortcut = new Shortcut();
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
        splitPane.setDividerLocation(950);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5);

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
        if(!fileManager.saveInSecondChance()){
            return EXIT_ON_CLOSE;
        }
        return DO_NOTHING_ON_CLOSE;
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

    public Shortcut getShortcut() {
        return shortcut;
    }

    public void openHelpWindow() {
        jHelpDialog = new JDialog(this, "Ajuda", true);
        jHelpDialog.setLocationRelativeTo(this);
        jHelpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jHelpDialog.setMinimumSize(new Dimension(W,H));
        jHelpDialog.setResizable(false);
        JLabel jHelpLabel = new JLabel(ajuda);
        jHelpLabel.setVisible(true);
        jHelpDialog.add(jHelpLabel, BorderLayout.PAGE_START);
        jHelpDialog.setVisible(true);
    }

}
