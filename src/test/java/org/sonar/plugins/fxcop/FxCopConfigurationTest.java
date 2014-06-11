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

import com.google.common.collect.Maps;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.Settings;

import java.io.File;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FxCopConfigurationTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String ASSEMBLY_PATH = "src/test/resources/FxCopConfigurationTest/MyLibrary.dll";

  @Test
  public void test() {
    FxCopConfiguration fxCopConf = new FxCopConfiguration("cs", "cs-fxcop", "fooAssemblyKey", "fooDirectoryPropertyKey", "fooFxCopCmdPathKey", "footimeout");
    assertThat(fxCopConf.languageKey()).isEqualTo("cs");
    assertThat(fxCopConf.repositoryKey()).isEqualTo("cs-fxcop");
    assertThat(fxCopConf.assemblyPropertyKey()).isEqualTo("fooAssemblyKey");
    assertThat(fxCopConf.directoryPropertyKey()).isEqualTo("fooDirectoryPropertyKey");
    assertThat(fxCopConf.fxCopCmdPropertyKey()).isEqualTo("fooFxCopCmdPathKey");
    assertThat(fxCopConf.timeoutPropertyKey()).isEqualTo("footimeout");

    fxCopConf = new FxCopConfiguration("vbnet", "vbnet-fxcop", "barAssemblyKey", "barDirectoryPropertyKey", "barFxCopCmdPathKey", "bartimeout");
    assertThat(fxCopConf.languageKey()).isEqualTo("vbnet");
    assertThat(fxCopConf.repositoryKey()).isEqualTo("vbnet-fxcop");
    assertThat(fxCopConf.assemblyPropertyKey()).isEqualTo("barAssemblyKey");
    assertThat(fxCopConf.directoryPropertyKey()).isEqualTo("barDirectoryPropertyKey");
    assertThat(fxCopConf.fxCopCmdPropertyKey()).isEqualTo("barFxCopCmdPathKey");
    assertThat(fxCopConf.timeoutPropertyKey()).isEqualTo("bartimeout");
  }

  @Test
  public void check_properties() {
    Settings settings = mock(Settings.class);
    when(settings.hasKey("fooAssemblyKey")).thenReturn(true);
    when(settings.getString("fooAssemblyKey")).thenReturn(new File(ASSEMBLY_PATH).getAbsolutePath());
    when(settings.hasKey("fooFxCopCmdPathKey")).thenReturn(true);

    new FxCopConfiguration("", "", "fooAssemblyKey", "", "fooFxCopCmdPathKey", "").checkProperties(settings);
  }

  @Test
  public void check_properties_without_assembly_extension() {
    Settings settings = mock(Settings.class);
    when(settings.hasKey("fooAssemblyKey")).thenReturn(true);
    when(settings.getString("fooAssemblyKey")).thenReturn(new File("src/test/resources/FxCopConfigurationTest/MyLibrary").getAbsolutePath());
    when(settings.hasKey("fooFxCopCmdPathKey")).thenReturn(true);

    new FxCopConfiguration("", "", "fooAssemblyKey", "", "fooFxCopCmdPathKey", "").checkProperties(settings);
  }

  @Test
  public void check_properties_assembly_property_not_set() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The property \"fooAssemblyKey\" must be set.");

    Settings settings = mock(Settings.class);
    when(settings.hasKey("fooAssemblyKey")).thenReturn(false);

    new FxCopConfiguration("", "", "fooAssemblyKey", "", "", "").checkProperties(settings);
  }

  @Test
  public void check_properties_assembly_property_not_not_found() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot find the assembly");
    thrown.expectMessage(new File("src/test/resources/FxCopConfigurationTest/MyLibraryNotFound.dll").getAbsolutePath());
    thrown.expectMessage("\"fooAssemblyKey\"");

    Settings settings = mock(Settings.class);
    when(settings.hasKey("fooAssemblyKey")).thenReturn(true);
    when(settings.getString("fooAssemblyKey")).thenReturn(new File("src/test/resources/FxCopConfigurationTest/MyLibraryNotFound.dll").getAbsolutePath());

    new FxCopConfiguration("", "", "fooAssemblyKey", "", "", "").checkProperties(settings);
  }

  @Test
  public void check_properties_assembly_property_pdb_not_found() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot find the .pdb file");
    thrown.expectMessage(new File("src/test/resources/FxCopConfigurationTest/MyLibraryWithoutPdb.pdb").getAbsolutePath());
    thrown.expectMessage("\"fooAssemblyKey\"");

    Settings settings = mock(Settings.class);
    when(settings.hasKey("fooAssemblyKey")).thenReturn(true);
    when(settings.getString("fooAssemblyKey")).thenReturn(new File("src/test/resources/FxCopConfigurationTest/MyLibraryWithoutPdb.dll").getAbsolutePath());

    new FxCopConfiguration("", "", "fooAssemblyKey", "", "", "").checkProperties(settings);
  }

  @Test
  public void check_deprecated_fxcopcmd_path_property() {
    Settings settings = new Settings();
    Map<String, String> props = Maps.newHashMap();
    props.put("assembly.prop", ASSEMBLY_PATH);
    props.put("sonar.fxcop.installDirectory", "fake/path/FxCopCmd.exe");
    settings.addProperties(props);

    FxCopConfiguration conf = new FxCopConfiguration("", "", "assembly.prop", "", "cmd.prop", "");

    conf.checkProperties(settings);
    assertThat(settings.getString(conf.fxCopCmdPropertyKey())).isEqualTo("fake/path/FxCopCmd.exe");
  }

}
