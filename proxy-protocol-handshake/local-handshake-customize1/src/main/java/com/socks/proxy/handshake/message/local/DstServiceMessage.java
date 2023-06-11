package com.socks.proxy.handshake.message.local;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.DstServer;
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
public class DstServiceMessage extends ProxyMessage{

    private final int    port;
    private final String host;


    public DstServiceMessage(DstServer remoteServer){
        this(remoteServer.host(), remoteServer.port());
    }


    public DstServiceMessage(String host, int port){
        super(LocalProxyCommand.SEND_DST_ADDR.getCode());
        this.port = port;
        this.host = host;
    }
}
