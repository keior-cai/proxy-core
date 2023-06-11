package com.socks.proxy.handshake.handler.local;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
public class AckConnectSuccessMessageHandler implements LocalHandshakeMessageHandler{
    @Override
    public void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        log.debug("send to system connect success...");
        local.writeConnectSuccess();
    }


    @Override
    public ProxyCommand command(){
        return ServerProxyCommand.CONNECT_SUCCESS;
    }
}
