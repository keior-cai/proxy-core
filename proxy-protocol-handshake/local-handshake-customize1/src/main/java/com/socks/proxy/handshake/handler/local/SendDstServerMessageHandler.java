package com.socks.proxy.handshake.handler.local;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.handshake.message.local.SenTargetAddressMessage;
import com.socks.proxy.handshake.message.server.AckUserMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.handshake.SimpleLocalHandshakeMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证帐号成功，发送远程目标地址给服务端 order 2
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
public class SendDstServerMessageHandler extends SimpleLocalHandshakeMessageHandler<AckUserMessage>{

    private String username;

    private String password;

    private String method = "ase-256-cfb";

    private int passwordLen = 10;


    @Override
    protected void handleLocalMessage(LocalProxyConnect local, AckUserMessage message, RemoteProxyConnect remote){
        remote.write(JSON.toJSONString(new SenTargetAddressMessage(local.getDstServer())));

    }
}
