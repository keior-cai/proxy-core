package com.socks.proxy.service;

import com.neovisionaries.ws.client.WebSocket;
import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.handshake.websocket.DefaultWebsocketFactory;
import com.socks.proxy.handshake.websocket.WebsocketFactory;
import com.socks.proxy.netty.local.LocalServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ICipher;
import com.socks.proxy.protocol.codes.ProxyCodes;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 默认ss-loca 服务构建者
 * <pre>
 *     用于构建ss-local服务的创建
 *     由于构建服务需要的依赖比较多，因此使用构建者帮助使用者简单化的实现ss-local服务的创建
 * </pre>
 * 依赖参考
 * <pre>
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
     * 消息解析器
     * ss-local 于ss-server进行通信的时候，会对消息进行加密
     * 所以这里需要使用解析器对消息进行加解密
     * 这里的加解密不是发送数据源的加解密，这里只是对通信的消息进行加解密
     * 发送数据源消息的加解密参考{@link ICipher}
     * </pre>
     */
    private ProxyCodes codes;

    /**
     * 连接ss-server使用的用户名
     */
    private String username;

    /**
     * 连接ss-server使用的密码
     */
    private String password;


    public TcpService builder(){
        if(getExecutor() == null){
            setExecutor(new ThreadPoolExecutor(10, 200, 3000L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy()));
        }

        if(getCodes() == null){
            setCodes(new DefaultProxyCommandCodes());
        }
        if(getConnectFactory() == null){
            WebsocketFactory websocketFactory = createWebsocketPoolFactory();
            //            WebsocketConnectProxyServerFactory connectFactory = new WebsocketConnectProxyServerFactory(websocketFactory,
            //                    createWebsocketMessageFactory(), new EncodeLocalMiddleServiceProxyFactory(codes));

            //            setConnectFactory(connectFactory);
            WebsocketProxyConnectFactory factory = new WebsocketProxyConnectFactory(websocketFactory);
            //            RuleLocalConnectServerFactory ruleFactory = new RuleLocalConnectServerFactory(factory,
            //                    new DirectLocalConnectServerFactory(getListeners()));
            //            ruleFactory.addDomain("google.com");
            //            ruleFactory.addDomain("54.89.135.129");
            //            ruleFactory.addDomain("157.240.17.14");
            //            ruleFactory.addDomain("14.119.104.189");
            //            ruleFactory.addDomain("baidu.com");
            //            ruleFactory.addDomain("14.119.104.254");
            setConnectFactory(factory);
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

}
