package com.socks.proxy.protocol.codes;

/**
 * @author: chuangjie
 * @date: 2024/1/29
 **/
public class NoCodeProxyCodes implements ProxyCodes{
    @Override
    public String decodeStr(String str){
        return str;
    }


    @Override
    public String encodeStr(String message){
        return message;
    }
}
