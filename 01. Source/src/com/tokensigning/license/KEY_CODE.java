package com.tokensigning.license;


/**
* KEY_CODE: License key status
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public enum KEY_CODE
{
	// Key not set
    keyNotFound,
    // Key valid
    keyGood,
    // Key invalid
    keyInvalid,
    // Key expired
    keyExpired,
    // Key not yet valid
    keyNotYetValid,
}