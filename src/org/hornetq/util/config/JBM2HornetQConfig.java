package org.hornetq.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.hornetq.util.ProfileInfo;
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
import org.hornetq.util.xml.XMLConfigLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JBM2HornetQConfig
{
   private List<MigrationTask> tasks = new ArrayList<MigrationTask>();
   private List<JBMDeployment> jbmDeployments = new ArrayList<JBMDeployment>();

   /*
    * read the config file, default is jbm-to-hornetq.xml
    */
   public void load(File propFile) throws SAXException, IOException, ParserConfigurationException
   {
      Document document = XMLConfigLoader.load(propFile);
      
      parsingJBMDeployments(document);
      
   }
   
   private void parsingJBMDeployments(Document document)
   {
      Element elemJbmDeployments = (Element) document.getElementsByTagName("jbm-deployments").item(0);
      NodeList nodeListDeployment = elemJbmDeployments.getElementsByTagName("deployment");
      for (int i = 0; i < nodeListDeployment.getLength(); i++)
      {
         Element elemDeployment = (Element) nodeListDeployment.item(i);
         jbmDeployments.add(new JBMDeployment(elemDeployment));
      }
   }



   private String readJBMVersion(File jbmClientJar) throws ZipException, IOException
   {
      ZipFile zipFile = new ZipFile(jbmClientJar);
      ZipEntry entry = zipFile.getEntry("VERSION");
      InputStream input = zipFile.getInputStream(entry);
      Properties versionInfo = new Properties();
      versionInfo.load(input);
      
      return versionInfo.getProperty("jboss.messaging.providerIncrementingVersion");
   }

   private void addTargets(String[] targetNames) throws Exception
   {
      for (String name : targetNames)
      {
         String targetName = name.toUpperCase();
         MigrationTarget target = VALID_TARGETS.get(targetName);

         if (target == null)
         {
            throw new Exception("Invalid target: " + name
                  + ", should be one of AS7, AS5, or STANDALONE.");
         }

         target.init(this);
         targets.put(name, target);
      }
   }

   private void addTasks(String[] taskNames) throws Exception
   {
      for (String name : taskNames)
      {
         String taskName = name.toUpperCase();
         
         if ("ALL".equals(taskName))
         {
            tasks.putAll(VALID_TASKS);
            break;
         }
         
         MigrationTask task = VALID_TASKS.get(taskName);
         if (task == null)
         {
            throw new Exception("Invalid task: " + name + ", should be one of SERVER, DESTINATION, FACTORY, MESSAGE, BRIDGE and ALL");
         }
         
         tasks.put(name, task);
      }
   }

   private static File getDir(String jbPath) throws Exception
   {
      File jbHome = new File(jbPath);
      if (!jbHome.exists())
      {
         throw new Exception("Specified path doesn't exist! " + jbPath);
      }
      if (!jbHome.isDirectory())
      {
         throw new Exception("Invalid path, should point to a directory! " + jbPath);
      }
      
      return jbHome;
   }

   // workplace/migration-data/<profile_name>
   public File getMigrationDataDir(ProfileInfo profile) throws Exception
   {
      File parentDir = new File(workplace, "migration_data");
      File dir = new File(parentDir, profile.getName());
      
      if (dir.exists())
      {
         if (dir.isDirectory())
         {
            return dir;
         }
         else
         {
            throw new Exception("File: " + dir.getAbsolutePath() + " already exist but not a directory!");
         }
      }
      dir.mkdirs();
      return dir;
   }

   // workplace/target/<profile name>/
   public File getTargetBaseDir(ProfileInfo profile) throws Exception
   {
      File parentDir = new File(workplace, "target");
      File dir = new File(parentDir, profile.getName());
      
      if (dir.exists())
      {
         if (dir.isDirectory())
         {
            return dir;
         }
         else
         {
            throw new Exception("File: " + dir.getAbsolutePath() + " already exist but not a directory!");
         }
      }
      dir.mkdirs();
      return dir;
   }

   public ConfigSet getConfig(ProfileInfo profile)
   {
      ConfigSet cfgSet = cfgSets.get(profile.getName());
      if (cfgSet == null)
      {
         cfgSet = new ConfigSet();
         cfgSets.put(profile.getName(), cfgSet);
      }
      return cfgSet;
   }

   public List<MigrationTask> getTasks()
   {
      return tasks;
   }

   public List<ProfileInfo> getProfiles()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   private static final Map<String, MigrationTask> VALID_TASKS = new HashMap<String, MigrationTask>();
   
   private static final Map<String, MigrationTarget> VALID_TARGETS = new HashMap<String, MigrationTarget>();
   
   
   
   static 
   {
      VALID_TASKS.put("SERVER", new ServerMigrationTask());
      VALID_TASKS.put("DESTINATION", new DestinationMigrationTask());
      VALID_TASKS.put("FACTORY", new FactoryMigrationTask());
      VALID_TASKS.put("MESSAGE", new MessageMigrationTask());
      VALID_TASKS.put("BRIDGE", new BridgeMigrationTask());
      
      VALID_TARGETS.put("STANDALONE", new StandAloneTarget());
      VALID_TARGETS.put("AS7", new AS7Target());
      VALID_TARGETS.put("AS5", new AS5Target());
   }

}
