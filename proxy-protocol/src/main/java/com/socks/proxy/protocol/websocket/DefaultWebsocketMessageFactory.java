package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocketListener;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.listener.DecodeWebsocketMessageListenerProxy;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import com.socks.proxy.protocol.listener.WebsocketMessageFactory;
import com.socks.proxy.protocol.listener.WebsocketMessageListener;

import java.util.List;

/**
 * 默认消息生产工厂
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
public class DefaultWebsocketMessageFactory implements WebsocketMessageFactory{

    private final ProxyCodes<? super ProxyMessage> proxyCodes;

    private final List<LocalConnectListener> messageListenerList;


    public DefaultWebsocketMessageFactory(ProxyCodes<? super ProxyMessage> proxyCodes,
                                          List<LocalConnectListener> messageListenerList){
        this.proxyCodes = proxyCodes;
        this.messageListenerList = messageListenerList;
    }


    @Override
    public WebSocketListener getListener(LocalConnect connect){
        return new DecodeWebsocketMessageListenerProxy(new WebsocketMessageListener(connect, messageListenerList),
                proxyCodes);
    }
}
