package com.socks.proxy.service;

import com.neovisionaries.ws.client.WebSocket;
import com.socks.proxy.netty.local.LocalServiceBuilder;
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
import com.socks.proxy.protocol.handshake.handler.AckConnectSuccessMessageHandler;
import com.socks.proxy.protocol.handshake.handler.SendRandomPasswordMessageHandler;
import com.socks.proxy.protocol.handshake.handler.SendTargetServerMessageHandler;
import com.socks.proxy.protocol.handshake.message.AckTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.AckUserMessage;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import com.socks.proxy.protocol.listener.WebsocketMessageFactory;
import com.socks.proxy.protocol.websocket.DefaultWebsocketFactory;
import com.socks.proxy.protocol.websocket.DefaultWebsocketMessageFactory;
import com.socks.proxy.protocol.websocket.WebsocketConnectProxyServerFactory;
import com.socks.proxy.protocol.websocket.WebsocketFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认ss-loca 服务构建者
 * <pre>
 *     用于构建ss-local服务的创建
 *     由于构建服务需要的依赖比较多，因此使用构建者帮助使用者简单化的实现ss-local服务的创建
 * </pre>
 * 依赖参考
 * <pre>
 *  {@link com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler}
 *  {@link com.socks.proxy.protocol.codes.ProxyCodes}
 *  {@link com.socks.proxy.netty.local.LocalServiceBuilder}
 * </pre>
 *
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
     * <pre>
     * 消息处理器，接受ss-server发送过来的消息，并且对消息做出相应的处理
     * key: 消息类型编码， value消息处理器
     * </pre>
     * <pre>
     *     {@link com.socks.proxy.protocol.enums.ServerProxyCommand}
     *     {@link com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler}
     * </pre>
     */
    private Map<ProxyCommand, LocalHandshakeMessageHandler> messageHandlerMap;

    private ProxyMessage close;

    /**
     * <pre>
     * 消息解析器
     * ss-local 于ss-server进行通信的时候，会对消息进行加密
     * 所以这里需要使用解析器对消息进行加解密
     * 这里的加解密不是发送数据源的加解密，这里只是对通信的消息进行加解密
     * 发送数据源消息的加解密参考{@link com.socks.proxy.protocol.ICipher}
     * </pre>
     */
    private ProxyCodes<? super ProxyMessage> codes;

    /**
     * 连接ss-server使用的用户名
     */
    private String username;

    /**
     * 连接ss-server使用的密码
     */
    private String password;


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
        GenericObjectPoolConfig<WebSocket> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(getPool().getMaxTotal());
        poolConfig.setJmxEnabled(getPool().getJvmEnable());
        poolConfig.setMaxIdle(getPool().getMaxIdle());
        poolConfig.setMinIdle(getPool().getMinIdle());
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
        messageHandlerMap.put(ServerProxyCommand.ACK_USER_MESSAGE, new SendTargetServerMessageHandler());
        messageHandlerMap.put(ServerProxyCommand.SEND_PUBLIC_KEY,
                new SendRandomPasswordMessageHandler(new ConnectUserInfo(username, password)));
        messageHandlerMap.put(ServerProxyCommand.CLOSE, new CloseMessageHandler());
    }
}
