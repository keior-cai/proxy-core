package com.socks.proxy.netty;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.netty.connect.DirectConnectFactory;
import com.socks.proxy.netty.http.HttpHandle;
import com.socks.proxy.netty.http.HttpService;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.factory.RuleLocalConnectServerFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: chuangjie
 * @date: 2024/2/23
 **/
@Setter
@ToString
@Accessors(chain = true)
public class LocalHttpManagerBuilder implements ServiceBuilder{

    private int port = 8000;

    private TcpService tcpService;

    /**
     * 管理器
     */
    private ConnectContextManager manager;

    /**
     * 代理工厂
     */
    private Map<String, ProxyFactory> proxyFactoryMap;

    private LocalProxyMessageHandler localProxyMessageHandler;


    @Override
    public TcpService builder(){
        Map<String, HttpHandle> map = new HashMap<>();
        map.put("/stop", (request, response)->{
            tcpService.close();
            response.content().writeBytes("OK".getBytes());
        });
        map.put("/start", (request, response)->{
            tcpService.start();
            response.content().writeBytes("OK".getBytes());
        });
        map.put("/restart", (request, response)->{
            tcpService.restart();
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
                        .put("address", server.host() + ":" + server.port())
                        .put("proxyService", context.getConnect().remoteAddress().toString()).map());
                return jsonObject;
            }).collect(Collectors.toList());
            response.content().writeBytes(JSON.toJSONString(collect).getBytes());
        });
        // 切换节点
        map.put("/changeNode", (request, response)->{
            int i = request.uri().indexOf("?");
            if(i < 0){
                response.content().writeBytes("OK".getBytes());
                return;
            }
            String[] split = request.uri().substring(i).split("=");
            Map<String, String> param = new HashMap<>();
            for(int j = 0; j < split.length; j += 2) {
                param.put(split[j], split[j + 1]);
            }
            ProxyFactory factory = proxyFactoryMap.get(param.get("node"));
            ProxyFactory handleProxy = localProxyMessageHandler.getFactory();
            if(handleProxy instanceof RuleLocalConnectServerFactory){
                RuleLocalConnectServerFactory rule = (RuleLocalConnectServerFactory) handleProxy;
                rule.setDefaultProxyFactory(factory);
            } else if(!(handleProxy instanceof DirectConnectFactory)){
                localProxyMessageHandler.setFactory(factory);
            }
            response.content().writeBytes("OK".getBytes());
        });
        return new NettyTcpService(port, new HttpService(map));
    }
}
