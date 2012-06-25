package org.hornetq.util;

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

}
