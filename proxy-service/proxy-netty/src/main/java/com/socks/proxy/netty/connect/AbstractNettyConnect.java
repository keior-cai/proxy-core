package com.socks.proxy.netty.connect;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/6/1
 **/
@Slf4j
public abstract class AbstractNettyConnect implements LocalConnect{

    protected final ChannelHandlerContext context;

    private final TargetServer server;

    private final List<LocalConnectListener> listeners;


    public AbstractNettyConnect(ChannelHandlerContext context, TargetServer dstServer,
                                List<LocalConnectListener> listeners){
        this.context = context;
        this.server = dstServer;
        this.listeners = listeners;
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
                LocalMiddleProxy remoteProxyConnect = context.channel().attr(AttrConstant.TARGET_SERVICE).get();
                if(remoteProxyConnect != null){
                    remoteProxyConnect.write(bytes);
                } else {
                    log.warn("remote proxy channel is empty.");
                }
            }


            @Override
            public void channelInactive(ChannelHandlerContext ctx){
                LocalMiddleProxy remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
                for(LocalConnectListener listener : AbstractNettyConnect.this.listeners) {
                    listener.onLocalClose(remoteProxyConnect);
                }
            }


            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
                LocalMiddleProxy remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
                for(LocalConnectListener listener : AbstractNettyConnect.this.listeners) {
                    listener.onLocalClose(remoteProxyConnect);
                }
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
    public void setRemoteChannel(LocalMiddleProxy channel){
        context.channel().attr(AttrConstant.TARGET_SERVICE).set(channel);
    }


    @Override
    public void setCipher(ICipher iCipher){
        context.channel().attr(AttrConstant.CIPHER_KEY).set(iCipher);
    }


    @Override
    public TargetServer getDstServer(){
        return server;
    }
}
