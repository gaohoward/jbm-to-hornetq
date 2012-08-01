package org.hornetq.util.config;

import org.w3c.dom.Element;

public class JBMProfile
{
   private String profileName;
   private String actionName;
   
   private JBMRemotingConfig remotingConfig;
   private JBMServerConfig serverConfig;
   private JBMPersistenceConfig persistenceConfig;
   private JBMDestinationConfig destinationConfig;
   private JBMConnectionFactoryConfig connectionFactoryConfig;
   private JBMBridgeConfig bridgeConfig;

   public JBMProfile(Element elemProfile)
   {
      profileName = elemProfile.getAttribute("name");
      actionName = elemProfile.getAttribute("action");
      
      remotingConfig = parseRemotingConfig(elemProfile);
      serverConfig = parseServerConfig(elemProfile);
      persistenceConfig = parsePersistenceConfig(elemProfile);
      destinationConfig = parseDestinationConfig(elemProfile);
      connectionFactoryConfig = parseConnectionFactoryConfig(elemProfile);
      bridgeConfig = parseBridgeConfig(elemProfile);
   }

   private JBMBridgeConfig parseBridgeConfig(Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

   private JBMConnectionFactoryConfig parseConnectionFactoryConfig(
         Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

   private JBMDestinationConfig parseDestinationConfig(Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

   private JBMPersistenceConfig parsePersistenceConfig(Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

   private JBMServerConfig parseServerConfig(Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

   private JBMRemotingConfig parseRemotingConfig(Element elemProfile)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
