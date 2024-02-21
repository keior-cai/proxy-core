package com.socks.proxy.netty.system;

/**
 * @author: chuangjie
 * @date: 2024/2/21
 **/
public class MacSetUpProxy extends AbstractOsSetProxy{

    private String interfaceName = "Wi-Fi";


    public MacSetUpProxy(){
        //        try {
        //            // 执行命令：networksetup -listallhardwareports
        //            Process process = Runtime.getRuntime().exec("/usr/sbin/networksetup -listallhardwareports");
        //
        //            // 读取命令输出
        //            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //            String line;
        //            boolean isUsingWiFi = false;
        //            while ((line = reader.readLine()) != null) {
        //                // 检查输出中是否包含 Wi-Fi 接口
        //                if (line.contains("Wi-Fi")) {
        //                    isUsingWiFi = true;
        //                    break;
        //                }
        //            }
        //
        //            if (isUsingWiFi) {
        //                System.out.println("Mac is using WiFi connection.");
        //            } else {
        //                System.out.println("Mac is not using WiFi connection.");
        //            }
        //
        //            // 等待命令执行完毕
        //            process.waitFor();
        //        } catch (IOException | InterruptedException e) {
        //            e.printStackTrace();
        //        }
    }


    @Override
    public void turnOnProxy(String host, int port){
        // 执行设置HTTP代理的命令

        String[] setHTTPProxyCmd = { "networksetup", "-setwebproxy", interfaceName, host, Integer.toString(port) };

        executeCommand(setHTTPProxyCmd);

        // 执行设置HTTPS代理的命令
        String[] setHTTPSProxyCmd = { "networksetup", "-setsecurewebproxy", interfaceName, host,
                Integer.toString(port)
        };
        executeCommand(setHTTPSProxyCmd);

    }


    @Override
    public void turnOffProxy(){

        String[] disableHTTPProxyCmd = { "networksetup", "-setwebproxystate", "Wi-Fi", "off" };
        executeCommand(disableHTTPProxyCmd);

        // 执行关闭HTTPS代理的命令
        String[] disableHTTPSProxyCmd = { "networksetup", "-setsecurewebproxystate", "Wi-Fi", "off" };
        executeCommand(disableHTTPSProxyCmd);
    }

}
