package com.tokensigning.token;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;

import com.lowagie.text.pdf.PdfPKCS7;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.LOG;
import com.tokensigning.form.CertificateInfo;

import net.sf.jni4net.Bridge;

public class CertificateUtils {
	private static final String JNI_DLL = "I:\\WORK\\My-Github\\TokenSigning\\01. Source\\CertificateHandle_CSharp\\Lib built\\jni4net.n.w32.v40-0.8.8.0.dll";
	private static final String CERT_UTIL_DLL = "I:\\WORK\\My-Github\\TokenSigning\\01. Source\\CertificateHandle_CSharp\\Lib built\\CertificateUtils.j4n.dll";
	
	public static String getCertFromLocal()
	{
		Bridge.setVerbose(true);
		try {
			Bridge.init(new File(JNI_DLL));
			Bridge.LoadAndRegisterAssemblyFrom(new File(CERT_UTIL_DLL));
			String certList = certificateutils.CertificateUtils.GetCertificate();
			return certList;
		} catch (IOException ex) {
			LOG.write("CertificateUtils.getCertFromLocal", ex.getMessage());
		}
		return null;		
	}
	
	public static String signHash(String hashBase64, String serial, String hashAlgorithm)
	{
		if (hashBase64 == null || hashBase64.isEmpty() || serial == null || serial.isEmpty() || hashAlgorithm == null)
		{
			LOG.write("CertificateUtils.signHash", "hashBase64: " + hashBase64);
			LOG.write("CertificateUtils.signHash", "serial: " + serial);
			LOG.write("CertificateUtils.signHash", "hashAlgorithm: " + hashAlgorithm);
			return null;
		}
		Bridge.setVerbose(true);
		try {
			Bridge.init(new File(JNI_DLL));
			Bridge.LoadAndRegisterAssemblyFrom(new File(CERT_UTIL_DLL));
			String certList = certificateutils.CertificateUtils.SignHash(hashBase64, serial, hashAlgorithm);
			return certList;
		} catch (IOException ex) {
			LOG.write("CertificateUtils.signHash", ex.getMessage());
		}
		return null;	
	}
	
	public static X509Certificate stringToX509Certificate(String cer) {
        X509Certificate certificate = null;
        try {
            byte[] cerbytes = Base64.getDecoder().decode(cer);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cerbytes));

        } catch (CertificateException ex) {
        	LOG.write("CertificateUtils.stringToX509Certificate", ex.getMessage());
            return null;
        }
        return certificate;
    }
	
	public static CertificateInfo getCertificateInfo(X509Certificate certificate) {
		CertificateInfo certInfo = null;
		try
		{
			String subjectCN = PdfPKCS7.getSubjectFields(certificate).getField("CN");
			String subjectOU = PdfPKCS7.getSubjectFields(certificate).getField("OU");
			String subjectSerialNumber = PdfPKCS7.getSubjectFields(certificate).getField("SN");
			String serial = certificate.getSerialNumber().toString(16);
			String issuer = PdfPKCS7.getIssuerFields(certificate).getField("CN");
			String base64 = null;
			try {
				base64 = Base64Utils.base64Encode(certificate.getEncoded());
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			}
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String notAfter = df.format(((X509Certificate) certificate).getNotAfter());
			String notBefore = df.format(((X509Certificate) certificate).getNotBefore());
			
			certInfo = new CertificateInfo(subjectCN, serial,
					base64, issuer, notAfter, notBefore);
			certInfo.setSubjectOU(subjectOU);
			certInfo.setSubjectSN(subjectSerialNumber);
		}
		catch (Exception ex) {
			LOG.write("CertificateUtils.getCertificateInfo", ex.getMessage());
		}
        return certInfo;
    }
}
