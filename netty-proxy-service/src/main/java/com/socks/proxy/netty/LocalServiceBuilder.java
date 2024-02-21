package com.socks.proxy.netty;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.netty.connect.DirectConnectFactory;
import com.socks.proxy.netty.enums.ProxyModel;
import com.socks.proxy.netty.http.HttpHandle;
import com.socks.proxy.netty.http.HttpService;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.system.LinuxSetProxy;
import com.socks.proxy.netty.system.MacSetUpProxy;
import com.socks.proxy.netty.system.SetProxy;
import com.socks.proxy.netty.system.WindowsSetProxy;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ICipher;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.factory.RuleLocalConnectServerFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 代理工厂
     */
    private Map<String, ProxyFactory> proxyFactoryMap;

    /**
     * 管理器
     */
    private ConnectContextManager manager;

    /**
     * 代理模式
     */
    private Map<String, List<String>> ruleMap;


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
        NettyTcpService nettyTcpService = new NettyTcpService(port, protocolHandle);

        Map<String, HttpHandle> map = new HashMap<>();
        map.put("/stop", (request, response)->{
            nettyTcpService.close();
            response.content().writeBytes("OK".getBytes());
        });
        map.put("/start", (request, response)->{
            init();
            nettyTcpService.start();
            response.content().writeBytes("OK".getBytes());
        });
        map.put("/restart", (request, response)->{
            init();
            nettyTcpService.restart();
            response.content().writeBytes("OK".getBytes());
        });
        map.put("/ping", (request, response)->{
            List<Map<String, Object>> collect = proxyFactoryMap.entrySet().stream().map(value->{
                ProxyFactory factory = value.getValue();
                Map<String, Object> p = new HashMap<>();
                p.put("host", factory.uri().toString());
                p.put("ping", factory.ping());
                p.put("name", value.getKey());
                return p;
            }).collect(Collectors.toList());
            response.content().writeBytes(JSON.toJSONString(collect).getBytes());

        });
        map.put("/connects", (request, response)->{
            List<JSONObject> collect = manager.getTargetAllProxy().stream().map(item->{
                ProxyContext context = manager.getContext(item);
                TargetServer server = context.getProxyInfo().getServer();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("src", item.remoteAddress());
                jsonObject.put("type", context.getConnect().type());
                jsonObject.put("dst", MapUtil.builder("proxy", server.sourceProtocol().toString())
                        .put("address", server.host() + ":" + server.port()).map());
                return jsonObject;
            }).collect(Collectors.toList());
            response.content().writeBytes(JSON.toJSONString(collect).getBytes());
        });
        NettyTcpService httpService = new NettyTcpService(httpManagePort, new HttpService(map));

        String os = System.getProperty("os.name").toLowerCase();
        SetProxy setProxy;
        switch(os) {
            case "mac":
                setProxy = new MacSetUpProxy();
            break;
            case "linux":
                setProxy = new LinuxSetProxy();
                break;
            default:
            case "win":
                setProxy = new WindowsSetProxy();
                break;

        }

        return new TcpService(){
            @Override
            public void start(){
                nettyTcpService.start();
                httpService.start();
                setProxy.turnOnProxy(port);
            }


            @Override
            public void close(){
                setProxy.turnOffProxy();
                nettyTcpService.close();
                httpService.close();
            }


            @Override
            public void restart(){
                nettyTcpService.restart();
                httpService.restart();
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
        ProxyFactory factory = proxyFactoryMap.get(name);
        LocalProxyMessageHandler localProxyMessageHandler = new LocalProxyMessageHandler(rsaUtil, codes, manager);
        switch(proxyModel) {
            case RULE:
                RuleLocalConnectServerFactory proxyFactory = new RuleLocalConnectServerFactory(
                        new DirectConnectFactory());
                if(ruleMap != null){
                    ruleMap.forEach((k, v)->v.forEach(model->{
                        if(model.equalsIgnoreCase("DIRECT")){
                            proxyFactory.addDomain(k, new DirectConnectFactory());
                        } else if(model.equalsIgnoreCase("PROXY")){
                            proxyFactory.addDomain(k, factory);
                        } else {
                            ProxyFactory domain = proxyFactoryMap.get(model);
                            if(domain == null){
                                throw new IllegalArgumentException();
                            }
                            proxyFactory.addDomain(k, domain);
                        }
                    }));
                }
                localProxyMessageHandler.setFactory(proxyFactory);
                break;
            case DIRECT:
                localProxyMessageHandler.setFactory(new DirectConnectFactory());
            default:
            case GLOBAL:
                localProxyMessageHandler.setFactory(factory);
        }
        handler = localProxyMessageHandler;
    }
}
