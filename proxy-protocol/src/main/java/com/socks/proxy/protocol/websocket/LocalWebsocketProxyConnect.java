package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.ConnectStatus;
import com.socks.proxy.protocol.handshake.local.ReconnectMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * websocket 代理连接
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class LocalWebsocketProxyConnect implements LocalMiddleProxy{

    @Getter
    private final WebSocket webSocket;

    @Setter
    private ProxyCommandEncode<? super ProxyMessage> encode;


    public LocalWebsocketProxyConnect(WebSocket webSocket, ProxyCommandEncode<? super ProxyMessage> encode){
        this.webSocket = webSocket;
        this.encode = encode;
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
                String encodeStr = encode.encodeObject(new ReconnectMessage());
                webSocket.sendText(encodeStr);
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

    }


    @Override
    public void write(String message){
        String encodeStr = encode.encodeStr(message);
        webSocket.sendText(encodeStr);
    }


    @Override
    public TargetServer getDstServer(){
        return null;
    }


    @Override
    public void setDstServer(TargetServer dstServer){

    }


    @Override
    public void setTarget(LocalMiddleProxy proxyConnect){

    }

}
