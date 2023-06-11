package com.socks.proxy.handshake.handler.server;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.handshake.message.local.SenTargetAddressMessage;
import com.socks.proxy.handshake.message.server.AckTargetAddressMessage;
import com.socks.proxy.protocol.DefaultDstServer;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.factory.ServerConnectTargetFactory;
import com.socks.proxy.protocol.handshake.SimpleServerHandshakeMessageHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>ack 确认消息处理器实现</p>
 * <p>向本地连接发送连接成功处理器</p>
 * <p>eg：操作系统使用的http代理，或者socks代理；发送连接成功消息；
 * 之后操作系统，或者本地连接才会发送正常的数据到代理服务</p>
 * <p>参考LocaProxyChannel实现：{@link com.socks.proxy.protocol.LocalProxyConnect#writeConnectSuccess()}</p>
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
@AllArgsConstructor
public class ConnectSuccessMessageHandler extends SimpleServerHandshakeMessageHandler<SenTargetAddressMessage>{

    private final ServerConnectTargetFactory factory;


    @Override
    protected void handleServerMessage(RemoteProxyConnect local, SenTargetAddressMessage message,
                                       RemoteProxyConnect remote){
        log.debug("connect to target service = {}:{}", message.getHost(), message.getPort());
        local.setDstServer(new DefaultDstServer(message.getHost(), message.getPort()));
        RemoteProxyConnect proxyService = factory.getProxyService(local);
        try {
            proxyService.connect();
            local.setTarget(proxyService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        local.write(JSON.toJSONString(new AckTargetAddressMessage()));
    }
}
