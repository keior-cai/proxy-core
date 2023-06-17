package com.socks.proxy.protocol.handshake.message;

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
public class PublicKeyMessage extends ProxyMessage{

    private String publicKey;


    public PublicKeyMessage(String publicKey){
        super(ServerProxyCommand.SEND_PUBLIC_KEY.getCode());
        this.publicKey = publicKey;
    }
}
