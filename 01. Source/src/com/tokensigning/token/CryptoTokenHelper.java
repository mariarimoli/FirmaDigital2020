package com.tokensigning.token;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.cesecore.keys.token.p11.Pkcs11SlotLabelType;

import com.tokensigning.common.OSType;
import com.tokensigning.utils.Utils;

public class CryptoTokenHelper {

	public static final int MAX_SLOT = 10;

	public static final String PROPERTY_ATTRIBUTESFILE = "ATTRIBUTESFILE";
	public static final String PROPERTY_ATTRIBUTES = "ATTRIBUTES";
	public static final String PROPERTY_SLOTLISTINDEX = "SLOTLISTINDEX";
	public static final String PROPERTY_SLOT = "SLOT";
	public static final String PROPERTY_SHAREDLIBRARY = "SHAREDLIBRARY";
	public static final String PROPERTY_PIN = "PIN";
	public static final String PROPERTY_DEFAULTKEY = "DEFAULTKEY";
	public static final String PROPERTY_AUTHCODE = "AUTHCODE";
	public static final String PROPERTY_SLOTLABELTYPE = "SLOTLABELTYPE";
	public static final String PROPERTY_SLOTLABELVALUE = "SLOTLABELVALUE";
	
	public static final String PROVIDER_FOLDER = "/usr/local/lib";
	
	
	public static String PKCS11_PROVIDER_LIST = "vnptca_p11_v8.dll";

	/**
	 * @param props
	 * @return
	 */
	public static Properties fixP11Properties(final Properties props) {
		String prop = props.getProperty(PROPERTY_AUTHCODE);
		if (prop != null) {
			props.setProperty("authCode", prop);
		}
		prop = props.getProperty(PROPERTY_DEFAULTKEY);
		if (prop != null) {
			props.setProperty("defaultKey", prop);
		}
		prop = props.getProperty(PROPERTY_PIN);
		if (prop != null) {
			props.setProperty("pin", prop);
		}
		prop = props.getProperty(PROPERTY_SHAREDLIBRARY);
		if (prop != null) {
			props.setProperty("sharedLibrary", prop);
		}
		prop = props.getProperty(PROPERTY_SLOTLABELVALUE);
		if (prop != null) {
			props.setProperty(
					org.cesecore.keys.token.PKCS11CryptoToken.SLOT_LABEL_VALUE,
					prop);
		}
		prop = props.getProperty(PROPERTY_SLOT);
		if (prop != null) {
			props.setProperty("slot", prop);
			props.setProperty(PROPERTY_SLOTLABELTYPE,
					Pkcs11SlotLabelType.SLOT_NUMBER.getKey());
			props.setProperty(
					org.cesecore.keys.token.PKCS11CryptoToken.SLOT_LABEL_VALUE,
					prop);
			props.setProperty(PROPERTY_SLOTLABELVALUE, prop);
		}
		prop = props.getProperty(PROPERTY_SLOTLISTINDEX);
		if (prop != null) {
			props.setProperty("slotListIndex", prop);
			props.setProperty(PROPERTY_SLOTLABELTYPE,
					Pkcs11SlotLabelType.SLOT_INDEX.getKey());
			props.setProperty(
					org.cesecore.keys.token.PKCS11CryptoToken.SLOT_LABEL_VALUE,
					prop);
			props.setProperty(PROPERTY_SLOTLABELVALUE, prop);
		}
		prop = props.getProperty(PROPERTY_ATTRIBUTESFILE);
		if (prop != null) {
			props.setProperty("attributesFile", prop);
		}
		
		prop = props.getProperty(PROPERTY_SLOTLABELTYPE);
		if (prop != null) {
			props.setProperty(
					org.cesecore.keys.token.PKCS11CryptoToken.SLOT_LABEL_TYPE,
					prop);
		}
		return props;
	}

	public static int getMaxSlot() {
		try {
			Properties prop = new Properties();

			InputStream input;
			// load a properties file
			String configFile = Utils.getAppConfig();
			File f = new File(configFile.toString());
			if (f.exists() && !f.isDirectory()) {
				input = new FileInputStream(configFile.toString());
				prop.load(input);
				if (null != prop && null != prop.getProperty("max_slot")) {
					return Integer.parseInt(prop.getProperty("max_slot"));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MAX_SLOT;
	}

	public static List<String> getProviderExist() {
		List<String> providerExist = new ArrayList<String>();
		try {
			// load provider found previous
			String providerFound = Utils.getValueFromConfig(Utils.KEY_PROVIDER_FOUND);
			String[] providerFoundArr = providerFound == null ? null : providerFound.split(",");
			if (providerFoundArr != null && providerFoundArr.length  > 0)
			{
				for (String provider : providerFoundArr) {
					String providerPath = IsFileExistInSystem(provider);
					if (null != providerPath) {
						providerExist.add(providerPath);
					}
				}
			}
			// load a properties file
			String providerList = Utils.getValueFromConfig(Utils.KEY_PROVIDER_LIST);
			// get from string
			String[] providerArr = providerList.split(",");
			for (String provider : providerArr) {
				if (providerFoundArr != null && !Arrays.asList(providerFoundArr).contains(provider))
				{
					String providerPath = IsFileExistInSystem(provider);
					if (null != providerPath && !providerExist.contains(providerPath)) {
						providerExist.add(providerPath);
					}
				}
			}			
			
			// get from system
			List<String> listOnSystem = GetDriverOnSystem();
			for (String path : listOnSystem) {
				if (!providerExist.contains(path)) {
					providerExist.add(path);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return providerExist;
	}
	
	public static String IsFileExistInSystem(String providerName) {
		if (providerName == null || providerName.isEmpty())
		{
			return null;
		}
		OSType osType = Utils.getOperatingSystemType();
		switch (osType) {
		case Windows:
			Path path = Paths.get(System.getenv("WINDIR"), "system32",
					providerName + ".dll");
			File f = new File(path.toString());
			if (f.exists() && !f.isDirectory()) {
				return path.toString();
			}
		case Linux:
		case MacOS:
			Path path2 = Paths.get(PROVIDER_FOLDER, "lib" + providerName
					+ ".dylib");
			File f2 = new File(path2.toString());
			if (f2.exists() && !f2.isDirectory()) {
				return path2.toString();
			}
			path2 = Paths.get(PROVIDER_FOLDER, providerName + ".dylib");
			f2 = new File(path2.toString());
			if (f2.exists() && !f2.isDirectory()) {
				return path2.toString();
			}
		
		default:
			return null;
		}
	}

	public static List<String> GetDriverOnSystem() {
		OSType osType = Utils.getOperatingSystemType();
		switch (osType) {
		case Windows:
			return GetDriverOnWindows();
		case MacOS:
			return GetDriverOnMacOS();
		case Linux:
			return GetDriverOnMacOS();
		default:
			return null;
		}
		
	}
	
	
	public static List<String> GetDriverOnMacOS() {
		List<String> providerList = new ArrayList<String>();
		File folder = new File(PROVIDER_FOLDER);
		File[] files = folder.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				if (file.getPath().toString().contains(".dylib")) {
					providerList.add(file.getPath().toString());
				}
			}
		}
		return providerList;
	}
	
	public static List<String> GetDriverOnWindows() {
		List<String> providerList = new ArrayList<String>();
		String folderDriver = System.getenv("WINDIR") + "\\system32";
		File folder = new File(folderDriver);
		File[] files = folder.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				if (file.getPath().toString().contains(".dll")) {
					if (file.getName().toLowerCase().contains("microsoft") 
							|| file.getName().toLowerCase().contains("windows")
							|| file.getName().toLowerCase().contains("terminator"))
					{
						continue;
					}
					providerList.add(file.getPath().toString());
				}
			}
		}
		return providerList;
	}
	
}
