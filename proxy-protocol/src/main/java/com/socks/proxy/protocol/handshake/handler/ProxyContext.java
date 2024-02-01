package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.protocol.connect.ProxyConnect;
import lombok.Data;

import java.util.Optional;

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
     * 代理信息
     */
    private ProxyInfo proxyInfo = new ProxyInfo();


    public void write(byte[] binary){
        connect.write(binary);
    }


    public void encodeWrite(byte[] binary){
        Optional.ofNullable(proxyInfo).map(ProxyInfo::getCipher).ifPresent(item->write(item.encodeBytes(binary)));
    }


    public void decodeWrite(byte[] binary){
        Optional.ofNullable(proxyInfo).map(ProxyInfo::getCipher).ifPresent(item->write(item.decodeBytes(binary)));
    }
}
