package com.tokensigning.token;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.tokensigning.common.CertificateFilter;
import com.tokensigning.form.CertificateInfo;

public interface IKeystoreBase {
	public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException;
	public String getCertificateAlias(String serialNumber) throws CryptoTokenOfflineException;
	public List<CertificateInfo> getAllCertificateInfo() throws CryptoTokenOfflineException;
	public List<CertificateInfo> getAllCertificateInfo(CertificateFilter certFilter) throws CryptoTokenOfflineException;
	public Certificate[] getCertificateChain(String alias) throws CryptoTokenOfflineException;
	public String getProvider();
}
