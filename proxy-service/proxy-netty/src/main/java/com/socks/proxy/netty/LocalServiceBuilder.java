package com.socks.proxy.netty;

import com.neovisionaries.ws.client.WebSocket;
import com.socks.proxy.handshake.WebsocketLocalProxyMessageHandler;
import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.handshake.websocket.DefaultWebsocketFactory;
import com.socks.proxy.handshake.websocket.WebsocketFactory;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.proxy.ComplexProxy;
import com.socks.proxy.netty.proxy.HttpTunnelProxy;
import com.socks.proxy.netty.proxy.Socks5CommandHandler;
import com.socks.proxy.netty.proxy.Socks5Proxy;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ICipher;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.UnKnowProtocolException;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.*;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class LocalServiceBuilder implements ServiceBuilder{

    /**
     * local 服务端口
     */
    private int port = 1081;

    /**
     * local 启动代理协议, 默认使用COMPLEX
     */
    private Protocol protocol = Protocol.COMPLEX;

    /**
     * ss-server 服务器地址
     */
    private List<URI> serverList;

    /**
     * 连接远程服务工厂
     */
    private ProxyFactory connectFactory;

    /**
     * 处理请求线程池
     */
    private ExecutorService executor;

    /**
     * 连接池
     */
    private Pool pool;

    /**
     *
     */
    private RSAUtil rsaUtil;

    /**
     * 连接ss-server使用的用户名
     */
    private String username;

    /**
     * 连接ss-server使用的密码
     */
    private String password;

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
     * 连接管理器
     */
    private ConnectContextManager manager;

    @Getter
    @Setter
    @ToString
    public static class Pool{
        private int maxTotal = 200;

        private int maxIdle = 100;

        private int minIdle = 50;

        private Boolean jvmEnable = false;
    }


    @Override
    public TcpService builder(){
        init();
        AbstractLocalProxyMessageHandler handler = new WebsocketLocalProxyMessageHandler(rsaUtil, codes, connectFactory,
                manager);
        switch(protocol) {
            case HTTP:
            case HTTPS:
                HandshakeProtocolHandler httpProtocolHandler = new HttpHandshakeProtocolHandler();
                return new NettyTcpService(port,
                        new LocalProxyCode(httpProtocolHandler, new HttpTunnelProxy(handler), handler));
            case SOCKS5:
                return new NettyTcpService(port, new LocalProxyCode(new Socks5HandshakeProtocolHandler(),
                        new Socks5Proxy(new Socks5CommandHandler(handler)), handler));
            case COMPLEX:
                ComplexHandshakeProtocolHandler protocolHandler = new ComplexHandshakeProtocolHandler();
                List<SimpleChannelInboundHandler<?>> list = Arrays.asList(
                        new Socks5Proxy(new Socks5CommandHandler(handler)), new HttpTunnelProxy(handler));
                return new NettyTcpService(port, new LocalProxyCode(protocolHandler, new ComplexProxy(list), handler));
        }
        throw new UnKnowProtocolException();
    }


    private void init(){
        if(executor == null){
            this.executor = new ThreadPoolExecutor(10, 200, 3000L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        if(codes == null){
            this.codes = new DefaultProxyCommandCodes();
        }

        if(connectFactory == null){
            WebsocketFactory websocketFactory = createWebsocketPoolFactory();
            this.connectFactory = new WebsocketProxyConnectFactory(websocketFactory);
        }
        if(rsaUtil == null){
            this.rsaUtil = new RSAUtil();
        }
        if(codes == null){
            codes = new DefaultProxyCommandCodes();
        }
        if(manager == null){
            manager = new MapConnectContextManager();
        }
    }


    /**
     * 创建websocket 客户端连接池
     */
    private WebsocketFactory createWebsocketPoolFactory(){
        if(Objects.isNull(this.serverList) || this.serverList.isEmpty()){
            this.serverList = Collections.singletonList(URI.create("ws://127.0.0.1:8082"));
        }
        if(pool == null){
            this.pool = new Pool();
            GenericObjectPoolConfig<WebSocket> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(pool.getMaxTotal());
            poolConfig.setJmxEnabled(pool.getJvmEnable());
            poolConfig.setMaxIdle(pool.getMaxIdle());
            poolConfig.setMinIdle(pool.getMinIdle());
        }
        return new DefaultWebsocketFactory(getServerList());
    }
}
