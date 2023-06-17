package com.socks.proxy.protocol.handshake.message;

import com.socks.proxy.protocol.codes.ProxyMessage;
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
