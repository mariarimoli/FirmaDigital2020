package com.tokensigning.token;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tokensigning.common.CertificateFilter;
import com.tokensigning.common.DataResult;
import com.tokensigning.common.LOG;
import com.tokensigning.form.CertificateInfo;

/**
* WindowsKeystore: windows key store
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class WindowsKeystore implements IKeystoreBase{
	private KeyStore keystore = null;
	
	static {
		try
		{
			Security.addProvider((Provider)new BouncyCastleProvider());
		}
		catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
		}
	}
	
	/**
     * Constructor
     *
     */
	public WindowsKeystore() {
		keystore = null;
		loadWindowsKeyStore();
	}
	
	/**
     * Get all key alias in windows key store
     *
     */
	public void getKeyaliase()
	{	
		Enumeration<String> tmpAliases;
		try {
			tmpAliases = keystore.aliases();
			while (tmpAliases.hasMoreElements()) {
				final String tmpAlias = tmpAliases.nextElement();
				System.out.println(tmpAlias);
			}
		} catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
		}
		
	}
	
	/**
     * Get all store type in windows
     *
     */
	public static SortedSet<String> getKeyStores() {
	    Set<String> tmpKeyStores = Security.getAlgorithms("KeyStore");
	    return new TreeSet<String>(tmpKeyStores);
	}
	
	/**
     * Get provider name
     * Not use
     */
	public String getProvider()
    {
    	return null;
    }
	
	/**
     * Get certificates list
     * 
     * @return
     * @throws CryptoTokenOfflineException
     */
	public List<CertificateInfo> getAllCertInfoFromUserstore() throws CryptoTokenOfflineException
	{
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		try {
			final Enumeration<String> tmpAliases = keystore.aliases();
			while (tmpAliases.hasMoreElements()) {
				final String tmpAlias = tmpAliases.nextElement();
				System.out.println(tmpAlias);
				LOG.write("getAllCertificateInfo", tmpAlias);
				Certificate certificate = keystore.getCertificate(tmpAlias);
				CertificateInfo cert = CertificateUtils.getCertificateInfo((X509Certificate)certificate);
				cert.setCertStore(CertificateInfo.USER_STORE);
				certs.add(cert);
			}
		} catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
			throw new CryptoTokenOfflineException(e);
		}
		return certs;
	}
	
	/**
     * Get private key use pkcs#11
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
	public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException  {
        try {
			return (PrivateKey)keystore.getKey(alias, null);
		} catch (UnrecoverableKeyException e) {
			LOG.write("WindowsKeystore", e.getMessage());
			throw new CryptoTokenOfflineException(e);
		} catch (KeyStoreException e) {
			LOG.write("WindowsKeystore", e.getMessage());
			throw new CryptoTokenOfflineException(e);
		} catch (NoSuchAlgorithmException e) {
			LOG.write("WindowsKeystore", e.getMessage());
			throw new CryptoTokenOfflineException(e);
		}
        
    }
	
	/**
     * Load windows key store
     */
	public void loadWindowsKeyStore() {
		try {
			keystore = KeyStore.getInstance("WINDOWS-MY");
			keystore.load(null, null);	
			fixAliases(keystore);
		} catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
		} 
	}
	
	/**
     * Load windows key store
     * 
     * @param keytype is windows key store type
     */
	public void loadWindowsKeyStore(String keytype) {
		try {
			keystore = KeyStore.getInstance(keytype);
			keystore.load(null, null);	
			fixAliases(keystore);
		} catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
		}
	}
	
	 /**
     * Get certificate alias
     * @param serialNumber
     * @return
     * @throws CryptoTokenOfflineException
     */
	public String getCertificateAlias(String serialNumber) throws CryptoTokenOfflineException {
        try {
        	Enumeration<String> enumeration = keystore.aliases();
        	
        	while(enumeration.hasMoreElements()) {
        		String alias = (String) enumeration.nextElement();
        		Certificate certificate = keystore.getCertificate(alias);
				String serial = ((X509Certificate) certificate)
						.getSerialNumber().toString(16);
				if (serial.toLowerCase().equals(serialNumber.toLowerCase())) {
					return alias;
				}    
				    
            }	
		} catch (KeyStoreException e) {
			LOG.write("WindowsKeystore.getCertificate", e.getMessage());
		}
    	return null;
    }   
	
	/**
     * Get certificates chain
     * 
     * @param alias
     * @return
     * @throws CryptoTokenOfflineException
     */
	public Certificate[] getCertificateChain(String alias) throws CryptoTokenOfflineException {
        try {
        	Certificate[] certs = keystore.getCertificateChain(alias);
        	return certs;
		} catch (KeyStoreException e) {
			LOG.write("WindowsKeystore.getCertificate", e.getMessage());
		}
    	return null;
    }
	
	/**
     * Fix key alias
     * 
     * @param keyStore is windows keystore
     * @return
     */
	@SuppressWarnings("unchecked")
	private static void fixAliases(final KeyStore keyStore) {
		Field field;
		KeyStoreSpi keyStoreVeritable;
		final Set<String> tmpAliases = new HashSet<String>();
		try {
			field = keyStore.getClass().getDeclaredField("keyStoreSpi");
			field.setAccessible(true);
			keyStoreVeritable = (KeyStoreSpi) field.get(keyStore);

			if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
				Collection<Object> entries;
				String alias, hashCode;
				X509Certificate[] certificates;

				field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
				field.setAccessible(true);
				entries = (Collection<Object>) field.get(keyStoreVeritable);

				for (Object entry : entries) {
					field = entry.getClass().getDeclaredField("certChain");
					field.setAccessible(true);
					certificates = (X509Certificate[]) field.get(entry);

					hashCode = Integer.toString(certificates[0].hashCode());

					field = entry.getClass().getDeclaredField("alias");
					field.setAccessible(true);
					alias = (String) field.get(entry);
					String tmpAlias = alias;
					int i = 0;
					while (tmpAliases.contains(tmpAlias)) {
						i++;
						tmpAlias = alias + "-" + i;
					}
					tmpAliases.add(tmpAlias);
					if (!alias.equals(hashCode)) {
						field.set(entry, tmpAlias);
					}
				}
			}
		} catch (Exception exception) {
			LOG.write("WindowsKeystore", exception.getMessage());
		}
	}

	/**
     * Get certificates list
     * 
     * @param certFilter is conditions to filter
     * @return
     * @throws CryptoTokenOfflineException
     */
	@Override
	public List<CertificateInfo> getAllCertificateInfo(CertificateFilter certFilter)
			throws CryptoTokenOfflineException {
		if (certFilter == null || certFilter.getCertStore() == CertificateInfo.USER_STORE)
			return getAllCertInfoFromUserstore();
		//
		try {
			List<CertificateInfo> certsFromLocal = getAllCertFromLocalMachine();
			if (certFilter.getCertStore() == CertificateInfo.LOCAL_STORE)
			{
				return certsFromLocal;
			}
			List<CertificateInfo> certsFromUserStore = getAllCertInfoFromUserstore();
			List<CertificateInfo> certsConcat = concatenate(certsFromLocal, certsFromUserStore);
			return certsConcat;
			
		} catch (Exception e) {
			LOG.write("WindowsKeystore", e.getMessage());
			throw new CryptoTokenOfflineException(e);
		}
	}
	
	/**
     * Get all certificates on windows local store
     * 
     * @return list
     */
	public List<CertificateInfo> getAllCertFromLocalMachine()
	{		
		String listCert = CertificateUtils.getCertFromLocal();
		if (listCert == null)
			return null;
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		DataResult certResponse = gson.fromJson(listCert, DataResult.class);
		
		if (certResponse.getCode() == 1)
		{
			Type listCertType = new TypeToken<ArrayList<String>>(){}.getType();
			List<String> listObj = gson.fromJson(certResponse.getBaseData(), listCertType);
			for (String strCert : listObj) {
				X509Certificate cert = CertificateUtils.stringToX509Certificate(strCert);
				CertificateInfo certInfo = CertificateUtils.getCertificateInfo(cert);
				certInfo.setCertStore(CertificateInfo.LOCAL_STORE);
				certs.add(certInfo);
			}
		}
		else
		{
			LOG.write("getAllCertFromLocalMachine", certResponse.getBaseData());
		}
		return certs;
	}
	
	/**
     * Concat two list
     * 
     * @return list
     */
	@SafeVarargs
	public static<T> List<T> concatenate(List<T>... lists)
	{
		return Stream.of(lists)
					.flatMap(x -> x.stream())
					.collect(Collectors.toList());
	}

	/**
     * Get certificates chain
     * 
     * @param serialNumber
     * @return
     * @throws CryptoTokenOfflineException
     */
	@Override
	public Certificate[] getCertificateChainBySerial(String serialNumber) throws CryptoTokenOfflineException {
		List<CertificateInfo> certsFromLocal = getAllCertFromLocalMachine();
		if (certsFromLocal == null || certsFromLocal.size() == 0)
		{
			return null;
		}
		
		for (CertificateInfo certificateInfo : certsFromLocal) {
			if (certificateInfo.getSerial().toLowerCase().equals(serialNumber.toLowerCase()))
			{
				X509Certificate cert = CertificateUtils.stringToX509Certificate(certificateInfo.getBase64());
				Certificate[] certs = new Certificate[] { (X509Certificate)cert };
				return certs;
			}
		}
		return null;
	}
}
