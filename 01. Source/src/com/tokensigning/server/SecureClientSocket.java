package com.tokensigning.server;
import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

@WebSocket
public class SecureClientSocket
{
    public static void TestWebSocket()
    {
        String url = "wss://localhost:4433/plugin";

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true); // The magic

        WebSocketClient client = new WebSocketClient(sslContextFactory);
        try
        {
            client.start();
            SecureClientSocket socket = new SecureClientSocket();
            Future<Session> fut = client.connect(socket,URI.create(url));
            Session session = fut.get();
            session.getRemote().sendString("Hello");
            //session.getRemote().sendString("155-questions-active");
        }
        catch (Throwable t)
        {
            //LOG.warn(t);
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session sess)
    {
        //LOG.info("onConnect({})",sess);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        //LOG.info("onClose({}, {})", statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause)
    {
        //LOG.warn(cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        //LOG.info("onMessage() - {}", msg);
    }
}