package com.socks.proxy.netty;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.netty.connect.DirectConnectFactory;
import com.socks.proxy.netty.http.HttpHandle;
import com.socks.proxy.netty.http.HttpService;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.NoCodeProxyCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.ServiceProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ServerServiceBuilder implements ServiceBuilder{

    private int port;

    private int httpProt = 4000;

    private ChannelHandler handler;

    private ProxyCodes codes;

    private RSAUtil rsaUtil;

    /**
     * 是否使用转化握手协议 如果存在自定义codes时不生效
     */
    private boolean useCodes = true;

    /**
     * 开启http管理服务
     */
    private boolean useHttpService = true;

    /**
     * 连接管理器
     */
    private ConnectContextManager connectContextManager;


    @Override
    public TcpService builder(){
        init();
        NettyTcpService nettyTcpService = new NettyTcpService(port, handler);
        Map<String, HttpHandle> handleMap = new HashMap<>();
        handleMap.put("/connects", (request, response)->{
            Set<ProxyConnect> connects = connectContextManager.getTargetAllProxy();
            List<String> collect = connects.stream().map(item->item.remoteAddress().toString())
                    .collect(Collectors.toList());
            response.content().writeBytes(JSON.toJSONString(collect).getBytes());
        });
        NettyTcpService httpService;
        if(useHttpService){
            httpService = new NettyTcpService(httpProt, new HttpService(handleMap));
        } else {
            httpService = null;
        }
        return new TcpService(){
            @Override
            public void start(){
                nettyTcpService.start();
                Optional.ofNullable(httpService).ifPresent(TcpService::start);
            }


            @Override
            public void close(){
                nettyTcpService.close();
                Optional.ofNullable(httpService).ifPresent(TcpService::close);
            }


            @Override
            public void restart(){
                nettyTcpService.restart();
            }
        };
    }


    private void init(){
        if(connectContextManager == null){
            this.connectContextManager = new MapConnectContextManager();
        }
        if(handler == null){
            this.handler = new WebsocketHandler(
                    ()->new ServiceProxyMessageHandler(rsaUtil, codes, connectContextManager,
                            DirectConnectFactory.INSTANCE));
        }
        if(codes == null && useCodes){
            codes = new DefaultProxyCommandCodes();
        } else if(codes == null){
            codes = new NoCodeProxyCodes();
        }
        if(rsaUtil == null){
            rsaUtil = new RSAUtil();
        }
    }
}
