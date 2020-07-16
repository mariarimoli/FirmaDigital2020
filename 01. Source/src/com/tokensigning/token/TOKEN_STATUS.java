package com.tokensigning.token;

/**
* TOKEN_STATUS: define token init status
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public enum TOKEN_STATUS {
	// driver pkcs#11 not found on system
	PROVIDER_NOTFOUND,
	// pin incorrect
	PIN_INCORRECT,
	// usb token available
	TOKEN_AVAILABLE,
	// usb token not found 
	TOKEN_NOTFOUND,
	// pkcs#11 store uninitialized
	TOKEN_UNINITIALIZED,
	// usb token has locked
	TOKEN_LOCKED,
	// slot invalid
	SLOT_INVALID
}
