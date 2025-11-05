# Trabalho de Compiladores - 2024/2
Alunos: Arthur Pereira, Arthur Maia e Rafael Ratti

## TRABALHO 5 - erros:
1. Corrigir os erros que o professor mandou:
   - Acusando menos erros sintáticos do que, de fato, têm (teste6.txt)

## TRABALHO 6 - TODO:
1. Fazer o terminal fechar quando executar o programa de novo
2. Implementar a verificação de tipos:
   1. Em comando de atribuição, o resultado da avaliação da expressão deve ser um valor de mesmo tipo ou tipo
   compatível com o da variável. Assim, uma variável do tipo inteiro só pode receber valores inteiros, uma variável
   do tipo real só pode receber valores inteiros ou reais (um valor inteiro quando atribuído a uma variável do tipo
   real deve ser convertido em valor real), uma variável do tipo literal só pode receber valores literais e uma variável
   do tipo lógico só pode receber valores lógicos;
   2. Os operadores relacionais podem ser utilizados para comparar operandos de tipos compatíveis identificados no
   primeiro item.

## Devem ser entregues:
- Programa fonte do projeto
- arquivo javaCC
- .JAR do projeto
- lista impressa contendo os erros léxicos, sintáticos e semânticos
- lista impressa com a descrição das instruções da máquina virtual especificadas ou alteradas.

## Como rodar o javaCC
1. instale o javaCC
2. na pasta src do projeto, rode o comando 'javacc file_cc.jj'
3. rode o arquivo Main