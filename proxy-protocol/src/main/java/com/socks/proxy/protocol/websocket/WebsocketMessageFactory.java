package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocketListener;
import com.socks.proxy.protocol.LocalProxyConnect;

public interface WebsocketMessageFactory{

    WebSocketListener getListener(LocalProxyConnect connect);
}
