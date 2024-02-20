package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author: chuangjie
 * @date: 2024/2/1
 **/
@Slf4j
public class MapConnectContextManager implements ConnectContextManager{

    private final Map<String, ProxyContext> contextMap = new ConcurrentHashMap<>();

    public MapConnectContextManager(){
        new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                log.info("map = {} ", contextMap);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public void putLocalConnect(ProxyConnect connect, ProxyContext proxyContext){
        contextMap.put(connect.channelId(), proxyContext);
    }


    @Override
    public void putTargetConnect(ProxyConnect connect, ProxyContext proxyContext){
        ProxyConnect contextConnect = proxyContext.getConnect();
        log.info("L:= {} | R:={}", contextConnect.remoteAddress(), connect.remoteAddress());
        contextMap.put(connect.channelId(), proxyContext);
    }


    @Override
    public void remove(ProxyConnect connect){
        if(Objects.nonNull(connect)){
            ProxyContext remove = contextMap.remove(connect.channelId());
            Optional.ofNullable(remove.getConnect()).ifPresent(ProxyConnect::close);
        }
    }


    @Override
    public synchronized void removeAll(ProxyConnect connect){
        Optional<String> optional = Optional.ofNullable(connect).map(ProxyConnect::channelId);
        if(optional.isPresent()){
            connect.close();
            ProxyContext remove = contextMap.remove(optional.get());
            if(Objects.nonNull(remove)){
                Optional.ofNullable(remove.getProxyInfo().getCount()).filter(item->item.getCount() > 0)
                        .ifPresent(CountDownLatch::countDown);
                Optional.ofNullable(remove.getConnect()).ifPresent(ProxyConnect::close);
                Optional.ofNullable(remove.getConnect()).ifPresent(otherConnect->{
                    contextMap.remove(otherConnect.channelId());
                });
            }
        } else {
            log.debug("remove connect Id is empty.");
        }
    }


    @Override
    public ProxyContext getContext(ProxyConnect connect){
        return contextMap.get(connect.channelId());
    }
}
