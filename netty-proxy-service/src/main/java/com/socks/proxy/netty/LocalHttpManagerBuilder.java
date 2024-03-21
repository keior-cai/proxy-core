package com.socks.proxy.netty;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.netty.http.HttpHandle;
import com.socks.proxy.netty.http.HttpService;
import com.socks.proxy.netty.system.LinuxSetProxy;
import com.socks.proxy.netty.system.MacSetUpProxy;
import com.socks.proxy.netty.system.SetProxy;
import com.socks.proxy.netty.system.WindowsSetProxy;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private int tcpPort;

    /**
     * 管理器
     */
    private ConnectContextManager manager;

    private Map<String, ProxyFactory> proxyFactoryMap;


    @Override
    public TcpService builder(){
        String os = System.getProperty("os.name").toLowerCase();
        SetProxy setProxy;
        if(os.contains("mac")){
            setProxy = new MacSetUpProxy();
        } else if(os.contains("linux")){
            setProxy = new LinuxSetProxy();
        } else {
            setProxy = new WindowsSetProxy();
        }
        Map<String, HttpHandle> map = new HashMap<>();
        map.put("/stop", (request, response)->{
            try {
                tcpService.stop();
                response.content().writeBytes("OK".getBytes());
            } catch (LifecycleException e) {
                throw new RuntimeException(e);
            }
        });
        map.put("/start", (request, response)->{
            try {
                tcpService.start();
                response.content().writeBytes("OK".getBytes());
            } catch (LifecycleException e) {
                throw new RuntimeException(e);
            }
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
        // 切换节点
        map.put("/changeNode", (request, response)->{
            int i = request.uri().indexOf("?");
            if(i < 0){
                response.content().writeBytes("OK".getBytes());
                return;
            }
            String[] split = request.uri().substring(i + 1).split("=");
            Map<String, String> param = new HashMap<>();
            for(int j = 0; j < split.length; j += 2) {
                param.put(split[j], split[j + 1]);
            }
        });
        // connect
        map.put("/connects", (request, response)->{
            Set<ProxyContext> connects = manager.getTargetAllProxy();
            List<JSONObject> collect = connects.stream().map(item->{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("local", item.getLocal().remoteAddress());
                jsonObject.put("dst", String.format("%s:%s", item.getServer().host(), item.getServer().port()));
                jsonObject.put("protocol", item.getServer().sourceProtocol());
                return jsonObject;
            }).collect(Collectors.toList());
            response.content().writeBytes(JSON.toJSONString(collect).getBytes());
        });

        map.put("/system/startProxy", (request, response)->{
            setProxy.turnOnProxy(tcpPort);
        });

        map.put("/system/stopProxy", (request, response)->{
            setProxy.turnOffProxy();
        });

        return new NettyTcpService(port, new HttpService(map));
    }
}
