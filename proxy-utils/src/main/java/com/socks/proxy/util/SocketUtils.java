package com.socks.proxy.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author: chuangjie
 * @date: 2024/2/4
 **/
public class SocketUtils{


    private SocketUtils(){}


    public static long ping(String host, int port){
        return ping(host, port, 3000);
    }

    public static long ping(String host, int port, int timeout){
        return getPingDelay(host, port, timeout);
    }

    private static long getPingDelay(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);

            long startTime = System.currentTimeMillis();
            socket.connect(socketAddress, timeout); // 设置连接超时为5秒

            long endTime = System.currentTimeMillis();
            long delay = endTime - startTime;
            socket.close();
            return delay;
        } catch (IOException e) {
            // 处理连接失败或超时的异常
            return -1;
        }
    }


    public static void main(String[] args){
        long ping = SocketUtils.ping("chuangjie.icu", 8041);
        System.out.println(ping);
    }
}
