package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.handshake.SimpleLocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.AckUserMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证帐号成功，发送远程目标地址给服务端 order 2
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
public class SendTargetServerMessageHandler extends SimpleLocalHandshakeMessageHandler<AckUserMessage>{

    @Override
    protected void handleLocalMessage(LocalConnect local, AckUserMessage message, LocalMiddleProxy remote){
        remote.write(JSON.toJSONString(new SenTargetAddressMessage(local.getDstServer())));

    }
}
