package com.socks.proxy.handshake.handler.local;

import com.socks.proxy.handshake.message.server.AckTargetAddressMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.handshake.SimpleLocalHandshakeMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
public class AckConnectSuccessMessageHandler extends SimpleLocalHandshakeMessageHandler<AckTargetAddressMessage>{
    @Override
    public void handleLocalMessage(LocalProxyConnect local, AckTargetAddressMessage message,
                                   RemoteProxyConnect remote){
        log.debug("send to system connect success...");
        local.writeConnectSuccess();
    }

}
