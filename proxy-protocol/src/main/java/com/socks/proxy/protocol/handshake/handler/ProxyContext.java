package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ProxyConnect;
import lombok.Data;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * 代理上下文
 */
@Data
public class ProxyContext{

    /**
     * 连接
     */
    private ProxyConnect local;


    private ProxyConnect dst;


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



    private void write(byte[] binary, ProxyConnect connect){
        connect.write(binary);
    }


    public void localEncodeWrite(byte[] binary){
        write(cipher.encodeBytes(binary), local);
    }


    public void dstWrite(byte[] binary){
        write(binary, dst);
    }

    public void localWrite(byte[] binary){
        write(binary, local);
    }

    public void localDecodeWrite(byte[] binary){
        write(cipher.decodeBytes(binary), local);
    }


    public void dstEncodeWrite(byte[] binary){
        write(cipher.encodeBytes(binary), dst);
    }

    public void dstDecodeWrite(byte[] binary){
        write(cipher.decodeBytes(binary), dst);
    }
}
