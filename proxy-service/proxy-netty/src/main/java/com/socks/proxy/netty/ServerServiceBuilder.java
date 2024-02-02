package com.socks.proxy.netty;

import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.netty.connect.DricetConnectFactory;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.NoCodeProxyCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.ServiceProxyMessageHandler;
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

    /**
     * 连接管理器
     */
    private ConnectContextManager connectContextManager;


    @Override
    public TcpService builder(){
        init();
        return new NettyTcpService(port, handler);
    }


    private void init(){
        if(connectContextManager == null){
            this.connectContextManager = new MapConnectContextManager();
        }
        if(handler == null){
            this.handler = new WebsocketHandler(
                    ()->new ServiceProxyMessageHandler(rsaUtil, codes, connectContextManager,
                            new DricetConnectFactory()));
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
