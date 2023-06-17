package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;

public interface LocalConnectListener{

    /**
     * 创建连接是回调
     *
     * @param local 本地连接
     * @param remoteServer 远程连接地址
     * @param remote 远程连接对象，还没连接
     */
    void onCreate(LocalConnect local, TargetServer remoteServer, LocalMiddleProxy remote);


    /**
     * 连接成功之后回调该方法
     *
     * @param local 本地链接
     * @param remoteServer 远程连接地址
     * @param remote 远程连接对象，已经完成连接
     */
    void onConnect(LocalConnect local, TargetServer remoteServer, LocalMiddleProxy remote);


    /**
     * 读取数据之后出列业务逻辑发生异常回调
     *
     * @param local 本地连接
     * @param remote 远程连接
     * @param e 异常
     */
    void onCallbackError(LocalConnect local, LocalMiddleProxy remote, Throwable e);


    /**
     * 本地连接断开连接回调该方法
     *
     * @param remote 远程连接
     */
    void onLocalClose(LocalMiddleProxy remote);


    /**
     * 本地连接处理异常：例如连接断开，数据包解析错误等。。。
     *
     * @param connect 远程链接
     * @param cause 异常
     */
    void onError(LocalMiddleProxy connect, Throwable cause);


    /**
     * 本地代理服务local向远程服务server-service发送正常数据流量
     * <p>这里也称为上行流量</p>
     *
     * @param local 本地连接
     * @param message 发送原始数据
     * @param remote 远程连接
     */
    void onSendBinary(LocalConnect local, byte[] message, LocalMiddleProxy remote);


    /**
     * 本地代理服务接受远程服务server-service发送过的正常数据流量
     * <p>这里称为下行流量</p>
     *
     * @param local 本地远程连接
     * @param message 接收原始数据
     * @param remote 远程连接
     */
    void onBinary(LocalConnect local, byte[] message, LocalMiddleProxy remote);
}
