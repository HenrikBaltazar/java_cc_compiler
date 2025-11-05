import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminalExecucao extends JFrame {
    private final JTextArea terminalArea;
    private final MaquinaVirtual maquinaVirtual;

    private volatile String ultimoInput;
    private final Object lock = new Object();
    private boolean aguardandoInput = false;

    public TerminalExecucao(List<Instrucao> codigoIntermediario) {
        setTitle("Terminal de Execução");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        terminalArea = new JTextArea();
        terminalArea.setEditable(false);
        terminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalArea.setBackground(Color.BLACK);
        terminalArea.setForeground(Color.GREEN);

        terminalArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (aguardandoInput && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (lock) {
                        String[] linhas = terminalArea.getText().split("\n");
                        String ultimaLinha = linhas[linhas.length - 1];
                        if (ultimaLinha.startsWith(">> ")) {
                            ultimoInput = ultimaLinha.substring(3).trim();
                        } else {
                            ultimoInput = ultimaLinha.trim();
                        }
                        terminalArea.setEditable(false);
                        terminalArea.append("\n");
                        lock.notify();
                    }
                    e.consume();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(terminalArea);
        add(scrollPane, BorderLayout.CENTER);

        maquinaVirtual = new MaquinaVirtual(this);
        setVisible(true);
        new Thread(() -> executarCodigo(codigoIntermediario)).start();
    }

    public String solicitarInput(String mensagem) {
        terminalArea.append(mensagem);
        terminalArea.append(">> ");
        terminalArea.setEditable(true);
        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
        terminalArea.requestFocus();
        aguardandoInput = true;

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException("Erro ao aguardar input do usuário", e);
            }
        }
        aguardandoInput = false;
        return ultimoInput;
    }

    private void executarCodigo(List<Instrucao> codigoIntermediario) {
        Map<Integer, Instrucao> mapaInstrucoes = new HashMap<>();
        boolean result = false;
        for (Instrucao instrucao : codigoIntermediario) {
            mapaInstrucoes.put(instrucao.getNumero(), instrucao);
        }

        try {
            //terminalArea.append("Iniciando execução...\n");

            String codigoAtual = "";
            int numeroInstrucaoAtual = 1;

            while (!codigoAtual.equals("STP")) {
                Instrucao instrucaoAtual = mapaInstrucoes.get(numeroInstrucaoAtual);
                if (instrucaoAtual == null) {
                    throw new RuntimeException("Instrução inválida: " + numeroInstrucaoAtual);
                }

                codigoAtual = instrucaoAtual.getCodigo();

                switch (codigoAtual) {
                    case "BGE":
                        result = maquinaVirtual.executarBGE();
                        numeroInstrucaoAtual++;
                        break;
                    case "BGR":
                        result = maquinaVirtual.executarBGR();
                        numeroInstrucaoAtual++;
                        break;
                    case "DIF":
                        result = maquinaVirtual.executarDIF();
                        numeroInstrucaoAtual++;
                        break;
                    case "EQL":
                        result = maquinaVirtual.executarEQL();
                        numeroInstrucaoAtual++;
                        break;
                    case "SME":
                        result = maquinaVirtual.executarSME();
                        numeroInstrucaoAtual++;
                        break;
                    case "SMR":
                        result = maquinaVirtual.executarSMR();
                        numeroInstrucaoAtual++;
                        break;
                    case "JMF":
                        if (!result) {
                            numeroInstrucaoAtual = Integer.parseInt(instrucaoAtual.getParametro());
                            maquinaVirtual.executarJMF(numeroInstrucaoAtual-1);
                        } else {
                            numeroInstrucaoAtual++;
                        }
                        break;
                    case "JMT":
                        if (result) {
                            numeroInstrucaoAtual = Integer.parseInt(instrucaoAtual.getParametro());
                            maquinaVirtual.executarJMT(numeroInstrucaoAtual-1);
                        } else {
                            numeroInstrucaoAtual++;
                        }
                        break;
                    case "JMP":
                        numeroInstrucaoAtual = Integer.parseInt(instrucaoAtual.getParametro());
                        maquinaVirtual.executarJMP(numeroInstrucaoAtual-1);
                        break;
                    case "WRT":
                        Object valor = maquinaVirtual.executarWRT();
                        terminalArea.append(valor + "\n");
                        numeroInstrucaoAtual++;
                        break;
                    case "STP":
                        break;
                    case "OR":
                        result = maquinaVirtual.executarOR();
                        numeroInstrucaoAtual++;
                        break;
                    case "AND":
                        result = maquinaVirtual.executarAND();
                        numeroInstrucaoAtual++;
                        break;
                    case "NOT":
                        result = maquinaVirtual.executarNOT();
                        numeroInstrucaoAtual++;
                        break;
                    default:
                        maquinaVirtual.executarInstrucao(instrucaoAtual);
                        numeroInstrucaoAtual++;
                        break;
                }
            }
            terminalArea.append("\nExecução concluída\n");
        } catch (RuntimeException ex) {
            terminalArea.append(ex.getMessage() + "\n");
        }
    }
}
