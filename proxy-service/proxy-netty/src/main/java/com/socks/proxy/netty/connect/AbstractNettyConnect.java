package com.socks.proxy.netty.connect;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.ProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/1
 **/
@Slf4j
public abstract class AbstractNettyConnect implements LocalProxyConnect{
    protected final ChannelHandlerContext context;

    protected ProxyConnect remoteChannel;


    public AbstractNettyConnect(ChannelHandlerContext context){
        this.context = context;
        context.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
                byte[] content = ByteBufUtil.getBytes(msg);
                if(log.isDebugEnabled()){
                    log.debug("receive system proxy byteBuf \n {}",
                            ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(content)));
                }
                ICipher cipher = ctx.channel().attr(AttrConstant.CIPHER_KEY).get();
                byte[] bytes = cipher.encode(content);
                log.debug("send byte to server size = {}", bytes.length);
                remoteChannel.write(bytes);
            }


            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
                log.error("error", cause);
                ctx.close();
            }
        });
    }


    @Override
    public String channelId(){
        return context.channel().id().asShortText();
    }


    @Override
    public void write(byte[] content){
        ICipher cipher = context.channel().attr(AttrConstant.CIPHER_KEY).get();
        log.debug("receive proxy service byte size = {}", content.length);
        byte[] bytes = cipher.decode(content);
        log.debug("send to system proxy byte size = {}", bytes.length);
        context.writeAndFlush(Unpooled.wrappedBuffer(bytes));
    }


    @Override
    public void close(){
        context.close();
    }


    @Override
    public void setRemoteChannel(RemoteProxyConnect channel){
        this.remoteChannel = channel;
    }


    @Override
    public void setCipher(ICipher iCipher){
        context.channel().attr(AttrConstant.CIPHER_KEY).set(iCipher);
    }
}
