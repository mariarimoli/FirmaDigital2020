package com.tokensigning.license;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.pdf.codec.Base64;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.utils.Utils;

/**
* LicenseKey: check license valid or not
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class LicenseKey {
    private static final String TIME_FOREVER            = "00:00:00";
    private static final String CERT_THUMPRINT 			= "96ba93bfda1a593efc2f4daeffc68b818c10ee37";
    
    /**
     * Check license key time
     *
     * @param 
     * @return true or false
     */
    
	public static Boolean CheckValidTime(LicenseContent liContent)
    {
		String validFrom = liContent.getNotBefore();
        String validTo = liContent.getNotAfer();
        // check License forever: <ValidTo>00/00/0000 00:00:00</ValidTo>                               
        if (validTo != null && validTo.equals(TIME_FOREVER))
        {
            return true;
        }
        Date dateVaildFrom = ConvertStringToTime(validFrom);
        Date dateVaildTo = ConvertStringToTime(validTo);
        if (null != dateVaildFrom && null != dateVaildTo)
        {
            Date dateTimeNow = new Date();
            if (dateTimeNow.after(dateVaildFrom) && dateTimeNow.before(dateVaildTo))
            {
                return true;
            }
        }
        return false;
    }
	
	/**
     * Check domain
     *
     * @param 
     * @return true or false
     */
	public static Boolean checkValidHostName(LicenseContent liContent, String domain)
    {
		String domainInLicense = liContent.getDomain();
        Boolean iCheck = true;
        if (!domainInLicense.equals("*")) // license test
        {
            iCheck = false;
            String[] domainSplit = domainInLicense.split(",");
            //domain = "https://3702181271-democadmin.vnpt-invoice.com.vn";
            for (String d : domainSplit)
            {
                if (d.equals(domain))
                {                                           
                    iCheck = true;
                    break;
                }
                else if (domain.contains(d)) // truong hop subdomain
                {
                    int index = domain.lastIndexOf(d);
                    String sub = domain.substring(index + d.length(), domain.length());
                    
                    if (null == sub || sub.isEmpty() || sub.equals("/"))
                    {
                        iCheck = true;
                        break;
                    }
                }
            }
            return iCheck;
        }
		return true;
    }
	
	/**
     * Check os support
     *
     * @param 
     * @return true or false
     */
	private static Boolean CheckOSLicense(LicenseContent liContent)
	{
		String osSupport = liContent.getOs();
		if (osSupport == null || osSupport.isEmpty())
		{
			return true;
		}
		OSType osType = Utils.getOperatingSystemType();
		switch (osType) {
		case Windows:
			return osSupport.contains("win");			
		case MacOS:
			return osSupport.contains("mac");			
		case Linux:
			return osSupport.contains("linux");
		default:
			return false;
		}
	}

	/**
     * Convert string to datetime
     *
     * @param s is date in string format
     * @return Date
     */
	public static Date ConvertStringToTime(String s) {
        //String s = "24/03/2013 21:54:09"; 09/07/2017 00:00:00
        if (s != null) {
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat();
                sdf1.applyPattern("MM/dd/yyyy HH:mm:ss");
                Date date = sdf1.parse(s);
                return date;
            } catch (ParseException ex) {
                System.out.println("Exception " + ex);
            }
        }
        return null;
    }
	
	/**
     * Get certificate thumbprint to check license's certificate
     *
     * @param cert is license's certificate
     * @return thumbprint
     */
	public static String getThumbPrint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        return hexify(digest);

    }
	
	/**
     * Convert to hex string
     *
     * @param bytes 
     * @return hex
     */
    public static String hexify (byte bytes[]) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuilder buf = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
            buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0x0f]);
        }

        return buf.toString();
    }
    
    /**
     * Build response
     *
     * @param code is key code
     * @param baseData is information
     * @param errDesc is error description
     * @return result
     */
    public static String CreateJsonResult(int code, String baseData, String errDesc)
    {
        String respone = String.format("\"code\":%d, \"data\":\"%s\", \"error\":\"%s\"", code, baseData, errDesc);
        return "{" + respone + "}";
    }
    
    /**
     * Check license key is valid or not
     *
     * @param key is license key
     * @param domain is website's domain
     * @return Key_Code is license code
     */
    public static KEY_CODE checkKey(String key, String domain)
    {
    	Security.addProvider(new BouncyCastleProvider());
    	try {
    		// Security.addProvider("BC");
    		CMSSignedData cms = null;
    		try {
    			cms = new CMSSignedData(Base64.decode(key));
    		} catch (CMSException ex) {
    			return KEY_CODE.keyInvalid;
    		}
    		Store store = cms.getCertificates();

    		SignerInformationStore signers = cms.getSignerInfos();
            Collection<?> c = signers.getSigners();
            Iterator<?> it = c.iterator();
            
            if(!it.hasNext())
            {
            	return KEY_CODE.keyInvalid;
            }
            while (it.hasNext()) {
            	String licenseConBase64= getOriginalData(cms);
            	byte[] licBytes = Base64.decode(licenseConBase64);
            	String licenseContent = new String(licBytes, StandardCharsets.UTF_8);
            	SignerInformation signer = (SignerInformation) it.next();
            	Collection<?> certCollection = store.getMatches(signer.getSID());
                Iterator<?> certIt = certCollection.iterator();
                X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                X509Certificate cert = null;
                try {
    				 cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
    			} catch (CertificateException e) {
    				// TODO Auto-generated catch block
    				return KEY_CODE.keyInvalid;
    			}
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                  String thumbPrint = getThumbPrint(cert);
                  if (!thumbPrint.toLowerCase().equals(CERT_THUMPRINT))
                  {
                	  LOG.write("LicenseKey.checkKey", "The license invalid");
                	  return KEY_CODE.keyInvalid;
                  }
                  Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                  LicenseContent liContent = gson.fromJson(licenseContent, LicenseContent.class);
                  // check domain
                  if (!checkValidHostName(liContent, domain)){
                      LOG.write("LicenseKey.checkKey", "The license not correspond");
                      return KEY_CODE.keyInvalid;
                  }
                  // check valid time
                  if (!CheckValidTime(liContent))
                  {
                      LOG.write("LicenseKey.checkLicense", "The license has expired or is not yet valid");
                      return KEY_CODE.keyExpired;
                  }
                  if (!CheckOSLicense(liContent))
                  {
                      LOG.write("LicenseKey.checkLicense", "The license not support the os");
                      return KEY_CODE.keyInvalid;
                  }
                }
                
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return KEY_CODE.keyInvalid;
		}
		
		return KEY_CODE.keyGood;
	}
    
    /**
     * Get information from license key
     *
     * @param cms signed data 
     * @return license information
     */
    public static String getOriginalData(CMSSignedData cms) {
        try {
            byte[] byte_out = null;
            ByteArrayOutputStream out = null;
            out = new ByteArrayOutputStream();
            cms.getSignedContent().write(out);
            byte_out = out.toByteArray();
            //String s = new String(byte_out);
            String s = Base64Utils.base64Encode(byte_out);
            return s;
        } catch (Exception ex) {
        	LOG.write("LicenseKey.checkLicense", ex.getMessage());
        }
        return null;
    }
}
