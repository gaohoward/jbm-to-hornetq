package org.hornetq.util.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hornetq.util.FakeLogger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLConfigLoader
{
   private static FakeLogger log = new FakeLogger();

   public static Document load(File xmlFile) throws SAXException, IOException, ParserConfigurationException
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      
      return document;
   }

}
