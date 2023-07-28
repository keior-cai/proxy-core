package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.AckTargetAddressMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
public class AckConnectSuccessMessageHandler implements LocalHandshakeMessageHandler<AckTargetAddressMessage>{

    @Override
    public void handle(LocalConnect local, AckTargetAddressMessage message, LocalMiddleService remote){
        local.writeConnectSuccess();

    }
}
