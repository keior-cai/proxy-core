package com.socks.proxy.netty.connect;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public class DefaultSocks5NettyConnect extends AbstractNettyConnect{

    public DefaultSocks5NettyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                     List<LocalConnectListener> listeners){
        super(ctx, dstServer, listeners);
    }


    @Override
    public void writeConnectSuccess(){
        Socks5AddressType socks5AddressType = context.channel().attr(AttrConstant.SOCKS5_ADDRESS_TYPE).get();
        log.debug("send to system proxy success address type = {}", socks5AddressType);
        context.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, socks5AddressType));
    }


    @Override
    public void writeConnectFail(){
        Socks5AddressType socks5AddressType = context.channel().attr(AttrConstant.SOCKS5_ADDRESS_TYPE).get();
        context.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, socks5AddressType));
    }

}
