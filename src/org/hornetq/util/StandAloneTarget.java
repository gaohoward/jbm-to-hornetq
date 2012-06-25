package org.hornetq.util;

import java.io.File;

import org.hornetq.util.config.ServerConfig;
import org.hornetq.util.exporter.StandaloneServerExporter;

public class StandAloneTarget extends AbstractMigrationTarget
{
   @Override
   public void export(ServerConfig config, File baseOutputDir)
   {
       StandaloneServerExporter exporter = new StandaloneServerExporter(config, baseOutputDir);
       exporter.generateServer();
   }

}
