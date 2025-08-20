package org.example;

public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            Interface window = new Interface();
            window.setLocationRelativeTo(null); // Centraliza a window
            window.setVisible(true); // Torna a window visível
        });
    }
}