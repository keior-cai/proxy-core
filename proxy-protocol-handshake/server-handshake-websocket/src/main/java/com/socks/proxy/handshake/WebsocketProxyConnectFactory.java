package com.socks.proxy.handshake;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.socks.proxy.handshake.connect.WebsocketRegisterConnect;
import com.socks.proxy.handshake.websocket.WebsocketFactory;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class WebsocketProxyConnectFactory implements ProxyFactory{

    private final WebsocketFactory factory;


    @Override
    public ConnectProxyConnect create(TargetServer targetServer, ProxyMessageHandler handler) throws IOException{
        WebSocket client = factory.getClient();
        client.addListener(new BlockWebsocketProtocol(handler));
        return new WebsocketRegisterConnect(client);
    }


    @AllArgsConstructor
    private static class BlockWebsocketProtocol extends WebSocketAdapter{

        private final ProxyMessageHandler handler;


        @Override
        public void onTextMessage(WebSocket websocket, String text){
            handler.handleLocalTextMessage(new WebsocketRegisterConnect(websocket), text);
        }


        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary){
            handler.handleTargetBinaryMessage(new WebsocketRegisterConnect(websocket), binary);
        }


        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame){
            handler.handleTargetClose(new WebsocketRegisterConnect(websocket), frame.getCloseReason());
        }


        @Override
        public void onError(WebSocket websocket, WebSocketException cause){
            handler.handleTargetClose(new WebsocketRegisterConnect(websocket), cause.getMessage());
        }
    }
}
