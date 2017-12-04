package com.mohan.parseXML;

import java.io.*;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

public class Demo2 {

	public static void main(String[] args) throws Exception {
		Demo2 demo = new Demo2();
		demo.split("D:\\Users\\bramasam\\Downloads\\xml.xml", null);
		// demo.split("src/forum7408938/input.xml", null);
	}

	private void split(String xmlResource, String condition) throws Exception {
		XMLEventFactory xef = XMLEventFactory.newFactory();
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLEventReader xer = xif.createXMLEventReader(new FileReader(xmlResource));
		StartElement rootStartElement = xer.nextTag().asStartElement(); // Advance to statements element
		StartDocument startDocument = xef.createStartDocument();
		EndDocument endDocument = xef.createEndDocument();

		XMLOutputFactory xof = XMLOutputFactory.newFactory();
		while (xer.hasNext() && !xer.peek().isEndDocument()) {
			boolean metCondition;
			XMLEvent xmlEvent = xer.nextTag();
			if (!xmlEvent.isStartElement()) {
				break;
			}
			// Be able to split XML file into n parts with x split elements(from
			// the dummy XML example staff is the split element).
			StartElement breakStartElement = xmlEvent.asStartElement();
			List<XMLEvent> cachedXMLEvents = new ArrayList<XMLEvent>();

			// BOUNTY CRITERIA
			// I'd like to be able to specify condition that must be in the
			// split element i.e. I want only staff which have nickname, I want
			// to discard those without nicknames. But be able to also split
			// without conditions while running split without conditions.
			if (null == condition) {
				cachedXMLEvents.add(breakStartElement);
				metCondition = true;
			} else {
				cachedXMLEvents.add(breakStartElement);
				xmlEvent = xer.nextEvent();
				metCondition = false;
				while (!(xmlEvent.isEndElement()
						&& xmlEvent.asEndElement().getName().equals(breakStartElement.getName()))) {
					cachedXMLEvents.add(xmlEvent);
					if (xmlEvent.isStartElement()
							&& xmlEvent.asStartElement().getName().getLocalPart().equals(condition)) {
						metCondition = true;
						break;
					}
					xmlEvent = xer.nextEvent();
				}
			}

			if (metCondition) {
				// Create a file for the fragment, the name is derived from the value of the id
				// attribute
				FileWriter fileWriter = null;
				fileWriter = new FileWriter("D:\\Users\\bramasam\\Downloads\\"
						+ "new" + ".xml");

				// A StAX XMLEventWriter will be used to write the XML fragment
				XMLEventWriter xew = xof.createXMLEventWriter(fileWriter);
				xew.add(startDocument);

				// BOUNTY CRITERIA
				// The content of the spitted files should be wrapped in the
				// root element from the original file(like in the dummy example
				// company)
				xew.add(rootStartElement);

				// Write the XMLEvents that were cached while when we were
				// checking the fragment to see if it matched our criteria.
				for (XMLEvent cachedEvent : cachedXMLEvents) {
					xew.add(cachedEvent);
				}

				// Write the XMLEvents that we still need to parse from this
				// fragment
				xmlEvent = xer.nextEvent();
				while (xer.hasNext() && !(xmlEvent.isEndElement()
						&& xmlEvent.asEndElement().getName().equals(breakStartElement.getName()))) {
					xew.add(xmlEvent);
					xmlEvent = xer.nextEvent();
				}
				xew.add(xmlEvent);

				// Close everything we opened
				xew.add(xef.createEndElement(rootStartElement.getName(), null));
				xew.add(endDocument);
				fileWriter.close();
			}
		}
	}
}