package org.hornetq.util.config;

import org.hornetq.api.core.TransportConfiguration;

public class RemotingConfig
{
   public TransportConfiguration acceptorConfig;
   public TransportConfiguration connectorConfig;
   public long connectionTTL;
   public long clientFailureCheckPeriod;
}
