package org.example.Actions;

import org.example.JavaCC.Token;
import org.example.SymTable.Symtable;

import java.util.ArrayList;
import java.util.List;


public class AnalisadorSemantico {
    public Symtable tabela = new Symtable("Programa");
    public ArrayList<ArrayList<String>> codigIn = new ArrayList<>(); //codigo intermediario uwu vulgo codiguin
    public int VT; //total de posições já alocadas na pilha de execução (base-1)
    public int VP; //soma das posições a alocar na linha atual de declaração
    public ArrayList<String> listaDeIdentificadoresDaLinha; //lista (na ordem) dos identificadores da linha corrente
    public ArrayList<Integer> listaBasesDaLinha; // lista (na ordem) das bases correspondentes da linha corrente
    //categoriaAtual: 1/2/3/4 (num/real/text/flag)
    public int ponteiro; //contador de instruções, inicia em 1
    //pilhaDeDesvios: endereços de JMF/JMP a resolver
    //temIndice: marca se há índice após um identificador
    //baseDoUltimoVetor: base do último vetor declarado na linha
    //tamanhoDoUltimoVetor: tamanho do último vetor declarado na linha
    public boolean houveInitLinha; //marca se houve inicialização comum na linha de escalares
    public int primeiroBaseInit; //base do primeiro identificador escalara inicializado na linha

    public AnalisadorSemantico() {
        this.ponteiro = 1;
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

    public void declaracao0(){ //D0
        listaDeIdentificadoresDaLinha = new ArrayList<>();
        listaBasesDaLinha = new ArrayList<>();
        VP = 0;
        houveInitLinha = false;
        primeiroBaseInit = -1;
    }


    public void declaracao1(){

    }


}
