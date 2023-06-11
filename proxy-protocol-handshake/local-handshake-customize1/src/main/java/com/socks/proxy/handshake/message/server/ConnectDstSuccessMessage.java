package com.socks.proxy.handshake.message.server;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@ToString
public class ConnectDstSuccessMessage extends ProxyMessage{

    public ConnectDstSuccessMessage(){
        super(ServerProxyCommand.CONNECT_SUCCESS.getCode());
    }
}
