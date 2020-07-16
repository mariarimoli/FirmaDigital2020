package com.tokensigning.exception;

/**
* TSSignatureException: exception when signing
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class TSSignatureException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TSSignatureException(Throwable cause) {
        super(cause);
    }

    public TSSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public TSSignatureException(String message) {
        super(message);
    }
}
