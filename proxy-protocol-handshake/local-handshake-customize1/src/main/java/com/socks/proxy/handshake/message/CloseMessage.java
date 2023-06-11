package com.socks.proxy.handshake.message;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.ServerProxyCommand;

/**
 * 关闭连接消息
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class CloseMessage extends ProxyMessage{

    private static final CloseMessage CLOSE = new CloseMessage();


    public CloseMessage(){
        super(ServerProxyCommand.CLOSE.getCode());
    }


    public static CloseMessage instance(){
        return CLOSE;
    }
}
