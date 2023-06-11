package com.socks.proxy.handshake.channel;

import com.socks.proxy.handshake.constant.WebsocketAttrConstant;
import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.util.AttributeKey;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
public class WebsocketLocalChannel implements LocalProxyConnect{

    private final ChannelHandlerContext context;

    private static final AttributeKey<ICipher> CIPHER = AttributeKey.valueOf("cipher");


    public WebsocketLocalChannel(ChannelHandlerContext context){
        this.context = context;
    }


    @Override
    public void writeConnectSuccess(){
    }


    @Override
    public void writeConnectFail(){
        context.writeAndFlush(new CloseWebSocketFrame());
    }


    @Override
    public void setRemoteChannel(RemoteProxyConnect channel){

    }


    @Override
    public void setCipher(ICipher iCipher){
        context.channel().attr(CIPHER).set(iCipher);
    }


    @Override
    public DstServer getDstServer(){
        return context.channel().attr(WebsocketAttrConstant.DST_SERVER).get();
    }


    @Override
    public String channelId(){
        ChannelId id = context.channel().id();
        return id.asShortText();
    }


    @Override
    public void write(byte[] content){

        ICipher iCipher = context.channel().attr(CIPHER).get();
        context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(iCipher.encode(content))));
    }


    @Override
    public void connect(){
        throw new RuntimeException();
    }


    @Override
    public void close(){
        context.close();
    }
}
