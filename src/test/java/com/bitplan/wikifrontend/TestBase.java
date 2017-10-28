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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.bitplan.rest.RestServer;
import com.bitplan.rest.test.TestRestServer;
import com.bitplan.wikifrontend.rest.WikiFrontendServer;

/**
 * base class for Tests around WikiFrontEndServer and WikiBackend
 */
public class TestBase extends TestRestServer {

	// if debugging is on logging information may be written
	public static boolean debug = false;
	// the wiki to be used - which is null initially
	private static BackendWiki wiki = null;

	// prepare a LOGGER
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.wikifrontend");

	/**
	 * get the wiki with the given siteName
	 * @return
	 * @throws Exception
	 */
	public BackendWiki getWiki(String siteName) throws Exception {
	  SiteManager sm=SiteManager.getInstance();
	  Site site = sm.getSite(siteName);
		if (site == null) {
		  String user = System.getProperty("user.name");
		  // is this the travis environment?
	    if (user.equals("travis")) {
	      // then prepare the wiki configuration
	      File srcFile=new File("src/test/resources/travis.ini");
	      File destFile=new File(BackendWiki.getPropertyFileName(siteName));
	      FileUtils.copyFile(srcFile, destFile);
	    }
			// get the configured backend wiki
	    sm.addSites(siteName); // add the unnamed default wiki
			wiki = sm.getSite(siteName).getWiki();
			if (sm.siteMap.size()==1) {
			  startServer();
			}
		}
		return wiki;
	}

	@Override
	public RestServer createServer() throws Exception {
		RestServer result = new WikiFrontendServer();
		return result;
	}

	public static void shutdownServer() throws Exception {
		if (debug) {
			LOGGER.log(Level.INFO, "Stopping server");
		}
		rs.stop();
	}

}
