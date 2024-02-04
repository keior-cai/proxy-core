package com.socks.proxy.netty.local;

import com.socks.proxy.netty.enums.HandshakeProtocol;
import com.socks.proxy.util.SocketUtils;
import lombok.Data;

import java.net.URI;
import java.util.Map;

/**
 * 代理配置
 *
 * @author: chuangjie
 * @date: 2024/2/2
 **/
@Data
public class ProxyProperties{

    /**
     * 服务地址
     */
    private URI addr;

    /**
     * 握手传输协议
     */
    private HandshakeProtocol type;

    /**
     * 配置项
     */
    private Map<String, Object> option;

    /**
     * 配置项名称
     */
    private String name;


    /**
     * ping 延迟
     */
    public long pingDelay(){
        return SocketUtils.ping(addr.getHost(), addr.getPort());
    }
}
