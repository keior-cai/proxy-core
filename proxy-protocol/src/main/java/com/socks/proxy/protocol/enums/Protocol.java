package com.socks.proxy.protocol.enums;

/**
 * socket 代理类型
 */
public enum Protocol{
    /**
     * 隧道代理，http类型
     */
    HTTP,
    /**
     * 隧道代理https类型
     */
    HTTPS,
    /**
     * socks5 协议代理
     */
    SOCKS5,
    /**
     * socks4 协议代理
     */
    SOCKS4,
    /**
     * 复合协议
     */
    COMPLEX,

    /**
     * 未知协议
     */
    UNKNOWN

}
