package com.ili.pas.util;

import java.io.File;
import java.io.FilenameFilter;

public class DirectoryUtil {
	
	public File createDirectory(String directoryPath) {

		final File directory = new File(directoryPath);
		if (!directory.exists()) {
			System.out.println(directory + " directory doesn't exists. Creating new...");
			if (directory.mkdir()) {
				System.out.println("Directory created.");
			} else {
				System.err.println("Unable to create directory " + directory + " .Exiting...");
				System.exit(1);
			}
		}
		return directory;
	}
	
	public File[] getFilesFromDirectory (File directory, final String fileExtension){
		
		if (directory.isDirectory()){
			
			File[] files = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(fileExtension);
				}
			});
			
			return files;
		}
		return new File[0];
	}

}
