package com.socks.proxy.protocol.enums;

import com.socks.proxy.protocol.command.ProxyCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
@Getter
@AllArgsConstructor
public enum ServerProxyCommand implements ProxyCommand{
    /**
     * 关闭连接
     */
    CLOSE(-1),

    /**
     * 连接目标服务超时
     */
    CONNECT_TIMEOUT(2),

    /**
     * 连接目标服务错误
     */
    CONNECT_ERROR(3),

    /**
     * 连接目标服务成功
     */
    CONNECT_SUCCESS(4),

    /**
     * 发送RSA公钥
     */
    SEND_PUBLIC_KEY(5),

    /**
     * 确认收到用户消息
     */
    ACK_USER_MESSAGE(6),
    ;

    private final int code;


    public static ProxyCommand of(int command){
        for(ServerProxyCommand proxyCommand : ServerProxyCommand.values()) {
            if(proxyCommand.code == command){
                return proxyCommand;
            }
        }
        return null;
    }
}
