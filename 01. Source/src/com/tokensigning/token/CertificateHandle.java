package com.tokensigning.token;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.pdf.PdfPKCS7;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.CertificateFilter;
import com.tokensigning.common.Connector;
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.form.CertificateChooser;
import com.tokensigning.form.CertificateInfo;
import com.tokensigning.form.PINVerification;
import com.tokensigning.utils.Utils;
/**
* CertificateHandle: handle get certificate
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class CertificateHandle {
	
	/**
     * Constructor
     *
     */
	public CertificateHandle() {
	}

	// Object interact with usb token by pkcs11 standard
	private PKCS11CryptoToken _token;
	// PIN of Usb token
	private String PIN;
	// Usb token Pkcs#11 driver 
	private String pkcs11Provider;
	// Usb token slot
	private String slot;

	public String getPIN() {
		return PIN;
	}

	public void setPIN(String pIN) {
		PIN = pIN;
	}

	public String getPkcs11Provider() {
		return pkcs11Provider;
	}

	public void setPkcs11Provider(String pkcs11Provider) {
		this.pkcs11Provider = pkcs11Provider;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	/**
     * get object PKCS11CryptoToken
     *
     * @param 
     * @return Pkcs11 crypto token 
     */
	public PKCS11CryptoToken getPkcs11Token() {
		if (TOKEN_STATUS.TOKEN_AVAILABLE == checkLogin()) {
			return this._token;
		} else {
			return initToken();
		}
	}

	/**
     * init object PKCS11CryptoToken
     *
     * @param 
     * @return Pkcs11 crypto token 
     */
	public PKCS11CryptoToken initToken() {
		try {
			// PKCS11
			List<String> providersList = CryptoTokenHelper.getProviderExist();

			String PIN = null;
			boolean iBreakFor = false;

			if (providersList.size() > 0) {
				Boolean tkInit = false;
				for (String provider : providersList) {
					for (int slot = 0; slot < CryptoTokenHelper.getMaxSlot(); slot++)
					// for (int slot = 0; slot < 3; slot++)
					{						
						PKCS11CryptoToken token = new PKCS11CryptoToken();
						LOG.write("CertificateHandle.initToken", "Provider: "
								+ provider + ". Slot: " + slot);
						tkInit = token.init(provider, Integer.toString(slot),
								PIN);
						if (tkInit) {
							this.pkcs11Provider = provider;
							this.slot = Integer.toString(slot);
							int iCount = 0;
							do {
								if (null == PIN || PIN.isEmpty()) { 																	
									PIN = PINVerification.show();
								}
								if (null == PIN || PIN.isEmpty()) { 
									return null;
								}
								this.PIN = PIN;
								TOKEN_STATUS tokenStatus = checkLogin();
								if (TOKEN_STATUS.PIN_INCORRECT == tokenStatus) {
									PINVerification.showPINIncorrect();
									PIN = null;
								} else if (TOKEN_STATUS.TOKEN_LOCKED == tokenStatus) {
									PINVerification.showTokenLocked();
									return null;
								} else if (TOKEN_STATUS.PROVIDER_NOTFOUND == tokenStatus) {
									iBreakFor = true;
									break;
								} else {
									PKCS11CryptoToken tokenTmp = new PKCS11CryptoToken();
									tkInit = tokenTmp.init(this.pkcs11Provider,
											this.slot, this.PIN);
									if (!tokenTmp.getTokenStatus()) {
										tkInit = false;
										break;
									} else {
										// /tkInit = tokenTmp.init(provider,
										// Integer.toString(slot), PIN);
										// Save Provider
										Utils.setProviderFoundList(this.pkcs11Provider);
										this._token = tokenTmp;
										return this._token;
									}
								}
								iCount++;
							} while (iCount < 5);
							if (iCount > 4) {
								PINVerification.showNumberOfPinInputInvalid();
							}
							if (iBreakFor) {
								iBreakFor = false;
								break;
							}
						}
					}
				}
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
			LOG.write("CertificateHandle.initToken", e.getMessage());
		}
		return null;
	}

	/**
     * Get user's certificate information
     * Windows OS: get certificate from windows key store use class WindowsKeystore
     * MacOS + Linux: get certificate directly from Usb Token by Pkcs#11 standard use class PKCS11CryptoToken 
     *
     * @param certFilter is conditions to filter certificate
     * @return Certificate information
     */
	public String getCertificateInfo(CertificateFilter certFilter) {
		String serialNumber = null;
		List<CertificateInfo> certInfo = null;
		
		OSType osType = Utils.getOperatingSystemType();
		IKeystoreBase token = null;
		if (osType == OSType.Windows)
		{
			token = new WindowsKeystore();
		}
		else
		{
			if (TOKEN_STATUS.TOKEN_AVAILABLE != checkLogin()) {
				initToken();
			}
			
			if (!checkTokenInit()) {
				return null;
			}
			token = this._token;
		}
		
		try {
			certInfo = token.getAllCertificateInfo(certFilter);
			if (certInfo != null && certInfo.size() > 0) {
				serialNumber = CertificateChooser.show(certInfo);
			}
		} catch (CryptoTokenOfflineException e) {
			LOG.write(CertificateHandle.class.toString(), e.getMessage());
		}
		if (null != serialNumber) {
			for (CertificateInfo cert : certInfo) {
				if (cert.getSerial().equals(serialNumber)) {
					Gson gson = new GsonBuilder().disableHtmlEscaping().create();
					String info = gson.toJson(cert);
					return info;
				}
			}
		}
		return null;
	}

	/**
     * check token login or not use Pkcs#11 standard
     *
     * @param 
     * @return TOKEN_STATUS
     */
	public TOKEN_STATUS checkLogin() {
		File tmpConfigFile;
		try {
			if (null == this.PIN || null == this.pkcs11Provider
					|| null == this.slot) {
				return TOKEN_STATUS.TOKEN_UNINITIALIZED;
			}
			tmpConfigFile = File.createTempFile("pkcs11-", "conf");
			tmpConfigFile.deleteOnExit();
			PrintWriter configWriter = new PrintWriter(new FileOutputStream(
					tmpConfigFile), true);
			configWriter.println("name=SmartCard");
			configWriter.println("library=" + this.pkcs11Provider);
			configWriter.println("slot=" + this.slot);
			Provider provider = new sun.security.pkcs11.SunPKCS11(
					tmpConfigFile.getAbsolutePath());
			Security.addProvider(provider);
			KeyStore keyStore = KeyStore.getInstance("PKCS11", provider);
			keyStore.load(null, this.PIN.toCharArray());
			// ((SunPKCS11) provider).logout();
			configWriter.close();
			return TOKEN_STATUS.TOKEN_AVAILABLE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String err = ExceptionUtils.getRootCauseMessage(e.getCause());
			LOG.write("CertificateHandle.checkLogin", e.getMessage() + err);
			if (err.contains("CKR_PIN_INCORRECT")) {
				return TOKEN_STATUS.PIN_INCORRECT;
			} else if (err.contains("CKR_PIN_LOCKED")) {
				return TOKEN_STATUS.TOKEN_LOCKED;
			} else if (err.contains("no such algorithm: PKCS11 for provider")) {
				return TOKEN_STATUS.PROVIDER_NOTFOUND;
			}
		}
		return TOKEN_STATUS.TOKEN_NOTFOUND;
	}

	/**
     * check token init or not usb Pkcs#11 standard
     *
     * @param 
     * @return true or false
     */
	public Boolean checkTokenInit() {
		PKCS11CryptoToken token = null;
		try {
			token = new PKCS11CryptoToken();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		if (null == this._token || !this._token.getTokenStatus()
				|| null == token
				|| !token.init(this.pkcs11Provider, this.slot, this.PIN)) {
			return false;
		}
		return true;
	}

	/**
     * show certificate form for chooser
     *
     * @param connect is conditions to filter certificate
     * @return certificate info
     */
	public static String showCertificateViewer(Connector connect) {
		if (connect.getArgs().length == 0) {
			return "0";
		}
		CertificateFactory certFactory;
		try {
			certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(
					Base64Utils.base64Decode(connect.getArgs()[0]));
			X509Certificate certificate = (X509Certificate) certFactory
					.generateCertificate(in);
			String subjectCN = PdfPKCS7.getSubjectFields(certificate).getField(
					"CN");
			String serialNumber = certificate.getSerialNumber().toString(16);
			CertificateInfo certInfo = new CertificateInfo(subjectCN,
					serialNumber);
			List<CertificateInfo> certList = new ArrayList<CertificateInfo>();
			certList.add(certInfo);
			// show
			Thread t1 = new Thread() {
				public void run() {
					CertificateChooser.show(certList);
				}
			};
			t1.start();
			return "1";
		} catch (CertificateException e) {			
			e.printStackTrace();
			LOG.write("CertificateHandle.showCertificateViewer", e.getMessage());
		}
		return "0";
	}

	/**
     * get certificate with specify serial number use Pkcs#11 standard
     *
     * @param serialNumber is condition to filter certificate
     * @return Certificate
     */
	public Certificate getCertificateBySerial(String serialNumber) {
		PKCS11CryptoToken token = getPkcs11Token();
		try {
			if (null != token) {
				String alias = token.getCertificateAlias(serialNumber);
				if (null != alias) {
					Certificate cert = token.getCertificate(alias);
					return cert;
				}
			}
		} catch (Exception ex) {
			LOG.write("CertificateHandle.getCert", ex.getMessage());
		}
		return null;
	}

	/**
     * get certificate with specify serial number use Pkcs#11 standard
     *
     * @param serialNumber is condition to filter certificate
     * @return X509Certificate
     */
	public X509Certificate getX509CertificateBySerial(String serialNumber) {
		return (X509Certificate) getCertificateBySerial(serialNumber);
	}

	/**
     * check usb token plugged or not use Pkcs#11 standard
     *
     * @param 
     * @return result
     */
	public static int checkToken() {
		List<String> providersList = CryptoTokenHelper.getProviderExist();
		if (providersList.size() > 0) {
			Boolean tkInit = false;
			for (String provider : providersList) {
				try {

					for (int slot = 0; slot < CryptoTokenHelper.getMaxSlot(); slot++) {
						PKCS11CryptoToken token = new PKCS11CryptoToken();
						tkInit = token.init(provider, Integer.toString(slot),
								null);
						token.logoutSession();
						if (tkInit) {
							return 1;
						}
					}

				} catch (Exception ex) {
					LOG.write("CertHandle.checkToken", ex.getMessage());
					return -1;
				}
			}
		} else {
			LOG.write("CertHandle.checkToken", "Not found token provider");
			return -2;
		}
		return 0;
	}
}
