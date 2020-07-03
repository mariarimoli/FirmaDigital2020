package com.tokensigning.token;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.lowagie.text.pdf.PdfPKCS7;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.CertificateFilter;
import com.tokensigning.common.LOG;
import com.tokensigning.form.CertificateInfo;

/**
 * @author Tuan Tran
 *
 */
public class PKCS11CryptoToken implements IKeystoreBase {
		
	private final KeyStorePKCS11CryptoToken delegate;	
	
    public PKCS11CryptoToken() throws InstantiationException {		
		delegate = new KeyStorePKCS11CryptoToken();
		
	}
    private TOKEN_STATUS _tkStatus = TOKEN_STATUS.TOKEN_UNINITIALIZED;
    
    public TOKEN_STATUS checkTokenStatus()
    {
    	return this._tkStatus;
    }
	/**
	 * @param providerPkcs11
	 * @param slotLabelValue
	 * @param PIN
	 * @return
	 */
	public Boolean init(String providerPkcs11, String slot, String PIN) {
		if (providerPkcs11 == null) {
        	LOG.write("PKCS11CryptoToken.init", "Missing SHAREDLIBRARY property");
        	return false;
        }
        final File sharedLibrary = new File(providerPkcs11);
        if (!sharedLibrary.isFile() || !sharedLibrary.canRead()) {
        	LOG.write("PKCS11CryptoToken.init", "The shared library file can't be read: " + sharedLibrary.getAbsolutePath());
        }
        
        if (slot == null) {
           LOG.write("PKCS11CryptoToken.init", "Missing " + CryptoTokenHelper.PROPERTY_SLOT + " property");
           return false;
        }
        // properties
        Properties props = new Properties();  
        props.setProperty(CryptoTokenHelper.PROPERTY_SHAREDLIBRARY, providerPkcs11);         
        if (null != PIN)
        {
        	props.setProperty(CryptoTokenHelper.PROPERTY_PIN, PIN);	
        }
        props.setProperty(CryptoTokenHelper.PROPERTY_SLOT, slot);
        
        int randomNum = ThreadLocalRandom.current().nextInt(0, 1001);
        props = CryptoTokenHelper.fixP11Properties(props);
        try {        	
        	delegate.init(props, null, randomNum); 
        	//delegate
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			String err = ExceptionUtils.getRootCauseMessage(e.getCause()); 
			System.out.println(err);
			LOG.write("PKCS11CryptoToken.init", e.getMessage() + err);
			if (err.contains("CKR_PIN_INCORRECT"))
			{
				this._tkStatus = TOKEN_STATUS.PIN_INCORRECT;
			}
			else if (err.contains("CKR_PIN_LOCKED"))
			{
				this._tkStatus =TOKEN_STATUS.TOKEN_LOCKED;	
			}
			else if (err.contains("no such algorithm: PKCS11 for provider"))
			{
				this._tkStatus =TOKEN_STATUS.PROVIDER_NOTFOUND;
			}
			else if (err.contains("CKR_SLOT_ID_INVALID"))
			{
				this._tkStatus =TOKEN_STATUS.SLOT_INVALID;
			}
		}
        return false;
    }
	
	public Boolean getTokenStatus()
	{
		return (delegate.getTokenStatus() == 1);
	}
    
    /**
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
    public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException {
        try {
            return delegate.getPrivateKey(alias);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }
    
    public String getProvider()
    {
    	return delegate.getSignProviderName();
    }
    
    /**
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
    public PublicKey getPublicKey(String alias) throws CryptoTokenOfflineException {
        try {
            return delegate.getPublicKey(alias);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    /**
     * @param providerUsage
     * @return
     */
    public String getProvider(int providerUsage) {
        return delegate.getSignProviderName();
    }

    /**
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
    public Certificate getCertificate(String alias) throws CryptoTokenOfflineException {
        try {
        	KeyStore ks = delegate.getActivatedKeyStore();
        	Certificate cert = ks.getCertificate(alias);
        	return cert;
		} catch (KeyStoreException e) {
			LOG.write("PKCS11CryptoToken.getCertificate", e.getMessage());
		}
    	return null;
    }
    
    /**
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
    public Certificate[] getCertificateChain(String alias) throws CryptoTokenOfflineException {
        try {
        	KeyStore ks = delegate.getActivatedKeyStore();
        	if (null != ks)
        	{
        		Certificate[] certs = ks.getCertificateChain(alias);
            	return certs;
        	}
		} catch (KeyStoreException e) {
			LOG.write("PKCS11CryptoToken.getCertificate", e.getMessage());
		}
    	return null;
    }
    /**
     * @param serialNumber
     * @return
     * @throws CryptoTokenOfflineException
     */
    public String getCertificateAlias(String serialNumber) throws CryptoTokenOfflineException {
        try {
        	Enumeration<String> enumeration = delegate.getAliases();
        	KeyStore ks = delegate.getActivatedKeyStore();
        	if (null != ks)
        	{
        		while(enumeration.hasMoreElements()) {
    				String alias = (String) enumeration.nextElement();
    				Certificate certificate = ks.getCertificate(alias);
    				String serial = ((X509Certificate) certificate)
    						.getSerialNumber().toString(16);
    				if (serial.toLowerCase().equals(serialNumber.toLowerCase())) {
    					return alias;
    				}                
                }	
        	}
		} catch (KeyStoreException e) {
			LOG.write("PKCS11CryptoToken.getCertificate", e.getMessage());
			System.out.println(e);
		}
    	return null;
    }   
    public List<CertificateInfo> getAllCertificateInfo() throws CryptoTokenOfflineException
    {
    	List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
    	try {
        	Enumeration<String> enumeration = delegate.getAliases();
        	KeyStore ks = delegate.getActivatedKeyStore();
        	if (null != ks)
        	{
        		while(enumeration.hasMoreElements()) {
    				String alias = (String) enumeration.nextElement();
    				Certificate certificate = ks.getCertificate(alias);
    				
    				String subjectCN = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("CN");
    				String serial = ((X509Certificate) certificate)
    						.getSerialNumber().toString(16);
    				String issuer = PdfPKCS7.getIssuerFields((X509Certificate) certificate).getField("CN");
    				String base64 = null;
    				try {
						base64 = Base64Utils.base64Encode(certificate.getEncoded());
					} catch (CertificateEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    				String notAfter = df.format(((X509Certificate) certificate).getNotAfter());
    				String notBefore = df.format(((X509Certificate) certificate).getNotBefore());
    				
    				CertificateInfo cert = new CertificateInfo(subjectCN, serial, base64, issuer, notAfter, notBefore);
    				certs.add(cert);
                }	
        	}
		} catch (KeyStoreException e) {
			LOG.write("PKCS11CryptoToken.getCertificate", e.getMessage());
			System.out.println(e);
		}
    	return certs;
    }
    public void logoutSession()
    {
    	try
    	{
    		delegate.deactivate();
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
           
   	/**
	 * @author Tuan Tran
	 *
	 */
	private static class KeyStorePKCS11CryptoToken extends
			org.cesecore.keys.token.PKCS11CryptoToken {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8838194745550979281L;

		public KeyStorePKCS11CryptoToken() throws InstantiationException {
			super();
		}

		public KeyStore getActivatedKeyStore()
				throws CryptoTokenOfflineException {
			try {
				return getKeyStore();
			} catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
				throw new CryptoTokenOfflineException(ex);
			}
		}
	}

	@Override
	public List<CertificateInfo> getAllCertificateInfo(CertificateFilter certFilter)
			throws CryptoTokenOfflineException {
		if (certFilter == null)
			return getAllCertificateInfo();
		return null;
	}
}
