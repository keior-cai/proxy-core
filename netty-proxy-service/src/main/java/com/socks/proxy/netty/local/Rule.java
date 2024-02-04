package com.socks.proxy.netty.local;

import lombok.Data;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2024/2/2
 **/
@Data
public class Rule{

    /**
     * 域名/ ip
     */
    private String domain;

    /**
     * 责任代理名称
     */
    private List<String> proxyNames;
}
