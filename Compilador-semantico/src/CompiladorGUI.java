import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.List;

public class CompiladorGUI extends JFrame {
    private final JTextArea codeArea;
    private final JTextArea messageArea;
    private final JTable codigoIntermediarioTable;
    private final JLabel statusLabel;
    private boolean isEdited = false;
    private File currentFile;
    private File lastDirectory = null;
    private final StringBuilder errosLexicos = new StringBuilder();
    private final StringBuilder errosSintaticos = new StringBuilder();
    private String errosSemanticos = "";
    private List<Instrucao> codigoIntermediario;

    public CompiladorGUI() {
        setTitle("Compilador - Sem titulo");
        setSize(1000, 640);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codeArea.setTabSize(4);
        codeArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                    evt.consume();
                    int pos = codeArea.getCaretPosition();
                    codeArea.insert("    ", pos);
                }
            }
        });

        messageArea = new JTextArea();
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messageArea.setEditable(false);

        String[] colunas = {"Número", "Código", "Parâmetro"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        codigoIntermediarioTable = new JTable(modeloTabela);
        codigoIntermediarioTable.setEnabled(false);
        codigoIntermediarioTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codigoIntermediarioTable.setRowHeight(20);
        JScrollPane codigoScrollPane = new JScrollPane(codigoIntermediarioTable);

        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, messageScrollPane);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.6);

        JSplitPane lateralSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainSplitPane, codigoScrollPane);
        lateralSplitPane.setDividerLocation(700);
        lateralSplitPane.setResizeWeight(0.7);
        add(lateralSplitPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Linha: 1 Coluna: 1");
        add(statusLabel, BorderLayout.SOUTH);

        codeArea.addCaretListener(_ -> {
            int caretPos = codeArea.getCaretPosition();
            int rowNum = (caretPos == 0) ? 1 : 0;
            int colNum = 0;

            try {
                rowNum = codeArea.getLineOfOffset(caretPos) + 1;
                colNum = caretPos - codeArea.getLineStartOffset(rowNum - 1) + 1;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            statusLabel.setText("Linha: " + rowNum + " Coluna: " + colNum);
        });

        codeArea.getDocument().addDocumentListener((SimpleDocumentListener) () -> isEdited = true);

        addLineNumbering(codeScrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenu editMenu = new JMenu("Edição");
        JMenu compileMenu = new JMenu("Compilação");

        JMenuItem newItem = new JMenuItem("Novo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Salvar");
        JMenuItem saveAsItem = new JMenuItem("Salvar Como");
        JMenuItem exitItem = new JMenuItem("Sair");

        fileMenu.add(newItem);
        fileMenu.add(openItem);

        newItem.addActionListener(_ -> {
            if (isEdited) {
                Object[] options = {"Sim", "Não", "Cancelar"};
                int option = JOptionPane.showOptionDialog(this, "Deseja salvar as alterações antes de criar um novo arquivo?", "Salvar Alterações", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] // "Sim" como padrão
                );

                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                } else if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            codeArea.setText("");
            messageArea.setText("");
            currentFile = null;
            isEdited = false;
            updateWindowTitle(null);
        });

        openItem.addActionListener(_ -> {
            if (isEdited) {
                Object[] options = {"Sim", "Não", "Cancelar"};
                int option = JOptionPane.showOptionDialog(this, "Deseja salvar as alterações antes de abrir um novo arquivo?", "Salvar Alterações", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] // "Sim" como padrão
                );

                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                } else if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            JFileChooser fileChooser = lastDirectory != null ? new JFileChooser(lastDirectory) : new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                lastDirectory = file.getParentFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    codeArea.setText(content.toString());
                    messageArea.setText("");
                    modeloTabela.setRowCount(0);
                    currentFile = file;
                    isEdited = false;
                    updateWindowTitle(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        fileMenu.add(saveItem);
        saveItem.addActionListener(_ -> {
            if (isEdited) {
                saveFile();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma alteração para salvar.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        fileMenu.add(saveAsItem);
        saveAsItem.addActionListener(_ -> saveAsFile());

        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenuItem copyItem = new JMenuItem("Copiar");
        JMenuItem pasteItem = new JMenuItem("Colar");
        JMenuItem cutItem = new JMenuItem("Recortar");

        editMenu.add(copyItem);
        copyItem.addActionListener(_ -> {
            if (codeArea.getSelectedText() != null) {
                codeArea.copy();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum texto selecionado para copiar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        editMenu.add(pasteItem);
        pasteItem.addActionListener(_ -> {
            try {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    codeArea.paste();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum texto disponível para colar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        editMenu.add(cutItem);
        cutItem.addActionListener(_ -> {
            if (codeArea.getSelectedText() != null) {
                codeArea.cut();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum texto selecionado para recortar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem compileItem = new JMenuItem("Compilar");
        JMenuItem runItem = new JMenuItem("Executar");

        JToolBar toolBar = new JToolBar();

        JButton newButton = createToolbarButton("/new.png");
        newButton.setToolTipText("Novo");
        toolBar.add(newButton);
        newButton.addActionListener(_ -> newItem.doClick());

        JButton openButton = createToolbarButton("/open.png");
        openButton.setToolTipText("Abrir");
        toolBar.add(openButton);
        openButton.addActionListener(_ -> openItem.doClick());

        JButton saveButton = createToolbarButton("/save.png");
        saveButton.setToolTipText("Salvar");
        toolBar.add(saveButton);
        saveButton.addActionListener(_ -> saveItem.doClick());

        JButton saveAsButton = createToolbarButton("/save_as.png");
        saveAsButton.setToolTipText("Salvar Como");
        toolBar.add(saveAsButton);
        saveAsButton.addActionListener(_ -> saveAsItem.doClick());

        JButton exitButton = createToolbarButton("/close.png");
        exitButton.setToolTipText("Sair");
        toolBar.add(exitButton);
        exitButton.addActionListener(_ -> exitItem.doClick());

        toolBar.addSeparator();

        JButton copyButton = createToolbarButton("/copy.png");
        copyButton.setToolTipText("Copiar");
        toolBar.add(copyButton);
        copyButton.addActionListener(_ -> copyItem.doClick());

        JButton pasteButton = createToolbarButton("/paste.png");
        pasteButton.setToolTipText("Colar");
        toolBar.add(pasteButton);
        pasteButton.addActionListener(_ -> pasteItem.doClick());

        JButton cutButton = createToolbarButton("/cut.png");
        cutButton.setToolTipText("Recortar");
        toolBar.add(cutButton);
        cutButton.addActionListener(_ -> cutItem.doClick());

        toolBar.addSeparator();

        JButton compileButton = createToolbarButton("/compile.png");
        compileButton.setToolTipText("Compilar");
        toolBar.add(compileButton);

        JButton runButton = createToolbarButton("/play.png");
        runButton.setToolTipText("Executar");
        toolBar.add(runButton);

        add(toolBar, BorderLayout.NORTH);

        compileButton.addActionListener(_ -> compileItem.doClick());
        runButton.addActionListener(_ -> runItem.doClick());

        compileMenu.add(compileItem);
        compileMenu.add(runItem);


        compileItem.addActionListener(_ -> compilar(modeloTabela));

        runItem.addActionListener(_ -> {
            try {
                errosSintaticos.setLength(0);
                errosLexicos.setLength(0);
                errosSemanticos = "";
                compilar(modeloTabela);
                if (!errosLexicos.isEmpty()) {
                    //JOptionPane.showMessageDialog(null, "Erro léxico encontrado:\n" + errosLexicos, "Erro de Execução", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!errosSintaticos.isEmpty()) {
                    //JOptionPane.showMessageDialog(null, "Erro sintático encontrado:\n" + errosSintaticos, "Erro de Execução", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!errosSemanticos.isEmpty()) {
                    //JOptionPane.showMessageDialog(null, "Erro semântico encontrado:\n" + errosSemanticos, "Erro de Execução", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TerminalExecucao terminal = new TerminalExecucao(codigoIntermediario);
                terminal.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro durante a execução: " + ex.getMessage(), "Erro de Execução", JOptionPane.ERROR_MESSAGE);
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(compileMenu);
        setJMenuBar(menuBar);
        exitItem.addActionListener(_ -> exitApplication());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private void compilar(DefaultTableModel modeloTabela) {
        StringBuilder output = new StringBuilder();
        List<Instrucao> outputSemantico;
        String entrada = codeArea.getText();
        Compilador analisadorLexico = new Compilador(new StringReader(entrada));
        codigoIntermediario = null;

        try {
            Token token;
            // Analisador léxico
            while (true) {
                try {
                    token = analisadorLexico.getNextToken();
                    errosLexicos.setLength(0); // Limpa os erros anteriores
                    if (analisadorLexico.token_source.countLexError > 0) {
                        errosLexicos.append(analisadorLexico.token_source.errosLexicos);
                        output.append(analisadorLexico.token_source.errosLexicos);
                        analisadorLexico.token_source.errosLexicos = new StringBuilder();
                    }
                    if (token.kind == CompiladorConstants.EOF)
                        break;

                } catch (TokenMgrError lexicalError) {
                    output.append("Erros Léxicos encontrados: ").append(lexicalError.getMessage()).append("\n");
                }
            }
            analisadorLexico.token_source.errosLexicos = new StringBuilder();
            analisadorLexico.token_source.checkUnclosedMultilineComment();
            errosLexicos.append(analisadorLexico.token_source.errosLexicos);
            output.append(analisadorLexico.token_source.errosLexicos);
            messageArea.setText(output.toString());

            // Analisador Sintático
            if (analisadorLexico.token_source.countLexError == 0) {
                output.append("Nenhum erro léxico encontrado, prosseguindo para o analisador sintático").append("\n");
                messageArea.setText(output.toString());

                Compilador analisadorSintatico = new Compilador(new StringReader(entrada));
                AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
                analisadorSintatico.programa(analisadorSemantico);

                int qtdErrosSintaticos = analisadorSintatico.getParseErrorCount();

                // Analisador Semântico
                errosSintaticos.setLength(0);
                if (qtdErrosSintaticos != 0) {
                    output.append("Erros Sintáticos encontrados: ").append(qtdErrosSintaticos).append("\n\n");
                    output.append(analisadorSintatico.errosSintaticos);
                    errosSintaticos.append(analisadorSintatico.errosSintaticos);
                } else {
                    output.append("Nenhum erro sintático encontrado, prosseguindo para o analisador semântico").append("\n");
                    errosSemanticos = analisadorSemantico.obterErrosSemanticos();

                    if (!errosSemanticos.isEmpty()) {
                        output.append("Erros Semânticos encontrados: ").append(analisadorSemantico.getSemanticErrorCount()).append("\n\n").append(errosSemanticos).append("\n");
                        modeloTabela.setRowCount(0);
                    } else {
                        outputSemantico = analisadorSemantico.getAreaInstrucoes();
                        output.append("Nenhum erro semântico encontrado. Compilação concluída com sucesso!\n");
                        exibirCodigoIntermediario(outputSemantico);
                        codigoIntermediario = outputSemantico;
                    }
                }
                messageArea.setText(output.toString());
            }
        } catch (Exception error) {
            output.append("Erro durante a compilação: ").append(error.getMessage());
            messageArea.setText(output.toString());
        }
    }

    private void exibirCodigoIntermediario(List<Instrucao> instrucoes) {
        DefaultTableModel modeloTabela = (DefaultTableModel) codigoIntermediarioTable.getModel();
        modeloTabela.setRowCount(0);

        for (Instrucao instrucao : instrucoes) {
            modeloTabela.addRow(new Object[]{
                    instrucao.getNumero(),
                    instrucao.getCodigo(),
                    instrucao.getParametro()
            });
        }
    }

    private void addLineNumbering(JScrollPane scrollPane) {
        JTextArea lines = new JTextArea("1");
        lines.setBackground(Color.LIGHT_GRAY);
        lines.setForeground(Color.RED);
        lines.setEditable(false);

        lines.setFont(codeArea.getFont());
        lines.setMargin(new Insets(0, 5, 0, 5));

        int maxLineNumberDigits = 5;
        int width = lines.getFontMetrics(lines.getFont()).charWidth('0') * maxLineNumberDigits;
        lines.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));

        codeArea.getDocument().addDocumentListener((SimpleDocumentListener) () -> updateLineNumbers(lines));
        scrollPane.setRowHeaderView(lines);
    }

    private void updateLineNumbers(JTextArea lines) {
        StringBuilder text = new StringBuilder("1");
        int caretPosition = codeArea.getDocument().getLength();
        Element root = codeArea.getDocument().getDefaultRootElement();

        for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
            text.append(System.lineSeparator()).append(i);
        }
        lines.setText(text.toString());
    }

    private void updateWindowTitle(File file) {
        String title = file != null ? file.getName() : "Sem titulo";
        setTitle("Compilador - " + title);
    }

    private void exitApplication() {
        if (isEdited) {
            Object[] options = {"Sim", "Não", "Cancelar"};
            int option = JOptionPane.showOptionDialog(this, "Deseja salvar as alterações antes de sair?", "Salvar Arquivo", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            switch (option) {
                case JOptionPane.YES_OPTION:
                    if (currentFile != null && currentFile.exists()) {
                        saveFile();
                    } else {
                        saveAsFile();
                    }
                    dispose();
                    break;
                case JOptionPane.NO_OPTION:
                    dispose();
                    break;
                default:
                    break;
            }
        } else {
            dispose();
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try (FileWriter fileWriter = new FileWriter(currentFile)) {
                fileWriter.write(codeArea.getText());
                updateWindowTitle(currentFile);
                isEdited = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            saveAsFile();
        }
    }

    private void saveAsFile() {
        JFileChooser fileChooser = new JFileChooser(lastDirectory);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto (*.txt)", "txt"));
        fileChooser.setSelectedFile(new File("novo_arquivo.txt"));

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            lastDirectory = file.getParentFile();

            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(codeArea.getText());
                currentFile = file;
                updateWindowTitle(file);
                isEdited = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*private String encontraCategoria(Token token) {
        String categoria = "";

        if (token.kind >= 11 && token.kind <= 28) categoria = "Palavra reservada";
        else if (token.kind < 52) categoria = "Símbolo especial";
        else if (token.kind == 52) categoria = "Identificador";
        else if (token.kind == 53) categoria = "Constante numérica real";
        else if (token.kind == 54) categoria = "Constante numérica inteira";
        else if (token.kind == 55) categoria = "Constante literal";

        return categoria;
    }*/

    private JButton createToolbarButton(String iconPath) {
        URL iconUrl = getClass().getResource(iconPath);

        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image img = icon.getImage();

            Dimension buttonSize = new Dimension(24, 24); // Tamanho padrão do botão
            Image scaledImg = img.getScaledInstance(buttonSize.width, buttonSize.height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImg);

            JButton button = new JButton(icon);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(false);

            return button;
        } else {
            System.err.println("Ícone não encontrado: " + iconPath);
            return new JButton();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompiladorGUI frame = new CompiladorGUI();
            frame.setVisible(true);
        });
    }

    private interface SimpleDocumentListener extends DocumentListener {
        void update();

        @Override
        default void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        default void changedUpdate(DocumentEvent e) {
            update();
        }
    }
}
