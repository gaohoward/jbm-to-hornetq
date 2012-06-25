package org.hornetq.util;

import javax.jms.Message;

public interface MessageReader
{
   Message readMessage();

   public void close();

}
