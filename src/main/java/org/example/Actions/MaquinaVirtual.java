package org.example.Actions;

import org.example.ui.Interface;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MaquinaVirtual {
    private final String title = "Execução";
    private final ArrayList<ArrayList<String>> codigIn;
    private final Interface parent;
    private JFrame frame;
    private JEditorPane textArea;
    private static final Font FONT_OUTPUT = new Font("Monospaced", Font.PLAIN, 20);
    private static final HashSet<String> iAritmetica = new HashSet<>(Set.of("ADD", "DIV", "MUL", "SUB"));
    private static final HashSet<String> iMemoria = new HashSet<>(Set.of("ALB", "ALI", "ALR", "ALS", "LDB","LDI","LDR","LDS","LDV","STR", "LDX", "STX"));
    private static final HashSet<String> iLogica = new HashSet<>(Set.of("AND", "NOT", "OR"));
    private static final HashSet<String> iRelacional = new HashSet<>(Set.of("BGE", "BGR", "DIF", "EQL", "SME", "SMR"));
    private static final HashSet<String> iDesvio = new HashSet<>(Set.of("JMF", "JMP", "JMT", "STP"));
    private static final HashSet<String> iDados = new HashSet<>(Set.of("REA", "WRT"));
    List<String> pilha = new ArrayList<>();
    int topo = -1;
    Integer ponteiro = 0;
    private final Semaphore sem = new Semaphore(0);
    private String userInput = "";
    String history="Execucao iniciada...<br>--<br>";
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
    public MaquinaVirtual (Interface parent, ArrayList<ArrayList<String>> codigIn) {
        this.parent = parent;
        this.codigIn = codigIn;
        initializeWindow();
        new Thread(this::performVM).start();
    }

    private void performVM(){
        int n = codigIn.size();
        int m = 3;

        for (int i = 0; i < n; i++) {
            ArrayList<String> row = codigIn.get(i);
            System.out.println( "["+row.get(0)+","+row.get(1)+","+row.get(2)+"]");
            if (iAritmetica.contains(row.get(1))) {
                aritmetica(row);
            }else if (iMemoria.contains(row.get(1))) {
                memoria(row);
            }else if (iLogica.contains(row.get(1))) {
                logica(row);
            }else if (iRelacional.contains(row.get(1))) {
                relacional(row);
            }else if (iDesvio.contains(row.get(1))) {
                desvio(row);
            }else if (iDados.contains(row.get(1))) {
                dados(row);
            }

        }
    }

    private void dados(ArrayList<String> instrucao) {
        //1: num, 2: real, 3:texto, 4: flag

        switch (instrucao.get(1)){
            case "REA":
                String input = getUserInput();
                int tipo = detectarTipo(input);
                if(tipo!=Integer.parseInt(instrucao.get(2))){
                    quit("Leitura de tipo incorreta! ["+instrucao.get(0)+","+instrucao.get(1)+","+instrucao.get(2)+"]");
                    break;
                }
                pilha.add(getUserInput());
                topo++;
                ponteiro++;
                break;
            case "WRT":
                updateOutput(pilha.remove(topo--));
                ponteiro++;
                break;
        }
    }



    private void desvio(ArrayList<String> instrucao) {
        switch (instrucao.get(1)){
            case "JMF":
                if(pilha.get(topo).equalsIgnoreCase("false")){
                    ponteiro = Integer.parseInt(instrucao.get(2));
                }else{
                    ponteiro++;
                }
                topo--;
                break;
            case "JMP":
                ponteiro = Integer.parseInt(instrucao.get(2));
                break;
            case "JMT":
                if(pilha.get(topo).equalsIgnoreCase("true")){
                    ponteiro = Integer.parseInt(instrucao.get(2));
                }else{
                    ponteiro++;
                }
                topo--;
                break;
            case "STP":
                quit();
                break;
        }
    }

    private void relacional(ArrayList<String> instrucao) {
        if(pilha.size()>1) {
            if ((detectarTipo(pilha.get(topo)) == 1 || detectarTipo(pilha.get(topo)) == 2 ) && (detectarTipo(pilha.get(topo-1)) == 1 || detectarTipo(pilha.get(topo-1)) == 2)) {
                float a = Float.parseFloat(pilha.get(topo - 1));
                float b = Float.parseFloat(pilha.get(topo));
                switch (instrucao.get(1)){
                    case "BGE":
                        pilha.set(topo-1,String.valueOf(a>=b));
                        topo--;
                        ponteiro++;
                        break;
                    case "BGR":
                        pilha.set(topo-1,String.valueOf(a>b));
                        topo--;
                        ponteiro++;
                        break;
                    case "DIF":
                        pilha.set(topo-1,String.valueOf(a!=b));
                        topo--;
                        ponteiro++;
                        break;
                    case "EQL":pilha.set(topo-1,String.valueOf(a==b));
                        topo--;
                        ponteiro++;
                        break;
                    case "SME":
                        pilha.set(topo-1,String.valueOf(a<=b));
                        topo--;
                        ponteiro++;
                        break;
                    case "SMR":
                        pilha.set(topo-1,String.valueOf(a<b));
                        topo--;
                        ponteiro++;
                        break;
        }
            }else{
                quit("Operacao incorreta: tipo nao eh num ou real! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else{
            quit("Overflow! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
        }
    }

    private void logica(ArrayList<String> instrucao) {
        if(pilha.size()>1) {
            if (detectarTipo(pilha.get(topo)) == 4 && detectarTipo(pilha.get(topo-1)) == 4) {
                boolean a = Boolean.parseBoolean(pilha.get(topo - 1));
                boolean b = Boolean.parseBoolean(pilha.get(topo));
                switch (instrucao.get(1)) {
                    case "AND":
                        pilha.set(topo - 1, String.valueOf(a && b));
                        topo--;
                        ponteiro++;
                        break;
                    case "NOT":
                        pilha.set(topo, String.valueOf(!Boolean.parseBoolean(pilha.get(topo))));
                        topo--;
                        ponteiro++;
                        break;
                    case "OR":
                        pilha.set(topo - 1, String.valueOf(a || b));
                        topo--;
                        ponteiro++;
                        break;
                }
            }else{
                quit("Operacao incorreta: tipo nao eh boleano! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else{
            quit("Overflow! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
        }
    }

    private void memoria(ArrayList<String> instrucao) {
        HashSet<String> l = new HashSet<>(Set.of("LDB", "LDI","LDR","LDS"));
        if(l.contains(instrucao.get(1))){
            topo++;
            pilha.add(instrucao.get(2));
            ponteiro++;
        }else if(instrucao.get(1).equals("STR")) {
            if (pilha.size() >= Integer.parseInt(instrucao.get(2))) {
                pilha.set(Integer.parseInt(instrucao.get(2))-1, pilha.get(topo));
                topo--;
                ponteiro++;
            } else {
                quit("Overflow! pilha["+pilha.size()+"] mas [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else if(instrucao.get(1).equals("LDV")){
            if (pilha.size() >= Integer.parseInt(instrucao.get(2))) {
                topo++;
                pilha.add(pilha.get(Integer.parseInt(instrucao.get(2))));
                ponteiro++;
            } else {
                quit("Overflow! pilha["+pilha.size()+"] mas [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else if(instrucao.get(1).equals("LDX")){
            if (!pilha.isEmpty()) {
                pilha.set(topo,String.valueOf(topo));
                ponteiro++;
            } else {
                quit("Overflow! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else if(instrucao.get(1).equals("STX")){
            if(pilha.size()>1) {
                int endereco = topo;
                topo--;
                String valor = pilha.get(topo);
                pilha.set(endereco, valor);
                topo--;
                ponteiro++;
            } else {
                quit("Overflow! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else{
            int deslocamento = Integer.parseInt(instrucao.get(2));
            String value = switch (instrucao.get(1)) {
                case "ALB" -> "false";
                case "ALI" -> "0";
                case "ALR" -> "0.0";
                case "ALS" -> "";
                default -> "";
            };
            for (int i = topo + 1; i <= topo + deslocamento; i++) {
                if (i < pilha.size()) {
                    pilha.set(i, value);
                } else {
                    pilha.add(value);
                }
            }
            topo+=deslocamento;
            ponteiro++;
        }
    }

    private void aritmetica(ArrayList<String> instrucao) {
        if(topo>0){
            if ((detectarTipo(pilha.get(topo)) == 1 || detectarTipo(pilha.get(topo)) == 2 ) && (detectarTipo(pilha.get(topo-1)) == 1 || detectarTipo(pilha.get(topo-1)) == 2)) {
                int topoValue = Integer.parseInt(pilha.get(topo-1));
                int topoMenosUm = Integer.parseInt(pilha.get(topo-1));
                switch (instrucao.get(1)) {
                    case "ADD":
                        topo++;
                        pilha.add(String.valueOf(topoMenosUm + topoValue));
                        ponteiro++;
                        break;
                    case "DIV":
                        if (Integer.parseInt(pilha.get(topo)) == 0) {
                            quit("Divisão por zero! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
                            break;
                        }
                        topo++;
                        pilha.add(String.valueOf(topoMenosUm / topoValue));
                        ponteiro++;
                        break;
                    case "MUL":
                        topo++;
                        pilha.add(String.valueOf(topoMenosUm * topoValue));
                        ponteiro++;
                        break;
                    case "SUB":
                        topo++;
                        pilha.add(String.valueOf(topoMenosUm - topoValue));
                        ponteiro++;
                        break;
                }
            }else{
                quit("Operacao incorreta: tipo nao eh num ou real! [" + instrucao.get(0) + "," + instrucao.get(1) + "," + instrucao.get(2) + "]");
            }
        }else{
            quit("Overflow! ["+instrucao.get(0)+","+instrucao.get(1)+","+instrucao.get(2)+"]");
        }

    }

    public static int detectarTipo(String valor) {
        if (valor == null) return 3; // string

        valor = valor.trim();

        // --- BOOLEAN (4) ---
        if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
            return 4;
        }

        // --- INTEGER (1) ---
        try {
            Integer.parseInt(valor);
            return 1;
        } catch (NumberFormatException e) {
            // não é inteiro
        }

        // --- REAL (2) ---
        try {
            Double.parseDouble(valor);
            return 2;
        } catch (NumberFormatException e) {
            // não é real
        }

        // --- STRING (3) ---
        return 3;
    }



    private void initializeWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle(title);
        frame.getContentPane().setBackground(Color.BLACK);

        textArea = new JEditorPane();
        textArea.setFont(FONT_OUTPUT);
        textArea.setCaretColor(Color.GREEN);
        textArea.setEditable(false);
        textArea.setContentType("text/html");
        textArea.setText("<html<head><style>"+style+"</style></head><body>"+history+"</body></html>");
        textArea.setPreferredSize(new Dimension(500, 500));
        textArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBorder(null);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
    }

    void updateOutput(String out){
        if(out.contains("\"")){
            out = out.replace("\"","");
        }
        history += out;
        textArea.setText("<html<head><style>"+style+"</style></head><body>"+history+"</body></html>");
    }

    private String getUserInput() {
        textArea.setEditable(true);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume(); // evita nova linha

                    userInput = textArea.getText().trim();
                    textArea.setEditable(false);

                    sem.release(); // libera o método que estava esperando
                }
            }
        });

        try {
            // Aguarda até o usuário apertar ENTER
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateOutput(userInput);
        return userInput;
    }

    void quit(String msg){
        textArea.setEditable(false);
        updateOutput("<br>--<br>Execução finalizada com mensagem: <b>" + msg + "</b>");
        return;
    }

    void quit(){
        textArea.setEditable(false);
        updateOutput("<br>--<br>Execução finalizada.</b>");
        return;
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
