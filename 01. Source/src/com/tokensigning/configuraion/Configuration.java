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
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.form.PINVerification;
import com.tokensigning.utils.LanguageOption;
import com.tokensigning.utils.Utils;

public class Configuration {
	private final static String SSL_CA_SERIAL = "5cd3818b";
	
	private final static String killFirefoxProcess = "killall -9 \"firefox\"";
	private final static String killSafariProcess = "killall -9 \"Safari\"";
	private final static String killChromeProcess = "killall -9 \"Google Chrome\"";
	public  static final String ICON_WARNING= "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAASbSURBVFiFxZdtTJVlGMd/1/2cl+cgHOSgCKjTlGFEFDrUGAIBQ8DICeopRXHK8syXWetbH3RuuvVFp261am2t1Rc3+yK51cS5UEcvY9ZW0idbrJjhS04BhfN29eEArgx4Dtj6f7xf/tfvvq77fp77FlXl/5RruhM3bg/laDTccOtmX0Qi5sKlS1/emI6PJJuB9aFQimcw8plCA0D/jd8THcoXw2n2pu729gf/GUAwGPTF3KnngOq56bBqmZDlvc+ZzkGu98cALsaHBxu7uroeOvVMqgRRz6xDolQ/lS0c3m5ItQFm0/BCKvuO/8FPfbFq4007BLzl1NNxBja1tC1RtMcyeE+ELObP+Xv/3b4hmt6+TTTOiFj6zOWOjl+c+BqnpKp6DPA2rJTHggNkZKfwymoPgFejcsypryOA5q27qhGa/CkQrJhgiiW01ftJ9wkITWU1ddVPBCAYDFpGOAmwpcowy060D4fh9FdxPmi/x8ORRBk9gRT2vOgdNdaTwWDQmjFA3J26W6Fo8TyhdrmMt3dcVc5cVk5ffMDZKwOJRkt4qSyNvCyDKkV9d+7unhFAY0tLhsIRgF11gjyKz683E6s2xuJ6X2S8XdJ9vLk2kSZROVJe3pgxbQAb72Egs7RAKFwkkw19JEt4rmAWVctcAJm4w4enBdD86msFCnvdLthR6/iwJOS32V9j43GBwt7SytqCpAHEip0AXBtKhbnpycXHMsyb72PrKg+Ay2XJiaQAmrftagTqMv3QVJbk6sfkt9lW6iUrTVCoK6+qa3QEEAqF3KJyHGB7jcHrnl58LIMdsNlTmTiWKnq8pKTkMbfHAG4PRQ+A5i9bIJQ/63DjTSS/TW2hm6L5FkC+Ly1wYFKA5tbWLFQPCtBWP8PgAJYBv83rNV4EQDhYVleXNSGAibqPAulVxcLSnMkBvGP/UVVszyRj/TZP51isK3KjkG4iHP1XgKaWncUq2ubzQEvV1BuvuliYZYPHFWNdaerEA0ezEKrwkOIRFG0rq1pbPNY9fh8wcAowm8oNsyfxG1NervDhGxaqOZNnAMBvE7g/zI5SN+91ho0Ip4DK0bjQ3LJzM0hFdgAaVzuvvdfN1MFhPAvBEg8LMgxARXnN2s0AVm9vrx2OSTswe/96w8K5zgDuDcG7n8c5d+UOK/K9+LxTlM1jYQ2OkO0XLvwcBVj16ccfvW8GwqYVWPT8EmFlvvPVn7+qdPUo3/REOXt5cOoJo1lYk+di5WILYFFUPK1GVesBNq5J7tjl5YIAqnEKFnucTUpLfJR2lCbGq2q9C6HSCOQvSA5g+VLhnX0WkWgWC7Mc3m1dFhihMNfCCMSh0iD0xxV6epN/IWUHcB4cIBKDuPL9bzHiCiL0G1HtBDj7tTISmcphhhoYZjiinP4uDIDG6ZSXt+ye45bINSDL54XKIiE303k5HgxNvQEDPojcH+Fab5jz16IMhRXgplsjhaKqbNi2s8il8olC8VRm/9T40yw5/RCPaWtXZ8eP4w+TUCjkvjUQXY3oCkECTlxU1XWrvy/qNGpc+dNo/OrDgbvfdnd3R2Aaj9Mnrb8A0TtykI+7cqgAAAAASUVORK5CYII=";
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
						.equals(SSL_CA_SERIAL)) {
					return true;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void ExecuteCmd(String cmd) {
		try {
			String[] argss = new String[] { "/bin/bash", "-c", cmd };
			Process proc = new ProcessBuilder(argss).start();
			proc.waitFor();
		} catch (Exception ex) {
			LOG.write("Main", ex.getMessage());
		}
	}

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

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
	//
	public static void FFConfig() {
		try {
			if (Utils.checkFirefoxConfig() != 1)
			{
				LOG.write("Main", "Configuring Firefox Browser");
				String libraryPath = System.getProperty("user.home");
				// /Users/trantuan/Library/Application
				// Support/Firefox/Profiles/nd6j60vh.default
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
								LOG.write("Main", "Configuring db");
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
			LOG.write("Main", ex.getMessage());
		}
	}
	public static void SafariConfig() {
		try {
			String certCAPath = Paths.get(com.tokensigning.utils.Utils.getCertificateFolder(), "localhost.cer").toString();		
			// config Safari and Chrome
			LOG.write("Main", "Configuring Chrome and Safari Browser");
			if (!Configuration.checkCACert())
			{
				final JFrame frame = new JFrame("Configuration");		
				frame.setAlwaysOnTop(true);
				ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
				int result = JOptionPane.showConfirmDialog(frame, LanguageOption.CLOSE_BROWSER, LanguageOption.WARNING_CAPTION,
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
				
				if (result == JOptionPane.OK_OPTION) 
				{
					ExecuteCmd(killSafariProcess);
					ExecuteCmd(killChromeProcess);
				}
				File fCertCA = new File(certCAPath);
				if (!fCertCA.exists() || fCertCA.isDirectory()) {
					LOG.write("Main", "Not found trusted ca: " + certCAPath);
					PINVerification.showErrorMessage(LanguageOption.WARNING_INSTALL_FALIED);
					return;
				}
				//TODO: check cert co trong keychain khong truoc khi thuc hien cau lenh
				String commandAddLogin = "security add-trusted-cert -r trustRoot -k \"$HOME/Library/Keychains/login.keychain-db\" \""
						+ fCertCA + "\"";
				Configuration.ExecuteCmd(commandAddLogin);				
			}
		} catch (Exception ex) {
			LOG.write("Main", ex.getMessage());
		}
	}
	//
	public static Boolean checkFirefoxRunning() {
		return checkBrowserRunning("firefox", "firefox.app");
	}
	
	public static Boolean checkSafariRunning() {
		return checkBrowserRunning("Safari", "safari.app");
	}
	
	public static Boolean checkChromeRunning() {
		return checkBrowserRunning("Google Chrome", "google chrome.app");
	}
	
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
	public static void showWarningFirefoxRunning()
	{
		final JFrame frame = new JFrame("Configuration");	
		frame.setAlwaysOnTop(true);
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
		int result = JOptionPane.showConfirmDialog(frame, LanguageOption.CLOSE_FIREFOX, LanguageOption.WARNING_CAPTION,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		
		if (result == JOptionPane.OK_OPTION) 
		{
			ExecuteCmd(killFirefoxProcess);			
		}
	}
	public String unobfuscate(String s) {
		String key = "Zx" + Math.log(2) / 3;
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = (char) (s.charAt(i) - key.charAt(i % key.length()));
        }
        return new String(result);
    }
    final static int feistelRounds = 4;
    final static int randRounds = 4;
    final static int seed = 12345;
    private static int f (int x) {
        final int a = 12+1;
        final int c = 1361423303;
        x = (x + seed) % mod;
        int r = randRounds;
        while (r-- != 0) {
            x = (a*x+c) % mod;
        }
        return x;
    }
    final static int mod = 60466176;
	public static int illuminate (String s) {
        int a = Integer.valueOf(s.substring(0,5),36);
        int b = Integer.valueOf(s.substring(5,10),36);
        int r = feistelRounds;
        while (r-- != 0) {
            b = (b - f(a)) % mod;
            a = (a - f(b)) % mod;
        }
        // make the modulus positive:
        a = (a + mod)%mod;
        b = (b + mod)%mod;

        return a*mod+b;
    }
	public static String password(String s) {
        int iPass = illuminate(s);
        char[] passExtend = {'I', 'V', 'A', 'N', '2', '0', '1', '8', 'a', '@', 'A'};
        String pwd = Integer.toString(iPass).concat(String.valueOf(passExtend));
        return pwd;
    }
	
	//
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
	public static void ShowRestartBrowserMacOS()
	{
		if (checkSafariRunning() || checkChromeRunning()
				|| checkFirefoxRunning()) {
			final JFrame frame = new JFrame("Configuration");
			frame.setAlwaysOnTop(true);
			ImageIcon icon = new ImageIcon(
					Base64Utils.base64Decode(ICON_WARNING));
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
	public static void ShowRestartBrowserWindows() {
		
	}
	public static void ShowRetartBrowserLinux() {
		
	}

	//
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
