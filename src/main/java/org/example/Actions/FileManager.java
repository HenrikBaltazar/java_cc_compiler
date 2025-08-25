package org.example.Actions;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileManager {

    public static File openFileChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser(new java.io.File("resources"));
        fileChooser.setPreferredSize(new Dimension(800, 600));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Selecione o Programa");

        int returnValue = fileChooser.showOpenDialog(parent);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.getPath().endsWith(".txt")) {
                return selectedFile;
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Arquivo deve ser do tipo TXT",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
