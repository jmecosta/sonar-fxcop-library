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
  private final String assemblyPropertyKey;
  private final String directoryPropertyKey;
  private String fxCopCmdPropertyKey;
  private String timeoutPropertyKey;
  private final String aspnetPropertyKey;
  private final String directoriesPropertyKey;
  private final String referencesPropertyKey;

  public FxCopConfiguration(String languageKey, String repositoryKey, String assemblyPropertyKey, String fxCopCmdPropertyKey, String timeoutPropertyKey, String aspnetPropertyKey, String directoryPropertyKey) {
    String directoriesPropertyKey, String referencesPropertyKey) {
    this.languageKey = languageKey;
    this.repositoryKey = repositoryKey;
    this.assemblyPropertyKey = assemblyPropertyKey;
    this.directoryPropertyKey = directoryPropertyKey;
    this.fxCopCmdPropertyKey = fxCopCmdPropertyKey;
    this.timeoutPropertyKey = timeoutPropertyKey;
    this.aspnetPropertyKey = aspnetPropertyKey;
    this.directoriesPropertyKey = directoriesPropertyKey;
    this.referencesPropertyKey = referencesPropertyKey;
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

  public String aspnetPropertyKey() {
    return aspnetPropertyKey;
  }

  public String directoriesPropertyKey() {
    return directoriesPropertyKey;
  }

  public String referencesPropertyKey() {
    return referencesPropertyKey;
  }

  public void checkProperties(Settings settings) {
    checkMandatoryProperties(settings);
    checkAssemblyProperty(settings);
    checkFxCopCmdPathProperty(settings);
    checkTimeoutProeprty(settings);
  }

  private void checkMandatoryProperties(Settings settings) {
    if (!settings.hasKey(assemblyPropertyKey)) {
      throw new IllegalArgumentException("The property \"" + assemblyPropertyKey + "\" must be set and the project must have been built to execute FxCop rules. "
        + "This property can be automatically set by the Analysis Bootstrapper for Visual Studio Projects plugin, see: http://docs.codehaus.org/x/TAA1Dg."
        + "If you wish to skip the analysis of not built projects, set the property \"sonar.visualstudio.skipIfNotBuilt\".");
    }
  }

  private void checkAssemblyProperty(Settings settings) {
    String[] assembliesPaths = settings.getString(assemblyPropertyKey).split(",");

    for(String assemblyPath : assembliesPaths) {
      File assemblyFile = new File(assemblyPath);
      Preconditions.checkArgument(
        assemblyFile.isFile(),
        "Cannot find the assembly \"" + assemblyFile.getAbsolutePath() + "\" provided by the property \"" + assemblyPropertyKey + "\".");

      File pdbFile = new File(pdbPath(assemblyPath));
      Preconditions.checkArgument(
        pdbFile.isFile(),
        "Cannot find the .pdb file \"" + pdbFile.getAbsolutePath() + "\" inferred from the property \"" + assemblyPropertyKey + "\".");
    }
  private static String pdbPath(String assemblyPath) {
    int i = assemblyPath.lastIndexOf('.');
    if (i == -1) {
      i = assemblyPath.length();
    }

    return assemblyPath.substring(0, i) + ".pdb";
  }

  private void checkFxCopCmdPathProperty(Settings settings) {
    if (!settings.hasKey(fxCopCmdPropertyKey) && settings.hasKey(DEPRECATED_FXCOPCMD_PATH_PROPERTY_KEY)) {
      fxCopCmdPropertyKey = DEPRECATED_FXCOPCMD_PATH_PROPERTY_KEY;
    }

    String value = settings.getString(fxCopCmdPropertyKey);

    File file = new File(value);
    Preconditions.checkArgument(
      file.isFile(),
      "Cannot find the FxCopCmd executable \"" + file.getAbsolutePath() + "\" provided by the property \"" + fxCopCmdPropertyKey + "\".");
  }

  private void checkTimeoutProeprty(Settings settings) {
    if (!settings.hasKey(timeoutPropertyKey) && settings.hasKey(DEPRECATED_TIMEOUT_MINUTES_PROPERTY_KEY)) {
      timeoutPropertyKey = DEPRECATED_TIMEOUT_MINUTES_PROPERTY_KEY;
    }
  }

}
