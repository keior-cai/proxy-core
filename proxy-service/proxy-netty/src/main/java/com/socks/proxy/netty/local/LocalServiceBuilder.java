package com.socks.proxy.netty.local;

import com.socks.proxy.netty.ServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
    private LocalConnectServerFactory connectFactory;

    /**
     * 本地连接监听器
     */
    private List<LocalConnectListener> listeners = new ArrayList<>();

    /**
     * 处理请求线程池
     */
    private ExecutorService executor;

    /**
     * 连接池
     */
    private Pool pool;


    public LocalServiceBuilder addListener(LocalConnectListener listener){
        listeners.add(listener);
        return this;
    }


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
        if(executor == null){
            executor = new ThreadPoolExecutor(10, 200, 3000L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        switch(protocol) {
            case HTTP:
            case HTTPS:
                return new LocalHttpProxyService(port, connectFactory, listeners, executor);
            case SOCKS5:
                return new LocalSocks5ProxyService(port, connectFactory, listeners, executor);
            case COMPLEX:
                return new LocalComplexProxyService(port, connectFactory, listeners, executor);
        }
        throw new RuntimeException();
    }
}
