package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;

/**
 * socks4 版本协议解析
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class Socks4SocksHandshakeProtocolHandler extends AbstractSocksHandshakeProtocolHandler{

    private static final byte VERSION = 0x04;


    @Override
    protected boolean validate(byte b, byte[] bytes){
        if(bytes.length <= 6){
            return false;
        }
        return b == 0x02 || b == 0x01;
    }


    @Override
    protected byte version(){
        return VERSION;
    }


    @Override
    protected Protocol protocol(){
        return Protocol.SOCKS4;
    }
}
