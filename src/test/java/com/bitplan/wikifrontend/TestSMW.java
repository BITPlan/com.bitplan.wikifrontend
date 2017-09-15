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

import java.util.List;
import java.util.logging.Level;
import org.junit.Test;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * test semantic MediaWiki access
 * 
 * @author wf
 *
 */
public class TestSMW extends TestBase {

  public class Api {
   Query query;
  }
  public class DataItem {
    String type;
    String item;
  }
  public class Property {
    String property;
    List <DataItem> dataitem;
  }
  
  public class Query {
    String subject;
    String serializer;
    String version;
    List<Property> data;
  }
  
  @Test
  public void testGetSMWData() throws Exception {
    String pageTitle = "Demo:Amsterdam";
    BackendWiki wiki = new BackendWiki();
    wiki.setSiteurl("https://www.semantic-mediawiki.org");
    wiki.setScriptPath("/wiki");
    wiki.wikiId="smw";
    String queryUrl = "https://www.semantic-mediawiki.org/w/api.php";
    String params="?action=browsebysubject&subject="+pageTitle+"&format=json";
    String json;
    ClientResponse response;
    // decide for the method to use for api access
    response = wiki.getPostResponse(queryUrl, params, null,null);
    json = this.getResponseString(response);
    //debug=true;
    if (debug) {
      json=json.replaceAll("\\{", "\n\\{");
      LOGGER.log(Level.INFO, json);
    }
    assertNotNull(json);
    Gson gson=new Gson();
    Api api=gson.fromJson(json, Api.class);
    assertNotNull(api);
    Query query = api.query;
    assertNotNull(query);
    assertNotNull(query.subject);
    assertNotNull(query.serializer);
    assertNotNull(query.version);
    assertNotNull(query.data);
    assertEquals("Amsterdam#202#",query.subject);
    assertEquals("SMW\\Serializers\\SemanticDataSerializer",query.serializer);
    assertEquals("0.1",query.version);
    List<Property> data = query.data;
    assertEquals(18,data.size());
    for (Property prop:data) {
      assertNotNull(prop.dataitem);
      for (DataItem item:prop.dataitem) {
        assertNotNull(item.type);
        assertNotNull(item.item);
        // debug=true;
        if (debug)
          System.out.println(prop.property+" "+item.type+"="+item.item);
      }
    }
  }

}
