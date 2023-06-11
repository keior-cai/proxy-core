package com.socks.proxy.cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 解密/加密工具
 */
public abstract class AbstractCipher{

    /**
     * cipher name
     */
    private final String cipherName;
    /**
     * key
     */
    private final byte[] key;


    public AbstractCipher(String cipherName, String password){
        this.cipherName = cipherName;
        key = getKey(password, getKeyLength());
    }


    /**
     * 解密
     *
     * @param secretBytes 密文
     * @return 明文
     */
    public abstract byte[] decodeBytes(byte[] secretBytes);


    /**
     * 加密
     *
     * @param originBytes 明文
     * @return 密文
     */
    public abstract byte[] encodeBytes(byte[] originBytes);


    /**
     * 获取密钥长度
     *
     * @return 密钥长度
     */
    public abstract int getKeyLength();


    /**
     * 生成随机数 byte
     *
     * @param size 位数
     * @return random of bytes
     */
    protected byte[] getRandomBytes(int size){
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }


    public byte[] getKey(){
        return key;
    }


    public String getCipherName(){
        return cipherName;
    }


    public static byte[] getKey(String password, int keyLength){
        byte[] result = new byte[keyLength];
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            for(int hasLength = 0; hasLength < keyLength; hasLength += 16) {
                byte[] passwordBytes = password.getBytes();

                //组合需要摘要的byte[]
                byte[] combineBytes = new byte[hasLength + passwordBytes.length];
                System.arraycopy(result, 0, combineBytes, 0, hasLength);
                System.arraycopy(passwordBytes, 0, combineBytes, hasLength, passwordBytes.length);

                //增加
                byte[] digestBytes = messageDigest.digest(combineBytes);
                int addLength = hasLength + 16 > keyLength ? keyLength - hasLength : 16;
                System.arraycopy(digestBytes, 0, result, hasLength, addLength);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
