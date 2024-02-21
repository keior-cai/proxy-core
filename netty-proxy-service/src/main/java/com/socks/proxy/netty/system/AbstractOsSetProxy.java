package com.socks.proxy.netty.system;

import java.io.IOException;

/**
 * @author: chuangjie
 * @date: 2024/2/21
 **/
public abstract class AbstractOsSetProxy implements SetProxy{

    protected void executeCommand(String command){
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("修改系统代理失败");
        }
    }


    protected void executeCommand(String[] command){
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("修改系统代理失败");
        }
    }
}
