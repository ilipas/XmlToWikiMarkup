package com.ili.pas.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.ili.pas.util.DirectoryPathWatcher;
import com.ili.pas.util.DirectoryUtil;
import com.ili.pas.util.FileProcessor;
import com.ili.pas.util.XmlToWikiFileProcessor;

public class MainService {

	private static String START = "start";
	private static String STOP = "stop";

	private static boolean isStopRequested = false;
	// default input and output directories
	private static String inputDir = "input";
	private static String outputDir = "output";
	private static String FILE_EXTENSION = ".xml";

	private static DirectoryPathWatcher directoryPathWatcher;
	private static FileProcessor xmlToWikiFileProcessor;

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

			DirectoryUtil directoryUtil = new DirectoryUtil();
			// create input and output directories if don't exist
			final File inputDirectory = directoryUtil.createDirectory(inputDir);
			final File outputDirectory = directoryUtil.createDirectory(outputDir);

			xmlToWikiFileProcessor = new XmlToWikiFileProcessor();
			xmlToWikiFileProcessor.setOutputDirectory(outputDirectory);

			File[] files = directoryUtil.getFilesFromDirectory(inputDirectory, FILE_EXTENSION);
			// Process existing files if any
			if (files.length != 0) {
				System.out
						.println("Found " + files.length + "files in " + inputDirectory + " directory. Processing...");
				xmlToWikiFileProcessor.processFiles(files);
			}

			directoryPathWatcher = new DirectoryPathWatcher(inputDirectory);
			directoryPathWatcher.setFileProcessor(xmlToWikiFileProcessor);
			while (!isStopRequested) {
				// Keep looking for new files added to the input directory
				directoryPathWatcher.watchDirectoryPath();
				System.out.println("Watching for new files in: " + inputDirectory);

			}

		} catch (final Throwable t) {
			System.err.println("Unhandled exception");
		} finally {
			System.err.println("Service stopped");
		}

	}

	public static void stop(String[] args) {
		System.err.println("Stopping service ...");
		try {
			directoryPathWatcher.startStopWatchService(false);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		isStopRequested = true;
	}

}
