/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikifrontend open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * manages the Sites
 * 
 * @author wf
 *
 */
public class SiteManager {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.wikifrontend");

  private static SiteManager siteManager;
  public Map<String, Site> siteMap = new HashMap<String, Site>();

  /**
   * singleton access for SiteManager
   * 
   * @return the SiteManager instance
   */
  public static SiteManager getInstance()  {
    if (siteManager == null) {
      siteManager = new SiteManager();
    }
    return siteManager;
  }

  /**
   * add the sites from the given Site list
   * @param siteList
   * @throws Exception 
   */
  public void addSites(String siteList) throws Exception {
    for (String siteName : siteList.split(",")) {
      LOGGER.log(Level.INFO, "adding site " + siteName);
      siteMap.put(siteName, new Site(siteName));
    }
  }

  /**
   * get the site with the given name
   * @param siteName
   * @return the site
   */
  public Site getSite(String siteName) {
    Site site=this.siteMap.get(siteName);
    return site;
  }

}
