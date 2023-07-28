package com.socks.proxy.protocol.websocket;

import com.alibaba.fastjson2.JSON;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.enums.ConnectStatus;
import com.socks.proxy.protocol.handshake.message.SendReconnectMessage;
import lombok.Getter;

/**
 * websocket 代理连接
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class LocalWebsocketProxyConnect implements LocalMiddleService{

    @Getter
    private final WebSocket webSocket;


    public LocalWebsocketProxyConnect(WebSocket webSocket){
        this.webSocket = webSocket;
    }


    @Override
    public String channelId(){
        return webSocket.getSocket().toString();
    }


    @Override
    public void write(byte[] content){
        webSocket.sendBinary(content);
    }


    @Override
    public void connect() throws WebSocketException{
        switch(webSocket.getState()) {
            case CREATED:
                webSocket.connect();
                break;
            case OPEN:
                write(JSON.toJSONString(new SendReconnectMessage()));
                break;
        }
    }


    @Override
    public ConnectStatus status(){
        switch(webSocket.getState()) {
            case OPEN:
                return ConnectStatus.OPEN;
            case CLOSED:
                return ConnectStatus.CLOSED;
            case CLOSING:
                return ConnectStatus.CLOSING;
            case CONNECTING:
                return ConnectStatus.CONNECTING;
            case CREATED:
            default:
                return ConnectStatus.CREATED;
        }
    }


    @Override
    public void close(){
        webSocket.sendClose();
    }


    @Override
    public void write(String message){
        webSocket.sendText(message);
    }

}
