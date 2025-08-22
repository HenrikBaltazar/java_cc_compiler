package org.example.ui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextOutput extends JPanel {
    private final JTextArea textArea = new JTextArea();
    private static final Font FONT_OUTPUT = new Font("Monospaced", Font.PLAIN, 20);
    TextOutput(){
        textArea.setRows(20);
        textArea.setColumns(60);
        textArea.setEditable(false);
        textArea.setFont(FONT_OUTPUT);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(0, 50, 10, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(scrollPane,BorderLayout.CENTER);
    }

    public void setText(String text){
        textArea.setText(text);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

}
