package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;

public interface WebsocketPoolFactory extends WebsocketFactory{

    void returnClient(WebSocket webSocket);


    void invalidateObject(WebSocket webSocket);

}
