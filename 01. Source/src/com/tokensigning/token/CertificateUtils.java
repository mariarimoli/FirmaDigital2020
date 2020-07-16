package com.tokensigning.token;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

import com.lowagie.text.pdf.PdfPKCS7;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.LOG;
import com.tokensigning.form.CertificateInfo;

import net.sf.jni4net.Bridge;

/**
* CertificateUtils: handle get certificate from windows local store
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/


public class CertificateUtils {
	// library jni
	private static final String JNI_DLL = "jni4net.n.w32.v40-0.8.8.0.dll";
	// CSharp library for getting certificate from windows local store
	private static final String CERT_UTIL_DLL = "CertificateUtils.j4n.dll";
	
	/**
     * get certificate from windows local store
     *
     * @return encoded base64 certificates list 
     */
	public static String getCertFromLocal()
	{
		Bridge.setVerbose(true);
		try {
			String folderRuntime = System.getProperty("user.dir");
			LOG.write("folderRuntime", folderRuntime);
			LOG.write("", folderRuntime + "\\" + JNI_DLL);			
			Bridge.init(new File(folderRuntime + "\\" + JNI_DLL));
			Bridge.LoadAndRegisterAssemblyFrom(new File(folderRuntime + "\\" + CERT_UTIL_DLL));
			String certList = certificateutils.CertificateUtils.GetCertificate();
			return certList;
		} catch (Exception ex) {
			LOG.write("CertificateUtils.getCertFromLocal", ex.getMessage());
		}
		return null;		
	}
	
	/**
     * sign hash data with certificate in windows local store
     *
     * @param hashbase64 is hash string encoded base64
     * @param serial is certificate serial number
     * @param hashAlgorithm is hash Algorithm
     * @return signature
     */
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
			String folderRuntime = System.getProperty("user.dir");
			LOG.write("folderRuntime", folderRuntime);
			Bridge.init(new File(folderRuntime + "\\" + JNI_DLL));
			Bridge.LoadAndRegisterAssemblyFrom(new File(folderRuntime + "\\" + CERT_UTIL_DLL));
			String certList = certificateutils.CertificateUtils.SignHash(hashBase64, serial, hashAlgorithm);
			return certList;
		} catch (Exception ex) {
			LOG.write("CertificateUtils.signHash", ex.getMessage());
		}
		return null;	
	}
	
	/**
     * convert base64 string to object X509Certificate
     *
     * @param cer is certificate content encoded basse64
     * @return X509Certificate
     */
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
	
	/**
     * get certificate information
     *
     * @param certificate is X509Certificate
     * @return CertificateInfo
     */
	public static CertificateInfo getCertificateInfo(X509Certificate certificate) {
		CertificateInfo certInfo = null;
		try
		{
			String subjectCN = PdfPKCS7.getSubjectFields(certificate).getField("CN");
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
			
			String subjectOU = getCargoFromSubjectAlternative(certificate);
			certInfo.setSubjectOU(subjectOU);
			certInfo.setSubjectSN(subjectSerialNumber);
		}
		catch (Exception ex) {
			LOG.write("CertificateUtils.getCertificateInfo", ex.getMessage());
		}
        return certInfo;
    }
	
	/**
     * get certificate cargo firmante
     *
     * @param certificate is X509Certificate
     * @return cargo firmante
     */
	public static String getCargoFromSubjectAlternative(X509Certificate cert) {
        try {
            Collection<List<?>> altNames = cert.getSubjectAlternativeNames();
            if (altNames != null) {
                for (List<?> altName : altNames) {
                	String altNameStr = (String) altName.get(1);
                	if (altNameStr.contains("2.5.4.12"))
                	{
                		String[] alsSplt = altNameStr.split(",");
                		for (String alt : alsSplt) {
							if (alt.contains("2.5.4.12"))
							{
								String[] altCargo = alt.split("=#1307");
								byte[] cargo = Hex.decode(altCargo[1].getBytes(StandardCharsets.UTF_8));
								return new String(cargo);
							}
						}
                	}
                }
            }
        } catch (CertificateParsingException ignored) {
        }
        return "";
    }
}
