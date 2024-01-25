package com.socks.proxy.protocol.enums;

import com.socks.proxy.protocol.command.ProxyCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum LocalProxyCommand implements ProxyCommand{

    /**
     * 关闭连接
     */
    CLOSE(-1),
    /**
     * 发送随机密码
     */
    SEND_RANDOM_KEY(1),
    /**
     * 发送目标服务地址和端口
     */
    SEND_DST_ADDR(2),
    /**
     * 发送用户信息
     */
    SEND_USER_INFO(3),
    /**
     * 重新连接
     */
    SEND_RECONNECT(4),

    /**
     * 未知代理消息
     */
    UNKNOWN(-999),

    ;
    private final int code;


    private static final Map<Integer, LocalProxyCommand> map = new HashMap<>();

    static {
        for(LocalProxyCommand proxyCommand : LocalProxyCommand.values()) {
            map.put(proxyCommand.getCode(), proxyCommand);
        }
    }


    public static LocalProxyCommand of(int command){

        return map.getOrDefault(command, UNKNOWN);
    }

    public static boolean isLocalCommand(int command){
        return map.containsKey(command);
    }

}
