package com.socks.proxy.netty.proxy;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * resolve http proxy implement
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpTunnelProxy extends AbstractProxy<FullHttpRequest>{

    public HttpTunnelProxy(ProxyMessageHandler handler, ExecutorService executorService){
        super(handler, executorService,false);
    }


    @Override
    protected TargetServer resolveRemoteServer(FullHttpRequest msg){
        return new HttpTargetServer(msg);
    }


    @Override
    protected void writeSuccess(ChannelHandlerContext context, FullHttpRequest msg, TargetServer target,
                                ProxyMessageHandler handler){

        if(Objects.equals(target.sourceProtocol(), Protocol.HTTP)){
            handler.handleLocalBinaryMessage(new DirectConnectChannel(context.channel()), ByteBufUtil.getBytes(convertHttpRequestToByteBuf(msg)));
            context.pipeline().remove(HttpServerCodec.class);
            context.pipeline().remove(HttpObjectAggregator.class);
        }else{
            HttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.buffer());
            context.writeAndFlush(response).addListener(future->{
                if(future.isSuccess()){
                    context.pipeline().remove(HttpServerCodec.class);
                    context.pipeline().remove(HttpObjectAggregator.class);
                }
            });
        }

    }


    @Override
    protected void writeFail(ChannelHandlerContext context, FullHttpRequest msg, TargetServer target){
        context.writeAndFlush(new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND));
    }

    private static ByteBuf convertHttpRequestToByteBuf(FullHttpRequest httpRequest) {
        String requestAsString = httpRequest.method().toString() + " " +
                httpRequest.uri() + " " +
                httpRequest.protocolVersion().text() + "\r\n" +
                headersToString(httpRequest.headers()) + "\r\n";
        ByteBuf byteBuf = Unpooled.copiedBuffer(requestAsString, io.netty.util.CharsetUtil.UTF_8);
        byte[] body = ByteBufUtil.getBytes(httpRequest.content().asByteBuf());
        byteBuf.writeBytes(body);
        return byteBuf;
    }

    private static String headersToString(HttpHeaders headers) {
        StringBuilder headerString = new StringBuilder();
        for (CharSequence name : headers.names()) {
            headerString.append(name).append(": ").append(headers.get(name)).append("\r\n");
        }
        return headerString.toString();
    }
}
