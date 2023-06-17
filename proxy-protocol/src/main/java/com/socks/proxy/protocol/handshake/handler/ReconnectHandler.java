package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.handshake.SimpleServerHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import com.socks.proxy.protocol.handshake.message.SendReconnectMessage;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
@AllArgsConstructor
public class ReconnectHandler extends SimpleServerHandshakeMessageHandler<SendReconnectMessage>{

    private final String publicKey;


    @Override
    protected void handleServerMessage(ServerMiddleProxy local, SendReconnectMessage message,
                                       TargetConnect middleProxy){
        local.write(JSON.toJSONString(new PublicKeyMessage(publicKey)));
    }
}
