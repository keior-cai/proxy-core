package com.socks.proxy.local;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.netty.LocalHttpManagerBuilder;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.netty.enums.ProxyModel;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class Test{
    public static void main(String[] args){
        RSAUtil rsaUtil = new RSAUtil();
        MapConnectContextManager manager = new MapConnectContextManager();
        LocalProxyMessageHandler localProxyMessageHandler = new LocalProxyMessageHandler(new RSAUtil(),
                new DefaultProxyCommandCodes(), manager,
                WebsocketProxyConnectFactory.createDefault("ws://chuangjie.icu:8042"));

        TcpService tcpService = new LocalServiceBuilder()
                .setPort(1089)
                .setManager(manager)
                .setProxyModel(ProxyModel.RULE)
                .setHandler(localProxyMessageHandler)
                .setRsaUtil(rsaUtil)
                .setProtocol(Protocol.COMPLEX)
                .builder();
        tcpService.start();

        TcpService http = new LocalHttpManagerBuilder()
                .setPort(8000)
                .setManager(manager)
                .setLocalProxyMessageHandler(localProxyMessageHandler)
                .builder();
        http.start();
    }
}
