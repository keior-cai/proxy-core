package com.socks.proxy.netty;

import com.socks.proxy.netty.enums.ProxyModel;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.system.LinuxSetProxy;
import com.socks.proxy.netty.system.MacSetUpProxy;
import com.socks.proxy.netty.system.SetProxy;
import com.socks.proxy.netty.system.WindowsSetProxy;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ICipher;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
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
     * http 管理端口
     */
    private int httpManagePort = 8000;

    /**
     * proxy name
     */
    private String name;

    /**
     * local 启动代理协议, 默认使用COMPLEX
     */
    private Protocol protocol = Protocol.COMPLEX;

    /**
     * 代理模式
     */
    private ProxyModel proxyModel = ProxyModel.RULE;

    /**
     * 非对称加密工具
     */
    private RSAUtil rsaUtil;

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
     * 消息协议处理
     */
    private ProxyMessageHandler handler;

    /**
     * 代理协议处理器
     */
    private ChannelHandler protocolHandle;

    /**
     * 管理器
     */
    private ConnectContextManager manager;

    /**
     * 连接数据交换线程池
     */
    private ExecutorService executorService;


    @Override
    public TcpService builder(){
        init();
        switch(protocol) {
            case HTTP:
            case HTTPS:
                this.protocolHandle = LocalProxyCode.ofHttp(handler, executorService);
                break;
            case SOCKS5:
                this.protocolHandle = LocalProxyCode.ofSocks5(handler, executorService);
                break;
            case COMPLEX:
                this.protocolHandle = LocalProxyCode.ofComplex(handler, executorService);
                break;
        }
        NettyTcpService nettyTcpService = new NettyTcpService(port, protocolHandle);
        String os = System.getProperty("os.name").toLowerCase();
        SetProxy setProxy;
        if(os.contains("mac")){
            setProxy = new MacSetUpProxy();

        } else if(os.contains("linux")){
            setProxy = new LinuxSetProxy();
        } else {
            setProxy = new WindowsSetProxy();
        }
        return new TcpService(){
            @Override
            public void start(){
                nettyTcpService.start();
                setProxy.turnOnProxy(port);
            }


            @Override
            public void close(){
                setProxy.turnOffProxy();
                nettyTcpService.close();
            }


            @Override
            public void restart(){
                nettyTcpService.restart();
            }
        };

    }


    private void init(){
        if(codes == null){
            this.codes = new DefaultProxyCommandCodes();
        }
        if(rsaUtil == null){
            this.rsaUtil = new RSAUtil();
        }
        if(manager == null){
            manager = new MapConnectContextManager();
        }
        if(executorService == null){
            executorService = new ThreadPoolExecutor(100, 200, 3000L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1024), new NamedThreadFactory("Thread-socks-", true));
        }
    }
}
