package org.example.Actions;

import org.example.JavaCC.Token;
import org.example.JavaCC.TokenMgrError;
import org.example.SymTable.Symtable;
import org.example.SymTable.SymtableEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AnalisadorSemantico {
    public Symtable tabela = new Symtable("Programa");
    public ArrayList<ArrayList<String>> codigIn = new ArrayList<>(); //codigo intermediario uwu vulgo codiguin
    public int VT; //total de posições já alocadas na pilha de execução (base-1)
    public int VP; //soma das posições a alocar na linha atual de declaração
    public ArrayList<String> listaDeIdentificadoresDaLinha; //lista (na ordem) dos identificadores da linha corrente
    public ArrayList<Integer> listaBasesDaLinha; // lista (na ordem) das bases correspondentes da linha corrente
    public int categoriaAtual; // 1/2/3/4 (num/real/text/flag)
    public int ponteiro; //contador de instruções, inicia em 1
    //pilhaDeDesvios: endereços de JMF/JMP a resolver
    public boolean temIndice; //marca se há índice após um identificador
    public int baseDoUltimoVetor;  //base do último vetor declarado na linha
    public int tamanhoDoUltimoVetor; //tamanho do último vetor declarado na linha
    public boolean houveInitLinha; //marca se houve inicialização comum na linha de escalares
    public int primeiroBaseInit; //base do primeiro identificador escalara inicializado na linha
    public StringBuilder erros;

    public SymtableEntry aux;


    public AnalisadorSemantico() {
        this.ponteiro = 1;
        this.erros = new StringBuilder();
    }

    private String found(Token token){
        return "<span class='found'>"+token.image+"</span>";
    }
    private String expected(String msg){
        return "<span class='expected'>"+msg+"</span>";
    }

    private void erroSemantico(Token token, String msg){
        String erro = String.format(
                "<div class='log-entry'>"+
                    "<span class='line-info'>"+
                        "Erro Semântico na linha %d , coluna %d:"+
                    "</span><br>"+
                    "%s</div>",
                token.beginLine,
                token.beginColumn,
                msg);

        erros.append(erro);
    }


    public void programa1(Token programa) // #P1
    {
        tabela.insert(programa.image, 0, 0);
    }

    private ArrayList<String> linha(int ponteiro, String inst, int value){
        ArrayList<String> linhas = new ArrayList<>(List.of(String.valueOf(ponteiro), inst, String.valueOf(value)));
        return linhas;
    }

    public void programa2() // #P2
    {
        codigIn.add(linha(ponteiro, "STP", 0));
        ponteiro++;
    }

    public void declaracao0(){ // #D0
        listaDeIdentificadoresDaLinha = new ArrayList<>();
        listaBasesDaLinha = new ArrayList<>();
        VP = 0;
        houveInitLinha = false;
        primeiroBaseInit = -1;
    }


    public void declaracao1(Token id){ // #D1
        if(id == null) return;
        String nome = id.image;

        if (tabela.lookup(nome) != null) {
            erroSemantico(id,"identificador "+found(id)+" já declarado");
            return;
        }

        listaDeIdentificadoresDaLinha.add(nome);
    }

    public void tipo(Token tipoToken){ // #T
        String tipo = tipoToken.image;
        if(Objects.equals(tipo, "num")){
            categoriaAtual = 1;
        }else if(Objects.equals(tipo, "real")){
            categoriaAtual = 2;
        }else if(Objects.equals(tipo, "text")){
            categoriaAtual = 3;
        }else if(Objects.equals(tipo, "flag")){
            categoriaAtual = 4;
        }
    }

    public void vetor1(Token valorVetorToken){ // #V1 8====D
        Integer valor = Integer.parseInt(valorVetorToken.image);
        if (valor<=0){
            erroSemantico(valorVetorToken,"Encontrado valor "+found(valorVetorToken)+" mas precisa ser "+expected("maior que 0"));
            return;
        }
        tamanhoDoUltimoVetor = valor;
    }


    public void vetor2(){ // #V2
        for(String nome: listaDeIdentificadoresDaLinha){
            int base = VT + 1;
            tabela.insert(nome, categoriaAtual, base, tamanhoDoUltimoVetor);
            VT +=  tamanhoDoUltimoVetor;
            VP += tamanhoDoUltimoVetor;
            baseDoUltimoVetor = base;
            listaBasesDaLinha.add(base);
        }
    }

    public void escalar2(){ // #E2
        for(String nome: listaDeIdentificadoresDaLinha){
            int base = VT + 1;
            tabela.insert(nome, categoriaAtual, base);
            VT += 1;
            VP += 1;
            listaBasesDaLinha.add(base);
        }
    }

    public void declaracao6(){ // #D6
        //modelo de geração de codigo codigIn.add(linha(ponteiro, "STP", 0))
        if(categoriaAtual == 1){
            codigIn.add(linha(ponteiro, "ALI", VP));
        }else if(categoriaAtual == 2){
            codigIn.add(linha(ponteiro, "ALR", VP));
        }else if(categoriaAtual == 3){
            codigIn.add(linha(ponteiro, "ALS", VP));
        }else if(categoriaAtual == 4){
            codigIn.add(linha(ponteiro, "ALB", VP));
        }
        ponteiro++;
        if(houveInitLinha){
            for(int k = 2; k < listaBasesDaLinha.size(); k++){
                codigIn.add(linha(ponteiro, "LDV", primeiroBaseInit));
                ponteiro++;
                codigIn.add(linha(ponteiro, "STR", listaBasesDaLinha.get(k)));
                ponteiro++;
            }
            houveInitLinha = false;
            primeiroBaseInit = 1;
            listaDeIdentificadoresDaLinha.clear();
            listaBasesDaLinha.clear();
            VP = 0;
        }
    }

    public void inicializaVet(Token listaConstantesToken, Token primeiraConstanteToken,Token valorVetorToken) { // #IV
        if(listaConstantesToken == null){
            //— Inicialização de vetor com um único valor (replicação sem peephole):
            /*
            EXEMPLO:
            define
             v : num [5] = 3;
            gera:
            (1, ALI, 5)
            (2, LDI, 3)
            (3, STR, 1)
            (4, LDV, 1)
            (5, STR, 2)
            ...
            (7, STR, 5)
            --
            (1) Gerar a <expressão> desse valor (no topo).
            (2) baseV ← baseDoUltimoVetor.
            (3) gerar instrução(ponteiro, STR, baseV); ++ponteiro.
            (4) para j de 2 até tamanhoDoUltimoVetor:
                  gerar instrução(ponteiro, LDV, baseV); ++ponteiro.
                  gerar instrução(ponteiro, STR, baseV + (j−1)); ++ponteiro.
             */
            return;
        }
        String listaConstante = primeiraConstanteToken.image + listaConstantesToken.image;
        int qtdConstantes = listaConstante.split(",").length;
        if(qtdConstantes+1 < Integer.parseInt(valorVetorToken.image)){
            erroSemantico(primeiraConstanteToken,"Faltam indices a serem inicializados, esperam-se "+expected(valorVetorToken.image)+" constantes ");
            return;
        }
        //• Caso lista completa (um valor por elemento): cada <valor> já empilha sua constante; emitir STR direto em #VAL (ver abaixo).


    }

    public void inicializaEscalar(){ // #IE

    }

    public void val(){ // #VAL

    }

    public void constanteInteira(Token token){ //#C1
        Integer valor = Integer.parseInt(token.image);
        codigIn.add(linha(ponteiro, "LDI", valor));
        ponteiro++;
    }

    public void constanteReal(Token token){ //#C2
        Integer valor = Integer.parseInt(token.image);
        codigIn.add(linha(ponteiro, "LDR", valor));
        ponteiro++;
    }

    public void constanteLiteral(Token token){ //#C3
        Integer valor = Integer.parseInt(token.image);
        codigIn.add(linha(ponteiro, "LDS", valor));
        ponteiro++;
    }

    public void constanteVerdadeira(){ // #C4
        codigIn.add(linha(ponteiro, "LDB",1));
        ponteiro++;
    }

    public void constanteFalsa(){ //#C5
        codigIn.add(linha(ponteiro, "LDB", 0));
        ponteiro++;
    }

//EXPRESSAO
    public void rIgual(){ // #R==
        codigIn.add(linha(ponteiro, "EQL", 0));
        ponteiro++;
    }
    public void rMenor(){ // #R<
        codigIn.add(linha(ponteiro, "SMR", 0));
        ponteiro++;
    }
    public void rMenorIgual(){ // #R<=
        codigIn.add(linha(ponteiro, "SME", 0));
        ponteiro++;
    }
    public void rDiferente(){ // #R!=
        codigIn.add(linha(ponteiro, "DIF", 0));
        ponteiro++;
    }
    public void rMaior(){ // #R>
        codigIn.add(linha(ponteiro, "BGR", 0));
        ponteiro++;
    }
    public void rMaiorIgual(){ // #R>=
        codigIn.add(linha(ponteiro, "BGE", 0));
        ponteiro++;
    }

    public void rAdd(){
        codigIn.add(linha(ponteiro, "ADD", 0));
        ponteiro++;
    }
    public void rSub(){ // #SUB
        codigIn.add(linha(ponteiro, "ADD", 0));
    }

    public void rOr(){
        codigIn.add(linha(ponteiro, "OR", 0));
        ponteiro++;
    }

    public void rMul(){
        codigIn.add(linha(ponteiro, "MUL", 0));
        ponteiro++;
    }

    public void rDiv(){
        codigIn.add(linha(ponteiro, "DIV", 0));
        ponteiro++;
    }

    public void rMod(){
        codigIn.add(linha(ponteiro, "MOD", 0));
        ponteiro++;
    }

    public void rRem(){
        codigIn.add(linha(ponteiro, "REM", 0));
        ponteiro++;
    }

    public void rAnd(){
        codigIn.add(linha(ponteiro, "AND", 0));
        ponteiro++;
    }

    public void rPow(){
        codigIn.add(linha(ponteiro, "POW", 0));
        ponteiro++;
    }

    public void rNot(){
        codigIn.add(linha(ponteiro, "NOT", 0));
        ponteiro++;
    }

    public void expressao1(Token id){
        String nome = id.image;
        aux = tabela.lookup(nome);

        if(aux == null){
            erroSemantico(id, "identificador " + found(id) + " não declarado");
            temIndice = false;
            return;
        }

        temIndice = false; // 4. temIndice ← falso
    }

}
