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
    public ArrayList<Integer> pilhaDeDesvios = new ArrayList<>() ; // endereços de JMF/JMP a resolver
    public boolean temIndice; //marca se há índice após um identificador
    public int baseDoUltimoVetor;  //base do último vetor declarado na linha
    public int tamanhoDoUltimoVetor; //tamanho do último vetor declarado na linha
    public boolean houveInitLinha; //marca se houve inicialização comum na linha de escalares
    public int primeiroBaseInit; //base do primeiro identificador escalara inicializado na linha
    public StringBuilder erros;
    public int inicioLoop;
    public SymtableEntry ExpAux,AtrAux,ShoAux;
    public int valAux = 0;
    public int indiceEstatico = -1; // -1 indica que não é estático (é dinâmico)

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

    private ArrayList<String> linha(int ponteiro, String inst, String value){
        ArrayList<String> linhas = new ArrayList<>(List.of(String.valueOf(ponteiro), inst,value));
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
            System.out.println(nome);
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

    public void vetorConstante(Token t) {
        temIndice = true;
        // Guarda o valor do índice para usar depois
        indiceEstatico = Integer.parseInt(t.image);
        // NÃO gera LDI. O cálculo será feito na hora do STR/LDV.
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
            for(int k = 1; k < listaBasesDaLinha.size(); k++){
                codigIn.add(linha(ponteiro, "LDV", primeiroBaseInit));
                ponteiro++;
                codigIn.add(linha(ponteiro, "STR", listaBasesDaLinha.get(k)));
                ponteiro++;
            }
        }

        houveInitLinha = false;
        primeiroBaseInit = -1;
        listaDeIdentificadoresDaLinha.clear();
        listaBasesDaLinha.clear();
        VP = 0;
    }

    public void inicializaVet(Token brace) { // #IV
            if(valAux == 1){
                int baseV = baseDoUltimoVetor;
                codigIn.add(linha(ponteiro, "STR", baseV));
                ponteiro++;
                for(int j = 1; j < tamanhoDoUltimoVetor - 1; j++){
                    codigIn.add(linha(ponteiro, "LDV", baseV));
                    ponteiro++;
                    codigIn.add(linha(ponteiro, "STR", baseV + (j-1)));
                    ponteiro++;
                }
            }else if(valAux == tamanhoDoUltimoVetor){
                for(int i = 0; i < tamanhoDoUltimoVetor; i++){
                    codigIn.add(linha(ponteiro, "STR", baseDoUltimoVetor + i));
                    ponteiro++;
                }
            }else{
                //numero de variaveis diferente do tamanha do veto -> erro
                erroSemantico(brace,"Numero de variaveis diferente do tamanho do vetor");
            }
            valAux = 0;
    }

    public void inicializaEscalar(){ // #IE
        primeiroBaseInit = listaBasesDaLinha.getFirst();
        codigIn.add(linha(ponteiro, "STR", primeiroBaseInit));
        ponteiro++;
        houveInitLinha = true;
    }

    public void val(){ // #VAL
        valAux++;
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
        String valor = token.image;
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
        codigIn.add(linha(ponteiro, "SUB", 0));
        ponteiro++;
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

    public void expressao1(Token id){ // #E1
        System.out.println(tabela);
        String nome = id.image;
        ExpAux = tabela.lookup(nome);

        if(ExpAux == null){
            erroSemantico(id, "identificador " + found(id) + " não declarado");
            temIndice = false;
            return;
        }

        temIndice = false; // 4. temIndice ← falso
    }

    public void expressao2(){ //#E2
        if(ExpAux == null){return;}

        // Se não tem índice (escalar simples)
        if(!temIndice) {
            codigIn.add(linha(ponteiro, "LDV", ExpAux.base));
            ponteiro++;
            return;
        }

        // Se tem índice...
        if (indiceEstatico != -1) {
            // --- OTIMIZAÇÃO ---
            int enderecoFinal = ExpAux.base + (indiceEstatico - 1);
            codigIn.add(linha(ponteiro, "LDV", enderecoFinal));
            ponteiro++;
            indiceEstatico = -1;
        } else {
            // --- CASO DINÂMICO ---
            codigIn.add(linha(ponteiro, "LDI", ExpAux.base - 1));
            ponteiro++;
            codigIn.add(linha(ponteiro, "ADD", 0));
            ponteiro++;
            codigIn.add(linha(ponteiro, "LDX", 0));
            ponteiro++;
        }
    }



    public void atribuicao1(Token id){ //#A1
        System.out.println("ATRIBUICAO ");
        String nome = id.image;
        if (tabela.lookup(nome) == null) {
            erroSemantico(id,"identificador "+found(id)+" não foi declarado");
            return;
        }

        AtrAux = tabela.lookup(nome);
        temIndice = false;
    }

    public void i1(){ //#I1
        temIndice = true;
        indiceEstatico = -1; // Indica que o índice está na pilha (dinâmico)
    }

    public void atribuicao2(Token id){ //#A2
        if(AtrAux != null && AtrAux.tam == 0 && temIndice){
            erroSemantico(id,"identificador "+found(id)+" é escalar e "+expected("não deve possuir índice"));
        }

        if(AtrAux != null &&AtrAux.tam > 0 && !temIndice){
            erroSemantico(id,"identificador "+found(id)+" é vetor "+expected("mas não possui índice"));
        }

    }

    public void atribuicao3(){ //#A3
        if(AtrAux == null) return;

        // Caso 1: Escalar Simples
        if(AtrAux.tam == 0 ){
            codigIn.add(linha(ponteiro, "STR", AtrAux.base));
            ponteiro++;
            return;
        }

        // Caso 2: Vetor
        if (indiceEstatico != -1) {
            // --- OTIMIZAÇÃO (Saída Esperada) ---
            // Calcula endereço final: Base + (Índice - 1)
            int enderecoFinal = AtrAux.base + (indiceEstatico - 1);

            // Gera STR direto no endereço calculado!
            codigIn.add(linha(ponteiro, "STR", enderecoFinal));
            ponteiro++;

            // Limpa para a próxima
            indiceEstatico = -1;
        } else {
            // --- CASO DINÂMICO (v[i]) ---
            codigIn.add(linha(ponteiro, "LDI", AtrAux.base - 1));
            ponteiro++;
            codigIn.add(linha(ponteiro, "ADD", 0));
            ponteiro++;
            codigIn.add(linha(ponteiro, "STX", 0)); // Store Indexado
            ponteiro++;
        }
    }

    public void read1(Token id){ //R1
        String nome = id.image;

        AtrAux = tabela.lookup(nome);
        if(AtrAux == null){
            erroSemantico(id, "identificador " + found(id) + " não foi declarado");
            temIndice = false;
            return;
        }
        temIndice = false;
    } //#R1

    // TODO FAZER O SHEREK
    public void read2(Token id){ //R1
        if (AtrAux == null) { // Se falahar em R1, AtxAux sera null e vai aborta
            temIndice = false;
            return;
        }

        if(AtrAux.tam == 0){ // se for igual a 0 indica que nao é vetor, váriavel simples
            if(temIndice){
                erroSemantico(id, "Identificador " + found(id) + " é escalar e não deve possuir índice.");
            }else{ //gera codigo pro escalar

                // le o valor do teclado e empilha
                codigIn.add(linha(ponteiro, "LDI", AtrAux.cat)); // Passa a categoria do tipo pro REA validar a entrada
                ponteiro++;

                // armazena o valor do topo no endereço da variável
                codigIn.add(linha(ponteiro, "STR", AtrAux.base));
                ponteiro++;
            }

        }else if(AtrAux.tam > 0){ // se for maior que 0 indica que é um vetor
            if(!temIndice){ //validadcao semantica o vetor precisa ter indice de maneira obrigatoria
                erroSemantico(id, "Identificador " + found(id) + " é vetor e precisa de índice");
            }else{
                // Carrega o Endereço Base do Vetor (ajustado em -1)
                codigIn.add(linha(ponteiro, "LDI", AtrAux.base - 1));
                ponteiro++;

                // calcula o Endereço Físico (Soma Base + Índice)
                codigIn.add(linha(ponteiro, "ADD", 0));
                ponteiro++;

                // lê o Valor do Teclado (REA)
                // Colocamos o valor lido NO TOPO, acima do endereço.
                codigIn.add(linha(ponteiro, "REA", AtrAux.cat));
                ponteiro++;

                // AVISO PARA SUA VM: O Topo é o VALOR, o Sub-topo é o ENDEREÇO.
                codigIn.add(linha(ponteiro, "STX", 0));
                ponteiro++;
            }
        }

        temIndice = false;
    } //#R2

    public void show2(Token id){ // #S2
        if (tabela.lookup(id.image) == null) {
            erroSemantico(id,"Identificador "+found(id)+" foi chamado "+expected("mas não foi declarado"));
            return;
        }
        ShoAux = tabela.lookup(id.image);
        temIndice = false;
    }

    public void show3(){ // #S3
        if (ShoAux == null) return;

        // --- CASO 1: Escalar Simples (ex: show(x)) ---
        if(ShoAux.tam == 0 ) {
            // CORREÇÃO: Usa ShoAux.base em vez de VT+1
            codigIn.add(linha(ponteiro, "LDV", ShoAux.base));
            ponteiro++;

            codigIn.add(linha(ponteiro, "WRT", 0));
            ponteiro++;
            return;
        }

        // --- CASO 2: Vetor com Índice Estático (ex: show(v[3])) ---
        if (indiceEstatico != -1) {
            // OTIMIZAÇÃO: Calcula o endereço final em tempo de compilação
            // Endereço = Base + (Indice - 1)
            int enderecoFinal = ShoAux.base + (indiceEstatico - 1);

            codigIn.add(linha(ponteiro, "LDV", enderecoFinal));
            ponteiro++;

            codigIn.add(linha(ponteiro, "WRT", 0));
            ponteiro++;

            // Reseta o índice para a próxima instrução
            indiceEstatico = -1;

        } else {
            // --- CASO 3: Vetor Dinâmico (ex: show(v[i])) ---
            // Se cair aqui, gera LDI + ADD + LDX

            // CORREÇÃO: Usa ShoAux.base - 1 em vez de VT
            codigIn.add(linha(ponteiro, "LDI", ShoAux.base - 1));
            ponteiro++;

            codigIn.add(linha(ponteiro, "ADD", 0));
            ponteiro++;

            codigIn.add(linha(ponteiro, "LDX", 0)); // LDX carrega o valor usando o endereço do topo
            ponteiro++;

            codigIn.add(linha(ponteiro, "WRT", 0));
            ponteiro++;
        }

        // Limpeza padrão
        temIndice = false;
    }

    public void saidaConstInteira(Token k){ //#K1
        Integer valor = Integer.parseInt(k.image);

        codigIn.add(linha(ponteiro, "LDI", valor)); //
        ponteiro++;

        codigIn.add(linha(ponteiro, "WRT", 0));
        ponteiro++;
    }

    public void saidaConstReal(Token r){ //#K2
        Integer valor = Integer.parseInt(r.image);

        codigIn.add(linha(ponteiro, "LDR", valor)); //
        ponteiro++;

        codigIn.add(linha(ponteiro, "WRT", 0));
        ponteiro++;
    }

    public void saidaConstLiteral(Token s){ //#K3
        //Integer valor = Integer.parseInt(s.image);
        String valor = s.image;

        codigIn.add(linha(ponteiro, "LDS", valor)); //arruma aqui era LDR virou LDS (bonda 3)
        ponteiro++;

        codigIn.add(linha(ponteiro, "WRT", 0));
        ponteiro++;
    }

    public void selecaoF1() { //#F1
        codigIn.add(linha(ponteiro, "JMF", 0)); // 0 = placeholder

        pilhaDeDesvios.add(ponteiro);

        ponteiro++;
    }
    /*
    15    if condicao -> 15 JMF 0
        then
     27   else -> selec2 15 JMF 27
        end
     */

    public void selecaoF2() { //#F2
        // gerar instrução(ponteiro, JMF, 0);
        codigIn.add(linha(ponteiro, "JMP", 0)); // 0 = placeholder

        int enderecoJMP = ponteiro;
        ponteiro++;

        int ultimoIndiceJMF = pilhaDeDesvios.size() - 1;
        int enderecoJMF = pilhaDeDesvios.remove(ultimoIndiceJMF);

        int IndiceListaJMF = enderecoJMF - 1;
        ArrayList<String> instrucaoJMF = codigIn.get(IndiceListaJMF);
        instrucaoJMF.set(2, String.valueOf(ponteiro));

        pilhaDeDesvios.add(enderecoJMP);
    }


    public void selecaoF3() { //#F3

        int ultimoIndice = pilhaDeDesvios.size() - 1;
        int enderecoPendente = pilhaDeDesvios.remove(ultimoIndice);

        int indiceDaLista = enderecoPendente - 1;
        ArrayList<String> instrucaoPendente = codigIn.get(indiceDaLista);

        instrucaoPendente.set(2, String.valueOf(ponteiro));
    }

    public void loop1(){
        inicioLoop = ponteiro;
        codigIn.add(linha(ponteiro, "JMF", 0));
        pilhaDeDesvios.add(ponteiro);
        ponteiro++;
    }

    public void loop2(){
        codigIn.add(linha(ponteiro, "JMP", inicioLoop));
        ponteiro++;

        int ultimoIndice = pilhaDeDesvios.size() - 1;
        int enderecoPendente = pilhaDeDesvios.remove(ultimoIndice);

        int indiceDaLista = enderecoPendente - 1;
        ArrayList<String> instrucaoPendente = codigIn.get(indiceDaLista);

        instrucaoPendente.set(2, String.valueOf(ponteiro));
    }
}
