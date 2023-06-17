package com.socks.proxy.handshake;

import com.socks.proxy.handshake.config.WebsocketConfig;
import com.socks.proxy.handshake.inbound.WebSocketBinaryInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketCloseInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketTextInboundHandle;
import com.socks.proxy.protocol.listener.ServerMiddleMessageListener;
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

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class WebsocketHandler extends ChannelInitializer<Channel>{

    private final WebSocketServerProtocolConfig protocolConfig;

    private final ServerMiddleMessageListener messageListener;

    private final NettyServerMiddleProxyFactory factory;


    public WebsocketHandler(ServerMiddleMessageListener listener, NettyServerMiddleProxyFactory factory){
        this(new WebsocketConfig(), listener, factory);
    }


    public WebsocketHandler(WebsocketConfig config, ServerMiddleMessageListener listener,
                            NettyServerMiddleProxyFactory factory){
        this(config.getPath(), config.getHandshakeTimeout(), config.getSubprotocols(), config.getMaxFramePayload(),
                listener, factory);
    }


    public WebsocketHandler(String path, long handshakeTime, String subProtocol, int maxFramePayload,
                            ServerMiddleMessageListener listener, NettyServerMiddleProxyFactory factory){
        WebSocketServerProtocolConfig.Builder builder = WebSocketServerProtocolConfig.newBuilder()
                .handshakeTimeoutMillis(handshakeTime).websocketPath(path).subprotocols(subProtocol)
                .checkStartsWith(false).handleCloseFrames(true).decoderConfig(
                        WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayload)
                                .allowMaskMismatch(false).allowExtensions(false).build()).dropPongFrames(true)
                .allowExtensions(false);
        this.protocolConfig = builder.build();
        this.messageListener = listener;
        this.factory = factory;
    }


    @Override
    protected void initChannel(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(65535))
                .addLast(new WebSocketServerProtocolHandler(protocolConfig))
                .addLast(new WebsocketHandshakeCompleteEvent(messageListener, factory))
                .addLast(new WebsocketTextInboundHandle(messageListener, factory))
                .addLast(new WebSocketBinaryInboundHandle(messageListener, factory))
                .addLast(new WebsocketCloseInboundHandle(messageListener, factory));
    }


    @Slf4j
    @AllArgsConstructor
    @ChannelHandler.Sharable
    public static class WebsocketHandshakeCompleteEvent
            extends SimpleUserEventChannelHandler<WebSocketServerProtocolHandler.HandshakeComplete>{

        private final ServerMiddleMessageListener messageListener;

        private final NettyServerMiddleProxyFactory factory;


        @Override
        protected void eventReceived(ChannelHandlerContext ctx, WebSocketServerProtocolHandler.HandshakeComplete evt){
            Map<String, String> headerValue = new HashMap<>();
            HttpHeaders headers = evt.requestHeaders();
            headers.entries().forEach(item->headerValue.put(item.getKey(), item.getValue()));
            messageListener.onConnect(factory.getProxy(ctx), headerValue, evt.requestUri(), evt.selectedSubprotocol());
        }
    }
}
