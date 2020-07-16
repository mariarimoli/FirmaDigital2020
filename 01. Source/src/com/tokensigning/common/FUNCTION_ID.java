package com.tokensigning.common;

/**
* FUNCTION_ID: define funtions can call from webclient
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public enum FUNCTION_ID
{
	// check tokensigning is running?
	checkTokenSigning,
	// check tokensigning's version
    getVersion,
	// choose certificate from store
    selectCertificate,
    // sign xml data
    signXml,
    // sign pdf data
    signPdf,
    // sign office data
    signOOxml,
    // sign text data
    signCms,
}
