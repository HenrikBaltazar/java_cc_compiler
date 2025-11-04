package org.example.ui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextOutput extends JPanel {
    private final JEditorPane textArea = new JEditorPane();
    private static final Font FONT_OUTPUT = new Font("Monospaced", Font.PLAIN, 20);
    private JLabel RowsColunmLabel = new JLabel("Linha: 1 ; Coluna: 1");
    TextOutput(){
        //textArea.setRows(20);
        //textArea.setColumns(60);
        textArea.setEditable(false);
        textArea.setFont(FONT_OUTPUT);
        textArea.setContentType("text/html");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(0, 50, 10, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        RowsColunmLabel.setFont(FONT_OUTPUT);
        setLayout(new BorderLayout());
        add(scrollPane,BorderLayout.CENTER);
        add(RowsColunmLabel,BorderLayout.PAGE_END);
    }

    public void setText(String text){
        textArea.setText(text);
    }

    public String getText(){
        return textArea.getText();
    }

    public void updateRowsColunm(int rows, int column){
        RowsColunmLabel.setText("Linha: " + rows + " ; Coluna: " + column);
    }

    public JEditorPane getTextArea() {
        return textArea;
    }

}
