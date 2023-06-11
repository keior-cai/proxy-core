package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;

/**
 * socks5 握手协议处理
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class Socks5HandshakeProtocolHandler extends AbstractSocksHandshakeProtocolHandler{

    private static final byte VERSION = 0x05;


    @Override
    protected boolean validate(byte b, byte[] bytes){
        return bytes.length == b;
    }


    @Override
    protected byte version(){
        return VERSION;
    }


    @Override
    protected Protocol protocol(){
        return Protocol.SOCKS5;
    }
}
