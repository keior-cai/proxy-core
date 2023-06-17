package com.socks.proxy.netty;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Slf4j
public class NettyServerWebsocketConnect implements ServerMiddleProxy{

    private final Channel channel;

    private final ProxyCommandEncode<? super ProxyMessage> encode;

    private TargetConnect target;


    public NettyServerWebsocketConnect(Channel channel, ProxyCommandEncode<? super ProxyMessage> encode){
        this.channel = channel;
        this.encode = encode;
    }


    @Override
    public String channelId(){
        return channel.id().asShortText();
    }


    @Override
    public void write(byte[] content){
        log.debug("receive target service byte size = {}", content.length);
        if(log.isDebugEnabled()){
            log.debug("receive byteBuf\n {}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(content)));
        }
        ICipher iCipher = channel.attr(AttrConstant.CIPHER_KEY).get();
        byte[] encodeData = iCipher.encode(content);
        if(log.isDebugEnabled()){
            log.debug("encode byteBuf send to local\n {}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(content)));
        }
        log.debug("send to websocket local byte size = {}", encodeData.length);
        channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(encodeData)));
    }


    @Override
    public void close(){
        channel.close();
    }


    @Override
    public void setCipher(ICipher iCipher){
        channel.attr(AttrConstant.CIPHER_KEY).set(iCipher);
    }


    @Override
    public void write(String message){
        log.debug("send to local message = {}", message);
        channel.writeAndFlush(new TextWebSocketFrame(encode.encodeStr(message)));
    }


    @Override
    public TargetConnect getTarget(){
        return target;
    }


    @Override
    public void setTarget(TargetConnect target){
        this.target = target;
    }

}
