package com.example.demo.util;

import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SignUtils {

    /**
     * 加密的块大小
     */
    private static final int encryptBlockSize = 117;
    /**
     * 解密的块大小
     */
    private static final int decryptBlockSize = 128;

    /**
     * 加密算法
     */
    private static final String rsaAlgorithm = "RSA";
    /**
     * 签名
     */
    private static final String signAlgorithm = "MD5withRSA";

    /**
     * key长度
     */
    private static int keySize = 1024;


    /**
     * 生成公钥私钥 Map:0公钥/1私钥
     *
     * @apiNote 公钥和私钥是base64编码
     */
    public static Map<Integer, String> genKeyPair() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance(rsaAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成公钥私钥失败", e);
        }
        keyPairGen.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Map<Integer, String> map = new HashMap<>();
        map.put(0, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        map.put(1, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        return map;
    }

    /**
     * 私钥加密
     *
     * @param data 要加密的原始字符串
     * @param key  base64编码私钥
     * @return 加密后的base64编码字符串
     */
    public static String encrypt(String data, String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(rsaAlgorithm);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(rsaAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            int dataLength = data.getBytes().length;
            //分段
            int offSet = 0;
            //剩余长度
            int remainLength = dataLength;
            int blockSize;
            //对数据分段处理
            while (remainLength > 0) {
                blockSize = Math.min(remainLength, encryptBlockSize);
                out.write(cipher.doFinal(data.getBytes(), offSet, blockSize));
                offSet += blockSize;
                remainLength = dataLength - offSet;
            }
        } catch (Exception e) {
            throw new RuntimeException("私钥加密失败", e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }


    /**
     * 公钥解密
     *
     * @param data 要解密的base64编码字符串
     * @param key  base64编码的公钥
     * @return 解密后的字符串
     */
    public static String decrypt(String data, String key) {
        byte[] dataBytes = Base64.getDecoder().decode(data);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            RSAPublicKeyImpl publicKey = new RSAPublicKeyImpl(keyBytes);
            Cipher cipher = Cipher.getInstance(rsaAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            int dataLength = dataBytes.length;
            int offSet = 0;
            int remainLength = dataLength;
            int blockSize;
            while (remainLength > 0) {
                blockSize = Math.min(remainLength, decryptBlockSize);
                out.write(cipher.doFinal(dataBytes, offSet, blockSize));
                offSet += blockSize;
                remainLength = dataLength - offSet;
            }
        } catch (Exception e) {
            throw new RuntimeException("公钥解密失败", e);
        }
        return new String(out.toByteArray());
    }

    /**
     * 对数据进行加签
     *
     * @param data 要进行加签的数据
     * @param key  base64编码的私钥
     * @return 签名后base64编码的数据
     */
    public static String sign(String data, String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        byte[] bytes;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(rsaAlgorithm);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            bytes = signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("私钥签名失败", e);
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 验签
     *
     * @param data    原数据
     * @param signStr base64编码的签名
     * @param key     base64编码的公钥
     * @return true验签成功/false验签失败
     */
    public static boolean verify(String data, String signStr, String key) {
        byte[] bytes = Base64.getDecoder().decode(signStr);
        byte[] keyBytes = Base64.getDecoder().decode(key);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        boolean verify;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(rsaAlgorithm);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            verify = signature.verify(bytes);
        } catch (Exception e) {
            throw new RuntimeException("公钥验签失败", e);
        }
        return verify;
    }

    public static void main(String[] args) {
        String str = "a";
        String str2 = "a";
        Map<Integer, String> genkeyPair = SignUtils.genKeyPair();
        String pubkey = genkeyPair.get(0);
        String prikey = genkeyPair.get(1);

        String sign = SignUtils.sign(str, prikey);
        boolean verify = SignUtils.verify(str2, sign, pubkey);
        System.out.println(verify);
    }
}
