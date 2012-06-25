package org.hornetq.util;

import javax.jms.Message;

public class MessageImporter
{
   
   public static void exportMessagesFromJBM(String toDir) throws Exception
   {
      MessageReader input = new JDBCJBMMessageReader();
      MessageWriter output = new XMLMessageWriter(toDir);

      exportMessage(input, output);
   }
   
   
   
   private static void exportMessage(MessageReader input,
         MessageWriter output)
   {
      try
      {
         Message msg = input.readMessage();
      
         while (msg != null)
         {
            output.writeMessage(msg);
            msg = input.readMessage();
         }
      }
      finally
      {
         input.close();
         output.close();
      }
   }



   /*
    * This method reads messages from JBM server and writes to HornetQ data store.
    * It is supposed that both JBM and HornetQ server config are readable to 
    * this method.
    */
   public static void importMessagesFromJBM(String jbmConfigDir, String serverConfigDir) throws Exception
   {
      MessageReader input = new JBMMessageReader(jbmConfigDir);
      MessageWriter output = new HornetQMessageWriter(serverConfigDir);

      exportMessage(input, output);
   }

}
