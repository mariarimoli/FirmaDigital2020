package com.tokensigning.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.cesecore.keys.token.CryptoTokenOfflineException;
import org.openxml4j.exceptions.InvalidFormatException;
import org.openxml4j.exceptions.OpenXML4JException;
import org.openxml4j.opc.PackageAccess;
import org.openxml4j.opc.signature.PackageDigitalSignatureManager;

import com.tokensigning.common.OSType;
import com.tokensigning.form.CertificateChooser;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.token.IKeystoreBase;
import com.tokensigning.token.PKCS11CryptoToken;
import com.tokensigning.token.WindowsKeystore;
import com.tokensigning.utils.Utils;

public class Office {
	public static final  Logger LOG = Logger.getLogger(Pdf.class);
	private CertificateHandle handle = new CertificateHandle();
	public Office() {
	
	}
	public Office(CertificateHandle handle) {
		super();
		if (handle != null) {
			this.handle = handle;
		} else {
			this.handle = new CertificateHandle();
		}
	}
	public SigningResult sign(byte[] data, SignatureOption sigOption) {
		SigningResult sigResult = new SigningResult();
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
			sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
		}
		return sigResult;
	}
	// option: sigOption => de nang cap phan option ky sha256,...
	public SigningResult sign(Certificate[] certChain, PrivateKey privKey, byte[] data, SignatureOption siOption) {
		org.openxml4j.opc.Package docxPackage;
		SigningResult result = new SigningResult();
		try {
			docxPackage = org.openxml4j.opc.Package.open(new ByteArrayInputStream(data),
					PackageAccess.READ_WRITE);
		} catch (InvalidFormatException|IOException e) {
			LOG.error("Data received is not in valid openxml package format", e);
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return result;
		} 
		// create digital signature manager object
        PackageDigitalSignatureManager dsm = new PackageDigitalSignatureManager(
                docxPackage);
        // sign document
        try {            
        	X509Certificate cert = (X509Certificate) certChain[0];
            dsm.SignDocument(privKey, cert);
        } catch (OpenXML4JException e1) {
        	LOG.error("Problem signing document", e1);
        	result.setSigResult(SIGNING_RESULT.sigSigningFailed);
			return result;
        }

        // save output to package
        ByteArrayOutputStream boutFinal = new ByteArrayOutputStream();
        try {
            dsm.getContainer().save(boutFinal);
            result.setSignedData(boutFinal.toByteArray());
            result.setSigResult(SIGNING_RESULT.sigSuccess);
            return result;
        } catch (IOException e) {
        	LOG.error("Error saving final output data to output", e);
        }
		result.setSigResult(SIGNING_RESULT.sigSigningFailed);
		return result;
	}
}
