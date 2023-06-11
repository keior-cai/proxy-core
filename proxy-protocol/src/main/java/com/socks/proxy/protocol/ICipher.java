package com.socks.proxy.protocol;

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
