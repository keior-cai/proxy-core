package com.socks.proxy.netty.connect;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
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
                LocalMiddleService remoteProxyConnect = context.channel().attr(AttrConstant.TARGET_SERVICE).get();
                for(LocalConnectListener listener : listeners) {
                    listener.onSendBinary(AbstractNettyConnect.this, content, remoteProxyConnect);
                }
                if(log.isDebugEnabled()){
                    log.debug("pre send to proxy service data\n{}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(content)));
                }
                ICipher iCipher = ctx.channel().attr(AttrConstant.CIPHER_KEY).get();
                byte[] encode = iCipher.encode(content);
                remoteProxyConnect.write(encode);
            }


            @Override
            public void channelInactive(ChannelHandlerContext ctx){
                LocalMiddleService remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
                for(LocalConnectListener listener : AbstractNettyConnect.this.listeners) {
                    listener.onLocalClose(AbstractNettyConnect.this, remoteProxyConnect);
                }
            }


            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
                LocalMiddleService remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
                for(LocalConnectListener listener : AbstractNettyConnect.this.listeners) {
                    listener.onLocalClose(AbstractNettyConnect.this, remoteProxyConnect);
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
        context.writeAndFlush(Unpooled.wrappedBuffer(content));
    }


    @Override
    public void close(){
        context.close();
    }


    @Override
    public void setRemoteChannel(LocalMiddleService channel){
        context.channel().attr(AttrConstant.TARGET_SERVICE).set(channel);
    }


    @Override
    public void setCipher(ICipher iCipher){
        context.channel().attr(AttrConstant.CIPHER_KEY).set(iCipher);
    }


    @Override
    public ICipher getCipher(){
        return context.channel().attr(AttrConstant.CIPHER_KEY).get();
    }


    @Override
    public TargetServer getDstServer(){
        return server;
    }
}
