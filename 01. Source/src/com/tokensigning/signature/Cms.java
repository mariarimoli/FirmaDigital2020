package com.tokensigning.signature;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.cesecore.keys.token.CryptoTokenOfflineException;

import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.form.CertificateChooser;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.token.IKeystoreBase;
import com.tokensigning.token.PKCS11CryptoToken;
import com.tokensigning.token.WindowsKeystore;
import com.tokensigning.utils.Utils;

public class Cms {
	private CertificateHandle handle = new CertificateHandle();
	private String pkcs11Provider = null;
	
	
	public Cms(CertificateHandle handle) {
		super();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		if (handle != null) {
			this.handle = handle;
		} else {
			this.handle = new CertificateHandle();
		}
	}
	
	public Cms() {
		super();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public SigningResult signHash(byte[] data, SignatureOption sigOption) {
		SigningResult sigResult = new SigningResult();
		try {		
			// PKCS11		
			PKCS11CryptoToken token = this.handle.getPkcs11Token();
			if (null != token)
			{
				String serialNumber = null;
				if (sigOption == null || sigOption.getCertificateSerial() == null)
				{
					serialNumber = CertificateChooser.show(token.getAllCertificateInfo());
				}
				else
				{
					serialNumber = sigOption.getCertificateSerial();
				}
				String alias = token.getCertificateAlias(serialNumber);
				if (null != alias)
				{
					//Certificate[] certChain = token.getCertificateChain(alias);
					PrivateKey privKey 		= token.getPrivateKey(alias);
					return signHash(privKey, data, sigOption);
				}
				else
				{
					sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
				}
			}
			else
			{
				sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
			}			
		} catch (CryptoTokenOfflineException e) {
			e.printStackTrace();
			sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
		}
		return sigResult;
	}
	public SigningResult signHash(PrivateKey privKey, byte[] data, SignatureOption sigOption) {
		SigningResult result = new SigningResult();
		try {			
			DERObjectIdentifier shaoid_ = null;
			if (sigOption == null)
			{
				sigOption = new SignatureOption();
			}
			if (sigOption.getDigestMethod().toLowerCase().equals("sha1"))
			{
				shaoid_ = new DERObjectIdentifier(
						"1.3.14.3.2.26");
			}
			else
			{
				shaoid_ = new DERObjectIdentifier(
						"SHA256");
			}
			@SuppressWarnings("deprecation")
			AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(shaoid_, null);
			DigestInfo di = new DigestInfo(sha1aid_, data);
//			Cipher cipher2;
//			cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding",
//					new BouncyCastleProvider());
//			cipher2.init(Cipher.ENCRYPT_MODE, privKey);
			Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher2.init(Cipher.ENCRYPT_MODE, privKey);
			byte[] signed = cipher2.doFinal(di.getEncoded());
			result.setSignedData(signed);
			result.setSigResult(SIGNING_RESULT.sigSuccess);
			return result;
		}  catch (Exception e) {
			e.printStackTrace();
			LOG.write("Cms.signHash", e.getMessage());
		}
		result.setSigResult(SIGNING_RESULT.sigSigningFailed);
		return result;
	}
	public SigningResult sign(byte[] data, SignatureOption sigOption) {
		SigningResult sigResult = new SigningResult();
		if (data == null)
		{
			sigResult.setSigResult(SIGNING_RESULT.sigBadInput);
			LOG.write("Cms.sign", "bad input");
			return sigResult;
		}
		try {		
			OSType osType = Utils.getOperatingSystemType();
			IKeystoreBase token = null;
			if (osType == OSType.Windows)
			{
				token = new WindowsKeystore();
			}
			else
			{
				token = this.handle.getPkcs11Token();
			}
			//
			if (null != token)
			{
				String serialNumber = null;
				if (sigOption == null || sigOption.getCertificateSerial() == null || sigOption.getCertificateSerial().isEmpty())
				{
					serialNumber = CertificateChooser.show(token.getAllCertificateInfo());
				}
				else
				{
					serialNumber = sigOption.getCertificateSerial();
				}
				String alias = token.getCertificateAlias(serialNumber);
				if (null != alias)
				{
					Certificate[] certChain = token.getCertificateChain(alias);
					PrivateKey privKey 		= token.getPrivateKey(alias);
					
					pkcs11Provider 			= token.getProvider();
					return sign(certChain, privKey, data, sigOption);
				}
				else
				{
					sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
				}
			}
			else
			{
				sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
			}			
		} catch (CryptoTokenOfflineException e) {
			e.printStackTrace();
			LOG.write("Cms.sign", e.getMessage());
			sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
		}
		return sigResult;
	}
	public SigningResult sign(Certificate[] certs, PrivateKey privKey, byte[] data, SignatureOption sigOption) {
		SigningResult result = new SigningResult();
		
		final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		try {
			if (sigOption == null)
			{
				sigOption = new SignatureOption();
			}

            
//			JcaSignerInfoGeneratorBuilder sigBuilder  = new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build());
//			SignerInfoGenerator signerInfoGen = sigBuilder.build(new JcaContentSignerBuilder(sigOption.getSignatureMethod()).build(privKey), (X509Certificate) certs[0]);
//			generator.addSignerInfoGenerator(signerInfoGen);
			if (pkcs11Provider == null || pkcs11Provider.isEmpty())
			{
				pkcs11Provider = BouncyCastleProvider.PROVIDER_NAME;
			}
			final ContentSigner contentSigner = new JcaContentSignerBuilder(sigOption.getSignatureMethod()).setProvider(pkcs11Provider).build(privKey);
			//final ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(privKey);
            generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                    .build(contentSigner, (X509Certificate) certs[0]));
		
			List<X509Certificate> x509CertChain = new LinkedList<X509Certificate>();
	        for (Certificate cert : certs) {
	            if (cert instanceof X509Certificate) {
	                x509CertChain.add((X509Certificate) cert);	                
	            }
	        }
			
			generator.addCertificates(new JcaCertStore(x509CertChain));
            final CMSTypedData content = new CMSProcessableByteArray(data);
            final CMSSignedData signedData = generator.generate(content, true);
            final byte[] signedbytes = signedData.getEncoded();
            result.setSignedData(signedbytes);
            result.setSigResult(SIGNING_RESULT.sigSuccess);
            return result;
		} catch (Exception e) {
			LOG.write("Cms.sign", e.getMessage());
		}
        result.setSigResult(SIGNING_RESULT.sigSigningFailed);
		return result;
	}
}
