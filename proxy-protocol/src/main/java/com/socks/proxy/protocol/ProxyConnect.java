package com.socks.proxy.protocol;

/**
 * 定义代理连接对象
 */
public interface ProxyConnect{

    /**
     * 连接唯一Id
     *
     * @return 通道Id
     */
    String channelId();


    /**
     * 向连接远程服务写入数据
     *
     * @param content 数据内容，二进制内容
     */
    void write(byte[] content);


    /**
     * 关闭远程服务连接
     */
    void close();

}
