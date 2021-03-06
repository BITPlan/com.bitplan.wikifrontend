/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikifrontend open source project
 *
 * Copyright 2017-2021 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
  Copyright (C) 2016-2017 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.wikifrontend;

import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfigurationKey;

/**
 * Rythm Context
 * @author wf
 *
 */
public class RythmContext  {
  protected RythmEngine engine;
  Map<String, Object> conf = new HashMap<String, Object>();
  /**
   * set the template Root
   * @see <a href="http://rythmengine.org/doc/configuration.md#home_template_dir">Rythm doc home_template_dir</a>
   * @param path
   */
  public void setTemplateRoot(String path) {
    Object currentPath = conf.get(RythmConfigurationKey.HOME_TEMPLATE.getKey());
    // avoid resetting the engine if the path doesn't change
    if (currentPath!=null && currentPath.equals(path)) {
      return;
    }
    System.getProperties().remove(HOME_TEMPLATE.getKey());
    conf.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(), new File(path));
    engine=null;
    getEngine();
  }
  
  /**
   * get the Rythm engine
   * 
   * @return the Rythm engine
   */
  public RythmEngine getEngine() {
    if (engine == null) {
      conf.put("codegen.compact.enabled", false);
      engine = new RythmEngine(conf);
    }
    return engine;
  }

  /**
   * render the given rootMap with the given File
   * 
   * @param template
   * @param rootMap
   * @return the string render result
   * @throws Exception
   */
  public String render(File template, Map<String, Object> rootMap)
      throws Exception {
    RythmEngine engine = getEngine();
    String result = engine.render(template, rootMap);
    return result;
  }
  
  /**
   * render with the given template
   * @param template
   * @param rootMap
   * @return the string render result
   * @throws Exception
   */
  public String render(String template, Map<String, Object> rootMap)
      throws Exception {
    RythmEngine engine = getEngine();
    String result = engine.render(template, rootMap);
    return result;
  }
  
  private static RythmContext instance=null;
  /***
   * enforce singleton
   */
  private RythmContext() {
  	
  }
  
  /**
   * get the singleton 
   * @return the instance
   */
  public static RythmContext getInstance() {
  		if (instance==null) {
  			instance=new RythmContext();
  		}
  		return instance;
  }
}
