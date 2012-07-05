package org.hornetq.util;

import java.util.Properties;

public class OptionKeys
{
   //the JBOSS_HOME path
   public static final String OPTION_JBOSS_HOME = "jboss.home";
   //the profiles where jbm is to be coverted to hornetq,
   //profiles are separated by commas
   public static final String OPTION_PROFILE = "jboss.profiles";
   //HornetQ targets, separated by commas
   //valid values: AS5, AS7, STANDALONE
   //default STANDALONE
   public static final String OPTION_TARGETS = "target.hornetq";

   //target hornetq version
   public static final String OPTION_TARGET_VERSION = "target.hornetq.version";
   
   //comma separated tasks
   //valid values: SERVER, DESTINATION, FACTORY, MESSAGE, BRIDGE, ALL
   //default ALL
   public static final String OPTION_TASKS = "migration.tasks";
   //where all the output goes.
   //default is ./workplace
   public static final String OPTION_WORKPLACE = "migration.workplace";
   
   //the directory name for deploy (normally not used).
   //default: deploy
   public static final String OPTION_DEPLOY_NAME = "profile.deploy.name";
   //the directory name where JBM configuration files reside
   //default: messaging
   public static final String OPTION_MESSAGING_NAME = "profile.messaging.name";
   //JBM server configuration file name
   //default: messaging-service.xml
   public static final String OPTION_SERVERPEER_CONFIG_NAME = "jbm.serverpeer.config.name";

   
   public static String getOption(String optionDeployName, Properties overrides, String defVal)
   {
      String val = overrides.getProperty(optionDeployName);
      return val == null ? defVal : val;
   }

}
