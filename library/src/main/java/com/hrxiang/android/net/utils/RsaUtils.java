package com.hrxiang.android.net.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by xianghairui on 2019/2/27.
 */
public class RsaUtils {
    public static String encrypt(String data, String publicKeyString) throws Exception {
        // Base64为android util包自带的，publicKeyString，参数选择NO_WRAP
        byte[] keyByte = Base64.decode(publicKeyString.getBytes(), Base64.NO_WRAP);
        // 获取keyspec （java.security.spec.X509EncodedKeySpec）
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        // 获取keyfactory （java.security.KeyFactory）
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        // 获取公钥 （java.security.PublicKey）
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        // 初始化加密类
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 加密后获取字节数组 text为要加密的字符串
        byte[] enBytes = cipher.doFinal(data.getBytes());
        // 加密结果再做一次编码，获取的字符串作为密文传给服务器验证
        return Base64.encodeToString(enBytes, Base64.NO_WRAP);
    }
}
