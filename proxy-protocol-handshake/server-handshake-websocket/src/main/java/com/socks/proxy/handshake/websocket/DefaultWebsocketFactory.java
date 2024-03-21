package com.socks.proxy.handshake.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: chuangjie
 * @date: 2023/6/12
 **/
@Slf4j
public class DefaultWebsocketFactory implements WebsocketFactory{

    private final URI address;

    private final WebSocketFactory factory = new WebSocketFactory();


    public DefaultWebsocketFactory(URI address){
        this.address = address;
    }


    @Override
    public WebSocket getClient() throws IOException{
        log.debug("create proxy server = {}", address);
        return factory.createSocket(address, 1000);
    }
}
