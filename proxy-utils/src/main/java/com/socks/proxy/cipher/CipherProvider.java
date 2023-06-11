package com.socks.proxy.cipher;

import com.socks.proxy.stream.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * cipherProvider
 */
@Slf4j
public class CipherProvider{

    private static final Map<String, Class<? extends AbstractCipher>> cipherMap = new HashMap<>();

    static{
        /* aes */
        cipherMap.put("aes-128-cfb", Aes128CfbCipher.class);
        cipherMap.put("aes-192-cfb", Aes192CfbCipher.class);
        cipherMap.put("aes-256-cfb", Aes256CfbCipher.class);

        /* camellia */
        cipherMap.put("camellia-128-cfb", Camellia128CfbCipher.class);
        cipherMap.put("camellia-192-cfb", Camellia192CfbCipher.class);
        cipherMap.put("camellia-256-cfb", Camellia256CfbCipher.class);

        /* chacha20 */
        cipherMap.put("chacha20", Chacha20Cipher.class);
        cipherMap.put("chacha20-ietf", Chacha20IetfCipher.class);

        /*others stream cipher */
        cipherMap.put("rc4-md5", Rc4Md5Cipher.class);
        cipherMap.put("salsa20", Salsa20Cipher.class);
    }

    /**
     * get Cipher by standard cipherName
     *
     * @param cipherName cipherName
     * @param password password
     * @return new cipher instance
     */
    public static AbstractCipher getByName(String cipherName, String password){
        if(cipherMap.containsKey(cipherName)){
            Class<? extends AbstractCipher> cipherClazz = cipherMap.get(cipherName);
            try {
                Constructor<? extends AbstractCipher> cipherClazzConstructor = cipherClazz.getConstructor(String.class);
                return cipherClazzConstructor.newInstance(password);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                    InstantiationException e) {
                log.error("get init cipher fail {}", e.getMessage());
            }
        }
        return null;
    }
}
