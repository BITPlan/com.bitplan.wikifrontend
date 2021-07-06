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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.bitplan.wikifrontend.BackendWiki;
import com.bitplan.wikifrontend.PostManager;
import com.bitplan.wikifrontend.Site;
import com.bitplan.wikifrontend.SiteManager;

/**
 * I represent a wiki Page
 * 
 * @author wf
 *
 */
// https://stackoverflow.com/a/5323598/1497139
@Path("{siteName}/{path:.+}")
public class Page {

  // http://stackoverflow.com/a/5323598/1497139
  @Context
  UriInfo uriInfo;

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces({ "text/html" })
  public Response handlePost(MultivaluedMap<String, String> formParams) throws Exception {
    SitePageInfo pageInfo=new SitePageInfo(uriInfo);
    String postToken=formParams.getFirst("postToken");
    if (postToken!=null) {
      PostManager postManager=PostManager.getInstance();
      if (postManager.handle(postToken,formParams)) {
        pageInfo.setPostToken(postToken);
      } else {
        // the token is not valid - there is something fishy going on 
        return Response.noContent().build();
      }
    }
    Response result = getPage(pageInfo);
    return result;
  }

  /**
   * get the Page - as html for frontend pages or redirect to the backend for
   * non frontend pages
   * 
   * @return the response for the page
   * @throws Exception
   */
  @GET
  public Response getPage() throws Exception {
    SitePageInfo pageInfo=new SitePageInfo(uriInfo);
    Response result = getPage(pageInfo);
    return result;
  }

  /**
   * get the page of the given site with the given PageTitle
   * 
   * @param sitePage - the site and pageTitle
   * @return the Response
   * @throws Exception
   */
  public Response getPage(SitePageInfo sitePage) throws Exception {
    if (sitePage.error!=null)
      return sitePage.error;
    SiteManager sm = SiteManager.getInstance();
    Site site = sm.getSite(sitePage.site);
    if (site == null) {
      return Page.unknownSite(sitePage.site);
    } else {
      ResponseBuilder rb = null;
      BackendWiki wiki = site.getWiki();
      if (wiki.containsCategory(sitePage.getPageTitle(), wiki.getCategory())) {
        String html = wiki.frame(sitePage,uriInfo);
        rb = Response.ok(html, MediaType.TEXT_HTML);
      } else {
        URI target = new URI(wiki.getSiteurl() + wiki.getScriptPath()
            + "index.php/" + sitePage.getPageTitle());
        rb = Response.seeOther(target);
      }
      return rb.build();
    }
  }

  /**
   * create an unknownSite response for the given siteName
   * 
   * @param siteName
   *          - the siteName
   * @return - the 404 Response
   */
  public static Response unknownSite(String siteName) {
    return Response.status(404).entity("unknown site " + siteName).build();
  }

}
