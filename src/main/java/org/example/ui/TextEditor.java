package org.example.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public class TextEditor extends JPanel {
    private RSyntaxTextArea textArea;

    public TextEditor() {
        setLayout(new BorderLayout());

        // Cria o RSyntaxTextArea
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        textArea.setAntiAliasingEnabled(true);

        // Adiciona margens internas
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Coloca dentro de um scroll pane
        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(0, 100, 10, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    public String getText(){
        return textArea.getText();
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }
}
