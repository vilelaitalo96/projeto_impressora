
public class Main {

    private static final int MAX_TENTATIVAS = 3;
    private static final String XML_VENDA_PADRAO = "XMLSAT.xml";
    private static final String XML_CANCELAMENTO_PADRAO = "CANC_SAT.xml";

    public static void main(String[] args) {
        Usuario usuario = new Usuario("admin", "1234");

        if (!fazerLogin(usuario)) {
            return; // errou 3 vezes: encerra o programa
        }

        Impressora impressora = new Impressora();
        int opcao;
        do {
            Menu.exibir();
            opcao = Entrada.numero("Escolha uma opção: ", 0, Menu.MAIOR_OPCAO);
            executarOpcao(opcao, impressora);
        } while (opcao != Menu.SAIR);

        // Antes de sair, fecha a conexão se ela ainda estiver aberta
        if (impressora.isConexaoAberta()) {
            impressora.fecharConexao();
        }
    }

    /** Login com no máximo 3 tentativas. */
    private static boolean fazerLogin(Usuario usuario) {
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            String login = Entrada.texto("Usuário: ");
            String senha = Entrada.texto("Senha: ");

            if (usuario.confere(login, senha)) {
                System.out.println("Login realizado com sucesso!");
                return true;
            }
            int restantes = MAX_TENTATIVAS - tentativa;
            System.out.println("Usuário ou senha incorretos. Tentativas restantes: " + restantes);
        }
        System.out.println("Número máximo de tentativas atingido. Encerrando o programa.");
        return false;
    }

    private static void executarOpcao(int opcao, Impressora impressora) {
        // Regra: sem conexão aberta, nem pergunta nada de impressão
        if (Menu.exigeConexao(opcao) && !impressora.verificarConexao()) {
            return;
        }

        switch (opcao) {
            case 1:
                configurarConexao(impressora);
                break;
            case 2:
                impressora.abrirConexao();
                break;
            case 3:
                impressora.fecharConexao();
                break;
            case 4:
                String texto = Entrada.texto("Texto a imprimir: ");
                int copias = Entrada.numero("Quantas cópias (1 a 5)? ", 1, 5);
                impressora.imprimirTexto(texto, copias);
                break;
            case 5:
                String conteudo = Entrada.texto("Conteúdo do QR Code: ");
                int tamanho = Entrada.numero("Tamanho (1 a 6): ", 1, 6);
                impressora.imprimirQRCode(conteudo, tamanho);
                break;
            case 6:
                System.out.println("Tipos: " + Impressora.LEGENDA_BARRAS);
                int tipoBarras = Entrada.numeroPermitido("Tipo do código: ", Impressora.TIPOS_BARRAS);
                String dados = Entrada.texto("Dados do código: ");
                impressora.imprimirCodigoBarras(tipoBarras, dados);
                break;
            case 7:
                impressora.imprimirXmlSat(perguntarArquivo(XML_VENDA_PADRAO));
                break;
            case 8:
                impressora.imprimirXmlCancelamento(perguntarArquivo(XML_CANCELAMENTO_PADRAO));
                break;
            case 9:
                impressora.abrirGaveta();
                break;
            case 10:
                int bips = Entrada.numero("Quantos bips (1 a 63)? ", 1, 63);
                impressora.sinalSonoro(bips);
                break;
            case 11:
                int linhas = Entrada.numero("Quantas linhas (1 a 10)? ", 1, 10);
                impressora.avancarPapel(linhas);
                break;
            case 12:
                impressora.verificarPapel();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
        }
    }

    private static void configurarConexao(Impressora impressora) {
        int tipo = Entrada.numero("Tipo (1=USB, 2=Serial, 3=Rede, 4=Bluetooth): ", 1, 4);
        String modelo = Entrada.textoValido("Modelo (i8 ou i9): ",
                "(?i)i8|i9", "Modelo inválido.").toLowerCase();
        String conexao = Entrada.textoValido("Conexão (USB, COM2 ou IP): ",
                "(?i)USB|COM2|\\d{1,3}(\\.\\d{1,3}){3}", "Conexão inválida.").toUpperCase();
        int parametro = Entrada.numero("Parâmetro (0 para USB): ", 0, 999999);
        impressora.configurar(tipo, modelo, conexao, parametro);
    }

    private static String perguntarArquivo(String padrao) {
        String digitado = Entrada.texto("Arquivo XML (Enter = " + padrao + "): ").trim();
        if (digitado.isEmpty()) {
            return padrao;
        }
        return digitado;
    }
}
