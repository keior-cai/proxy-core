package com.socks.proxy.protocol.handshake.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ConnectUserInfo{

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 随机密码长度
     */
    private int passwordLen;

    /**
     * 流式数据加密方式
     */
    private String method;


    public ConnectUserInfo(String username, String password){
        this(username, password, 10, "aes-256-cfb");
    }
}
