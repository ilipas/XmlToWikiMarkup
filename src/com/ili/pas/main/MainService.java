package com.ili.pas.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.ili.pas.util.DirectoryPathWatcher;
import com.ili.pas.util.XmlToWikiFilesProcessor;

public class MainService {

	private static String START = "start";
	private static String STOP = "stop";
	
	private static boolean isStopRequested = false;
	// default input and output directories
	private static String inputDir = "input";
	private static String outputDir = "output";

	public static void main(final String[] args) {

		if (START.equals(args[0])) {
            start(args);
        } else if (STOP.equals(args[0])) {
            stop(args);
        }
	}

	public static void start(String[] args) {
		try {
			if (args.length < 2) {
				System.out.println("Input and output directories not specified. Using default values: input, output.");
			} else {
				// Get user defined input and output directories
				inputDir = args[0];
				outputDir = args[1];
			}

			final File inputDirectory = new File(inputDir);
			if (!inputDirectory.exists()) {
				System.out.println(inputDirectory + " directory doesn't exists. Creating new...");
				createDirectory(inputDirectory);
			}

			final File outputDirectory = new File(outputDir);
			if (!outputDirectory.exists()) {
				System.out.println(outputDirectory + " directory doesn't exists. Creating new...");
				createDirectory(outputDirectory);
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
			
			DirectoryPathWatcher.setInputDirectory(inputDirectory);
			DirectoryPathWatcher.setXmlToWikiFilesProcessor(xmlToWikiFilesProcessor);
			while (!isStopRequested) {
				// Keep looking for new files that might be added to the input
				// directory
				DirectoryPathWatcher.watchDirectoryPath();
				System.out.println("Watching for new files in:  " + inputDirectory);

			}

		} catch (final Throwable t) {
			System.err.println("Unhandled exception");
		} finally {
			System.err.println("Service stopped");
		}

	}

	private static void createDirectory(File directory) {
		boolean isNewOutputDirectoryCreated = directory.mkdir();
		if (isNewOutputDirectoryCreated) {
			System.out.println("Directory created.");
		} else {
			System.err.println("Failed to create directory " + directory);
			System.err.println("Exiting...");
			System.exit(1);
		}

	}

	public static void stop(String[] args) {
		System.err.println("Stopping service ...");
		try {
			DirectoryPathWatcher.startStopWatchService(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isStopRequested = true;
	}

}
