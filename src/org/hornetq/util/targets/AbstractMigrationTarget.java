package org.hornetq.util.targets;

import org.hornetq.util.JBMToHornetQUtil;

public abstract class AbstractMigrationTarget implements MigrationTarget
{
   protected JBMToHornetQUtil util;
   
   public void init(JBMToHornetQUtil jbmToHornetQUtil)
   {
      this.util = jbmToHornetQUtil;
   }

}
