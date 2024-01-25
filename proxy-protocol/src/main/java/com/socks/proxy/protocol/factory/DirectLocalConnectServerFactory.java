//package com.socks.proxy.protocol.factory;
//
//import com.socks.proxy.protocol.DirectLocalMiddleProxy;
//import com.socks.proxy.protocol.connect.ProxyConnect;
//import com.socks.proxy.protocol.TargetServer;
//import com.socks.proxy.protocol.listener.ProxyListener;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author: chuangjie
// * @date: 2023/7/8
// **/
//public class DirectLocalConnectServerFactory implements ProxyFactory{
//
//    private final List<ProxyListener> messageLister;
//
//    private final Selector selector;
//
//    private Thread thread;
//
//    private final Map<SocketChannel, WorkSockets> connectMap = new ConcurrentHashMap<>();
//
//
//    public DirectLocalConnectServerFactory(List<ProxyListener> messageLister){
//        this.messageLister = messageLister;
//        try {
//            selector = Selector.open();
//        } catch (IOException e) {
//            throw new Error(e);
//        }
//        startSelect();
//    }
//
//
//    @Override
//    public ProxyConnect create(TargetServer targetServer){
//        try {
//            SocketChannel socketChannel = SocketChannel.open();
//            socketChannel.configureBlocking(false);
//            socketChannel.register(selector, SelectionKey.OP_CONNECT);
//            socketChannel.connect(new InetSocketAddress(targetServer.host(), targetServer.port()));
//            while(!socketChannel.finishConnect()) {
//
//            }
//            ProxyConnect connect = new DirectLocalMiddleProxy(socketChannel);
//            connectMap.put(socketChannel, new WorkSockets(connect, local));
//            return connect;
//        } catch (IOException e) {
//            throw new Error(e);
//        }
//    }
//
//
//    public void close(){
//        thread.interrupt();
//        try {
//            selector.close();
//        } catch (IOException e) {
//            // ignore
//        }
//        if(thread != null){
//            thread.interrupt();
//        }
//    }
//
//
//    private void startSelect(){
//        thread = new Thread(()->{
//            while(!Thread.currentThread().isInterrupted()) {
//                try {
//                    selector.select();
//                    // 获取发生事件的 SelectionKey 集合
//                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
//                    while(keyIterator.hasNext()) {
//                        SelectionKey key = keyIterator.next();
//                        keyIterator.remove();
//                        // 处理事件
//                        SocketChannel channel = (SocketChannel) key.channel();
//                        WorkSockets workSocket = connectMap.get(channel);
//                        if(workSocket == null){
//                            continue;
//                        }
//                        if(key.isConnectable()){
//                            channel.register(selector, SelectionKey.OP_WRITE);
//                        } else if(key.isReadable()){
//                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
//                            int bytesRead;
//                            while((bytesRead = channel.read(readBuffer)) != -1) {
//                                readBuffer.flip();
//                                byte[] receivedData = new byte[bytesRead];
//                                readBuffer.get(receivedData);
//                                for(ProxyListener listener : messageLister) {
//                                    listener.onMessage(workSocket.getConnect(), workSocket.getLocal(), receivedData);
//                                }
//                            }
//                            channel.register(selector, SelectionKey.OP_WRITE);
//                        }
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }, "nio-selector-main");
//        thread.start();
//    }
//
//
//    @Getter
//    @AllArgsConstructor
//    private static class WorkSockets{
//
//        private final ProxyConnect connect;
//
//        private final ProxyConnect local;
//
//    }
//}
