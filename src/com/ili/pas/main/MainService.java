package com.ili.pas.main;

import java.io.File;
import java.io.FilenameFilter;
import javax.xml.bind.JAXBException;

import com.ili.pas.util.DirectoryPathWatcher;
import com.ili.pas.util.XmlToWikiFilesProcessor;

public class MainService {

	// singleton instance
	private static MainService instance = new MainService();

	private boolean isStopRequested = false;

	public static void main(final String[] args) {

		final String cmd = (args.length > 0 ? args[0] : "start");
		if (cmd.equals("start")) {
			instance.start(args);
		} else {
			instance.stop();
		}
	}

	private void start(String[] args) {
		try {
			if (args.length != 2) {
				System.err.println("!!! Expected input and output directories to be specified, but found " + args.length
						+ " arguments. Exiting ...");
				System.exit(1);
			}

			// Get user specified input directory
			String inputDir = args[0];
			final File inputDirectory = new File(inputDir);
			if (!inputDirectory.exists()) {
				System.err.println(inputDirectory + " directory doesn't exists. Exiting ...");
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
					System.err.println("Failed to create directory " + outputDir);
					System.err.println("Exiting...");
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

			// Keep looking for new files that might be added to the input
			// directory
			DirectoryPathWatcher.watchDirectoryPath(inputDirectory, xmlToWikiFilesProcessor);

			// wait until the service is requested to be stopped
			while (!isStopRequested) {
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					break;
				}
			}
		} catch (final Throwable t) {
			System.err.println("Unhandled exception");
		} finally {
			System.err.println("Service stopped");
		}
	}

	private void stop() {
		System.err.println("Service stopping ...");
		isStopRequested = true;
	}

}
