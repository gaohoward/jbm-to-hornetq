package org.hornetq.util;

import java.io.File;

import org.hornetq.util.config.ConfigSet;

public class ServerMigrationTask extends AbstractMigrationTask
{
   private JBMServerConfigReader reader = new JBMServerConfigReader();
   private XMLServerConfigWriter writer = new XMLServerConfigWriter();
   private HornetQServerWriter hornetqWriter = new HornetQServerWriter(); 

   @Override
   public void init(JBMToHornetQUtil util)
   {
      this.util = util;
   }

   @Override
   public void perform(ProfileInfo profile) throws Exception
   {
      log.info("Performing server migration for profile <" + profile.name + ">.");
      ConfigSet cfgSet = util.getConfig(profile);
      
      reader.readProfile(profile, cfgSet);
      File migrationDataDir = util.getMigrationDataDir(profile);
      
      log.info("Writing server config to " + migrationDataDir.getAbsolutePath());
      
      File xmlFile = writer.write(migrationDataDir, cfgSet);
      
      //generate hornetq stuff
      File baseOutputDir = util.getTargetBaseDir(profile);
      
      log.info("Generating hornetq server config in : " + baseOutputDir.getAbsolutePath());
      
      MigrationTarget[] targets = util.getMigrationTargets();
      
      for (MigrationTarget t : targets)
      {
         t.export(cfgSet.getServerConfig(), baseOutputDir);
      }
      
      hornetqWriter.write(xmlFile, baseOutputDir);
      log.info("Server configuration generated.");
   }

   @Override
   public int getTaskId()
   {
      return 5;
   }

}
