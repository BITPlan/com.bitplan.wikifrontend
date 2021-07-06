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
package com.bitplan.wikifrontend.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * helper class for multi site handling
 * @author wf
 *
 */
public class SitePageInfo {
  String site;
  private String pageTitle;
  private String postToken;
  private String path;
  Response error;
  
  /**
   * construct me from the given site and pageTitle
   * @param site
   * @param pageTitle
   */
  public SitePageInfo(String site, String pageTitle) {
    this.site=site;
    this.setPageTitle(pageTitle);
  }
  /**
   * construct a SitePage from the given UriInfo
   * @param uriInfo
   */
  public SitePageInfo(UriInfo uriInfo) {
    path = uriInfo.getPath();
    String[] parts = path.split("/");
    if (parts.length<1)
      error=Response.status(500).entity("unsupported path"+path).build();
    setPageTitle("");
    String delim="";
    for (int i=1;i<parts.length;i++) {
      setPageTitle(getPageTitle() + delim+parts[i]);
      delim="/";
    }
    site=parts[0];
    if (getPageTitle().contains("index.php/")) {
      setPageTitle(getPageTitle().replaceAll("index.php/", ""));
    }
  }
  public String getPageTitle() {
    return pageTitle;
  }
  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }
  public String getPostToken() {
    return postToken;
  }
  public void setPostToken(String postToken) {
    this.postToken = postToken;
  }
}