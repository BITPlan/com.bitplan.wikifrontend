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
package com.bitplan.rythm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceManager;
import org.rythmengine.template.ITemplate;

import com.bitplan.wikifrontend.BackendWiki;
import com.bitplan.wikifrontend.RythmContext;


/**
 * loader for rythm templates from a wiki
 * @author wf
 *
 */
public class WikiTemplateResourceLoader implements ITemplateResourceLoader {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.rythm");
  
  private RythmEngine engine;
  private Map<String, WikiTemplateResource> resources = new LinkedHashMap<String, WikiTemplateResource>();
  private BackendWiki wiki;
  
  /**
   * construct me for the given wiki
   * 
   * @param wiki
   * @param rythmContext 
   */
  public WikiTemplateResourceLoader(BackendWiki wiki, RythmContext rythmContext) {
    setWiki(wiki);
    setEngine(rythmContext.getEngine());
    getEngine().registerResourceLoader(this);
  }

  @Override
  public void setEngine(RythmEngine engine) {
    this.engine = engine;
  }

  @Override
  public RythmEngine getEngine() {
    return this.engine;
  }
  
  public BackendWiki getWiki() {
    return wiki;
  }

  public void setWiki(BackendWiki wiki) {
    this.wiki = wiki;
  }
  
  /**
   * get a stack trace for the given throwable
   * 
   * @param t
   *          - the throwable to get the stack trace for
   * @return - the stacktrace as a string
   */
  public String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  /**
   * get an error message for the given throwable
   * 
   * @param at
   *          - the point where the error happened e.g. some method
   * @param th
   *          - the throwable to get the error message for
   * @return - the error Message
   */
  public String getErrMessage(String at, Throwable th) {
    String result = "\nError at " + at + ": " + th.getClass().getName() + "\n";
    result += th.getMessage() + "\n";
    result += getStackTrace(th);
    return result;
  }

  /**
   * handle the given Exception from the given method
   * 
   * @param method
   * @param th
   * @return the error Message
   */
  public void handle(String method, Throwable th) {
    String errMsg=getErrMessage(method, th);
    LOGGER.log(Level.SEVERE, errMsg);
  }
  
  /**
   * get the resource for the given path
   * 
   * @param path
   * @return
   * @throws Throwable
   */
  public WikiTemplateResource getResource(String path) throws Throwable {
    if (resources.containsKey(path)) {
      return resources.get(path);
    }
    // this is a new resource - check whether we need to load it from the wiki
    if (path.startsWith("wiki.")) {
      LOGGER.log(Level.INFO,"loading WikiTemplateResource from "+path);
      WikiTemplateResource wikiTemplate = new WikiTemplateResource(this, path,
          wiki);
      if (wikiTemplate.rythmTemplateCode!=null)
        resources.put(path, wikiTemplate);
      return wikiTemplate;
    }
    return null;
  }
  

  @Override
  public String getResourceLoaderRoot() {
    String result = "wiki";
    return result;
  }

  @Override
  public ITemplateResource load(String path) {
    WikiTemplateResource result=null;
    try {
      result = getResource(path);
    } catch (Throwable th) {
      handle("load ",th);
      return new WikiTemplateResource(this,th);
    }
    return result;
  }

  @Override
  public void scan(TemplateResourceManager arg0) {
    // do nothing
  }

  /**
   * register the given Template
   * @param key
   * @param wikiTemplate
   * @return
   */
  public TemplateClass registerTemplate(String key,WikiTemplateResource wikiTemplate ) {
    TemplateClass tc = new TemplateClass(wikiTemplate, engine);
    ITemplate template = tc.asTemplate(engine);
    if (null != template) {
      engine.registerTemplate(key, template);
    }
    return tc;
  }
  
  @Override
  public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine,
      TemplateClass callerClass, ICodeType codeType) {
    if (engine.templateRegistered(tmplName))
      return null;
    if (!tmplName.startsWith("wiki."))
      return null;
    String rythmSuffix = engine.conf().resourceNameSuffix();
    String key = tmplName;
    LOGGER.log(Level.INFO, "Loading wiki template '" + tmplName
        + "' for rythmSuffix " + rythmSuffix);
    TemplateClass tc = engine.classes().getByTemplate(key);
    if (tc == null) {
      try {
        WikiTemplateResource wikiTemplate = getResource(key);
        if (wikiTemplate == null) {
          throw new IllegalArgumentException("Template for " + key
              + " not found");
        }
        tc=registerTemplate(key,wikiTemplate);
      } catch (Throwable th) {
        handle("tryLoadTemplate", th);
        tc=registerTemplate(key,new WikiTemplateResource(this,th));
      }
    }
    return tc;
  }

}