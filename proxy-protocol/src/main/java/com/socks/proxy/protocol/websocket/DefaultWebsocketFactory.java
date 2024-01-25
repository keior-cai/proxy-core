package com.socks.proxy.protocol.websocket;

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

    private final List<URI> address;

    private final AtomicLong COUNTER = new AtomicLong();

    private final WebSocketFactory factory = new WebSocketFactory();


    public DefaultWebsocketFactory(List<URI> address){
        this.address = address;
    }


    @Override
    public WebSocket getClient() throws IOException{
        int len = address.size();
        int index = (int) (COUNTER.incrementAndGet() % len);
        URI target = address.get(index);
        log.debug("create proxy server = {}", target);
        return factory.createSocket(target);
    }
}
