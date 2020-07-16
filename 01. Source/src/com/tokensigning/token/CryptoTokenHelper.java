package com.tokensigning.token;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.cesecore.keys.token.p11.Pkcs11SlotLabelType;

import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.utils.Utils;

/**
* CryptoTokenHelper: pkcs#11 configuration
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class CryptoTokenHelper {

	// Define max slot in computer
	public static final int MAX_SLOT = 5;
	// Attributes temp file init token
	public static final String PROPERTY_ATTRIBUTESFILE = "ATTRIBUTESFILE";
	// Attributes init token
	public static final String PROPERTY_ATTRIBUTES = "ATTRIBUTES";
	// Slot list index
	public static final String PROPERTY_SLOTLISTINDEX = "SLOTLISTINDEX";
	// Slot
	public static final String PROPERTY_SLOT = "SLOT";
	// Pkcs#11 driver
	public static final String PROPERTY_SHAREDLIBRARY = "SHAREDLIBRARY";
	// Usb token PIN
	public static final String PROPERTY_PIN = "PIN";
	// Key alias
	public static final String PROPERTY_DEFAULTKEY = "DEFAULTKEY";
	// Authen code
	public static final String PROPERTY_AUTHCODE = "AUTHCODE";
	// Slot type: index, label
	public static final String PROPERTY_SLOTLABELTYPE = "SLOTLABELTYPE";
	// Slot label value
	public static final String PROPERTY_SLOTLABELVALUE = "SLOTLABELVALUE";
	// Folder store Pkcs#11 drivers in MacOS
	public static final String PROVIDER_FOLDER = "/usr/local/lib";
	// Temporary pkcs#11 driver
	public static String PKCS11_PROVIDER_LIST = "vnptca_p11_v8.dll";

	/**
	 * Fix pkcs#11 properties
	 * @param props is pkcs#11 properties
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
	/**
	 * Get config max slot
	 * @return
	 */
	public static int getMaxSlot() {
		String maxSlot = Utils.getValueFromConfig("max_slot");
		if (maxSlot == null || maxSlot.isEmpty())
		{
			return MAX_SLOT;
		}
		return Integer.parseInt(maxSlot);
	}

	/**
	 * Get all pkcs#11 driver on system 
	 * @return list pkcs#11 driver
	 */
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
			LOG.write("CryptoTokenHelper", e.getMessage());
		}
		return providerExist;
	}
	
	/**
	 * Check driver is exist on system
	 * @return driver full path
	 */
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

	/**
	 * Get drivers is exist on system
	 * @return list pkcs#11 driver
	 */
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
	
	/**
	 * Get drivers is exist on system MacOS
	 * @return list pkcs#11 driver
	 */
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
	
	/**
	 * Get drivers is exist on system Windows
	 * @return list pkcs#11 driver
	 */
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
