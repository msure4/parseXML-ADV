package com.mohan.parseXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLWithSAXParserAdv {

	public static void main(String[] args) {
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			String metadataFile = "D:\\Users\\bramasam\\Downloads\\metadata.txt";
			String inputXMLFile = "D:\\Users\\bramasam\\Downloads\\xml.xml";
			final String outFilePath = "D:\\Users\\bramasam\\Documents\\mohan\\" + "\\";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			final HashMap<String, String> attributeHash = new HashMap<>();
			final HashMap<String, String> nodeParent = new HashMap<>();
			// final HashMap<String, Integer> writeFileMap = new HashMap<>();
			final HashMap<String, Integer> multipleOccurence = new HashMap<>();
			final HashMap<String, Integer> primaryKey = new HashMap<>();
			try {

				br = new BufferedReader(new FileReader(metadataFile));
				int iteration = 0;

				while ((line = br.readLine()) != null) {
				    if(iteration == 0) {
				        iteration++;  
				        continue;
				    }
					// use comma as separator
					String[] country = line.split(cvsSplitBy);

					if (Integer.parseInt(country[3].trim()) > 1) {
						multipleOccurence.put(country[0].trim(), 1);
						nodeParent.put(country[0].trim(), country[1]);
						if (primaryKey.size() == 0) {
							primaryKey.put(country[0], 1);
						}
					}
				}
			} finally {
				br.close();
			}

			DefaultHandler handler = new DefaultHandler() {

				StringBuilder stringBuilder = new StringBuilder();
				Boolean startWriting = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					String attrTag = "";
					for (int i = 0; i < attributes.getLength(); i++) {
						attrTag += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
					}
					attributeHash.put(qName, "<" + qName + attrTag + ">");

					String parentBuilder = "";

					if (primaryKey.containsKey(qName)) {
						// outFilePath + "/" + qName + "_" + index + ".txt";
						startWriting = true;
						String[] parentString = nodeParent.get(qName).split("/");
						for (int i = 0; i < parentString.length; i++) {
							if (parentString[i].length() > 0 || !parentString[i].equals(""))
								parentBuilder += attributeHash.getOrDefault(parentString[i],
										"<" + parentString[i] + ">");
							if ((parentString[i].length() > 0 || !parentString[i].equals(""))
									&& i < parentString.length - 1) {
								parentBuilder += System.lineSeparator();

								/*
								 * for (int j = i; j > 0; j--) parentBuilder += "  ";
								 */
							}
						}
						stringBuilder.append(parentBuilder);
					} else {
						if (startWriting)
							stringBuilder.append(attributeHash.get(qName));
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {

					if (startWriting)
						stringBuilder.append("</" + qName + ">");
					if (primaryKey.containsKey(qName)) {
						String parentBuilder = "";
						String[] parentString = nodeParent.get(qName).split("/");
						for (int i = parentString.length - 2; i >= 0; i--)
							if (parentString[i].length() > 0 || !parentString[i].equals("")) {
								parentBuilder += System.lineSeparator() + "</" + parentString[i] + ">";

								/*
								 * for (int j = i; j > 0; j--) parentBuilder += "  ";
								 */
							}
						stringBuilder.append(parentBuilder);

						XMLWithSAXParserAdv parserObj = new XMLWithSAXParserAdv();
						try {
							parserObj.prepareWrite(stringBuilder, qName, multipleOccurence.get(qName), outFilePath);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						parserObj.parseInnerTags(stringBuilder, primaryKey, multipleOccurence, outFilePath, nodeParent,
								attributeHash);

						multipleOccurence.put(qName, multipleOccurence.get(qName) + 1);
						startWriting = false;
						stringBuilder = new StringBuilder();
					}

				}

				@Override
				public void characters(char ch[], int start, int length) throws SAXException {
					String value = new String(ch, start, length);

					if (startWriting) {
						stringBuilder.append(value);
						/*
						 * if (value.trim().isEmpty()) stringBuilder.append(System.lineSeparator());
						 */
					}
				}

			};

			saxParser.parse(inputXMLFile, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void prepareWrite(StringBuilder stringBuilder, String qName, Integer index, String filePath)
			throws IOException {

		String fileDetail = filePath + "/" + qName + "_" + index + ".xml";
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {

			// Write the file output
			fw = new FileWriter(fileDetail, true);
			bw = new BufferedWriter(fw);

			bw.write(stringBuilder.toString());
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null)
				fw.flush();
			if (bw != null)
				bw.flush();
		}
	}

	private void parseInnerTags(StringBuilder stringBuilder, HashMap<String, Integer> primaryKey,
			HashMap<String, Integer> multipleOccurence, String filePath, HashMap<String, String> nodeParent,
			HashMap<String, String> attributeHash) {
		String parentBuilder = "";
		String trailerBuilder = "";
		Boolean startWrite = false;
		StringBuilder innerBuilder = new StringBuilder();
		for (String key : multipleOccurence.keySet()) {
			if (!primaryKey.containsKey(key)) {
				String[] parentString = nodeParent.get(key).split("/");
				for (int i = 0; i < parentString.length - 1; i++) {
					if (parentString[i].length() > 0 || !parentString[i].equals(""))
						parentBuilder += attributeHash.getOrDefault(parentString[i], "<" + parentString[i] + ">");
					if ((parentString[i].length() > 0 || !parentString[i].equals("")) && i < parentString.length - 1) 
						parentBuilder += System.lineSeparator();
					}

					for (int j = parentString.length - 3; j >= 0; j--)
						if (parentString[j].length() > 0 || !parentString[j].equals(""))
							trailerBuilder += System.lineSeparator() + "</" + parentString[j] + ">";

					String[] lines = stringBuilder.toString().split("\\n");

					for (String str : lines) {
						if (str.contains(key) && !str.trim().startsWith("</" + key)) {
							innerBuilder.append(parentBuilder);
							startWrite = true;
						}
						if (startWrite)
							innerBuilder.append(str + System.lineSeparator());

						if (str.contains(key) && str.trim().startsWith("</" + key)) {
							innerBuilder.append(trailerBuilder);

							XMLWithSAXParserAdv parserObj = new XMLWithSAXParserAdv();
							try {
								parserObj.prepareWrite(innerBuilder, key, multipleOccurence.get(key), filePath);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							multipleOccurence.put(key, multipleOccurence.get(key) + 1);
							innerBuilder = new StringBuilder();
							startWrite = false;
						}

					}
				}

			else
				continue;
		}

	}
}