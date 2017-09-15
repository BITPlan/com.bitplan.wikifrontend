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

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.util.Properties;
import java.util.logging.Level;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

/**
 * test the Wiki FrontEnd
 * 
 * @author wf
 *
 */
public class TestWikiFrontEnd extends TestBase {

	@Context
	UriInfo uriInfo;

	/**
	 * tests reading configuration properties
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadWikiConfiguration() throws Exception {
		Properties props = getWiki().getConfigProperties();
		assertNotNull(props);
		/*
		 * assertEquals("http://partner.bitplan.com",
		 * props.getProperty("wiki.base.siteurl")); assertEquals("/",
		 * props.getProperty("wiki.base.scriptpath"));
		 * assertEquals("Mediawiki 1.24.1", props.getProperty("wiki.base.version"));
		 * assertEquals("partner.bitplan.com", props.getProperty("wiki.base.id"));
		 */
		/*
		assertEquals("http://localhost", props.getProperty("wiki.base.siteurl"));
		assertEquals("/mediawiki/", props.getProperty("wiki.base.scriptpath"));
		assertEquals("1.23.5", props.getProperty("wiki.base.version"));
		assertEquals("phobos", props.getProperty("wiki.base.id"));
		*/
		assertEquals("http://wiki.bitplan.com", props.getProperty("wiki.base.siteurl"));
		assertEquals("/", props.getProperty("wiki.base.scriptpath"));
		assertEquals("1.23.5", props.getProperty("wiki.base.version"));
		assertEquals("wiki", props.getProperty("wiki.base.id"));
		assertEquals("frontend",props.getProperty("wiki.frontend.category"));
		assertEquals("MediaWiki:Frame.rythm",props.getProperty("wiki.frontend.frame"));
	}

	/**
	 * tests querying main page
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testMainPage() throws Exception {
		String callPage = "Main Page";
		BackendWiki wiki = getWiki();
		String content = wiki.getPageContent(callPage);
		if (debug) {
			LOGGER.log(Level.INFO,content);
		}
		assertNotNull(content);
	}

	/**
	 * tests querying a page while you logged in
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQueryLoggedIn() throws Exception {
		String callPage = "Main Page";
		String expected = "BITPlan";
		BackendWiki wiki = getWiki();
		wiki.login();
		String content = wiki.getPageContent(callPage);
		assertTrue(content.contains(expected));
		wiki.logout();
	}

	/**
	 * test Login and logout see <a
	 * href='http://www.mediawiki.org/wiki/API:Login'>API:Login</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLogin() throws Exception {
		BackendWiki wiki = getWiki();
		wiki.login();
		assertEquals("Success", wiki.getAuth().getResult());
		assertNotNull(wiki.getAuth().getLguserid());
		assertEquals(wiki.wikiUser.getUsername().toLowerCase(), wiki.getAuth()
				.getLgusername().toLowerCase());
		assertNotNull(wiki.getAuth().getLgtoken());
		wiki.logout();
	}

	@Test
	public void testLogo() throws Exception {
		BufferedImage image = super.getImageResponse("/static/BITPlanLogo2012.png");
		assertNotNull(image);
		assertEquals(800, image.getWidth());
		assertEquals(449,image.getHeight());
	}

}