package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则连接，服务工厂
 *
 * @author: chuangjie
 * @date: 2023/7/9
 **/
@Slf4j
@Setter
public class RuleLocalConnectServerFactory implements ProxyFactory{

    private ProxyFactory defaultProxyFactory;

    private ProxyFactory directProxyFactory;

    private final Map<String, List<ProxyFactory>> domainMap = new ConcurrentHashMap<>();
    //
    //    private static final String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    public RuleLocalConnectServerFactory(ProxyFactory defaultProxyFactory, ProxyFactory proxyFactory){
        this.defaultProxyFactory = defaultProxyFactory;
        this.directProxyFactory = proxyFactory;
    }


    @Override
    public ConnectProxyConnect create(TargetServer remoteServer, ProxyMessageHandler handler) throws IOException{
        List<ProxyFactory> proxyFactories = domainRule(remoteServer.host());
        if(Objects.isNull(proxyFactories) || proxyFactories.isEmpty()){
            return defaultProxyFactory.create(remoteServer, handler);
        }
        return directProxyFactory.create(remoteServer, handler);
    }


    @Override
    public long ping(){
        return 0;
    }


    @Override
    public URI uri(){
        return null;
    }


    public void addDomain(String domain, ProxyFactory factory){
        List<ProxyFactory> proxyFactories = domainMap.computeIfAbsent(domain, (k)->new ArrayList<>());
        proxyFactories.add(factory);
    }


    public List<ProxyFactory> domainRule(String host){
        List<ProxyFactory> proxyFactories = domainMap.get(host);
        if(Objects.nonNull(proxyFactories) && !proxyFactories.isEmpty()){
            return proxyFactories;
        }
        String substring = host;
        int i;
        do {
            i = substring.indexOf(".");
            substring = host.substring(i + 1);
            proxyFactories = domainMap.get(substring);
            if(proxyFactories != null && !proxyFactories.isEmpty() && substring.contains(".")){
                return proxyFactories;
            }
        } while(i != -1);
        return null;
    }
}
