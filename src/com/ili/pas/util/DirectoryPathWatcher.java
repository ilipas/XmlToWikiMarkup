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
 * to an array and forwards the array to {@link FileProcessor} class
 * for processing
 * 
 */
public class DirectoryPathWatcher {

	private File watchedDirectory;
	private FileProcessor fileProcessor;
	private WatchService watchService;

	public DirectoryPathWatcher(File watchedDirectory) {
		this.watchedDirectory = watchedDirectory;
	}

	public void watchDirectoryPath() {

		Path path = FileSystems.getDefault().getPath(watchedDirectory.getPath());
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
			watchService = fs.newWatchService();

			// Register the path to the service
			// Watch for creation events
			path.register(watchService, ENTRY_CREATE);

			startStopWatchService(true);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void startStopWatchService(boolean isStart) throws InterruptedException, IOException {
		if (!isStart) {
			System.out.println("Stoping path watcher service ...");
			watchService.close();
		}
		// Start the infinite polling loop
		WatchKey key = null;
		while (isStart) {
			// Returns a queued key. If no queued key is available, this
			// method waits.
			key = watchService.take();

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
					files[i] = new File(watchedDirectory, newPath.toString());
					i++;
				}
			}

			fileProcessor.processFiles(files);

			if (!key.reset()) {
				break; // loop
			}

		}

	}

	public void setFileProcessor(FileProcessor fileProcessor) {
		this.fileProcessor = fileProcessor;
	}
}
