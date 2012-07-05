package org.hornetq.util.tasks;

import org.hornetq.util.JBMToHornetQUtil;
import org.hornetq.util.ProfileInfo;

public interface MigrationTask extends Comparable<MigrationTask>
{

   int getTaskId();
   
   void init(JBMToHornetQUtil util);

   void perform(ProfileInfo profile) throws Exception;

}
