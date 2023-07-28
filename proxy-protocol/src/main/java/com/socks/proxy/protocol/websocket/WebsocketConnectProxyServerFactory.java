package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.LocalMiddleServiceProxyFactory;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.WebsocketMessageFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public class WebsocketConnectProxyServerFactory implements LocalConnectServerFactory{

    private final WebsocketFactory factory;

    private final WebsocketMessageFactory messageFactory;

    private final LocalMiddleServiceProxyFactory middleServiceProxyFactory;


    public WebsocketConnectProxyServerFactory(WebsocketFactory factory, WebsocketMessageFactory websocketMessageFactory,
                                              LocalMiddleServiceProxyFactory middleServiceProxyFactory){
        this.factory = factory;
        this.messageFactory = websocketMessageFactory;
        this.middleServiceProxyFactory = middleServiceProxyFactory;
    }


    @Override
    public LocalMiddleService getProxyService(LocalConnect channel, TargetServer remoteServer){
        try {
            WebSocket client = factory.getClient();
            LocalWebsocketProxyConnect websocketProxyConnect = new LocalWebsocketProxyConnect(client);
            WebSocket webSocket = websocketProxyConnect.getWebSocket();
            webSocket.addListener(messageFactory.getListener(channel));
            if(!Objects.equals(webSocket.getState(), WebSocketState.CREATED)){
                log.debug("write system proxy success");
                channel.writeConnectSuccess();
            }
            return middleServiceProxyFactory.getService(websocketProxyConnect);
        } catch (Exception e) {
            throw new RuntimeException("获取websocket客户端失败");
        }
    }
}
