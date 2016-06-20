package com.project.bachelor.usi.voipcryptocaller.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryptor {

    private static Cipher cipher;

    static public void init(byte[] key, byte[] initVector, String mode){

        IvParameterSpec iv = null;
        try {
            iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);



        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    static public byte[] dec(byte[] ciphertext){

        try {
            byte[] original = cipher.doFinal(ciphertext);
            return original;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }


        return null;
    }

}