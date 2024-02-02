package com.socks.proxy.netty;

import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ICipher;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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


    @Override
    public TcpService builder(){
        init();
        switch(protocol) {
            case HTTP:
            case HTTPS:
                this.protocolHandle = LocalProxyCode.ofHttp(handler);
                break;
            case SOCKS5:
                this.protocolHandle = LocalProxyCode.ofSocks5(handler);
                break;
            case COMPLEX:
                this.protocolHandle = LocalProxyCode.ofComplex(handler);
                break;
        }
        return new NettyTcpService(port, protocolHandle);
    }


    private void init(){
        if(codes == null){
            this.codes = new DefaultProxyCommandCodes();
        }
        if(rsaUtil == null){
            this.rsaUtil = new RSAUtil();
        }
    }

    //
    //    public TcpService builderProperties(LocalProperties properties){
    //        if(Objects.nonNull(properties.getPrivateKey()) && Objects.nonNull(properties.getPublicKey())){
    //            rsaUtil = new RSAUtil(properties.getPrivateKey(), rsaUtil.getPublicKey());
    //        }
    //        this.port = properties.getPort();
    //        this.protocol = properties.getProtocol();
    //        List<ProxyProperties> proxies = properties.getProxies();
    //        Map<String, ProxyFactory> factoryMap = new ConcurrentHashMap<>();
    //        for(ProxyProperties proxy : proxies) {
    //            ProxyFactory factory;
    //            switch(proxy.getType()) {
    //                case WEBSOCKET:
    //                    factory = new WebsocketProxyConnectFactory(new DefaultWebsocketFactory(proxy.getAddr()));
    //                    break;
    //                default:
    //                    throw new IllegalArgumentException("unknown proxy type");
    //            }
    //            factoryMap.putIfAbsent(proxy.getName(), factory);
    //        }
    //        init();
    //        LocalProxyMessageHandler localProxyMessageHandler = new LocalProxyMessageHandler(rsaUtil, codes, manager);
    //        localProxyMessageHandler.setFactoryMap(factoryMap);
    //        localProxyMessageHandler.setName(properties.getName());
    //        this.handler = localProxyMessageHandler;
    //        return builder();
    //    }
}
