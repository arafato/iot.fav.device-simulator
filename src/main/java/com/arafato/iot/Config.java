package com.arafato.iot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
	public static void init(String connectionString) throws IllegalArgumentException {
		IOT_HUB_CONNECTION_STRING = connectionString;
		
		IOT_HUB_ENDPOINT = getToken("HostName=(.*?)", IOT_HUB_ENDPOINT);
		IOT_HUB_SHARED_ACCESS_KEY_NAME = getToken("SharedAccessKeyName=(.*?)", IOT_HUB_ENDPOINT);
		IOT_HUB_SHARED_ACCESS_KEY = getToken("SharedAccessKey=(.*?)", IOT_HUB_ENDPOINT);
	}

	public static String IOT_HUB_CONNECTION_STRING = "";
	public static String IOT_HUB_ENDPOINT = "";
	public static String IOT_HUB_SHARED_ACCESS_KEY_NAME = "";
	public static String IOT_HUB_SHARED_ACCESS_KEY = "";
	
	private static String getToken(String regex, String source) throws IllegalArgumentException {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		if (matcher.find())
		{
		    return matcher.group(1);
		} else {
			throw new IllegalArgumentException("missing iot hub endpoint");
		}
	}
}
