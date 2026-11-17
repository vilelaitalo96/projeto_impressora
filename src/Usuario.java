import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Usuario {

    // Atributos (as características do usuário)
    private String login;
    private byte[] hashSenha;

    public Usuario(String login, String senha) {
        this.login = login;
        this.hashSenha = gerarHash(senha);
    }


    public boolean confere(String loginDigitado, String senhaDigitada) {
        boolean loginCerto = login.equals(loginDigitado);
        boolean senhaCerta = MessageDigest.isEqual(hashSenha, gerarHash(senhaDigitada));
        return loginCerto && senhaCerta;
    }

    private byte[] gerarHash(String texto) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return sha.digest(texto.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}
