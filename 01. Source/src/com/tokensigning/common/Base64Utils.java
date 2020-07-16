package com.tokensigning.common;

import java.util.Base64;

/**
* Base64Utils: encode, decode base64
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class Base64Utils {
	
	/**
     * Method used when decode base64 from string
     *
     * @param data to encode
     * @return decoded bytes
     */
	public static byte[] base64Decode(String data) {
		return Base64.getDecoder().decode(data);
	}

	/**
     * Method used when decode base64 from bytes
     *
     * @param data to encode
     * @return decoded bytes
     */
	public static byte[] base64Decode(byte[] data) {
		return Base64.getDecoder().decode(data);
	}

	/**
     * Method used when encode base64 from bytes
     *
     * @param data to encode
     * @return encoded string
     */
	public static String base64Encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

}
