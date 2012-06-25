package org.hornetq.util.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.util.reporter.Reporter;
import org.hornetq.util.xml.XmlDocUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerConfig
{
   private static final String ATTR_CLUSTERED = "Clustered";
   private static final String ATTR_IDCACHESIZE = "IDCacheSize";
   private static final String ATTR_SERVER_BIND_ADDR = "serverBindAddress";
   private static final String ATTR_SERVER_BIND_PORT = "serverBindPort";
   private static final String ATTR_KEYSTORE_URL = "KeyStoreURL";
   
   //client-failure-check-period
   private static final String ATTR_PING_TIMEOUT = "validatorPingTimeout";
   //connection-ttl-override (should be double the value)
   private static final String ATTR_LEASE_PERIOD = "clientLeasePeriod";
   
   private Map<String, Element> allJBMServerPeerAttrs;
   private Map<String, Element> allRemotingAttrs;
   private Map<String, Element> allSSLRemotingAttrs;
   private Map<String, Element> allPersistenceAttrs;
   private Map<String, Element> allPostOfficeAttrs;
   
   private RemotingConfig remotingConfig = null;
   
   //is this sufficient? is it possible some users are added after server started?
   private Map<String, Element> allUserManagerProperties;

   public void setJBMServerPeerAttrs(Map<String, Element> attrs)
   {
      this.allJBMServerPeerAttrs = attrs;
   }

   public void setRemotingAttrs(Map<String, Element> attrs)
   {
      this.allRemotingAttrs = attrs;
   }

   public void setPersistenceAttrs(Map<String, Element> attrs)
   {
      this.allPersistenceAttrs = attrs;
   }

   public void setPostOfficeAttrs(Map<String, Element> attrs)
   {
      this.allPostOfficeAttrs = attrs;
   }

   public void setUserManagerAttrs(Map<String, Element> attrs)
   {
      this.allUserManagerProperties = attrs;
   }

   public boolean isClustered(Boolean def)
   {
      Element e = allPostOfficeAttrs.get(ATTR_CLUSTERED);
      if (e == null) return def;
      String value = e.getTextContent();
      return Boolean.valueOf(value);
   }

   public int getIdCacheSize(int def)
   {
      Element e = allPersistenceAttrs.get(ATTR_IDCACHESIZE);
      if (e == null) return def;
      String value = e.getTextContent();
      return Integer.valueOf(value);
   }

   public RemotingConfig initRemoting() throws Exception
   {
       if (remotingConfig != null) return remotingConfig;
       
       Map<String, Object> params = new HashMap<String, Object>();
       String configName = "netty";
       
       long connectionTTLOverride = 60000L;
       long clientFailureCheckPeriod = 30000L;
       
       //transport
       Element e = allRemotingAttrs.get("Configuration");//remoting root
       NodeList nlist = e.getElementsByTagName("invoker");
       
       if (nlist.getLength() < 1)
       {
          throw new Exception("No <invoker> element in remoting configuration!");
       }
       
       Element invokerElem = (Element) nlist.item(0);
       String transport = invokerElem.getAttribute("transport");
       
       if ("bisocket".equals(transport))
       {
          //host
          String host = XmlDocUtil.getAttribute(invokerElem, ATTR_SERVER_BIND_ADDR, "localhost");
          params.put(TransportConstants.HOST_PROP_NAME, host);
          //port
          String port = XmlDocUtil.getAttribute(invokerElem, ATTR_SERVER_BIND_PORT, "5445");
          params.put(TransportConstants.PORT_PROP_NAME, port);
          
          //connection ttl
          String leasePing = XmlDocUtil.getAttribute(invokerElem, ATTR_LEASE_PERIOD, null);
          if (leasePing != null)
          {
             connectionTTLOverride = Long.valueOf(leasePing)*2;
          }
          
          //pingTimeout
          String pingTimeout = XmlDocUtil.getAttribute(invokerElem, ATTR_PING_TIMEOUT, null);
          if (pingTimeout != null)
          {
             clientFailureCheckPeriod = Long.valueOf(pingTimeout);
          }
       }
       else if ("sslbisocket".equals(transport))
       {
          configName = "netty-ssl";
          params.put(TransportConstants.SSL_ENABLED_PROP_NAME, Boolean.TRUE);
          //host
          String host = XmlDocUtil.getAttribute(invokerElem, ATTR_SERVER_BIND_ADDR, "localhost");
          params.put(TransportConstants.HOST_PROP_NAME, host);
          //port
          String port = XmlDocUtil.getAttribute(invokerElem, ATTR_SERVER_BIND_PORT, "5445");
          params.put(TransportConstants.PORT_PROP_NAME, port);
          
          //connection ttl
          String leasePing = XmlDocUtil.getAttribute(invokerElem, ATTR_LEASE_PERIOD, null);
          if (leasePing != null)
          {
             connectionTTLOverride = Long.valueOf(leasePing)*2;
          }
          
          //pingTimeout
          String pingTimeout = XmlDocUtil.getAttribute(invokerElem, ATTR_PING_TIMEOUT, null);
          if (pingTimeout != null)
          {
             clientFailureCheckPeriod = Long.valueOf(pingTimeout);
          }
          
          //keystore path
          Element eKeyStoreUrl = this.allSSLRemotingAttrs.get(ATTR_KEYSTORE_URL);
          String keyStoreUrl = "The key store";
          if (eKeyStoreUrl != null)
          {
             keyStoreUrl = XmlDocUtil.getAttribute(invokerElem, ATTR_KEYSTORE_URL, null);
          }
          else
          {
             Reporter.addWarning("No KeyStoreURL found in remoting config!");
          }
          params.put(TransportConstants.KEYSTORE_PATH_PROP_NAME, keyStoreUrl);
       }
       else if ("http".equals(transport))
       {
          configName = "netty-http";
       }
       else if ("servlet".equals(transport))
       {
          configName = "netty-servlet";
       }
       else
       {
          throw new Exception("Transport not supported: " + transport);
       }
       
       return null;
   }

   public void setSSLRemotingAttrs(Map<String, Element> attrs)
   {
      this.allSSLRemotingAttrs = attrs;
   }

   public TransportConfiguration createAcceptorConfig() {
      // TODO Auto-generated method stub
      return null;
   }

   public TransportConfiguration createConnectorConfig() {
      // TODO Auto-generated method stub
      return null;
   }
}
