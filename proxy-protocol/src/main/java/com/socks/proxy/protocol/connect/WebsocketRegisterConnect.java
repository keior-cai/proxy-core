package com.socks.proxy.protocol.connect;

import com.neovisionaries.ws.client.*;
import com.socks.proxy.protocol.listener.ProxyListener;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Vector;

/**
 * @author: chuangjie
 * @date: 2024/1/24
 **/
public class WebsocketRegisterConnect implements RegisterProxyConnect{

    private final WebSocket client;

    private final List<ProxyListener> listeners = new Vector<>();


    public WebsocketRegisterConnect(WebSocket client){
        this.client = client;
        this.client.addListener(new WebSocketAdapter(){
            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary){
                for(ProxyListener listener : listeners) {
                    listener.onMessage(WebsocketRegisterConnect.this, WebsocketRegisterConnect.this, binary);
                }
            }


            @Override
            public void onTextMessage(WebSocket websocket, String text){
                for(ProxyListener listener : listeners) {
                    listener.onMessage(WebsocketRegisterConnect.this, WebsocketRegisterConnect.this, text);
                }
            }


            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer){
                for(ProxyListener listener : listeners) {
                    listener.onClose(WebsocketRegisterConnect.this, WebsocketRegisterConnect.this);
                }
            }
        });
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
    public void register(List<ProxyListener> listener){
        listeners.addAll(listener);
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

}
