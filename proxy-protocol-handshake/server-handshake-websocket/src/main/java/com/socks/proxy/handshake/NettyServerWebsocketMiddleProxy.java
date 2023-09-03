package com.socks.proxy.handshake;

import com.socks.proxy.handshake.constant.WebsocketAttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
@Slf4j
public class NettyServerWebsocketMiddleProxy implements ServerMiddleProxy{

    private final ChannelHandlerContext context;

    private final ProxyCodes<? extends ProxyMessage> codes;


    public NettyServerWebsocketMiddleProxy(ChannelHandlerContext context, ProxyCodes<? extends ProxyMessage> codes){
        this.context = context;
        this.codes = codes;
    }


    @Override
    public String channelId(){
        return context.channel().id().asShortText();
    }


    @Override
    public void write(byte[] content){
        if(log.isDebugEnabled()){
            log.debug("target server send buffer = {}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(content)));
        }
        ICipher iCipher = context.channel().attr(WebsocketAttrConstant.CIPHER).get();
        byte[] encode = iCipher.encode(content);
        if(log.isDebugEnabled()){
            log.debug("target server send encode buffer = {}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(encode)));
        }
        context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(encode)));
    }


    @Override
    public void close(){
        context.close();
    }


    @Override
    public void write(String message){
        String s = codes.encodeStr(message);
        if(log.isDebugEnabled()){
            log.debug("send local message = {}", s);
        }
        context.writeAndFlush(new TextWebSocketFrame(s));
    }


    @Override
    public TargetConnect getTarget(){
        return context.channel().attr(WebsocketAttrConstant.TARGET).get();
    }


    @Override
    public void setTarget(TargetConnect target){
        context.channel().attr(WebsocketAttrConstant.TARGET).set(target);
    }


    @Override
    public void setCipher(ICipher cipher){
        context.channel().attr(WebsocketAttrConstant.CIPHER).set(cipher);
    }

}
