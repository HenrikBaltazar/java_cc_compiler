public class MaquinaVirtual {
    private final Object[] pilha = new Object[10000];
    private int topo = -1;
    private int ponteiro = 0;
    private final TerminalExecucao terminal;

    public MaquinaVirtual(TerminalExecucao terminal) {
        this.terminal = terminal;
    }

    public void executarInstrucao(Instrucao instrucao) {
        System.out.println("Instrução atual: " + instrucao);
        int deslocamento, endereco;

        switch (instrucao.getCodigo()) {
            case "ADD":
                executarADD();
                break;
            case "ALB":
                deslocamento = Integer.parseInt(instrucao.getParametro());
                executarALB(deslocamento);
                break;
            case "ALI":
                deslocamento = Integer.parseInt(instrucao.getParametro());
                executarALI(deslocamento);
                break;
            case "ALR":
                deslocamento = Integer.parseInt(instrucao.getParametro());
                executarALR(deslocamento);
                break;
            case "ALS":
                deslocamento = Integer.parseInt(instrucao.getParametro());
                executarALS(deslocamento);
                break;
            case "DIV":
                executarDIV();
                break;
            case "LDV":
                endereco = Integer.parseInt(instrucao.getParametro());
                executarLDV(endereco-1);
                break;
            case "LDB":
                boolean constanteBoolean = Boolean.parseBoolean(instrucao.getParametro());
                executarLDB(constanteBoolean);
                break;
            case "LDI":
                int constanteInteira = Integer.parseInt(instrucao.getParametro());
                executarLDI(constanteInteira);
                break;
            case "LDR":
                float constanteFloat = Float.parseFloat(instrucao.getParametro());
                executarLDR(constanteFloat);
                break;
            case "LDS":
                String constanteString = instrucao.getParametro();
                executarLDS(constanteString);
                break;
            case "MUL":
                executarMUL();
                break;
            case "REA":
                int tipo = Integer.parseInt(instrucao.getParametro());
                executarREA(tipo);
                break;
            case "STR":
                endereco = Integer.parseInt(instrucao.getParametro());
                executarSTR(endereco-1);
                break;
            case "STC":
                deslocamento = Integer.parseInt(instrucao.getParametro());
                executarSTC(deslocamento);
                break;
            case "STP":
                executarSTP();
                break;
            case "SUB":
                executarSUB();
                break;
        }
    }

    public void executarADD() {
        if (!(pilha[topo] instanceof Number) || !(pilha[topo - 1] instanceof Number)) {
            throw new RuntimeException("Erro: Operandos para ADD devem ser numéricos.");
        }

        if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Integer) {
            int valor1 = (int) pilha[topo];
            int valor2 = (int) pilha[topo - 1];
            pilha[topo - 1] = valor1 + valor2;
            System.out.println("ADD - Resultado da soma de inteiros: " + (valor1 + valor2));
        } else {
            float valor1 = ((Number) pilha[topo]).floatValue();
            float valor2 = ((Number) pilha[topo - 1]).floatValue();
            pilha[topo - 1] = valor1 + valor2;
            System.out.println("ADD - Resultado da soma de reais: " + (valor1 + valor2));
        }

        topo--;
        ponteiro++;
        System.out.println("Instrução ADD executada. Novo topo: " + pilha[topo]);
    }

    public void executarALB(int deslocamento) {
        for (int i = topo + 1; i <= topo + deslocamento; i++) {
            pilha[i] = Boolean.FALSE;
        }
        topo += deslocamento;
        ponteiro++;
        System.out.println("Instrução ALB executada. Topo: " + (topo-deslocamento) + " Deslocamento: " + deslocamento + " Novo topo: " + topo);
    }

    public void executarALI(int deslocamento) {
        for (int i = topo + 1; i <= topo + deslocamento; i++) {
            pilha[i] = 0;
        }
        topo += deslocamento;
        ponteiro++;
        System.out.println("Instrução ALI executada. Topo: " + (topo-deslocamento) + " Deslocamento: " + deslocamento + " Novo topo: " + topo);
    }

    public void executarALR(int deslocamento) {
        for (int i = topo + 1; i <= topo + deslocamento; i++) {
            float valor = 0.0f;
            pilha[i] = valor;
        }
        topo += deslocamento;
        ponteiro++;
        System.out.println("Instrução ALR executada. Topo: " + (topo-deslocamento) + " Deslocamento: " + deslocamento + " Novo topo: " + topo);
    }

    public void executarALS(int deslocamento) {
        for (int i = topo + 1; i <= topo + deslocamento; i++) {
            pilha[i] = "";
        }
        topo += deslocamento;
        ponteiro++;
        System.out.println("Instrução ALS executada. Topo: " + (topo-deslocamento) + " Deslocamento: " + deslocamento + " Novo topo: " + topo);
    }

    public boolean executarAND() {
        if (!(pilha[topo] instanceof Boolean) || !(pilha[topo - 1] instanceof Boolean)) {
            throw new RuntimeException("Erro: Operandos para AND devem ser booleanos");
        }

        boolean valor1 = (Boolean) pilha[topo];
        boolean valor2 = (Boolean) pilha[topo - 1];
        boolean result = valor1 && valor2;

        pilha[topo - 1] = result;

        topo--;
        ponteiro++;
        System.out.println("Instrução AND executada. Resultado: " + pilha[topo]);
        return result;
    }

    public boolean executarBGE() {
        boolean resultado;
        System.out.println("------------------------------------------");
        System.out.println("BGE: Pilha topo: " + pilha[topo] + " " + pilha[topo].getClass().getName());
        System.out.println("BGE: Pilha topo-1: " + pilha[topo-1] + " " + pilha[topo-1].getClass().getName());
        System.out.println("------------------------------------------");

        if (pilha[topo - 1] instanceof Number && pilha[topo] instanceof Number) {
            if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Integer) {
                int valor1 = (int) pilha[topo - 1];
                int valor2 = (int) pilha[topo];
                resultado = valor1 >= valor2;
            } else {
                float valor1 = ((Number) pilha[topo - 1]).floatValue();
                float valor2 = ((Number) pilha[topo]).floatValue();
                resultado = valor1 >= valor2;
            }

            pilha[topo - 1] = resultado;
            topo--;
            ponteiro++;
            System.out.println("Instrução BGE executada. Resultado: " + pilha[topo]);

        }else throw new RuntimeException("Erro: Operandos para BGE devem ser do tipo numérico");
        return resultado;
    }

    public boolean executarBGR() {
        boolean resultado;
        System.out.println("------------------------------------------");
        System.out.println("BGR: Pilha topo: " + pilha[topo] + " " + pilha[topo].getClass().getName());
        System.out.println("BGR: Pilha topo-1: " + pilha[topo-1] + " " + pilha[topo-1].getClass().getName());
        System.out.println("------------------------------------------");

        if (pilha[topo - 1] instanceof Number && pilha[topo] instanceof Number) {
            if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Integer) {
                int valor1 = (int) pilha[topo - 1];
                int valor2 = (int) pilha[topo];
                resultado = valor1 > valor2;
            } else {
                float valor1 = ((Number) pilha[topo - 1]).floatValue();
                float valor2 = ((Number) pilha[topo]).floatValue();
                resultado = valor1 > valor2;
            }

            pilha[topo - 1] = resultado;
            topo--;
            ponteiro++;
            System.out.println("Instrução BGR executada. Resultado: " + pilha[topo]);

        }else throw new RuntimeException("Erro: Operandos para BGR devem ser do tipo numérico");
        return resultado;
    }

    public boolean executarDIF() {
        float valor1 = 0, valor2 = 0;
        boolean result = false, eh_numero = false;

        if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Float) {
            pilha[topo] = ((Integer) pilha[topo]).floatValue();
            valor1 = ((Number) pilha[topo - 1]).floatValue();
            valor2 = ((Number) pilha[topo]).floatValue();
            eh_numero = true;
        } else if (pilha[topo] instanceof Float && pilha[topo - 1] instanceof Integer) {
            pilha[topo - 1] = ((Integer) pilha[topo - 1]).floatValue();
            valor1 = ((Number) pilha[topo - 1]).floatValue();
            valor2 = ((Number) pilha[topo]).floatValue();
            eh_numero = true;
        } else if (!pilha[topo].getClass().equals(pilha[topo - 1].getClass())) {
            throw new RuntimeException("Erro: Operandos para EQL devem ser do mesmo tipo ou compatíveis.");
        } else {
            result = !pilha[topo].equals(pilha[topo-1]);
        }

        if (eh_numero) {
            result = valor1 == valor2;
        }


        pilha[topo - 1] = result;

        topo--;
        ponteiro++;
        System.out.println("Instrução EQL executada. Resultado: " + pilha[topo]);
        return result;
    }

    public void executarDIV() {
        if (pilha[topo] instanceof Number && ((Number) pilha[topo]).doubleValue() == 0) {
            throw new RuntimeException("Runtime error: Divisão por 0");
        }

        if (!(pilha[topo] instanceof Number) || !(pilha[topo - 1] instanceof Number)) {
            throw new RuntimeException("Erro: Divisão deve possuir apenas operadores numéricos");
        }

        float valor1 = ((Number) pilha[topo - 1]).floatValue();
        float valor2 = ((Number) pilha[topo]).floatValue();
        float resultado = valor1 / valor2;
        pilha[topo - 1] = resultado;

        topo--;
        ponteiro++;
        System.out.println("Instrução DIV executada. Resultado: " + resultado);
    }

    public boolean executarEQL() {
        float valor1 = 0, valor2 = 0;
        boolean result = false, eh_numero = false;

        if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Float) {
            pilha[topo] = ((Integer) pilha[topo]).floatValue();
            valor1 = ((Number) pilha[topo - 1]).floatValue();
            valor2 = ((Number) pilha[topo]).floatValue();
            eh_numero = true;
        } else if (pilha[topo] instanceof Float && pilha[topo - 1] instanceof Integer) {
            pilha[topo - 1] = ((Integer) pilha[topo - 1]).floatValue();
            valor1 = ((Number) pilha[topo - 1]).floatValue();
            valor2 = ((Number) pilha[topo]).floatValue();
            eh_numero = true;
        } else if (!pilha[topo].getClass().equals(pilha[topo - 1].getClass())) {
            throw new RuntimeException("Erro: Operandos para EQL devem ser do mesmo tipo ou compatíveis.");
        } else {
            result = pilha[topo].equals(pilha[topo-1]);
        }

        if (eh_numero) {
            result = valor1 == valor2;
        }


        pilha[topo - 1] = result;

        topo--;
        ponteiro++;
        System.out.println("Instrução EQL executada. Resultado: " + pilha[topo]);
        return result;
    }

    public void executarJMF(int endereco) {
        if (pilha[topo] instanceof Boolean && !(Boolean) pilha[topo])
            ponteiro = endereco;
        else
            ponteiro++;

        topo--;
        System.out.println("Instrução JMF executada. Novo ponteiro: " + ponteiro);
    }

    public void executarJMP(int endereco) {
        ponteiro = endereco;
        System.out.println("Instrução JMP executada. Novo ponteiro: " + ponteiro);
    }

    public void executarJMT(int endereco) {
        if (topo < 0) {
            throw new RuntimeException("Erro: Pilha vazia para JMT.");
        }

        if (pilha[topo] instanceof Boolean && (Boolean) pilha[topo]) {
            ponteiro = endereco;
        } else {
            ponteiro++;
        }

        topo--;
        System.out.println("Instrução JMT executada. Novo ponteiro: " + ponteiro);
    }

    public void executarLDV(int endereco) {
        System.out.println("LDV - endereço recebido: " + endereco);
        System.out.println("LDV - pilha endereço (valor que vai carregar): " + pilha[endereco]);
        System.out.println("LDV - pilha topo (onde o valor vai ser carregado): " + pilha[topo]);

        for (Object elemento: pilha) {
            if (elemento != null)
                System.out.println("elemento da pilha: " + elemento);
        }
        topo++;
        pilha[topo] = pilha[endereco];
        ponteiro++;
        System.out.println("Instrução LDV executada. Valor carregado: " + pilha[topo] + " , da posição: " + endereco);
    }

    public void executarLDB (boolean constante){
        topo++;
        pilha[topo] = constante;
        ponteiro++;
        System.out.println("Instrução LDB executada. Valor booleano carregado: " + pilha[topo]);
    }

    public void executarLDI (int constante){
        topo++;
        pilha[topo] = constante;
        ponteiro++;
        System.out.println("Instrução LDI executada. Valor inteiro carregado: " + pilha[topo]);
    }

    public void executarLDR (float constante){
        topo++;
        pilha[topo] = constante;
        ponteiro++;
        System.out.println("Instrução LDR executada. Valor real carregado: " + pilha[topo]);
    }

    public void executarLDS (String constante){
        topo++;
        pilha[topo] = constante;
        ponteiro++;
        System.out.println("Instrução LDS executada. String carregada: " + pilha[topo]);
    }

    public void executarMUL() {
        if (!(pilha[topo] instanceof Number) || !(pilha[topo - 1] instanceof Number)) {
            throw new RuntimeException("Erro: Multiplicação deve conter apenas operandos numéricos");
        }
        if (pilha[topo] instanceof Integer && pilha[topo-1] instanceof Integer) {
            int valor1 = (int) pilha[topo];
            int valor2 = (int) pilha[topo-1];
            System.out.println("Multiplicação inteira: " + (valor2 * valor1));
            pilha[topo-1] = valor2 * valor1;
        } else {
            float valor1 = ((Number) pilha[topo]).floatValue();
            float valor2 = ((Number) pilha[topo - 1]).floatValue();
            System.out.println("Multiplicação real: " + (valor2 * valor1));
            pilha[topo - 1] = valor2 * valor1;
        }
        topo--;
        ponteiro++;
        System.out.println("Instrução MUL executada. Resultado: " + pilha[topo]);
    }

    public boolean executarNOT() {
        if (!(pilha[topo] instanceof Boolean)) {
            throw new RuntimeException("Erro: Operando para NOT deve ser booleano");
        }

        pilha[topo] = !(Boolean) pilha[topo];
        boolean result = !(Boolean) pilha[topo];
        ponteiro++;
        System.out.println("Instrução NOT executada. Resultado: " + pilha[topo]);
        return result;
    }

    public boolean executarOR() {
        if (!(pilha[topo] instanceof Boolean) || !(pilha[topo - 1] instanceof Boolean)) {
            throw new RuntimeException("Erro: Operandos para OR devem ser booleanos");
        }

        boolean valor1 = (Boolean) pilha[topo - 1];
        boolean valor2 = (Boolean) pilha[topo];
        pilha[topo - 1] = valor1 || valor2;

        boolean result = valor1 || valor2;

        topo--;
        ponteiro++;
        System.out.println("Instrução OR executada. Resultado: " + pilha[topo]);
        return result;
    }

    public void executarREA(int tipo) {
        topo++;
        String input = terminal.solicitarInput("");

        switch (tipo) {
            case 1 -> {
                try {
                    pilha[topo] = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("RUNTIME error: tipo incorreto - Esperado int");
                }
            }
            case 2 -> {
                try {
                    pilha[topo] = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("RUNTIME error: tipo incorreto - esperado real");
                }
            }
            case 3 -> {
                try {
                    pilha[topo] = input;
                } catch (RuntimeException e) {
                    throw new RuntimeException("RUNTIME error: tipo incorreto - esperado char");
                }
            }
            default -> throw new RuntimeException("RUNTIME error: tipo incorreto");
        }

        ponteiro++;
        System.out.println("Instrução REA executada. Valor lido: " + pilha[topo]);
    }

    public boolean executarSME() {
        boolean resultado = false;
        if (pilha[topo - 1] instanceof Number && pilha[topo] instanceof Number) {
            if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Integer) {
                int valor1 = (int) pilha[topo-1];
                int valor2 = (int) pilha[topo];
                resultado = valor1 <= valor2;
            } else if (pilha[topo] instanceof Float && pilha[topo - 1] instanceof Float) {
                float valor1 = ((Number) pilha[topo - 1]).floatValue();
                float valor2 = ((Number) pilha[topo]).floatValue();
                resultado = valor1 <= valor2;
            }

            pilha[topo - 1] = resultado;
            topo--;
            ponteiro++;
        } else {
            throw new RuntimeException("Erro: Operandos para SME devem ser do tipo numérico.");
        }
        System.out.println("Instrução SME executada");

        return resultado;
    }

    public boolean executarSMR() {
        boolean resultado = false;
        if (pilha[topo - 1] instanceof Number && pilha[topo] instanceof Number) {
            if (pilha[topo] instanceof Integer && pilha[topo - 1] instanceof Integer) {
                int valor1 = (int) pilha[topo-1];
                int valor2 = (int) pilha[topo];
                resultado = valor1 < valor2;
            } else if (pilha[topo] instanceof Float && pilha[topo - 1] instanceof Float) {
                float valor1 = ((Number) pilha[topo - 1]).floatValue();
                float valor2 = ((Number) pilha[topo]).floatValue();
                resultado = valor1 < valor2;
            }

            pilha[topo - 1] = resultado;
            topo--;
            ponteiro++;
        } else {
            throw new RuntimeException("Erro: Operandos para SMR devem ser do tipo numérico.");
        }
        System.out.println("Instrução SMR executada");
        System.out.println("---------------------------");
        System.out.println("SMR RESULTADO: " + resultado);
        System.out.println("---------------------------");
        return resultado;
    }

    public void executarSTR(int endereco) { // TODO: Verificação de tipos aqui
        System.out.println("STR: Pilha endereço: " + pilha[endereco] + " " + pilha[endereco].getClass().getName());
        System.out.println("STR: Pilha topo: " + pilha[topo] + " " + pilha[topo].getClass().getName());

        switch (pilha[endereco]) {
            case Integer _ -> {
                if (!(pilha[topo] instanceof Integer)) {
                    throw new RuntimeException("Erro de tipo: Esperado Integer.");
                }
            }
            case Float _ -> {
                if (pilha[topo] instanceof Integer) {
                    pilha[topo] = ((Integer) pilha[topo]).floatValue();
                } else if (!(pilha[topo] instanceof Float)) {
                    throw new RuntimeException("Erro de tipo: Esperado Float ou Integer.");
                }
            }
            case String _ -> {
                if (!(pilha[topo] instanceof String)) {
                    throw new RuntimeException("Erro de tipo: Esperado String.");
                }
            }
            case Boolean _ -> {
                if (!(pilha[topo] instanceof Boolean)) {
                    throw new RuntimeException("Erro de tipo: Esperado Boolean.");
                }
            }
            case null, default -> throw new RuntimeException("Erro de tipo: Tipo desconhecido em pilha[endereço].");
        }

        pilha[endereco] = pilha[topo];
        topo--;
        ponteiro++;
        System.out.println("Instrução STR executada - Pilha[endereço] " + pilha[endereco]);
    }


    public void executarSTP() {
        //String output = "Execução do programa finalizada";
        System.out.println("Instrução STP executada");
        //System.out.println(output);
    }

    public void executarSUB() {
        if (pilha[topo] instanceof Number && pilha[topo - 1] instanceof Number) {
            if (pilha[topo] instanceof Integer && pilha[topo-1] instanceof Integer) {
                int valor1 = (int) pilha[topo];
                int valor2 = (int) pilha[topo-1];
                System.out.println("Subtração inteira: " + (valor2-valor1));
                pilha[topo-1] = valor2 - valor1;
            }
            else {
                float valor1 = ((Number) pilha[topo]).floatValue();
                float valor2 = ((Number) pilha[topo - 1]).floatValue();
                System.out.println("Subtração real: " + (valor2-valor1));
                pilha[topo-1] = valor2 - valor1;
            }
            topo--;
            ponteiro++;
        } else {
            throw new RuntimeException("Erro: Subtração deve conter somente operadores numéricos");
        }
        System.out.println("Instrução SUB executada");
    }

    public Object executarWRT() {
        System.out.println("Topo da pilha: " + pilha[topo]);
        Object valor = pilha[topo];
        topo--;

        if (valor instanceof String mensagem) {
            mensagem = mensagem.replaceAll("^['\"]|['\"]$", "");
            valor = mensagem;
        }

        ponteiro++;
        System.out.println("Instrução WRT executada");
        return valor;
    }

    public void executarSTC(int deslocamento) {
        for (int i = topo - deslocamento; i < topo; i++) {
            pilha[i] = pilha[topo];
        }

        topo--;
        ponteiro++;
        System.out.println("Instrução STC executada. Topo atualizado: " + topo);
    }
}