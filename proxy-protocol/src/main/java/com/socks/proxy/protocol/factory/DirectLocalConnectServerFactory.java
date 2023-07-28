package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.DirectLocalMiddleProxy;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/7/8
 **/
@AllArgsConstructor
public class DirectLocalConnectServerFactory implements LocalConnectServerFactory{

    private final List<LocalConnectListener> messageLister;


    @Override
    public LocalMiddleService getProxyService(LocalConnect channel, TargetServer remoteServer){
        return new DirectLocalMiddleProxy(channel, messageLister,
                remoteServer);
    }
}
