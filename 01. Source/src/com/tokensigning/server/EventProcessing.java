package com.tokensigning.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.CertificateFilter;
import com.tokensigning.common.Connector;
import com.tokensigning.common.LOG;
import com.tokensigning.common.RESPONSE_CODE;
import com.tokensigning.common.TSResponse;
import com.tokensigning.signature.Cms;
import com.tokensigning.signature.Office;
import com.tokensigning.signature.Pdf;
import com.tokensigning.signature.PdfSignatureOption;
import com.tokensigning.signature.SIGNING_RESULT;
import com.tokensigning.signature.SignatureOption;
import com.tokensigning.signature.SigningResult;
import com.tokensigning.signature.Xml;
import com.tokensigning.signature.XmlSignatureOption;
import com.tokensigning.token.CertificateHandle;
/**
* EventProcessing: handle request
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class EventProcessing {
	
	private CertificateHandle certHandle;
	Gson _gson = new GsonBuilder().disableHtmlEscaping().create();
	public EventProcessing(CertificateHandle certHandle) {
		if (null != certHandle)
		{
			this.certHandle = certHandle;	
		}
		else
		{
			this.certHandle = new CertificateHandle();
		}
	}
	/**
     * Method used when sign pdf
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */
	public TSResponse signPdf(Connector connect)
	{		
		SigningResult result = new SigningResult();
		if (connect.getArgs().length == 0)
		{
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return getResult(result);
		}
		byte[] pdfbytes = Base64Utils.base64Decode(connect.getArgs()[0]);
		if (pdfbytes == null)
		{
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return getResult(result);
		}
		PdfSignatureOption sigOption = null;
		if (connect.getArgs().length > 1)
		{
			try
			{
				sigOption = _gson.fromJson(connect.getArgs()[1], PdfSignatureOption.class);
			}
			catch (Exception ex)
			{
				LOG.write("EventProcessing.signPdf", ex.getMessage());
			}
		}
		Pdf pdf = new Pdf(this.certHandle);
		if (sigOption.getMetadatas() != null && sigOption.getMetadatas().size() > 0)
		{
			pdfbytes = Pdf.addCustomProperties(pdfbytes, sigOption.getMetadatas());
			if (pdfbytes == null)
			{
				result.setSigResult(SIGNING_RESULT.sigAddMetaDataFailed);
				return getResult(result);
			}
		}
		
		result = pdf.sign(pdfbytes, null, sigOption);
		return getResult(result);
	}
	/**
     * Method used when sign Xml
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	public TSResponse signXml(Connector connect)
	{
		SigningResult result = new SigningResult();
		if (connect.getArgs().length == 0)
		{
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return getResult(result);
		}
		byte[] data = Base64Utils.base64Decode(connect.getArgs()[0]);
		XmlSignatureOption sigOption = null;
		if (connect.getArgs().length > 1)
		{
			sigOption = _gson.fromJson(connect.getArgs()[1], XmlSignatureOption.class);
		}
		Xml xml = new Xml(this.certHandle);
		result = xml.sign(data, sigOption);
		return getResult(result);
	}
	/**
     * Method used when sign office
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	public TSResponse signOffice(Connector connect)
	{
		SigningResult result = new SigningResult();
		if (connect.getArgs().length == 0)
		{
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return getResult(result);
		}
		byte[] data = Base64Utils.base64Decode(connect.getArgs()[0]);
		SignatureOption sigOption = null;
		if (connect.getArgs().length > 1)
		{
			sigOption = _gson.fromJson(connect.getArgs()[1], SignatureOption.class);
		}
		Office office = new Office(this.certHandle);
		result = office.sign(data, sigOption);
		return getResult(result);
	}
	/**
     * Method used when sign text
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	public TSResponse signCms(Connector connect)
	{
		SigningResult result = new SigningResult();
		if (connect.getArgs().length == 0)
		{
			result.setSigResult(SIGNING_RESULT.sigBadInput);
			return getResult(result);
		}
		byte[] data = Base64Utils.base64Decode(connect.getArgs()[0]);
		SignatureOption sigOption = null;
		if (connect.getArgs().length > 1)
		{
			sigOption = _gson.fromJson(connect.getArgs()[1], SignatureOption.class);
		}
		Cms cms = new Cms(this.certHandle);
		result = cms.sign(data, sigOption);
		return getResult(result);
	}
	
	/**
     * Method used when get certificate info
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	public TSResponse getCertInfo(Connector connect)
	{
		CertificateFilter certFilter = null;
		if (connect.getArgs().length > 0)
		{
			certFilter = _gson.fromJson(connect.getArgs()[0], CertificateFilter.class);
		}
		
		TSResponse resp = new TSResponse(RESPONSE_CODE.error.getValue(), null, null);		
		String certInfo = this.certHandle.getCertificateInfo(certFilter);
		if (certInfo == null || certInfo.isEmpty())
		{
			resp.setError("Not found certificate");
		}
		else
		{
			resp.setCode(RESPONSE_CODE.success.getValue());
			resp.setData(certInfo);
		}
		return resp;
	}
	/**
     * Method used when build response
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	private static TSResponse getResult(SigningResult sigResult)
    {
		TSResponse result = new TSResponse(RESPONSE_CODE.error.getValue(), null, null);
        switch (sigResult.getSigResult())
        {
            case sigSuccess:
                result = CreateJsonResult(sigResult.getSigResult(), Base64Utils.base64Encode(sigResult.getSignedData()), "");
                break;
            case sigBadKey:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Not found certificate");
                break;
            case sigBadInput:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Bad input");
                break;
            case sigNotFoundPrvKey:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Private key not exists");
                break;
            case sigSigningFailed:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Signing failed");
                break;
            case sigUnknow:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Exception unknow");
                break;
            case sigMultiplePagesNotfound:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Not found mutilple pages pramater");
                break;
            case sigPDFPageNumberNotAllow:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Page number not allow");
                break;
            case sigXmlCantRefID:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Not found id tag signing");
                break;
            case sigXmlNotFoundTagName:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Not found tag signing");
                break;
            case sigDataIncludeSigInvalid:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Data contains one or more signatures invalid");
                break;
            case sigUserCancel:
                result = CreateJsonResult(sigResult.getSigResult(), "", "The action was cancelled by the user");
                break;
            default:
                result = CreateJsonResult(sigResult.getSigResult(), "", "Error");
                break;
        }
        return result;
    }
	/**
     * Method used when build response
     *
     * @param connect is request from webclient
     * @return response from TokenSigning
     */	
	public static TSResponse CreateJsonResult(SIGNING_RESULT code, String baseData, String errDesc)
    {
		TSResponse data = new TSResponse(code.ordinal(), baseData, errDesc);
		return data;
    }
}
