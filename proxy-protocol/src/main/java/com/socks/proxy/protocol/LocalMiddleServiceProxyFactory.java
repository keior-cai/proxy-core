package com.socks.proxy.protocol;

/**
 * @author: chuangjie
 * @date: 2023/7/9
 **/
public interface LocalMiddleServiceProxyFactory{

    /**
     * 获取代理服务
     *
     * @param service 正常服务
     * @return 获取到一个通过代理服务包装的对象
     */
    LocalMiddleService getService(LocalMiddleService service);

}
