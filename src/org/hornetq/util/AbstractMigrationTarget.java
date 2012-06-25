package org.hornetq.util;

public abstract class AbstractMigrationTarget implements MigrationTarget
{
   protected JBMToHornetQUtil util;
   
   public void init(JBMToHornetQUtil jbmToHornetQUtil)
   {
      this.util = jbmToHornetQUtil;
   }

}
