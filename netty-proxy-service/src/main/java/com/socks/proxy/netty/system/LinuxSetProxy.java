package com.socks.proxy.netty.system;

/**
 * @author: chuangjie
 * @date: 2024/2/21
 **/
public class LinuxSetProxy extends AbstractOsSetProxy{
    @Override
    public void turnOnProxy(String host, int port){

        executeCommand("gsettings set org.gnome.system.proxy mode 'manual'");
        executeCommand(String.format("gsettings set org.gnome.system.proxy.http host '%s'", host));
        executeCommand(String.format("gsettings set org.gnome.system.proxy.http port %d", port));

        // 设置HTTPS代理
        executeCommand(String.format("gsettings set org.gnome.system.proxy.https host '%s'", host));
        executeCommand(String.format("gsettings set org.gnome.system.proxy.https port %d", port));
    }


    @Override
    public void turnOffProxy(){
        executeCommand("gsettings set org.gnome.system.proxy mode 'none'");
    }
}
