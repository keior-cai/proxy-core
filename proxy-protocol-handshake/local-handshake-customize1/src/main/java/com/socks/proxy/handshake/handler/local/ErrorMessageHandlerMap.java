package com.socks.proxy.handshake.handler.local;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/5/24
 **/
@Slf4j
public class ErrorMessageHandlerMap implements LocalHandshakeMessageHandler{

    @Override
    public void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        log.debug("error message = {}", message);
    }


    @Override
    public ProxyCommand command(){
        return null;
    }

}
