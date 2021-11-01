package com.netease.util;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptUtil{
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);

    private static final String ALGORITHM = "AES";

    private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";

    private static final String IV_ = null;

    private static final Charset CHARACTER = StandardCharsets.UTF_8;

    private static final int PWD_SIZE = 16;

    private static byte[] pwdHandler(String password) {
        if (password == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(PWD_SIZE);
        sb.append(password);
        while (sb.length() < PWD_SIZE) {
            sb.append("0");
        }
        if (sb.length() > PWD_SIZE) {
            sb.setLength(PWD_SIZE);
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(clearTextBytes);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static byte[] decrypt(byte[] cipherTextBytes, byte[] pwdBytes) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_MODE);

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            return cipher.doFinal(cipherTextBytes);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String encryptBase64(String rawText, String password, boolean urlMode) {
        if(Strings.isNullOrEmpty(password)){
            return null;
        }
        try {
            byte[] cipherTextBytes = encrypt(rawText.getBytes(CHARACTER), pwdHandler(password));
            Base64.Encoder encoder = urlMode ? Base64.getUrlEncoder() : Base64.getEncoder();
            return encoder.encodeToString(cipherTextBytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String decryptBase64(String cipherText, String password, boolean urlMode) {
        try {
            Base64.Decoder encoder = urlMode ? Base64.getUrlDecoder() : Base64.getDecoder();
            byte[] cipherTextBytes = encoder.decode(cipherText);

            byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));
            if(clearTextBytes == null){
                return null;
            }
            return new String(clearTextBytes, CHARACTER);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static String encryptHex(String clearText, String password) {
        try {
            byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));

            assert cipherTextBytes != null;
            return Hex.encodeHexString(cipherTextBytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static String decryptHex(String cipherText, String password) {
        try {
            byte[] cipherTextBytes = Hex.decodeHex(cipherText);

            byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));

            assert clearTextBytes != null;
            return new String(clearTextBytes, CHARACTER);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static void main(String[] args) {
        String test = encryptHex("test", "1234567800000000");
        System.out.println(test);

        System.out.println(decryptHex(test, "1234567800000000"));
    }
}