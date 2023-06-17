package com.socks.proxy.protocol.enums;

import com.socks.proxy.protocol.command.ProxyCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    ;
    private final int code;


    public static ProxyCommand of(int command){
        for(LocalProxyCommand proxyCommand : LocalProxyCommand.values()) {
            if(proxyCommand.code == command){
                return proxyCommand;
            }
        }
        return null;
    }
}
