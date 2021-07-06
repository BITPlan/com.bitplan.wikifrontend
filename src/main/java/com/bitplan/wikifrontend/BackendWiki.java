/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikifrontend open source project
 *
 * Copyright 2017-2021 BITPlan GmbH https://github.com/BITPlan
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
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriInfo;

import org.htmlcleaner.TagNode;

import com.bitplan.mediawiki.japi.Mediawiki;
import com.bitplan.mediawiki.japi.SiteInfo;
import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.mediawiki.japi.api.Login;
import com.bitplan.mediawiki.japi.api.Property;
import com.bitplan.mediawiki.japi.api.Query;
import com.bitplan.mediawiki.japi.user.WikiUser;
import com.bitplan.rythm.WikiTemplateResourceLoader;
import com.bitplan.smw.PropertyMap;
import com.bitplan.wikifrontend.resources.SitePageInfo;

/**
 * Mediawiki backend site to be controlled by a wiki Frontend RESTful server
 * 
 * @author wf
 * @author wz
 */
public class BackendWiki extends Mediawiki {

  private Properties wikiProps = null;
  public String wikiId = null;
  protected WikiUser wikiUser = null;
  private boolean loggedIn = false;
  boolean restricted = true;
  // is this a semantic mediawiki?
  boolean smw = false;
  private Login auth = null;
  private String category;
  private String homePage;
  private String frame;
  private SiteInfo siteinfo;
  String site; // the name of the site
  String frontendUrl; // the frontend url of the site
  WikiTemplateResourceLoader wikiTemplateResourceLoader;

  /**
   * no argument constructor
   * 
   * @throws Exception
   */
  BackendWiki() throws Exception {
    super();
  }

  /**
   * Constructor
   * 
   * @param -
   *          the siteName to construct me from
   * @throws Exception
   */
  BackendWiki(String siteName) throws Exception {
    this();
    this.site = siteName;
    wikiProps = getConfigProperties();
    setWikiProperties(wikiProps);
  }

  /**
   * get the pageContent
   * 
   * @throws Exception
   * @return pageContent
   */
  public String getPageContent(String pageTitle) throws Exception {
    if (!isLoggedIn()) {
      login();
      siteinfo = super.getSiteInfo();
    }
    String result = super.getPageContent(pageTitle);
    return result;
  }

  /**
   * construct this wiki
   * 
   * @param siteurl
   * @param pScriptPath
   * @param wikiId
   * @param version
   * @throws Exception
   */
  public BackendWiki(String siteurl, String pScriptPath, String wikiId,
      String version) throws Exception {
    super(siteurl, pScriptPath);
    super.setVersion(version);
    this.wikiId = wikiId;
  }

  /**
   * get the property Directory
   * @return the property Directory
   */
  public static File getPropertyDir() {
    String userPropertiesDirName = System.getProperty("user.home")
        + "/.wikibackend";
    return new File(userPropertiesDirName);
  }
  
  /**
   * get the property file name for the given site
   * 
   * @param site
   * @return the property file name
   */
  public static String getPropertyFileName(String site) {
    String user = System.getProperty("user.name");
    String delim = "";
    if (!site.isEmpty()) {
      delim = "_";
    }
    String userPropertiesFileName=getPropertyDir().getPath() +"/"+ user + delim + site + ".ini";
    return userPropertiesFileName;
  }

  /**
   * Reads the properties from the configuration file
   * 
   * @return the properties
   * @throws Exception
   */
  public Properties getConfigProperties() throws Exception {
    String userPropertiesFileName = getPropertyFileName(site);
    File propFile = new File(userPropertiesFileName);
    Properties props = new Properties();
    props.load(new FileReader(propFile));
    return props;
  }

  /**
   * Sets the properties from the configuration file into the mediawiki
   * 
   * @param props
   */
  public void setWikiProperties(Properties props) {
    this.setVersion(props.getProperty("wiki.base.version"));
    this.wikiId = props.getProperty("wiki.base.id");
    this.smw = Boolean.parseBoolean(props.getProperty("wiki.smw", "true"));
    this.restricted = Boolean
        .parseBoolean(props.getProperty("wiki.restricted", "true"));
    this.setSiteurl(props.getProperty("wiki.base.siteurl"));
    this.setScriptPath(props.getProperty("wiki.base.scriptpath", "/"));
    this.setHomePage(props.getProperty("wiki.frontend.homepage", "Main_Page"));
    this.setCategory(props.getProperty("wiki.frontend.category", "frontend"));
    this.setFrame(
        props.getProperty("wiki.frontend.frame", "MediaWiki:Frame.rythm"));
    this.setFrontendUrl(
        props.getProperty("frontend.url", "http://wikicms.bitplan.com"));
  }

  /**
   * return the WikiUser
   * 
   * @return the WikiUser
   */
  public WikiUser getUser() {
    if (wikiUser == null) {
      wikiUser = WikiUser.getUser(wikiId);
    }
    return wikiUser;
  }

  public Login getAuth() {
    return auth;
  }

  /**
   * Does the login
   * 
   * @throws Exception
   */
  public void login() throws Exception {
    if (!loggedIn) {
      if (restricted) {
        wikiUser = getUser();
        if (wikiUser == null) {
          throw new IllegalStateException("backend user not configured");
        }
        auth = login(wikiUser.getUsername(), wikiUser.getPassword());
      }
      loggedIn = true;
    }
  }

  /**
   * @return true, if the user has been logged in, else false
   */
  public boolean isLoggedIn() {
    return loggedIn;
  }

  public boolean isSmw() {
    return smw;
  }

  public void setSmw(boolean smw) {
    this.smw = smw;
  }

  public String getWikiId() {
    return wikiId;
  }

  public void setWikiId(String wikiId) {
    this.wikiId = wikiId;
  }

  public boolean isRestricted() {
    return restricted;
  }

  public void setRestricted(boolean restricted) {
    this.restricted = restricted;
  }

  /**
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * @param category
   *          the category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  public String getHomePage() {
    return homePage;
  }

  public void setHomePage(String homePage) {
    this.homePage = homePage;
  }

  /**
   * @return the frame
   */
  public String getFrame() {
    return frame;
  }

  /**
   * @param frame
   *          the frame to set
   */
  public void setFrame(String frame) {
    this.frame = frame;
  }

  public String getFrontendUrl() {
    return frontendUrl;
  }

  public void setFrontendUrl(String frontendUrl) {
    this.frontendUrl = frontendUrl;
  }

  /**
   * Does the logout
   * 
   * @throws Exception
   */
  public void logOut() throws Exception {
    if (restricted)
      logout();
    loggedIn = false;
    auth = null;
  }

  /**
   * Checks whether this category is defined in this page
   * 
   * @param pageName
   * @param categoryName
   * @return true, if the category is defined, else false
   * @throws Exception
   */
  public boolean containsCategory(String pageName, String categoryName)
      throws Exception {
    String content = getPageContent(pageName);
    boolean result = checkCategory(content, categoryName);
    return result;
  }

  /**
   * check the category
   * 
   * @param content
   * @param categoryName
   * @return true if the category fits the given categoryName
   * @throws Exception
   */
  public boolean checkCategory(String content, String categoryName)
      throws Exception {
    boolean result = false;
    if (content != null) {
      String pattern = "(\\[\\[Category:)(.*?)(\\]\\])";
      for (Matcher m = Pattern.compile(pattern, Pattern.MULTILINE)
          .matcher(content); m.find();) {
        String category = m.group(2).trim();
        if (category.equals(categoryName.trim())) {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  /**
   * get the base url
   * 
   * @return the baseurl of this site
   */
  public String getBaseUrl() {
    String url = this.getSiteurl() + this.getScriptPath();
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    return url;
  }

  /**
   * fix the given attribute of the given node if the value starts with the given prefix
   * e.g. in an img node the src attribute might be fixed if it starts with "/images"
   * @param node
   * @param attribute
   * @param prefix
   * @return the original attribute Value
   */
  public String fixNode(TagNode node, String attribute, String prefix) {
    String attributeValue = node.getAttributeByName(attribute);
    if (attributeValue != null && attributeValue.startsWith(prefix)) {
      node.removeAttribute(attribute);
      node.addAttribute(attribute, this.getBaseUrl() + attributeValue);
    }
    return attributeValue;
  }
  
  /**
   * make sure img tags with src pointing to local /images are rewritten to
   * point to original
   * 
   * @param root
   * @throws Exception
   */
  public void fixImages(TagNode root) throws Exception {
    // fix images
    List<TagNode> imgNodes = Html.getXpathNodes(root, "//img");
    for (TagNode img : imgNodes) {
      String src=fixNode(img,"src","/images");
      String srcset = img.getAttributeByName("srcset");
      if (srcset != null) {
        String newsrcset = "";
        String delim = "";
        String[] srcsetparts = srcset.split(",");
        for (String srcsetpart : srcsetparts) {
          String newsrcsetpart = srcsetpart.trim();
          if (newsrcsetpart.startsWith("/images")) {
            newsrcsetpart = this.getBaseUrl() + srcsetpart;
          }
          newsrcset += delim + newsrcsetpart;
          delim = ", ";
        }
        img.removeAttribute("srcset");
        img.addAttribute("srcset", newsrcset);
      }
      if (debug) {
        LOGGER.log(Level.INFO, "src=" + src);
      }
    }
  }

  /**
   * fix video tags
   * 
   * @param root
   * @throws Exception
   */
  public void fixVideos(TagNode root) throws Exception {
    List<TagNode> videoSources = Html.getXpathNodes(root, "//video/source");
    for (TagNode video : videoSources) {
      fixNode(video,"src","/videos");
    }
  }
  
  /**
   * fix object tags e.g for PDF display
   * 
   * @param root
   * @throws Exception
   */
  public void fixObjects(TagNode root) throws Exception {
    List<TagNode> objectNodes = Html.getXpathNodes(root, "//object");
    for (TagNode objectNode : objectNodes) {
      fixNode(objectNode,"data","/images");
      List<TagNode> anchorNodes = Html.getXpathNodes(objectNode, "//a");
      for (TagNode anchorNode : anchorNodes) {
        fixNode(anchorNode,"href","/images");
      }
    }
  }

  /**
   * remove Edit Sections
   * 
   * @param root
   * @throws Exception
   */
  public void removeEditSections(TagNode root) throws Exception {
    List<TagNode> editSections = Html.getXpathNodes(root,
        "//span[@class='mw-editsection']");
    for (TagNode editSection : editSections) {
      editSection.getParent().removeChild(editSection);
    }
  }

  /**
   * remove the nodes with the given tagname
   * 
   * @param root
   * @param tagName
   */
  public void removeTagNode(TagNode root, String tagName) {
    TagNode[] tags = root.getElementsByName(tagName, false);
    if (tags != null && tags.length == 1) {
      TagNode tag = tags[0];
      TagNode parent = tag.getParent();
      List<TagNode> childs = tag.getChildTagList();
      // remove the full node
      parent.removeChild(tag);
      if ("body".equals(tagName)) {
        for (TagNode child : childs) {
          parent.addChild(child);
        }
      }
    }
  }

  /**
   * fix the MediaWiki elements in the given html code
   * 
   * @param html
   *          - the original html
   * @return - the html with fixes
   * @throws Exception
   */
  public String fixMediaWikiHtml(String html) throws Exception {
    TagNode root = Html.getDom(html);
    fixImages(root);
    fixVideos(root);
    fixObjects(root);
    removeEditSections(root);
    this.removeTagNode(root, "head");
    this.removeTagNode(root, "body");
    html = Html.getHtml(root);
    if (html == null)
      return "";
    return html;
  }

  /**
   * frame the html retrieved the given title using the rythm template for this
   * BackendWiki
   * 
   * @param sitePage - the page to frame
   * @param uriInfo 
   * @return the html code
   * @throws Exception
   */
  public String frame(SitePageInfo sitePage, UriInfo uriInfo) throws Exception {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("uriInfo", uriInfo);
    rootMap.put("postService", PostManager.getInstance());
    if (sitePage.getPostToken()!=null) {
      rootMap.put("postToken", sitePage.getPostToken());
    }
    rootMap.put("title", sitePage.getPageTitle());
    if (siteinfo != null)
      rootMap.put("lang", this.siteinfo.getLang());
    String html = getPageHtml(sitePage.getPageTitle());
    html = fixMediaWikiHtml(html);
    rootMap.put("content", html);
    String lframe=this.getFrame();
    if (this.smw) {
      PropertyMap smwprops = this.getSMWProperties(sitePage.getPageTitle());
      if (smwprops.getMap().containsKey("Frame"))
        lframe=smwprops.get("Frame");
      rootMap.put("smwprops", smwprops);
    }
    String template = getTemplate(lframe);
    RythmContext rythmContext = RythmContext.getInstance();
    if (this.wikiTemplateResourceLoader==null) {
      wikiTemplateResourceLoader=new WikiTemplateResourceLoader(this,rythmContext);
    }
    String result = rythmContext.render(template, rootMap);
    return result;
  }

  private static final String SOURCETAG_BEGIN = "<source";
  private static final String SOURCE_ATTRIBUTE_PATTERN = "[\\s](id|lang|cache|line)(\\s?=?\'([^\']*)\')?";
  private static final String SOURCETAG_PATTERN = SOURCETAG_BEGIN + "("
      + SOURCE_ATTRIBUTE_PATTERN + ")*" + "[\\s]?>";
  static final Pattern SOURCETAG_REGEX = Pattern.compile(SOURCETAG_PATTERN);
  private static final String SOURCETAG_END = "</source>";

  /**
   * get the template Code from the given pageTitle
   * 
   * @param pageTitle
   * @return the templateCode
   * @throws Exception
   */
  public String getTemplate(String pageTitle) throws Exception {
    String pageContent = this.getPageContent(pageTitle);
    if ((pageContent == null) || pageContent.isEmpty()) {
      pageContent = "error: template " + pageTitle + " is empty";
    } else {
      final Matcher matcher = SOURCETAG_REGEX.matcher(pageContent);
      if (matcher.find()) {
        int matchend = matcher.end();
        int sourcendpos = pageContent.indexOf(SOURCETAG_END);
        if (sourcendpos > 0) {
          pageContent = pageContent.substring(matchend, sourcendpos);
        }
      }
    }
    return pageContent;
  }

  /**
   * get the semantic MediaWiki properties for the given pageTitle
   * 
   * @param pageTitle
   *          - the pageTitle to get the properties for
   * @return - a map of properties
   * @throws Exception
   */
  public PropertyMap getSMWProperties(String pageTitle)
      throws Exception {
    PropertyMap result=new PropertyMap();
    String params = "&subject=" + super.encode(pageTitle);
    Api api = getActionResult("browsebysubject", params, null, null, "json");
    if (api != null) {
      Query query = api.getQuery();
      List<Property> data = query.getData();
      result.init(data);
    }
    return result;
  }

}
