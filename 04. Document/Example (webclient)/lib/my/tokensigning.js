//
var tokensigning = function tokensigning() {
    "use strict";
    var _debug = function (x) { };
    _debug("hwcrypto.js activated");    
    //<meta name="tokensigning-verification" content="key">
    //
    var USER_CANCEL         = "user_cancel";
    var NO_CERTIFICATES     = "no_certificates";
    var INVALID_ARGUMENT    = "invalid_argument";
    var TECHNICAL_ERROR     = "technical_error";
    var NOT_FOUND_KEY       = "not_found_key";
    var CONNECT_FAILED      = "tokensigning_connect_failed";
    var BROWSER_NOT_SUPPORT = "unsupported_browser";
    var BROWSER_NOT_SUPPORT_PROMISE = "promise_unsupported_browser";
    var META_NAME           = "tokensigning-verification";
    //
    var tokensigningws = null;
    var tsAppStatus = 0;
    //
    var funcId = {
        checkTokenSigning: 0,
        getVersion: 1,
        selectCertificate: 2,
        signXml: 3,
        signPdf: 4,
        signOOxml: 5,
        signCms: 6
    };

    var fields = {};

    fields.get_browser = function () {
        var ua = navigator.userAgent, tem,
            M = ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
        if (/trident/i.test(M[1])) {
            tem = /\brv[ :]+(\d+)/g.exec(ua) || [];
            return { name: 'IE', version: (tem[1] || '') };
        }
        if (M[1] === 'Chrome') {
            tem = ua.match(/\b(OPR|Edge)\/(\d+)/);
            if (tem !== null) {
                //return tem.slice(1).join(' ').replace('OPR', 'Opera');
                return {
                    name: tem.slice(1)[0].replace('OPR', 'Opera'),
                    version: tem.slice(1)[1]
                };
            }
        }
        M = M[2] ? [M[1], M[2]] : [navigator.appName, navigator.appVersion, '-?'];
        if ((tem = ua.match(/version\/(\d+)/i)) != null) M.splice(1, 1, tem[1]);
        //return M.join(' ');
        return {
            name: M[0],
            version: M[1]
        };
    };

    fields.checkBrowser = function () {
        var browser = fields.get_browser();
        switch (browser.name.toLowerCase()) {
            case "chrome":
                if (browser.version > 52) {
                    return true;
                }
            case "firefox":
                if (browser.version > 49) {
                    return true;
                }
            case "opera":
                if (browser.version > 26) {
                    return true;
                }
            case "edge":
                if (browser.version > 15) {
                    return true;
                }
            case "ie":
                if (browser.version > 6) {
                    return true;
                }
            case "msie":
                if (browser.version > 6) {
                    return true;
                }
            case "safari":
                if (browser.version > 10) {
                    return true;
                }
            default:
                return false;
        }
    };

    var browserSupported = fields.checkBrowser();

    fields.getKey = function () {
        var metas = document.getElementsByTagName('meta');
        for (var i = 0; i < metas.length; i++) {
            if (metas[i].getAttribute('name') === META_NAME) {
                return metas[i].getAttribute('content');
            }
        }
        return null;
    }
    //
    var tsKey = fields.getKey();

    var request = {};
    request.key = tsKey;

    fields.forward = function (data) {
        if (!browserSupported) {
            return Promise.reject(new Error(BROWSER_NOT_SUPPORT));
        }
        if (!window.Promise) {
            return Promise.reject(new Error(BROWSER_NOT_SUPPORT_PROMISE));
        }
        if (!tsKey) {
           return Promise.reject(new Error(NOT_FOUND_KEY));
        }
        data = JSON.stringify(data);
        //
        return new Promise(function (success, reject) {
            if (tokensigningws === null || tsAppStatus !== 1) {
                //tokensigningws = new WebSocket("wss://127.0.0.1:4667/tokensigning");
                tokensigningws = new WebSocket("wss://localhost:4667/tokensigning");
            }
            else {
                // do something when connected
                tokensigningws.send(data);
            }
            // opened
            tokensigningws.onopen = function () {
                tsAppStatus = 1;
                tokensigningws.send(data);
            };
            // closed
            tokensigningws.onclose = function () {
                tsAppStatus = 0;
            };
            // message
            tokensigningws.onmessage = function (message) {
                success(message.data);
            };
            // error
            tokensigningws.onerror = function () {
                tsAppStatus = 0;
                reject(new Error(CONNECT_FAILED));
            };
        });
    }; 

    fields.checkTokenSigning = function () {
        request.functionID = funcId.checkTokenSigning;        
        return this.forward(request);
    };
    
    fields.getVersion = function () {
        request.functionID = funcId.getVersion;
        return this.forward(request);
    };


    fields.selectCertificate = function (options) {
        request.functionID = funcId.selectCertificate;        
		var args = [
                JSON.stringify(options)
            ];
		request.args = args;
        return this.forward(request);
    };

    fields.signXml = function (data, options) {        
        if (!data) {
            return Promise.reject(new Error(INVALID_ARGUMENT));
        }
        request.functionID = funcId.signXml;
        var args = [
                data,
                JSON.stringify(options)
            ];
        request.args = args;
        return this.forward(request);
    };

    fields.signPdf = function (data, options) {
        if (!data) {
            return Promise.reject(new Error(INVALID_ARGUMENT));
        }
        request.functionID = funcId.signPdf;
        var args = [
                data,
                JSON.stringify(options)
            ];
        request.args = args;
        return this.forward(request);
    };

    fields.signOOxml = function (data, options) {
        if (!data) {
            return Promise.reject(new Error(INVALID_ARGUMENT));
        }
        request.functionID = funcId.signOOxml;
        var args = [
                data,
                JSON.stringify(options)
            ];
        request.args = args;
        return this.forward(request);
    };

    fields.signCms = function (data, options) {
        if (!data) {
            return Promise.reject(new Error(INVALID_ARGUMENT));
        }
        
        request.functionID = funcId.signCms;
        var args = [
                data,
                JSON.stringify(options)
            ];
        request.args = args;
        return this.forward(request);
    };

    return fields;
}();


function PdfSignatureOption() {
    this.page = 1;
    this.llx = 0;
    this.lly = 0;
    this.urx = 150;
    this.ury = 75;
    this.SigTextSize = 9;
    this.Signer = null;
    this.SigningTime = null; // HH:mm:ss dd/MM/yyyy
    this.Description = null;
    this.ImageBase64 = null;
    this.OnlyDescription = false;
    this.ValidationOption = true;
    this.SigColorRGB = null;    
    this.PagesArray = null;
    this.certificateSerial = null;
	this.tsaUrl = null;
	this.tsaUserName = null;
	this.tsaPass = null;
	this.metadatas = {}
}