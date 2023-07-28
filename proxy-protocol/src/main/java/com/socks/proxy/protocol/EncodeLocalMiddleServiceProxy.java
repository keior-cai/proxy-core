package com.socks.proxy.protocol;

import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.ConnectStatus;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/7/9
 **/
@AllArgsConstructor
public class EncodeLocalMiddleServiceProxy implements LocalMiddleService{

    private final LocalMiddleService proxy;

    private final ProxyCommandEncode<? super ProxyMessage> encode;


    @Override
    public void write(String message){
        proxy.write(encode.encodeStr(message));
    }


    @Override
    public void connect() throws Exception{
        proxy.connect();
    }


    @Override
    public ConnectStatus status(){
        return proxy.status();
    }


    @Override
    public String channelId(){
        return proxy.channelId();
    }


    @Override
    public void write(byte[] content){
        // 这里不知直接加解密，需要通过代理来实现，主要是应该走正常代理需要加解密，走直接连接不需要加解密
//        ICipher cipher = ctx.channel().attr(AttrConstant.CIPHER_KEY).get();
//        byte[] bytes = cipher.encode(content);
        proxy.write(content);
    }


    @Override
    public void close(){
        proxy.close();
    }
}
