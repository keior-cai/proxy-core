package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * 定义客户端（系统代理）与local握手协议处理
 */
public interface HandshakeProtocolHandler{

    /**
     * @param is 握手协议内容输入流
     * @return 代理协议
     */
    Protocol handler(InputStream is) throws IOException;
}
