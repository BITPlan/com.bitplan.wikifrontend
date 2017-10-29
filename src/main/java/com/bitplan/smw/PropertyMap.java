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
package com.bitplan.smw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitplan.mediawiki.japi.api.DataItem;
import com.bitplan.mediawiki.japi.api.Property;

/**
 * utility class to handle properties returned by semantic media wiki
 * 
 * @author wf
 *
 */
public class PropertyMap {
  Map<String, Property> propertyMap = new HashMap<String, Property>();

  /**
   * no args constructor
   */
  public PropertyMap() {
  }
  
  /**
   * get my map
   * @return the property Map
   */
  public  Map<String, Property> getMap() {
    return propertyMap;
  }

  /**
   * init me from the given data
   * 
   * @param data
   */
  public void init(List<Property> data) {
    for (Property prop : data) {
      propertyMap.put(prop.getProperty(), prop);
    }
  }

  public int size() {
    return propertyMap.size();
  }

  public Set<String> keySet() {
    return propertyMap.keySet();
  }

  /**
   * get the dataitem
   * 
   * @param key
   * @return
   */
  public String get(String key) {
    String result = "";
    Property prop = propertyMap.get(key);
    if (prop != null) {
      List<DataItem> dataitems = prop.getDataitem();
      if (dataitems != null) {
        String delim = "";
        for (DataItem dataitem : dataitems) {
          result += delim + dataitem.getItem();
          delim = ",";
        }
      }
    }
    return result;
  }

  /**
   * get the first DataItem for the givne key
   * 
   * @param key
   *          - the key
   * @return - the data item
   */
  public DataItem getFirst(String key) {
    Property prop = propertyMap.get(key);
    List<DataItem> dataitems = prop.getDataitem();
    if (dataitems != null && dataitems.size() >= 1) {
      DataItem dataitem = dataitems.get(0);
      return dataitem;
    }
    return null;
  }

  /**
   * get the integer for the given key
   * 
   * @param key
   *          - the integer value to get
   * @return - the integer
   */
  public Integer getInteger(String key) {
    DataItem dataitem = getFirst(key);
    if (dataitem == null)
      return null;
    return Integer.parseInt(dataitem.getItem());
  }

  Pattern hashEnd = Pattern.compile("^(.*)(#.#)$");

  /**
   * get the string for the given key
   * 
   * @param key
   * @return - the string value
   */
  public String getString(String key) {
    DataItem dataitem = getFirst(key);
    if (dataitem == null)
      return null;
    String s = dataitem.getItem();
    Matcher hashMatcher = hashEnd.matcher(s);
    if (hashMatcher.matches()) {
      return hashMatcher.group(1);
    }
    return s;
  }

}
