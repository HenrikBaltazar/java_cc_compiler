package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ToolBar extends JToolBar {

    private JButton jButtonNewFile, jButtonOpenFile, jButtonSaveFile, jButtonCutText, jButtonCopyText, jButtonPasteText, jButtonBuildCode, jButtonRunCode, jButtonClear, jButtonHelp;
    private Interface parent;

    public ToolBar(Interface parent) {
        setName("ToolBar");
        this.parent = parent;

        jButtonNewFile = createButton(loadIcon("/icon_new.png"), "Novo arquivo (CTRL+N)");
        jButtonOpenFile = createButton(loadIcon("/icon_open.png"), "Abrir arquivo (CTRL+SHIFT+A)");
        jButtonSaveFile = createButton(loadIcon("/icon_save.png"), "Salvar arquivo (CTRL+S)");
        jButtonCutText = createButton(loadIcon("/icon_cut.png"), "Recortar (CTRL+X)");
        jButtonCopyText = createButton(loadIcon("/icon_copy.png"), "Copiar (CTRL+C)");
        jButtonPasteText = createButton(loadIcon("/icon_paste.png"), "Colar (CTRL+V)");
        jButtonBuildCode = createButton(loadIcon("/icon_build.png"), "Compilar");
        jButtonRunCode = createButton(loadIcon("/icon_run.png"), "Executar");
        jButtonClear = createButton(loadIcon("/icon_clear.png"), "Limpar saída");
        jButtonHelp = createButton(loadIcon("/icon_help.png"), "Ajuda");

        jButtonNewFile.addActionListener(e -> newFile());
        jButtonOpenFile.addActionListener(e -> openFile());
        jButtonSaveFile.addActionListener(e -> saveFile());
        jButtonBuildCode.addActionListener(e -> buildCode());

        jButtonCutText.addActionListener(e -> parent.getTextInput().getTextArea().cut());
        jButtonCopyText.addActionListener(e -> parent.getTextInput().getTextArea().copy());
        jButtonPasteText.addActionListener(e -> parent.getTextInput().getTextArea().paste());
        jButtonRunCode.addActionListener(e -> runCode());
        jButtonClear.addActionListener(e -> clearAll());
        jButtonHelp.addActionListener(e -> parent.openHelpWindow());

        add(jButtonNewFile);
        add(jButtonOpenFile);
        add(jButtonSaveFile);
        addSeparator();
        add(jButtonCutText);
        add(jButtonCopyText);
        add(jButtonPasteText);
        addSeparator();
        add(jButtonBuildCode);
        add(jButtonRunCode);
        add(jButtonClear);
        addSeparator();
        add(jButtonHelp);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                parent.getShortcut().Detect(parent, e);
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Habilita/Desabilita "Compilar" conforme o editor está vazio ou não
        updateCompileEnabled(); // estado inicial
        parent.getTextInput().getTextArea().getDocument().addDocumentListener(new DocumentListener() {
            private void changed() { updateCompileEnabled(); }
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        });

    }

    public void newFile() {
        // Pergunta salvar apenas se houver alterações pendentes
        if (!parent.getFileManager().saveInSecondChance()) return; // Cancelar -> não faz nada

        parent.getTextInput().setInputText("");
        parent.getTextInput().setRow(0);
        parent.getTextInput().setCol(0);

        parent.getTextOutput().setText("");

        parent.getFileManager().resetAsNewCleanBuffer();

        parent.getTextInput().getTextArea().requestFocusInWindow();
    }

    public void openFile() {
        File selectedFile = parent.getFileManager().openFileChooser();
        if (selectedFile != null) {
            try {
                String text = Files.readString(selectedFile.toPath());
                parent.getTextInput().setInputText(text);
                parent.getTextOutput().setText("");
                parent.getTextInput().setRow(0);
                parent.getTextInput().setCol(0);
                parent.getTextInput().getTextArea().requestFocusInWindow();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Erro ao abrir arquivo: " + e.getMessage(), "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveFile() {
        parent.getFileManager().saveFile();
    }

    public void buildCode() {
        if (parent.getFileManager().saveInSecondChance()) {
            clearAll();
            parent.build.buildCode(false);
        } else {
            JOptionPane.showMessageDialog(parent, "Utilize o botão 'executar' para compilar sem salvar");
        }
    }


    private ImageIcon loadIcon(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("Erro: Não foi possível encontrar o recurso: " + path);
                return null;
            }
            return new ImageIcon(ImageIO.read(stream));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem " + path + ": " + e.getMessage());
            return null;
        }
    }

    private JButton createButton(ImageIcon icon, String toolTipText) {
        JButton button = new JButton(icon);
        button.setToolTipText(toolTipText);
        return button;
    }

    public void clearAll() {
        parent.getTextOutput().clear();
    }

    public void runCode() {
        clearAll();
        parent.build.buildCode(true);
    }

    private void updateCompileEnabled() {
        boolean enabled = !parent.getTextInput().getTextArea().getText().isBlank();
        jButtonBuildCode.setEnabled(enabled);
        jButtonRunCode.setEnabled(enabled);
    }


}