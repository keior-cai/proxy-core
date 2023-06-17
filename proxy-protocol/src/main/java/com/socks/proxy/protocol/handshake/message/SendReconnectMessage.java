package com.socks.proxy.protocol.handshake.message;

import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发送重新连接
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@ToString
public class SendReconnectMessage extends ProxyMessage{

    public SendReconnectMessage(){
        super(LocalProxyCommand.SEND_RECONNECT.getCode());
    }
}
