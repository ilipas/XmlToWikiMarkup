package com.ili.pas.main;

import java.io.Console;
import java.io.File;
import java.io.FilenameFilter;
import javax.xml.bind.JAXBException;

import com.ili.pas.util.XmlToWikiFilesProcessor;

public class Main {

	public static void main(String[] args) throws JAXBException {

		Console console = System.console();
		if (console == null) {
			System.err.println("No console.");
			System.exit(1);
		}

		//Read user inputs
		String inputDir = console.readLine("Enter file input directory: ");
		final File inputDirectory = new File(inputDir);
		if (!inputDirectory.exists()) {
			System.out.println(inputDirectory + " directory doesn't exists. Exiting ...");
			System.exit(1);
		}

		String outputDir = console.readLine("Enter file output directory: ");
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
