import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnalisadorSemantico {
    private String contexto;
    private int vt;
    private int vp;
    private int tipo;
    private final Stack<Integer> pilhaDesvios;
    private int ponteiro;
    private final List<Instrucao> areaInstrucoes;
    private final List<Simbolos> tabelaSimbolos;
    private final List<String> errosSemanticos;

    public AnalisadorSemantico() {
        this.contexto = "";
        this.vt = 0;
        this.vp = 0;
        this.tipo = 0;
        this.pilhaDesvios = new Stack<>();
        this.areaInstrucoes = new ArrayList<>();
        this.ponteiro = 1;
        this.tabelaSimbolos = new ArrayList<>();
        this.errosSemanticos = new ArrayList<>();
    }

    public int getSemanticErrorCount() {
        return this.errosSemanticos.size();
    }
    public List<Instrucao> getAreaInstrucoes() {
        return areaInstrucoes;
    }

    public void incrementaPonteiro() {
        this.ponteiro++;
    }

    public void incrementaVT() {
        this.vt++;
    }

    public void incrementaVP() {
        this.vp++;
    }

    public void gerarInstrucao(int numero, String codigo, String parametro) {
        areaInstrucoes.add(new Instrucao(numero, codigo, parametro));
    }

    public void inserirTabelaSimbolos(String nome, int categoria, String atributo) {
        tabelaSimbolos.add(new Simbolos(nome, categoria, atributo));
    }

    // ação 1
    public void reconhceFimPrograma() {
        gerarInstrucao(this.ponteiro, "STP", "0");
    }

    // ação 2
    public void reconheceIdentificadorPrograma(Token token) {
        String identificador = token.image;
        inserirTabelaSimbolos(identificador, 0, "-");
    }

    // ação 3
    public void reconhceConst() {
        this.contexto = "constante";
    }

    // ação 4
    public void reconheceFimDeclaracaoConstVar() {
        String vp = String.valueOf(this.vp);

        switch (this.tipo) {
            case 1, 5 -> {
                gerarInstrucao(this.ponteiro, "ALI", vp);
                incrementaPonteiro();
            }
            case 2, 6 -> {
                gerarInstrucao(this.ponteiro, "ALR", vp);
                incrementaPonteiro();
            }
            case 3, 7 -> {
                gerarInstrucao(this.ponteiro, "ALS", vp);
                incrementaPonteiro();
            }
            case 4 -> {
                gerarInstrucao(this.ponteiro, "ALB", vp);
                incrementaPonteiro();
            }
        }

        if (this.tipo == 1 || this.tipo == 2 || this.tipo == 3 || this.tipo == 4)
            this.vp = 0;
    }

    // ação 5
    public void reconhceValorDeclaracaoConst(Token token) {
        String valor = token.image;
        System.out.println("5 - Tipo:" + this.tipo);
        switch (this.tipo) {
            case 5 -> {
                gerarInstrucao(this.ponteiro, "LDI", valor);
                incrementaPonteiro();
            }
            case 6 -> {
                gerarInstrucao(this.ponteiro, "LDR", valor);
                incrementaPonteiro();
            }
            case 7 -> {
                gerarInstrucao(this.ponteiro, "LDS", valor);
                incrementaPonteiro();
            }
        }
        String vp = String.valueOf(this.vp);
        gerarInstrucao(this.ponteiro, "STC", vp);
        incrementaPonteiro();
        this.vp = 0;
    }

    // ação 6
    public void reconheceVar() {
        this.contexto = "variável";
    }

    // ação 7
    public void reconheceInt() {
        if (this.contexto.equals("variável")) {
            this.tipo = 1;
        } else this.tipo = 5;
        //System.out.println("Ação 7 foi chamada - " + this.tipo);
    }

    // ação 8
    public void reconheceReal() {
        if (this.contexto.equals("variável")) {
            this.tipo = 2;
        } else this.tipo = 6;
        //System.out.println("Ação 8 foi chamada - " + this.tipo);
    }

    // ação 9
    public void reconheceChar() {
        if (this.contexto.equals("variável")) {
            this.tipo = 3;
        } else this.tipo = 7;
        //System.out.println("Ação 9 foi chamada - " + this.tipo);
    }

    // ação 10
    public void reconheceBool(int linha, int coluna) {
        if (this.contexto.equals("variável")) {
            this.tipo = 4;
        } else this.errosSemanticos.add("Tipo inválido para constantes. Linha: " + linha + ", Coluna: " + coluna);
        //System.out.println("Ação 10 foi chamada - " + this.tipo);
    }

    // ação 11
    public void reconheceIdentificador(Token token) {
        int linha = token.beginLine;
        int coluna = token.beginColumn;
        int tipoSimboloAtual = 0;
        String identificador = token.image;

        boolean encontrou = false;

        switch (this.contexto) {
            case "constante", "variável":
                for (Simbolos simbolos: this.tabelaSimbolos) {
                    if (simbolos.getNome().equals(identificador)) {
                        encontrou = true;
                        break;
                    }
                }
                if (encontrou) {
                    this.errosSemanticos.add("Identificador já declarado. Linha: " + linha + ", Coluna: " + coluna);
                }
                else {
                    incrementaVT();
                    incrementaVP();
                    inserirTabelaSimbolos(identificador, this.tipo, String.valueOf(this.vt));
                    //System.out.println("Adicionou " + identificador + " do tipo: " + this.tipo + "na tabela de símbolos");
                }
                break;
            case "entrada de dados":
                //System.out.println("Entrou aqui - ação 11 - identificador atual: " + identificador);
                //System.out.println("Vai começar a varrer a tabela de simbolos: ");
                for (Simbolos simbolos: this.tabelaSimbolos) {
                    //System.out.println("Símbolo atual: " + simbolos.getNome());
                    if (simbolos.getNome().equals(identificador)) {
                        //System.out.println("Encontrou!");
                        tipoSimboloAtual = simbolos.getCategoria();
                        encontrou = true;
                        break;
                    }
                }
                if (encontrou) {
                    //System.out.println("Entrou aqui 1: Identificador " + identificador + " do tipo: " + this.tipo);
                    if (tipoSimboloAtual == 1 || tipoSimboloAtual == 2 || tipoSimboloAtual == 3 || tipoSimboloAtual == 4) {
                        for (Simbolos simbolos: this.tabelaSimbolos) {
                            if (simbolos.getNome().equals(identificador)) {
                                int categoria = simbolos.getCategoria();
                                String categoriaString = String.valueOf(categoria);
                                gerarInstrucao(this.ponteiro, "REA", categoriaString);
                                //System.out.println("Categoria: " + categoriaString);
                                incrementaPonteiro();
                                String atributo = simbolos.getAtributo();
                                gerarInstrucao(this.ponteiro, "STR", atributo);
                                //System.out.println("Atributo: " + atributo);
                                incrementaPonteiro();
                            }
                        }
                    } else {
                        this.errosSemanticos.add("11 - Identificador de programa ou de constante. Linha: " + linha + ", Coluna: " + coluna);
                        //System.out.println("11 Erro encontrado com o identificador: " + identificador);
                    }
                }
                else this.errosSemanticos.add("Identificador não declarado. Linha: " + linha + ", Coluna: " + coluna);
                break;
        }
        //System.out.println("Ação 11 foi chamada - " + identificador);
    }

    // Ação 12
    public void reconheceIdentificadorAtribuicao(Token token) {
        int linha = token.beginLine;
        int coluna = token.beginColumn;
        String identificador = token.image;
        int tipoSimboloAtual = 0;

        boolean encontrou = false;

        for (Simbolos simbolos: this.tabelaSimbolos) {
            if (simbolos.getNome().equals(identificador)) {
                tipoSimboloAtual = simbolos.getCategoria();
                encontrou = true;
                break;
            }
        }
        if (encontrou) {
            if (tipoSimboloAtual == 1 || tipoSimboloAtual == 2 || tipoSimboloAtual == 3 || tipoSimboloAtual == 4) {
                for (Simbolos simbolos: this.tabelaSimbolos) {
                    if (simbolos.getNome().equals(identificador)) {
                        String atributo = simbolos.getAtributo();
                        gerarInstrucao(this.ponteiro, "STR", atributo);
                        //System.out.println("Atributo: " + atributo);
                        incrementaPonteiro();
                    }
                }
            } else this.errosSemanticos.add("12 - Identificador de programa ou de constante. Linha: " + linha + ", Coluna: " + coluna);
        } else this.errosSemanticos.add("Identificador não declarado. Linha: " + linha + ", Coluna: " + coluna);
    }

    // Ação 13
    public void reconheceGet() {
        this.contexto = "entrada de dados";
        //System.out.println("Ação 13 foi chamada - " + this.contexto);
    }

    // Ação 14
    public void reconheceMensagemSaidaDados() {
        gerarInstrucao(this.ponteiro, "WRT", "0");
        incrementaPonteiro();
        //System.out.println("Ação 14 foi chamada - " + this.ponteiro);
    }

    // ação 15: reconhecimento de identificador em comando de saída ou em expressão
    public void reconhecerIdentificadorSaidaOuExpressao(Token token) {
        int linha = token.beginLine;
        int coluna = token.beginColumn;
        String identificador = token.image;

        boolean encontrou = false;
        for (Simbolos simbolos: this.tabelaSimbolos) {
            if (simbolos.getNome().equals(identificador)) {
                encontrou = true;
                break;
            }
        }
        if (encontrou) {
            if (this.tipo == 1 || this.tipo == 2 || this.tipo == 3 || this.tipo == 4 || this.tipo == 5 || this.tipo == 6 || this.tipo == 7) {
                for (Simbolos simbolos: this.tabelaSimbolos) {
                    if (simbolos.getNome().equals(identificador)) {
                        String atributo = simbolos.getAtributo();
                        gerarInstrucao(this.ponteiro, "LDV", atributo);
                        //System.out.println("Atributo: " + atributo);
                        incrementaPonteiro();
                    }
                }
            } else this.errosSemanticos.add("Identificador de programa. Linha: " + linha + ", Coluna: " + coluna);
        } else this.errosSemanticos.add("Identificador não declarado. Linha: " + linha + ", Coluna: " + coluna);
    }

    // ação 16: reconhecimento de constante inteira em comando de saída ou em expressão
    public void reconhecerConstanteInteira(Token token) {
        String constante = token.image;
        gerarInstrucao(this.ponteiro, "LDI", constante);
        //System.out.println("Constante: " + constante);
        incrementaPonteiro();
        //System.out.println("Ação 16 foi chamada - Constante inteira: " + constante);
    }

    // ação 17: reconhecimento de constante real em comando de saída ou em expressão
    public void reconhecerConstanteReal(Token token) {
        String constante = token.image;
        gerarInstrucao(this.ponteiro, "LDR", constante);
        //System.out.println("Constante: " + constante);
        incrementaPonteiro();
        //System.out.println("Ação 17 foi chamada - Constante real: " + constante);
    }

    // ação 18: reconhecimento de constante literal em comando de saída ou em expressão
    public void reconhecerConstanteLiteral(Token token) {
        String constante = token.image;
        gerarInstrucao(this.ponteiro, "LDS", constante);
        //System.out.println("Constante: " + constante);
        incrementaPonteiro();
        //System.out.println("Ação 18 foi chamada - Constante literal: " + constante);
    }

    // ação 19: reconhecimento de constante lógica verdadeiro
    public void reconhecerConstanteLogicaVerdadeiro() {
        gerarInstrucao(this.ponteiro, "LDB", "TRUE");
        incrementaPonteiro();
        //System.out.println("Ação 19 foi chamada - Constante lógica: TRUE");
    }

    // ação 20: reconhecimento de constante lógica falso
    public void reconhecerConstanteLogicaFalso() {
        gerarInstrucao(this.ponteiro, "LDB", "FALSE");
        incrementaPonteiro();
        //System.out.println("Ação 20 foi chamada - Constante lógica: FALSE");
    }

    // ação 21: reconhecimento de expressão em comando de seleção
    public void reconhecerExpressaoComandoSelecao() {
        gerarInstrucao(this.ponteiro, "JMF", "?");
        incrementaPonteiro();
        this.pilhaDesvios.push(this.ponteiro - 1);
        //System.out.println("Ação 21 foi chamada - JMF gerado com endereço ?, ponteiro atualizado: " + this.ponteiro + ", endereço JMF empilhado: " + (this.ponteiro - 1));
    }

    // ação 22: reconhecimento do fim de comando de seleção
    public void reconhecerFimComandoSelecao() {
        int enderecoDesvio = this.pilhaDesvios.pop();
        Instrucao instrucao = this.areaInstrucoes.get(enderecoDesvio-1);
        String codigo = instrucao.getCodigo();
        //System.out.println("22 - Código pego da instrução: " + codigo);
        //System.out.println("22 - Endereço Desvio: " + enderecoDesvio);
        Instrucao instrucaoAtualizada = new Instrucao(enderecoDesvio, codigo, String.valueOf(this.ponteiro));
        //System.out.println("22 - Nova instrução gerada: " + instrucaoAtualizada);
        this.areaInstrucoes.set(enderecoDesvio-1, instrucaoAtualizada);
        //System.out.println("Ação 22 foi chamada - Endereço de desvio atualizado para: " + this.ponteiro);
    }

    // ação 23: reconhecimento da cláusula senão em comando de seleção
    public void reconhecerClausulaSenao() {
        int enderecoJMF = this.pilhaDesvios.pop();
        Instrucao instrucaoAtualizadaJMF = new Instrucao(enderecoJMF, "JMF", String.valueOf(this.ponteiro + 1));
        this.areaInstrucoes.set(enderecoJMF-1, instrucaoAtualizadaJMF);

        gerarInstrucao(this.ponteiro, "JMP", "?");
        incrementaPonteiro();
        this.pilhaDesvios.push(this.ponteiro - 1);
        //System.out.println("Ação 23 foi chamada - Endereço JMF atualizado para: " + (this.ponteiro + 1) + ", JMP gerado com endereço ?, ponteiro atualizado: " + this.ponteiro);
    }

    // ação 24: reconhecimento da palavra reservada while
    public void reconhecerInicioWhile() {
        pilhaDesvios.push(this.ponteiro);
        //System.out.println("Ação 24 foi chamada - Ponteiro empilhado: " + this.ponteiro);
    }

    // ação 25: reconhecimento de expressão em comando de repetição
    public void reconhecerExpressaoComandoRepeticao() {
        gerarInstrucao(this.ponteiro, "JMF", "?");
        incrementaPonteiro();
        this.pilhaDesvios.push(this.ponteiro - 1);
        //System.out.println("Ação 25 foi chamada - JMF gerado com endereço ?, ponteiro atualizado: " + this.ponteiro + ", endereço JMF empilhado: " + (this.ponteiro - 1));
    }

    // ação 26 reconhecimento do fim do comando de repetição
    public void reconhecerFimComandoRepeticao() {
        int enderecoJMF = this.pilhaDesvios.pop();
        Instrucao instrucaoAtualizada = new Instrucao(enderecoJMF, "JMF", String.valueOf(this.ponteiro + 1));

        this.areaInstrucoes.set(enderecoJMF-1, instrucaoAtualizada);

        int enderecoInicioWhile = this.pilhaDesvios.pop();
        gerarInstrucao(this.ponteiro, "JMP", String.valueOf(enderecoInicioWhile));
        // String enderecoInicioWhileString = String.valueOf(enderecoInicioWhile);
        // System.out.println("endereco: " + enderecoInicioWhileString);

        incrementaPonteiro();
        // System.out.println("Ação 26 foi chamada - Endereço JMF atualizado para: " + (this.ponteiro + 1) + ", JMP gerado para endereço: " + enderecoInicioWhile + ", ponteiro atualizado: " + this.ponteiro);
    }

    // ação 27: reconhecimento de operação relacional igual
    public void reconhecerOperacaoRelacionalIgual() {
        gerarInstrucao(this.ponteiro, "EQL", "0");
        incrementaPonteiro();
        //System.out.println("Ação 27 foi chamada - " + this.ponteiro);
    }

    // ação 28: reconhecimento de operação relacional diferente
    public void reconhecerOperacaoRelacionalDiferente() {
        gerarInstrucao(this.ponteiro, "DIF", "0");
        incrementaPonteiro();
        //System.out.println("Ação 28 foi chamada - " + this.ponteiro);
    }

    // ação 29 reconhecimento de operação relacional menor
    public void reconhecerOperacaoRelacionalMenor() {
        gerarInstrucao(this.ponteiro, "SMR", "0");
        incrementaPonteiro();
        //System.out.println("Ação 29 foi chamada - " + this.ponteiro);
    }

    // ação 30: reconhecimento de operação relacional maior
    public void reconhecerOperacaoRelacionalMaior() {
        gerarInstrucao(this.ponteiro, "BGR", "0");
        incrementaPonteiro();
        //System.out.println("Ação 30 foi chamada - " + this.ponteiro);
    }

    // ação 31: reconhecimento de operação relacional menor igual
    public void reconhecerOperacaoRelacionalMenorIgual() {
        gerarInstrucao(this.ponteiro, "SME", "0");
        incrementaPonteiro();
        //System.out.println("Ação 31 foi chamada - " + this.ponteiro);
    }

    // ação 32: reconhecimento de operação relacional maior igual
    public void reconhecerOperacaoRelacionalMaiorIgual() {
        gerarInstrucao(this.ponteiro, "BGE", "0");
        incrementaPonteiro();
        //System.out.println("Ação 32 foi chamada - " + this.ponteiro);
    }

    // ação 33: reconhecimento de operação aritmética adição
    public void reconhecerOperacaoAritmeticaAdicao() {
        gerarInstrucao(this.ponteiro, "ADD", "0");
        incrementaPonteiro();
        //System.out.println("Ação 33 foi chamada - " + this.ponteiro);
    }

    // ação 34: reconhecimento de operação aritmética subtração
    public void reconhecerOperacaoAritmeticaSubtracao() {
        gerarInstrucao(this.ponteiro, "SUB", "0");
        incrementaPonteiro();
        //System.out.println("Ação 34 foi chamada - " + this.ponteiro);
    }

    // ação 35: reconhecimento de operação lógica OU
    public void reconhecerOperacaoLogicaOu() {
        gerarInstrucao(this.ponteiro, "OR", "0");
        incrementaPonteiro();
        //System.out.println("Ação 35 foi chamada - " + this.ponteiro);
    }

    // ação 36: reconhecimento de operação aritmética multiplicação
    public void reconhecerOperacaoAritmeticaMultiplicacao() {
        gerarInstrucao(this.ponteiro, "MUL", "0");
        incrementaPonteiro();
        //System.out.println("Ação 36 foi chamada - " + this.ponteiro);
    }


    // ação 37: reconhecimento de operação aritmética divisão real
    public void reconhecerOperacaoAritmeticaDivisaoReal() {
        gerarInstrucao(this.ponteiro, "DIV", "0");
        incrementaPonteiro();
        //System.out.println("Ação 37 foi chamada - " + this.ponteiro);
    }


    // Ação 38 reconhecimento de operação aritmética divisão inteira
    public void reconhecerOperacaoAritmeticaDivisaoInteira() {
        gerarInstrucao(this.ponteiro, "DIV", "0");
        incrementaPonteiro();
        //System.out.println("Ação 38 foi chamada - " + this.ponteiro);
    }

    // Ação 39 reconhecimento de operação aritmética resto da divisão inteira
    public void reconhecerOperacaoAritmeticaRestoDivisaoInteira() {
        gerarInstrucao(this.ponteiro, "MOD", "0");
        incrementaPonteiro();
        //System.out.println("Ação 39 foi chamada - " + this.ponteiro);
    }

    // Ação 40 reconhecimento de operação lógica E
    public void reconheceOperacaoLogicaE() {
        gerarInstrucao(this.ponteiro, "AND", "0");
        incrementaPonteiro();
        //System.out.println("Ação 40 foi chamada - " + this.ponteiro);
    }

    // Ação 41 reconhecimento de operação aritmética potenciação especifica
    public void reconhecerOperacaoAritmeticaPotenciacao() {
        gerarInstrucao(this.ponteiro, "POW", "0");
        incrementaPonteiro();
        // System.out.print("Ação 41 foi chamada - " + this.ponteiro);
    }

    // Ação 42 - reconhecimento de operação lógica NÃO
    public void reconheceOperacaoLogicaNao() {
        gerarInstrucao(this.ponteiro, "NOT", "0");
        incrementaPonteiro();
        //System.out.println("Ação 42 foi chamada - " + this.ponteiro);
    }

    public String obterErrosSemanticos() {
        if (this.errosSemanticos.isEmpty()) {
            return "";
        }

        StringBuilder erros = new StringBuilder();
        for (String erro : this.errosSemanticos) {
            erros.append(erro).append("\n");
        }
        return erros.toString();
    }
}