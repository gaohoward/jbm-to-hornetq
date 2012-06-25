package org.hornetq.util.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hornetq.utils.XMLConfigurationUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlDocUtil extends XMLConfigurationUtil
{
   public static Map<String, Element> getMBeanAttributes(Document document, String mbeanCode)
   {
       Map<String, Element> attrs = new HashMap<String, Element>();
      
      NodeList nlist = document.getElementsByTagName("mbean");
      
      Element beanNode = null;
      
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Element elem = (Element)nlist.item(i);
         String attr = elem.getAttribute("code");
         if (mbeanCode.equals(attr))
         {
            beanNode = elem;
            break;
         }
      }
      
      if (beanNode != null)
      {
         nlist = beanNode.getElementsByTagName("attribute");
         for (int i = 0; i < nlist.getLength(); i++)
         {
            Element attrElem = (Element)nlist.item(i);
            String attrName = attrElem.getAttribute("name");
            
            attrs.put(attrName, attrElem);
         }
      }
      
      return attrs;
   }

   public static String getAttribute(Element elem, String attr, String def)
   {
      NodeList nlist = elem.getElementsByTagName("attribute");
      String value = null;
      
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Element node = (Element)nlist.item(i);
         if (attr.equals(node.getAttribute("name")))
         {
            value = node.getTextContent();
            break;
         }
      }
      
      if (value == null)
      {
         value = def;
      }
      return value;
   }

}
