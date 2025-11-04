package org.example.Actions;

import org.example.JavaCC.*;
import org.example.ui.Interface;

import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        List<Dicionario> dicionario = List.of(
                new Dicionario("identifier", "um <b>identificador de variável</b> (ex.: myVar)")
        );

        for (Dicionario d : dicionario) {
            int idx = report.indexOf(d.recebido);
            if (idx != -1) {
                report.replace(idx, idx + d.recebido.length(), d.substituto);
            }
        }
        return report;
    }

    public void buildCode() {
        StringBuilder css = new StringBuilder(
                ".title{color:red;text-decoration:underline;}"+
                "body{font-family:Arial,sans-serif;font-size:x-large;}"
        );
        StringBuilder outputLog = new StringBuilder(
                "<html>" +
                        "<head><style>"+css+"</style></head>" +
                        "<body>"
        );
        outputLog.append("<p class='title'>--- Análise iniciada em: ").append(LocalDateTime.now().format(formatador)).append(" ---</p></br>");
        String sourceCode = parent.getTextInput().getText();
        specialTokensList = new ArrayList<>();
        boolean lexicalApproved = false;
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
                    String lexicalReport = String.format("Erro Lexico na linha %-3d, coluna %-3d: Encontrado '%-15', mas esperava [%s]\n",
                            token.beginLine,
                            token.beginColumn,
                            token.image.replace(" ", "").replace("\n", ""),
                            category
                            );
                    outputLog.append(lexicalReport);
                }
            }
            System.out.println("Nenhum erro lexico encontrado, seguindo analise sintatica...");
            lexicalApproved = true;

        } catch (TokenMgrError e) {
            outputLog.append("\n--- ERRO LÉXICO ---\n");
            outputLog.append(e.getMessage());

            parent.getTextOutput().setText(outputLog.toString());
            outputLog.setLength(0);
            return;
        }
        Language2025x2 parser = null;
        if(lexicalApproved) {
            try {
                parser = new Language2025x2(new StringReader(sourceCode));
                parser.programa();
                if (parser != null && parser.errosSintaticos.length() > 0) {
                    outputLog.append(sintaticBreakout(parser.errosSintaticos));
                }
            } catch (ParseException e) {
                outputLog.append("\n--- ERRO SINTÁTICO ---\n");
                if (parser != null && parser.errosSintaticos.length() > 0) {
                    outputLog.append(parser.errosSintaticos);
                }
            } catch (TokenMgrError e) {
                outputLog.append("\n--- ERRO LÉXICO (durante a análise sintática) ---\n");
                outputLog.append(e.getMessage());
            }
        }
        outputLog.append(
                "</body>" +
                        "</html>"
        );
        System.out.println(outputLog.toString());
        parent.getTextOutput().setText(outputLog.toString());
        outputLog.setLength(0);
    }

}