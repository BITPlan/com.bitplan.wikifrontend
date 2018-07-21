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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;

/**
 * handler for PostService - keeps track of the UUIDs
 * @author wf
 *
 */
public class PostManager implements PostService {
  private static PostManager instance;
  Map<String,MultivaluedMap<String, String>> postMap=new HashMap<String,MultivaluedMap<String, String>>();
  private String managerToken;
  
  /**
   * private constructor - this is a singleton
   */
  private PostManager() {
    managerToken=UUID.randomUUID().toString();
  }
  
  public static PostManager getInstance() {
    if (instance==null)
      instance=new PostManager();
    return instance;
  }
  
  @Override
  public String getPostToken() {
    // create a unique post token
    //  https://stackoverflow.com/questions/1389736/how-do-i-create-a-unique-id-in-java
    String postToken=UUID.randomUUID().toString();
    // prepend the managerToken
    postToken=managerToken+"."+postToken;
    return postToken;
  }
  
  /**
   * get the post data for the given postToken
   * @param postToken
   * @return the form data
   */
  public MultivaluedMap<String, String> getPostData(String postToken) {
    MultivaluedMap<String, String> result = postMap.get(postToken);
    postMap.remove(postToken);
    return result;
  }
  
  /**
   * handle the given post formParams for the given postToken
   * remember the data under the given token
   * 
   * @param postToken
   * @param formParams
   * @return true if the postToken is valid/known false if not
   */
  public boolean handle(String postToken,
      MultivaluedMap<String, String> formParams) {
    if (postToken.startsWith(managerToken)) {
      postMap.put(postToken, formParams);
      return true;
    }
    return false; // this is not a valid post Token
  }
 
}
