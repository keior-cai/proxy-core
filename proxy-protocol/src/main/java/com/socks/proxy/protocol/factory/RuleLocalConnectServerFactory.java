package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 规则连接，服务工厂
 *
 * @author: chuangjie
 * @date: 2023/7/9
 **/
public class RuleLocalConnectServerFactory implements ProxyFactory{

    private final ProxyFactory targetServerFactory;

    private final ProxyFactory directFactory;

    private final Set<String> domainMap = new CopyOnWriteArraySet<>();

    private static final String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";


    public RuleLocalConnectServerFactory(ProxyFactory targetServerFactory, ProxyFactory directFactory){

        this.targetServerFactory = targetServerFactory;
        this.directFactory = directFactory;
    }


    @Override
    public ConnectProxyConnect create(TargetServer remoteServer, ProxyMessageHandler handler) throws IOException{
        ConnectProxyConnect proxyService = null;
        if(domainRule(remoteServer.host())){
            proxyService = targetServerFactory.create(remoteServer, handler);
        } else {
            proxyService = directFactory.create(remoteServer, handler);
        }
        return proxyService;
    }


    public void addDomain(String domain){
        domainMap.add(domain);
    }


    private boolean domainRule(String host){
        if(host.matches(ipRegex)){
            return domainMap.contains(host);
        } else {
            return domainMap.contains(host.substring(host.indexOf(".") + 1));
        }

    }

}
