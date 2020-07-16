package com.tokensigning.signature;

/**
* SigningResult: signing result 
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class SigningResult {
	// error code
	private SIGNING_RESULT sigResult;
	// signed data
	private byte[]	signedData;
	public SigningResult()
	{
		this.signedData = null;
		this.sigResult = SIGNING_RESULT.sigUnknow;
	}
	public SIGNING_RESULT getSigResult() {
		return sigResult;
	}
	public void setSigResult(SIGNING_RESULT sigResult) {
		this.sigResult = sigResult;
	}
	public byte[] getSignedData() {
		return signedData;
	}
	public void setSignedData(byte[] signedData) {
		this.signedData = signedData;
	}
	
}
