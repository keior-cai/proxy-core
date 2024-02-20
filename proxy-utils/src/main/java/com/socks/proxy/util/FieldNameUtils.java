package com.socks.proxy.util;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;

import java.lang.invoke.SerializedLambda;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Locale;
import java.util.Set;

/**
 * @Description: field name utils
 */
public final class FieldNameUtils{

    private FieldNameUtils(){

    }


    public static <T> String getFieldName(Func1<T, ?> func){
        SerializedLambda resolve = LambdaUtil.resolve(func);
        return methodToProperty(resolve.getImplMethodName());
    }


    public static String methodToProperty(String name){
        if(name.startsWith("is")){
            name = name.substring(2);
        } else {
            if(!name.startsWith("get") && !name.startsWith("set")){
                throw new RuntimeException(
                        "Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if(name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))){
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }


    public static void main(String[] args) throws Throwable{
        Selector open = Selector.open();
        new Thread(()->{
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    SocketChannel channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.register(open, SelectionKey.OP_CONNECT);
                    channel.connect(new InetSocketAddress("www.baidu.com", 80));
                    open.wakeup();
                    Thread.sleep(3000L);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        while(!Thread.currentThread().isInterrupted()) {
            int select = open.select();
            Set<SelectionKey> selectionKeys = open.selectedKeys();
            for(SelectionKey key : selectionKeys) {
                SocketChannel channel = (SocketChannel) key.channel();
                if(key.isConnectable()){
                    channel.register(open, SelectionKey.OP_WRITE);
                } else if(key.isWritable()){
                    channel.write(ByteBuffer.wrap(new byte[] { 01, 02 }));
                    channel.register(open, SelectionKey.OP_READ);
                }
            }
        }
    }
}
