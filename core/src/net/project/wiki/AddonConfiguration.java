/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.wiki;

import net.project.base.property.PropertyProvider;
import net.project.wiki.tags.FormTag;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.wiki.model.Configuration;

public class AddonConfiguration extends Configuration {

	// ADD NEW WIKI PREFIX BLOCK - START //
	
	/**
	 * Project.net configured prefixes by use of system properties 
	 * required format http://someDestination/ <br><br>
	 * 
	 * How to add new prefix:
	 *  <ol>
	 *   <li> Add new system property in pnet with value containing the target location,
	 *   <li> Retreive that property value from the db into one new property of this class,
	 *   <li> Add key/value pair for that new prefix into PNET_INTERWIKI_STRINGS property of this class.
	 *  </ol>
	 * 
	 * */
	private static String wikiLinkHref = PropertyProvider.get("prm.project.wiki.configuration.innerwikiprefix.wiki");
	private static String helpLinkHref = PropertyProvider.get("prm.project.wiki.configuration.innerwikiprefix.help");
	private static String tracLinkHref = PropertyProvider.get("prm.project.wiki.configuration.innerwikiprefix.trac");
	private static String wikipediaLinkHref = PropertyProvider.get("prm.project.wiki.configuration.innerwikiprefix.wikipedia");
	
	private static String[] PNET_INTERWIKI_STRINGS = {
								"wiki", wikiLinkHref,
								"help", helpLinkHref,
								"wikipedia", wikipediaLinkHref,
								"trac", tracLinkHref
							};
	
	// ADD NEW WIKI PREFIX BLOCK - END //
	
	static {
		
		for (int i = 0; i < PNET_INTERWIKI_STRINGS.length; i += 2) {
			INTERWIKI_MAP.put(PNET_INTERWIKI_STRINGS[i], PNET_INTERWIKI_STRINGS[i + 1]);
		}
		
		TAG_TOKEN_MAP.put("form", new FormTag());	// TODO make TagConstants enum clas in tags package
		TagNode.addAllowedAttribute("url"); 

	}

	public static AddonConfiguration DEFAULT_CONFIGURATION = new AddonConfiguration();

	public AddonConfiguration() {
	}

	/**
	 * @return Returns the pNET_INTERWIKI_STRINGS.
	 */
	public static String[] getPNET_INTERWIKI_STRINGS() {
		return PNET_INTERWIKI_STRINGS;
	}
	
	
}
