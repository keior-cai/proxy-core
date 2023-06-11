package com.socks.proxy.netty.connect;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.DstServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public class DefaultHttpNettyConnect extends AbstractNettyConnect{

    private static final String SUCCESS = "HTTP/1.1 200 Connection Established\r\n\r\n";


    public DefaultHttpNettyConnect(ChannelHandlerContext context){
        super(context);
    }


    @Override
    public void writeConnectSuccess(){
        log.debug("write to system proxy http response = {}", SUCCESS);
        context.writeAndFlush(Unpooled.wrappedBuffer(SUCCESS.getBytes()));
    }


    @Override
    public void writeConnectFail(){
        context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
    }


    @Override
    public DstServer getDstServer(){
        return context.channel().attr(AttrConstant.REMOTE_SERVER).get();
    }
}
