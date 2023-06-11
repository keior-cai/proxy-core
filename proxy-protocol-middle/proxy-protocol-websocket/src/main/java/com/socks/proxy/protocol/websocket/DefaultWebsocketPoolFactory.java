package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxt.codes.ProxyCommandEncode;
import com.socks.proxt.codes.ProxyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: chuangjie
 * @date: 2023/5/22
 **/
@Slf4j
public class DefaultWebsocketPoolFactory extends BasePooledObjectFactory<WebSocket> implements WebsocketPoolFactory{

    private final GenericObjectPool<WebSocket> pool;
    private final List<URI>                    address;
    private       Thread                       thread;
    private final AtomicLong                   COUNTER = new AtomicLong();

    private final ProxyCommandEncode<? super ProxyMessage> encode;

    private final WebSocketFactory factory = new WebSocketFactory();

    private final ProxyMessage close;


    public DefaultWebsocketPoolFactory(List<URI> address, GenericObjectPoolConfig<WebSocket> config,
                                       ProxyCommandEncode<? super ProxyMessage> encode, ProxyMessage close){
        this.pool = new GenericObjectPool<>(this, config);
        this.address = address;
        this.encode = encode;
        this.close = close;
    }


    public WebSocket getClient() throws Exception{
        WebSocket webSocket = pool.borrowObject();
        if(Objects.equals(webSocket.getState(), WebSocketState.OPEN)){
            try {
                webSocket.sendPing();
            } catch (Exception e) {
                invalidateObject(webSocket);
                return getClient();
            }
            return webSocket;
        }
        return webSocket;
    }


    @Override
    public void returnClient(WebSocket webSocket){
        try {
            webSocket.sendText(encode.encodeObject(close));
            webSocket.clearListeners();
            pool.returnObject(webSocket);
        } catch (Exception e) {
            log.error("", e);
            try {
                pool.invalidateObject(webSocket);
            } catch (Exception ex) {
                // ignore
            }
        }
    }


    @Override
    public void invalidateObject(WebSocket webSocket){
        try {
            pool.invalidateObject(webSocket, DestroyMode.NORMAL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void destroyObject(PooledObject<WebSocket> p){
        WebSocket object = p.getObject();
        if(Objects.equals(object.getState(), WebSocketState.CLOSED) || Objects.equals(object.getState(),
                WebSocketState.CLOSING)){
            return;
        }
        object.disconnect();
    }


    @Override
    public boolean validateObject(PooledObject<WebSocket> p){
        WebSocket object = p.getObject();
        try {
            object.sendPing();
            return true;
        } catch (Exception e) {
            log.error("ping error");
        }
        return false;
    }


    @Override
    public WebSocket create() throws Exception{
        int len = address.size();
        int index = (int) (COUNTER.incrementAndGet() % len);
        URI target = address.get(index);
        log.debug("create proxy server = {}", target);
        return factory.createSocket(target);
    }


    @Override
    public PooledObject<WebSocket> wrap(WebSocket obj){
        return new DefaultPooledObject<>(obj);
    }


    public void print(){
        thread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()) {
                log.info("pool active {}, waiters = {}, now count = {} total = {}", pool.getNumActive(),
                        pool.getNumWaiters(), pool.getCreatedCount() - pool.getDestroyedCount(), pool.getMaxTotal());
                try {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread.start();
    }


    public void close(){
        thread.interrupt();
        pool.clear();
        pool.close();
    }
}
