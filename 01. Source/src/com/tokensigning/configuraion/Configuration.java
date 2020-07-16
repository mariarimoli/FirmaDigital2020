package com.tokensigning.configuraion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.IconLoader;
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.form.PINVerification;
import com.tokensigning.utils.LanguageOption;
import com.tokensigning.utils.Utils;


/**
* Configuration: config environment when install or run TokenSigning
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class Configuration {
	private final static String killFirefoxProcess = "killall -9 \"firefox\"";
	private final static String killSafariProcess = "killall -9 \"Safari\"";
	private final static String killChromeProcess = "killall -9 \"Google Chrome\"";
		
	/**
     * Check CA ssl certificate is trusted in MacOS 
     *
     * @param 
     * @return 
     */
	public static Boolean checkCACert() {
		try {
			KeyStore ks = KeyStore.getInstance("KeychainStore");
			ks.load(null);
			Enumeration<String> enumeration = ks.aliases();
			// Enumeration enumeration = ks.aliases();
			while (enumeration.hasMoreElements()) {
				String alias = (String) enumeration.nextElement();
				Certificate certificate = ks.getCertificate(alias);
				X509Certificate x509 = (X509Certificate) certificate;
				if (x509.getSerialNumber().toString(16).trim().toLowerCase()
						.equals(Utils.getCertSslSerial())) {
					return true;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			LOG.write("Configuration", e.getMessage());
		} catch (CertificateException e) {
			LOG.write("Configuration", e.getMessage());
		} catch (IOException e) {
			LOG.write("Configuration", e.getMessage());
		} catch (KeyStoreException e) {
			LOG.write("Configuration", e.getMessage());
		}
		return false;
	}

	/**
     * Execute command line in MacOS 
     *
     * @param command line
     * @return 
     */
	public static void ExecuteCmd(String cmd) {
		try {
			String[] argss = new String[] { "/bin/bash", "-c", cmd };
			Process proc = new ProcessBuilder(argss).start();
			proc.waitFor();
		} catch (Exception ex) {
			LOG.write("Main", ex.getMessage());
		}
	}

	/**
     * get md5 hash of file 
     *
     * @param command line
     * @return 
     */
	 //reference: https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	/**
     * get md5 hash of file 
     *
     * @param command line
     * @return 
     */
	// reference: https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
     * FFConfig: add ssl certificate into firefox' trusted store 
     *
     * @param 
     * @return 
     */
	public static void FFConfig() {
		try {
			if (Utils.checkFirefoxConfig() != 1)
			{
				LOG.write("FFConfig", "Configuring Firefox Browser");
				String libraryPath = System.getProperty("user.home");
				Path path = Paths.get(libraryPath, "Library",
						"Application Support", "Firefox", "Profiles");
				File[] directories = new File(path.toString())
						.listFiles(File::isDirectory);
				for (File profielFolder : directories) {
					if (profielFolder.getName().contains("default")) {
						String dbFile = Paths.get(profielFolder.getAbsolutePath(),
								"cert9.db").toString();
						String dbConfigFile = Utils.CONFIG_FOLDER_MACOS
								+ "/cert9.db";
						String key4 = Utils.CONFIG_FOLDER_MACOS + "key4.db";
						String key4Default = Paths.get(
								profielFolder.getAbsolutePath(), "key4.db")
								.toString();
						File dbDefault = new File(dbFile);
						if (dbDefault.exists()) {
							String dbFileSum = getMD5Checksum(dbFile);
							String dbConfigFileSum = getMD5Checksum(dbConfigFile);
							if (!dbFileSum.equals(dbConfigFileSum)) {
								LOG.write("FFConfig", "Configuring db");
								if (checkFirefoxRunning()) {
									showWarningFirefoxRunning();
								}
								Files.copy(Paths.get(dbConfigFile),
										Paths.get(dbFile),
										StandardCopyOption.REPLACE_EXISTING);
								Files.copy(Paths.get(key4), Paths.get(key4Default),
										StandardCopyOption.REPLACE_EXISTING);
							}
						} else {
							LOG.write("Main", "Configuring db");
							if (checkFirefoxRunning()) {
								showWarningFirefoxRunning();
							}
							Files.copy(Paths.get(dbConfigFile), Paths.get(dbFile),
									StandardCopyOption.REPLACE_EXISTING);
							Files.copy(Paths.get(key4), Paths.get(key4Default),
									StandardCopyOption.REPLACE_EXISTING);
						}
						Utils.setFirefoxConfig();
						break;
					}
				}
			}
		} catch (Exception ex) {
			LOG.write("FFConfig", ex.getMessage());
		}
	}
	
	/**
     * SafariConfig: add ssl certificate into Chrome and Safari' trusted store 
     *
     * @param 
     * @return 
     */
	public static void SafariConfig() {
		try {
			String certCAPath = Paths.get(com.tokensigning.utils.Utils.getCertificateFolder(), "localhost.cer").toString();	
			LOG.write("SafariConfig", "Configuring Chrome and Safari Browser");
			if (!Configuration.checkCACert())
			{
				final JFrame frame = new JFrame("Configuration");		
				frame.setAlwaysOnTop(true);
				ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
				int result = JOptionPane.showConfirmDialog(frame, LanguageOption.CLOSE_BROWSER, LanguageOption.WARNING_CAPTION,
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
				
				if (result == JOptionPane.OK_OPTION) 
				{
					ExecuteCmd(killSafariProcess);
					ExecuteCmd(killChromeProcess);
				}
				File fCertCA = new File(certCAPath);
				if (!fCertCA.exists() || fCertCA.isDirectory()) {
					LOG.write("SafariConfig", "Not found trusted ca: " + certCAPath);
					PINVerification.showErrorMessage(LanguageOption.WARNING_INSTALL_FALIED);
					return;
				}
				//TODO: check cert co trong keychain khong truoc khi thuc hien cau lenh
				String commandAddLogin = "security add-trusted-cert -r trustRoot -k \"$HOME/Library/Keychains/login.keychain-db\" \""
						+ fCertCA + "\"";
				Configuration.ExecuteCmd(commandAddLogin);				
			}
		} catch (Exception ex) {
			LOG.write("SafariConfig", ex.getMessage());
		}
	}

	/**
     * Check firefox is running
     *
     * @param 
     * @return 
     */
	public static Boolean checkFirefoxRunning() {
		return checkBrowserRunning("firefox", "firefox.app");
	}
	
	/**
     * Check Safari is running
     *
     * @param 
     * @return 
     */
	public static Boolean checkSafariRunning() {
		return checkBrowserRunning("Safari", "safari.app");
	}
	
	/**
     * Check Chrome is running
     *
     * @param 
     * @return 
     */
	public static Boolean checkChromeRunning() {
		return checkBrowserRunning("Google Chrome", "google chrome.app");
	}
	
	/**
     * Check app is running
     *
     * @param 
     * @return 
     */
	public static Boolean checkBrowserRunning(String name, String appName) {
		try {
			String getPID = "ps aux|grep \"" + name+  "\"";
			String[] argss = new String[] { "/bin/bash", "-c", getPID };
			Process proc = new ProcessBuilder(argss).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().contains(appName)) {
					return true;
				}
			}
			proc.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
     * Show warning
     *
     * @param 
     * @return 
     */
	public static void showWarningFirefoxRunning()
	{
		final JFrame frame = new JFrame("Configuration");	
		frame.setAlwaysOnTop(true);
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
		int result = JOptionPane.showConfirmDialog(frame, LanguageOption.CLOSE_FIREFOX, LanguageOption.WARNING_CAPTION,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		
		if (result == JOptionPane.OK_OPTION) 
		{
			ExecuteCmd(killFirefoxProcess);			
		}
	}
	
	/**
     * Show warning restart browser
     *
     * @param 
     * @return 
     */
	public static void ShowRestartBrowserRequired() {
		if (Utils.checkConfigFirtTime() != 1) {
			OSType osType = Utils.getOperatingSystemType();
			
			switch (osType) {
			case Windows:
				ShowRestartBrowserWindows();
				break;
			case MacOS:
				ShowRestartBrowserMacOS();
				break;
			case Linux:
				ShowRetartBrowserLinux();
				break;
			default:
				break;
			}
		}
	}
	
	/**
     * Show warning restart browser
     *
     * @param 
     * @return 
     */
	public static void ShowRestartBrowserMacOS()
	{
		if (checkSafariRunning() || checkChromeRunning()
				|| checkFirefoxRunning()) {
			final JFrame frame = new JFrame("Configuration");
			frame.setAlwaysOnTop(true);
			ImageIcon icon = new ImageIcon(
					Base64Utils.base64Decode(IconLoader.getIconWarning()));
			int result = JOptionPane.showConfirmDialog(frame,
					LanguageOption.CLOSE_BROWSER, LanguageOption.WARNING_CAPTION,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, icon);

			if (result == JOptionPane.OK_OPTION) {
				ExecuteCmd(killFirefoxProcess);
				ExecuteCmd(killSafariProcess);	
				ExecuteCmd(killChromeProcess);
			} else {
				JOptionPane
						.showConfirmDialog(
								frame,
								LanguageOption.CLOSE_BROWSER,
								LanguageOption.WARNING_CAPTION, JOptionPane.OK_OPTION,
								JOptionPane.INFORMATION_MESSAGE, icon);
			}
			Utils.setConfigFirtTime();
		}
	}
	/**
     * Show warning restart browser
     *
     * @param 
     * @return 
     */
	public static void ShowRestartBrowserWindows() {
		
	}
	public static void ShowRetartBrowserLinux() {
		
	}

	/**
     * Config browser with OS
     *
     * @param 
     * @return 
     */
	public static void ConfigBrowsers() {
		OSType osType = Utils.getOperatingSystemType();
		
		switch (osType) {
			case Windows:
				ConfigBrowsersWindows();
				break;
			case MacOS:
				ConfigBrowsersMac();
				break;
			case Linux:
				ConfigBrowsersLinux();
				break;
			default:
				break;
		}
	}
	public static void ConfigBrowsersMac() {
		// Config for Google Chrome, Safari
		SafariConfig();				
		// Config for Firefox
		FFConfig();			
	}
	public static void ConfigBrowsersWindows() {
		
	}
	public static void ConfigBrowsersLinux() {
		
	}
}
