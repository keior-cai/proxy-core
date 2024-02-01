package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.protocol.TargetServer;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

/**
 * @author: chuangjie
 * @date: 2024/2/1
 **/
@Data
public class ProxyInfo{

    /**
     * 流式数据加密工具
     */
    private AbstractCipher cipher;

    /**
     * 加密随机数
     */
    private String random;

    private CountDownLatch count;

    private TargetServer server;
}
