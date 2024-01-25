package com.socks.proxy.protocol.connect;

import com.neovisionaries.ws.client.WebSocket;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class WebsocketConnect implements ProxyConnect{

    private final WebSocket client;


    @Override
    public String channelId(){
        return null;
    }


    @Override
    public void write(byte[] binary){

    }


    @Override
    public void write(String message){

    }


    @Override
    public void close(){

    }
}
