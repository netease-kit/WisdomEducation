package com.netease.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoUtil {

    private static Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    public static String encodeHmacMD5(String secret, String rawString)
        throws Exception {
        SecretKey key = new SecretKeySpec(Hex.decodeHex(secret.toCharArray()),
            "DESede");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);

        byte[] signBytes = mac.doFinal(rawString.getBytes("UTF-8"));
        return Hex.encodeHexString(signBytes);
    }

    public static String encodeBase64HmacSHA256(String key, byte[] bytes)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(),
            "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] buf = sha256_HMAC.doFinal(bytes);
        return Base64.encodeBase64String(buf);
    }

    public static byte[] do128AESDecrypt(byte[] content, byte[] sKey)
        throws Exception {

        if (sKey == null)
            throw new Exception("Decrpty failed, skey = null");
        if (sKey.length != 16)
            throw new Exception("Decrpty failed, skey length is not 16");
        byte[] raw = sKey;
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec/* , IV_PARAMETER_SPEC */);
        return cipher.doFinal(content);
    }

    public static byte[] do128AESEncrypt(byte[] rawBytes, byte[] sKey)
        throws Exception {

        if (sKey == null)
            throw new Exception("Encrpty failed, skey = null");
        if (sKey.length != 16)
            throw new Exception("Encrpty failed, skey length is not 16");
        byte[] raw = sKey;
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec/* , IV_PARAMETER_SPEC */);
        return cipher.doFinal(rawBytes);
    }

    public static byte[] get128AESKey() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //AES 要求密钥长度为 128
        kg.init(128);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] doDESDecrypt(byte[] content, byte[] sKey)
        throws Exception {
        if (sKey == null) {
            throw new Exception("Decrpty failed, skey = null");
        }
        byte[] raw = sKey;
        if (sKey.length > 8) {
            raw = Arrays.copyOf(sKey, 8);
        }

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(content);
    }

    public static byte[] doDESEncrypt(byte[] content, byte[] sKey)
        throws Exception {
        if (sKey == null) {
            throw new Exception("Encrypt failed, skey = null");
        }
        byte[] raw = sKey;
        if (sKey.length > 8) {
            raw = Arrays.copyOf(sKey, 8);
        }

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(content);
    }

    public static byte[] getDESKey() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象  
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小  
        kg.init(56);
        //生成一个密钥  
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static void main(String[] args) throws Exception {
        byte[] key = getDESKey();
        System.out.println(Base64.encodeBase64String(key));
        byte[] rawBytes = "d4546f2lj".getBytes();
        System.out.println(Base64.encodeBase64String(rawBytes));
        byte[] encodeBytes = doDESEncrypt(rawBytes, key);
        System.out.println(Base64.encodeBase64String(encodeBytes));
        byte[] decodeBytes = doDESDecrypt(encodeBytes, key);
        System.out.println(Base64.encodeBase64String(decodeBytes));
    }
}
