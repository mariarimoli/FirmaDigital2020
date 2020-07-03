package com.tokensigning.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Set;

import com.tokensigning.common.LOG;

public class LanguageOption {	
	// Config install
	public static String WARNING_CAPTION = "Warning";
	public static String CLOSE_BROWSER = "You need to close your browser to continue the configuration process of TokenSigning.";
	public static String RESTART_BROWSER = "You need open TokenSigning and restart browser to complete";
	public static String WARNING_INSTALL_FALIED = "Certificate configuration failed. Please reinstall the software!";
	public static String COMMON_DIALOG_TITLE = "Notification";	
	// Choose Certificate
	public static String CERT_DIALOG_REQUEST = "Select certificate:";
	public static String CERT_DIALOG_CAPTION = "List of digital certificates";
	// PIN DIALOG
	public static String PIN_DIALOG_TITLE = "PIN Authentication";
	public static String PIN_DIALOG_LABEL = "Enter PIN:";	
	public static String PIN_DIALOG_ALERT_CONTENT = "Enter your PIN here";	
	public static String PIN_DIALOG_WARNING_INVALID_SIZE = "Invalid PIN length!";
	public static String PIN_DIALOG_WARNING_INCORRECT = "Incorrect PIN!";
	public static String PIN_DIALOG_WARNING_LOCK = "Your usb token has been locked. Please contact the vendor to unlock!";
	public static String PIN_DIALOG_WARNING_MAX_INPUT = "You have entered incorrect pin too many times!";
	// SYSTEM TRAY
	public static String SYSTEM_TRAY_MENU_INFO = "Information";
	public static String SYSTEM_TRAY_MENU_CLOSE = "Close";
	public static String SYSTEM_TRAY_MENU_LANGUAGE = "Language";
	public static String SYSTEM_TRAY_VERSION = "TokenSigning. Version ";
	public static String SYSTEM_TRAY_WARNING_CLOSE = "You will need to re-open the software when signing on the web! Still want to close?";
	public static String SYSTEM_TRAY_RUNNING = "is running";
	// FIREFOX
	public static String CLOSE_FIREFOX = "You need to close Firefox browser. Click OK to close Firefox.";
	// LANGUAGE
	public static String LANGUAGE_DIALOG = "Language";
	public static String LANGUAGE_DIALOG_REQUEST = "Choose language:";
	public static String LANGUAGE_DIALOG_CAPTION = "Language";
	//
	public LanguageOption()
	{
		String fileLang = Utils.getCurrentLanguage();
		if (fileLang != null)
		{
			updateLanguage(fileLang);	
		}
	}
	
	public LanguageOption(String langConfig)
	{
		//String fileLang = Utils.getCurrentLanguage();
		String configFolder = Utils.getConfigFolder();
		String langFile = Paths.get(configFolder.trim(), "Language", langConfig + ".lg").toString();
		if (langFile != null)
		{
			updateLanguage(langFile);	
		}
	}
	
	public void updateLanguage(String fileConfig)
	{
		Hashtable<String, String> hashLang = getLanguage(fileConfig);
		Set<String> keys = hashLang.keySet();
        for(String key: keys){
            //System.out.println("Value of "+key+" is: "+hashLang.get(key));
            try {
				//Field field = this.getClass().getField(key);
            	Field field = this.getClass().getDeclaredField(key);
				field.set(this, hashLang.get(key));
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public Hashtable<String, String> getLanguage(String fileConfig)
	{
		Hashtable<String, String> hashLang = new Hashtable<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					fileConfig));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				try
				{
					// read next line
					line = reader.readLine();
					if (line.startsWith("#"))
					{
						continue;
					}
					String[] lSp = line.split("=");
					hashLang.put(lSp[0], lSp[1]);
				}
				catch(Exception e)
				{
					LOG.write("getLanguage", e.getMessage());		
				}		
			}
			reader.close();
		} catch (IOException e) {
			LOG.write("getLanguage", e.getMessage());
		}
		return hashLang;		
	}
}
