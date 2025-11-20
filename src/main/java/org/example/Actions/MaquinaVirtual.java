package org.example.Actions;

import org.example.ui.Interface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MaquinaVirtual {
    private final String title = "Execução";
    private final ArrayList<ArrayList<String>> codigIn;
    private final Interface parent;
    private JFrame frame;
    private JEditorPane textArea;
    private static final Font FONT_OUTPUT = new Font("Monospaced", Font.PLAIN, 20);
    public MaquinaVirtual (Interface parent, ArrayList<ArrayList<String>> codigIn) {
        this.parent = parent;
        this.codigIn = codigIn;
        performVM();
    }

    private void performVM(){
        initializeWindow();
        //TODO: Understand and execute codigIn

        if (1!=1){
            showError("ERRO!"); //chamada em caso de falha
        }

        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
    }

    private void initializeWindow() {
        String style= """
                body {
                    background-color: #000000;
                    color: #00ff00;
                    font-family: Consolas, "Courier New", monospace;
                    font-size: 14px;
                    margin: 0;
                    padding: 10px;
                    white-space: pre-wrap;
                }
                """;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle(title);
        frame.getContentPane().setBackground(Color.BLACK);

        textArea = new JEditorPane();
        textArea.setFont(FONT_OUTPUT);
        textArea.setCaretColor(Color.GREEN);
        textArea.setEditable(true);
        textArea.setContentType("text/html");
        textArea.setText("<html<head><style>"+style+"</style></head><body>Digite aqui...</body></html>");
        textArea.setPreferredSize(new Dimension(500, 500));
        textArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBorder(null);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);


    }

    private void showError(String msg){
        JOptionPane.showMessageDialog(parent, msg);
    }

    public void showcodigInFrame() {
        String title = "Código intermediário";
        ArrayList<String> colLabels = new ArrayList<>(List.of("Número", "Operação", "Parâmetro"));
        int n = codigIn.size();
        int m = colLabels.size();

        Object[][] data = new Object[n][m];
        for (int i = 0; i < n; i++) {
            ArrayList<String> row = codigIn.get(i);
            for (int j = 0; j < m; j++) {
                data[i][j] = row.get(j);
            }
        }

        JTable table = new JTable(data, colLabels.toArray()) {
        };
        table.setEnabled(false);
        table.setRowHeight(30);
        table.setFont(new Font("Courier New", Font.PLAIN, 18));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Courier New", Font.BOLD, 18));

        // ScrollPane com row header
        JScrollPane scrollPane = new JScrollPane(table);

        // Frame
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(parent); // parent deve ser JFrame ou Component
        frame.setVisible(true);
    } //LEGADO
    
}
