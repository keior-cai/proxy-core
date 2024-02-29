package com.socks.proxy.handshake;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.socks.proxy.handshake.connect.WebsocketConnect;
import com.socks.proxy.handshake.websocket.DefaultWebsocketFactory;
import com.socks.proxy.handshake.websocket.WebsocketFactory;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.util.SocketUtils;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.URI;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class WebsocketProxyConnectFactory implements ProxyFactory{

    private final WebsocketFactory factory;

    private final URI uris;


    public static WebsocketProxyConnectFactory createDefault(String uris){
        return createDefault(URI.create(uris));
    }


    public static WebsocketProxyConnectFactory createDefault(URI uris){
        return new WebsocketProxyConnectFactory(new DefaultWebsocketFactory(uris), uris);
    }


    @Override
    public ConnectProxyConnect create(TargetServer targetServer, ProxyMessageHandler handler) throws IOException{
        WebSocket client = factory.getClient();
        client.addListener(new BlockWebsocketProtocol(handler));
        return new WebsocketConnect(client);
    }


    @Override
    public long ping(){
        return SocketUtils.ping(uris.getHost(), uris.getPort(), 3000);
    }


    @Override
    public URI uri(){
        return uris;
    }


    @AllArgsConstructor
    private static class BlockWebsocketProtocol extends WebSocketAdapter{

        private final ProxyMessageHandler handler;


        @Override
        public void onTextMessage(WebSocket websocket, String text){
            handler.handleLocalTextMessage(new WebsocketConnect(websocket), text);
        }


        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary){
            handler.handleTargetBinaryMessage(new WebsocketConnect(websocket), binary);
        }


        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause){
            handler.handleLocalClose(new WebsocketConnect(websocket), cause.getMessage());
        }


        @Override
        public void onError(WebSocket websocket, WebSocketException cause){
            handler.handleTargetClose(new WebsocketConnect(websocket), cause.getMessage());
        }


        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame){
            handler.handleTargetClose(new WebsocketConnect(websocket), "websocket 断开连接");
        }


        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause){
            handler.handleTargetClose(new WebsocketConnect(websocket), cause.getMessage());
        }
    }
}
