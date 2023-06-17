package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;
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
    public void handle(LocalConnect local, ProxyMessage message, LocalMiddleProxy remote){
        local.close();
    }


    @Override
    public void handle(ServerMiddleProxy local, ProxyMessage message, TargetConnect remote){
        local.close();
    }
}
