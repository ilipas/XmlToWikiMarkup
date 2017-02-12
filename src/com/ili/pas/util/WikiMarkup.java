package com.ili.pas.util;

public class WikiMarkup {
	
	public static String ITALIC = "''";
	public static String BOLD = "'''";

	public static String getHeadingLevel(int key) {
		
		String headingLevel = null;
		
		switch (key) {
		case 1:
			headingLevel = "=";
			break;
		case 2:
			headingLevel = "==";
			break;
		case 3:
			headingLevel = "===";
			break;
		case 4:
			headingLevel = "====";
			break;
		case 5:
			headingLevel = "=====";
			break;
		default:
			headingLevel = "======";
			break;
		}
		return headingLevel;
	}

}
