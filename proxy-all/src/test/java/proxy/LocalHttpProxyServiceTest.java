package proxy;

import com.socks.proxy.netty.local.LocalHttpProxyService;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.codes.NoCodeProxyCodes;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.service.DefaultLocalServiceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * @author: chuangjie
 * @date: 2024/1/24
 **/
@Slf4j
public class LocalHttpProxyServiceTest{

    @Test
    public void testDirectHttpProxyServer(){
        TcpService service = new LocalHttpProxyService(1088, null);
        service.start();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1088)));
        RestTemplate template = new RestTemplate(factory);
        String forObject = template.getForObject("http://www.baidu.com", String.class);
        log.info("http body = {}", forObject);
        service.close();
    }


    @Test
    public void testDirectSocks5ProxyServer() throws URISyntaxException{
        TcpService service = new DefaultLocalServiceBuilder()
                .setPassword("123456")
                .setCodes(new NoCodeProxyCodes())
                .setUsername("admin")
                .setPort(1088)
                .setProtocol(Protocol.SOCKS5)
                .setServerList(Collections.singletonList(new URI("ws://127.0.0.1:8083")))
                .builder();
        service.start();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1088)));
        RestTemplate template = new RestTemplate(factory);
        String forObject = template.getForObject("http://www.baidu.com", String.class);
        log.info("http body = {}", forObject);
        service.close();
    }
}
