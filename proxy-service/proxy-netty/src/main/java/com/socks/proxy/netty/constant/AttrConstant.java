package com.socks.proxy.netty.constant;

import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.RemoteProxyConnect;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.util.AttributeKey;

public interface AttrConstant{
    /**
     * 流式加密工具
     */
    AttributeKey<ICipher> CIPHER_KEY = AttributeKey.valueOf("cipher");

    /**
     * socks5 地址类型
     */
    AttributeKey<Socks5AddressType> SOCKS5_ADDRESS_TYPE = AttributeKey.valueOf("socks5AddressType");
    /**
     * 连接远程服务
     */
    AttributeKey<DstServer>         REMOTE_SERVER       = AttributeKey.valueOf("remoteServer");

    /**
     * 连接远程服务
     */
    AttributeKey<RemoteProxyConnect> TARGET_SERVICE = AttributeKey.valueOf("targetService");

}
