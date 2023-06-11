package com.socks.proxy.codes.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxt.codes.ProxyCodes;
import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.FieldNameUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 代理消息命令编码
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
@AllArgsConstructor
public class DefaultProxyCommandCodes<T> implements ProxyCodes<T>{

    private final Map<Integer, Class<? extends ProxyMessage>> commandMap;


    @Override
    @SuppressWarnings("unchecked")
    public T decode(String str){
        String content = AESUtil.decryptByDefaultKey(str);
        JSONObject object = JSON.parseObject(content);
        if(object == null){
            return null;
        }
        String commandField = FieldNameUtils.getFieldName(ProxyMessage::getCommand);
        Integer command = object.getInteger(commandField);
        log.debug("receive command = {}", command);
        Class<? extends ProxyMessage> clazz = commandMap.get(command);
        return (T) object.toJavaObject(clazz);
    }


    @Override
    public String encodeObject(T message){
        return AESUtil.encryptByDefaultKey(JSON.toJSONString(message));
    }


    @Override
    public String encodeStr(String message){
        return AESUtil.encryptByDefaultKey(message);
    }
}
