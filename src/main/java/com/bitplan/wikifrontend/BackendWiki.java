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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.TagNode;

import com.bitplan.mediawiki.japi.Mediawiki;
import com.bitplan.mediawiki.japi.SiteInfo;
import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.mediawiki.japi.api.DataItem;
import com.bitplan.mediawiki.japi.api.Login;
import com.bitplan.mediawiki.japi.api.Property;
import com.bitplan.mediawiki.japi.api.Query;
import com.bitplan.mediawiki.japi.user.WikiUser;

/**
 * Mediawiki backend site to be controlled by a wiki Frontend RESTful server
 * 
 * @author wf
 * @author wz
 */
public class BackendWiki extends Mediawiki {

  private static BackendWiki backendwiki;
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
  public static String site = "";

  /**
   * Constructor
   * 
   * @throws Exception
   */
  BackendWiki() throws Exception {
    super();
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
    String userPropertiesFileName = System.getProperty("user.home")
        + "/.wikibackend/" + user + delim + site + ".ini";
    return userPropertiesFileName;
  }

  /**
   * Reads the properties from the configuration file
   * 
   * @return
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
  }

  /**
   * return the WikUser
   * 
   * @return
   */
  public WikiUser getUser() {
    if (wikiUser == null) {
      wikiUser = WikiUser.getUser(wikiId, super.getSiteurl());
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
   * @return
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
   * @return
   */
  public String getBaseUrl() {
    String url = this.getSiteurl() + this.getScriptPath();
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    return url;
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
      String src = img.getAttributeByName("src");
      if (src != null && src.startsWith("/images")) {
        img.removeAttribute("src");
        img.addAttribute("src", this.getBaseUrl() + src);
      }
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
      String src = video.getAttributeByName("src");
      if (src != null && src.startsWith("/videos")) {
        video.removeAttribute("src");
        video.addAttribute("src", this.getBaseUrl() + src);
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
    removeEditSections(root);
    this.removeTagNode(root, "head");
    this.removeTagNode(root, "body");
    html = Html.getHtml(root);
    if (html == null)
      return "";
    return html;
  }

  /**
   * 
   * @return
   * @throws Exception
   */
  public static BackendWiki getInstance() throws Exception {
    if (backendwiki == null) {
      backendwiki = new BackendWiki();
    }
    return backendwiki;
  }

  /**
   * frame the html retrieved the given title using the rythm template for this
   * BackendWiki
   * 
   * @param title
   * @return the html code
   * @throws Exception
   */
  public String frame(String pageTitle) throws Exception {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    String template = getTemplate(this.getFrame());
    rootMap.put("title", pageTitle);
    if (siteinfo != null)
      rootMap.put("lang", this.siteinfo.getLang());
    String html = getPageHtml(pageTitle);
    html = fixMediaWikiHtml(html);
    rootMap.put("content", html);
    if (this.smw) {
      Map<String, Object> smwprops = this.getSMWProperties(pageTitle);
      rootMap.put("smwprops", smwprops);
    }
    String result = RythmContext.getInstance().render(template, rootMap);
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
    if ((pageContent==null) || pageContent.isEmpty()) {
      pageContent="error: template "+pageTitle+" is empty";
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
  public Map<String, Object> getSMWProperties(String pageTitle)
      throws Exception {
    Map<String, Object> props = new HashMap<String, Object>();
    String params = "&subject=" + super.encode(pageTitle);
    Api api = getActionResult("browsebysubject", params, null, null, "json");
    if (api != null) {
      Query query = api.getQuery();
      List<Property> data = query.getData();
      for (Property prop : data) {
        assertNotNull(prop.getDataitem());
        for (DataItem item : prop.getDataitem()) {
          int typenum = Integer.parseInt(item.getType());
          Object value = item.getItem();
          switch (typenum) {
          case 1: // Integer
            value = Integer.parseInt(value.toString());
            break;
          case 2:
            break;
          case 4: // Boolean
            break;
          case 6: // Date
            break;
          case 9: // Page
            break;
          default:
            LOGGER.log(Level.WARNING, "unknown type number " + typenum
                + " for property " + prop.getProperty() + " value " + value);
          }
          props.put(prop.getProperty(), value);
        }
      }
    }
    return props;
  }

}
