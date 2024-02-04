package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
public class RuleLocalConnectServerFactory implements ProxyFactory{

    private final ProxyFactory                    defaultProxyFactory;
    private final Map<String, List<ProxyFactory>> domainMap = new ConcurrentHashMap<>();

    private static final String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";


    public RuleLocalConnectServerFactory(ProxyFactory defaultProxyFactory){
        this.defaultProxyFactory = defaultProxyFactory;
    }


    @Override
    public ConnectProxyConnect create(TargetServer remoteServer, ProxyMessageHandler handler) throws IOException{
        List<ProxyFactory> proxyFactories = domainRule(remoteServer.host());
        if(Objects.isNull(proxyFactories) || proxyFactories.isEmpty()){
            return defaultProxyFactory.create(remoteServer, handler);
        }
        for(ProxyFactory factory : proxyFactories) {
            try {
                return factory.create(remoteServer, handler);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return defaultProxyFactory.create(remoteServer, handler);
    }


    public void addDomain(String domain, ProxyFactory factory){
        List<ProxyFactory> proxyFactories = domainMap.computeIfAbsent(domain, (k)->new ArrayList<>());
        proxyFactories.add(factory);
    }


    private List<ProxyFactory> domainRule(String host){
        if(host.matches(ipRegex)){
            return domainMap.get(host);
        } else {
            return domainMap.get(host.substring(host.indexOf(".") + 1));
        }

    }

}
