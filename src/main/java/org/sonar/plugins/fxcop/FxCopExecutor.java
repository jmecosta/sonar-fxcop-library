/*
 * SonarQube FxCop Library
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.fxcop;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FxCopExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopExecutor.class);
  private static final int EXIT_CODE_SUCCESS = 0;
  
  public void execute(String executable, String assemblies, File rulesetFile, File reportFile, int timeout, String assemblyDependencyDirectories) {
    int exitCode = CommandExecutor.create().execute(createCommand(executable, assemblies, rulesetFile, reportFile, assemblyDependencyDirectories), TimeUnit.MINUTES.toMillis(timeout));
            
    StringBuilder errorData = new StringBuilder();

    boolean isFatal = IsFatalError(exitCode, errorData);
    if(exitCode != EXIT_CODE_SUCCESS) {
        LOG.info("Some errors were reported during execution of FxCop Error Code: " + exitCode);
        LOG.info("Error Data: " + errorData);
        LOG.info("See: http://msdn.microsoft.com/en-us/library/bb429400(v=vs.80).aspx");        
    }
    
    Preconditions.checkState(exitCode == EXIT_CODE_SUCCESS || !isFatal,
      "The execution of \"" + executable + "\" failed and returned " + exitCode + " as exit code.");
    
    }

  
  private Command createCommand(String executable, String assemblies, File rulesetFile, File reportFile, String assemblyDependencyDirectories) {
        Command command = Command.create(getExecutable(executable))
            .addArgument("/file:" + assemblies)
            .addArgument("/ruleset:=" + rulesetFile.getAbsolutePath())
            .addArgument("/out:" + reportFile.getAbsolutePath())
            .addArgument("/outxsl:none")
            .addArgument("/forceoutput")
            .addArgument("/searchgac");
	  
	  if(assemblyDependencyDirectories != null && assemblyDependencyDirectories.length() > 0) {
		  String[] directories = assemblyDependencyDirectories.split(",");
		  for (String directory : directories) {
			  command.addArgument("/directory:" + directory);
		}
	  }
	  return command;
  }
  
  @VisibleForTesting
  boolean IsFatalError(int errorCode, StringBuilder errorData) {
      boolean isFatal = false;
      int errorCopy = errorCode;
      
      if((errorCode & 0x1) == 1) {
          errorData.append("[Analysis error]");
          isFatal = true;          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Rule exceptions]");          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Project load error]");          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Assembly load error]");          
      }      
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Rule library load error]");          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Import report load error]");          
      }      
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Output error]");          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Command line switch error]");          
      }
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Initialization error]");          
      }      
      
      errorCopy = errorCopy >> 1;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Assembly references error]");          
      }
      
      errorCopy = errorCopy >> 4;
      if((errorCopy & 0x1) == 1) {
          errorData.append("[Unknown error]");          
      }                  
      
      return isFatal;
  } 
    
  /**
   * Handles deprecated property: "installDirectory", which gives the path to the directory only.
   */
  private static String getExecutable(String propertyValue) {
    String execName = "FxCopCmd.exe";

    if (!propertyValue.endsWith(execName)) {
      return (new File(propertyValue, execName)).getAbsolutePath();
    } else {
      return propertyValue;
    }
  }

}
