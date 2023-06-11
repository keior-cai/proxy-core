package com.socks.proxy.handshake.handler.server;

import com.alibaba.fastjson2.JSON;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.handshake.DefaultCipher;
import com.socks.proxy.handshake.message.CloseMessage;
import com.socks.proxy.handshake.message.local.UserMessage;
import com.socks.proxy.handshake.message.server.AckUserMessage;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
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
public class AckUserMessageHandler implements ServerHandshakeMessageHandler{

    private final RSAUtil rsaUtil;


    @Override
    public void handle(RemoteProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        UserMessage userMessage = (UserMessage) message;
        try {
            String methodPassword = rsaUtil.decrypt(AESUtil.decryptByDefaultKey(userMessage.getRandom()));
            log.debug("{}write ack user message = {} password = {}", local.channelId(), message, methodPassword);
            local.setCipher(new DefaultCipher(CipherProvider.getByName(userMessage.getMethod(), methodPassword)));
            local.write(JSON.toJSONString(new AckUserMessage()));
        } catch (Exception e) {
            log.error("", e);
            local.write(JSON.toJSONString(new CloseMessage()));
        }
    }


    @Override
    public ProxyCommand command(){
        return ServerProxyCommand.ACK_USER_MESSAGE;
    }
}
