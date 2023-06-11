package com.socks.proxy.protocol;

import com.socks.proxy.cipher.AbstractCipher;
import lombok.AllArgsConstructor;

/**
 * 加解密实现
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@AllArgsConstructor
public class DefaultCipher implements ICipher{

    private final AbstractCipher cipher;


    @Override
    public byte[] encode(byte[] bytes){
        if(cipher != null){
            return cipher.encodeBytes(bytes);
        }
        return bytes;
    }


    @Override
    public byte[] decode(byte[] bytes){
        if(cipher != null){
            return cipher.decodeBytes(bytes);
        }
        return bytes;
    }
}
