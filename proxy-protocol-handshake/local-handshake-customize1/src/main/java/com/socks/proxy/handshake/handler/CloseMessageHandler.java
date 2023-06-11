package com.socks.proxy.handshake.handler;

import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端发送关闭连接消息
 *
 * @author: chuangjie
 * @date: 2023/5/23
 **/
@Slf4j
public class CloseMessageHandler implements ServerHandshakeMessageHandler, LocalHandshakeMessageHandler{

    @Override
    public void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        local.close();
    }


    @Override
    public void handle(RemoteProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        local.close();
    }
}
