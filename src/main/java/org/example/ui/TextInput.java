package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.border.EmptyBorder;

import org.example.Actions.FileManager;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public class TextInput extends JPanel {
    private RSyntaxTextArea textArea;
    private static final Font EDITOR_FONT = new Font("Monospaced", Font.PLAIN, 20);
    private int row=0, col=0;
    public TextInput(Interface parent) {
        setLayout(new BorderLayout());

        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setFont(EDITOR_FONT);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAutoIndentEnabled(true);
        SwingUtilities.invokeLater(() -> textArea.requestFocusInWindow());

        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(parent.getFileManager().isFileSaved()){
                    parent.getFileManager().setFileSaved(false,parent);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                parent.getShortcut().Detect(parent,e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updatePosition(parent);
            }

        });

        textArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updatePosition(parent);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

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

    public void updatePosition(Interface parent){
        try {
            int caretPos = textArea.getCaretPosition();

            int row = textArea.getLineOfOffset(caretPos) + 1;

            int col = caretPos - textArea.getLineStartOffset(row - 1) + 1;

            setRow(row);
            setCol(col);
            parent.getTextOutput().updateRowsColunm(row, col);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
