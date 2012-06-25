package org.hornetq.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.util.config.ConfigSet;
import org.hornetq.util.config.ServerConfig;
import org.hornetq.util.xml.XMLConfigLoader;
import org.hornetq.util.xml.XmlDocUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class JBMServerConfigReader
{
   private static final FakeLogger log = new FakeLogger();

   public ServerConfig readProfile(ProfileInfo profile, ConfigSet cfgSet) throws Exception
   {
      ServerConfig config = cfgSet.getServerConfig();
      
      File deployDir = new File(profile.loc, "deploy");
      
      if (!deployDir.exists())
      {
         throw new Exception("Directory: " + deployDir + " doesn't exists. Invalid JBoss Home?");
      }
      
      File jbmCfgDir = new File(deployDir, "messaging");
      
      if (!jbmCfgDir.exists())
      {
         //try AS4
         jbmCfgDir = new File(deployDir, "jboss-messaging.sar");
      }
      
      if (!jbmCfgDir.exists())
      {
         throw new Exception("Cannot find JBM config dir under " + deployDir.getAbsolutePath());
      }
      
      log.info("JBM config dir: " + jbmCfgDir.getAbsolutePath());
      
      //now read "messaging-service.xml"
      File messagingConfigFile = new File(jbmCfgDir, "messaging-service.xml");
      
      if (!messagingConfigFile.exists())
      {
         throw new Exception("Cannot find server config file: " + messagingConfigFile.getAbsolutePath());
      }
      readServerConfig(messagingConfigFile, config);
      
      File persistFile = null;
      File remotingFile = null;

      File[] files = jbmCfgDir.listFiles(new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            if (name.endsWith(".xml"))
            {
               return true;
            }
            return false;
         }
      });
      
      for (File f : files)
      {
         if (f.getName().contains("persistence-service"))
         {
            log.info("Found persistence config file: " + f.getAbsolutePath());
            persistFile = f;
         }
         else if (f.getName().contains("remoting"))
         {
            log.info("Found remoting config file: " + f.getAbsolutePath());
            remotingFile = f;
         }
      }
      
      if (persistFile == null)
      {
         throw new Exception("Persistence configuration file not found!");
      }
      if (remotingFile == null)
      {
         throw new Exception("Remoting configuration file not found!");
      }
      
      //xxx-persistence-service.xml
      readPersistenceConfig(persistFile, config);
      //remoting-service.xml
      readRemotingConfig(remotingFile, config);
      
      return config;
   }

   private void readRemotingConfig(File remotingFile, ServerConfig config) throws SAXException, IOException, ParserConfigurationException
   {
      Document document = XMLConfigLoader.load(remotingFile);
      Map<String, Element> attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.remoting.transport.Connector");
      
      config.setRemotingAttrs(attrs);
      
      attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.remoting.security.SSLSocketBuilder");
      
      config.setSSLRemotingAttrs(attrs);

   }

   private void readPersistenceConfig(File persistFile, ServerConfig config) throws SAXException, IOException, ParserConfigurationException
   {
      Document document = XMLConfigLoader.load(persistFile);
      Map<String, Element> attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.messaging.core.jmx.JDBCPersistenceManagerService");
      
      config.setPersistenceAttrs(attrs);      

      attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.messaging.core.jmx.MessagingPostOfficeService");
      config.setPostOfficeAttrs(attrs);      

      attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.jms.server.plugin.JDBCJMSUserManagerService");
      config.setUserManagerAttrs(attrs);      

   }

   private void readServerConfig(File messagingConfigFile, ServerConfig config) throws ParserConfigurationException, SAXException, IOException
   {
      Document document = XMLConfigLoader.load(messagingConfigFile);
      Map<String, Element> attrs = XmlDocUtil.getMBeanAttributes(document, "org.jboss.jms.server.ServerPeer");
      
      config.setJBMServerPeerAttrs(attrs);
   }

}
