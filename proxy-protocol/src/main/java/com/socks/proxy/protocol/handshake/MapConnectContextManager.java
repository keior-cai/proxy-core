package com.socks.proxy.protocol.handshake;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author: chuangjie
 * @date: 2024/2/1
 **/
@Slf4j
public class MapConnectContextManager implements ConnectContextManager{

    private final Map<String, ProxyContext> contextMap = new ConcurrentHashMap<>();

    private final Set<String> connectIds = new ConcurrentHashSet<>();


    @Override
    public ProxyContext putLocalConnect(ProxyConnect connect){
        ProxyContext proxyContext = new ProxyContext();
        proxyContext.setCount(new CountDownLatch(1));
        proxyContext.setLocal(connect);
        contextMap.put(connect.channelId(), proxyContext);
        return proxyContext;
    }


    @Override
    public void putTargetConnect(ProxyConnect connect, ProxyConnect dst){
        ProxyContext context = contextMap.get(connect.channelId());
        context.setDst(dst);
        log.info("L:= {} | R:={}", connect.remoteAddress(), connect.remoteAddress());
        contextMap.put(dst.channelId(), context);
        connectIds.add(connect.channelId());
    }


    @Override
    public synchronized void removeAll(ProxyConnect connect){
        ProxyContext context = contextMap.remove(connect.channelId());
        if(Objects.nonNull(context.getCount())){
            context.getCount().countDown();
        }
        Optional.ofNullable(context.getDst()).ifPresent(ProxyConnect::close);
        Optional.ofNullable(context.getLocal()).ifPresent(ProxyConnect::close);
    }


    @Override
    public ProxyContext getContext(ProxyConnect connect){
        return contextMap.get(connect.channelId());
    }


    @Override
    public void putProxyConnect(ProxyConnect connect){
        connectIds.add(connect.channelId());
    }


    @Override
    public Set<ProxyContext> getTargetAllProxy(){
        return connectIds.stream().map(contextMap::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
