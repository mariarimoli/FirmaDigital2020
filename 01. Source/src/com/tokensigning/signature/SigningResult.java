package com.tokensigning.signature;

public class SigningResult {
	private SIGNING_RESULT sigResult;
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
