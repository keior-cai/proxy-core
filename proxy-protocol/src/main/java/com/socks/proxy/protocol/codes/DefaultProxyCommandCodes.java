package com.socks.proxy.protocol.codes;

import com.socks.proxy.util.AESUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 代理消息命令编码
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
@AllArgsConstructor
public class DefaultProxyCommandCodes implements ProxyCodes{

    @Override
    public String decodeStr(String str){
        return AESUtil.decryptByDefaultKey(str);
    }


    @Override
    public String encodeStr(String message){
        return AESUtil.encryptByDefaultKey(message);
    }
}
