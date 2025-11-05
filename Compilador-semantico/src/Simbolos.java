class Simbolos {
    private String nome;
    private int categoria;
    private String atributo;

    public Simbolos(String nome, int categoria, String atributo) {
        this.nome = nome;
        this.categoria = categoria;
        this.atributo = atributo;
    }

    public String getNome() { return nome; }
    public int getCategoria() { return categoria; }
    public String getAtributo() { return atributo; }
}