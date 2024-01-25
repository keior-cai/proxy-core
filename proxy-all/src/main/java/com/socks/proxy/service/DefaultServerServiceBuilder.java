package com.socks.proxy.service;

import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.handshake.handler.NettyWebsocketProxyMessageHandler;
import com.socks.proxy.netty.ServerServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ReconnectHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Getter
@Setter
@Accessors(chain = true)
public class DefaultServerServiceBuilder extends ServerServiceBuilder{

    private List<ServerHandshakeMessageHandler> handlerList = new ArrayList<>();

    private ProxyCodes codes;

    private RSAUtil rsaUtil;


    @Override
    public TcpService builder(){
        if(rsaUtil == null){
            this.rsaUtil = new RSAUtil();
        }
        if(codes == null){
            codes = new DefaultProxyCommandCodes();
        }

        if(handlerList.isEmpty()){
            handlerList.add(new ReconnectHandler(rsaUtil.getPublicKey()));

        }
        if(getHandler() == null){
            setHandler(new WebsocketHandler(()->new NettyWebsocketProxyMessageHandler(rsaUtil)));
        }
        return super.builder();
    }
}
