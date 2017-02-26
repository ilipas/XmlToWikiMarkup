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

public class DirectoryPathWatcher {
	

	public static void watchDirectoryPath(File inputDirectory, XmlToWikiFilesProcessor xmlToWikiFilesProcessor) {
		
		Path path = FileSystems.getDefault().getPath(inputDirectory.getPath());
		// Sanity check - Check if path is a directory
		try {
			Boolean isDirectory = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
			if (!isDirectory) {
				throw new IllegalArgumentException("Path: " + path + " is not a folder");
			}
		} catch (IOException ioe) {
			// Directory does not exists
			ioe.printStackTrace();
		}

		System.out.println("Watching path: " + path);

		// Obtain the file system of the Path
		FileSystem fs = path.getFileSystem();

		// Create the new WatchService
		try (WatchService service = fs.newWatchService()) {

			// Register the path to the service
			// Watch for creation events
			path.register(service, ENTRY_CREATE);

			// Start the infinite polling loop
			WatchKey key = null;
			while (true) {
				//Returns a queued key. If no queued key is available, this method waits.
				key = service.take();

				// Dequeue events
				Kind<?> kind = null;
				int i = 0;
				List<WatchEvent<?>> watchEvents = key.pollEvents();
				File[] files = new File[watchEvents.size()];
				for (WatchEvent<?> watchEvent : watchEvents) {
					// Get the type of the event
					kind = watchEvent.kind();
					if (OVERFLOW == kind) {
						continue; // loop
					} else if (ENTRY_CREATE == kind) {
						// A new Path was created
						@SuppressWarnings("unchecked")
						Path newPath = ((WatchEvent<Path>) watchEvent).context();
						System.out.println("New file added: " + newPath);
						//File file = new File(inputDirectory, newPath.toString());
						files[i] = new File(inputDirectory, newPath.toString());
						i++;
					}
				}

				xmlToWikiFilesProcessor.processFiles(files);
				
				if (!key.reset()) {
					break; // loop
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

	}

}
