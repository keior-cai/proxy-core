package com.socks.proxy.netty.constant;

import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.util.AttributeKey;

public interface AttrConstant{
    /**
     * 流式加密工具
     */
    AttributeKey<ICipher> CIPHER_KEY = AttributeKey.valueOf("cipher");

    /**
     * 连接远程服务
     */
    AttributeKey<LocalMiddleService> TARGET_SERVICE = AttributeKey.valueOf("targetService");

    /**
     * 本地连接通道
     */
    AttributeKey<LocalConnect> LOCAL_CONNECT = AttributeKey.valueOf("localConnect");

    /**
     * socks5 地址类型
     */
    AttributeKey<Socks5AddressType> SOCKS5_ADDRESS_TYPE = AttributeKey.valueOf("addressType");
}
