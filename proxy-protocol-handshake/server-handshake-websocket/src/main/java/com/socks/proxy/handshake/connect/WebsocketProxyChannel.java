package com.socks.proxy.handshake.connect;

import com.socks.proxy.protocol.connect.ProxyConnect;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * channel context 代理对象
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@AllArgsConstructor
public class WebsocketProxyChannel implements ProxyConnect{

    private Channel context;


    @Override
    public String channelId(){
        return context.id().asLongText();
    }


    @Override
    public void write(byte[] binary){
        context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
    }


    @Override
    public void write(String message){
        context.writeAndFlush(new TextWebSocketFrame(message));
    }


    @Override
    public void close(){
        // ???
        context.writeAndFlush(new CloseWebSocketFrame());
    }


    @Override
    public int hashCode(){
        return channelId().hashCode();
    }


    @Override
    public boolean equals(Object obj){
        return Objects.equals(channelId(), obj);
    }
}
