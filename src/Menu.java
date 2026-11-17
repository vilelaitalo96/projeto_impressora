/** Só mostra o menu na tela. Os números aqui são os mesmos usados no switch do Main. */
public class Menu {

    public static final int SAIR = 0;
    public static final int MAIOR_OPCAO = 12;

    public static void exibir() {
        System.out.println();
        System.out.println("===== MENU IMPRESSORA ELGIN =====");
        System.out.println(" 1 - Configurar conexão");
        System.out.println(" 2 - Abrir conexão");
        System.out.println(" 3 - Fechar conexão");
        System.out.println(" 4 - Imprimir texto");
        System.out.println(" 5 - Imprimir QR Code");
        System.out.println(" 6 - Imprimir código de barras");
        System.out.println(" 7 - Imprimir XML SAT");
        System.out.println(" 8 - Imprimir XML de cancelamento SAT");
        System.out.println(" 9 - Abrir gaveta");
        System.out.println("10 - Sinal sonoro (bip)");
        System.out.println("11 - Avançar papel");
        System.out.println("12 - Status do papel");
        System.out.println(" 0 - Sair");
    }

    /** Opções de 4 a 12 só funcionam com a impressora conectada. */
    public static boolean exigeConexao(int opcao) {
        return opcao >= 4 && opcao <= 12;
    }
}
