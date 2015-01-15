package com.kaancelen.charter.utils;

public class StringUtil {
	/**
     * Replace Turkish characters with international characters
     * @param inString, given string
     * @return string with replaced characters
     */
    public static String replaceTurkishChars(String inString) {
        inString = inString.replace("ý", "i");
        inString = inString.replace("þ", "s");
        inString = inString.replace("ð", "g");
        inString = inString.replace("ü", "u");
        inString = inString.replace("ö", "o");
        inString = inString.replace("ç", "c");
        inString = inString.replace("Ý", "I");
        inString = inString.replace("Þ", "S");
        inString = inString.replace("Ð", "G");
        inString = inString.replace("Ü", "U");
        inString = inString.replace("Ö", "O");
        inString = inString.replace("Ç", "C");
        inString = inString.replace("\"", "'");
        return inString;
    }
}
