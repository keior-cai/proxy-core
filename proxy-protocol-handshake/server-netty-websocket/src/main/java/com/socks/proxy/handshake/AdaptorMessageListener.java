package com.socks.proxy.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
public class AdaptorMessageListener implements MessageListener{
    @Override
    public void onConnect(ChannelHandlerContext context, HttpHeaders headers, String s, String selectedSubprotocol){

    }


    @Override
    public void onDisconnect(ChannelHandlerContext context){

    }


    @Override
    public void onText(ChannelHandlerContext context, String message){

    }


    @Override
    public void onBinary(ChannelHandlerContext context, byte[] content){

    }


    @Override
    public void onClose(ChannelHandlerContext context, int status, String reason){

    }


    @Override
    public void onCallbackError(ChannelHandlerContext context, Throwable cause){

    }


    @Override
    public void onError(ChannelHandlerContext context, Throwable cause){

    }


}
