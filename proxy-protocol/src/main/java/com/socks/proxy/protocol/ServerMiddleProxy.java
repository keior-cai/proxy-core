package com.socks.proxy.protocol;

/**
 * <p>
 * server中间连接定义，主要用于server与local连接进行通信，包括数据交换
 * </p>
 * local->server
 */
public interface ServerMiddleProxy extends ProxyConnect{

    /**
     * 向客户端写文本消息
     *
     * @param message 消息内容
     */
    void write(String message);


    /**
     * 目标服务连接
     */
    TargetConnect getTarget();


    /**
     * 设置目标连接
     *
     * @param target 目标服务器连接
     */
    void setTarget(TargetConnect target);


    void setCipher(ICipher defaultCipher);

}
