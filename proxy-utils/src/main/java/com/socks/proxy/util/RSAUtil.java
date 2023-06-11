package com.socks.proxy.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ccj
 * @date 2019/8/26 19:25
 * @description rsa 加密工具
 **/
public class RSAUtil{

    public static final String CHAR_ENCODING = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    public final String privateKey;
    public final String publicKey;


    public RSAUtil(String privateKey, String publicKey){
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    public RSAUtil(){
        this("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAOXBLp/gUFIcoOCDxO2LZhdbqJzwEYt6gojwi5cRTLSrZxXncNwns43nmhe64O4FEsTo9qdPzqByhFOnUg5MqMjt+BrdqshpaNi5sQHMItSVM9DH+OM7T+ZZquPjK8rOTpSWj+5da6ZH53HR1iIa1slCUKWAgu9i3E0dAHu5dY+PAgMBAAECgYEAk87uYeh7g/fq/8WGAZR2v3w2Q5CmmObd559pDm0QvgKvNQZKMzhPaXGgTrfpUPdulcOSOx06vzotK2wvfAeRZUqmApZqlLOiNkcrafEIBjwBlWh7EKxw9bXauKgdQXr7MPfQg11ipbw52wGXmEElvB5tEuCX5tVD9KHzkluXyKECQQD3fq/WgaDTWlnTVb1QvnyBP+bS+40A9JMst9WDK1qKQ8urKFX4Lnfw7s5953Lbx/euLzM1+e9tnWmcUTMa0Op5AkEA7aZtUg48z9rTN4OposMITmOaO870CZot8DE0RS1MshVsSCL6AbKRFiOLzxoDlFGEFtAvephN5qHPtYhWT4bIRwJBAMbY25Ad4EhPjGIWvh9UnJX/8IXNJBIDbwf7v6k+uOTj6YxfwQrA0w8Z34Aa6BabSG2DcMLKR8srMQIt30CJYAkCQQDI6cDWdG75Evkqn8cUcWpeS1qjYa1zSMO5ov+b1FZY4D+xJNDUCpEadGbIaifIhrnzR4I8VPLXHsmpoV/G0B4VAkEArhaCTjjg5KIyyccBIcyTo8RVCQV1/cEwtdl/b+E4JzFatkMvVLbWVSZJ+b0ZxRqDA4DD6qFaZKl2Ya0vgtiPhg==",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlwS6f4FBSHKDgg8Tti2YXW6ic8BGLeoKI8IuXEUy0q2cV53DcJ7ON55oXuuDuBRLE6PanT86gcoRTp1IOTKjI7fga3arIaWjYubEBzCLUlTPQx/jjO0/mWarj4yvKzk6Ulo/uXWumR+dx0dYiGtbJQlClgILvYtxNHQB7uXWPjwIDAQAB");
    }


    /**
     * 指定key的大小
     */
    private static final int KEY_SIZE = 1024;


    /**
     * 生成密钥对
     */
    public static Map<String, String> generateKeyPair() throws Exception{
        // RSA算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        // 利用上面的随机数据源初始化这个KeyPairGenerator对象
        kpg.initialize(KEY_SIZE, sr);
        // 生成密匙对
        KeyPair kp = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String pub = Base64.encodeBase64String(publicKeyBytes);
        //得到私钥
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = Base64.encodeBase64String(privateKeyBytes);
        Map<String, String> map = new HashMap<>();
        map.put("publicKey", pub);
        map.put("privateKey", pri);
        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.encodeBase64URLSafe(b);
        String retValue = new String(deBase64Value);
        map.put("modulus", retValue);
        return map;
    }


    /**
     * 加密方法 source： 源数据
     */
    public String encrypt(String source) throws Exception{
        return encrypt(source, publicKey);
    }


    public static String encrypt(String source, String publicKey) throws Exception{
        Key key = getPublicKey(publicKey);
        /* 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = source.getBytes();
        /* 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return new String(Base64.encodeBase64URLSafe(b1), CHAR_ENCODING);
    }


    /**
     * 解密算法 cryptograph:密文
     */
    public String decrypt(String cryptograph) throws Exception{
        Key key = getPrivateKey(privateKey);
        /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b1 = Base64.decodeBase64(cryptograph.getBytes());
        /* 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }


    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     */
    private static PublicKey getPublicKey(String key) throws Exception{
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     */
    private PrivateKey getPrivateKey(String key) throws Exception{
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    public static void main(String[] args) throws Exception{
        //公私钥生成的地址:https://www.devglan.com/online-tools/rsa-encryption-decryption    1024
        String sadasdasd = new RSAUtil().encrypt("sadasdasd");
        System.out.println(sadasdasd);
        String decrypt = new RSAUtil().decrypt(sadasdasd);
        System.out.println(decrypt);
        Map<String, String> map = RSAUtil.generateKeyPair();
        System.out.println(map);
    }
}
