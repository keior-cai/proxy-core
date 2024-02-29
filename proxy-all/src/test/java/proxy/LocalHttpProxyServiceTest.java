package proxy;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author: chuangjie
 * @date: 2024/1/24
 **/
@Slf4j
public class LocalHttpProxyServiceTest{

    @Test
    public void testDirectSocks5ProxyServer(){
        RSAUtil rsaUtil = new RSAUtil();
        ProxyCodes codes = new DefaultProxyCommandCodes();
        LocalProxyMessageHandler handler = new LocalProxyMessageHandler(rsaUtil, codes, new MapConnectContextManager(),
                WebsocketProxyConnectFactory.createDefault("ws://127.0.0.1:8083"));
        TcpService tcpService = new LocalServiceBuilder().setPort(1088).setCodes(codes).setHandler(handler)
                .setRsaUtil(rsaUtil).setProtocol(Protocol.COMPLEX).builder();
        tcpService.start();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1088)));
        RestTemplate template = new RestTemplate(factory);
        String forObject = template.getForObject("https://www.baidu.com", String.class);
        log.info("http body = {}", forObject);
        tcpService.close();
    }
}
