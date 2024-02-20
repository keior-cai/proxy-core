package proxy;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.junit.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author: chuangjie
 * @date: 2024/2/20
 **/
public class ConcurrentService{

    @Test
    public void websocket() throws Exception{
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        for(int i = 0; i < 100; i++) {
            WebSocket socket = webSocketFactory.createSocket("ws://127.0.0.1:8083");
            socket.addListener(new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception{
                    System.out.println(text);
                }
            });
            socket.connect();
        }
        Thread.sleep(100000L);
    }


    @Test
    public void http() throws Exception{
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1088)));
        RestTemplate template = new RestTemplate(factory);
        String forObject = template.getForObject("https://www.baidu.com", String.class);
    }

}
