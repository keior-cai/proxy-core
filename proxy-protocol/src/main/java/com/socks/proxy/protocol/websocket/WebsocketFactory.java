package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;

public interface WebsocketFactory{

    WebSocket getClient() throws Exception;
}
