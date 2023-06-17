package com.socks.proxy.protocol.handshake.message;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * 发送目标服务地址消息
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@ToString
public class SenTargetAddressMessage extends ProxyMessage{

    private final int port;

    private final String host;


    public SenTargetAddressMessage(TargetServer remoteServer){
        this(remoteServer.host(), remoteServer.port());
    }


    public SenTargetAddressMessage(String host, int port){
        super(LocalProxyCommand.SEND_DST_ADDR.getCode());
        this.port = port;
        this.host = host;
    }
}
