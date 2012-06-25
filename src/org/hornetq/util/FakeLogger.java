package org.hornetq.util;

public class FakeLogger
{

   public void warn(String string)
   {
      stdout("WARN: " + string);
   }
   
   private void stdout(String str)
   {
      System.out.println(str);
   }

   public void info(String string)
   {
      stdout("INFO: " + string);
   }

}
