package com.socks.proxy.handshake.connect;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.enums.ConnectType;

import java.net.SocketAddress;
import java.util.Objects;

/**
 * @author: chuangjie
 * @date: 2024/1/24
 **/
public class WebsocketConnect implements ConnectProxyConnect{

    private final WebSocket client;


    public WebsocketConnect(WebSocket client){
        this.client = client;
    }


    @Override
    public String channelId(){
        return client.toString();
    }


    @Override
    public void write(byte[] content){
        client.sendBinary(content);
    }


    @Override
    public void write(String message){
        client.sendText(message);
    }


    @Override
    public void close(){
        client.sendClose();
    }


    @Override
    public SocketAddress remoteAddress(){
        return client.getSocket().getRemoteSocketAddress();
    }


    @Override
    public void connect(){
        try {
            if(Objects.equals(client.getState(), WebSocketState.CREATED)){
                client.connect();
            }
        } catch (WebSocketException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ConnectType type(){
        return ConnectType.PROXY;
    }

}
