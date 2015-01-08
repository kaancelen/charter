package com.kaancelen.charter.helpers;

public class StringHelper {

	/**
	 * look for key in array if found return true
	 * @param array
	 * @param key
	 * @return if key one of the elemnts of array return true otherwise false
	 */
	public static boolean inArray(String[] array, String key){
		for (String string : array) {
			if(key.compareTo(string) == 0)
				return true;
		}
		return false;
	}
	
}
