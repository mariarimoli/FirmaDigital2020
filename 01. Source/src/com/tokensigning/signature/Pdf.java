package com.tokensigning.signature;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfSignatureAppearance.SignatureComment;
import com.lowagie.text.pdf.PdfSignatureAppearance.SignatureWidget;
import com.lowagie.text.xml.xmp.XmpWriter;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TSAClient;
import com.lowagie.text.pdf.TSAClientBouncyCastle;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.DataResult;
import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.configuraion.ProxyInfo;
import com.tokensigning.exception.TSSignatureException;
import com.tokensigning.form.CertificateChooser;
import com.tokensigning.form.CertificateInfo;
import com.tokensigning.form.ProxyConfigurationForm;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.token.CertificateUtils;
import com.tokensigning.token.IKeystoreBase;
import com.tokensigning.token.WindowsKeystore;
import com.tokensigning.utils.Utils;

/**
* Pdf: handle sign pdf file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class Pdf {
	// 
	private static final String RGB = "0,0,255";
	private static final String FUNCTION = "Pdf.Sign";
	//private static final String HASH_ALGORITHM = "SHA-256";
	private static final String HASH_ALGORITHM = "SHA1";
	private static final Gson _gson = new GsonBuilder().disableHtmlEscaping().create();
	private CertificateHandle handle = new CertificateHandle();
	private static boolean isCertInLocalMachine = false;
	private String _certSerial = null;
	
	/**
     * Constructor
     *
     * @param hadle is instance for certificate handle: get cert, key,... 
     */
	public Pdf(CertificateHandle handle) {
		super();
		if (handle != null) {
			this.handle = handle;
		} else {
			this.handle = new CertificateHandle();
		}
	}

	/**
     * Constructor
     *
     */
	public Pdf() {
	}

	
	/**
     * Sign pdf
     *
     * @param pdfbytes is file content in bytes
     * @param password is files's password
     * @param SigningResult is signature options
     * @return result
     */
	public SigningResult sign(byte[] pdfbytes, byte[] password,
			PdfSignatureOption pdfSignature) {
		SigningResult sigResult = new SigningResult();
		try {
			LOG.write("Pdf.sign", "Sign pdf init");
			// PKCS11
			OSType osType = Utils.getOperatingSystemType();
			IKeystoreBase token = null;
			isCertInLocalMachine = pdfSignature.getCertStore() == CertificateInfo.LOCAL_STORE;
			if (osType == OSType.Windows)
			{
				LOG.write("Pdf.sign", "Sign pdf windows");
				token = new WindowsKeystore();
			}
			else
			{
				LOG.write("Pdf.sign", "Sign pdf pkcs11");
				token = this.handle.getPkcs11Token();
			}
			if (null != token) {
				String serialNumber = null;
				if (pdfSignature == null
						|| pdfSignature.getCertificateSerial() == null
						|| pdfSignature.getCertificateSerial().isEmpty()) {
					// serialNumber = "5401f8b96391ca0971053be87920ef25";
					serialNumber = CertificateChooser.show(token
							.getAllCertInfoFromUserstore());
				} else {
					serialNumber = pdfSignature.getCertificateSerial();
				}
				_certSerial = serialNumber;
				LOG.write("Pdf.sign", "Serial: " + _certSerial);
				//						
				Certificate[] certChain = null;
				PrivateKey privKey = null;
				if (!isCertInLocalMachine)
				{
					LOG.write("Pdf.sign", "User store");
					String alias = token.getCertificateAlias(serialNumber);
					if (null != alias) {
						certChain = token.getCertificateChain(alias);
						privKey = token.getPrivateKey(alias);
						
					} else {
						sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
						return sigResult;
					}
				}
				else
				{
					LOG.write("Pdf.sign", "Local Machine Store");
					certChain = token.getCertificateChainBySerial(serialNumber);
				}
				return sign(certChain, privKey, pdfbytes, password,
						pdfSignature);
				
			} else {
				LOG.write("Pdf.sign", "Token init failed");
				sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
			}
		} catch (Exception e) {
			sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
		}
		return sigResult;
	}
	
	/**
     * Get widgets from config
     *
     * @param signatures is list signature in file
     * @return Array of widget
     */
	public SignatureWidget[] getSigWidgets(List<com.tokensigning.common.PdfSignature> signatures) {
		String sigStr = _gson.toJson(signatures);
		SignatureWidget[] widgets = null;
		try {
			widgets = _gson.fromJson(sigStr, SignatureWidget[].class);
		} catch (Exception ex) {
			LOG.write("Pdf.Sign", "Multiple signatures warning: " + ex.getMessage());
		}

		return widgets;
	}

	/**
     * Calculate Signature's Comments
     *
     * @param stamper
     * @param sigComments is list comment
     * @return PdfStamper
     */
	private PdfStamper calculateSignComments(PdfStamper stamper, SignatureComment[] sigComments) {
        try {
            stamper.getSignatureAppearance().setSignatureComments(sigComments);
        } catch (Exception ex) {
            LOG.write("Pdf.sign", ex.getMessage());
            return stamper;
        }
        return stamper;
    }
	
	/**
     * Get signature image
     * 
     * @param imageBase64 is imaege content encoded base64
     * @return Image
     */
	private Image getImage(String imageBase64)
	{
		if (imageBase64 != null && !imageBase64.isEmpty()) {
			try {
				Image image = Image.getInstance(Base64Utils
						.base64Decode(imageBase64));
				return image;
			} catch (BadElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}
		return null;
	}
	
	/**
     * Calculate text visible in signature
     *
     * @param today is datetime now
     * @param params is signature confgi
     * @return PdfStamper
     */
	protected String calculateSigText(Date today, Certificate[] certChain, 
			PdfSignatureOption params) {
		
		if (params.getSignatureText() != null && !params.getSignatureText().isEmpty())
		{
			return params.getSignatureText().replace("\\n", "\n");
		}
		else
		{
			  DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		      String text = "Signed by: Unknown\n";
		      

		      PdfPKCS7.X509Name x509Name = PdfPKCS7.getSubjectFields((X509Certificate) certChain[0]);
		      if (x509Name != null) {
		          text = "";
		          text += "Signed By: " + x509Name.getField("CN");
		      }
		      
		      String dateStr =  df.format(today);
		      text += "\nSigned On: " + dateStr;    

		      return text;
		}
	}
	
	/**
     * Sign pdf
     *
     * @param today is datetime now
     * @param certChain is signer's certificates chain
     * @param privKey is PrivateKey to sign
     * @param pdfbytes is file content in bytes
     * @param password is files's password
     * @param SigningResult is signature options
     * @return result
     */
	
	public SigningResult sign(Certificate[] certChain, PrivateKey privKey,
			byte[] pdfbytes, byte[] password, PdfSignatureOption pdfSignature) {
		SigningResult sigResult = new SigningResult();
		Calendar cal = Calendar.getInstance();
		// load document
		PdfReader reader = null;
		int contentEstimated = 0;
		try {
			reader = new PdfReader(pdfbytes, password);
		} catch (IOException e1) {
			e1.printStackTrace();
			sigResult.setSigResult(SIGNING_RESULT.sigBadInput);
			return sigResult;
		}
		boolean appendMode = true;
		if (pdfSignature == null) {
			pdfSignature = new PdfSignatureOption(1, 10, 10, 150, 75, null,
					true, RGB, 11, HASH_ALGORITHM);
		}
		if(pdfSignature.getPage() == 0)
		{
			pdfSignature.setPage(1);
		}
		
		ByteArrayOutputStream fout = new ByteArrayOutputStream();

		try {
			PdfStamper stp;
			stp = PdfStamper.createSignature(reader, fout,
					PdfWriter.VERSION_1_7, null, appendMode);
			
			stp = calculateSignComments(stp, pdfSignature.getComments());
			
	        PdfSignatureAppearance sap = stp.getSignatureAppearance();
	        
	        sap.setSignatureWigets(getSigWidgets(pdfSignature.getSignatures()));
	        sap.setPdfReader(reader);
	        sap.setImage(getImage(pdfSignature.getImageBase64()));
			
			sap.setCrypto(null, certChain, null,
					PdfSignatureAppearance.SELF_SIGNED);

			String fieldName = PdfPKCS7.getSubjectFields(
					(X509Certificate) certChain[0]).getField("CN");
			String fieldNameReplaced = calculateSigFieldName(reader,
					fieldName.replaceAll("[^a-zA-Z0-9 ]+", ""));
			sap.setVisibleSignature(new com.lowagie.text.Rectangle(
					pdfSignature.getLlx(), pdfSignature.getLly(),
					pdfSignature.getUrx(), pdfSignature.getUry()),
					pdfSignature.getPage(), fieldNameReplaced);
			
			if (pdfSignature.getValidationOption() != null && !pdfSignature.getValidationOption()) {
				sap.setAcro6Layers(true);
			}
			
			String text = calculateSigText(cal.getTime(), certChain, pdfSignature);
			System.out.println(text);
			sap.setLayer2Text(text);
            // font
            Font font = calculateFont(pdfSignature);
            sap.setLayer2Font(font);
            switch (pdfSignature.getSigType()) {
	            case VisibleType.TEXT_ONLY:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.TEXT_ONLY);
	                break;
	            case VisibleType.TEXT_WITH_BACKGROUND:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.TEXT_WITH_BACKGROUND);
	                break;
	            case VisibleType.TEXT_WITH_LOGO_LEFT:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.TEXT_WITH_LOGO_LEFT);
	                break;
	            case VisibleType.TEXT_WITH_LOGO_TOP:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.TEXT_WITH_LOGO_TOP);
	                break;
	            case VisibleType.LOGO_ONLY:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.LOGO_ONLY);
	                break;
	            default:
	                sap.setRenderMode(PdfSignatureAppearance.RenderMode.NONE);
	                break;

            }
			
			// TODO: convert pdfSignature SigningTime to Calendar
			sap.setSignDate(cal);
			PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE,
					new PdfName("adbe.pkcs7.detached"));
			dic.setReason(pdfSignature.getReason());
			dic.setLocation(pdfSignature.getLocation());
			dic.setDate(new PdfDate(cal));
			dic.setName(PdfPKCS7.getSubjectFields(
					(X509Certificate) certChain[0]).getField("CN"));
			sap.setCryptoDictionary(dic);
			
			TSAClient tsc = null;
			final String tsaUrl = pdfSignature.getTsaUrl();

            if (tsaUrl != null) {
                tsc = getTimeStampClient(pdfSignature.getTsaUrl(),
                		pdfSignature.getTsaUserName(), 
                		pdfSignature.getTsaPass());
            }
            
			PdfPKCS7 sgn = null;
			String digestAlgorithm = HASH_ALGORITHM;
			try {

				if (pdfSignature.getDigestAlgrothim() != null) {
					digestAlgorithm = pdfSignature.getDigestAlgrothim();
				}
				LOG.write(FUNCTION, digestAlgorithm);
				sgn = new PdfPKCS7(privKey, certChain, null, digestAlgorithm,
						null, false);

			} catch (InvalidKeyException e) {
				LOG.write(FUNCTION, e.getMessage());
				sigResult.setSigResult(SIGNING_RESULT.sigBadKey);
				return sigResult;
			} catch (NoSuchProviderException | NoSuchAlgorithmException e) {
				LOG.write(FUNCTION, e.getMessage());
				sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
				return sigResult;
			}
			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance(digestAlgorithm);
			} catch (NoSuchAlgorithmException e) {
				LOG.write(FUNCTION, e.getMessage());
				sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
				return sigResult;
			}

			// calculate signature size
			if (contentEstimated == 0) {
				contentEstimated = calculateEstimatedSignatureSize(certChain,
						tsc, null, null);
			}
			byte[] encodedSig = calculateSignature(sgn, contentEstimated,
					messageDigest, cal, certChain, tsc, null, sap);
			byte[] paddedSig = new byte[contentEstimated];
			System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
			PdfDictionary dic2 = new PdfDictionary();
			dic2.put(PdfName.CONTENTS,
					new PdfString(paddedSig).setHexWriting(true));
			sap.close(dic2);
			reader.close();
			fout.close();
			sigResult.setSigResult(SIGNING_RESULT.sigSuccess);
			sigResult.setSignedData(fout.toByteArray());
			return sigResult;
		} catch (Exception ex) {
			LOG.write(FUNCTION, ex.getMessage());
			sigResult.setSigResult(SIGNING_RESULT.sigSigningFailed);
			return sigResult;
		}
	}

	/**
     * Calculate signature field name
     *
     * @param reader is pdf reader
     * @param fieldName for check fieldname is exist
     * @return field name new
     */
	public static String calculateSigFieldName(PdfReader reader,
			String fieldName) {
		String newField = fieldName;
		AcroFields fields = reader.getAcroFields();
		if (fields != null) {
			@SuppressWarnings("unchecked")
			ArrayList<String> sigNames = fields.getSignatureNames();
			if (sigNames != null && !sigNames.isEmpty()) {
				int index = sigNames.size();
				if (sigNames.contains(fieldName)) {
					newField = fieldName + "-" + (index);
				}
			}
		}
		return newField;
	}

	/**
     * Calculate signature size
     *
     * @param today is datetime now
     * @param params is signature confi
     * @return signature size
     */
	protected int calculateEstimatedSignatureSize(Certificate[] certChain,
			TSAClient tsc, byte[] ocsp, CRL[] crlList) {
		int estimatedSize = 0;
		for (Certificate cert : certChain) {
			try {
				int certSize = cert.getEncoded().length;
				estimatedSize += certSize;

			} catch (CertificateEncodingException e) {
				LOG.write("calculateEstimatedSignatureSize",
						"Error estimating signature size contribution for certificate: "
								+ e.getMessage());
				return estimatedSize;
			}
		}
		// add estimate for PKCS#7 structure + hash
		estimatedSize += 2000;

		// add space for OCSP response
		if (ocsp != null) {
			estimatedSize += ocsp.length;
		}

		if (tsc != null) {
			// add guess for timestamp response (which we can't really know)
			// TODO: we might be able to store the size of the last TSA response
			// and re-use next time...
			final int tscSize = 4096;

			estimatedSize += tscSize;
		}

		// add estimate for CRL
		if (crlList != null) {
			for (CRL crl : crlList) {
				if (crl instanceof X509CRL) {
					X509CRL x509Crl = (X509CRL) crl;

					try {
						int crlSize = x509Crl.getEncoded().length;
						// the CRL is included twice in the signature...
						estimatedSize += crlSize * 2;
					} catch (CRLException e) {
						LOG.write("calculateEstimatedSignatureSize",
								"Error estimating signature size contribution for CRL: "
										+ e.getMessage());
						return estimatedSize;
					}
				}
			}
			estimatedSize += 100;
		}

		return estimatedSize;
	}

	/**
     * Calculate signature
     *
     * @param sgn is instance to calculate signature
     * @param size is signature size
     * @param messageDigest is hash algorithm
     * @param cal is signature time
     * @param certChain is user's certificate chain
     * @param tsc is timestamp
     * @param ocsp is ocsp response
     * @param sap is pdf signature instance
     * @return signature
     */
	protected byte[] calculateSignature(PdfPKCS7 sgn, int size,
			MessageDigest messageDigest, Calendar cal, Certificate[] certChain,
			TSAClient tsc, byte[] ocsp, PdfSignatureAppearance sap) {
		try {
			final HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
			exc.put(PdfName.CONTENTS, Integer.valueOf(size * 2 + 2));
			sap.preClose(exc);

			InputStream data = sap.getRangeStream();
			if (data == null) {
				LOG.write("calculateSignature", "Data null");
			}
			byte buf[] = new byte[8192];
			int n;
			while ((n = data.read(buf)) > 0) {
				messageDigest.update(buf, 0, n);
			}
			byte hash[] = messageDigest.digest();

			byte sh[] = sgn.getAuthenticatedAttributeBytes(hash, cal, ocsp);
			//
			if (isCertInLocalMachine)
			{
				byte[] hashValue = Hash(sh, HASH_ALGORITHM);
				String hashAlgorithm = "SHA1";
				if (HASH_ALGORITHM == "SHA-256")
				{
					hashAlgorithm = "SHA256";
				}
				//String sigResponse = CertificateUtils.SignHash(Base64Utils.base64Encode(hashValue), _certSerial, hashAlgorithm);
				String sigResponse = CertificateUtils.signHash(Base64Utils.base64Encode(hashValue), _certSerial, hashAlgorithm);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				DataResult sig = gson.fromJson(sigResponse, DataResult.class);
				if (sig.getCode() == 1)
				{
					sgn.setExternalDigest(Base64Utils.base64Decode(sig.getBaseData()), null, "RSA");	
				}
				else
				{
					LOG.write("calculateSignature",
							"Error calculating signature: " + sig.getBaseData());
					throw new TSSignatureException(sig.getBaseData());					
				}
				
			}
			else
			{
				sgn.update(sh, 0, sh.length);	
			}
			
			byte[] encodedSig = sgn.getEncodedPKCS7(hash, cal, tsc, ocsp);

			return encodedSig;
		} catch (Exception e) {
			LOG.write("calculateSignature",
					"Error calculating signature: " + e.getMessage());
			System.out.println(e);
		}
		return null;
	}

	/**
     * Calculate signature text font
     *
     * @param params is signature configuration
     * @return PdfStamper
     */
	private Font calculateFont(PdfSignatureOption params) {
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8f, Font.NORMAL, Color.BLACK);
        String pathFont = Utils.getFontFolder();
        LOG.write(FUNCTION, pathFont);
        
        Color color = Color.BLUE;
        try {
            color = Color.decode(params.getFontColor());
        } catch (Exception ex) {
            
        }
        try {
			File f = new File(pathFont.toString());
			if (f.exists() && !f.isDirectory()) {
				font = FontFactory.getFont(pathFont,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED,
						params.getSigTextSize(), Font.NORMAL,
						color);
			}
        } catch (Exception ex) {
            LOG.write(FUNCTION, "CalculateFont: " + ex.getMessage());
        }

        return font;
    }
	
	/**
     * Get time from timestamp server
     *
     * @param url Timestamp server url
     * @param username
     * @param password
     * @return timestamp client
     */
	protected TSAClient getTimeStampClient(String url, String username, String password) {
		TSAClientBouncyCastle tsaClient = new TSAClientBouncyCastle(url, username, password);
		ProxyInfo proxyInfo = ProxyConfigurationForm.getProxyConfig();
		if (proxyInfo != null)
		{
			tsaClient.setIsUseProxy(true);
			tsaClient.setProxyServer(proxyInfo.getServer());
			tsaClient.setProxyPort(proxyInfo.getPort());
			tsaClient.setProxyUser(proxyInfo.getUserName());
			tsaClient.setProxyPass(proxyInfo.getPassword());	
		}
		else
		{
			tsaClient.setIsUseProxy(false);
		}
		
		return tsaClient;
    }

	/**
     * Add properties to pdf file
     *
     * @param input is pdf content
     * @param metadatas list meta add to properties
     * @return pdf content after add properties
     */
	public static byte[] addCustomProperties(byte[] input, HashMap<String, String> metadatas) {
		try {

			PdfReader reader = new PdfReader(input);
			ByteArrayOutputStream fout = new ByteArrayOutputStream();

			PdfStamper stamper = new PdfStamper(reader, fout);
			HashMap<String, String> info = reader.getInfo();
			// reader.get

			for (Map.Entry me : metadatas.entrySet()) {
				System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
				if (me.getKey() != null && me.getKey() != null && !me.getKey().toString().isEmpty()
						&& !me.getValue().toString().isEmpty()) {
					info.put(me.getKey().toString(), me.getValue().toString());
				}

			}
			stamper.setMoreInfo(info);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XmpWriter xmp = new XmpWriter(baos, info);
			xmp.close();
			stamper.setXmpMetadata(baos.toByteArray());
			stamper.close();
			reader.close();
			fout.close();
			return fout.toByteArray();

		} catch (IOException | DocumentException e) {			
			LOG.write("PDF", e.getMessage());
		}
		return null;
	}

	/**
     * Calculate hash of string
     *
     * @param input is data in bytes
     * @param digestAlgorithm is hash algorithm
     * @return hash
     */
	private static byte[] Hash(byte[] input, String digestAlgorithm) {
        try {
            MessageDigest crypt = MessageDigest.getInstance(digestAlgorithm);
            crypt.reset();
            crypt.update(input);
            return crypt.digest();
        } catch (NoSuchAlgorithmException ex) {
        	LOG.write(FUNCTION, "Hash: " + ex.getMessage());
        }
        return null;
    }

	/**
	* VisibleType: signature type in pdf file
	*
	* @author  Tuan
	* @version 1.0
	* @since   2020-07-12 
	*/
	static class VisibleType {
		// Visible text only
	    public final static int TEXT_ONLY = 1;
	    // Visible text and logo (logo in left)
	    public final static int TEXT_WITH_LOGO_LEFT = 2;
	    // Visible logo (image) only
	    public final static int LOGO_ONLY = 3;
	    // Visible text and logo (logo on top)
	    public final static int TEXT_WITH_LOGO_TOP = 4;
	    // Visible text and logo (logo is background)
	    public final static int TEXT_WITH_BACKGROUND = 5;
	    // Signature invisible
	    public final static int NONE = 0;
	}

}
