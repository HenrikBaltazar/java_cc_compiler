package org.example.Actions;

import org.example.JavaCC.*;
import org.example.ui.Interface;

import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Build {
    Interface parent;
    StringBuilder outputLog = new StringBuilder();
    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private List<String> RESERVED_WORDS = List.of("begin","define","start","end","set","num","real","text","flag","read","show","if","then","else","true","false");
    private List<String> SPECIAL_SYMBOLS = List.of("==", "!=", ";", "=", ":", ",", ".", "{", "}", "[", "]", "(", ")", "+", "-", ">>=", "*", "/", "%", "**", "%%", "<<", ">>","<<=", "!", "|" ,"&");
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
        } else if (category.equals("<INVALID_SYMBOL>")) {
            return "ERRO LÉXICO: símbolo inválido";
        } else if (category.equals("<ERROR_UNTERMINATED_STRING>")) {
           return "ERRO LÉXICO: constante literal não finalizada";
        }else if (category.equals("<TEXT>")) {
            return "CONSTANTE DE TEXTO";
        }
        return category;
    }

    public void buildCode() {
        outputLog.append(parent.getTextOutput().getText()+"\n\n");
        outputLog.append("--- Compilacao Iniciada em: ").append(LocalDateTime.now().format(formatador)).append(" ---\n\n");
        String sourceCode = parent.getTextInput().getText();

        try {
            SimpleCharStream inputReader = new SimpleCharStream(Reader.of(sourceCode));
            Language2025x2TokenManager tokenManager = new Language2025x2TokenManager(inputReader);
            Token token;
            while (true) {
                token = tokenManager.getNextToken();

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

}