package org.hornetq.util.exporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.util.config.ServerConfig;

public class StandaloneServerExporter
{
   private ServerConfig config;
   private File baseOutputDir;

   public StandaloneServerExporter(ServerConfig config, File baseOutputDir)
   {
      this.config = config;
      this.baseOutputDir = baseOutputDir;
   }

   public void generateServer()
   {
       File outputDir = new File(baseOutputDir, "standalone");
       outputDir.mkdirs();
       
       ConfigurationImpl hqConfig = new ConfigurationImpl();
       
       hqConfig.setClustered(config.isClustered(false));
       hqConfig.setSharedStore(config.isClustered(false));
       
       //jbm doesn't have the scan period, so disable it
       hqConfig.setMessageExpiryScanPeriod(-1);
       
       hqConfig.setIDCacheSize(config.getIdCacheSize(ConfigurationImpl.DEFAULT_ID_CACHE_SIZE));
       
       //client-lease
       hqConfig.setConnectionTTLOverride(0);
       
       //remoting --> netty
       TransportConfiguration acceptorConfig = config.createAcceptorConfig();
       TransportConfiguration connectorConfig = config.createConnectorConfig();
       
//               new TransportConfiguration(NettyAcceptorFactory.class.getName(), params, "netty");
//       TransportConfiguration connectorConfig = new TransportConfiguration(NettyConnectorFactory.class.getName(), params, "netty");
   }

}
