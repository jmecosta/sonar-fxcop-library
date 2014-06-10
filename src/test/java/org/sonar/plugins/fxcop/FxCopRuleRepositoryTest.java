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

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.XMLRuleParser;

public class FxCopRuleRepositoryTest {

  private static final String FXCOP_CUSTOM_RULES_PROPERTY_KEY = "sonar.cs.fxcop.customRules";
  
  String profile = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n" +
          "<rules>\n" +
          "    <rule key=\"readability/nolint-0\">\n" +
          "        <name><![CDATA[ Unknown NOLINT error category: %s  % category]]></name>\n" +
          "        <configKey><![CDATA[readability/nolint-0@CPP_LINT]]></configKey>\n" +
          "        <category name=\"readability\" />\n" +
          "        <description><![CDATA[  Unknown NOLINT error category: %s  % category ]]></description>\n" +
          "    </rule>\n" +
          "    <rule key=\"readability/fn_size-0\">\n" +
          "        <name>name</name>\n" +
          "        <configKey>key</configKey>\n" +
          "        <category name=\"readability\" />\n" +
          "        <description>descr</description>\n" +
          "    </rule></rules>";
  
  @Test
  public void test_cs() {
    Settings settings = new Settings();
    settings.appendProperty(FXCOP_CUSTOM_RULES_PROPERTY_KEY, null);    
    FxCopRuleRepository repo = new FxCopRuleRepository(new FxCopConfiguration("cs", "cs-fxcop", "", "", ""), FXCOP_CUSTOM_RULES_PROPERTY_KEY, new XMLRuleParser(), settings);
    assertThat(repo.getLanguage()).isEqualTo("cs");
    assertThat(repo.getKey()).isEqualTo("cs-fxcop");

    List<Rule> rules = repo.createRules();
    assertThat(rules.size()).isEqualTo(232);
    for (Rule rule : rules) {
      assertThat(rule.getKey()).isNotNull();
      assertThat(rule.getName()).isNotNull();
      assertThat(rule.getDescription()).isNotNull();
    }
  }

  @Test
  public void test_vbnet() {
    Settings settings = new Settings();
    settings.appendProperty("sonar.cs.fxcop.customRules", profile);    
    FxCopRuleRepository repo = new FxCopRuleRepository(new FxCopConfiguration("vbnet", "vbnet-fxcop", "", "", ""), FXCOP_CUSTOM_RULES_PROPERTY_KEY, new XMLRuleParser(), settings);
    assertThat(repo.getLanguage()).isEqualTo("vbnet");
    assertThat(repo.getKey()).isEqualTo("vbnet-fxcop");

    List<Rule> rules = repo.createRules();
    assertThat(rules.size()).isEqualTo(234);
    for (Rule rule : rules) {
      assertThat(rule.getKey()).isNotNull();
      assertThat(rule.getName()).isNotNull();
      assertThat(rule.getDescription()).isNotNull();
    }
  }

}
