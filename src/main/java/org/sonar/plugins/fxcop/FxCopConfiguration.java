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

import com.google.common.base.Preconditions;
import org.sonar.api.config.Settings;

import java.io.File;

public class FxCopConfiguration {

  private static final String DEPRECATED_FXCOPCMD_PATH_PROPERTY_KEY = "sonar.fxcop.installDirectory";
  private static final String DEPRECATED_TIMEOUT_MINUTES_PROPERTY_KEY = "sonar.fxcop.timeoutMinutes";

  private final String languageKey;
  private final String repositoryKey;
  private String timeoutPropertyKey;
  private final String assemblyPropertyKey;
  private final String directoryPropertyKey;
  private String fxCopCmdPropertyKey;

  public FxCopConfiguration(String languageKey, String repositoryKey, String assemblyPropertyKey, String directoryPropertyKey, String fxCopCmdPropertyKey, String timeoutPropertyKey) {
    this.languageKey = languageKey;
    this.repositoryKey = repositoryKey;
    this.assemblyPropertyKey = assemblyPropertyKey;
    this.directoryPropertyKey = directoryPropertyKey;
    this.fxCopCmdPropertyKey = fxCopCmdPropertyKey;
    this.timeoutPropertyKey = timeoutPropertyKey;
  }

  public String languageKey() {
    return languageKey;
  }

  public String repositoryKey() {
    return repositoryKey;
  }

  public String assemblyPropertyKey() {
    return assemblyPropertyKey;
  }
  
  public String directoryPropertyKey() { 
	  return directoryPropertyKey; 
	  }

  public String fxCopCmdPropertyKey() {
    return fxCopCmdPropertyKey;
  }

  public String timeoutPropertyKey() {
    return timeoutPropertyKey;
  }

  public void checkProperties(Settings settings) {
    checkAssemblyProperty(settings);
    checkFxCopCmdPathProperty(settings);
    checkTimeoutProeprty(settings);
  }

  private void checkTimeoutProeprty(Settings settings) {
    if (!settings.hasKey(timeoutPropertyKey) && settings.hasKey(DEPRECATED_TIMEOUT_MINUTES_PROPERTY_KEY)) {
      timeoutPropertyKey = DEPRECATED_TIMEOUT_MINUTES_PROPERTY_KEY;
    }
  }

  private void checkFxCopCmdPathProperty(Settings settings) {
    if (!settings.hasKey(fxCopCmdPropertyKey) && settings.hasKey(DEPRECATED_FXCOPCMD_PATH_PROPERTY_KEY)) {
      fxCopCmdPropertyKey = DEPRECATED_FXCOPCMD_PATH_PROPERTY_KEY;
    }
  }

  private void checkAssemblyProperty(Settings settings) {
    checkProperty(settings, assemblyPropertyKey);

    String assemblyPath = settings.getString(assemblyPropertyKey);

    File assemblyFile = new File(assemblyPath);
    Preconditions.checkArgument(
      assemblyFile.isFile(),
      "Cannot find the assembly \"" + assemblyFile.getAbsolutePath() + "\" provided in the property \"" + assemblyPropertyKey + "\".");

    File pdbFile = new File(pdbPath(assemblyPath));
    Preconditions.checkArgument(
      pdbFile.isFile(),
      "Cannot find the .pdb file \"" + pdbFile.getAbsolutePath() + "\" inferred from the property \"" + assemblyPropertyKey + "\".");
  }

  private static void checkProperty(Settings settings, String property) {
    if (!settings.hasKey(property)) {
      throw new IllegalArgumentException("The property \"" + property + "\" must be set.");
    }
  }

  private static String pdbPath(String assemblyPath) {
    int i = assemblyPath.lastIndexOf('.');
    if (i == -1) {
      i = assemblyPath.length();
    }

    return assemblyPath.substring(0, i) + ".pdb";
  }

}
