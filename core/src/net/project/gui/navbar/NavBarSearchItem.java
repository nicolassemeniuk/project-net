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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.gui.navbar;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.search.IObjectSearch;
import net.project.search.SearchException;
import net.project.search.SearchManager;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * Container to hold information about rendering a search tool for the
 * left navigation bar.  Also contains the logic to render it.
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class NavBarSearchItem implements NavBarItem {
    /** Class the provides a list of search types for the search type drop-down box */
    private SearchManager searchManager = new SearchManager();
    /** The title of the Project Search **/
    private String displayLabel = "";
    /** The url that the seerch will be submitted to **/
    private String url;
    /** The security module passed to search url. */
    private int module;
    /** The security action passed to search url. */
    private int action;
    /** Search type is a valid constant from IObjectSearch (such as IObjectSearch.QUICK_SEARCH)*/
    private int searchType;


    /**
     * Gets the title that will be displayed on top of the search tool
     *
     * @see #setDisplayLabel
     * @return the title that will be displayed on top of the search tool
     */
    public String getDisplayLabel() 
    {
        return this.displayLabel;
    }

    /**
     * Stores the title that will be displayed on top of the search tool
     *
     * @see #getDisplayLabel
     * @param argDisplayLabel The title that will be displayed on top of the search tool
     */
    public void setDisplayLabel(String argDisplayLabel)
    {
        this.displayLabel = argDisplayLabel;
    }

    /**
     * Gets the value of url that the search will be submitted to.
     *
     * @see #setUrl
     * @return the value of url that the search will be submitted to.
     */
    public String getUrl() 
    {
        return this.url;
    }

    /**
     * Sets the value of url
     *
     * @param argUrl Value to assign to this.url
     */
    public void setUrl(String argUrl)
    {
        this.url = argUrl;
    }

    /**
     * Sets the module to pass to the search URL.
     * @param module the security module
     */
    public void setModule(int module) {
        this.module = module;
    }

    /**
     * Sets the action to pass to the search URL.
     * @param action the security action
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * Gets the value of searchType
     *
     * @return the value of searchType
     */
    public int getSearchType() 
    {
        return this.searchType;
    }

    /**
     * Determine what type of search we will be doing.  Valid values for this search
     * are defined in the IObjectSearch class.  
     *
     * @param searchType an <code>int</code> value
     * @see net.project.search.IObjectSearch
     */
    public void setSearchType(int argSearchType)
    {
        this.searchType = argSearchType;
    }

    /**
     * Clear cleans out the private member variables to restore this object to post
     * creation condition.
     */
    public void clear() {
        displayLabel = "";
        url = "";
        searchType = IObjectSearch.QUICK_SEARCH;
    }

    /**
     * This method renders the XML required to display this search tool in the left
     * navigation bar.
     *
     * @param depth an <code>int</code> value indicating how far we should indent the 
     * xml document
     * @return a <code>String</code> value containing the xml for this search tag.
     */
    public String getNavBarXML(int depth) throws PnetException 
    {
        StringBuffer xml = new StringBuffer();

        //Calculate how far this node should be indented (in spaces)
        StringBuffer leftPad=new StringBuffer();
        for (int i =0; i<depth; i++)
            leftPad.append("  ");

        xml.append(leftPad).append("<Search>\n");
        xml.append(leftPad).append("  <SearchType>").append(searchType).append("</SearchType>\n");
        xml.append(leftPad).append("  <Label>").append(displayLabel).append("</Label>\n");
        xml.append(leftPad).append("  <URL>").append(url).append("</URL>\n");
        xml.append(leftPad).append("  <Module>").append(this.module).append("</Module>\n");
        xml.append(leftPad).append("  <Action>").append(this.action).append("</Action>\n");
        xml.append(leftPad).append("  <ObjectTypeList>\n");
        xml.append(leftPad).append(getObjectTypeList(leftPad.toString()));
        xml.append(leftPad).append("  </ObjectTypeList>\n");
        xml.append(leftPad).append("</Search>\n");

        return xml.toString();
    }

    /**
     * Get the types of search objects that will appear in the search object
     * drop down list.
     *
     * @param leftTag a <code>String</code> value containing a number of spaces
     * to prepend to each line of xml.
     * @return a <code>String</code> value containing encoded html that will be
     * put inside of an html "select" list.  The method will log an exception and
     * return a blank string if no object types can be fetched.
     */
    public String getObjectTypeList(String leftPad)
    {
        StringBuffer xml = new StringBuffer();
    
        try {
            //Query the search manager to find the object types supported for search
            searchManager.clear();
            searchManager.setAllowAll(true);
            searchManager.setSearchType(IObjectSearch.QUICK_SEARCH);
            //searchManager.setSearchObjectType("all");

            //Get an array list that contains all of the search types
            ArrayList searchTypes = searchManager.getSearchObjectTypesForSpace(SessionManager.getUser().getCurrentSpace().getSpaceType());

            //Iterate through all of the search types and add them to the xml
            ObjectType searchObjectType;
            for (Iterator it = searchTypes.iterator(); it.hasNext();)
            {
                searchObjectType = (ObjectType)it.next();
                xml.append(leftPad).append("  <ObjectType>\n");
                xml.append(leftPad).append("    <Type>").append(searchObjectType.getType()).append("</Type>\n");
                xml.append(leftPad).append("    <Description>").append(searchObjectType.getDescription()).append("</Description>\n");
                xml.append(leftPad).append("  </ObjectType>\n");
            }

            return xml.toString();
        } catch (SearchException se) {
        	Logger.getLogger(NavBarSearchItem.class).error("Unable to fetch object types for search: " + se);
            //We might as well not prevent loading of the whole page for this, just return
            //a blank string
            return "";
        }
    }

    /**
     * Add a child object to this NavBarItem (!!!Child objects are not supported
     * by the FeaturedMenuItem, calling this will just raise an error.)  
     *
     * @param child a <code>NavBarItem</code> that would have became the child
     * object of this object.
     */
    public void addChild(NavBarItem child) throws PnetException {
        throw new PnetException("The search tag does not support child objects");
    }
}
