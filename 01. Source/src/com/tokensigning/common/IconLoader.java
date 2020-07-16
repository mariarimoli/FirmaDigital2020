package com.tokensigning.common;

import com.tokensigning.utils.Utils;

/**
* IconLoader: load icon image from config
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class IconLoader {
	private static String iconWarning = null;
	private static String iconCertificate = null;
	private static String iconQuestion = null;
	private static String iconInfo = null;
	private static String iconApp = null;
	
	public void loadIcon()
	{
		iconWarning = Utils.getValueFromConfig("ICON_WARNING");
		iconCertificate = Utils.getValueFromConfig("ICON_CERTIFICATE");
		iconQuestion = Utils.getValueFromConfig("ICON_QUESTION");
		iconInfo = Utils.getValueFromConfig("ICON_INFO");
		iconApp = Utils.getValueFromConfig("ICON_APP");
	}

	public static String getIconWarning() {
		return iconWarning;
	}

	public static String getIconCertificate() {
		return iconCertificate;
	}

	public static String getIconQuestion() {
		return iconQuestion;
	}

	public static void setIconQuestion(String iconQuestion) {
		IconLoader.iconQuestion = iconQuestion;
	}

	public static String getIconInfo() {
		return iconInfo;
	}

	public static String getIconApp() {
		return iconApp;
	}
}
