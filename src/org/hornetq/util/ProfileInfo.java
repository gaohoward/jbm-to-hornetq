package org.hornetq.util;

import java.io.File;

public class ProfileInfo
{
   public String name;
   public File loc;

   public ProfileInfo(String name, File loc)
   {
      this.name = name;
      this.loc = loc;
   }
}
