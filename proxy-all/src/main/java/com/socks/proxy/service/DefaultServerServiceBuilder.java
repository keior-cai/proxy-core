package com.socks.proxy.service;

import com.socks.proxy.handshake.MessageListener;
import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.listener.ServerWebsocketListener;
import com.socks.proxy.netty.DefaultNettyConnectServerFactory;
import com.socks.proxy.netty.ServerServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import com.socks.proxy.protocol.factory.ServerConnectTargetFactory;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.local.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.local.SendUserMessage;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Getter
@Setter
@Accessors(chain = true)
public class DefaultServerServiceBuilder extends ServerServiceBuilder{

    private MessageListener messageListener;

    private List<ServerHandshakeMessageHandler> handlerList = new ArrayList<>();

    private ProxyCodes<? super ProxyMessage> decode;

    private RSAUtil rsaUtil;

    private ServerConnectTargetFactory connectFactory;


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
            codeMap.put(LocalProxyCommand.SEND_USER_INFO.getCode(), SendUserMessage.class);
            codeMap.put(LocalProxyCommand.SEND_DST_ADDR.getCode(), SenTargetAddressMessage.class);
            decode = new DefaultProxyCommandCodes<>(codeMap);
        }

        if(messageListener == null){
            Map<Class<? extends ProxyMessage>, List<ServerHandshakeMessageHandler>> handlerMap = handlerList.stream()
                    .collect(Collectors.groupingBy(this::getProxyMessageClass, Collectors.toList()));
            messageListener = new ServerWebsocketListener(handlerMap, decode, rsaUtil.getPublicKey());
        }
        if(getHandler() == null){
            setHandler(new WebsocketHandler(messageListener));
        }
        return super.builder();
    }


    @SuppressWarnings("unchecked")
    private Class<? extends ProxyMessage> getProxyMessageClass(ServerHandshakeMessageHandler handler){
        ParameterizedType genericSuperclass = (ParameterizedType) handler.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        if(actualTypeArguments.length == 0){
            return ProxyMessage.class;
        }
        return (Class<? extends ProxyMessage>) actualTypeArguments[0];
    }
}
