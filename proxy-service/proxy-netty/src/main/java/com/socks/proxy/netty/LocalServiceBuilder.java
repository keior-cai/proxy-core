package com.socks.proxy.netty;

import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.TcpService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.net.URI;
import java.util.List;

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
     * local 启动代理协议, 默认使用socks5
     */
    private Protocol protocol = Protocol.SOCKS5;

    /**
     * ss-server 服务器地址
     */
    private List<URI> serverList;

    /**
     * 连接远程服务工厂
     */
    private LocalConnectServerFactory connectFactory;

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
        switch(protocol) {
            case HTTP:
            case HTTPS:
                return new LocalHttpProxyService(port, connectFactory);
            case SOCKS5:
                return new LocalSocks5ProxyService(port, connectFactory);
        }
        throw new RuntimeException();
    }
}
