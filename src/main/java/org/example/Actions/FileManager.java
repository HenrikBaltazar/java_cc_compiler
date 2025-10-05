package org.example.Actions;

import org.example.ui.Interface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    private Path filePath;
    private boolean isFileSaved = true;
    private Interface parent;
    private boolean isFileOpened = false;
    private File lastDir = new File("resources");

    public FileManager(Interface parent) {
        this.parent = parent;
    }
    public File openFileChooser() {
        if(saveInSecondChance()){
            JFileChooser fileChooser = new JFileChooser(lastDir);
            fileChooser.setPreferredSize(new Dimension(800, 600));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Selecione o Programa");
            fileChooser.setApproveButtonText("Abrir");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));

            int returnValue = fileChooser.showOpenDialog(parent);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                lastDir = fileChooser.getCurrentDirectory(); // atualiza pasta corrente
                if (selectedFile.getPath().endsWith(".txt")) {
                    this.filePath = selectedFile.toPath();
                    this.isFileSaved = true;
                    parent.setWindowTitle("Compilador - "+selectedFile.getName());
                    isFileOpened = true;
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

    public int saveFile() {
        if(!isFileOpened){
            JFileChooser fileChooser = new JFileChooser(lastDir);
            fileChooser.setPreferredSize(new Dimension(800, 600));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Selecione o diretorio para salvar");
            fileChooser.setApproveButtonText("Salvar");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt")); // <<<

            int returnValue = fileChooser.showOpenDialog(parent);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                lastDir = fileChooser.getCurrentDirectory();

                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getParent(), selectedFile.getName() + ".txt");
                }

                if (selectedFile.exists()) {
                    int over = JOptionPane.showConfirmDialog(parent,
                            "O arquivo já existe. Deseja sobrescrever?",
                            "Confirmar sobrescrita",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (over != JOptionPane.YES_OPTION) return 0;
                }

                try(FileWriter fw = new FileWriter(selectedFile)) {
                    fw.write(parent.getTextInput().getText());
                    parent.setWindowTitle("Compilador"+" - "+selectedFile.getName());
                    JOptionPane.showMessageDialog(parent,"Arquivo salvo em "+selectedFile.getAbsolutePath()+" com sucesso!");
                    this.isFileSaved = true;
                    this.filePath = selectedFile.toPath();
                }catch (Exception e){
                    e.printStackTrace();
                }
                isFileOpened = true;
                return 1;
            }
        }else{
            try(FileWriter fw = new FileWriter(filePath.toFile())) {
                fw.write(parent.getTextInput().getText());
                parent.setWindowTitle("Compilador"+" - "+filePath.toFile().getName());
                JOptionPane.showMessageDialog(parent,"Arquivo salvo em "+filePath.toFile().getAbsolutePath()+" com sucesso!");
                this.isFileSaved = true;
                lastDir = filePath.toFile().getParentFile();//  atualiza pasta corrente para a do arquivo


                this.filePath = filePath.toFile().toPath();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    public boolean saveInSecondChance() {
        if (!this.isFileSaved) {
            int option = JOptionPane.showConfirmDialog(
                    parent,
                    "O arquivo ainda não foi salvo, deseja salvar o arquivo?",
                    "Confirmar novo arquivo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                return false;           // não prossegue
            }
            if (option == JOptionPane.NO_OPTION) {
                return true;            // prossegue SEM salvar (regra do enunciado)
            }
            // YES_OPTION -> tenta salvar
            int file = saveFile();
            return (file == 1) || isFileOpened;
        }
        return true; // já está salvo -> pode prosseguir
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

    public void setFileSaved(boolean fileSaved) {
        isFileSaved = fileSaved;
        if (parent.getWindowTitle().endsWith("*") && isFileSaved) {
            parent.setWindowTitle(parent.getWindowTitle().replace("*", ""));
        }

        if (!isFileSaved && !parent.getWindowTitle().endsWith("*")) {
            parent.setWindowTitle(parent.getWindowTitle()+"*");
        }
    }

    // Reseta o estado como se fosse um buffer novo e limpo (sem arquivo associado)
    public void resetAsNewCleanBuffer() {
        this.filePath = null;          // sem arquivo vinculado
        this.isFileOpened = false;     // não há arquivo aberto
        this.isFileSaved = true;       // estado "limpo" (não pedir para salvar)
        // remove asterisco do título e volta ao título base
        parent.setWindowTitle("Compilador");
    }

}
