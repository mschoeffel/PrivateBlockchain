package persistence;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import utils.SHA3Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * Class to store a account persistent
 */
public class AccountPersistence {

    private Charset encoding = StandardCharsets.UTF_8;
    private String path = "accounts/";

    /**
     * Creates a new account persistence
     */
    public AccountPersistence() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * Writes a account key pair to the account storage
     * @param keyPair KeyPair to store
     * @param minerId Id of the corresponding miner
     */
    public void saveKeyPair(KeyPair keyPair, String minerId) {
        File file = new File(path + minerId + ".json");

        try (OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
            outputStream.write("{\"publicKey\":\"");
            outputStream.write(SHA3Util.digestToHex(keyPair.getPublic().getEncoded()).substring(46));
            outputStream.write("\",\n\"privateKey\":\"");
            outputStream.write(SHA3Util.digestToHex(((BCECPrivateKey) keyPair.getPrivate()).getD().toByteArray()));
            outputStream.write("\"}");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
