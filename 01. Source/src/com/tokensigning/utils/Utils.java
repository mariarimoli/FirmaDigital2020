/**
 * 
 */
package com.tokensigning.utils;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.FileInfo;
import com.tokensigning.common.FileUtil;
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;

/**
* Utils: read value from config file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class Utils {
	
	// Folder config on MACOS
	public static final String CONFIG_FOLDER_MACOS 		= "/Library/TokenSigning Config";	
	// Folder config on WINDOWS 
	public static final String CONFIG_FOLDER_WINDOWS    = "\\TokenSigning Config\\";	
	// Font name
	public static final String PDF_FONT				    = "times.ttf";
	// File config
	public static final String APP_CONFIG			    = "app.properties";
	// Pkcs#11 driver configuration
	public static final String KEY_PROVIDER_LIST			    = "provider_list";
	// Pkcs#11 driver found on system
	public static final String KEY_PROVIDER_FOUND			    = "provider_found_list";
	// Proxy configuration
	public static final String PROXY_CONFIG_FILE 				= "proxy.txt";
	
	// cached result of OS detection
	protected static OSType detectedOS;
	
	// Properties load from configuration file
	private static Properties prop = null;
		
	// file content encoded base64
	private static volatile String FileBase64           = "";
	

	/**
     * get certificate folder configuration
     *
     * @return path to file
     */
	public static String getCertificateFolder()
	{
		String configFolder = getConfigFolder();
		return Paths.get(configFolder, "Certificate").toString();
		
	}
	
	/**
     * get font folder configuration
     *
     * @return path to folder
     */
	public static String getFontFolder()
	{
		String pathFile = null;
		String configFolder = getConfigFolder();
		pathFile = Paths.get(configFolder, "Fonts", "times.ttf").toString();
    	return pathFile;
	}
	
	/**
     * get log folder configuration
     *
     * @return path to folder
     */
	public static String getLogFolder()
	{
    	String configFolder = getConfigFolder();		
    	return Paths.get(configFolder, "Log").toString();
	}
	
	/**
     * get file configuration
     *
     * @return path to file
     */
	public static String getAppConfig()
	{		
    	String configFolder = getConfigFolder();		
    	return Paths.get(configFolder, APP_CONFIG).toString();
	}
	
	/**
     * get folder configuration
     *
     * @return path to folder
     */
	public static String getConfigFolder()
	{
		String pathFile = null;
		
		OSType osType = Utils.getOperatingSystemType();
		
		switch (osType) {
			case Windows:
				pathFile = System.getenv("APPDATA") + CONFIG_FOLDER_WINDOWS;
				break;
			case MacOS:
				pathFile = CONFIG_FOLDER_MACOS;
				break;
			case Linux:
				break;
			default:
				break;
		}
		
    	return pathFile;
	}
	
	/**
     * Choose file on system
     *
     * @return file content encoded base64
     */
	public static String chooseFile()
	{
		LOG.write("Utils.chooseFile", "Create dialog");
		final JFrame frame = new JFrame("Choose File Dialog");
		frame.setAlwaysOnTop(true);
		LOG.write("Utils.chooseFile", "Show dialog");
		
		 try {
			EventQueue.invokeAndWait(new Runnable() {
			        @Override
			        public void run() {
			            String folder = System.getProperty("user.home");
			            JFileChooser fileChooser = new JFileChooser(folder);
			            //fileChooser.setCurrentDirectory(new File(folder));
			            int result = fileChooser.showOpenDialog(frame);
			    		if (result == JFileChooser.APPROVE_OPTION) {
			    		    File selectedFile = fileChooser.getSelectedFile();
			    		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			    		    try {
			    				String base64File = Base64Utils.base64Encode(FileUtil.readBytesFromFile(selectedFile.getAbsolutePath()));
			    				FileInfo file = new FileInfo(selectedFile.getAbsolutePath(), base64File);
			    				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			    				FileBase64 = gson.toJson(file);
			    			} catch (IOException e) {
			    				LOG.write("Utils", e.getMessage());
			    			}
			    		}
			    		else
			    		{
			    			FileInfo file = new FileInfo("", "");
			    			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		    				FileBase64 = gson.toJson(file);
			    		}
			        }
			    });
		} catch (InvocationTargetException | InterruptedException e1) {
			LOG.write("Utils", e1.getMessage());
		}
		
		return FileBase64;
	}
	
	/**
     * Save text file
     *
     * @param text is content
     * @param path to file
     * @return true or false
     */
	public static boolean SaveTxtFile(String text, String path) {
        try 
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(text);
            bw.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }	
		
	/**
     * Get value by key
     *
     * @param key
     * @return value
     */
	public static String getValueFromConfig(String key) {
		if (prop != null)
		{
			if (null != prop.getProperty(key)) {

				return prop.getProperty(key);
			}
		}
		else			
		{
			prop = new Properties();
			InputStream input = null;
			File f = null;
			try {
				

				// load a properties file
				String configFile = Utils.getAppConfig();
				f = new File(configFile.toString());
				if (f.exists() && !f.isDirectory()) {
					input = new FileInputStream(configFile.toString());
					prop.load(input);
					if (null != prop && null != prop.getProperty(key)) {

						return prop.getProperty(key);
					}
				}
			} catch (FileNotFoundException e) {
				LOG.write("Utils", e.getMessage());
			} catch (IOException e) {
				LOG.write("Utils", e.getMessage());
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					LOG.write("Utils", e.getMessage());
				}
			}

		}
		
		return null;
	}

	/**
     * Check configuration status when install or start app
     *
     * @param key
     * @return value
     */
	public static int checkConfigStatus(String key) {
		try {
			return Integer.parseInt(getValueFromConfig(key));

		} catch (Exception e) {
			LOG.write("Utils", e.getMessage());
		}

		return 0;
	}
	
	/**
     * Save new value to configuration file
     *
     * @param key
     * @param value
     */
	public static void setValueToConfig(String key, String value)
    {
    	try {
    		Properties prop = new Properties();    	
        	InputStream input;
			// load a properties file
			String configFile = Utils.getAppConfig();
			File f = new File(configFile.toString());
    		if(f.exists() && !f.isDirectory()) { 
    			input = new FileInputStream(configFile);			
    			prop.load(input);
    			if (null != prop && null != prop.getProperty(key))
    			{
    				prop.setProperty(key, value);
    				input.close();
    	            FileOutputStream fr=new FileOutputStream(configFile);
    	            prop.store(fr, "Properties");
    	            fr.close();
    			}	
    		}	
    	} catch (FileNotFoundException e) {
    		LOG.write("Utils", e.getMessage());
		} catch (IOException e) {
			LOG.write("Utils", e.getMessage());
		} 
    	return;
    }

	/**
     * Set Firefox browser config
     *
     */
	public static void setFirefoxConfig()
    {
		setValueToConfig("firefox_config", "1");
    }
	
	/**
     * Check Firefox browser config
     *
     */
	public static int checkFirefoxConfig()
    {
		return checkConfigStatus("firefox_config");
    }
	
	/**
     * Set Safari browser config
     *
     */
	public static void setSafariConfig()
    {
		setValueToConfig("safari_config", "1");
    }
	
	/**
     * Check Safari browser config
     *
     */
	public static int checkSafariConfig()
    {
		return checkConfigStatus("safari_config");
    }
	
	/**
     * Check config firt time when install app
     *
     */
	public static int checkConfigFirtTime()
    {
		return checkConfigStatus("install_config");
    }
	
	/**
     * Set config
     *
     */
	public static void setConfigFirtTime()
    {
		setValueToConfig("install_config", "1");
    }
	
	/**
     * Get app version from config file
     * 
     * @return app's version
     */
	public static String getAppVersion()
    {
		return getValueFromConfig("app_version");
    }
	
	/**
     * Get certificate ssl serial
     * 
     * @return serial
     */
	public static String getCertSslSerial()
    {
		return getValueFromConfig("SSL_CA_SERIAL");
    }
	
	/**
     * Get server max post size
     * 
     * @return max size
     */
	public static int getServerMaxSize()
    {
		int maxSize = 100 * 1024 * 1024;
		String maxSizeStr = Utils.getValueFromConfig("MAX_SIZE");
		if (maxSizeStr == null || maxSizeStr.isEmpty())
		{
			return maxSize;
		}
		return Integer.parseInt(maxSizeStr);
    }
	
	/**
     * Get server timeout
     * 
     * @return timeout
     */
	public static int getServerTimeout()
    {
		int timeOut = 15000;
		String timeOutStr = Utils.getValueFromConfig("TIME_OUT");
		if (timeOutStr == null || timeOutStr.isEmpty())
		{
			return timeOut;
		}
		return Integer.parseInt(timeOutStr);
    }
	
	/**
     * Get Pkcs#11 driver found on system
     * 
     * @return driver list
     */
	public static String getProviderFoundList()
	{
		return getValueFromConfig(KEY_PROVIDER_FOUND);
	}

	/**
     * Set Pkcs#11 driver found on system
     * 
     * @param dllFound
     */
	public static void setProviderFoundList(String dllFound) {
		try {
			String dll = FilenameUtils.getBaseName(dllFound);
			String listFound = getValueFromConfig(KEY_PROVIDER_FOUND);
			if (listFound == null || listFound.isEmpty()) {
				setValueToConfig(KEY_PROVIDER_FOUND, dll);
			} else {
				String[] providerArr = listFound.split(",");
				if (!Arrays.asList(providerArr).contains(listFound)) {
					listFound = dll + "," + listFound;
					setValueToConfig(KEY_PROVIDER_FOUND, listFound);
				}
			}
		} catch (Exception ex) {
			LOG.write("setProviderFoundList", ex.getMessage());
		}
	}

	/**
     * Hash SHA1
     * 
     * @param text
     */
	public static byte[] SHA1(byte[] text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text, 0, text.length);
			sha1hash = md.digest();
			return sha1hash;
		} catch (Exception ex) {
			LOG.write("Utils.SHA1", ex.getMessage());
		}
		return null;
	}
	
	/**
     * Get current language configuration
     * 
     * @return language
     */
	public static String getCurrentLanguage()
    {
		try
		{
			String lang = getValueFromConfig("language");
			if (lang == null)
			{
				lang = "en";
			}
			String configFolder = getConfigFolder();
			String langFile = Paths.get(configFolder.trim(), "Language", lang + ".lg").toString();
			return langFile;
		}
		catch (Exception ex)
		{
			LOG.write("", ex.getMessage());
		}
		return null;
    }
	

	/**
     * Detect os type
     * 
     * @return OSType
     */
	public static OSType getOperatingSystemType() {
	    if (detectedOS == null) {
	      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
	      if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
	        detectedOS = OSType.MacOS;
	      } else if (OS.indexOf("win") >= 0) {
	        detectedOS = OSType.Windows;
	      } else if (OS.indexOf("nux") >= 0) {
	        detectedOS = OSType.Linux;
	      } else {
	        detectedOS = OSType.Other;
	      }
	    }
	    return detectedOS;
	}

}
