package com.kaancelen.charter.utils;

public class StringUtil {
	/**
     * Replace Turkish characters with international characters
     * @param inString, given string
     * @return string with replaced characters
     */
    public static String replaceTurkishChars(String inString) {
        inString = inString.replace("�", "i");
        inString = inString.replace("�", "s");
        inString = inString.replace("�", "g");
        inString = inString.replace("�", "u");
        inString = inString.replace("�", "o");
        inString = inString.replace("�", "c");
        inString = inString.replace("�", "I");
        inString = inString.replace("�", "S");
        inString = inString.replace("�", "G");
        inString = inString.replace("�", "U");
        inString = inString.replace("�", "O");
        inString = inString.replace("�", "C");
        inString = inString.replace("\"", "'");
        return inString;
    }
}
