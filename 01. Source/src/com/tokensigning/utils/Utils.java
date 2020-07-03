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
 * @author Tuan Tran
 *
 */
public class Utils {
	
	// MACOS
	public static final String CONFIG_FOLDER_MACOS 		= "/Library/TokenSigning Config";
	//public static final String CONFIG_FOLDER_LOG_MACOS  = "/Library/TokenSigning Config/Log";
	//public static final String CONFIG_FOLDER_CERTIFICATE_MACOS  = "/Library/TokenSigning Config/Certificate";
	
	//WINDOWS 
	public static final String CONFIG_FOLDER_WINDOWS    = "\\TokenSigning Config\\";
	
	
	public static final String PDF_FONT				    = "times.ttf";
	public static final String APP_CONFIG			    = "app.properties";
	// Key
	public static final String KEY_PROVIDER_LIST			    = "provider_list";
	public static final String KEY_PROVIDER_FOUND			    = "provider_found_list";

	// cached result of OS detection
	protected static OSType detectedOS;
		
	
	//private static final String key = "Zx" + Math.log(2) / 3;
	
	private static volatile String FileBase64           = "";
	
	public static String getCertificateFolder()
	{
		String configFolder = getConfigFolder();
		return Paths.get(configFolder, "Certificate").toString();
		
	}
	
	public static String getFontFolder()
	{
		String pathFile = null;
		String configFolder = getConfigFolder();		
		//String lang = getValueFromConfig("language");		
		pathFile = Paths.get(configFolder, "Fonts", "times.ttf").toString();
    	return pathFile;
	}
	
	public static String getLogFolder()
	{
    	String configFolder = getConfigFolder();		
    	return Paths.get(configFolder, "Log").toString();
	}
	public static String getIssuerConfigFolder(){
    	String configFolder = getConfigFolder();		
    	return Paths.get(configFolder, "CA").toString();
	}	
	public static String getAppConfig()
	{		
    	String configFolder = getConfigFolder();		
    	return Paths.get(configFolder, APP_CONFIG).toString();
	}
	
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
	
	// choose file
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
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return FileBase64;
	}
	
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
		
	public static String getValueFromConfig(String key) {
		InputStream input = null;
		File f = null;
		try {
			Properties prop = new Properties();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public static int checkConfigStatus(String key) {
		try {
			return Integer.parseInt(getValueFromConfig(key));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
	
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
    				//return Integer.parseInt(prop.getProperty("firefox_config"));
    				prop.setProperty(key, value);
    				input.close();
    	            FileOutputStream fr=new FileOutputStream(configFile);
    	            prop.store(fr, "Properties");
    	            fr.close();
    			}	
    		}	
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return;
    }

	public static void setFirefoxConfig()
    {
		setValueToConfig("firefox_config", "1");
    }
	public static int checkFirefoxConfig()
    {
		return checkConfigStatus("firefox_config");
    }
	public static void setSafariConfig()
    {
		setValueToConfig("safari_config", "1");
    }
	public static int checkSafariConfig()
    {
		return checkConfigStatus("safari_config");
    }
	public static int checkConfigFirtTime()
    {
		return checkConfigStatus("install_config");
    }
	public static void setConfigFirtTime()
    {
		setValueToConfig("install_config", "1");
    }
	public static String getAppVersion()
    {
		return getValueFromConfig("app_version");
    }
	public static String getUpdateUrl()
    {
		return getValueFromConfig("update_url");
    }
	public static String getProviderFoundList()
	{
		return getValueFromConfig(KEY_PROVIDER_FOUND);
	}

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
