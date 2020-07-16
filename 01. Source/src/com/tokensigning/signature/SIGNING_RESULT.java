package com.tokensigning.signature;

/**
* SIGNING_RESULT: response code when sign
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public enum SIGNING_RESULT {
    // input null or incorrect format
	sigBadInput,
	// sign successfully
    sigSuccess,
    // not found user's certificate 
    sigBadKey,
    // signing failed
    sigSigningFailed,
    // certificate without private key 
    sigNotFoundPrvKey,
    // sign failed with unknown error
    sigUnknow,
    // not found config pages
    sigMultiplePagesNotfound,
    // page not found
    sigPDFPageNumberNotAllow,
    // xml signing result
    // not found tag signing
    sigXmlNotFoundTagName,
    // not found tag id
    sigXmlCantRefID,
    // xml have invalid signature
    sigDataIncludeSigInvalid,
    // user cancel
    sigUserCancel,
    // sign failed
    sigInternalError,
    // invalid hash algorithm
    sigInvalidAlgorithm,
    // add metadatas to pdf failed
    sigAddMetaDataFailed
}
