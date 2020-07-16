package com.tokensigning.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.tokensigning.utils.Utils;

/**
* EventServlet: Websocket server config
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class EventServlet extends WebSocketServlet
{
	private static final long serialVersionUID = 1L;

	@Override
    public void configure(WebSocketServletFactory factory)
    {
    	int maxSize = Utils.getServerMaxSize();
    	factory.getPolicy().setIdleTimeout(Utils.getServerTimeout());
    	factory.getPolicy().setMaxBinaryMessageBufferSize(maxSize);
    	factory.getPolicy().setMaxBinaryMessageSize(maxSize);
    	
    	factory.getPolicy().setMaxTextMessageSize(maxSize);
    	factory.getPolicy().setMaxTextMessageBufferSize(maxSize);
        factory.register(EventSocket.class);
    }    
    
}