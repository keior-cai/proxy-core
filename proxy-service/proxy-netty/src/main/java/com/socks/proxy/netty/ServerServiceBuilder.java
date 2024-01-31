package com.socks.proxy.netty;

import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.handshake.handler.NettyWebsocketProxyMessageHandler;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.NoCodeProxyCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.util.RSAUtil;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ServerServiceBuilder implements ServiceBuilder{

    private int port;

    private ChannelHandler handler;

    private ProxyCodes codes;

    private RSAUtil rsaUtil;

    /**
     * 是否使用转化握手协议 如果存在自定义codes时不生效
     */
    private boolean useCodes = true;


    @Override
    public TcpService builder(){
        init();
        return new NettyTcpService(port, handler);
    }


    private void init(){
        if(handler == null){
            this.handler = new WebsocketHandler(()->new NettyWebsocketProxyMessageHandler(rsaUtil));
        }
        if(codes == null && useCodes){
            codes = new DefaultProxyCommandCodes();
        } else if(codes == null){
            codes = new NoCodeProxyCodes();
        }
        if(rsaUtil == null){
            rsaUtil = new RSAUtil();
        }
    }
}
