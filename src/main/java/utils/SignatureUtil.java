package utils;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import persistence.AccountPersistence;

import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;

/**
 * Helper class to calculate and validate signatures
 */
public class SignatureUtil {

    //Signature curve name
    private static final String CURVE_NAME = "secp256k1";
    //Signature algorithm
    private static final String ALGORITHM = "ECDSA";
    //Signature provider
    private static final String PROVIDER = "BC";
    //Signature curve
    private static final X9ECParameters CURVE = SECNamedCurves.getByName(CURVE_NAME);
    //Signature domain
    private static final ECDomainParameters DOMAIN =
            new ECDomainParameters(CURVE.getCurve(), CURVE.getG(), CURVE.getN(), CURVE.getH());


    /**
     * Verifies a given hash and signature depending on the public key
     *
     * @param hash      HAsh to verify
     * @param signature Signature to verify
     * @param publicKey Public key
     * @return Boolean if the given hash and signature is valid or nit
     */
    public static boolean verify(byte[] hash, byte[] signature, byte[] publicKey) {
        boolean result;

        try (ASN1InputStream asn1 = new ASN1InputStream(signature)) {
            ECDSASigner signer = new ECDSASigner();

            signer.init(false, new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), DOMAIN));

            DLSequence seq = (DLSequence) asn1.readObject();
            BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
            BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
            result = signer.verifySignature(hash, r, s);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Generates a new public/private key pair
     *
     * @return New public/private key pair
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair;

        try {
            Security.addProvider(new BouncyCastleProvider());
            ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
            KeyPairGenerator g = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            g.initialize(ecSpec, new SecureRandom());
            keyPair = g.generateKeyPair();
        } catch (Exception e) {
            keyPair = null;
        }
        return keyPair;
    }

    /**
     * Saves a given keypair to a miner
     *
     * @param keyPair Keypair to save
     * @param minerId Miner Id to save the keypair to
     */
    public static void saveKeyPair(KeyPair keyPair, String minerId) {
        AccountPersistence accountPersistence = new AccountPersistence();
        accountPersistence.saveKeyPair(keyPair, minerId);
    }

    /**
     * Returns the coinbase from a given keypair
     *
     * @param keyPair Keypair to get the coinbase from
     * @return Coinbase of the keypair
     */
    public static byte[] getCoinbaseFromPublicKey(KeyPair keyPair) {
        return getCoinbaseFromPublicKey(keyPair.getPublic());
    }

    /**
     * Returns the coinbase from a public key
     *
     * @param key Public key to get the coinbase from
     * @return Coinabse of the public key
     */
    public static byte[] getCoinbaseFromPublicKey(PublicKey key) {
        return Arrays.copyOfRange(key.getEncoded(), 23, 88);
    }
}
