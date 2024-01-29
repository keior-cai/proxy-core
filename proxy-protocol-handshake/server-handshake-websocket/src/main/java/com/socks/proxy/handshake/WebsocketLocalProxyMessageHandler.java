package com.socks.proxy.handshake;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.connect.RegisterProxyConnect;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
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


    public WebsocketLocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ProxyFactory factory){
        super(rsaUtil, codes);
        this.factory = factory;
    }


    @Override
    public ProxyConnect serviceConnect(ProxyConnect local, TargetServer targetServer){
        ProxyContext proxyContext = getProxyContext(local);
        proxyContext.setServer(targetServer);
        try {
            RegisterProxyConnect targetConnect = factory.create(targetServer, this);
            proxyContext.setConnect(targetConnect);
            ProxyContext targetContext = new ProxyContext();
            targetContext.setServer(targetServer);
            targetContext.setConnect(local);
            targetContext.setCount(proxyContext.getCount());
            putProxyContext(targetConnect, targetContext);
            targetConnect.connect();
            return targetConnect;
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
