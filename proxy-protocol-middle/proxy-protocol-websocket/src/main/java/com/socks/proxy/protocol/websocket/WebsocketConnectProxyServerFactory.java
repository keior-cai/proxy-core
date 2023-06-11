package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public class WebsocketConnectProxyServerFactory implements LocalConnectServerFactory{

    private final WebsocketPoolFactory factory;

    private final WebsocketMessageFactory messageFactory;

    private final ProxyCommandEncode<? super ProxyMessage> encode;


    public WebsocketConnectProxyServerFactory(WebsocketPoolFactory factory,
                                              WebsocketMessageFactory websocketMessageFactory,
                                              ProxyCommandEncode<? super ProxyMessage> encode){
        this.factory = factory;
        this.messageFactory = websocketMessageFactory;
        this.encode = encode;
    }


    @Override
    public RemoteProxyConnect getProxyService(LocalProxyConnect channel){
        try {
            WebSocket client = factory.getClient();
            WebsocketProxyConnect websocketProxyConnect = new WebsocketProxyConnect(factory, client, encode);
            WebSocket webSocket = websocketProxyConnect.getWebSocket();
            webSocket.addListener(messageFactory.getListener(channel));
            if(!Objects.equals(webSocket.getState(), WebSocketState.CREATED)){
                log.debug("write system proxy success");
                channel.writeConnectSuccess();
            }
            return websocketProxyConnect;
        } catch (Exception e) {
            throw new RuntimeException("获取websocket客户端失败");
        }
    }
}
