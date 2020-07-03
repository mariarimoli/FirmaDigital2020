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
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lowagie.text.pdf.PdfPKCS7;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.CertificateFilter;
import com.tokensigning.common.LOG;
import com.tokensigning.form.CertificateInfo;

public class WindowsKeystore implements IKeystoreBase{
	private KeyStore keystore = null;
	private String _keyStoreType = "WINDOWS-MY";
	
	static {
	    Security.addProvider((Provider)new BouncyCastleProvider());
	}
	
	public WindowsKeystore() {
//		if (keystore == null)
//		{
//			loadWindowsKeyStore();
//		}
		//Set<String> tmpKsTypes = getKeyStores();
		keystore = null;
		loadWindowsKeyStore();
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static SortedSet<String> getKeyStores() {
	    Set<String> tmpKeyStores = Security.getAlgorithms("KeyStore");
	    return new TreeSet<String>(tmpKeyStores);
	  }
	public String getProvider()
    {
    	return null;
    }
	
	public List<CertificateInfo> getAllCertificateInfo() throws CryptoTokenOfflineException
	{
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		try {
			final Enumeration<String> tmpAliases = keystore.aliases();
			while (tmpAliases.hasMoreElements()) {
				final String tmpAlias = tmpAliases.nextElement();
				System.out.println(tmpAlias);
				LOG.write("getAllCertificateInfo", tmpAlias);
//				if (keystore.isCertificateEntry(tmpAlias)) {
//					
//				}
				Certificate certificate = keystore.getCertificate(tmpAlias);
				
				String subjectCN = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("CN");
				String subjectOU = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("OU");
				String subjectSerialNumber = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("SN");
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
				cert.setSubjectOU(subjectOU);
				cert.setSubjectSN(subjectSerialNumber);
				certs.add(cert);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CryptoTokenOfflineException(e);
		}
		return certs;
	}
	
	public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException  {
        try {
			return (PrivateKey)keystore.getKey(alias, null);
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CryptoTokenOfflineException(e);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CryptoTokenOfflineException(e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CryptoTokenOfflineException(e);
		}
        
    }
	
	public void loadWindowsKeyStore() {
		try {
			keystore = KeyStore.getInstance("WINDOWS-MY");
			//keystore = KeyStore.getInstance("WINDOWS-ROOT");
			keystore.load(null, null);	
			fixAliases(keystore);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//IOUtils.closeQuietly(tmpIS);
		}
	}
	
	public void loadWindowsKeyStore(String keytype) {
		try {
			keystore = KeyStore.getInstance(keytype);
			keystore.load(null, null);	
			fixAliases(keystore);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//IOUtils.closeQuietly(tmpIS);
		}
	}
	
	
	public String getCertificateAlias(String serialNumber) throws CryptoTokenOfflineException {
        try {
        	Enumeration<String> enumeration = keystore.aliases();
        	
        	while(enumeration.hasMoreElements()) {
        		String alias = (String) enumeration.nextElement();
//        		if (keystore.isCertificateEntry(alias)) 
//        		{
//    				        
//        		}
        		Certificate certificate = keystore.getCertificate(alias);
				String serial = ((X509Certificate) certificate)
						.getSerialNumber().toString(16);
				if (serial.toLowerCase().equals(serialNumber.toLowerCase())) {
					return alias;
				}    
				    
            }	
		} catch (KeyStoreException e) {
			LOG.write("WindowsKeystore.getCertificate", e.getMessage());
			System.out.println(e);
		}
    	return null;
    }   
	
	public Certificate[] getCertificateChain(String alias) throws CryptoTokenOfflineException {
        try {
        	Certificate[] certs = keystore.getCertificateChain(alias);
        	return certs;
		} catch (KeyStoreException e) {
			LOG.write("WindowsKeystore.getCertificate", e.getMessage());
		}
    	return null;
    }
	
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
			exception.printStackTrace();
		}
	}

	@Override
	public List<CertificateInfo> getAllCertificateInfo(CertificateFilter certFilter)
			throws CryptoTokenOfflineException {
		if (certFilter == null)
			return getAllCertificateInfo();
		//
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		try {
			final Enumeration<String> tmpAliases = keystore.aliases();
			while (tmpAliases.hasMoreElements()) {
				final String tmpAlias = tmpAliases.nextElement();
				System.out.println(tmpAlias);
				LOG.write("getAllCertificateInfo", tmpAlias);
//				if (keystore.isCertificateEntry(tmpAlias)) {
//					
//				}
				Certificate certificate = keystore.getCertificate(tmpAlias);
				
//				String subjectCN = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("CN");
//				String subjectOU = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("OU");
//				String subjectSerialNumber = PdfPKCS7.getSubjectFields((X509Certificate) certificate).getField("SN");
//				String serial = ((X509Certificate) certificate)
//						.getSerialNumber().toString(16);
//				String issuer = PdfPKCS7.getIssuerFields((X509Certificate) certificate).getField("CN");
//				String base64 = null;
//				try {
//					base64 = Base64Utils.base64Encode(certificate.getEncoded());
//				} catch (CertificateEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//				String notAfter = df.format(((X509Certificate) certificate).getNotAfter());
//				String notBefore = df.format(((X509Certificate) certificate).getNotBefore());
				
//				CertificateInfo cert = new CertificateInfo(subjectCN, serial, base64, issuer, notAfter, notBefore);
//				cert.setSubjectOU(subjectOU);
//				cert.setSubjectSN(subjectSerialNumber);
				CertificateInfo cert = CertificateUtils.getCertificateInfo((X509Certificate)certificate);
				certs.add(cert);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CryptoTokenOfflineException(e);
		}
		return certs;
	}
	
	public List<CertificateInfo> getAllCertFromLocalMachine()
	{		
		String listCert = CertificateUtils.getCertFromLocal();
		if (listCert == null)
			return null;
		List<CertificateInfo> certs = new ArrayList<CertificateInfo>();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Type listCertType = new TypeToken<ArrayList<String>>(){}.getType();
		List<String> listObj = gson.fromJson(listCert, listCertType);
		for (String strCert : listObj) {
			X509Certificate cert = CertificateUtils.stringToX509Certificate(strCert);
			CertificateInfo certInfo = CertificateUtils.getCertificateInfo(cert);
			certs.add(certInfo);
		}
		return certs;
	}
}
