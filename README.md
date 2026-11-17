# Sistema para Controlar a Impressora Elgin

## Sobre o projeto
Programa de console em Java, feito para a disciplina de Programação Orientada a Objetos. Ele pede login, mostra um menu e envia comandos para uma impressora térmica Elgin (i8 ou i9): imprimir texto, QR Code, código de barras, cupons SAT, abrir a gaveta, apitar e mais.

## Tecnologias
- Java (JDK 11 ou superior; o JDK de 64 bits usa a DLL x64 e o de 32 bits usa a x86)
- JNA 5.15.0 (liga o Java à DLL da Elgin)
- DLL `E1_Impressora01` (x64 e x86) da Elgin
- IntelliJ IDEA

## Estrutura
```
projeto_impressora/
├── src/
│   ├── Main.java           -> cria os objetos, faz o login e mostra o menu
│   ├── Usuario.java        -> guarda login e senha (hash SHA-256) e confere
│   ├── Impressora.java     -> dados da conexão + um método para cada operação
│   ├── Menu.java           -> exibe as opções do menu
│   ├── Entrada.java        -> leitura do teclado com validação
│   └── ImpressoraDLL.java  -> ligação com a DLL (fornecido, não alterar)
├── Bibliotecas E1 Impressora/  (x64 e x86)
├── libs Java/jna-5.15.0.jar
├── XMLSAT.xml
└── CANC_SAT.xml
```

## Como executar
1. Abra o projeto no IntelliJ.
2. Adicione o JNA: File > Project Structure > Libraries > + > Java > `libs Java/jna-5.15.0.jar`.
3. Em Run > Edit Configurations, deixe o **Working directory** na pasta principal do projeto (onde está `Bibliotecas E1 Impressora`).
4. Ligue a impressora, conecte o cabo USB e execute a classe `Main`.

## Login de teste
- Usuário: `admin`
- Senha: `1234`

São 3 tentativas. A cada erro o programa mostra quantas restam; errou as 3, ele encerra.

## Funcionalidades do menu
| # | Opção | Comando da DLL |
|---|-------|----------------|
| 1 | Configurar conexão | (dados digitados) |
| 2 | Abrir conexão | AbreConexaoImpressora |
| 3 | Fechar conexão | FechaConexaoImpressora |
| 4 | Imprimir texto (1 a 5 cópias) + corte | ImpressaoTexto, Corte |
| 5 | Imprimir QR Code + corte | ImpressaoQRCode, Corte |
| 6 | Imprimir código de barras + corte | ImpressaoCodigoBarras, Corte |
| 7 | Imprimir XML SAT | ImprimeXMLSAT |
| 8 | Imprimir XML de cancelamento SAT | ImprimeXMLCancelamentoSAT |
| 9 | Abrir gaveta | AbreGaveta |
| 10 | Sinal sonoro (bip) | SinalSonoro |
| 11 | Avançar papel | AvancaPapel |
| 12 | Status do papel | StatusImpressora |
| 0 | Sair | |

Regras importantes:
- Opções de 4 a 12 só funcionam com a conexão aberta. Se não estiver, o programa avisa "Conexão fechada. Abra a conexão antes." e volta ao menu, sem perguntar nada.
- Toda chamada à DLL tem o retorno conferido (0 = sucesso). No texto, só retorno negativo é erro.
- Letras no lugar de números, arquivo XML inexistente e DLL que não carrega são tratados com try/catch.

## Testes feitos
- [x] Login certo e login errado 3 vezes
- [x] Opções 4 a 12 bloqueadas com conexão fechada
- [x] Letras e números fora da faixa no menu e nas perguntas
- [x] DLL não carregada (mensagem de erro, sem travar)
- [ ] Cada opção testada na impressora (preencher após o teste em sala)

## Autores
- (nomes dos integrantes do grupo)
