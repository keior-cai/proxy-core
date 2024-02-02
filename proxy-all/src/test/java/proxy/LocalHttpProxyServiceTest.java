package proxy;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        LocalProxyMessageHandler handler = new LocalProxyMessageHandler(rsaUtil, codes, new MapConnectContextManager());
        Map<String, ProxyFactory> proxyFactoryMap = new HashMap<>();
        proxyFactoryMap.put("test", WebsocketProxyConnectFactory.createDefault(
                Collections.singletonList(URI.create("ws://127.0.0.1:8083"))));
        handler.setName("test");
        handler.setFactoryMap(proxyFactoryMap);
        TcpService tcpService = new LocalServiceBuilder()
                .setPort(1088)
                .setCodes(codes)
                .setHandler(handler)
                .setRsaUtil(rsaUtil)
                .setProtocol(Protocol.SOCKS5)
                .builder();
        tcpService.start();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1088)));
        RestTemplate template = new RestTemplate(factory);
        String forObject = template.getForObject("http://www.baidu.com", String.class);
        log.info("http body = {}", forObject);
        tcpService.close();
    }

    @Test
    public void testRestart(){
        TcpService tcpService = createTcpService();
        tcpService.start();
        tcpService.restart();
        tcpService.close();
    }

    private TcpService createTcpService(){
        RSAUtil rsaUtil = new RSAUtil();
        ProxyCodes codes = new DefaultProxyCommandCodes();
        LocalProxyMessageHandler handler = new LocalProxyMessageHandler(rsaUtil, codes, new MapConnectContextManager());
        Map<String, ProxyFactory> proxyFactoryMap = new HashMap<>();
        proxyFactoryMap.put("test", WebsocketProxyConnectFactory.createDefault(
                Collections.singletonList(URI.create("ws://127.0.0.1:8083"))));
        handler.setName("test");
        handler.setFactoryMap(proxyFactoryMap);
        return new LocalServiceBuilder()
                .setPort(1088)
                .setCodes(codes)
                .setHandler(handler)
                .setRsaUtil(rsaUtil)
                .setProtocol(Protocol.SOCKS5)
                .builder();
    }
}
