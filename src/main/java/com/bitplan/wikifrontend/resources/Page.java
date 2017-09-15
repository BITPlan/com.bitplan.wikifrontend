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

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.bitplan.wikifrontend.BackendWiki;

/**
 * I represent a wiki Page
 * 
 * @author wf
 *
 */
@Path("/index.php/{pagename}")
public class Page {

	// http://stackoverflow.com/a/5323598/1497139
	@Context
	UriInfo uriInfo;

	 @GET
	 @Path("{subpage}")
	 public Response getSubPage() throws Exception {
	   return getPage();
	 }
	 
	 @GET
   @Path("{subpage}/{subsubpage}")
   public Response getSubSubPage() throws Exception {
     return getPage();
   }
	 
	 @GET
   @Path("{subpage}/{subsubpage}/{subsubsubpage}")
   public Response getSubSubSubPage() throws Exception {
     return getPage();
   }
	 
	 @GET
   @Path("{subpage}/{subsubpage}/{subsubsubpage}/{subsubsubsubpage}")
   public Response getSubSubSubSubPage() throws Exception {
     return getPage();
   }
	 
	 @GET
   @Path("{subpage}/{subsubpage}/{subsubsubpage}/{subsubsubsubpage}/{subsubsubsubpage}")
   public Response getSubSubSubSubSubPage() throws Exception {
     return getPage();
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
		String pageTitle = uriInfo.getPath();
		if (pageTitle.contains("index.php/")) {
		  pageTitle=pageTitle.replaceAll("index.php/", "");
		}
		Response result=getPage(pageTitle);
		return result;
	}

	/**
	 * get the page with the given PageTitle
	 * @param pageTitle
	 * @return the Response
	 * @throws Exception
	 */
  public Response getPage(String pageTitle) throws Exception {
    BackendWiki wiki = BackendWiki.getInstance();
    ResponseBuilder rb = null;
    if (wiki.containsCategory(pageTitle, wiki.getCategory())) {
      String html = wiki.getPageHtml(pageTitle);
      html=wiki.fixMediaWikiHtml(html);
      html=wiki.frame(pageTitle,html);
      rb = Response.ok(html, MediaType.TEXT_HTML);
    } else {
      URI target = new URI(wiki.getSiteurl() + wiki.getScriptPath() + "index.php/"+pageTitle);
      rb = Response.seeOther(target);
    }
    ;
    return rb.build();
  }

}
