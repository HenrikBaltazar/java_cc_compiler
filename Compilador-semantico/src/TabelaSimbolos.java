import java.util.HashMap;
import java.util.Map;

public class TabelaSimbolos {
    private Map<String, Simbolo> simbolos;

    public TabelaSimbolos() {
        simbolos = new HashMap<>();
    }

    public boolean inserirSimbolo(String nome, Simbolo simbolo) {
        if (simbolos.containsKey(nome)) {
            return false;
        }
        simbolos.put(nome, simbolo);
        return true;
    }

    public boolean existeSimbolo(String nome) {
        return simbolos.containsKey(nome);
    }

    public Simbolo obterSimbolo(String nome) {
        return simbolos.get(nome);
    }
}
