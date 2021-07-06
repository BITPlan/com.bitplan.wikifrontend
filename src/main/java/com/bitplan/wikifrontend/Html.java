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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * Html helper class based on htmlCleaner
 * 
 * @author wf
 *
 */
public class Html {
	public static boolean debug=false;
	// prepare a LOGGER
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.wikifrontend");
	static HtmlCleaner htmlCleaner;

	/**
	 * get a cleaner with extended properties
	 * 
	 * @return the preconfigured HtmlCleaner
	 */
	// @SuppressWarnings("deprecation")
	public static HtmlCleaner getCleaner() {
		if (htmlCleaner == null) {
			/*
			 * String USER_AGENT = "Tracker 1.0"; int TIMEOUT = 5; HttpParams params =
			 * new BasicHttpParams();
			 * HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
			 * HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
			 * HttpProtocolParams.setUserAgent(params, USER_AGENT);
			 */
			htmlCleaner = new HtmlCleaner();
			// https://thinkandroid.wordpress.com/2010/01/05/using-xpath-and-html-cleaner-to-parse-html-xml/
			CleanerProperties props = htmlCleaner.getProperties();
			props.setAllowHtmlInsideAttributes(true);
			props.setAllowMultiWordAttributes(true);
			props.setRecognizeUnicodeChars(true);
			props.setOmitComments(true);
		}
		return htmlCleaner;
	}

	/**
	 * get the DOM for the given html
	 * 
	 * @param html
	 * @return the DOM
	 * @throws Exception
	 */
	public static TagNode getDom(String html) {
		TagNode root = null;
		try {
			root = getCleaner().clean(html);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"error for html: '" + html + "'->" + e.getMessage());
		}
		return root;
	}

	/**
	 * get the html for the given node
	 * 
	 * @param node
	 */
	public static String getHtml(TagNode node) {
		String html = htmlCleaner.getInnerHtml(node);
		return html;
	}
	
	/**
	 * get the objects for the given node and xpath
	 * 
	 * @param node
	 * @param xpath
	 * @return the array of objects
	 * @throws Exception
	 */
	public static Object[] getXpath(TagNode node, String xpath) throws Exception {
		if (debug) {
			String html = getHtml(node);
			LOGGER.log(Level.INFO,html);
		}
		Object[] result = node.evaluateXPath(xpath);
		return result;
	}

	/**
	 * get XpathNodes for the given Objects
	 * 
	 * @param objects
	 * @return the list of TagNodes
	 */
	public static List<TagNode> getXpathNodes(Object[] objects) {
		List<TagNode> nodes = new ArrayList<TagNode>();
		for (Object object : objects) {
			if (object instanceof TagNode) {
				nodes.add((TagNode) object);
			}
		}
		return nodes;

	}

	/**
	 * get the xpath nodes for the given tagnode and xpath expression
	 * 
	 * @param node
	 * @param xpath
	 * @return the list of TagNodes
	 * @throws Exception
	 */
	public static List<TagNode> getXpathNodes(TagNode node, String xpath)
			throws Exception {
		Object[] objects = getXpath(node, xpath);
		List<TagNode> nodes = getXpathNodes(objects);
		return nodes;
	}
}
