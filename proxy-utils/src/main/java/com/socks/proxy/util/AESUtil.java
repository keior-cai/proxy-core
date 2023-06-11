package com.socks.proxy.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * @author ccj
 * @Description: 实现aes加密、解密
 * @date 2020/8/5
 */
public class AESUtil{

    /**
     * 密钥长度必须是16
     */
    public static final String defaultKey = "ca72ed29dc5eed56b203057f50c6c4de";

    public static final String encodeType = "utf-8";

    public static final String iv = "0000000000000000";

    /**
     * 算法
     */
    private static final String ALGORITHMSTR = "AES/CBC/PKCS5Padding";


    public static void main(String[] args){
        String testContent = "123456";
        System.out.println("加密前：" + testContent);
        System.out.println("密钥：" + defaultKey);
        String publicKey = AESUtil.encryptByDefaultKey(testContent);
        System.out.println(AESUtil.decryptByDefaultKey(publicKey));
        publicKey = publicKey.substring(0, publicKey.indexOf("="));
        System.out.println("publicKey 加密后：" + publicKey);
        publicKey = "Ua25HPj/9u1gnDIMB9PdcELLjycWaNl4VXE610trMU8";
        String s = AESUtil.decryptByDefaultKey(publicKey);
        System.out.println("privatekey 加密后111：" + s);
        System.out.println("privatekey 加密后111：" + DigestUtils.md5Hex(s));

    }


    /**
     * aes解密
     *
     * @param encrypt 内容
     */
    public static String decryptByDefaultKey(String encrypt){
        return decrypt(encrypt, defaultKey, iv);
    }


    /**
     * aes解密
     *
     * @param encrypt 内容
     */
    public static String decryptByDefaultIv(String encrypt, String key){
        try {
            return decrypt(encrypt, key, iv);
        } catch (Exception e) {
            throw new RuntimeException("解密失败");
        }
    }


    public static String decryptByKeyOrDefaultKey(String content, String key){
        if(key == null || key.length() == 0){
            return decryptByDefaultKey(content);
        }
        return decryptByDefaultIv(content, key);
    }


    /**
     * aes加密
     */
    public static String encryptByDefaultKey(String content){
        return encryptBase64(content, defaultKey, iv, encodeType);
    }


    /**
     * aes加密
     */
    public static String encryptByDefaultIv(String content, String key){
        try {
            return encryptBase64(content, key, iv, encodeType);
        } catch (Exception e) {
            throw new RuntimeException("加密失败");
        }
    }


    public static String encryptByKeyOrDefaultKey(String content, String key){
        if(key == null || key.length() == 0){
            return encryptByDefaultKey(content);
        }
        return encryptByDefaultIv(content, key);
    }


    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    private static String base64Encode(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }


    /**
     * hex 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    private static String hexEncode(byte[] bytes){
        return Hex.encodeHexString(bytes);
    }


    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     */
    private static byte[] base64Decode(String base64Code){
        return Objects.isNull(base64Code) ? null : Base64.getDecoder().decode(base64Code);
    }


    /**
     * AES加密
     *
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     */
    private static byte[] aesEncryptToBytes(String content, String encryptKey, String iv, String encodeType){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"),
                    new IvParameterSpec(iv.getBytes()));
            return cipher.doFinal(content.getBytes(encodeType));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * AES加密为base 64 code
     *
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    public static String encryptBase64(String content, String encryptKey, String iv, String encodeType){
        return base64Encode(aesEncryptToBytes(content, encryptKey, iv, encodeType));
    }


    /**
     * AES加密为hex 64 code
     *
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    public static String encryptHex(String content, String encryptKey, String iv, String encodeType){
        return hexEncode(aesEncryptToBytes(content, encryptKey, iv, encodeType));
    }


    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     */
    private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey, String iv){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);

            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"),
                    new IvParameterSpec(iv.getBytes()));
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            throw new Error(e);
        }

    }


    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String decrypt(String encryptStr, String decryptKey, String iv){
        return Objects.isNull(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey, iv);
    }

}
