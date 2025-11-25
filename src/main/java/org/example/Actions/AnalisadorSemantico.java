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
    public int inicioVetC, fimVetC; // Marcadores para o swap
    public int indiceInicioLinha; // Marca onde começa o código da linha atual

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

    public String getConstTypeByCat(int cat){
        if(cat == 1){
            return "constante inteira";
        }else if(cat == 2){
            return "constante real";
        }else if(cat == 3){
            return "constante literal";
        }else if(cat == 4){
            return "flag";
        }
        return "";
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
        indiceInicioLinha = codigIn.size();
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

    public void inicioVetorAtribuicao() {
        inicioVetC = codigIn.size();
    }


    public void fimVetorAtribuicao() {
        fimVetC = codigIn.size();
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
        ArrayList<String> instrucaoAlloc = null;

        // 1. Cria a instrução de alocação (sem adicionar na lista ainda)
        if(categoriaAtual == 1){
            instrucaoAlloc = linha(0, "ALI", VP); // Use 0 temporariamente no ponteiro
        }else if(categoriaAtual == 2){
            instrucaoAlloc = linha(0, "ALR", VP);
        }else if(categoriaAtual == 3){
            instrucaoAlloc = linha(0, "ALS", VP);
        }else if(categoriaAtual == 4){
            instrucaoAlloc = linha(0, "ALB", VP);
        }

        // 2. INSERE a instrução no início da sequência da linha (antes dos inits)
        if (instrucaoAlloc != null) {
            codigIn.add(indiceInicioLinha, instrucaoAlloc);

            // 3. RENUMERA as instruções afetadas (do ponto de inserção até o fim)
            for (int i = indiceInicioLinha; i < codigIn.size(); i++) {
                // Ajusta o número da linha (coluna 0) para i + 1
                codigIn.get(i).set(0, String.valueOf(i + 1));
            }

            // Atualiza o ponteiro global para refletir o novo tamanho
            ponteiro = codigIn.size() + 1;
        }
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
        // CORREÇÃO DE OURO:
        // Não usamos baseDoUltimoVetor (que ainda é 0).
        // Usamos VT (Total de Variáveis) + 1 para achar o próximo buraco livre na memória.
        // Exemplo: Se já tem 'i' (VT=1), o vetor começa no 2.
        int baseCorreta = VT + 1;

        if(valAux == 1){
            // Caso: x : num[5] = {0}; (Inicializa tudo com um valor só)

            // 1. Grava o primeiro valor na base
            codigIn.add(linha(ponteiro, "STR", baseCorreta));
            ponteiro++;

            // 2. Copia para o resto do vetor
            for(int j = 1; j < tamanhoDoUltimoVetor; j++){
                codigIn.add(linha(ponteiro, "LDV", baseCorreta)); // Pega o valor da 1ª posição
                ponteiro++;
                codigIn.add(linha(ponteiro, "STR", baseCorreta + j)); // Grava na próxima
                ponteiro++;
            }

        } else if(valAux == tamanhoDoUltimoVetor){
            // Caso: x : num[5] = {"A", "B", "C"...}; (Inicialização completa)

            // O loop percorre cada item que já foi empilhado pelos comandos anteriores (LDS/LDI...)
            // E gera o STR para a posição correta.
            for(int i = 0; i < tamanhoDoUltimoVetor; i++){

                // AQUI ESTAVA O ERRO (STR 0, STR 1...)
                // COM A CORREÇÃO: (baseCorreta + i)
                // i=0 -> STR (2+0) = STR 2 (Correto!)
                // i=1 -> STR (2+1) = STR 3 (Correto!)
                codigIn.add(linha(ponteiro, "STR", baseCorreta + i));
                ponteiro++;
            }

        } else {
            erroSemantico(brace, "Numero de variaveis diferente do tamanho do vetor");
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
        if(categoriaAtual != 1){
            erroSemantico(token, "identificador "+found(token)+" do tipo constante inteira mas esperado do tipo "+expected(getConstTypeByCat(categoriaAtual)));
            return;
        }
        codigIn.add(linha(ponteiro, "LDI", token.image));
        ponteiro++;
    }

    public void constanteReal(Token token){ //#C2
        if(categoriaAtual != 2){
            erroSemantico(token, "identificador "+found(token)+" do tipo constante real mas esperado do tipo "+expected(getConstTypeByCat(categoriaAtual)));
            return;
        }
        codigIn.add(linha(ponteiro, "LDR", token.image));
        ponteiro++;
    }

    public void constanteLiteral(Token token){ //#C3
        if(categoriaAtual != 3){
            erroSemantico(token, "identificador "+found(token)+" do tipo constante literal mas esperado do tipo "+expected(getConstTypeByCat(categoriaAtual)));
            return;
        }
        codigIn.add(linha(ponteiro, "LDS", token.image));
        ponteiro++;
    }

    public void constanteVerdadeira(Token token){ // #C4
        if(categoriaAtual != 4){
            erroSemantico(token, "identificador "+found(token)+" do tipo flag mas esperado do tipo "+expected(getConstTypeByCat(categoriaAtual)));
            return;
        }
        codigIn.add(linha(ponteiro, "LDB",1));
        ponteiro++;
    }

    public void constanteFalsa(Token token){ //#C5
        if(categoriaAtual != 4){
            erroSemantico(token, "identificador "+found(token)+" do tipo flag mas esperado do tipo "+expected(getConstTypeByCat(categoriaAtual)));
            return;
        }
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
        categoriaAtual = 1;
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
        categoriaAtual = 1;
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

        // --- CASO 1: ESCALAR ---
        if(AtrAux.tam == 0 ){
            codigIn.add(linha(ponteiro, "STR", AtrAux.base));
            ponteiro++;
            return;
        }

        // --- CASO 2: VETOR ---
        // Precisamos garantir a ordem [Valor, Endereço] na pilha.

        // Se for índice estático (v[3]), não gerou código de vetor, então não precisa trocar.
        // Apenas geramos o STR direto (Otimização que fizemos antes).
        if (indiceEstatico != -1) {
            int enderecoFinal = AtrAux.base + (indiceEstatico - 1);
            codigIn.add(linha(ponteiro, "STR", enderecoFinal));
            ponteiro++;
            indiceEstatico = -1;
            return;
        }

        // Se for índice DINÂMICO (v[i+1]), o código do vetor já foi gerado ANTES da expressão.
        // Precisamos TROCAR: Mover o bloco do vetor para DEPOIS da expressão.

        // 1. Identificar os blocos
        // Bloco Vetor: de inicioVetC até fimVetC
        // Bloco Expressão: de fimVetC até o final atual

        if (fimVetC > inicioVetC) { // Só faz se tiver código de vetor para mover
            List<ArrayList<String>> blocoVetor = new ArrayList<>(codigIn.subList(inicioVetC, fimVetC));

            // Remove o bloco do vetor da posição original
            codigIn.subList(inicioVetC, fimVetC).clear();

            // Adiciona o bloco do vetor no final (depois da expressão)
            codigIn.addAll(blocoVetor);

            // IMPORTANTE: Renumerar os ponteiros (instruções) que ficaram fora de ordem
            // Começamos a renumeração a partir do ponto onde mexemos (inicioVetC)
            for (int i = inicioVetC; i < codigIn.size(); i++) {
                // O ponteiro correto é i + 1 (pois sua lista é base-0 e ponteiro é base-1)
                codigIn.get(i).set(0, String.valueOf(i + 1));
            }

            // Atualiza o ponteiro global para o próximo
            ponteiro = codigIn.size() + 1;
        }

        // Agora a pilha está [Valor, Índice]. Vamos calcular o endereço.
        // Gera: LDI Base-1, ADD, STX

        codigIn.add(linha(ponteiro, "LDI", AtrAux.base - 1));
        ponteiro++;

        codigIn.add(linha(ponteiro, "ADD", 0));
        ponteiro++;

        codigIn.add(linha(ponteiro, "STX", 0));
        ponteiro++;
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
    public void read2(Token id){
        if (AtrAux == null) {
            temIndice = false;
            return;
        }

        // --- CASO 1: ESCALAR (read(x)) ---
        if(AtrAux.tam == 0){
            if(temIndice){
                erroSemantico(id, "Identificador " + found(id) + " é escalar e não deve possuir índice.");
            } else {
                // 1. Lê valor (Pilha: [Valor])
                codigIn.add(linha(ponteiro, "REA", AtrAux.cat));
                ponteiro++;

                // 2. Salva na base
                codigIn.add(linha(ponteiro, "STR", AtrAux.base));
                ponteiro++;
            }

            // --- CASO 2: VETOR (read(v...)) ---
        } else if(AtrAux.tam > 0){
            if(!temIndice){
                erroSemantico(id, "Identificador " + found(id) + " é vetor e precisa de índice");
            } else {

                // --- Otimização de Índice Estático (read(v[3])) ---
                if (indiceEstatico != -1) {
                    int enderecoFinal = AtrAux.base + (indiceEstatico - 1);

                    codigIn.add(linha(ponteiro, "REA", AtrAux.cat));
                    ponteiro++;

                    codigIn.add(linha(ponteiro, "STR", enderecoFinal));
                    ponteiro++;

                    indiceEstatico = -1; // Reset

                } else {
                    // --- VETOR DINÂMICO (read(v[i])) SEM STO ---
                    // O código do índice já está no codigIn (entre inicioVetC e agora).
                    // Precisamos colocar o REA *antes* dele para a pilha ficar [Valor, Endereço].

                    // 1. Insere o REA na posição onde começou o vetor (inicioVetC)
                    // Use ponteiro temporário 0, será corrigido abaixo
                    codigIn.add(inicioVetC, linha(0, "REA", AtrAux.cat));

                    // 2. Renumera tudo daqui para frente
                    for (int i = inicioVetC; i < codigIn.size(); i++) {
                        codigIn.get(i).set(0, String.valueOf(i + 1));
                    }

                    // Atualiza o ponteiro global (cresceu 1 instrução)
                    ponteiro = codigIn.size() + 1;

                    // Pilha virtual neste momento: [Valor, Indice]

                    // 3. Calcula o Endereço (Base + Indice)
                    // O LDI vai empilhar a Base SOBRE o Índice.
                    // Pilha: [Valor, Indice, Base-1]
                    codigIn.add(linha(ponteiro, "LDI", AtrAux.base - 1));
                    ponteiro++;

                    // O ADD soma os dois do topo (Indice + Base)
                    // Pilha: [Valor, EnderecoFinal]  <-- A ORDEM PERFEITA PARA O STX!
                    codigIn.add(linha(ponteiro, "ADD", 0));
                    ponteiro++;

                    // 4. Salva usando STX
                    codigIn.add(linha(ponteiro, "STX", 0));
                    ponteiro++;
                }
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
        //Integer valor = Integer.parseInt(r.image);
        String valor = r.image;
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

    public void loop0(){
        inicioLoop = ponteiro;
    }

    public void loop1(){
        codigIn.add(linha(ponteiro, "JMF", 0));
        pilhaDeDesvios.add(ponteiro);
        ponteiro++;
    }

    public void loop2(){
        //gerar instrução(ponteiro, JMP, inicioLoop); ERRADO!!!!
        codigIn.add(linha(ponteiro, "JMP", inicioLoop));
        ponteiro++;

        //ajustar JMF pendente para o ponteiro atual (saída do laço). correto!
        int ultimoIndice = pilhaDeDesvios.size() - 1;
        int enderecoPendente = pilhaDeDesvios.remove(ultimoIndice);

        int indiceDaLista = enderecoPendente - 1;
        ArrayList<String> instrucaoPendente = codigIn.get(indiceDaLista);

        instrucaoPendente.set(2, String.valueOf(ponteiro));
    }
}
