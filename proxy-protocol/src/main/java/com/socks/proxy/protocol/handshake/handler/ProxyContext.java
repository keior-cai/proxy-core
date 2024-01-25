package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ProxyConnect;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

/**
 * 代理上下文
 */
@Data
public class ProxyContext{

    /**
     * 连接
     */
    private ProxyConnect connect;

    /**
     * 流式数据加密工具
     */
    private AbstractCipher cipher;

    /**
     * 加密随机数
     */
    private String random;

    private TargetServer server;


    public void write(byte[] binary){
        connect.write(binary);
    }


    public void encodeWrite(byte[] binary){
        write(cipher.encodeBytes(binary));
    }


    public void decodeWrite(byte[] binary){
        write(cipher.decodeBytes(binary));
    }
}
