package com.tokensigning.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
* Comment: read/wire file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class FileUtil {
	
	public static byte[] readBytesFromFile(String inputPath) throws IOException {
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(new File(inputPath));
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
			}

			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
		return ous.toByteArray();

	}
	
	public static void writeToFile(byte[] input, String pathname) throws IOException {
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(new File(pathname));
			outStream.write(input);
		} catch (IOException e) {
			throw e;
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * Set property to file
	 * 
	 * @param key
	 *            Property key
	 * @param value
	 *            Property value
	 */
	public static void setProperty(String key, String value, String fileName) {
		Properties prop = new Properties();
		String path = "resources/" + fileName;
		try {
			prop.load(new FileInputStream(new File(path)));
			prop.setProperty(key, value);

			FileOutputStream output = new FileOutputStream(path);
			prop.store(output, "This is overwrite file");
		} catch (FileNotFoundException e) {
			LOG.write("FileUtil", "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			LOG.write("FileUtil", "IOException: " + e.getMessage());
		}
	}

	/**
	 * Get property value from file
	 * 
	 * @param key
	 *            Property key
	 * @return Property value
	 */
	public static String getProperty(String key, String fileName) {
		Properties prop = new Properties();
		String path = "resources/" + fileName;
		try {
			prop.load(new FileInputStream(new File(path)));

			return prop.getProperty(key);
		} catch (FileNotFoundException e) {
			LOG.write("FileUtil","FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			LOG.write("FileUtil","IOException: " + e.getMessage());
		}

		return "";
	}
}
