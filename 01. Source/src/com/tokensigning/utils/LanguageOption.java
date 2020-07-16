package com.tokensigning.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Set;

import com.tokensigning.common.LOG;

/**
* LanguageOption: language configuration
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

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

	/**
     * Constructor
     *
     */
	public LanguageOption()
	{
		String fileLang = Utils.getCurrentLanguage();
		if (fileLang != null)
		{
			updateLanguage(fileLang);	
		}
	}
	
	
	/**
     * Constructor
     *
     * @param langConfig is language config (spa or por)
     */
	public LanguageOption(String langConfig)
	{
		String configFolder = Utils.getConfigFolder();
		String langFile = Paths.get(configFolder.trim(), "Language", langConfig + ".lg").toString();
		if (langFile != null)
		{
			updateLanguage(langFile);	
		}
	}
	
	/**
     * Update label, text from language configuration file
     *
     * @param fileConfig is path to file configuration (spa or por)
     */
	public void updateLanguage(String fileConfig)
	{
		Hashtable<String, String> hashLang = getLanguage(fileConfig);
		Set<String> keys = hashLang.keySet();
        for(String key: keys){
            try {
            	Field field = this.getClass().getDeclaredField(key);
				field.set(this, hashLang.get(key));
			} catch (NoSuchFieldException e) {
				LOG.write("LanguageOption", e.getMessage());
			} catch (SecurityException e) {
				LOG.write("LanguageOption", e.getMessage());
			} catch (IllegalArgumentException e) {
				LOG.write("LanguageOption", e.getMessage());
			} catch (IllegalAccessException e) {
				LOG.write("LanguageOption", e.getMessage());
			}
        }
	}
	
	
	/**
     * Load label, text from language configuration file
     *
     * @param fileConfig is path to file configuration (spa or por)
     */
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
