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

 /*--------------------------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the interface for creating Search classes for the various types of objects.
 *
 * To add a new object type to search, do the following.
 * 1. add the object type to the supportedObjectTypes &  objectDisplayNames lists in SearchManager.
 * 2. implement the IObjectSearch for the new object type.
 * 3. Add the new object type to the ObjectSearchFactory.
 *
 * @author Brian Conneen
 * @author Tim
 * @author Roger
 * @since 03/2000
 */
public interface IObjectSearch {
    // Search types
    /** Simple search using only one text field; usually searches several fields in the object for matches. */
    static final int SIMPLE_SEARCH = 0;
    /** Advanced search using only several object-specific fields. */
    static final int ADVANCED_SEARCH = 1;
    /** Browse search using object-specific browsing. */
    static final int BROWSE_SEARCH = 2;
    /** Quick search. */
    static final int QUICK_SEARCH = 3;

    /** Textual representation of above search types / levels for JSP page */
    static final String[] SEARCH_LEVELS = {"simple", "advanced", "browse"};

    /**
     * This is used to set the type of search context, that the object will perform it's
     * doSearch() and getSearchForm() methods in.
     *
     * @param type one of the enumerations of the search types
     */
    public void setSearchType(int type);

    /**
     * Returns the search type
     * @return the search type
     */
    public int getSearchType();

    /**
     Get the display name for the seach object, Document, Calendar, etc.
     */
    public String getDisplayName();

    /**
     Set the parent object context.
     The parent object context is  required by some search objects such as form data.
     @param objectID the id of the parent object.
     */
    public void setParentObjectID(String objectID);

    /**
     * Add a space id to the space context of the seach.
     * <p>
     * <b>Note:</b> It appears that most implementations only
     * use the first Space ID added.  As a result, this method
     * should only be called once.  Searching multiple spaces
     * is achieved through the {@link SearchManager}.
     * <p>
     * @param spaceID the ID of a space to search
     */
    public void addSpaceID(String spaceID);

    /**
     * Specifies the spaces that will be searched.
     * <p>
     * <b>Note:</b> It is not expected that an actual Search implementation
     * will search all these spaces.  Searching across spaces is
     * accomplished through {@link SearchManager}.
     * Instead, this space collection provides information that may be required
     * by a Search implementation to draw a search form.
     * </p>
     * @param searchSpaces a collection where each element is a
     * <code>Space</code> indicating the spaces that will be search
     */
    public void setSearchSpaces(Collection searchSpaces);

    /**
     * This returns a properly formated HTML form for performing the type of search that has
     * been set by setSearchType(). This will be called from a JSP page for the user
     * to search for a specific object.
     * @param formName TODO
     * @param request TODO
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSearchForm(String formName, HttpServletRequest request);

    /**
     * This returns a properly formated HTML form for performing a simple search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.
     * @param formName TODO
     * @param request TODO
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSimpleSearchForm(String formName, HttpServletRequest request);

    /**
     * This returns a properly formatted HTML form for performing an advanced search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.
     * @param formName TODO
     * @param request TODO
     *
     * @return the HTML Form UI for doing an advanced search
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request);

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doSearch(HttpServletRequest m_request);

    /**
     * This will actually seach the database for the object matching the passed in keyowrd.
     * It will then store these results somewhere internally in the class, so that they
     * can be used by the getXMLResults() method.
     *
     * @param m_keyword a <code>String</code> value that we are searching for.
     */
    public void doKeywordSearch(String m_keyword);

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSimpleSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doSimpleSearch(HttpServletRequest m_request);

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getAdvancedSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doAdvancedSearch(HttpServletRequest m_request);

    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     *
     * The XML should be in the following format:
     *
     *  <results_list> // ONE
     *     <desc_one_label>Descriptor One Label</desc_one_label> // ONE PER RESULTS_LIST
     *     <desc_two_label>Descriptor One Label</desc_two_label> // ONE PER RESULTS_LIST
     *     <result> // ONE OR MULTIPLE PER RESULTS LIST
     *        <object_id>Object's ID</object_id> // ONE PER RESULT
     *        <desc_one>Descriptor One</desc_one> // ONE PER RESULT
     *        <desc_two>Descriptor Two</desc_two> // ONE PER RESULT
     *        <href>Http://www.whatever.com/here/we/are/now.jsp</href>
     *     </result>
     *     ...
     *  </results_list>
     *
     * @return XML Formatted results of the search
     */
    public String getXMLResults();

    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     *
     * The XML should be in the following format:
     *
     *  <results_list> // ONE
     *     <desc_one_label>Descriptor One Label</desc_one_label> // ONE PER RESULTS_LIST
     *     <desc_two_label>Descriptor One Label</desc_two_label> // ONE PER RESULTS_LIST
     *     <result> // ONE OR MULTIPLE PER RESULTS LIST
     *        <object_id>Object's ID</object_id> // ONE PER RESULT
     *        <desc_one>Descriptor One</desc_one> // ONE PER RESULT
     *        <desc_two>Descriptor Two</desc_two> // ONE PER RESULT
     *   	 <href>Http://www.whatever.com/here/we/are/now.jsp</href>
     *     </result>
     *     ...
     *  </results_list>
     *
     * @param start the first result to return
     * @param end the last result to return
     * @return XML Formatted results of the search
     */
    public String getXMLResults(int start, int end);

    /**
     * Will return the number of results found by the search.
     *
     * @return the number of results found by the search.
     */
    public int getResultCount();

    /**
     * Returns the default search type, used to determine which should be
     * presented to the user by default.
     *
     * @return the search type
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public int getDefaultSearchType();

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     * @param searchType the search type constant
     * @return true if the search type is supported, false otherwise
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public boolean isSearchTypeSupported(int searchType);

    /**
     * Returns the search level string for a JSP page based on a search type
     * @return search level string
     */
    public String getSearchLevel(int searchType);
    
    /**
     * Returns localized error messages for errors that occurecd during
     * the calls to do*Search() methods.
     * 
     * @return search errors
     */
    public Collection getSearchErrors();
}



