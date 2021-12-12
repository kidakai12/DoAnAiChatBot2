package Sercurity;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtil {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAUtil() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PublicKey getPublickey() {
        return publicKey;
    }

    public void setPublickey(PublicKey key) {
        this.publicKey = key;
    }

    public PrivateKey getPrivatekey() {
        return privateKey;
    }

    public String Encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        String result;
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, getPublickey());

        byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        result = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        return result;
    }

    public String Decrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String result;
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, getPrivatekey());

        byte[] data = Base64.getDecoder().decode(message);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(data);
        result = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

        return result;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String secretMessage = "Baeldung secret message";
        RSAUtil rs = new RSAUtil();

        System.out.println(rs.Encrypt("Hello world"));

        System.out.println(rs.Decrypt(rs.Encrypt("Hello world")));
    }

}
