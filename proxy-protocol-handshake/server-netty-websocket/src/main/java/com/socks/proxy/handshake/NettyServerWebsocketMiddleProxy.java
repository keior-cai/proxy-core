package com.socks.proxy.handshake;

import com.socks.proxy.handshake.constant.WebsocketAttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
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
        ICipher iCipher = context.channel().attr(WebsocketAttrConstant.CIPHER).get();
        byte[] encode = iCipher.encode(content);
        context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(encode)));
    }


    @Override
    public void close(){

    }


    @Override
    public void write(String message){
        context.writeAndFlush(new TextWebSocketFrame(codes.encodeStr(message)));
    }


    @Override
    public TargetConnect getTarget(){
        return null;
    }


    @Override
    public void setTarget(TargetConnect target){

    }


    @Override
    public void setCipher(ICipher cipher){
        context.channel().attr(WebsocketAttrConstant.CIPHER).set(cipher);
    }
}
