package org.hornetq.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.jms.Message;

public class JDBCJBMMessageReader implements MessageReader
{
   
   protected Connection connection;
   
   public static void main(String[] args) throws SQLException
   {
      JDBCJBMMessageReader reader = new JDBCJBMMessageReader();
      reader.close();
   }
   
   public JDBCJBMMessageReader() throws SQLException
   {
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/messaging?user=sa");
   }

   @Override
   public Message readMessage()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void close()
   {
      try
      {
         connection.close();
      }
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
