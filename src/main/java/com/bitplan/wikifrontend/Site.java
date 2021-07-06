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
package com.bitplan.wikifrontend;

import java.io.File;

/**
 * I represent a Site
 * @author wf
 *
 */
public class Site {
  BackendWiki wiki;
  String siteName;
  
  /**
   * check whether this apache configuration exits
   * @return the checksymbol
   */
  public boolean checkApacheConfiguration() {
    File apacheFile=new File("/etc/apache2/sites-available/"+siteName+".conf");
    return apacheFile.exists();
  }
  
  /**
   * check whether the configuration file exists
   * @return the check symbol
   */
  public boolean checkConfigFile() {
    File configFile=new File(BackendWiki.getPropertyFileName(siteName));
    return configFile.exists();
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  /**
   * create a new site with the given siteName
   * @param siteName
   * @throws Exception 
   */
  public Site(String siteName) throws Exception {
    this.siteName=siteName;
    wiki=new BackendWiki(siteName);
  }

  public BackendWiki getWiki() {
    return wiki;
  }

  public void setWiki(BackendWiki wiki) {
    this.wiki = wiki;
  }
}
