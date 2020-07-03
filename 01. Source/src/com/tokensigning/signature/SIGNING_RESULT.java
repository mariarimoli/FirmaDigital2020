package com.tokensigning.signature;

public enum SIGNING_RESULT {
    sigBadInput,
    sigSuccess,
    sigBadKey,
    sigSigningFailed,
    sigNotFoundPrvKey,
    sigUnknow,
    sigMultiplePagesNotfound,
    sigPDFPageNumberNotAllow,
    //xml
    sigXmlNotFoundTagName,
    sigXmlCantRefID,
    sigDataIncludeSigInvalid,
    sigUserCancel,
    sigInternalError,
    sigInvalidAlgorithm,
    sigAddMetaDataFailed
}
