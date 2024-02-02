package com.socks.proxy.local;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class Test{
    public static void main(String[] args){
        RSAUtil rsaUtil = new RSAUtil();
        ProxyCodes codes = new DefaultProxyCommandCodes();
        LocalProxyMessageHandler handler = new LocalProxyMessageHandler(rsaUtil, codes, new MapConnectContextManager());
        Map<String, ProxyFactory> proxyFactoryMap = new HashMap<>();
        proxyFactoryMap.put("test", WebsocketProxyConnectFactory.createDefault(
                Collections.singletonList(URI.create("ws://127.0.0.1:8083"))));
        handler.setName("test");
        handler.setFactoryMap(proxyFactoryMap);
        TcpService tcpService = new LocalServiceBuilder().setPort(1088).setCodes(codes).setHandler(handler)
                .setRsaUtil(rsaUtil).setProtocol(Protocol.COMPLEX).builder();
        tcpService.start();
    }
}
