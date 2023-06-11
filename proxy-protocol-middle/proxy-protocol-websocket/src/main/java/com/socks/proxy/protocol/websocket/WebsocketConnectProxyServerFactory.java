package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.socks.proxt.codes.ProxyCommandEncode;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
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
            websocketProxyConnect.getWebSocket().addListener(messageFactory.getListener(channel));
            return websocketProxyConnect;
        } catch (Exception e) {
            //            throw new ProxyException("获取websocket客户端失败");
        }
        return null;
    }
}
