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

import java.util.List;

import net.project.wiki.model.PnWikiPageModel;

/**
 * Class made as helper class for usage in T5 loop coponent. <br>
 * Represents a Map with key/value pairs. <br>
 * 
 * @author uros
 */
public class WikiIndexLoopModel {

	private String key;
	
	private List<PnWikiPageModel> values;


	public WikiIndexLoopModel() {
	}
	
	public WikiIndexLoopModel(String key, List<PnWikiPageModel> values) {
		this.key = key;
		this.values = values;
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return Returns the values.
	 */
	public List<PnWikiPageModel> getValues() {
		return values;
	}

	/**
	 * @param values The values to set.
	 */
	public void setValues(List<PnWikiPageModel> values) {
		this.values = values;
	}
	
}
