package com.socks.proxy.netty.constant;

import com.socks.proxy.protocol.*;
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
    AttributeKey<TargetServer>      REMOTE_SERVER       = AttributeKey.valueOf("remoteServer");

    /**
     * 中间链接，local-server
     */
    AttributeKey<ServerMiddleProxy> MIDDLE_PROXY = AttributeKey.valueOf("serverService");

    /**
     * 目标地址连接
     */
    AttributeKey<TargetConnect> TARGET_SERVER = AttributeKey.valueOf("targetServer");

    /**
     * 连接远程服务
     */
    AttributeKey<LocalMiddleProxy> TARGET_SERVICE = AttributeKey.valueOf("targetService");

}
