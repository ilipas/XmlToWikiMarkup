package com.ili.pas.util;

import java.io.File;

public interface FileProcessor {

	/**
	 * Creates wiki-markup file and saves it into user specified directory
	 * 
	 * @param filesToProcess,
	 *            list of files to be processed
	 */
	void processFiles(File[] filesToProcess);

	/**
	 * Setter for user defined output directory
	 * 
	 * @param outputDirectory,
	 *            location of the wiki-markup file
	 */
	public void setOutputDirectory(File outputDirectory);

}