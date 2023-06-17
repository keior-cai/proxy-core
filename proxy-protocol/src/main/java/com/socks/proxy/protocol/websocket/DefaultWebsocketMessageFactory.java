package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocketListener;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.listener.WebsocketMessageFactory;
import com.socks.proxy.protocol.listener.WebsocketMessageListener;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 默认消息生产工厂
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@AllArgsConstructor
public class DefaultWebsocketMessageFactory implements WebsocketMessageFactory{

    private final Map<ProxyCommand, LocalHandshakeMessageHandler> messageHandlerMap;

    private final ProxyCodes<? super ProxyMessage> proxyCodes;


    @Override
    public WebSocketListener getListener(LocalConnect connect){
        return new WebsocketMessageListener(connect, messageHandlerMap, proxyCodes);
    }
}
