package com.socks.proxy.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2024/2/20
 **/

public class HttpService extends ChannelInitializer<Channel>{

    private final Map<String, HttpHandle> handleMap;


    public HttpService(Map<String, HttpHandle> handleMap){
        this.handleMap = handleMap;
    }


    @Override
    protected void initChannel(Channel channel){
        channel.pipeline().addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpServerExpectContinueHandler())
                .addLast(new SimpleChannelInboundHandler<FullHttpRequest>(){
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request){
                        String uri = request.uri();
                        if(uri != null){
                            int i = uri.indexOf("?");
                            if(i > 0){
                                uri  = uri.substring(0, i);
                            }
                        }
                        HttpHandle httpHandle = handleMap.get(uri);
                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(),
                                HttpResponseStatus.NOT_FOUND);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                        if(httpHandle == null){
                            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
                            channelHandlerContext.writeAndFlush(response);
                            return;
                        }
                        response.setStatus(HttpResponseStatus.OK);
                        try {
                            if(HttpMethod.GET.equals(request.method())){
                                httpHandle.get(request, response);
                            } else if(HttpMethod.POST.equals(request.method())){
                                httpHandle.post(request, response);
                            } else if(HttpMethod.DELETE.equals(request.method())){
                                httpHandle.delete(request, response);
                            }
                        } catch (Exception e) {
                            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                        }
                        int size = response.content().readableBytes();
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, size);
                        channelHandlerContext.writeAndFlush(response);
                    }
                });
    }
}
