package org.hornetq.util;

import java.io.File;

import org.hornetq.util.config.ServerConfig;

public interface MigrationTarget
{

   void export(ServerConfig config, File baseOutputDir);

   void init(JBMToHornetQUtil jbmToHornetQUtil);

}
