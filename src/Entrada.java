import java.util.Scanner;

/**
 * Cuida de toda a leitura do teclado.
 * Repete a pergunta até a pessoa digitar algo válido (assim o programa não trava).
 */
public class Entrada {

    private static final Scanner teclado = new Scanner(System.in);


    public static String texto(String pergunta) {
        System.out.print(pergunta);
        return teclado.nextLine();
    }


    public static int numero(String pergunta, int minimo, int maximo) {
        while (true) {
            String digitado = texto(pergunta).trim();
            try {
                int valor = Integer.parseInt(digitado);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Digite um valor de " + minimo + " a " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Isso não é um número válido.");
            }
        }
    }

    /** Lê um número que precisa estar na lista de valores permitidos. */
    public static int numeroPermitido(String pergunta, int[] permitidos) {
        while (true) {
            int valor = numero(pergunta, Integer.MIN_VALUE, Integer.MAX_VALUE);
            for (int i = 0; i < permitidos.length; i++) {
                if (permitidos[i] == valor) {
                    return valor;
                }
            }
            System.out.println("Opção inválida.");
        }
    }

    /** Lê um texto que precisa seguir o padrão (regex). Repete até acertar. */
    public static String textoValido(String pergunta, String regex, String mensagemErro) {
        String valor = texto(pergunta).trim();
        while (!valor.matches(regex)) {
            System.out.println(mensagemErro);
            valor = texto(pergunta).trim();
        }
        return valor;
    }
}
