package utils;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Helperclass to calculate the hash of objects
 */
public final class SHA3Util {

    private SHA3Util() {
    }

    /**
     * Forms a given digest to a hex String
     *
     * @param digest Given digest
     * @return Hex String
     */
    public static String digestToHex(byte[] digest) {
        return Hex.toHexString(digest);
    }

    /**
     * HAshes a given object and returns it as hex String
     *
     * @param o Object to hash
     * @return Hash of object as hex String
     */
    public static String hash256AsHex(Serializable o) {
        return Hex.toHexString(hash256(o));
    }

    /**
     * Hashes a given object and returns the hash
     *
     * @param o Object to hash
     * @return Hash of the object
     */
    public static byte[] hash256(Serializable o) {
        byte[] digest = new byte[0];

        ByteArrayOutputStream byteArrayOutputStreamos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamos);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();

            digest = hash256(byteArrayOutputStreamos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return digest;
    }

    /**
     * Hashes a given byte Array
     *
     * @param bytes Byte Array to hash
     * @return Hash of the byte Array
     */
    public static byte[] hash256(byte[] bytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

        return digestSHA3.digest(bytes);
    }

    /**
     * decodes a hex String to a byte Array
     *
     * @param hex Hex String
     * @return Decoded byte Array
     */
    public static byte[] hexToDigest(String hex) {
        return Hex.decode(hex);
    }
}
