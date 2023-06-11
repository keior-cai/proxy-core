package com.socks.proxy.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;

public interface MessageListener{

    void onConnect(ChannelHandlerContext context, HttpHeaders headers, String s, String selectedSubprotocol);


    void onDisconnect(ChannelHandlerContext context);


    void onText(ChannelHandlerContext context, String message);


    void onBinary(ChannelHandlerContext context, byte[] content);


    void onClose(ChannelHandlerContext context, int status, String reason);


    void onCallbackError(ChannelHandlerContext context, Throwable cause);


    void onError(ChannelHandlerContext context, Throwable cause);
}
