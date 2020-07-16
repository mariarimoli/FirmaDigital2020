package com.tokensigning.server;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.tokensigning.common.IconLoader;
import com.tokensigning.common.LOG;
import com.tokensigning.configuraion.Configuration;
import com.tokensigning.form.PINVerification;
import com.tokensigning.form.SystemTrayEvent;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.utils.LanguageOption;

/**
* EventServer: Websocket server
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class EventServer {
	public static CertificateHandle certHandle;
	public static LanguageOption _language;
	public static List<String> DomainLicenseChecked = new ArrayList<String>();
	public final static int TIMER_INTERVAL 			= 1*60*60*1000;
	public final static int SERVER_PORT 			= 4667;
	
	/**
     * Main
     *
     * @param 
     * @return
     */	
	public static void main(String[] args) throws Exception {
		try {
			
			certHandle = new CertificateHandle();
			_language = new LanguageOption();
			IconLoader iconLoader = new IconLoader();
			iconLoader.loadIcon();
			//
			Server server = new Server();
			LOG.write("Main", "Starting...");
			// Creating the web application context
			WebAppContext webapp = new WebAppContext();
			webapp.setResourceBase("src/main/webapp");
			server.setHandler(webapp);

			// HTTPS configuration
			HttpConfiguration https = new HttpConfiguration();
			https.addCustomizer(new SecureRequestCustomizer());

			// Configuring SSL
			LOG.write("Main", "Configuring SSL");
			SslContextFactory sslContextFactory = new SslContextFactory();
						
			// Defining keystore path and passwords			
			String keyStorePath = Paths.get(com.tokensigning.utils.Utils.getCertificateFolder(), "localhost.jks").toString();
			File fKeystore = new File(keyStorePath);
			if (!fKeystore.exists() || fKeystore.isDirectory()) {
				LOG.write("Main", "Not found ssl keystore: " + keyStorePath);
				PINVerification.showErrorMessage(LanguageOption.WARNING_INSTALL_FALIED);
				return;
			}
			// Check browser
			Configuration.ShowRestartBrowserRequired();
			Configuration.ConfigBrowsers();
			sslContextFactory.setKeyStorePath(keyStorePath);
			LOG.write("Main", "Implement ssl two way");
			String keyStorePwd = "tsBun@123s";
			sslContextFactory.setKeyStorePassword(keyStorePwd);
			sslContextFactory.setKeyManagerPassword(keyStorePwd);

			// Configuring the connector
			ServerConnector sslConnector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
			sslConnector.setPort(SERVER_PORT);
			sslConnector.setHost("127.0.0.1");
			// Setting HTTP and HTTPS connectors
			server.addConnector(sslConnector);
			//
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			// Add a websocket to a specific path spec
			ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
			context.addServlet(holderEvents, "/tokensigning/*");
	
			LOG.write("Main", "Show notification");
			SystemTrayEvent.Show();
			// Starting the Server
			LOG.write("Main", "Server accepted");
			server.start();
			LOG.write("Main", "Started");
			server.join();
		} catch (Throwable t) {			
			t.printStackTrace(System.err);
			System.exit(0);
		}
	}
}