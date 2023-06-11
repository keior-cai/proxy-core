package com.socks.proxy.handshake.message.local;

import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户消息,ss-local发送消息
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@ToString
public class SendUserMessage extends ProxyMessage{

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String password;

    /**
     * 使用数据流式加密方式
     */
    private String method;

    /**
     * 客户端生成随机密码
     */
    private String random;


    public SendUserMessage(){
        super(LocalProxyCommand.SEND_USER_INFO.getCode());
    }


    public SendUserMessage(String method, String username, String password, String random){
        this();
        this.method = method;
        this.username = username;
        this.password = password;
        this.random = random;
    }
}
