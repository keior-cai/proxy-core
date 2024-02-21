package com.socks.proxy.netty.local;

import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.netty.connect.DirectConnectFactory;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.NoCodeProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;

import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2024/2/20
 **/
public class SimpleLocalTcpServer implements TcpService{

    private final TcpService tcpService;

    private final DirectConnectFactory factory = new DirectConnectFactory();

    public SimpleLocalTcpServer(int port){
        tcpService = new LocalServiceBuilder()
                .setCodes(new NoCodeProxyCodes())
                .setHandler(new ProxyMessageHandler(){

                    @Override
                    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){

                    }


                    @Override
                    public void handleLocalTextMessage(ProxyConnect local, String text){

                    }


                    @Override
                    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){

                    }


                    @Override
                    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){

                    }


                    @Override
                    public void handleTargetClose(ProxyConnect target, Exception e){

                    }


                    @Override
                    public void handleLocalClose(ProxyConnect local, Exception e){

                    }


                    @Override
                    public ProxyConnect targetConnect(ProxyConnect local, TargetServer target){
                        return factory.create(target, this);
                    }
                })
                .setPort(port)
                .builder();

    }


    @Override
    public void start(){
        tcpService.start();
    }


    @Override
    public void close(){
        tcpService.close();
    }


    @Override
    public void restart(){
        tcpService.restart();
    }


    public static void main(String[] args){
        SimpleLocalTcpServer simpleLocalTcpServer = new SimpleLocalTcpServer(1088);
        simpleLocalTcpServer.start();
    }
}
