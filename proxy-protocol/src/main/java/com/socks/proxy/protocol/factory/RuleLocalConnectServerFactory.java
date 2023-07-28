package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.NoCodeCipher;
import com.socks.proxy.protocol.TargetServer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 规则连接，服务工厂
 *
 * @author: chuangjie
 * @date: 2023/7/9
 **/
public class RuleLocalConnectServerFactory implements LocalConnectServerFactory{

    private final LocalConnectServerFactory targetServerFactory;

    private final LocalConnectServerFactory directFactory;

    private final Set<String> domainMap = new CopyOnWriteArraySet<>();

    private static final String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";


    public RuleLocalConnectServerFactory(LocalConnectServerFactory targetServerFactory,
                                         LocalConnectServerFactory directFactory){

        this.targetServerFactory = targetServerFactory;
        this.directFactory = directFactory;
    }


    @Override
    public LocalMiddleService getProxyService(LocalConnect channel, TargetServer remoteServer){
        LocalMiddleService proxyService;
        if(domainRule(remoteServer.host())){
            proxyService = targetServerFactory.getProxyService(channel, remoteServer);
        } else {
            proxyService = directFactory.getProxyService(channel, remoteServer);
            channel.writeConnectSuccess();
        }
        channel.setRemoteChannel(proxyService);
        channel.setCipher(new NoCodeCipher());
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
