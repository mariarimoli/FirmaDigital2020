package com.tokensigning.server;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tokensigning.common.Connector;
import com.tokensigning.common.FUNCTION_ID;
import com.tokensigning.common.LOG;
import com.tokensigning.common.RESPONSE_CODE;
import com.tokensigning.common.TSResponse;
import com.tokensigning.license.KEY_CODE;
import com.tokensigning.license.LicenseKey;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.utils.Utils;

public class EventSocket extends WebSocketAdapter
{
	//TODO can check khi co nhieu ket noi toi thi session co bi anh huong hay khong?
	//TODO setup ssl nhu the nao?
	public static String _domainConnect = null;
	public static String _domainWithoutHttp = null;
	private Session sess;
	public static final String APP_VERSION = "1.0.0.2";
	private static final Gson _gson = new GsonBuilder().disableHtmlEscaping().create();
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        sess.setIdleTimeout(15000);
        
        _domainConnect = sess.getUpgradeRequest().getOrigin();
        String[] domainSplit = _domainConnect.split(":");
        if (domainSplit.length > 1)
        {
        	_domainWithoutHttp = domainSplit[1].substring(2, domainSplit[1].length());
        }
        this.sess = sess;
    }
    
    @Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		TSResponse resp = new TSResponse();
		resp.setCode(RESPONSE_CODE.error.getValue());
		try {
			Connector connect = _gson.fromJson(message, Connector.class);
			FUNCTION_ID id = FUNCTION_ID.values()[connect.getFunctionID()];
			
			String result = "";
			
			if (!id.equals(FUNCTION_ID.checkTokenSigning) && !id.equals(FUNCTION_ID.getVersion))
			{
				KEY_CODE keyCode = LicenseKey.checkKey(connect.getKey(), _domainWithoutHttp);
				if (keyCode != KEY_CODE.keyGood)
				{					
					resp.setError("License invalid. Domain : " + _domainConnect + " .Code: " + keyCode);
					resp.setCode(RESPONSE_CODE.error.getValue());
					result = _gson.toJson(resp);
					sendMessageToBrowser(result);
					return;		
				}
			}
			//
			if (null == EventServer.certHandle)
			{
				EventServer.certHandle = new CertificateHandle();		
			}
			EventProcessing processing = new EventProcessing(EventServer.certHandle);
			
			// check license
			LOG.write("Main.ID", id.toString());
			switch (id) {
				case checkTokenSigning:
					resp.setCode(RESPONSE_CODE.success.getValue());
					break;
				case getVersion:
					result = Utils.getAppVersion();
					break;
				case selectCertificate:
					resp = processing.getCertInfo(connect);
					break;
				case signXml:
					resp = processing.signXml(connect);
					break;
				case signPdf:
					resp = processing.signPdf(connect);					
					break;
				case signOOxml:
					resp = processing.signOffice(connect);
					break;
				case signCms:
					resp = processing.signCms(connect);
					break;
				default:
					break;
			}
			result = _gson.toJson(resp);
			sendMessageToBrowser(result);
		} catch (Exception ex) {
			System.err.println(ex);
			LOG.write("Main", ex.getMessage());
			resp.setCode(RESPONSE_CODE.error.getValue());
			sendMessageToBrowser(_gson.toJson(resp));
		}
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        //cause.printStackTrace(System.err);
    }
    
    private void sendMessageToBrowser(String message)
    {
    	try {
    		//LOG.write("Main.Return", message);
			sess.getRemote().sendString(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.write("Main.Return", e.getMessage());
		}
    }
}