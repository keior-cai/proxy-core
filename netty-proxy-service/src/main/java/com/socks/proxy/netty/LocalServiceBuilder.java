package com.socks.proxy.netty;

import com.socks.proxy.netty.proxy.ProtocolChannelHandler;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

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
     * 代理协议处理器
     */
    private ProtocolChannelHandler protocolHandle;

    /**
     * 管理器
     */
    private ConnectContextManager manager;


    @Override
    public TcpService builder(){
        return new NettyTcpService(port, protocolHandle);

    }

}
