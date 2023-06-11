package com.socks.proxy.handshake.handler.local;

import com.alibaba.fastjson2.JSON;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.handshake.message.local.DstServiceMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证帐号成功，发送远程目标地址给服务端 order 2
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
public class SendDstServerMessageHandler implements LocalHandshakeMessageHandler{

    private String username;

    private String password;

    private String method = "ase-256-cfb";

    private int passwordLen = 10;


    @Override
    public void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        remote.write(JSON.toJSONString(new DstServiceMessage(local.getDstServer())));
    }


    @Override
    public ProxyCommand command(){
        return ServerProxyCommand.ACK_USER_MESSAGE;
    }
}
