package com.project.bachelor.usi.voipcryptocaller.security;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.Security;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.ECGenParameterSpec;
import javax.crypto.KeyAgreement;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.math.ec.ECPoint;

public class ECDHManager {
    static {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte [] savePublicKey (PublicKey key) throws Exception {
        ECPublicKey eckey = (ECPublicKey)key;
        return eckey.getQ().getEncoded(true);
    }

    public static PublicKey loadPublicKey (byte [] data) throws Exception {
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");
        return kf.generatePublic(pubKey);
    }

    public static byte [] savePrivateKey (PrivateKey key) throws Exception {
        ECPrivateKey eckey = (ECPrivateKey)key;
        return eckey.getD().toByteArray();
    }

    public static PrivateKey loadPrivateKey (byte [] data) throws Exception {
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");
        return kf.generatePrivate(prvkey);
    }

    public static byte[] doECDH (String name, byte[] dataPrv, byte[] dataPub) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "SC");
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKey(dataPub), true);
        byte [] secret = ka.generateSecret();
        return secret;
    }

    public static KeyPair initiate() {
        KeyPairGenerator kpgen = null;

        KeyPair kp = null;
        try {
            kpgen = KeyPairGenerator.getInstance("ECDH", "SC");
            kpgen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            kp = kpgen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return kp;
    }
}
