package org.hornetq.util;

public interface MigrationTask extends Comparable<MigrationTask>
{

   int getTaskId();
   
   void init(JBMToHornetQUtil util);

   void perform(ProfileInfo profile) throws Exception;

}
