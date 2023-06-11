package com.socks.proxy.netty;

import com.socks.proxy.netty.server.ServerService;
import com.socks.proxy.protocol.TcpService;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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

    private ChannelHandler handler;


    @Override
    public TcpService builder(){
        assert handler != null;
        return new ServerService(port, handler);
    }
}
