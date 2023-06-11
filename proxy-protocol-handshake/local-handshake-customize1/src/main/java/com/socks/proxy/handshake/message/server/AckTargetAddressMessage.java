package com.socks.proxy.handshake.message.server;

import com.socks.proxy.protocol.codes.ProxyMessage;
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
public class AckTargetAddressMessage extends ProxyMessage{

    public AckTargetAddressMessage(){
        super(ServerProxyCommand.CONNECT_SUCCESS.getCode());
    }
}
