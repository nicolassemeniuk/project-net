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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net.project.database.DBBean;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * Abstract Generic Search class.
 * This implements the IObjectSearch interface and declares all additional
 * methods as abstract - thus, it is not mandatory that this class is used.
 */
public abstract class GenericSearch
        implements IObjectSearch {
    /** Internal storage of the search results */
    protected ArrayList m_results = new ArrayList();

    /** Provides a collection of spaceIDs to search. */
    private final Collection spaceIDList = new ArrayList();

    /** Provides a list of spaces to search. */
    private final Collection searchSpaces = new ArrayList();
    
    /** Internal storage for search error messages */
    protected ArrayList searchErrors = new ArrayList();


    protected final DBBean m_db = new DBBean();

    /** the type of seach to perform */
    protected int search_type = IObjectSearch.ADVANCED_SEARCH;
    protected User m_user = null;
    protected String m_parentObjectID = null;

    /**
     * Creates a new GenericSearch object.
     * Sets default search type.
     */
    public GenericSearch() {
        m_user = SessionManager.getUser();
        setSearchType(getDefaultSearchType());
    }

    /**
     * Returns the current user.
     * 
     * @return the current user
     */
    protected User getUser() {
        return this.m_user;
    }

    /**
     * Set the parent object context.
     * The parent object context is  required by some search objects such as form data.
     * 
     * @param objectID the id of the parent object.
     */
    public void setParentObjectID(String objectID) {
        m_parentObjectID = objectID;
    }


    /**
     * Sub-classes should override this value.
     * 
     * @return null
     */
    public String getDisplayName() {
        // Does nothing, extending class must override.
        return null;
    }

    /**
     * Add a space id to the space context of the seach.
     */
    public void addSpaceID(String spaceID) {
        spaceIDList.add(spaceID);
    }

    public void setSearchSpaces(Collection searchSpaces) {
        this.searchSpaces.clear();
        this.searchSpaces.addAll(searchSpaces);
    }

    /**
     * Returns an unmodifiable collection of spaces that are going
     * to be searched.
     * @return an unmodifiable collection where each element is a <code>Space</code>
     */
    protected Collection getSearchSpaces() {
        return Collections.unmodifiableCollection(this.searchSpaces);
    }

    /**
     * Get first space id.  Convenience for existing searches which support
     * only one space currently.
     * 
     * @return first space id or null if there are none
     */
    public String getFirstSpaceID() {
        if (spaceIDList != null && spaceIDList.size() > 0) {
            return (String) spaceIDList.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Returns the default search type, used to determine which should be
     * presented to the user by default.<br>
     * 
     * @return the search type
     */
    public abstract int getDefaultSearchType();

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     * 
     * @param searchType search type constant
     * @return true if the search type is supported, false otherwise
     */
    public abstract boolean isSearchTypeSupported(int searchType);

    /**
     * This is used to set the type of search context, that the object will perform it's
     * doSearch() and getSearchForm() methods in.
     * 
     * @param type one of the enumerations of the search types
     */
    public void setSearchType(int type) {
        this.search_type = type;
    }

    /**
     * Returns the search type
     * 
     * @return the search type
     */
    public int getSearchType() {
        return this.search_type;
    }

    /**
     * This returns a properly formated HTML form for performing the type of search that has
     * been set by setSearchType(). This will be called from a JSP page for the user
     * to search for a specific object.
     * 
     * @return the HTML Form UI for doing a simple search
     */
    public String getSearchForm(String formName, HttpServletRequest request) {
        if (this.search_type == SIMPLE_SEARCH) {
            return getSimpleSearchForm(formName, request);

        } else if (this.search_type == ADVANCED_SEARCH) {
            return getAdvancedSearchForm(formName, request);

        } else {
            return "";

        }
    }

    /**
     * This returns a properly formated HTML form for performing a simple search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.  It does not include the opening and closing <FORM> tags.  This is so you
     * specify where it is posting to and add any necessary hidden form fields.<br>
     * Sub-classes should override this method if they support the simple search type
     * <p>Sub-classes may want to call getSearchFormHeader() and getSearchFormTrailer()
     * to simplify the construction of a search Form</p>
     * 
     * @return the HTML Form UI for doing a simple search
     * @see #getSearchFormHeader
     * @see #getSearchFormTrailer
     */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
        // Does nothing, sub-class must override.
        return null;
    }

    /**
     * This returns a properly formatted HTML form for performing an advanced search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.<br>
     * Sub-classes should override this method if they support the advanced search type
     * <p>Sub-classes may want to call getSearchFormHeader() and getSearchFormTrailer()
     * to simplify the construction of a search Form</p>
     * 
     * @return the HTML Form UI for doing an advanced search
     * @see #getSearchFormHeader
     * @see #getSearchFormTrailer
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
        // Does nothing, sub-class must override.
        return null;
    }

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     * 
     * @param request the Request object.
     */
    public void doSearch(HttpServletRequest request) {
        if (this.search_type == SIMPLE_SEARCH) {
            doSimpleSearch(request);

        } else if (this.search_type == ADVANCED_SEARCH) {
            doAdvancedSearch(request);

        }
    }

    /**
     * This will actually seach the database for the object matching the passed in keyowrd.
     * It will then store these results somewhere internally in the class, so that they
     * can be used by the getXMLResults() method.
     * 
     * @param keyword the keyword to search on
     */
    public void doKeywordSearch(String keyword) {
        // Does nothing.  Sub-class must override.
    }

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSimpleSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.<br>
     * This should be overridden by Search Objects that support simple searches
     * 
     * @param request the Request object.
     */
    public void doSimpleSearch(HttpServletRequest request) {
        // Does nothing.  Sub-class must override.
    }

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getAdvancedSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     * This should be overridden by Search Objects that support advanced searches
     * 
     * @param request the Request object.
     */
    public void doAdvancedSearch(HttpServletRequest request) {
        // Does nothing.  Sub-class must override.
    }

    /**
     * Returns all xml results.<br>
     * Performs <code>getXMLResults(1, results.size());</code>
     * 
     * @return XML Formatted results of the search
     * @see net.project.search.GenericSearch#getXMLResults(int start, int end)
     */
    public String getXMLResults() {
        return getXMLResults(1, m_results.size());
    }


    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     * <p/>
     * The XML should be in the following format:<pre>
     * <channel>
     *   <table_def>
     *     <col>COLUMN1 NAME</col>
     *     <col>COLUMN2 NAME</col>
     *        .
     *        .
     *   </table_def>
     *   <content>                        --YOU MUST HAVE THE SAME NUMBER OF DATA
     *      <row>                          -- DATA TAGS IN EACH ROW AS COLUMNS
     *       <data>TEXT DATA</data>
     *       <data_href>                  --USE <DATA> AND <DATA_HREF> INTERCHANGABLY
     *         <label>TEXT DATA</label>
     *         <href>URL</href>
     *       </data_href>
     *        .
     *        .
     *     </row>
     *      .
     *      .
     *    </content>
     *  </channel></pre>
     * 
     * @param start the starting row (inclusive, starts at 1)
     * @param end   the ending row (inclusive, ends at maximum size of results)
     * @return XML Formatted results of the search
     */
    public abstract String getXMLResults(int start, int end);


    /**
     * Will return the number of results found by the search.
     * 
     * @return the number of results found by the search.
     */
    public int getResultCount() {
        return m_results.size();
    }

    /**
     * Returns start for search form, including title and an open table division
     * 
     * @param title the title for the search form
     * @return the html for the start of the search form with open &lt;td> tag
     */
    protected static String getSearchFormHeader(String title) {
        StringBuffer formString = new StringBuffer();
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");
        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% align=left NOWRAP>" + title + "</td>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<td>\n");
        return formString.toString();
    }

    /**
     * Returns and of search form, closes table division
     * 
     * @return the html for the end of the search form with an initial closing &lt;td> tag
     */
    protected static String getSearchFormTrailer() {
        StringBuffer formString = new StringBuffer();
        formString.append("\n</td>");
        formString.append("\n<td>&nbsp;</td></TR></TABLE>");
        return formString.toString();
    }

    /**
     * Returns the search level string for a JSP page based on a search type
     * 
     * @return search level string
     */
    public final String getSearchLevel(int searchType) {
        return SEARCH_LEVELS[searchType];
    }

    /**
     * Indicates whether a single space is to be searched or
     * multiple spaces are to be searched.
     * @return true if this is a single search space; false if multiple
     */
    protected boolean isSingleSpaceSearch() {
        // Search space might be zero (meaning current space)
        // Or one (meaning search one specific space)
        return (getSearchSpaces().size() <= 1);
    }

    /**
     * Returns localized error messages for errors that occurecd during
     * the calls to do*Search() methods.
     * 
     * @return search errors
     */
	public Collection getSearchErrors() {
		return Collections.unmodifiableCollection(searchErrors);
	}
	
	/**
	 * A simple helper function. Returns string representation of the value of 
	 * a property extracted from the request. Safe to pass null for every parameter.
	 * Never returns null. May return empty string.
	 * 
	 * @param request
	 * @param attributeName 
	 * @return string value
	 */
	protected String getParameterSafe(HttpServletRequest request,
			String attributeName) {
		if (request == null)
			return "";
		Object value = request.getParameter(attributeName);
		return value == null ? "" : String.valueOf(value);
	}
	
	/**
	 * A simple helper function. Returns 'SELECTED' if the acual value is the
	 * same as optionValue, empty string otherwise. Used to resore 
	 * selection in <select> HTML tags.
	 * 
	 * @param optionValue
	 * @param actualValue
	 * @return
	 */
	protected String getOptionSelection(int optionValue, String actualValue) {
		try {
			int actualIntValue = Integer.parseInt(actualValue);
			if (optionValue == actualIntValue)
				return "SELECTED";
		} catch (NumberFormatException e) {
		}
		return "";
	}
	/**
	 * A simple helper function. Returns 'SELECTED' if the acual value is the
	 * same as optionValue, empty string otherwise. Used to resore 
	 * selection in <select> HTML tags.
	 * 
	 * @param optionValue
	 * @param actualValue
	 * @return
	 */
	protected String getOptionSelection(String optionValue, String actualValue) {
		if (actualValue == null)
			return "";
		else if (actualValue.equals(optionValue))
			return "SELECTED";
		return "";

	}

}


