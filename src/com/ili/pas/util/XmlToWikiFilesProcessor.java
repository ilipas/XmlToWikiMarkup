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
 * Processes xml files from the specified directory and creates a new file
 * containing corresponding wiki markup in the specified output directory
 * 
 * @author ilijapasic
 *
 */
public class XmlToWikiFilesProcessor {

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private File outputDirectory;
	private static final String NEW_LINE = "\n";
	private static final String WIKI_FILE_EXTENTION = ".wiki";
	private int headingLevel;

	public XmlToWikiFilesProcessor(){
		
		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		headingLevel = 0;
	}

	@SuppressWarnings("deprecation")
	public void processFiles(File[] filesToProcess) {

		for (File xmlFile : filesToProcess) {
			System.out.println("Processing file: " + xmlFile);
			Report report = null;
			try {
				//check if xml file
				if(!xmlFile.getName().toLowerCase().endsWith(".xml")){
					System.out.println("Found non xml file " + xmlFile);
					continue;
				}
				// Unmarshal xml file
				report = (Report) unmarshaller.unmarshal(xmlFile);

				// Process results
				List<Object> reportContent = report.getContent();
				StringBuilder stringBuilder = processContent(reportContent, new StringBuilder());

				// Write result to .wiki file
				String filePath = outputDirectory + System.getProperty("file.separator")
						+ FilenameUtils.getBaseName(xmlFile.getName()) + WIKI_FILE_EXTENTION;
				
				System.out.println("Writing file :" + filePath);

				FileUtils.writeStringToFile(new File(filePath), stringBuilder.toString());
				
			} catch (IOException | JAXBException e) {
				System.out.println("Error processing file : " + xmlFile.getName());
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
					text = removeExtraNewLineCharacters(text);
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
	private static boolean isEmptyLine(Object text) {
		String result = ((String) text).replace("\t", "").replace("\n", "");
		if (result.length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Remove new line characters from the beginning of the string and duplicate
	 * new line characters at the end of the string
	 * 
	 * @param text,
	 *            text to process
	 * @return string with no leading new line characters and one or none new
	 *         line characters at the end of the string
	 */
	private static String removeExtraNewLineCharacters(String text) {
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
	private static String removeTabs(Object text) {
		String result = ((String) text).replace("\t", "");
		return result;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
