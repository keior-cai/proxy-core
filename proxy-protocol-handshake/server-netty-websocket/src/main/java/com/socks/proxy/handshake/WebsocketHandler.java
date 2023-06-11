package com.socks.proxy.handshake;

import com.socks.proxy.handshake.config.WebsocketConfig;
import com.socks.proxy.handshake.inbound.WebSocketBinaryInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketCloseInboundHandle;
import com.socks.proxy.handshake.inbound.WebsocketTextInboundHandle;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class WebsocketHandler extends ChannelInitializer<Channel>{

    private final WebSocketServerProtocolConfig protocolConfig;

    private final MessageListener messageListener;


    public WebsocketHandler(){
        this(new AdaptorMessageListener());
    }


    public WebsocketHandler(MessageListener listener){
        this(new WebsocketConfig(), listener);
    }


    public WebsocketHandler(WebsocketConfig config, MessageListener listener){
        this(config.getPath(), config.getHandshakeTimeout(), config.getSubprotocols(), config.getMaxFramePayload(),
                listener);
    }


    public WebsocketHandler(String path, long handshakeTime, String subProtocol, int maxFramePayload,
                            MessageListener listener){
        WebSocketServerProtocolConfig.Builder builder = WebSocketServerProtocolConfig.newBuilder()
                .handshakeTimeoutMillis(handshakeTime).websocketPath(path).subprotocols(subProtocol)
                .checkStartsWith(false).handleCloseFrames(true).decoderConfig(
                        WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayload)
                                .allowMaskMismatch(false).allowExtensions(false).build()).dropPongFrames(true)
                .allowExtensions(false);
        this.protocolConfig = builder.build();
        this.messageListener = listener;
    }


    @Override
    protected void initChannel(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(65535))
                .addLast(new WebSocketServerProtocolHandler(protocolConfig))
                .addLast(new WebsocketHandshakeCompleteEvent(messageListener))
                .addLast(new WebsocketTextInboundHandle(messageListener))
                .addLast(new WebSocketBinaryInboundHandle(messageListener))
                .addLast(new WebsocketCloseInboundHandle(messageListener));
    }


    @Slf4j
    @AllArgsConstructor
    @ChannelHandler.Sharable
    public static class WebsocketHandshakeCompleteEvent
            extends SimpleUserEventChannelHandler<WebSocketServerProtocolHandler.HandshakeComplete>{
        private final MessageListener messageListener;


        @Override
        protected void eventReceived(ChannelHandlerContext ctx, WebSocketServerProtocolHandler.HandshakeComplete evt){
            messageListener.onConnect(ctx, evt.requestHeaders(), evt.requestUri(), evt.selectedSubprotocol());
        }
    }
}
