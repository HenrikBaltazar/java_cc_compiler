package org.example;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Interface extends JFrame {
    private static final int W = 1920, H = 1080;

    public Interface(){
        setMinimumSize(new Dimension(W,H));
        setTitle("Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

}
