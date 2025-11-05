public class Instrucao {
    int numero;
    private String codigo;
    private String parametro;

    @Override
    public String toString() {
        return "Número = " + numero + ", Código = " + codigo + ", Parâmetro = " + parametro;
    }

    public int getNumero() {
        return numero;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getParametro() {
        return parametro;
    }

    public Instrucao(int numero, String codigo, String parametro) {
            this.numero = numero;
            this.codigo = codigo;
            this.parametro = parametro;
    }
}
