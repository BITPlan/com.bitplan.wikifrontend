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

import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * get the wiki
	 * @return
	 * @throws Exception
	 */
	public BackendWiki getWiki() throws Exception {
		if (wiki == null) {
			// get the configured backend wiki
			wiki = BackendWiki.getInstance();
			startServer();
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
