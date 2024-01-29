package com.socks.proxy.protocol.codes;

/**
 * 请求向目的服务发送数据和目的服务向本地机器发送数据加解密处理定义
 * <pre>
 * {@link DefaultCipher}
 * {@link com.socks.proxy.cipher.AbstractCipher}
 * {@link com.socks.proxy.cipher.CipherProvider}
 * {@link com.socks.proxy.cipher.LocalStreamCipher}
 * </pre>
 */
public interface ICipher{

    /**
     * 流式加密定义
     *
     * @param bytes 原始数据流
     * @return 加密后数据流
     */
    byte[] encode(byte[] bytes);


    /**
     * 流式解密定义
     *
     * @param bytes 原始数据流
     * @return 解密后数据流
     */
    byte[] decode(byte[] bytes);
}
