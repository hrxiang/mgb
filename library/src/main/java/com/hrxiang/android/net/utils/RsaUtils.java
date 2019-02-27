package com.hrxiang.android.net.utils;

import android.util.Base64;
import com.hrxiang.android.base.utils.Base64Utils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by xianghairui on 2019/2/27.
 */
public class RsaUtils {
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String encryptByPublicKey(String source, String publicKeyString) throws Exception {
        // Base64为android util包自带的，publicKeyString，参数选择NO_WRAP
        byte[] keyByte = Base64.decode(publicKeyString, Base64.NO_WRAP);
        // 获取keyspec （java.security.spec.X509EncodedKeySpec）
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        // 获取keyfactory （java.security.KeyFactory）
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        // 获取公钥 （java.security.PublicKey）
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        // 初始化加密类
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 加密后获取字节数组 text为要加密的字符串
        byte[] enBytes = cipher.doFinal(source.getBytes());
        // 加密结果再做一次编码，获取的字符串作为密文传给服务器验证
        return Base64.encodeToString(enBytes, Base64.NO_WRAP);
    }


    /**
     * 使用私钥解密
     */
    public static String decryptByPrivateKey(String encryptedStr, String privateKeyStr) throws Exception {
        byte[] encryptedBytes = Base64.decode(encryptedStr, Base64.NO_WRAP);
        byte[] keyBytes = Base64.decode(privateKeyStr, Base64.NO_WRAP);
        // 得到私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 解密数据
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] decryptBytes = cipher.doFinal(encryptedBytes);
//        byte[] arr = cp.doFinal(encryptedStr.getBytes());
//        return Base64.encodeToString(arr, Base64.NO_WRAP);
        return new String(decryptBytes);
    }
    /** */
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
//        byte[] keyBytes = Base64.decode(privateKey, Base64.NO_WRAP);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, "BC");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
//        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
}
