package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.border.EmptyBorder;

import org.example.Actions.FileManager;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public class TextInput extends JPanel {
    private RSyntaxTextArea textArea;
    private static final Font EDITOR_FONT = new Font("Monospaced", Font.PLAIN, 20);
    private int row=0, col=0;
    public TextInput(FileManager fileManager, Interface parent, TextOutput textOutput) {
        setLayout(new BorderLayout());

        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setFont(EDITOR_FONT);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAutoIndentEnabled(true);

        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(fileManager.isFileSaved()){
                    fileManager.setFileSaved(false,parent);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int caretPos = textArea.getCaretPosition();

                    int row = textArea.getLineOfOffset(caretPos) + 1;

                    int col = caretPos - textArea.getLineStartOffset(row - 1) + 1;

                    setRow(row);
                    setCol(col);
                    textOutput.updateRowsColunm(row, col);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(0, 50, 10, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public String getText() {
        return textArea.getText();
    }

    public void setInputText(String input) {
        textArea.setText(input);
    }
}
