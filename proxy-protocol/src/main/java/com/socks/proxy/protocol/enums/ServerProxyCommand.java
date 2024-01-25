package com.socks.proxy.protocol.enums;

import com.socks.proxy.protocol.command.ProxyCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
     * 发送RSA公钥
     */
    SEND_PUBLIC_KEY(5),

    /**
     * 确认收到用户消息
     */
    ACK_USER_MESSAGE(6),

    /**
     * 连接目标服务成功
     */
    CONNECT_SUCCESS(7),

    /**
     * 未知消息类型
     */
    UNKNOWN(-8888);

    private final int code;

    private static final Map<Integer, ServerProxyCommand> map = new HashMap<>();

    static{
        for(ServerProxyCommand proxyCommand : ServerProxyCommand.values()) {
            map.put(proxyCommand.getCode(), proxyCommand);
        }
    }

    public static ServerProxyCommand of(int command){
        return map.getOrDefault(command, UNKNOWN);
    }


    public static boolean isServiceCommand(int command){
        return map.containsKey(command);
    }
}
