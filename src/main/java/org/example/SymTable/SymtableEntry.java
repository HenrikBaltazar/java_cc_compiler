package org.example.SymTable;

public class SymtableEntry {
    public String iden; //identificador
    public int cat; //categoria: 0=programa, 1=num, 2=real, 3=text, 4=flag
    public int base; //base: endereço lógico base-1 (primeira posição utilizável = 1)
    public int tam = 0; //tamanho: “–” se escalar; N>0 se vetor

    public SymtableEntry(String iden, int cat, int base, int tam) {
        this.iden = iden;
        this.cat = cat;
        this.base = base;
        this.tam = tam;
    }

    public SymtableEntry(String iden, int cat, int base) {
        this.iden = iden;
        this.cat = cat;
        this.base = base;
        this.tam = 0;
    }
}
