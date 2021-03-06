package com.ili.pas.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.ili.pas.jaxb.Bold;
import com.ili.pas.jaxb.Italic;
import com.ili.pas.jaxb.ObjectFactory;
import com.ili.pas.jaxb.Report;
import com.ili.pas.jaxb.Section;

/**
 * Processes files from the user specified directory, creates a new file
 * containing corresponding wiki markup and saves it in the user specified output directory
 * 
 */
public class XmlToWikiFileProcessor implements FileProcessor {

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private File outputDirectory;
	private static final String NEW_LINE = "\n";
	private static final String WIKI_FILE_EXTENTION = ".wiki";
	private static final String XML_FILE_EXTENTION = ".xml";
	private int headingLevel;

	public XmlToWikiFileProcessor(){
		
		headingLevel = 0;
		
		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void processFiles(File[] filesToProcess) {

		for (File file : filesToProcess) {
			System.out.println("Processing file: " + file);
			Report report = null;
			try {
				//Check if xml file
				if(!file.getName().toLowerCase().endsWith(XML_FILE_EXTENTION)){
					System.out.println("Found non xml file " + file);
					continue;
				}
				// Unmarshal xml file
				report = (Report) unmarshaller.unmarshal(file);

				// Process results
				List<Object> reportContent = report.getContent();
				StringBuilder stringBuilder = processContent(reportContent, new StringBuilder());

				// Write result to .wiki file
				String filePath = outputDirectory + System.getProperty("file.separator")
						+ FilenameUtils.getBaseName(file.getName()) + WIKI_FILE_EXTENTION;
				
				System.out.println("Writing file :" + filePath);

				FileUtils.writeStringToFile(new File(filePath), stringBuilder.toString());
				
			} catch (IOException | JAXBException e) {
				System.out.println("Error processing file : " + file.getName());
			}
		}

	}

	/**
	 * Process content and apply wiki markup recursively
	 * 
	 * @param content,
	 *            array list of objects to be processed
	 * @param stringBuilder,
	 *            holds wiki markup data
	 * @return StringBuilder, content to be written to a wiki file
	 */
	private StringBuilder processContent(List<Object> content, StringBuilder stringBuilder) {

		for (Object ob : content) {

			if (ob instanceof String) {
				if (!isEmptyLine(ob)) {
					String text = removeTabs(ob);
					text = removeNewLineCharacters(text);
					stringBuilder.append(text);
				}
			} else if (ob instanceof Italic) {
				stringBuilder.append(WikiMarkup.ITALIC);
				stringBuilder = processContent(((Italic) ob).getContent(), stringBuilder);
				stringBuilder.append(WikiMarkup.ITALIC);
			} else if (ob instanceof Bold) {
				stringBuilder.append(WikiMarkup.BOLD);
				stringBuilder = processContent(((Bold) ob).getContent(), stringBuilder);
				stringBuilder.append(WikiMarkup.BOLD);
			} else if (ob instanceof Section) {
				if (stringBuilder.lastIndexOf("\n") != stringBuilder.length() - 1) {
					stringBuilder.append(NEW_LINE);
				}
				String headingLevelMarkup = WikiMarkup.getHeadingLevel(++headingLevel);
				stringBuilder.append(headingLevelMarkup).append(((Section) ob).getHeading());
				stringBuilder.append(headingLevelMarkup).append(NEW_LINE);
				stringBuilder = processContent(((Section) ob).getContent(), stringBuilder);
				headingLevel--;
			}
		}
		return stringBuilder;
	}

	/**
	 * Check if text contains only tab and new line characters
	 * 
	 * @param text,
	 *            text to process
	 * @return boolean true if text contains only tab and new line characters
	 */
	private boolean isEmptyLine(Object text) {
		String result = ((String) text).replace("\t", "").replace("\n", "");
		if (result.length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Remove new line characters from the beginning of the string and remove duplicate
	 * new line characters from the end of the string
	 * 
	 * @param text,
	 *            text to process
	 * @return string with no leading new line characters and one or none new
	 *         line characters at the end of the string
	 */
	private String removeNewLineCharacters(String text) {
		while (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}
		while (text.endsWith("\n") && text.indexOf("\n") == text.length() - 2) {
			text = text.replaceFirst("\n", "");
		}
		return text;
	}

	/**
	 * Remove tab characters from the string
	 * 
	 * @param text,
	 *            text to process
	 * @return string with no tab characters
	 */
	private String removeTabs(Object text) {
		String result = ((String) text).replace("\t", "");
		return result;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
