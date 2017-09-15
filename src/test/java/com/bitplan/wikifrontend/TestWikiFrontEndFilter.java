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
/**
Ï * Copyright (C) 2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.wikifrontend;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.junit.Test;

/**
 * test the Wiki FrontEnd
 * 
 * @author wf
 *
 */
public class TestWikiFrontEndFilter extends TestBase {

	/**
	 * Tests whether this category is defined in this page
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContainsCategory() throws Exception {
		BackendWiki wiki = getWiki();
		String callPage = "Workdocumentation 2016-10-19";
		String category = "frontend";
		boolean result = wiki.containsCategory(callPage, category);
		assertTrue(result);
	}
	
	@Test
	public void testRewrite() throws Exception {
		BackendWiki wiki = getWiki();
		wiki.setDebug(debug);
		String html="<img alt=\"Joker.png\" src=\"/images/wiki/e/ef/Joker.png\" width=\"188\" height=\"277\" /></a>";
		html=wiki.fixMediaWikiHtml(html);
		String imageLink=wiki.getSiteurl()+wiki.getScriptPath()+"images/wiki/e/ef/Joker.png";
		if (debug) {
			LOGGER.log(Level.INFO,"imageLink="+imageLink);
		}
		assertTrue(html.contains(imageLink));
	}
	
	@Test
	public void testImageSrcSet() throws Exception {
		BackendWiki wiki = getWiki();
		String imgHtml="<img srcset='/images/wiki/thumb/9/92/800px-CPSA-F_Logo_mit_Text_300dpi.png/300px-800px-CPSA-F_Logo_mit_Text_300dpi.png 1.5x, /images/wiki/thumb/9/92/800px-CPSA-F_Logo_mit_Text_300dpi.png/400px-800px-CPSA-F_Logo_mit_Text_300dpi.png 2x'>";
		String html=wiki.fixMediaWikiHtml(imgHtml);
		if (debug) {
			LOGGER.log(Level.INFO,html);
		}
		String expected=wiki.getBaseUrl()+"/images/wiki/thumb/9/92/800px-CPSA-F_Logo_mit_Text_300dpi.png";
		assertTrue(html.contains(expected));
	}
	
  @Test
  public void testVideoSource() throws Exception {
    BackendWiki wiki = getWiki();
    String videoHtml="<video width=\"640\" height=\"360\" autobuffer=\"autobuffer\" controls=\"controls\" preload=\"auto\"><source src=\"/videos/chademoweibersbrunn.mp4\" type=\"video/mp4\" /><source src=\"/videos/chademoweibersbrunn.ogv\" type=\"video/ogg\" /><source src=\"/videos/chademoweibersbrunn.webm\" type=\"video/webm\" /></video>";
    String html=wiki.fixMediaWikiHtml(videoHtml);
    if (debug) {
      LOGGER.log(Level.INFO,html);
    }
    String expected=wiki.getBaseUrl()+"/videos/chademoweibersbrunn.mp4";
    assertTrue(html.contains(expected));
  }
	
	
	/**
	 * remove edit sections
	 * @throws Exception 
	 */
	@Test
	public void testRemoveEditSections() throws Exception {
		BackendWiki wiki = getWiki();
		String span="<span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span><a href=\"/index.php?title=Workdocumentation_2017-02-01_mf&amp;action=edit&amp;section=2\" title=\"Edit section: paid\">edit</a><span class=\"mw-editsection-bracket\">]</span>";
		String html=wiki.fixMediaWikiHtml(span);
		if (debug) {
			LOGGER.log(Level.INFO,html);
		}
		assertFalse(html.contains("span"));
	}
	
	@Test
	public void testNoHead() throws Exception {
		BackendWiki wiki = getWiki();
		String html="<h1>Header</h1>";
		html=wiki.fixMediaWikiHtml(html);
		if (debug) {
			LOGGER.log(Level.INFO,html);
		}
		assertFalse(html.contains("head"));
		
	}

}