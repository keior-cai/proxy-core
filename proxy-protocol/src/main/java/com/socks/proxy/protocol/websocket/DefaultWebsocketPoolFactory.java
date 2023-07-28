package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;
import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
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

/**
 * <pre>
 *     websocket connect server service pool
 * </pre>
 *
 * @author: chuangjie
 * @date: 2023/5/22
 **/
@Slf4j
public class DefaultWebsocketPoolFactory extends BasePooledObjectFactory<WebSocket> implements WebsocketPoolFactory{

    private final GenericObjectPool<WebSocket>             pool;
    private final ProxyCommandEncode<? super ProxyMessage> encode;

    private final WebsocketFactory factory;

    private final ProxyMessage close;


    public DefaultWebsocketPoolFactory(List<URI> address, GenericObjectPoolConfig<WebSocket> config,
                                       ProxyCommandEncode<? super ProxyMessage> encode, ProxyMessage close){
        this.pool = new GenericObjectPool<>(this, config);
        this.factory = new DefaultWebsocketFactory(address);
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
        return factory.getClient();
    }


    @Override
    public PooledObject<WebSocket> wrap(WebSocket obj){
        return new DefaultPooledObject<>(obj);
    }


    public void close(){
        pool.clear();
        pool.close();
    }
}
