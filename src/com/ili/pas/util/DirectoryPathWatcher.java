package com.ili.pas.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

/**
 * This class detects new files added to the specified directory, adds new files
 * to an array and forwards the array to {@link XmlToWikiFilesProcessor} class
 * for processing
 * 
 */
public class DirectoryPathWatcher {

	private static File inputDirectory;
	private static XmlToWikiFilesProcessor xmlToWikiFilesProcessor;
	private static WatchService service;

	public static void watchDirectoryPath() {

		Path path = FileSystems.getDefault().getPath(inputDirectory.getPath());
		// Sanity check - Check if path is a directory
		try {
			Boolean isDirectory = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
			if (!isDirectory) {
				throw new IllegalArgumentException("Path: " + path + " is not a directory");
			}
		} catch (IOException ioe) {
			// Directory does not exists
			ioe.printStackTrace();
		}

		// Obtain the file system of the Path
		FileSystem fs = path.getFileSystem();

		try {
			// Create the WatchService
			service = fs.newWatchService();

			// Register the path to the service
			// Watch for creation events
			path.register(service, ENTRY_CREATE);

			startStopWatchService(true);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void startStopWatchService(boolean isStart) throws InterruptedException, IOException {
		if (!isStart) {
			System.out.println("Stoping path watcher service ...");
			service.close();
		}
		// Start the infinite polling loop
		WatchKey key = null;
		while (isStart) {
			// Returns a queued key. If no queued key is available, this
			// method waits.
			key = service.take();

			// Dequeue events
			Kind<?> kind = null;
			int i = 0;
			List<WatchEvent<?>> watchEvents = key.pollEvents();
			File[] files = new File[watchEvents.size()];
			for (WatchEvent<?> watchEvent : watchEvents) {
				// Get a type of the event
				kind = watchEvent.kind();
				if (OVERFLOW == kind) {
					continue; // loop
				} else if (ENTRY_CREATE == kind) {
					// A new Path was created
					@SuppressWarnings("unchecked")
					Path newPath = ((WatchEvent<Path>) watchEvent).context();
					System.out.println("New file added: " + newPath);
					files[i] = new File(inputDirectory, newPath.toString());
					i++;
				}
			}

			xmlToWikiFilesProcessor.processFiles(files);

			if (!key.reset()) {
				break; // loop
			}

		}

	}

	public static void setInputDirectory(File inputDirectory) {
		DirectoryPathWatcher.inputDirectory = inputDirectory;
	}

	public static void setXmlToWikiFilesProcessor(XmlToWikiFilesProcessor xmlToWikiFilesProcessor) {
		DirectoryPathWatcher.xmlToWikiFilesProcessor = xmlToWikiFilesProcessor;
	}
}
