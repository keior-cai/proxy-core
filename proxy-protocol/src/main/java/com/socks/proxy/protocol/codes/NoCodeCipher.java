package com.socks.proxy.protocol.codes;

/**
 * @author: chuangjie
 * @date: 2023/7/28
 **/
public class NoCodeCipher implements ICipher{
    @Override
    public byte[] encode(byte[] bytes){
        return bytes;
    }


    @Override
    public byte[] decode(byte[] bytes){
        return bytes;
    }
}
