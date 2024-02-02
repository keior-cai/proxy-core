package com.socks.proxy.netty.local;

import com.socks.proxy.netty.enums.ProxyModel;
import com.socks.proxy.protocol.enums.Protocol;
import lombok.Data;

import java.util.List;

/**
 * local 配置项
 *
 * @author: chuangjie
 * @date: 2024/2/2
 **/
@Data
public class LocalProperties{

    /**
     * 代理配置
     */
    private List<ProxyProperties> proxies;


    /**
     * 代理端口
     */
    private int port = 1081;

    /**
     * 代理方式
     */
    private Protocol protocol = Protocol.COMPLEX;

    /**
     * 规则配置项
     */
    private List<Rule> rules;

    /**
     * 非对称加密公钥
     */
    private String publicKey;

    /**
     * 非对称加密私钥
     */
    private String privateKey;

    private String name;

}
