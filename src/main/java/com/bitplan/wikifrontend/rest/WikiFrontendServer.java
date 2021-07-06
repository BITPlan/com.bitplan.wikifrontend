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
package com.bitplan.wikifrontend.rest;

import java.util.logging.Logger;

import com.bitplan.rest.RestServerImpl;
import com.bitplan.rest.clicks.ClickFilterProvider;
import com.bitplan.wikifrontend.SiteManager;


/**
 * RESTFul Server for MediaWiki Java FrontEnd
 * @author wf
 *
 */
public class WikiFrontendServer extends RestServerImpl {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.wikifrontend.rest");
  
  /**
   * construct WikiFrontend Server
   * setting defaults
   * @throws Exception 
   */
  public WikiFrontendServer() throws Exception {
    // listen to whole network
    settings.setHost("0.0.0.0");
    // listen on given port
    settings.setPort(8122);
    // add the default path
    settings.setContextPath("/");
    // add a static handler
    settings.addClassPathHandler("/static", "com/bitplan/wikifrontend/webcontent/");
    // setup resources from the given packages
    String packages="com.bitplan.wikifrontend.resources;";
    settings.setPackages(packages);
    String[] filters= {ClickFilterProvider.class.getName()};
    // String[] filters = settings.getContainerRequestFilters();
    settings.setContainerRequestFilters(filters);
   }
  
  /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String[] args) throws Exception {
     WikiFrontendServer rs=new WikiFrontendServer();
     rs.settings.parseArguments(args);
     String options = rs.settings.getOptions();
     SiteManager sm=SiteManager.getInstance();
     if (options!=null) {
       sm.addSites(options);
     }
     rs.startWebServer();
     
   } // main

}