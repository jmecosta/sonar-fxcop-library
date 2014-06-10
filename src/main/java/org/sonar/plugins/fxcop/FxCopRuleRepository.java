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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

public class FxCopRuleRepository extends RuleRepository {

  private static final String REPOSITORY_NAME = "FxCop / Code Analysis";

  private final XMLRuleParser xmlRuleParser;
  private final String languageKey;
  private final String customKey;
  private final Settings settings;

  public FxCopRuleRepository(FxCopConfiguration fxCopConf, String customKey, XMLRuleParser xmlRuleParser, Settings settings) {
    super(fxCopConf.repositoryKey(), fxCopConf.languageKey());
    setName(REPOSITORY_NAME);
    this.xmlRuleParser = xmlRuleParser;
    this.languageKey = fxCopConf.languageKey();
    this.customKey = customKey;
    this.settings = settings;
  }

  @Override
  public List<Rule> createRules() {
    
    List<Rule> rules = new ArrayList<Rule>();
        
    rules.addAll(xmlRuleParser.parse(getClass().getResourceAsStream("/org/sonar/plugins/fxcop/" + languageKey + "-rules.xml")));
   
    String customRules = this.settings.getString(this.customKey);
    
    if (StringUtils.isNotBlank(customRules)) {
      rules.addAll(xmlRuleParser.parse(new StringReader(customRules)));
    }
    
    return rules;
  }
}
