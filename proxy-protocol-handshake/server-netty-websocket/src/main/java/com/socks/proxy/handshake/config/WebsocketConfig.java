package com.socks.proxy.handshake.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: chuangjie
 * @date: 2023/5/17
 **/
@Getter
@Setter
public class WebsocketConfig{
    /**
     * websocket path
     */
    private String path = "/";

    /**
     * websocket 握手超时时间
     */
    private long handshakeTimeout = 3000L;

    /**
     * websocket 子协议
     */
    private String subprotocols = null;

    /**
     * websocket 最大消息体内容，字节
     */
    private int maxFramePayload = 65535;
}
