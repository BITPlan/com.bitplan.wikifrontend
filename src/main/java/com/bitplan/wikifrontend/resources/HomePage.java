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
package com.bitplan.wikifrontend.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.bitplan.rest.resources.TemplateResource;
import com.bitplan.wikifrontend.Site;
import com.bitplan.wikifrontend.SiteManager;

@Path("/")
public class HomePage extends TemplateResource {
  @GET
  public Response getFrontEndServerHomePage() {
    return super.templateResponse("serverhome.rythm");
  }
  
  @GET
  @Path("{siteName}")
  public Response getPage(@PathParam("siteName") String siteName)
      throws Exception {
    SiteManager sm = SiteManager.getInstance();
    Site site = sm.getSite(siteName);
    if (site == null) {
      return Page.unknownSite(siteName);
    } else {
      Page page = new Page();
      String homePage = site.getWiki().getHomePage();
      Response result = page.getPage(siteName,homePage);
      return result;
    }
  }
}
