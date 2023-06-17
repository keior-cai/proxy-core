package com.socks.proxy.protocol;

/**
 * 目标连接
 *
 * @author: chuangjie
 * @date: 2023/6/17
 **/
public interface TargetConnect extends ProxyConnect{
    void connect() throws Exception;
}
