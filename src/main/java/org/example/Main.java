package org.example;

import org.example.ui.Interface;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interface ui = new Interface();
            ui.setVisible(true);
        });
    }
}