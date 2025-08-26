package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowBar extends JMenuBar{
    private javax.swing.JMenu jMenuFile, jMenuEdit, jMenuCompile;
    private javax.swing.JMenuItem jMenuItemLoad, jMenuItemUndo, jMenuItemRedo, jMenuItemCompile;
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 16);

    public WindowBar(Interface parent){
        jMenuFile = new JMenu("Arquivo");
        jMenuFile.setFont(TEXT_FONT);
        jMenuItemLoad = new JMenuItem("Carregar programa...");
        jMenuItemLoad.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemLoad);

        jMenuEdit = new javax.swing.JMenu("Edição");
        jMenuEdit.setFont(TEXT_FONT);
        jMenuItemUndo = new JMenuItem("Desfazer");
        jMenuItemUndo.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemUndo);
        jMenuItemRedo = new JMenuItem("Refazer");
        jMenuItemRedo.setFont(TEXT_FONT);
        jMenuEdit.add(jMenuItemRedo);

        jMenuCompile = new javax.swing.JMenu("Compilação");
        jMenuCompile.setFont(TEXT_FONT);
        jMenuItemCompile = new JMenuItem("Compilar");
        jMenuItemCompile.setFont(TEXT_FONT);
        jMenuCompile.add(jMenuItemCompile);

        add(jMenuFile);
        add(jMenuEdit);
        add(jMenuCompile);

        jMenuItemLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getToolBar().openFile(parent);
            }
        });
    }
}
