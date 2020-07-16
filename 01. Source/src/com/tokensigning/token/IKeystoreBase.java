package com.tokensigning.token;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.tokensigning.common.CertificateFilter;
import com.tokensigning.form.CertificateInfo;

/**
* Interface Key Store 
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public interface IKeystoreBase {
	public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException;
	public String getCertificateAlias(String serialNumber) throws CryptoTokenOfflineException;
	public List<CertificateInfo> getAllCertInfoFromUserstore() throws CryptoTokenOfflineException;
	public List<CertificateInfo> getAllCertificateInfo(CertificateFilter certFilter) throws CryptoTokenOfflineException;
	public Certificate[] getCertificateChain(String alias) throws CryptoTokenOfflineException;
	public Certificate[] getCertificateChainBySerial(String serialNumber) throws CryptoTokenOfflineException;
	public String getProvider();
}
