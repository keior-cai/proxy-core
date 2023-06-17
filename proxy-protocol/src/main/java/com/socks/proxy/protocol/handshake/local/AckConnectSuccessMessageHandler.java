package com.socks.proxy.protocol.handshake.local;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.handshake.SimpleLocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.server.AckTargetAddressMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
public class AckConnectSuccessMessageHandler extends SimpleLocalHandshakeMessageHandler<AckTargetAddressMessage>{
    @Override
    public void handleLocalMessage(LocalConnect local, AckTargetAddressMessage message, LocalMiddleProxy remote){
        log.debug("send to system connect success...");
        local.writeConnectSuccess();
    }

}
