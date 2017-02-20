package com.ili.pas.main;

import java.io.Console;
import java.io.File;
import java.io.FilenameFilter;
import javax.xml.bind.JAXBException;

import com.ili.pas.util.XmlToWikiFilesProcessor;

public class Main {

	public static void main(String[] args) throws JAXBException {
		
		System.out.println("Enter file input and output directories");
		
		if(args.length != 2){
			System.err.println("!!! Expected two inputs: input and output directories. Exiting ...");
			System.exit(1);
		}

		//Read user inputs
		String inputDir = args[0];
		final File inputDirectory = new File(inputDir);
		if (!inputDirectory.exists()) {
			System.out.println(inputDirectory + " directory doesn't exists. Exiting ...");
			System.exit(1);
		}

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
		
		//Get xml files from the specified directory
		File[] xmlFiles = inputDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		//Process files
		if (xmlFiles.length == 0) {
			System.out.println("No files found to process in directory: " + inputDirectory + ". Exiting...");
			System.exit(1);
		} else {
			System.out.println("Number of files to process: " + xmlFiles.length);
			XmlToWikiFilesProcessor xmlToWikiFilesProcessor = new XmlToWikiFilesProcessor();
			xmlToWikiFilesProcessor.processFiles(xmlFiles, outputDirectory);
			System.out.println("Done");
		}

	}

}
