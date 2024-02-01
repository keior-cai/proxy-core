package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.protocol.connect.ProxyConnect;

import java.util.Map;

/**
 * <pre>
 *  网络代理分成两部分 本地客户端和 远程服务端
 *  本地客户端（ss-local）代理本地程序发起网络请求
 *  1、ss-local 本地客户端代理服务；主要作用是代理本地的网络请求；可通过对请求进行拦截，篡改本地程序发起的请求数据；
 *      主要的代理协议例如：socks5，http等详细的代理协议参考{@link com.socks.proxy.protocol.enums.Protocol}
 *      本地客户端收集到本地的网络请求连接之后将和远程服务端进行握手；握手协议参考
 *      {@link ProxyMessageHandler#handleLocalTextMessage(ProxyConnect, String)}
 *      {@link ProxyMessageHandler#handlerShakeEvent(ProxyConnect, Map)}
 *  2、本地客户端和服务端握手成功之后，本地客户端在将真正的请求数据发送给远程服务端;
 *      这里发送的数据会对发送的数据进行自定义加密
 *      详细参考{@link AbstractLocalProxyMessageHandler}
 *  3、
 *  本地服务会先跟本地服务进行连接；
 *  代理消息协议处理器定义
 * </pre>
 */
public interface ProxyMessageHandler{

    /**
     * <pre>
     *     处理连接握手事件信息
     * </pre>
     */
    void handlerShakeEvent(ProxyConnect local, Map<String, Object> context);


    /**
     * <pre>
     *     处理客户端发送文本请求数据
     *     ss-server 通过文本协议对ss-local进行权限的校验；包括数据加密信息，数据加公钥，签名等等
     * </pre>
     *
     * @param local 客户端连接
     * @param text 文本请求数据
     */
    void handleLocalTextMessage(ProxyConnect local, String text);


    /**
     * 处理客户端请求数据
     *
     * @param local 客户端连接
     * @param binary 客户端请求数据
     */
    void handleLocalBinaryMessage(ProxyConnect local, byte[] binary);


    /**
     * 处理目标服务发送的响应数据
     *
     * @param target 目标服务
     * @param binary 目标服务响应数据
     */
    void handleTargetBinaryMessage(ProxyConnect target, byte[] binary);


    /**
     * 目标服务断开连接
     */
    void handleTargetClose(ProxyConnect target, Exception e);


    /**
     * 处理客户端断开连接
     */
    void handleLocalClose(ProxyConnect local, Exception e);
}
