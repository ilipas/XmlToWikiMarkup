package com.ili.pas.main;

import java.io.File;
import java.io.FilenameFilter;
import javax.xml.bind.JAXBException;

import com.ili.pas.util.DirectoryPathWatcher;
import com.ili.pas.util.XmlToWikiFilesProcessor;

public class Main {

	public static void main(String[] args) throws JAXBException {

		if (args.length != 2) {
			System.err.println(
					"!!! Expected input and output directories to be specified, but found " + args.length + " arguments. Exiting ...");
			System.exit(1);
		}

		// Get user specified input directory
		String inputDir = args[0];
		final File inputDirectory = new File(inputDir);
		if (!inputDirectory.exists()) {
			System.out.println(inputDirectory + " directory doesn't exists. Exiting ...");
			System.exit(1);
		}

		// Get user specified output directory
		String outputDir = args[1];
		final File outputDirectory = new File(outputDir);
		if (!outputDirectory.exists()) {
			System.out.println(outputDirectory + " directory doesn't exists. Creating new...");
			boolean isNewOutputDirectoryCreated = outputDirectory.mkdir();
			if (isNewOutputDirectoryCreated) {
				System.out.println("Directory created.");
			} else {
				System.out.println("Failed to create directory " + outputDir);
				System.out.println("Exiting...");
				System.exit(1);
			}
		}

		// Get existing xml files from the specified input directory
		File[] xmlFiles = inputDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});
		
		XmlToWikiFilesProcessor xmlToWikiFilesProcessor = new XmlToWikiFilesProcessor();
		xmlToWikiFilesProcessor.setOutputDirectory(outputDirectory);

		// Process existing files if any
		if (xmlFiles.length != 0) {
			System.out.println("Found " + xmlFiles.length + "files. Processing...");
			xmlToWikiFilesProcessor.processFiles(xmlFiles);
		}
		
		//Keep looking for new files that might be added to the input directory
		DirectoryPathWatcher.watchDirectoryPath(inputDirectory, xmlToWikiFilesProcessor);

	}

}
