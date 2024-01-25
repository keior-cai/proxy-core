package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;

import java.io.IOException;

public interface WebsocketFactory{

    WebSocket getClient() throws IOException;
}
