package com.socks.proxy.handshake;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import com.socks.proxy.protocol.handshake.handler.ProxyInfo;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 本地客户端websocket 协议处理消息
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@Slf4j
public class WebsocketLocalProxyMessageHandler extends AbstractLocalProxyMessageHandler{

    private final ProxyFactory factory;


    public WebsocketLocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ProxyFactory factory, ConnectContextManager manager){
        super(rsaUtil, codes,manager );
        this.factory = factory;
    }


    @Override
    public void serviceConnect(ProxyConnect local, TargetServer targetServer){
        ProxyContext proxyContext = manager.getContext(local);
        ProxyInfo proxyInfo = proxyContext.getProxyInfo();
        proxyInfo.setServer(targetServer);
        try {
            ConnectProxyConnect targetConnect = factory.create(targetServer, this);
            proxyContext.setConnect(targetConnect);
            ProxyContext targetContext = new ProxyContext();
            targetContext.setProxyInfo(proxyInfo);
            targetContext.setConnect(local);
            targetConnect.connect();
            manager.putTargetConnect(targetConnect, targetContext);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
