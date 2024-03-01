package com.socks.proxy.handshake;

import com.socks.proxy.handshake.config.WebsocketConfig;
import com.socks.proxy.handshake.connect.WebsocketProxyChannel;
import com.socks.proxy.handshake.inbound.WebSocketBinaryInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketCloseInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketTextInboundHandle;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class WebsocketHandler extends ChannelInitializer<Channel>{

    private final WebSocketServerProtocolConfig protocolConfig;

    private final Supplier<ProxyMessageHandler> handlerFactory;


    public WebsocketHandler(Supplier<ProxyMessageHandler> handlerFactory){
        this(new WebsocketConfig(), handlerFactory);
    }


    public WebsocketHandler(WebsocketConfig config, Supplier<ProxyMessageHandler> handlerFactory){
        this(config.getPath(), config.getHandshakeTimeout(), config.getSubprotocols(), config.getMaxFramePayload(),
                handlerFactory);
    }


    public WebsocketHandler(String path, long handshakeTime, String subProtocol, int maxFramePayload,
                            Supplier<ProxyMessageHandler> handlerFactory){
        WebSocketServerProtocolConfig.Builder builder = WebSocketServerProtocolConfig.newBuilder()
                .handshakeTimeoutMillis(handshakeTime).websocketPath(path).subprotocols(subProtocol)
                .checkStartsWith(false).handleCloseFrames(true).decoderConfig(
                        WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayload)
                                .allowMaskMismatch(false).allowExtensions(false).build()).dropPongFrames(true)
                .allowExtensions(false);
        this.protocolConfig = builder.build();
        this.handlerFactory = handlerFactory;
    }


    @Override
    protected void initChannel(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();
        ProxyMessageHandler handler = handlerFactory.get();
        pipeline.addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(65535))
                .addLast(new WebSocketServerProtocolHandler(protocolConfig))
                .addLast(new WebsocketHandshakeCompleteEvent(handler))
                .addLast(new WebsocketTextInboundHandle(handler))
                .addLast(new WebSocketBinaryInboundHandle(handler))
                .addLast(new WebsocketCloseInboundHandle(handler))
                .addLast(new ChannelInboundHandlerAdapter(){

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx){
                        handler.handleLocalClose(new WebsocketProxyChannel(ctx.channel()), "客户端断开连接");
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
                        handler.handleLocalClose(new WebsocketProxyChannel(ctx.channel()), "服务端发送关闭连接命令");
                    }
                });
    }


    @Slf4j
    @AllArgsConstructor
    @ChannelHandler.Sharable
    public static class WebsocketHandshakeCompleteEvent
            extends SimpleUserEventChannelHandler<WebSocketServerProtocolHandler.HandshakeComplete>{

        private ProxyMessageHandler handler;


        @Override
        protected void eventReceived(ChannelHandlerContext ctx, WebSocketServerProtocolHandler.HandshakeComplete evt){
            Map<String, Object> headerValue = new HashMap<>();
            HttpHeaders headers = evt.requestHeaders();
            headers.entries().forEach(item->headerValue.put(item.getKey(), item.getValue()));
            handler.handlerShakeEvent(new WebsocketProxyChannel(ctx.channel()), headerValue);
        }
    }
}
