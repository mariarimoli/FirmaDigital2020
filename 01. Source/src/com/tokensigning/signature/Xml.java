package com.tokensigning.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cesecore.keys.token.CryptoTokenOfflineException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tokensigning.common.LOG;
import com.tokensigning.common.OSType;
import com.tokensigning.form.CertificateChooser;
import com.tokensigning.token.CertificateHandle;
import com.tokensigning.token.IKeystoreBase;
import com.tokensigning.token.WindowsKeystore;
import com.tokensigning.utils.Utils;

/**
* Xml: handle sign xml data
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class Xml {
	// Fixed id if not set
	private String 				NODE_ID = "NODE_TO_SIGN";
	// id of tag signing time
	private static String 		SIGNINGTIME_ID = "sigid";
	// id of tag signature properties
	private static final String SIGNATURE_PROPERTIES_ID = "proid";
	// tag signingtime's name
	private static final String SIGNINGTIME_TAGNAME = "SigningTime";
	// handle certificate
	private CertificateHandle handle = new CertificateHandle();

	/**
     * Constructor
     *
     * @param hadle is instance for certificate handle: get cert, key,... 
     */
	public Xml(CertificateHandle handle) {
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
	public Xml() {
		
	}
	
	/**
     * Sign xml data
     *
     * @param data is file content in bytes
     * @param xmlSignature is signature options
     * @return result
     */
	// function
	public SigningResult sign(byte[] data, XmlSignatureOption xmlSignature) {
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
				if (xmlSignature == null || xmlSignature.getCertificateSerial() == null || xmlSignature.getCertificateSerial().isEmpty())
				{
					//TODO show chon cert
					// serialNumber = "5401f8b96391ca0971053be87920ef25";
					serialNumber = CertificateChooser.show(token.getAllCertInfoFromUserstore());
				}
				else
				{
					serialNumber = xmlSignature.getCertificateSerial();
				}
				String alias = token.getCertificateAlias(serialNumber);
				if (null != alias)
				{
					Certificate[] certChain = token.getCertificateChain(alias);
					PrivateKey privKey 		= token.getPrivateKey(alias);
					List<X509Certificate> x509CertChain = new LinkedList<X509Certificate>();
		            for (Certificate cert : certChain) {
		                if (cert instanceof X509Certificate) {
		                    x509CertChain.add((X509Certificate) cert);
		                }
		            }
					return sign(x509CertChain, certChain[0], privKey, data, xmlSignature);
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
	
	/**
     * Sign xml
     *
     * @param x509CertChain is signer's certificates chain
     * @param cert is signer's certificate
     * @param privKey is PrivateKey to sign
     * @param data is file content in bytes
     * @param xmlSigOption is signature options
     * @return result
     */
	public SigningResult sign(List<X509Certificate> x509CertChain,
			Certificate cert, PrivateKey privKey, byte[] data,
			XmlSignatureOption xmlSigOption) {
		String providerName = System.getProperty("jsr105Provider",
				"org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		XMLSignatureFactory fac = null;
		SigningResult result = new SigningResult();
		try {
			fac = XMLSignatureFactory.getInstance("DOM", (Provider) Class
					.forName(providerName).newInstance());
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			LOG.write("Xml.Sign", e.getMessage());
			result.setSigResult(SIGNING_RESULT.sigInternalError);
			return result;
		} 

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc;
		try {
			try
			{
				// Load document to sign
				doc = dbf.newDocumentBuilder()
						.parse(new ByteArrayInputStream(data));
			}
			catch (Exception e)
			{
				LOG.write("Xml.Sign", e.getMessage());
				result.setSigResult(SIGNING_RESULT.sigBadInput);
				return result;
			}			
			//
			SignedInfo si = null;
			if (xmlSigOption != null && xmlSigOption.getTagSigning() != null)
			{
				Node nodeSign = doc.getElementsByTagName(xmlSigOption.getTagSigning()).item(0);
				if (nodeSign != null)
				{
					if (xmlSigOption.getNodeToSign() == null || xmlSigOption.getNodeToSign().isEmpty())
					{
						xmlSigOption.setNodeToSign(NODE_ID);
					}
					Element e = (Element) nodeSign;
					if (e.hasAttribute("id")) {
						e.setIdAttribute("id", true);
						NODE_ID = e.getAttribute("id");
					} else if (e.hasAttribute("iD")) {
						e.setIdAttribute("iD", true);
						NODE_ID = e.getAttribute("iD");
					} else if (e.hasAttribute("Id")) {
						e.setIdAttribute("Id", true);
						NODE_ID = e.getAttribute("Id");
					} else if (e.hasAttribute("ID")) {
						e.setIdAttribute("ID", true);
						NODE_ID = e.getAttribute("ID");
					} else {
						e.setAttribute("id", xmlSigOption.getNodeToSign());
						e.setIdAttribute("id", true);
						NODE_ID = xmlSigOption.getNodeToSign();
					}
					si = createSignedInfo(fac, "#" + NODE_ID);
				}
				// Create SignedInfo Object
			}
			if (si == null)
			{
				try {
                    Reference ref = fac.newReference("",
                            fac.newDigestMethod(DigestMethod.SHA1, null),
                            Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (XMLStructure) null)),
                            null, null);

                    si = fac.newSignedInfo(fac.newCanonicalizationMethod(
            				CanonicalizationMethod.INCLUSIVE,
            				(C14NMethodParameterSpec) null), fac.newSignatureMethod(
            				SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

                } catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException e) {
                	LOG.write("Xml.Sign", e.getMessage());
        			result.setSigResult(SIGNING_RESULT.sigInvalidAlgorithm);
        			return result;
                } 
			}
		
			// Create KeyInfo Object
			KeyInfo ki = createKeyInfo(x509CertChain, cert, fac);
			// check format
			String sigTime = null;
			if (xmlSigOption != null)
			{
				Date dateFromMetaData = ConvertStringToTime(xmlSigOption.getSigningTime());
				sigTime = ConvertDateToStringTZ(dateFromMetaData);
			}		
			if (sigTime == null) {
				sigTime = ConvertDateToStringTZ(new Date());
			}
			XMLObject object = null;
			if (sigTime != null) {
				Element SigningTime = doc.createElement(SIGNINGTIME_TAGNAME);
				Attr attr = doc.createAttribute("xmlns");
				attr.setValue("http://example.org/#signatureProperties");
				SigningTime.setAttributeNode(attr);
				SigningTime.appendChild(doc.createTextNode("" + sigTime));
				DOMStructure timeStructure = new DOMStructure(SigningTime);

				SignatureProperty signTimeProperty = fac.newSignatureProperty(
						Collections.singletonList(timeStructure), "#"
								+ SIGNINGTIME_ID, null);

				SignatureProperties properties = fac.newSignatureProperties(
						Collections.singletonList(signTimeProperty),
						SIGNATURE_PROPERTIES_ID);

				object = fac
						.newXMLObject(Collections.singletonList(properties),
								null, null, null);
			}
			// Get parent node to store signature
			DOMSignContext dsc;
			if (xmlSigOption != null && xmlSigOption.getParrentTagSigning() != null &&  
					xmlSigOption.getParrentTagSigning().length() > 0)
			{
				Node xpathParrent = doc.getElementsByTagName(xmlSigOption.getParrentTagSigning()).item(0);
				dsc = new DOMSignContext(privKey, xpathParrent);	
			}
			else
			{
				dsc = new DOMSignContext(privKey, doc.getDocumentElement());
			}
			
			XMLSignature signature;

			if (object == null) {
				signature = fac.newXMLSignature(si, ki);
			} else {
				signature = fac
						.newXMLSignature(si, ki,
								Collections.singletonList(object),
								SIGNINGTIME_ID, null);
			}
			// Sign document
			try
			{
				signature.sign(dsc);
			}
			catch (Exception ex)
			{
 				result.setSigResult(SIGNING_RESULT.sigSigningFailed);
				LOG.write("Xml.Sign", ex.getMessage());
				return result;
			}
			
			// Get signed document as bytes array
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans;
			trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(bout));			
			result.setSignedData(bout.toByteArray());
			result.setSigResult(SIGNING_RESULT.sigSuccess);
			return result;
		} catch (KeyException ex) {
			LOG.write("Xml.Sign", ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			LOG.write("Xml.Sign", ex.getMessage());
		} catch (InvalidAlgorithmParameterException ex) {
			LOG.write("Xml.Sign", ex.getMessage());
		} catch (TransformerConfigurationException ex) {
			LOG.write("Xml.Sign", ex.getMessage());
		} catch (TransformerException ex) {
			LOG.write("Xml.Sign", ex.getMessage());
		}
		result.setSigResult(SIGNING_RESULT.sigSigningFailed);
		return result;
	}
	
	/**
     * create keyinfo tag in signature tag
     *
     * @param x509CertChain is signer's certificates chain
     * @param cert is signer's certificate
     * @param fac
     * @return KeyInfo
     */
	private KeyInfo createKeyInfo(List<X509Certificate> x509CertChain,
			Certificate cert, XMLSignatureFactory fac) throws KeyException {

		KeyInfo ki = null;
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		List<XMLStructure> kviItems = new LinkedList<XMLStructure>();
		KeyValue kv;
		try {
			kv = kif.newKeyValue(cert.getPublicKey());
			kviItems.add(kv);
		} catch (KeyException ex) {
			java.util.logging.Logger.getLogger(Xml.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		if (!x509CertChain.isEmpty()) {
			X509Data x509d = kif.newX509Data(x509CertChain);
			kviItems.add(x509d);
		}
		ki = kif.newKeyInfo(kviItems);
		return ki;
	}
	/**
     * create keyinfo tag in signature tag
     *
     * @param fac
     * @param timeReferenceID id of signingtime tag     * 
     * @return KeyInfo
     */
	private SignedInfo createSignedInfo(final XMLSignatureFactory fac,
			String timeReferenceID) throws NoSuchAlgorithmException,
			InvalidAlgorithmParameterException {
		List<Transform> transforms;

		transforms = new ArrayList<Transform>() {
			/**
		     *
		     */
			private static final long serialVersionUID = 4157122831331920365L;

			{
				TransformParameterSpec tranSpec = null;
				add(fac.newTransform(Transform.ENVELOPED, tranSpec));
			}
		};
		System.out.println(NODE_ID);
		Reference ref = fac.newReference("#" + NODE_ID,
				fac.newDigestMethod(DigestMethod.SHA1, null), transforms, null,
				null);

		// Create a reference for signing time node
		// Reference timeRef = fac.newReference(timeReferenceID,
		// fac.newDigestMethod(DigestMethod.SHA1, null), null, null, null);

		List<Reference> referenceList = new ArrayList<Reference>();
		referenceList.add(ref);
		// referenceList.add(timeRef);

		SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
				CanonicalizationMethod.INCLUSIVE,
				(C14NMethodParameterSpec) null), fac.newSignatureMethod(
				SignatureMethod.RSA_SHA1, null), referenceList);

		return si;
	}

	/**
     * convert string to time
     *
     * @param s is time in string format
     * @return date
     */
	public static Date ConvertStringToTime(String s) {
		// String s = "24/03/2013 21:54:09";
		if (s != null) {
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat();
				sdf1.applyPattern("dd/MM/yyyy HH:mm:ss");
				Date date = sdf1.parse(s);
				return date;
			} catch (ParseException ex) {
				LOG.write("Xml.ConvertStringToTime", ex.getMessage());
			}
		}
		return null;
	}
	/**
     * convert time to string format TZ (iso 8601)
     *
     * @param t is datetime
     * @return date in string format
     */
	public static String ConvertDateToStringTZ(Date t) {
		// String s = "24/03/2013 21:54:09";
		if (t != null) {
			try {
				TimeZone tz = TimeZone.getTimeZone("UTC");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted
																					// "Z"
																					// to
																					// indicate
																					// UTC,
																					// no
																					// timezone
																					// offset
				df.setTimeZone(tz);
				String nowAsISO = df.format(t);
				return nowAsISO;
			} catch (Exception ex) {
			}
		}
		return null;
	}

}
