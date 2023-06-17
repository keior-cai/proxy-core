package com.socks.proxy.protocol.listener;

import com.neovisionaries.ws.client.WebSocketListener;
import com.socks.proxy.protocol.LocalConnect;

public interface WebsocketMessageFactory{

    WebSocketListener getListener(LocalConnect connect);
}
