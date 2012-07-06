package org.hornetq.util;

import java.io.File;
import java.util.Properties;

/*
 * This class should provide all the server deployment informations
 * of JBM. It should be able to give the exact configuration file,
 * for example, without the caller knowing of any details of it's
 * location, file name, etc.
 */
public class ProfileInfo
{
   private String name;
   private File loc;
   private Properties overrides;

   public ProfileInfo(String name, File loc, Properties overrides)
   {
      this.name = name;
      this.loc = loc;
      this.overrides = overrides;
   }

   public String getName()
   {
      return name;
   }

   //returning the messaing-service.xml (server peer config)
   public File getMessagingConfigFile() throws Exception
   {
      File jbmCfgDir = getJbmCfgDir();
      
      // now read "messaging-service.xml"
      String jbmServerPeerFileName = OptionKeys.getOption(OptionKeys.OPTION_SERVERPEER_CONFIG_NAME, overrides, "messaging-service.xml");
      File messagingConfigFile = new File(jbmCfgDir, jbmServerPeerFileName);

      if (!messagingConfigFile.exists())
      {
         throw new Exception("Cannot find server config file: "
               + messagingConfigFile.getAbsolutePath());
      }
      
      return messagingConfigFile;
   }

   public File getPersistenceConfigFile() throws Exception
   {
      File jbmCfgDir = getJbmCfgDir();
      String persistenceName = OptionKeys.getOption(OptionKeys.OPTION_PERSISTENCE_CFG_NAME, overrides, null);
      
      return null;
   }

   private File getJbmCfgDir() throws Exception
   {
      String deployName = OptionKeys.getOption(OptionKeys.OPTION_DEPLOY_NAME, overrides, "deploy");
      File deployDir = new File(loc, deployName);

      if (!deployDir.exists())
      {
         throw new Exception("Directory: " + deployDir
               + " doesn't exists. Invalid JBoss Home?");
      }

      String messagingName = OptionKeys.getOption(OptionKeys.OPTION_MESSAGING_NAME, overrides, "messaging");
      File jbmCfgDir = new File(deployDir, messagingName);

      if (!jbmCfgDir.exists())
      {
         //try AS4
         jbmCfgDir = new File(deployDir, "jboss-messaging.sar");
      }

      if (!jbmCfgDir.exists())
      {
         throw new Exception("Cannot find JBM config dir under "
               + deployDir.getAbsolutePath());
      }
      return jbmCfgDir;
   }
}
