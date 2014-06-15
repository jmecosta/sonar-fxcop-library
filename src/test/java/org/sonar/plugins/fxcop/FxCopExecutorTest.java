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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class FxCopExecutorTest {

  @Test
  public void testFxCopErrorCodes() {
    FxCopExecutor fxCopExec = new FxCopExecutor();
    
    StringBuilder errorData = new StringBuilder();    
    assertThat(fxCopExec.IsFatalError(0, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("");
    
    errorData = new StringBuilder();     
    assertThat(fxCopExec.IsFatalError(1, errorData)).isTrue();
    assertThat(errorData.toString()).isEqualTo("[Analysis error]");    
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(2, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Rule exceptions]");
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(4, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Project load error]");
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(8, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Assembly load error]");    
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(16, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Rule library load error]");    

    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(32, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Import report load error]");    
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(64, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Output error]");  
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(128, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Command line switch error]"); 
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(256, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Initialization error]");     
    
    errorData = new StringBuilder();
    assertThat(fxCopExec.IsFatalError(512, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Assembly references error]");        
    
    errorData = new StringBuilder();       
    assertThat(fxCopExec.IsFatalError(3, errorData)).isTrue();
    assertThat(errorData.toString()).isEqualTo("[Analysis error][Rule exceptions]");
    
    errorData = new StringBuilder();   
    assertThat(fxCopExec.IsFatalError(129, errorData)).isTrue();
    assertThat(errorData.toString()).isEqualTo("[Analysis error][Command line switch error]");
    
    errorData = new StringBuilder();    
    assertThat(fxCopExec.IsFatalError(65, errorData)).isTrue();
    assertThat(errorData.toString()).isEqualTo("[Analysis error][Output error]");
    
    errorData = new StringBuilder();    
    assertThat(fxCopExec.IsFatalError(72, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Assembly load error][Output error]");    

    errorData = new StringBuilder();    
    assertThat(fxCopExec.IsFatalError(1536, errorData)).isFalse();
    assertThat(errorData.toString()).isEqualTo("[Assembly references error]");    
  }
}
