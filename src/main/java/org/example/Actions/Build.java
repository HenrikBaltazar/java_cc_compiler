package org.example.Actions;

import org.example.JavaCC.*;
import org.example.ui.Interface;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Build {
    Interface parent;
    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final List<String> RESERVED_WORDS = List.of("begin","define","start","end","set","num","real","text","flag","read","show","if","then","else","true","false");
    private static final List<String> SPECIAL_SYMBOLS = List.of("==", "!=", ";", "=", ":", ",", ".", "{", "}", "[", "]", "(", ")", "+", "-", ">>=", "*", "/", "%", "**", "%%", "<<", ">>","<<=", "!", "|" ,"&");

    List<Token> specialTokensList;
    public Build() {
    }

    public void setParent(Interface parent) {
        this.parent = parent;
    }

    private String fixCategory(String category) {
        if (SPECIAL_SYMBOLS.contains(category)) {
            return "SÍMBOLO ESPECIAL";
        } else if (RESERVED_WORDS.contains(category)) {
            return "PALAVRA RESERVADA";
        } else if (category.equals("<IDENTIFIER>")) {
            return "IDENTIFICADOR";
        } else if (category.equals("<NUM>")) {
            return "CONSTANTE NUMERICA INTEIRA";
        } else if (category.equals("<REAL>")) {
            return "CONSTANTE NUMERICA REAL";
        } else if (category.equals("<ERROR_INVALID_SYMBOL>")) {
            return "ERRO LÉXICO: símbolo inválido";
        } else if (category.equals("<ERROR_UNTERMINATED_STRING>")) {
           return "ERRO LÉXICO: constante literal não finalizada";
        } else if (category.equals("<ERROR_IDENTIFIER_START_WITH_NUM>")) {
            return "ERRO LÉXICO: identificador inicia com numero";
        } else if (category.equals("<ERROR_UNTERMINATED_COMMENT>")) {
            return "ERRO LÉXICO: comentario de bloco não finalizado";
        }else if (category.equals("<TEXT>")) {
            return "CONSTANTE LITERAL";
        }else if (category.equals("<ERROR_REAL_TOO_MANY_DIGITS>") || category.equals("<ERROR_REAL_TOO_MANY_DIGITS2>") || category.equals("<ERROR_REAL_TOO_MANY_DIGITS3>")) {
            return "ERRO LÉXICO: constante real com muitas casas";
        }else if (category.equals("<ERROR_INVALID_REAL>")) {
            return "ERRO LÉXICO: constante real inválida";
        }else if (category.equals("<ERROR_NUMBER_TOO_MANY_DIGITS>")) {
            return "ERRO LÉXICO: constante numérica com muitas casas";
        }else if (category.equals("<ERROR_IDENTIFIER_WITH_MANY_NUMS>")) {
            return "ERRO LÉXICO: identificador com muitos números consecutivos";
        }else if (category.equals("<ERROR_IDENTIFIER_ENDS_WITH_NUM>")) {
            return "ERRO LÉXICO: identificador termina com número";
        }
        return category;
    }

    private StringBuilder sintaticBreakout(StringBuilder report){
        record Dicionario(String recebido, String substituto) {}
        //new Dicionario("","" )
        List<Dicionario> dicionario = List.of(
                new Dicionario("identifier", "identificador de variável"),

                // Símbolos estruturais
                new Dicionario("@{", "abre chaves <i>{</i>"),
                new Dicionario("@}", "fecha chaves <i>}</i>"),
                new Dicionario("@[", "abre colchetes <i>[</i>"),
                new Dicionario("@]", "fecha colchetes <i>]</i>"),
                new Dicionario("@(", "abre parênteses <i>(</i>"),
                new Dicionario("@)", "fecha parênteses <i>)</i>"),
                new Dicionario("@espaco", "espaço vazio ' '"),
                new Dicionario("@:", "dois pontos <i>:</i>"),
                new Dicionario("@;", "ponto e vírgula <i>;</i>"),

                // Palavras reservadas
                new Dicionario("@begin", " <i>begin</i>"),
                new Dicionario("@define", " <i>define</i>"),
                new Dicionario("@start", " <i>start</i>"),
                new Dicionario("@end", " <i>end</i>"),
                new Dicionario("@set", " <i>set</i>"),
                new Dicionario("@num", " <i>num</i>"),
                new Dicionario("@real", " <i>real</i>"),
                new Dicionario("@text", " <i>text</i>"),
                new Dicionario("@flag", " <i>flag</i>"),
                new Dicionario("@read", " <i>read</i>"),
                new Dicionario("@show", " <i>show</i>"),
                new Dicionario("@if", " <i>if</i>"),
                new Dicionario("@then", " <i>then</i>"),
                new Dicionario("@else", " <i>else</i>"),
                new Dicionario("@true", " <i>true</i>"),
                new Dicionario("@false", " <i>false</i>"),
                new Dicionario("@loop", " <i>loop</i>"),

                // Símbolos especiais (HTML seguro)
                new Dicionario("@==", "operador de igualdade <i>==</i>"),
                new Dicionario("@!=", "operador de diferença <i>!=</i>"),
                new Dicionario("@;", "ponto e vírgula <i>;</i>"),
                new Dicionario("@=", "atribuição <i>=</i>"),
                new Dicionario("@:", "dois pontos <i>:</i>"),
                new Dicionario("@,", "vírgula <i>,</i>"),
                new Dicionario("@.", "ponto <i>.</i>"),
                new Dicionario("@{", "abre chaves <i>{</i>"),
                new Dicionario("@}", "fecha chaves <i>}</i>"),
                new Dicionario("@[", "abre colchetes <i>[</i>"),
                new Dicionario("@]", "fecha colchetes <i>]</i>"),
                new Dicionario("@(", "abre parênteses <i>(</i>"),
                new Dicionario("@)", "fecha parênteses <i>)</i>"),
                new Dicionario("@+", "soma <i>+</i>"),
                new Dicionario("@-", "subtração <i>-</i>"),
                new Dicionario("@*", "multiplicação <i>*</i>"),
                new Dicionario("@/", "divisão <i>/</i>"),
                new Dicionario("@%", "resto <i>%</i>"),
                new Dicionario("@**", "exponenciação <i>**</i>"),
                new Dicionario("@%%", "resto inteiro <i>%%</i>"),
                new Dicionario("@&", "e lógico <i>&amp;</i>"),              // HTML seguro
                new Dicionario("@|", "ou lógico <i>|</i>"),
                new Dicionario("@!", "negação lógica <i>!</i>"),
                new Dicionario("@<", "menor que <i>&lt;</i>"),             // HTML seguro
                new Dicionario("@>", "maior que <i>&gt;</i>"),             // HTML seguro
                new Dicionario("@<<", "deslocamento à esquerda <i>&lt;&lt;</i>"),  // HTML seguro
                new Dicionario("@>>", "deslocamento à direita <i>&gt;&gt;</i>"),   // HTML seguro
                new Dicionario("@<<=", "atribuição com deslocamento à esquerda <i>&lt;&lt;=</i>"), // HTML seguro
                new Dicionario("@>>=", "atribuição com deslocamento à direita <i>&gt;&gt;=</i>"),  // HTML seguro

                // Último — remover marcações
                new Dicionario("@", "")
        );


        int divSearchIndex = 0;

        for (Dicionario d : dicionario) {
            int idx = report.indexOf(d.recebido);
            while (idx != -1) {

                // Substituição do dicionário
                report.replace(idx, idx + d.recebido.length(), d.substituto);

                // Verifica se é uma palavra reservada e se ainda não foi adicionada dentro da div
                if (RESERVED_WORDS.contains(d.recebido.replace("@", ""))) {
                    // Procura o div que contém esta ocorrência
                    int divStart = report.lastIndexOf("<div class='log-entry'>", idx);
                    int divEnd = report.indexOf("</div>", idx);
                    if (divStart != -1 && divEnd != -1) {
                        String divContent = report.substring(divStart, divEnd);
                        if (!divContent.contains("palavra reservada:")) {
                            // Inserir ": " após a primeira ocorrência dentro da div
                            int spanStart = report.indexOf("<span class='expected'>", divStart);
                            if (spanStart != -1 && spanStart < divEnd) {
                                spanStart += "<span class='expected'>".length();
                                report.insert(spanStart, "palavra reservada: ");
                                divEnd += "palavra reservada: ".length(); // ajusta o final da div
                            }
                        }
                    }
                }

                idx = report.indexOf(d.recebido, idx + d.substituto.length());
            }
        }

// Troca a última vírgula por " ou"
        int lastComma = report.lastIndexOf(",");
        if (lastComma != -1) {
            report.replace(lastComma, lastComma + 1, " ou");
        }

        return report;
    }

    public void buildCode() {
        StringBuilder css = new StringBuilder(
                """
                body {
                     background-color: #fafafa;
                     color: #222;
                     font-family: 'Consolas', 'Courier New', monospace;
                     margin: 20px;
                     font-size: 18px;
                 }
                 h1 {
                     font-size: 24px;
                     color: #444;
                     margin-bottom: 10px;
                     border-bottom: 1px solid #ddd;
                     padding-bottom: 5px;
                 }
                 .success {
                     color: #155724;
                     background-color: #d4edda;
                     border: 1px solid #c3e6cb;
                     border-radius: 8px;
                     padding: 10px 14px;
                     font-family: 'Segoe UI', Arial, sans-serif;
                     font-size: 16px;
                     font-weight: 600;
                     text-align: center;
                     margin: 12px 0;
                     box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                 }
                 .error {
                     color: #721c24;
                     background-color: #f8d7da;
                     border: 1px solid #f5c6cb;
                     border-radius: 8px;
                     padding: 10px 14px;
                     font-family: 'Segoe UI', Arial, sans-serif;
                     font-size: 16px;
                     font-weight: 700;
                     text-align: center;
                     margin: 12px 0;
                     box-shadow: 0 1px 3px rgba(0,0,0,0.06);
                 }
                 .log-entry {
                     padding: 6px 18px;
                     border-left: 3px solid #e74c3c;
                     background-color: #fff;
                     margin: 5px 0;
                     border-radius: 4px;
                 }
                 .line-info {
                     color: #555;
                 }
                 .found {
                     color: #c0392b;
                     font-weight: bold;
                 }
                 .expected {
                     color: #2980b9;
                     font-weight: bold;
                 }
                """
        );
        StringBuilder outputLog = new StringBuilder(
                "<html>" +
                        "<head><style>"+css+"</style></head>" +
                        "<body>"
        );
        outputLog.append("<h1>Análise iniciada em: ").append(LocalDateTime.now().format(formatador)).append("</h1></br>");
        String sourceCode = parent.getTextInput().getText();
        specialTokensList = new ArrayList<>();
        boolean lexicalApproved = true, sintaticApproved = false, semanticApproved = false;
        StringBuilder lexicalLog = new StringBuilder();
        ArrayList<ArrayList<String>> codigIn = new ArrayList<>();
        try {
            SimpleCharStream inputReader = new SimpleCharStream(Reader.of(sourceCode));
            Language2025x2TokenManager tokenManager = new Language2025x2TokenManager(inputReader);
            Token token;
            while (true) {
                token = tokenManager.getNextToken();
                if (token.kind == Language2025x2Constants.EOF) {
                    break;
                }
                if (token.kind == Language2025x2Constants.EOF) {
                    break;
                }

                String category = Language2025x2Constants.tokenImage[token.kind].replace("\"", "").replace(" ", "").replace("\n", "");
                category = fixCategory(category);
                String tokenInfo = String.format("Lexema: %-15s | Linha: %-3d | Coluna: %-3d | Código: %s | Categoria: %s",
                        token.image.replace(" ", "").replace("\n", ""),
                        token.beginLine,
                        token.beginColumn,
                        token.kind < 10 ? token.kind + " " : token.kind,
                        category
                );
                System.out.println(tokenInfo);
                if(category.contains("ERRO")) {
                    String lexicalReport = String.format("<div class='log-entry'><span class='line-info'>Erro Lexico na linha %-3d, coluna %-3d:</span><br>  Encontrado  <span class='expected'>%s</span> <b>→</b> <span class='found'>%-15s</span></div>\n",
                            token.beginLine,
                            token.beginColumn,
                            category.replace("ERRO LÉXICO: ", ""),
                            token.image.replace(" ", "").replace("\n", "")
                            );
                    lexicalLog.append(lexicalReport);
                    lexicalApproved = false;
                }
            }
        } catch (TokenMgrError e) {
            lexicalLog.append(e.getMessage());
            lexicalApproved = false;
        }

        if(lexicalApproved) {
            System.out.println("Nenhum erro lexico encontrado, seguindo analise sintatica...");
        }else{
            outputLog.append("<h1 class='error'>Falha ao compilar</h1></br>");
            outputLog.append(lexicalLog);
        }

        Language2025x2 parser = null;
        if(lexicalApproved) {
            try {
                parser = new Language2025x2(new StringReader(sourceCode));
                parser.programa();
                if (parser != null && parser.errosSintaticos.length() > 0) {
                    outputLog.append("<h1 class='error'>Falha ao compilar</h1></br>");
                    outputLog.append(sintaticBreakout(parser.errosSintaticos));
                }else{
                    System.out.println("Nenhum erro sintatico encontrado, seguindo analise semantica...");
                    sintaticApproved = true;
                }
            } catch (ParseException e) {
                outputLog.append("<h1 class='error'>Falha ao compilar</h1></br>");
                outputLog.append("\n--- ERRO SINTÁTICO ---\n");
                if (parser != null && parser.errosSintaticos.length() > 0) {
                    outputLog.append(parser.errosSintaticos);
                }
            } catch (TokenMgrError e) {
                outputLog.append("<h1 class='error'>Falha ao compilar</h1></br>");
                outputLog.append("\n--- ERRO LÉXICO (durante a análise sintática) ---\n");
                outputLog.append(e.getMessage());
            }
        }

        if(lexicalApproved && sintaticApproved) {
            codigIn = parser.semantico.codigIn;
            if (codigIn.size() > 0) {
                semanticApproved = true;
            }else{
                outputLog.append("<h1 class='error'>Falha ao gerar o código intermediário</h1></br>");
                semanticApproved = false;
            }
        }

        if(lexicalApproved && sintaticApproved && semanticApproved) {
            outputLog.append("<h1 class='success'>Programa compilado com sucesso</h1></br>");

            ArrayList<String> linhas = new ArrayList<>(List.of("Número", "Operação", "Parâmetro"));

            showMatrixFrame(codigIn, linhas,  "Código intermediário");
        }

        outputLog.append(
                "</body>" +
                        "</html>"
        );
        parent.getTextOutput().setText(outputLog.toString());
        System.out.println(outputLog.toString());
        outputLog.setLength(0);
    }

    private void showMatrixFrame(ArrayList<ArrayList<String>> matrix,
                                 ArrayList<String> colLabels,
                                 String title) {

        int n = matrix.size();
        int m = colLabels.size();

        Object[][] data = new Object[n][m];
        for (int i = 0; i < n; i++) {
            ArrayList<String> row = matrix.get(i);
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
    }


}