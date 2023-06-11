package com.socks.proxy.service;

import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.CloseMessage;
import com.socks.proxy.protocol.handshake.CloseMessageHandler;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.config.ConnectUserInfo;
import com.socks.proxy.protocol.handshake.local.AckConnectSuccessMessageHandler;
import com.socks.proxy.protocol.handshake.local.SendDstServerMessageHandler;
import com.socks.proxy.protocol.handshake.local.SendRandomPasswordMessageHandler;
import com.socks.proxy.protocol.handshake.server.AckTargetAddressMessage;
import com.socks.proxy.protocol.handshake.server.AckUserMessage;
import com.socks.proxy.protocol.handshake.server.PublicKeyMessage;
import com.socks.proxy.protocol.websocket.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DefaultLocalServiceBuilder extends LocalServiceBuilder{

    /**
     * 消息处理器
     */
    private Map<ProxyCommand, LocalHandshakeMessageHandler> messageHandlerMap;

    private ProxyMessage close;

    private ProxyCodes<? super ProxyMessage> codes;


    public TcpService builder(){
        if(getCodes() == null){
            Map<Integer, Class<? extends ProxyMessage>> codeMap = new HashMap<>();
            codeMap.put(ServerProxyCommand.SEND_PUBLIC_KEY.getCode(), PublicKeyMessage.class);
            codeMap.put(ServerProxyCommand.ACK_USER_MESSAGE.getCode(), AckUserMessage.class);
            codeMap.put(ServerProxyCommand.CONNECT_SUCCESS.getCode(), AckTargetAddressMessage.class);
            codeMap.put(ServerProxyCommand.CLOSE.getCode(), CloseMessage.class);
            setCodes(new DefaultProxyCommandCodes<>(codeMap));
        }
        if(messageHandlerMap == null){
            messageHandlerMap = new HashMap<>();
            initMessageHandlerMap();

        }
        if(getConnectFactory() == null){
            WebsocketFactory websocketFactory = createWebsocketPoolFactory();
            setConnectFactory(
                    new WebsocketConnectProxyServerFactory(websocketFactory, createWebsocketMessageFactory(), codes));
        }

        return super.builder();
    }


    /**
     * 创建websocket 客户端连接池
     */
    private WebsocketFactory createWebsocketPoolFactory(){
        if(getServerList() == null || getServerList().isEmpty()){
            try {
                setServerList(Collections.singletonList(new URI("ws://127.0.0.1:8082")));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        if(getPool() == null){
            setPool(new Pool());
        }
        //        GenericObjectPoolConfig<WebSocket> poolConfig = new GenericObjectPoolConfig<>();
        //        poolConfig.setMaxTotal(getPool().getMaxTotal());
        //        poolConfig.setJmxEnabled(getPool().getJvmEnable());
        //        poolConfig.setMaxIdle(getPool().getMaxIdle());
        //        poolConfig.setMinIdle(getPool().getMinIdle());
        //        return new DefaultWebsocketPoolFactory(getServerList(), poolConfig, codes, close);
        return new DefaultWebsocketFactory(getServerList());
    }


    /**
     * 创建消息处理工厂
     */
    private WebsocketMessageFactory createWebsocketMessageFactory(){
        log.info("load message handle = {}", messageHandlerMap);
        return new DefaultWebsocketMessageFactory(messageHandlerMap, codes);
    }


    private void initMessageHandlerMap(){
        messageHandlerMap.put(ServerProxyCommand.CONNECT_SUCCESS, new AckConnectSuccessMessageHandler());
        messageHandlerMap.put(ServerProxyCommand.ACK_USER_MESSAGE, new SendDstServerMessageHandler());
        messageHandlerMap.put(ServerProxyCommand.SEND_PUBLIC_KEY,
                new SendRandomPasswordMessageHandler(new ConnectUserInfo(null, null)));
        messageHandlerMap.put(ServerProxyCommand.CLOSE, new CloseMessageHandler());
    }
}
