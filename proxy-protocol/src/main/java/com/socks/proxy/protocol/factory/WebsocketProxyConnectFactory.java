package com.socks.proxy.protocol.factory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.RegisterProxyConnect;
import com.socks.proxy.protocol.connect.WebsocketRegisterConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.protocol.websocket.WebsocketFactory;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class WebsocketProxyConnectFactory implements ProxyFactory{

    private final WebsocketFactory factory;


    @Override
    public RegisterProxyConnect create(TargetServer targetServer, ProxyMessageHandler handler) throws IOException{
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
    }
}
