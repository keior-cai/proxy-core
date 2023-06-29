package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.UnKnowProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 复合协议，支持socks5和HTTP协议复合
 *
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class ComplexHandshakeProtocolHandler implements HandshakeProtocolHandler{

    public ComplexHandshakeProtocolHandler(){
        handshakeProtocolHandlerList.add(new Socks5HandshakeProtocolHandler());
        handshakeProtocolHandlerList.add(new HttpHandshakeProtocolHandler());
        handshakeProtocolHandlerList.add(new Socks4SocksHandshakeProtocolHandler());
    }


    private final List<HandshakeProtocolHandler> handshakeProtocolHandlerList = new ArrayList<>();


    @Override
    public Protocol handler(InputStream is) throws IOException{
        for(HandshakeProtocolHandler handler : handshakeProtocolHandlerList) {
            try {
                return handler.handler(is);
            } catch (UnKnowProtocolException e) {
                is.reset();
            }
        }
        throw new UnKnowProtocolException();
    }
}
