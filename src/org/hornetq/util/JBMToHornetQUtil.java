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

import org.hornetq.util.config.ConfigSet;

/**
 * 
 * This collection of utilities aids users to migrate their existing JBM deployments to HornetQ.
 * 
 * General Input:
 * 
 * JBOSS HOME, server profiles to migrate, target types AS7, AS5 or Standalone, tasks
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
   public static final File DEFAULT_OPT_FILE = new File("default.properties");
   //default target
   public static final String DEFAULT_TARGET = "STANDALONE";
   //default workplace
   public static final String DEFAULT_WORKPLACE = "workplace";
   //default task
   public static final String DEFAULT_TASK = "ALL";

   private static final FakeLogger log = new FakeLogger();
   
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

   private File jbHome;
   private int jbmVersion;
   private String targetVersion;
   
   private List<ProfileInfo> profiles = new ArrayList<ProfileInfo>();
   private File workplace;

   private Map<String, MigrationTarget> targets = new HashMap<String, MigrationTarget>();

   private Map<String, MigrationTask> tasks = new HashMap<String, MigrationTask>();
   
   private Map<String, ConfigSet> cfgSets = new HashMap<String, ConfigSet>();
   
   
   public JBMToHornetQUtil(Properties props) throws Exception
   {
      //first is jboss.home
      String jbPath = props.getProperty(OptionKeys.OPTION_JBOSS_HOME);
      if (jbPath == null)
      {
         throw new Exception("Missing property: " + OptionKeys.OPTION_JBOSS_HOME);
      }
      jbHome = getDir(jbPath);
      
      //jbm version
      File clientDir = new File(jbHome, "client");
      File jbmClientJar = new File(clientDir, "jboss-messaging-client.jar");
      
      String version = readJBMVersion(jbmClientJar);
      jbmVersion = Integer.valueOf(version);
      
      log.info("JBM VERSION is " + jbmVersion);
      
      //second the profiles
      String profileValue = props.getProperty(OptionKeys.OPTION_PROFILE);
      if (profileValue == null)
      {
         throw new Exception("Missing property: " + OptionKeys.OPTION_PROFILE);
      }

      String[] profileNames = profileValue.split(",");
      
      for (String p : profileNames)
      {
         String profilePath = jbHome.getAbsolutePath() + File.separator + "server" + File.separator + p;
         File profileDir = getDir(profilePath);
         profiles.add(new ProfileInfo(p, profileDir));
      }
      
      //the targets
      String targetValue = props.getProperty(OptionKeys.OPTION_TARGETS);
      if (targetValue == null)
      {
         log.info("No targets specified. default to " + DEFAULT_TARGET);
         targetValue = DEFAULT_TARGET;
      }
      String[] targetNames = targetValue.split(",");

      addTargets(targetNames);
      
      String taskValue = props.getProperty(OptionKeys.OPTION_TASKS);
      if (taskValue == null)
      {
         log.info("No tasks specified, default to " + DEFAULT_TASK);
         taskValue = DEFAULT_TASK;
      }
      String[] taskNames = taskValue.split(",");
      
      addTasks(taskNames);

      //workplace
      String workplaceValue = props.getProperty(OptionKeys.OPTION_WORKPLACE);
      if (workplaceValue == null)
      {
         log.info("No workplace specified, default to " + DEFAULT_WORKPLACE);
         workplaceValue = DEFAULT_WORKPLACE;
      }
      
      workplace = new File(workplaceValue);
      
      if (workplace.exists() && workplace.isFile())
      {
         throw new Exception("Workplace: " + workplace + " is already a file, please specify a different one.");
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
   
   public static void main(String[] args) throws Exception
   {
      Properties props = preprocessing(args);
      if (props == null) return;
      
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
   
   private static void loadPropFile(File propFile, Properties prop) throws IOException
   {
      FileReader reader = null;
      try
      {
         reader = new FileReader(propFile);
         prop.load(reader);
         reader.close();
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }
      }
   }
   
   //validate options
   private static Properties preprocessing(String[] args) throws IOException
   {
      Properties prop = new Properties();
      
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
         
         // must be a properties file!
         File propFile = new File(arg0);
         if (propFile.isFile())
         {
            loadPropFile(propFile, prop);
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
            loadPropFile(DEFAULT_OPT_FILE, prop);
         }
      }
      
      //now examine props passed from comand line, and override the above props.
      String jbHome = System.getProperty(OptionKeys.OPTION_JBOSS_HOME);
      if (jbHome != null)
      {
         prop.put(OptionKeys.OPTION_JBOSS_HOME, jbHome);
      }
      
      String profiles = System.getProperty(OptionKeys.OPTION_PROFILE);
      if (profiles != null)
      {
         prop.put(OptionKeys.OPTION_PROFILE, profiles);
      }
      
      String targets = System.getProperty(OptionKeys.OPTION_TARGETS);
      if (targets != null)
      {
         prop.put(OptionKeys.OPTION_TARGETS, targets);
      }
      
      String tasks = System.getProperty(OptionKeys.OPTION_TASKS);
      if (tasks != null)
      {
         prop.put(OptionKeys.OPTION_TASKS, tasks);
      }
      
      String workplace = System.getProperty(OptionKeys.OPTION_WORKPLACE);
      if (workplace != null)
      {
         prop.put(OptionKeys.OPTION_WORKPLACE, workplace);
      }
      
      log.info("Options prepared.");
      return prop;
   }

   private void process() throws Exception
   {
      log.info("Starting to process...");
      log.info("Using options: \n" + this);
      
      //do tasks for each profile
      List<MigrationTask> taskList = new ArrayList<MigrationTask>();
      Iterator<MigrationTask> iterTasks = tasks.values().iterator();
      while (iterTasks.hasNext())
      {
         MigrationTask task = iterTasks.next();
         task.init(this);
         taskList.add(task);
      }

      Collections.sort(taskList);
      
      for (ProfileInfo profile : profiles)
      {
         for (MigrationTask t : taskList)
         {
            log.info("------Performing task: " + t + " for profile " + profile.name);
            t.perform(profile);
         }
      }
      
      log.info("Done.");
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer("JBoss Home: ");
      sb.append(this.jbHome.getAbsolutePath());
      sb.append("\n");
      sb.append("Profiles: ");
      for (ProfileInfo p : profiles)
      {
         sb.append(p.name + " ");
      }
      sb.append("\n");
      sb.append("Migration target: ");
      Iterator<String> keys = targets.keySet().iterator();
      while (keys.hasNext())
      {
         sb.append(keys.next() + " ");
      }
      sb.append("\n");
      sb.append("Tasks: ");
      Iterator<String> taskKeys = tasks.keySet().iterator();
      while (taskKeys.hasNext())
      {
         sb.append(taskKeys.next() + " ");
      }
      sb.append("\n");
      sb.append("Workplace: ");
      sb.append(workplace.getAbsolutePath());
      sb.append("\n");
      
      return sb.toString();
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

   private static void printUsage()
   {
      System.out.println("Usage: ");
      System.out.println("java " + JBMToHornetQUtil.class.getName() + " options");
      System.out.println("where options can be:");
      System.out.println("-? or -help : get this information");
      System.out.println("jboss.home and profile points to where your JBM deployment is.");
      System.out.println("target is one of AS7, AS5 and STANDALONE (if you give null, default to STANDALONE)");
      System.out.println("tasks can be one or more values from SERVER, DESTINATION, FACTORY, and MESSAGES");
      System.out.println("if no tasks given it defaults to SERVER");
   }

   // workplace/migration-data/<profile_name>
   public File getMigrationDataDir(ProfileInfo profile) throws Exception
   {
      File parentDir = new File(workplace, "migration_data");
      File dir = new File(parentDir, profile.name);
      
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
      File dir = new File(parentDir, profile.name);
      
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
      ConfigSet cfgSet = cfgSets.get(profile.name);
      if (cfgSet == null)
      {
         cfgSet = new ConfigSet();
         cfgSets.put(profile.name, cfgSet);
      }
      return cfgSet;
   }

   public MigrationTarget[] getMigrationTargets()
   {
      return this.targets.values().toArray(new MigrationTarget[0]);
   }
   
}
