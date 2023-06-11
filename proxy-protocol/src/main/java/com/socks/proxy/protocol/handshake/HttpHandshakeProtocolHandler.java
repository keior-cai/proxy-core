package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.UnKnowProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class HttpHandshakeProtocolHandler implements HandshakeProtocolHandler{

    @Override
    public Protocol handler(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s = reader.readLine();
        String[] s1 = s.split(" ");
        if(s1.length != 3){
            throw new UnKnowProtocolException();
        }
        return Protocol.HTTP;
    }
}
