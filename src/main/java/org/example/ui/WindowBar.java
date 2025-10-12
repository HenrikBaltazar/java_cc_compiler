package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowBar extends JMenuBar{
    private javax.swing.JMenu jMenuFile, jMenuEdit, jMenuCompile;
    private javax.swing.JMenuItem jMenuItemNewFile, jMenuItemOpenFile, jMenuItemSaveFile, jMenuItemSaveFileAs, jMenuItemUndo, jMenuItemRedo, jMenuItemCutText, jMenuItemCopyText, jMenuItemPasteText, jMenuItemBuildCode, jMenuItemRunCode, jMenuItemClear, jMenuItemHelp;;
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 16);

    public WindowBar(Interface parent){
        jMenuFile = new JMenu("Arquivo");
        jMenuFile.setFont(TEXT_FONT);
        jMenuItemNewFile = new JMenuItem("Novo arquivo...");
        jMenuItemNewFile.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemNewFile);
        jMenuItemOpenFile = new JMenuItem("Abrir arquivo...");
        jMenuItemOpenFile.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemOpenFile);
        jMenuItemSaveFile = new JMenuItem("Salvar");
        jMenuItemSaveFile.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemSaveFile);
        jMenuItemSaveFileAs = new JMenuItem("Salvar como...");
        jMenuItemSaveFileAs.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemSaveFileAs);


        jMenuEdit = new javax.swing.JMenu("Edição");
        jMenuEdit.setFont(TEXT_FONT);
        jMenuItemUndo = new JMenuItem("Desfazer");
        jMenuItemUndo.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemUndo);
        jMenuItemRedo = new JMenuItem("Refazer");
        jMenuItemRedo.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemRedo);
        jMenuItemCopyText = new JMenuItem("Copiar");
        jMenuItemCopyText.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemCopyText);
        jMenuItemCutText = new JMenuItem("Cortar");
        jMenuItemCutText.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemCutText);
        jMenuItemPasteText = new JMenuItem("Colar");
        jMenuItemPasteText.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemPasteText);


        jMenuCompile = new javax.swing.JMenu("Compilação");
        jMenuCompile.setFont(TEXT_FONT);
        jMenuItemBuildCode = new JMenuItem("Compilar");
        jMenuItemBuildCode.setFont(TEXT_FONT);
        jMenuCompile.add(jMenuItemBuildCode);
        jMenuItemRunCode = new JMenuItem("Executar");
        jMenuItemRunCode.setFont(TEXT_FONT);
        jMenuCompile.add(jMenuItemRunCode);
        jMenuItemClear = new JMenuItem("Limpar saída");
        jMenuItemClear.setFont(TEXT_FONT);
        jMenuCompile.add(jMenuItemClear);

        add(jMenuFile);
        add(jMenuEdit);
        add(jMenuCompile);

        jMenuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getToolBar().openFile();
            }
        });

        jMenuItemUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getTextInput().getTextArea().undoLastAction();
            }
        });

        jMenuItemRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getTextInput().getTextArea().redoLastAction();
            }
        });


        jMenuItemBuildCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getToolBar().buildCode();
            }
        });

    }
}
