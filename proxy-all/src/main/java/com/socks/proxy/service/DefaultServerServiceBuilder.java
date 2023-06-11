package com.socks.proxy.service;

import com.socks.proxt.codes.ProxyCodes;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.codes.json.DefaultProxyCommandCodes;
import com.socks.proxy.handshake.MessageListener;
import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.handshake.handler.CloseMessageHandler;
import com.socks.proxy.handshake.handler.server.AckUserMessageHandler;
import com.socks.proxy.handshake.handler.server.ConnectSuccessMessageHandler;
import com.socks.proxy.handshake.message.local.DstServiceMessage;
import com.socks.proxy.handshake.message.local.UserMessage;
import com.socks.proxy.listener.ServerWebsocketListener;
import com.socks.proxy.netty.DefaultNettyConnectServerFactory;
import com.socks.proxy.netty.ServerServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import com.socks.proxy.protocol.factory.ServerConnectTargetFactory;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Getter
@Setter
@Accessors(chain = true)
public class DefaultServerServiceBuilder extends ServerServiceBuilder{

    private MessageListener messageListener;

    private Map<ProxyCommand, ServerHandshakeMessageHandler> handlerMap;

    private ProxyCodes<? super ProxyMessage> decode;

    private RSAUtil rsaUtil;

    private ServerConnectTargetFactory connectFactory;

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlwS6f4FBSHKDgg8Tti2YXW6ic8BGLeoKI8IuXEUy0q2cV53DcJ7ON55oXuuDuBRLE6PanT86gcoRTp1IOTKjI7fga3arIaWjYubEBzCLUlTPQx/jjO0/mWarj4yvKzk6Ulo/uXWumR+dx0dYiGtbJQlClgILvYtxNHQB7uXWPjwIDAQAB";


    @Override
    public TcpService builder(){
        if(rsaUtil == null){
            this.rsaUtil = new RSAUtil();
        }
        if(connectFactory == null){
            connectFactory = new DefaultNettyConnectServerFactory();
        }
        if(decode == null){
            Map<Integer, Class<? extends ProxyMessage>> codeMap = new HashMap<>();
            codeMap.put(LocalProxyCommand.SEND_USER_INFO.getCode(), UserMessage.class);
            codeMap.put(LocalProxyCommand.SEND_DST_ADDR.getCode(), DstServiceMessage.class);
            decode = new DefaultProxyCommandCodes<>(codeMap);
        }
        if(handlerMap == null){
            handlerMap = new HashMap<>();
            initHandlerMap();
        }
        if(messageListener == null){
            messageListener = new ServerWebsocketListener(handlerMap, decode, publicKey);
        }
        if(getHandler() == null){
            setHandler(new WebsocketHandler(messageListener));
        }
        return super.builder();
    }


    private void initHandlerMap(){
        handlerMap.put(LocalProxyCommand.CLOSE, new CloseMessageHandler());
        handlerMap.put(LocalProxyCommand.SEND_DST_ADDR, new ConnectSuccessMessageHandler(connectFactory));
        handlerMap.put(LocalProxyCommand.SEND_USER_INFO, new AckUserMessageHandler(rsaUtil));
        //        handlerMap.put(LocalProxyCommand.SEND_USER_INFO, new AckDstServerMessageHandler(connectFactory, decode));
        //        handlerMap.put(ServerProxyCommand.CONNECT_SUCCESS, new AckDstServerMessageHandler(connectFactory, decode));
        //        handlerMap.put(ServerProxyCommand.SEND_PUBLIC_KEY, new SendPublicKeyMessageHandler());
    }
}
