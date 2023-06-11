package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxt.codes.ProxyCommandEncode;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.RemoteProxyConnect;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * websocket 代理连接
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class WebsocketProxyConnect implements RemoteProxyConnect{

    @Getter
    private final WebSocket webSocket;

    private final WebsocketPoolFactory                     factory;
    @Setter
    private       ProxyCommandEncode<? super ProxyMessage> encode;


    public WebsocketProxyConnect(WebsocketPoolFactory factory, WebSocket webSocket,
                                 ProxyCommandEncode<? super ProxyMessage> encode){
        this.factory = factory;
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
        if(Objects.equals(webSocket.getState(), WebSocketState.CREATED)){
            webSocket.connect();
        }
    }


    @Override
    public void close(){
        if(factory != null){
            if(Objects.equals(webSocket.getState(), WebSocketState.CLOSED)){
                factory.invalidateObject(webSocket);
            } else {
                factory.returnClient(webSocket);
            }
        }
    }


    @Override
    public void setCipher(ICipher iCipher){

    }


    @Override
    public void write(String message){
        String encodeStr = encode.encodeStr(message);
        webSocket.sendText(encodeStr);
    }


    @Override
    public DstServer getDstServer(){
        return null;
    }


    @Override
    public void setDstServer(DstServer dstServer){

    }


    @Override
    public void setTarget(RemoteProxyConnect proxyConnect){

    }

}
