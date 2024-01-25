package com.socks.proxy.netty.local;

import com.socks.proxy.netty.ServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.UnKnowProtocolException;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.WebsocketLocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
        RSAUtil rsaUtil = new RSAUtil();
        WebsocketLocalProxyMessageHandler handler = new WebsocketLocalProxyMessageHandler(rsaUtil,
                new DefaultProxyCommandCodes(), connectFactory);
        switch(protocol) {
            case HTTP:
            case HTTPS:
                return new LocalHttpProxyService(port, handler);
            case SOCKS5:
                return new LocalSocks5ProxyService(port, handler);
            case COMPLEX:
                return new LocalComplexProxyService(port, handler);
        }
        throw new UnKnowProtocolException();
    }
}
