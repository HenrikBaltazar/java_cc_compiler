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
    StringBuilder outputLog = new StringBuilder();
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

    public void buildCode() {
        outputLog.append(parent.getTextOutput().getText()+"\n\n");
        outputLog.append("--- Compilacao Iniciada em: ").append(LocalDateTime.now().format(formatador)).append(" ---\n\n");
        String sourceCode = parent.getTextInput().getText();
        specialTokensList = new ArrayList<>();
        try {
            SimpleCharStream inputReader = new SimpleCharStream(Reader.of(sourceCode));
            Language2025x2TokenManager tokenManager = new Language2025x2TokenManager(inputReader);
            Token token;
            while (true) {
                token = tokenManager.getNextToken();
                if (token.specialToken != null) {
                    collectSpecialTokens(token.specialToken);
                }
                if (token.kind == Language2025x2Constants.EOF) {
                    break;
                }
                if (token.kind == Language2025x2Constants.EOF) {
                    break;
                }

                String category = Language2025x2Constants.tokenImage[token.kind].replace("\"", "").replace(" ", "").replace("\n", "");
                category = fixCategory(category);
                String tokenInfo = String.format("Lexema: %-15s | Linha: %-3d | Coluna: %-3d | Código: %s | Categoria: %s \n",
                        token.image.replace(" ", "").replace("\n", ""),
                        token.beginLine,
                        token.beginColumn,
                        token.kind < 10 ? token.kind + " " : token.kind,
                        category
                );
                outputLog.append(tokenInfo);
            }

            outputLog.append("\n--- Análise Léxica Concluída com Sucesso ---\n");
            outputLog.append(Language2025x2.run(Reader.of(sourceCode)));
            printSpecialTokens();
        } catch (TokenMgrError e) {
            outputLog.append("\n--- ERRO LÉXICO INESPERADO ---\n");
            outputLog.append(e.toString());
        } catch (Exception e) {
            outputLog.append("\n--- ERRO INTERNO INESPERADO ---\n");
            outputLog.append(e.toString());
        }
        parent.getTextOutput().setText(outputLog.toString());
        outputLog.setLength(0);
    }
    /**
     * Coleta os special tokens. A lista encadeada 'specialToken' aponta para trás,
     * então precisamos iterar e adicionar na nossa lista.
     */
    private void collectSpecialTokens(Token initialSpecialToken) {
        Token current = initialSpecialToken;
        while (current != null) {
            specialTokensList.add(current);
            current = current.specialToken;
        }
    }

    /**
     * Imprime os special tokens coletados formatados.
     */
    private void printSpecialTokens() {
        if (!specialTokensList.isEmpty()) {
            outputLog.append("\n--- Special Tokens Encontrados (Comentários) ---\n");

            // Reverte a lista para imprimir na ordem cronológica de aparição no código
            Collections.reverse(specialTokensList);

            for (Token st : specialTokensList) {
                String category = Language2025x2Constants.tokenImage[st.kind].replace("\"", "");
                String content = st.image.replace("\n", " ").replace("\r", "").trim(); // Limpa a formatação
                outputLog.append(String.format("Tipo: %-20s | Linha: %-3d | Conteúdo: \"%s\"\n",
                        category, st.beginLine, content));
            }
        }
    }

}