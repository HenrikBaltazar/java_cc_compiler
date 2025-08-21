package org.example.ui;
import java.awt.*;
import javax.swing.*;

public class Interface extends JFrame {
    private static final int W = 1920, H = 1080;
    private static final Font FONT_WINDOWBAR = new Font("Arial", Font.PLAIN, 16);
    private TextEditor textEditor;

    public Interface(){
        textEditor = new TextEditor();
        setMinimumSize(new Dimension(W,H));
        setTitle("Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setJMenuBar(new WindowBar(FONT_WINDOWBAR));

        getContentPane().add(textEditor, BorderLayout.CENTER);
        getContentPane().add(new ToolBar(textEditor.getTextArea()), BorderLayout.PAGE_START);

    }

}
