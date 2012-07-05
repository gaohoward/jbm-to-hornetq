package org.hornetq.util.tasks;

import org.hornetq.util.FakeLogger;
import org.hornetq.util.JBMToHornetQUtil;

public abstract class AbstractMigrationTask implements MigrationTask
{
   protected static final FakeLogger log = new FakeLogger();
   
   protected JBMToHornetQUtil util;

   @Override
   public int compareTo(MigrationTask o)
   {
      if (this.getTaskId() < ((MigrationTask)o).getTaskId())
      {
         return -1;
      }
      if (this.getTaskId() > ((MigrationTask)o).getTaskId())
      {
         return 1;
      }
      return 0;
   }

   @Override
   public void init(JBMToHornetQUtil util)
   {
      this.util = util;
   }

}
