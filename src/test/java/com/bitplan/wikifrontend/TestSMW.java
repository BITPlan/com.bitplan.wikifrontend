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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bitplan.smw.PropertyMap;

/**
 * test semantic MediaWiki access
 * 
 * @author wf
 *
 */
public class TestSMW extends TestBase {
  
  @Test
  public void testGetSMWData() throws Exception {
    String pageTitle = "Demo:Amsterdam";
    BackendWiki wiki = new BackendWiki();
    wiki.setSiteurl("https://www.semantic-mediawiki.org");
    wiki.setScriptPath("/w");
    wiki.wikiId="smw";
    PropertyMap props = wiki.getSMWProperties(pageTitle);
    assertEquals(18,props.size());
    // . debug=true;
    if (debug) {
      for (String key:props.keySet()) {
        System.out.println(key+"="+props.get(key));
      }
    }
    Integer rainDays=props.getInteger("Average_rainy_days");
    assertNotNull(rainDays);
    assertEquals(234,rainDays.intValue());
    Integer population=props.getInteger("Population");
    assertEquals(783364,population.intValue());
    String image=props.getString("Has_image");
    if (debug) {
      System.out.println("image is "+image);
    }
    assertEquals("Amsterdam_-_Waag.jpg",image);
    
  }

}
