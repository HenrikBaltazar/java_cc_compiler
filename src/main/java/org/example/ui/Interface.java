package org.example.ui;
import java.awt.*;
import javax.swing.*;

public class Interface extends JFrame {
    private static final int W = 1920, H = 1080;
    private TextEditor textEditor;

    public Interface(){
        textEditor = new TextEditor();
        setMinimumSize(new Dimension(W,H));
        setTitle("Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setJMenuBar(new WindowBar());

        getContentPane().add(textEditor, BorderLayout.CENTER);
        getContentPane().add(new ToolBar(textEditor.getTextArea()), BorderLayout.PAGE_START);

    }

}
