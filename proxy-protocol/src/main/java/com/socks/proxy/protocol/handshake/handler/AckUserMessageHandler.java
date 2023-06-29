package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.DefaultCipher;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.handshake.CloseMessage;
import com.socks.proxy.protocol.handshake.SimpleServerHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.AckUserMessage;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
@AllArgsConstructor
public class AckUserMessageHandler extends SimpleServerHandshakeMessageHandler<SendUserMessage>{

    private final RSAUtil rsaUtil;


    @Override
    protected void handleServerMessage(ServerMiddleProxy local, SendUserMessage message, TargetConnect middleProxy){
        try {
            String methodPassword = rsaUtil.decrypt(AESUtil.decryptByDefaultKey(message.getRandom()));
            log.debug("{}write ack user message = {} password = {}", local.channelId(), message, methodPassword);
            local.setCipher(new DefaultCipher(CipherProvider.getByName(message.getMethod(), methodPassword)));
            local.write(JSON.toJSONString(new AckUserMessage()));
        } catch (Exception e) {
            log.error("", e);
            local.write(JSON.toJSONString(CloseMessage.instance()));
        }
    }
}