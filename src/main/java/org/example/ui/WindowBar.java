package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowBar extends JMenuBar{
    private javax.swing.JMenu jMenuFile, jMenuEdit, jMenuCompile;
    private javax.swing.JMenuItem jMenuItemLoad, jMenuItemUndo, jMenuItemRedo, jMenuItemLoadE1, jMenuItemLoadE2, jMenuItemCompile;
    private String e1 = "begin teste\n" +
            "set a\n" +
            "= 10\n" +
            "show (\n" +
            "3.14 ,cont\n" +
            "\"abrir\n" +
            ") @ ", e2 = "begin Exemplo\n" +
            "    define\n" +
            "        nome    : text;\n" +
            "        notas   : real[3];\n" +
            "        media   : real;\n" +
            "        aprovado: flag;\n" +
            "    start\n" +
            "        show ( \"Digite seu nome: \" );\n" +
            "        read ( nome );\n" +
            "\n" +
            "        show ( \"Digite as 3 notas (use ponto para decimais): \" );\n" +
            "        read ( notas[0] );\n" +
            "        read ( notas[1] );\n" +
            "        read ( notas[2] );\n" +
            "\n" +
            "        set media = (notas[0] + notas[1] + notas[2]) / 3;\n" +
            "        set aprovado = media >>= 6.0;\n" +
            "\n" +
            "        show ( \"Aluno: \", nome );\n" +
            "        show ( \"Notas: \", notas[0], \", \", notas[1], \", \", notas[2] );\n" +
            "        show ( \"Média = \", media );\n" +
            "\n" +
            "        if aprovado then\n" +
            "            show ( \"Resultado: Aprovado\" );\n" +
            "        else\n" +
            "            show ( \"Resultado: Reprovado\" );\n" +
            "        end;\n" +
            "    end;\n" +
            "end.\n" +
            "\n" +
            "begin\n" +
            "    define\n" +
            "        nomes : text[4] = { \"Ana\", \"Bia\", \"Caio\", \"Davi\" };\n" +
            "    start\n" +
            "        show ( \"Nomes armazenados:\" );\n" +
            "        show ( nomes[0], \", \", nomes[1], \", \", nomes[2], \", \", nomes[3] );\n" +
            "\n" +
            "        show ( \"Digite o índice (0 a 3) de um nome para exibir: \" );\n" +
            "        read ( i );\n" +
            "\n" +
            "        if (i << 0) | (i >> 4) then\n" +
            "            show ( \"Índice inválido!\" );\n" +
            "        else\n" +
            "            show ( \"Nome selecionado: \", nomes[i] );\n" +
            "        end;\n" +
            "    end;\n" +
            "end.";
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 16);

    public WindowBar(Interface parent){
        jMenuFile = new JMenu("Arquivo");
        jMenuFile.setFont(TEXT_FONT);
        jMenuItemLoad = new JMenuItem("Carregar programa...");
        jMenuItemLoad.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemLoad);
        jMenuItemLoadE1 = new javax.swing.JMenuItem("Carregar exemplo 1");
        jMenuItemLoadE1.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemLoadE1);
        jMenuItemLoadE2 = new javax.swing.JMenuItem("Carregar exemplo 2");
        jMenuItemLoadE2.setFont(TEXT_FONT);
        jMenuFile.add(jMenuItemLoadE2);

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

        jMenuItemLoadE1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getTextInput().setInputText(e1);
                parent.getFileManager().setFileSaved(false);
            }
        });

        jMenuItemLoadE2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getTextInput().setInputText(e2);
                parent.getFileManager().setFileSaved(false);
            }
        });

        jMenuItemCompile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getToolBar().buildCode();
            }
        });

    }
}
