package org.example.ui;
import java.awt.*;
import javax.swing.*;

public class Interface extends JFrame {
    private static final int W = 1920, H = 1080;
    private TextInput textInput;
    private TextOutput textOutput;
    public Interface(){
        textInput = new TextInput();
        textOutput = new TextOutput();
        setMinimumSize(new Dimension(W,H));
        setTitle("Compilador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                textInput, textOutput
        );
        splitPane.setDividerLocation(1200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);

        setJMenuBar(new WindowBar());
        getContentPane().add(new ToolBar(textInput.getTextArea(), textOutput.getTextArea()), BorderLayout.PAGE_START);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

}
