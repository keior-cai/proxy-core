package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.AckUserMessage;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证帐号成功，发送远程目标地址给服务端 order 2
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
public class SendTargetServerMessageHandler implements LocalHandshakeMessageHandler<AckUserMessage>{
    @Override
    public void handle(LocalConnect local, AckUserMessage message, LocalMiddleService remote){
        remote.write(JSON.toJSONString(new SenTargetAddressMessage(local.getDstServer())));
    }
}
