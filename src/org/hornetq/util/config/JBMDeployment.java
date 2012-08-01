package org.hornetq.util.config;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JBMDeployment
{
   private String jbossHome;
   private List<JBMProfile> profiles = new ArrayList<JBMProfile>();

   public JBMDeployment(Element elemDeployment)
   {
      jbossHome = elemDeployment.getAttribute("jboss-home");
      NodeList nodeListProfile = elemDeployment.getElementsByTagName("profile");
      for (int i = 0; i < nodeListProfile.getLength(); i++)
      {
         Element elemProfile = (Element) nodeListProfile.item(i);
         profiles.add(new JBMProfile(elemProfile));
      }
   }

}
