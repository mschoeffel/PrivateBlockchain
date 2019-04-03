package utils;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SHA3Helper {

    public static String digestToHex(byte[] digest){
        return Hex.toHexString(digest);
    }

    public static byte[] hash256(Serializable o) {
        byte[] digest = new byte[0];

        ByteArrayOutputStream byteArrayOutputStreamos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamos);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();

            digest = hash256(byteArrayOutputStreamos.toByteArray());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return digest;
    }

    public static byte[] hash256( byte[] bytes )
    {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256( );

        return digestSHA3.digest( bytes );
    }
}
