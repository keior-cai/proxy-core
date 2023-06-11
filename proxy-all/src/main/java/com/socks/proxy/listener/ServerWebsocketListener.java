package com.socks.proxy.listener;

import com.socks.proxy.handshake.AdaptorMessageListener;
import com.socks.proxy.handshake.message.CloseMessage;
import com.socks.proxy.handshake.message.server.PublicKeyMessage;
import com.socks.proxy.netty.NettyLocalWebsocketRemoteConnect;
import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Slf4j
@AllArgsConstructor
public class ServerWebsocketListener extends AdaptorMessageListener{

    private final Map<Class<? extends ProxyMessage>, List<ServerHandshakeMessageHandler>> handlerMap;

    private final ProxyCodes<? super ProxyMessage> codes;

    private final String publicKey;


    @Override
    public void onConnect(ChannelHandlerContext context, HttpHeaders headers, String s, String selectedSubprotocol){
        try {
            String encode = codes.encodeObject(new PublicKeyMessage(publicKey));
            log.debug("send public key message = {}", encode);
            context.writeAndFlush(new TextWebSocketFrame(encode));
        } catch (Throwable cause) {
            onCallbackError(context, cause);
        }
    }


    @Override
    public void onText(ChannelHandlerContext context, String message){
        if(StringUtil.isNullOrEmpty(message)){
            log.error("socket message is empty now skip this message and return.");
            return;
        }
        log.debug("receive text message = {}", message);
        ProxyMessage proxyMessage = codes.decode(message);
        log.debug("receive text message = {}", proxyMessage);
        Class<? extends ProxyMessage> clazz = proxyMessage.getClass();
        List<ServerHandshakeMessageHandler> handlers = handlerMap.get(proxyMessage.getClass());
        if(handlers == null || handlers.isEmpty()){
            log.warn("proxy class {}, handler is empty.", clazz);
            return;
        }
        RemoteProxyConnect remoteProxyConnect = context.channel().attr(AttrConstant.TARGET_SERVICE).get();
        for(ServerHandshakeMessageHandler handler : handlers) {
            handler.handle(new NettyLocalWebsocketRemoteConnect(context.channel(), codes), proxyMessage,
                    remoteProxyConnect);
        }
    }


    @Override
    public void onBinary(ChannelHandlerContext context, byte[] content){
        log.debug("receive local byte size = {}", content.length);
        RemoteProxyConnect remoteProxyConnect = context.channel().attr(AttrConstant.TARGET_SERVICE).get();
        if(remoteProxyConnect != null){
            ICipher iCipher = context.channel().attr(AttrConstant.CIPHER_KEY).get();
            byte[] decode = iCipher.decode(content);
            if(log.isDebugEnabled()){
                log.debug("receive local binary\n {}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(decode)));
            }
            remoteProxyConnect.write(decode);
        } else {
            log.error("target service not connect");
        }
    }


    @Override
    public void onClose(ChannelHandlerContext context, int status, String reason){
        close(context);
    }


    @Override
    public void onError(ChannelHandlerContext context, Throwable cause){
        writeClose(context);
    }


    @Override
    public void onCallbackError(ChannelHandlerContext context, Throwable cause){
        log.error("on callback error message = {}", cause.getMessage());
        writeClose(context);
    }


    @Override
    public void onDisconnect(ChannelHandlerContext context){
        log.debug("channel close...");
        close(context);
    }


    private void writeClose(ChannelHandlerContext context){
        context.writeAndFlush(codes.encodeObject(new CloseMessage()));
    }


    private void close(ChannelHandlerContext context){
        RemoteProxyConnect remoteProxyConnect = context.channel().attr(AttrConstant.TARGET_SERVICE).get();
        if(remoteProxyConnect != null){
            remoteProxyConnect.close();
        }
    }
}
