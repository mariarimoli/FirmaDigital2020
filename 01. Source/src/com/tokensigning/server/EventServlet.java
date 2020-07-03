package com.tokensigning.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class EventServlet extends WebSocketServlet
{
	private static final int MAX_SIZE = 100 * 1024 * 1024;
    @Override
    public void configure(WebSocketServletFactory factory)
    {
    	factory.getPolicy().setIdleTimeout(15000);
    	factory.getPolicy().setMaxBinaryMessageBufferSize(MAX_SIZE);
    	factory.getPolicy().setMaxBinaryMessageSize(MAX_SIZE);
    	
    	factory.getPolicy().setMaxTextMessageSize(MAX_SIZE);
    	factory.getPolicy().setMaxTextMessageBufferSize(MAX_SIZE);
        factory.register(EventSocket.class);
    }    
    
}