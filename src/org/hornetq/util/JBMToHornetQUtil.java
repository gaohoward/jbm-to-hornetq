package org.hornetq.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.hornetq.util.config.ConfigSet;
import org.hornetq.util.config.JBM2HornetQConfig;
import org.hornetq.util.targets.AS5Target;
import org.hornetq.util.targets.AS7Target;
import org.hornetq.util.targets.MigrationTarget;
import org.hornetq.util.targets.StandAloneTarget;
import org.hornetq.util.tasks.BridgeMigrationTask;
import org.hornetq.util.tasks.DestinationMigrationTask;
import org.hornetq.util.tasks.FactoryMigrationTask;
import org.hornetq.util.tasks.MessageMigrationTask;
import org.hornetq.util.tasks.MigrationTask;
import org.hornetq.util.tasks.ServerMigrationTask;
import org.xml.sax.SAXException;

/**
 * 
 * This collection of utilities aids users to migrate their existing JBM deployments to HornetQ.
 * 
 * General Input:
 * 
 * JBOSS HOME, server profiles to migrate, target types AS7, AS5, AS4 or Standalone, target versions, tasks
 * 
 * Tasks needed:
 * 
 * 1. Server configuration
 * 
 * create hornetq server configs based on JBM server config (like server peer, postoffice, remoting, users etc)
 * 
 * 2. Destinations
 * 
 * 3. factories
 * 
 * 4. messages (requires destinations).
 * 
 * 5. bridges
 * 
 * The four tasks can be performed together or separately.
 * 
 * @author howard
 *
 */
public class JBMToHornetQUtil
{
   //default option properties file
   public static final File DEFAULT_OPT_FILE = new File("jbm-to-hornetq.xml");

   private static final FakeLogger log = new FakeLogger();
   
   private JBM2HornetQConfig config = null;

   public JBMToHornetQUtil(JBM2HornetQConfig props) throws Exception
   {
      this.config = props;
   }
   
   public static void main(String[] args) throws Exception
   {
      JBM2HornetQConfig props = preprocessing(args);
      
      JBMToHornetQUtil util = new JBMToHornetQUtil(props);
      util.process();
   }
   
   private static boolean helpFlag(String flag)
   {
      if ("?".equals(flag) || 
          "-help".equals(flag) ||
          "--help".equals(flag) ||
          "/?".equals(flag) ||
          "-?".equals(flag) ||
          "/help".equals(flag))
      {
         return true;
      }
      return false;
   }
   
   private static void loadPropFile(File propFile, JBM2HornetQConfig config) throws IOException, SAXException, ParserConfigurationException
   {
      config.load(propFile);
   }
   
   //validate options
   private static JBM2HornetQConfig preprocessing(String[] args) throws IOException, SAXException, ParserConfigurationException
   {
      JBM2HornetQConfig config = new JBM2HornetQConfig();
      
      boolean propLoaded = false;
      
      if (args.length > 0)
      {
         String arg0 = args[0];
         
         if (helpFlag(arg0))
         {
            printUsage();
            return null;
         }
         
         if (arg0.startsWith("-"))
         {
            throw new IllegalArgumentException("Unknown option " + arg0);
         }

         File propFile = new File(arg0);
         if (propFile.isFile())
         {
            loadPropFile(propFile, config);
            propLoaded = true;
         }
         else
         {
            log.warn("Property file: " + arg0 + " does not exist, Ignoring.");
         }
      }
      
      if (!propLoaded)
      {
         if (DEFAULT_OPT_FILE.exists())
         {
            log.info("Trying to load default file " + DEFAULT_OPT_FILE.getAbsolutePath());
            loadPropFile(DEFAULT_OPT_FILE, config);
         }
      }

      log.info("Options prepared.");
      return config;
   }

   private void process() throws Exception
   {
      log.info("Starting to process...");
      
      //do tasks for each profile
      List<MigrationTask> taskList = config.getTasks();
      
      List<ProfileInfo> profiles = config.getProfiles();
      
      for (ProfileInfo profile : profiles)
      {
         for (MigrationTask t : taskList)
         {
            log.info("------Performing task: " + t + " for profile " + profile.getName());
            t.perform(profile);
         }
      }
      
      log.info("Done.");
   }

   private static void printUsage()
   {
      System.out.println("Usage: ");
      System.out.println("java " + JBMToHornetQUtil.class.getName() + " options");
      System.out.println("where options can be:");
      System.out.println("-? or -help : get this information, or");
      System.out.println("<property file location> (default is default.properties)");
   }
   
}
