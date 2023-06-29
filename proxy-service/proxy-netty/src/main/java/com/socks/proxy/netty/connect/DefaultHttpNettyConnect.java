package com.socks.proxy.netty.connect;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * local connect proxy server use http connect of netty
 * </p>
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public class DefaultHttpNettyConnect extends AbstractNettyConnect{

    public DefaultHttpNettyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                   List<LocalConnectListener> listeners){
        super(ctx, dstServer, listeners);
    }


    @Override
    public void writeConnectSuccess(){
        //        log.debug("write to system proxy http response = {}", SUCCESS);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        context.writeAndFlush(response);
        context.pipeline().remove(HttpServerCodec.class);
    }


    @Override
    public void writeConnectFail(){
        context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
    }

}
