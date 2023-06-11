package com.socks.proxy.handshake.message.server;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.ServerProxyCommand;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
public class AckUserMessage extends ProxyMessage{
    public AckUserMessage(){
        super(ServerProxyCommand.ACK_USER_MESSAGE.getCode());
    }
}
