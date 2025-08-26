package org.example;

import org.example.Actions.Build;
import org.example.ui.Interface;

import javax.swing.*;

public class Main {
    private static Build build = new Build();
    private static Interface ui;
    public static void main(String[] args) {
        ui = new Interface(build);
        build.setParent(ui);
        SwingUtilities.invokeLater(() -> {
            ui.setVisible(args.length == 0 || !args[0].equals("-h"));
        });
    }
}