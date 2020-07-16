package com.tokensigning.common;

/**
* RESPONSE_CODE: define response code
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public enum RESPONSE_CODE {
	// failed
	error(-1),
	// success
    success(1);
	//
    private final int id;
    RESPONSE_CODE(int id) { this.id = id; }
    public int getValue() { return id; }
}
