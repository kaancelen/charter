package com.kaancelen.charter.utils;

public class StringUtil {
	/**
     * Replace Turkish characters with international characters
     * @param inString, given string
     * @return string with replaced characters
     */
    public static String replaceTurkishChars(String inString) {
        inString = inString.replace("ı", "i");
        inString = inString.replace("ş", "s");
        inString = inString.replace("ğ", "g");
        inString = inString.replace("ü", "u");
        inString = inString.replace("ö", "o");
        inString = inString.replace("ç", "c");
        inString = inString.replace("İ", "I");
        inString = inString.replace("Ş", "S");
        inString = inString.replace("Ğ", "G");
        inString = inString.replace("Ü", "U");
        inString = inString.replace("Ö", "O");
        inString = inString.replace("Ç", "C");
        inString = inString.replace("\"", "'");
        return inString;
    }
}
