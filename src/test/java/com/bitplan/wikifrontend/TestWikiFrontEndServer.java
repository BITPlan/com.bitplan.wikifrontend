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

import java.util.logging.Level;

import org.junit.Test;

/**
 * test the Wiki FrontEnd
 * 
 * @author wf
 *
 */
public class TestWikiFrontEndServer extends TestBase {

  @Test
  public void testInit() throws Exception {
    // this test does nothing just checking the Instance
    BackendWiki lwiki = getWiki("wiki");
    assertNotNull(lwiki);
    // check sites
    SiteManager sm = SiteManager.getInstance();
    assertEquals(1, sm.siteMap.size());
    for (Site site : sm.siteMap.values()) {
      assertNotNull(site.wiki);
    }
  }

  @Test
  public void testExampleServer() throws Exception {
    BackendWiki lwiki = getWiki("wiki");
    assertNotNull(lwiki);
    String urls[] = { "/wiki/index.php/FrontendTest2016-12-19",
        "/wiki/FrontendTest2016-12-19" };
    for (String url : urls) {
      String answer = super.getResponseString("text/html", url);
      if (debug) {
        LOGGER.log(Level.INFO, answer);
      }
      String expected = "Testpage as of 2016-12-19";
      assertTrue(answer.contains(expected));
    }
  }

  @Test
  public void testExampleHomePage() throws Exception {
    // debug=true;
    BackendWiki lwiki = getWiki("wiki");
    assertNotNull(lwiki);
    String answer = super.getResponseString("text/html", "/wiki");
    if (debug) {
      LOGGER.log(Level.INFO, answer);
    }
    String expected = "HolyGrail";
    assertTrue(answer.contains(expected));
  }

}