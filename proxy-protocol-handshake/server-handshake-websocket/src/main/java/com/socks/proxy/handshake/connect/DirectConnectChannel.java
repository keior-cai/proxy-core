package com.socks.proxy.handshake.connect;

import com.socks.proxy.handshake.constant.AttrConstant;
import com.socks.proxy.protocol.connect.ProxyConnect;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 直接连接
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class DirectConnectChannel implements ProxyConnect{

    private final Channel context;


    @Override
    public String channelId(){
        return context.id().asLongText();
    }


    @Override
    public void write(byte[] binary){
        context.writeAndFlush(Unpooled.wrappedBuffer(binary));
    }


    @Override
    public void write(String message){
        context.writeAndFlush(Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)));
    }


    @Override
    public void close(){
        context.close();
    }


    @Override
    public boolean equals(Object obj){
        return Objects.equals(channelId(), obj);
    }


    @Override
    public int hashCode(){
        return channelId().hashCode();
    }
}
