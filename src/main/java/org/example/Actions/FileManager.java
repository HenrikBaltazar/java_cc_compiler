package org.example.Actions;

import org.example.ui.Interface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    private Path filePath;
    private boolean isFileSaved = true;
    public File openFileChooser(Component parent, Interface classParent) {
        if(saveInSecondChance(classParent)){
            JFileChooser fileChooser = new JFileChooser(new java.io.File("resources"));
            fileChooser.setPreferredSize(new Dimension(800, 600));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Selecione o Programa");
            fileChooser.setApproveButtonText("Abrir");

            int returnValue = fileChooser.showOpenDialog(parent);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getPath().endsWith(".txt")) {
                    this.filePath = selectedFile.toPath();
                    this.isFileSaved = true;
                    classParent.setWindowTitle("Compilador - "+selectedFile.getName());
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
        return null;
    }

    public int saveFile(Interface parent) {
        JFileChooser fileChooser = new JFileChooser(new java.io.File("resources"));
        fileChooser.setPreferredSize(new Dimension(800, 600));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Selecione o diretorio para salvar");
        fileChooser.setApproveButtonText("Salvar");

        int returnValue = fileChooser.showOpenDialog(parent);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            if(!selectedDir.getName().toLowerCase().endsWith(".txt")){
                selectedDir = new File(selectedDir.getAbsolutePath()+".txt");
            }
            try(FileWriter fw = new FileWriter(selectedDir)) {
                fw.write(parent.getTextInput().getText());
                parent.setWindowTitle("Compilador"+" - "+selectedDir.getName());
                JOptionPane.showMessageDialog(parent,"Arquivo salvo em "+selectedDir.getAbsolutePath()+" com sucesso!");
                this.isFileSaved = true;
                this.filePath = selectedDir.toPath();
            }catch (Exception e){
                e.printStackTrace();
            }

            return 1;
        }
        return 0;
    }

    public boolean saveInSecondChance(Interface parent) {
        if (!this.isFileSaved) {
            int option = JOptionPane.showConfirmDialog(
                    parent,
                    "O arquivo ainda não foi salvo, deseja salvar o arquivo?",
                    "Confirmar novo arquivo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION){
                int file = saveFile(parent);
                if( file != 1){
                    saveInSecondChance(parent);
                }
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public boolean isFileSaved() {
        return isFileSaved;
    }

    public void setFileSaved(boolean fileSaved, Interface parent) {
        isFileSaved = fileSaved;
        if (parent.getWindowTitle().endsWith("*") && isFileSaved) {
            parent.setWindowTitle(parent.getWindowTitle().replace("*", ""));
        }

        if (!isFileSaved && !parent.getWindowTitle().endsWith("*")) {
            parent.setWindowTitle(parent.getWindowTitle()+"*");
        }
    }

}
