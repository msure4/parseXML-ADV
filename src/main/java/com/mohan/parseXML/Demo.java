package com.mohan.parseXML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

public class Demo {

	public static void main(String[] args) throws Exception {
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLEventReader xsr =
				xif.createXMLEventReader(new FileReader("D:\\Users\\bramasam\\Downloads\\xml.xml"));
        XMLEvent event = xsr.nextEvent();
		xsr.nextTag(); // Advance to statements element
		int i = 0;
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		while (xsr.hasNext()) {
            StartElement startElement = event.asStartElement();
            String qName = startElement.getName().getLocalPart();

			if(event.getEventType() == XMLStreamConstants.CHARACTERS)
				System.out.println(xsr.getElementText());
			File file = new File("D:\\\\Users\\\\bramasam\\\\Downloads\\\\out" + i + ".xml");
			FileOutputStream fos = new FileOutputStream(file, true);
			t.transform(new StAXSource(xsr), new StreamResult(fos));
			i++;
			xsr.nextTag();
		}
	}

}