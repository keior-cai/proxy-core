package com.socks.proxy.netty.system;

/**
 * @author: chuangjie
 * @date: 2024/2/21
 **/
public class WindowsSetProxy extends AbstractOsSetProxy{
    @Override
    public void turnOnProxy(String host, int port){
        setProxy("HTTP", host, Integer.toString(port));
        setProxy("HTTPS", host, Integer.toString(port));

    }


    @Override
    public void turnOffProxy(){
        executeCommand("reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f");
    }

    private void setProxy(String protocol, String server, String port) {
            // 设置注册表项的值
        String command = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 1 /f";
        executeCommand(command);
        command = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyServer /t REG_SZ /d " + server + ":" + port + " /f";
        executeCommand(command);
        command = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyOverride /t REG_SZ /d \"<local>;\" /f";
        executeCommand(command);
    }
}
