package com.tokensigning.common;

public enum RESPONSE_CODE {
	error(-1),
    success(1);
	//
    private final int id;
    RESPONSE_CODE(int id) { this.id = id; }
    public int getValue() { return id; }
}
