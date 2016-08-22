package com.kaancelen.charter.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

public class FileHelper {
	/**
	 * Verilen inputStream'i destinationPath dosyasına kaydeder.
	 */
	public static boolean copyFile(String destinationPath, InputStream inputStream){
		try{
			createDirectories(destinationPath);//create directories that not exist
			OutputStream outputStream = new FileOutputStream(destinationPath);
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = inputStream.read(bytes)) != -1){
				outputStream.write(bytes, 0, read);
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		}catch(IOException e){
			System.err.println(e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	public static void createDirectories(String path){
		int index = path.lastIndexOf(File.separator);
		if(index == -1)
			return;
		String destinationPath = path.substring(0, index);
		createDirectories(destinationPath);
		File dir = new File(destinationPath);
		if(!dir.exists())
			dir.mkdir();
	}
	
	/**
	 * Mass objesinin 'mass' property'sini parametre olarak alır ve dosyasını siler.
	 * @param filename
	 * @return
	 */
	public static boolean removeFile(String filename){
		File file = new File(filename);
		return (file.delete()?true:false);
	}
	/**
	 * remove given directory path
	 * @return
	 */
	public static boolean removeDirectory(String directory){
		try {
			FileUtils.deleteDirectory(new File(directory));
			return true;
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}
	
	/**
	 * @param path
	 * @return extension of file
	 */
	public static String mimeType(String path){
		return path.substring(path.lastIndexOf('.'));
	}
}
