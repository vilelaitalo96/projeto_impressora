import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Representa a impressora Elgin: guarda os dados da conexão
 * e tem um método para cada operação (abrir conexão, imprimir texto, bip...).
 */
public class Impressora {

    // ---------- constantes ----------
    // Assinatura do QR Code do XML de cancelamento (copiada do PDF, sem espaços)
    private static final String ASSINATURA_QRCODE_CANCELAMENTO = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    // Tipos de código de barras aceitos: 0 = UPC-A, 2 = EAN13, 3 = EAN8, 4 = CODE 39, 8 = CODE 128
    public static final int[] TIPOS_BARRAS = {0, 2, 3, 4, 8};
    public static final String LEGENDA_BARRAS =
            "0 = UPC-A | 2 = EAN13 | 3 = EAN8 | 4 = CODE 39 | 8 = CODE 128";

    private static final int AVANCO_CORTE = 5; // quanto o papel anda antes do corte
    private static final int NIVEL_CORRECAO_QRCODE = 2;
    private static final int ALTURA_BARRAS = 100;
    private static final int LARGURA_BARRAS = 2;
    private static final int HRI_ABAIXO = 2;

    // ---------- atributos ----------
    private int tipo;            // 1 = USB, 2 = Serial, 3 = Rede, 4 = Bluetooth
    private String modelo;       // "i8" ou "i9"
    private String conexao;      // "USB", "COM2" ou um IP
    private int parametro;       // 0 para USB; velocidade (serial) ou porta (rede)
    private boolean conexaoAberta; // só vira true quando a DLL devolve 0 ao abrir

    // Já começa com os valores sugeridos para teste: USB, i9
    public Impressora() {
        this.tipo = 1;
        this.modelo = "i9";
        this.conexao = "USB";
        this.parametro = 0;
        this.conexaoAberta = false;
    }

    // ---------- métodos de apoio ----------

    /** Confere o número devolvido pela DLL: 0 = sucesso, qualquer outro = erro. */
    private boolean conferir(String operacao, int retorno) {
        if (retorno == 0) {
            System.out.println(operacao + ": sucesso.");
            return true;
        }
        System.out.println("Erro em " + operacao + ". Código: " + retorno);
        return false;
    }

    /** Depois de imprimir, avança o papel e corta (comando Corte da DLL). */
    private void cortarAposImpressao() {
        int retorno = ImpressoraDLL.INSTANCE.Corte(AVANCO_CORTE);
        if (retorno == 0) {
            System.out.println("Papel cortado.");
        } else {
            System.out.println("Erro ao cortar o papel. Código: " + retorno);
        }
    }

    /** Lê o conteúdo de um arquivo XML. Devolve null se não conseguir. */
    private String lerArquivo(String caminho) {
        try {
            return Files.readString(Path.of(caminho));
        } catch (NoSuchFileException e) {
            System.out.println("Arquivo não encontrado: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Caminho inválido: " + caminho);
        }
        return null;
    }

    // ---------- conexão ----------

    public boolean isConexaoAberta() {
        return conexaoAberta;
    }

    /** Se a conexão estiver fechada, avisa e devolve false (usado antes de pedir qualquer dado). */
    public boolean verificarConexao() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes.");
        }
        return conexaoAberta;
    }

    public void configurar(int tipo, String modelo, String conexao, int parametro) {
        if (conexaoAberta) {
            System.out.println("Feche a conexão antes de mudar a configuração.");
            return;
        }
        this.tipo = tipo;
        this.modelo = modelo;
        this.conexao = conexao;
        this.parametro = parametro;
        System.out.println("Configuração salva.");
    }

    public void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("A conexão já está aberta.");
            return;
        }
        try {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            conexaoAberta = conferir("Abrir conexão", retorno);
        } catch (LinkageError e) {
            // LinkageError cobre UnsatisfiedLinkError e falha ao carregar a DLL
            System.out.println("Não foi possível carregar a DLL: " + e.getMessage());
        }
    }

    public void fecharConexao() {
        if (!conexaoAberta) {
            System.out.println("A conexão já está fechada.");
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (conferir("Fechar conexão", retorno)) {
            conexaoAberta = false;
        }
    }

    // ---------- impressão ----------

    public void imprimirTexto(String texto, int copias) {
        for (int i = 1; i <= copias; i++) {
            // Aqui só número NEGATIVO é erro (o retorno positivo é a quantidade enviada)
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto + "\n", 1, 0, 0);
            if (retorno < 0) {
                System.out.println("Erro ao imprimir a cópia " + i + ". Código: " + retorno);
                return;
            }
        }
        cortarAposImpressao();
        System.out.println(copias + " cópia(s) impressa(s).");
    }

    public void imprimirQRCode(String dados, int tamanho) {
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, tamanho, NIVEL_CORRECAO_QRCODE);
        if (conferir("QR Code", retorno)) {
            cortarAposImpressao();
        }
    }

    public void imprimirCodigoBarras(int tipoBarras, String dados) {
        // O CODE 128 precisa do prefixo "{A" no começo dos dados
        if (tipoBarras == 8 && !dados.startsWith("{")) {
            dados = "{A" + dados;
        }
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(
                tipoBarras, dados, ALTURA_BARRAS, LARGURA_BARRAS, HRI_ABAIXO);
        if (conferir("Código de barras", retorno)) {
            cortarAposImpressao();
        }
    }

    public void imprimirXmlSat(String arquivo) {
        String xml = lerArquivo(arquivo);
        if (xml == null) {
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);
        conferir("Imprimir XML SAT", retorno);
    }

    public void imprimirXmlCancelamento(String arquivo) {
        String xml = lerArquivo(arquivo);
        if (xml == null) {
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(
                xml, ASSINATURA_QRCODE_CANCELAMENTO, 0);
        conferir("Imprimir cancelamento SAT", retorno);
    }

    // ---------- gaveta, bip, papel ----------

    public void abrirGaveta() {
        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
        conferir("Abrir gaveta", retorno);
    }

    public void sinalSonoro(int quantidade) {
        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5);
        if (retorno == 0) {
            System.out.println("Bip!");
        } else {
            System.out.println("Erro no sinal sonoro. Código: " + retorno);
        }
    }

    public void avancarPapel(int linhas) {
        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        conferir("Avançar papel", retorno);
    }

    public void verificarPapel() {
        int retorno = ImpressoraDLL.INSTANCE.StatusImpressora(3);
        if (retorno == 5) {
            System.out.println("Papel OK.");
        } else if (retorno == 6) {
            System.out.println("Papel acabando.");
        } else if (retorno == 7) {
            System.out.println("Sem papel!");
        } else {
            System.out.println("Não foi possível ler o status do papel. Código: " + retorno);
        }
    }
}
