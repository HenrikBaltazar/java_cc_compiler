package org.example.SymTable;
import java.util.HashMap;
/*
1) Tabela de Símbolos (TS): tupla (identificador, categoria, base, tamanho).
   • categoria: 0=programa, 1=num, 2=real, 3=text, 4=flag
   • base: endereço lógico base-1 (primeira posição utilizável = 1)
   • tamanho: “–” se escalar; N>0 se vetor
*/
public class Symtable {

    String tableName;
    HashMap<String, SymtableEntry> table;
    int base = 1; //incrementa conforme adiciona variaveis à tabela

    public Symtable(String name){
        this.tableName = name;
        this.table = new HashMap<String, SymtableEntry>();
    }

    public SymtableEntry lookup (String sym){
        SymtableEntry result;
        result = table.get(sym);
        return result;
    }

    public void insert(String iden, int cat, int base){
        if(table.containsKey(iden)){
            System.err.println("Erro Semântico: Identificador '" + iden + "' já declarado.");
        }else {
            table.put(iden, new SymtableEntry(iden, cat, base));
        }
    }

    public void insert(String iden, int cat, int base, int tam){
        if(table.containsKey(iden)){
            System.err.println("Erro Semântico: Identificador '" + iden + "' já declarado.");
        }else {
            table.put(iden, new SymtableEntry(iden, cat, base,tam));
        }
    }

    public void print() {
        print(0);
    }

    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        //System.out.println(scope +  " - " + table.size() + " simbolos");
        for (SymtableEntry entry : table.values()) {
            // Agora usa a variável "tableName"
            System.out.println("--- Tabela: " + tableName + " (" + table.size() + " símbolos) ---");

        }
    }
}



