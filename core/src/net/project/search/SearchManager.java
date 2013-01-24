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
|   $Revision: 20727 $
|   $Date: 2010-04-20 11:28:19 -0300 (mar, 20 abr 2010) $
|   $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.form.FormMenu;
import net.project.form.FormMenuEntry;
import net.project.persistence.PersistenceException;
import net.project.space.SpaceType;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * Bean that provides centralized searching on various items.
 */
public class SearchManager extends GenericSearch {
    /** Constant for "classID". */
    private static final String CLASS_ID = "classID";
    /** Object types to be searched.  This is an array list of SearchObjectType */
    private ArrayList searchObjectTypes = new ArrayList();
    /** list of spaces to be searched.  This is an array list of SearchSpace */
    private ArrayList searchSpaces = new ArrayList();
    /** Search type. */
    private int m_searchType = IObjectSearch.SIMPLE_SEARCH;
    /** Are we searching all object types ? */
    private boolean isSearchAll = false;
    /** Indicates whether all object types may be searched. */
    protected boolean m_allowAll = false;
    /** the number of search results items to display on the page at once. */
    private int m_maxDisplayNum = 25;
    /** Contains XML formatting information and utilities specific to this object. */
    private XMLFormatter m_formatter = new XMLFormatter();

    /** Support object types */
    private static final HashMap SUPPORTED_OBJECT_TYPES = SearchManagerObjectTypes.getSupportedObjectTypes();
    private String referLink = "";
    private int moduleID = 0;
    private int actionID = 1;
    private String keyword = ""; 
    /** Distinct search errors */
	private Set distinctSearchErrors = new HashSet();

    /**
     * Creates a new SearchManager.  Default search space is user's current
     * space.  Default object type is Document.
     */
    public SearchManager() {
        searchObjectTypes.add(SUPPORTED_OBJECT_TYPES.get(ObjectType.DOCUMENT));
        setSearchType(getDefaultSearchType());
    }

    /**
     * Set the type of search: simple, advanced, browse.
     *
     * @param type one of the enumerations of the search types.  Allowed types
     * defined in IObjectSearch: SIMPLE_SEARCH, ADVANCED_SEARCH, BROWSE_SEARCH,
     * QUICK_SEARCH.
     * @see net.project.search.IObjectSearch
     */
    public void setSearchType(int type) {
    	if(m_searchType != type)
    		searchErrors.clear();
        m_searchType = type;
    }

    /**
     * Returns the search type.
     *
     * @return the enumeration of the search type.  Allowed types defined in IObjectSearch: SIMPLE_SEARCH, ADVANCED_SEARCH, BROWSE_SEARCH, QUICK_SEARCH.
     * @see net.project.search.IObjectSearch
     */
    public int getSearchType() {
        return m_searchType;
    }

    public void setSearchAll(boolean isSearchAll) {
        this.isSearchAll = isSearchAll;
    }

    public boolean isSearchAll() {
        return this.isSearchAll;
    }

    /**
     * Set the ObjectType to perform the search on.  This clears out any
     * previous object types set.
     *
     * @param objectType the ObjectType to perform the search on, ObjectType.ALL
     * to search all supported objects.
     * @throws SearchException if the object type is not supported
     */
    public void setSearchObjectType(String objectType) throws SearchException {
        searchObjectTypes.clear();

        // Default to Document search
        if (objectType == null || objectType.equals("")) {
            objectType = ObjectType.DOCUMENT;
        }

        // Add object type to list to be searched, or add all
        if (objectType.equals(ObjectType.ALL)) {
            Iterator it = SUPPORTED_OBJECT_TYPES.values().iterator();
            while (it.hasNext()) {
                addSearchObjectType(((SearchObjectType)it.next()).getType());
            }

            setSearchAll(true);

        } else {
            addSearchObjectType(objectType);

            setSearchAll(false);
        }
    }

    /**
     * Set the ObjectType to perform the search on.  This clears out any
     * previous object types set.
     *
     * @param spaceType the ObjectType to perform the search on, ObjectType.ALL
     * to search all supported objects.
     * @throws SearchException if the object type is not supported
     */
    public ArrayList getSearchObjectTypesForSpace(SpaceType spaceType) throws SearchException {
        searchObjectTypes.clear();

        Iterator it = SearchManagerObjectTypes.getSupportedObjectTypesForSpace(spaceType).values().iterator();

        while (it.hasNext()) {
            addSearchObjectType(((SearchObjectType)it.next()).getType());
        }

        // Add object type to list to be searched, or add all
        if (spaceType == null) {
            setSearchAll(true);
        } else {
            setSearchAll(false);
        }
        return this.searchObjectTypes;
    }

    /**
     * Add an object type to be searched.  The search type becomes Quick Search
     * if more than one object type has been added.
     *
     * @param objectType the object type to be searched
     * @throws SearchException if the object type is not supported
     */
    public void addSearchObjectType(String objectType) throws SearchException {
        SearchObjectType searchObjectType = null;

        // Ensure object type is supported
        searchObjectType = (SearchObjectType)SUPPORTED_OBJECT_TYPES.get(objectType);
        if (searchObjectType == null) {
            throw new SearchException("Unsupported object type '" + objectType + "' specified.");
        }

        // Add to our list of object types to be searched
        searchObjectTypes.add(searchObjectType);

        if (isMultipleObjectTypes()) {
            this.m_searchType = IObjectSearch.QUICK_SEARCH;
        }
    }

    /**
     * Get the ObjectType to perform the search on.  Actually returns the first
     * object type of the object types list.
     *
     * @return the ObjectType to perform the search on, ObjectType.ALL to search all supported objects.
     */
    public String getSearchObjectType() {
        if (isSearchAll()) {
            return ObjectType.ALL;
        } else {
            if (getSearchObjectTypes() != null && getSearchObjectTypes().size() > 0) {
                return ((SearchObjectType)getSearchObjectTypes().get(0)).getType();
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the list of object types to be searched.
     *
     * @return the list of object types to be searched.
     */
    public ArrayList getSearchObjectTypes() {
        return this.searchObjectTypes;
    }

    /**
     * Set the number of search result items to display on a single page.
     */
    public void setMaxDisplayNum(int max) {
        m_maxDisplayNum = max;
    }


    /**
     * Get the number of search result items to display on a single page.
     */
    public int getMaxDisplayNum() {
        return m_maxDisplayNum;
    }


    /**
     * Add a space to the search scope.
     *
     * @param spaceID the space id
     * @param spaceName the space name
     * @param spaceDescription the space description
     */
    public void addSearchSpace(String spaceID, String spaceName, String spaceDescription) {
        SearchSpace searchSpace = new SearchSpace();
        searchSpace.setID(spaceID);
        searchSpace.setName(spaceName);
        searchSpace.setDescription(spaceDescription);

        // Add search space to list of search spaces
        searchSpaces.add(searchSpace);
    }

    public void setSearchSpaces(ArrayList searchSpaces) {
        this.searchSpaces = searchSpaces;
    }

    public Collection getSearchSpaces() {
        return this.searchSpaces;
    }


    /**
     * Returns the default search type, used to determine which should be
     * presented to the user by default.
     *
     * @return the search type
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public int getDefaultSearchType() {
        if (getSearchObjectTypes() != null) {
            if (isMultipleObjectTypes()) {
                return IObjectSearch.QUICK_SEARCH;
            } else {
                if (getSearchObjectType() != null) {
                    return ObjectSearchFactory.make(getSearchObjectType()).getDefaultSearchType();
                } else {
                    return IObjectSearch.SIMPLE_SEARCH;
                }
            }
        } else {
            return IObjectSearch.QUICK_SEARCH;
        }
    }


    /** Set search to allow searching "all" supported object types. */
    public void setAllowAll(boolean allowAll) {
        m_allowAll = allowAll;
    }


    /** Does the search allow searching "all" supported object types. */
    public boolean isAllowAll() {
        return m_allowAll;
    }

    /**
     * Clear the properties and data.
     */
    public void clear() {
        searchSpaces.clear();
        searchObjectTypes.clear();
        setSearchAll(false);
        searchObjectTypes.add(SUPPORTED_OBJECT_TYPES.get(ObjectType.DOCUMENT));
        setSearchType(getDefaultSearchType());
        m_allowAll = false;
        searchErrors.clear();
    }


    /**
     * Clears search results without affecting any other attributes.
     */
    public void clearResults() {
        SearchSpace searchSpace = null;
        Iterator it = searchSpaces.iterator();
        while (it.hasNext()) {
            searchSpace = (SearchSpace)it.next();

            searchSpace.getSearchObjects().clear();
        }
    }

    /**
     * Returns the HTML option list of all seachable objects without the
     * <select> tags.
     */
    public String getSearchTypeOptionList(SpaceType spaceType) {
        StringBuffer sb = new StringBuffer();

        // Allow searching all objects.
        if (m_allowAll) {
            sb.append("<option value=\"all\"");
            if (isSearchAll()) {
                sb.append(" selected");
            }
            sb.append(">" + PropertyProvider.get("prm.global.search.manager.option.all.name") + "</option>\n"); // All


        }

        SearchObjectType searchObjectType = null;
        Iterator it = SearchManagerObjectTypes.getSupportedObjectTypesForSpace(spaceType).values().iterator();

        while (it.hasNext()) {
            searchObjectType = (SearchObjectType)it.next();

            sb.append("<option value=\"" + searchObjectType.getType() + "\"");
            if (!isSearchAll() && isObjectTypeSelected(searchObjectType.getType())) {
                sb.append(" selected");
            }
            sb.append(">" + searchObjectType.getDescription() + "</option>\n");
        }
        return sb.toString();
    }

    /**
     * Indicates whether object type is selected.  An object type is selected
     * if it is present in the search object types list.
     * @param objectType the object type to check
     * @return true if the object type is selected; false otherwise
     */
    private boolean isObjectTypeSelected(String objectType) {
        boolean isSelected = false;

        Iterator it = searchObjectTypes.iterator();
        while (it.hasNext()) {
            if (((SearchObjectType)it.next()).getType().equals(objectType)) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    /**
     * Indicates whether multiple object types are selected for searching.
     */
    private boolean isMultipleObjectTypes() {
        if (searchObjectTypes != null && searchObjectTypes.size() > 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     * @param searchType the search type constant
     * @return true if the search type is supported, false otherwise
     * @see IObjectSearch#SIMPLE_SEARCH
     * @see IObjectSearch#ADVANCED_SEARCH
     * @see IObjectSearch#BROWSE_SEARCH
     * @see IObjectSearch#QUICK_SEARCH
     */
    public boolean isSearchTypeSupported(int searchType) {
        boolean isSupported = false;

        if (isMultipleObjectTypes()) {
            if (searchType == IObjectSearch.QUICK_SEARCH) {
                isSupported = true;
            } else {
                isSupported = false;
            }

        } else {
            isSupported = ObjectSearchFactory.make(getSearchObjectType()).isSearchTypeSupported(searchType);
        }

        return isSupported;
    }


    /**
     * This returns a properly formated HTML form for performing the type of search that has
     * been set by setSearchType(). This will be called from a JSP page for the user
     * to search for a specific object.  Quick search forms must be implemented by the JSP programmer.
     * @return the HTML Form UI for doing a simple search.
     */
    public String getSearchForm(String formName, HttpServletRequest request) {
        IObjectSearch search;

        if (isMultipleObjectTypes()) {
            return getQuickSearchForm();

        } else {
            search = ObjectSearchFactory.make(getSearchObjectType());
            search.setSearchType(getSearchType());
            search.setSearchSpaces(getSearchSpaces());
            return search.getSearchForm(formName, request);
        }
    }


    /**
     * This returns a properly formated HTML form for performing a quick search
     * on one or more object types.  This will be called from a JSP page for the
     * user to search for a specific object.  It does not include the opening
     * and closing <FORM> tags.  This is so you specify where it is posting to
     * and add any necessary hidden form fields.
     *
     * @return the HTML Form UI for doing a quick search
     */
    public String getQuickSearchForm() {
        StringBuffer formString = new StringBuffer();

        formString.append(getSearchFormHeader(PropertyProvider.get("prm.global.search.manager.channel.quicksearch.title"))); // Quick Search
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"100%\">");
        formString.append("\n<tr>");
        formString.append("\n<td class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.manager.keyword.label") + "&nbsp;&nbsp;</td>"); // Keyword:
        formString.append("\n<td class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"KEYWORD\">");
        formString.append("</TD>");
        formString.append("\n</TR></TABLE>");
        formString.append(getSearchFormTrailer());

        return formString.toString();
    }


    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     * @param request the Request object.
     */
    public void doSearch(HttpServletRequest request) {

        // Initialize search object lists in each space to be searched
        initSearchObjectList(request);
        
        // clear search errors
        searchErrors.clear();

        SearchSpace searchSpace = null;
        Iterator spaceIterator = searchSpaces.iterator();
        while (spaceIterator.hasNext()) {
            searchSpace = (SearchSpace)spaceIterator.next();

            try {
                searchObjects(searchSpace, request);
            } catch (SearchException se) {

                System.out.println(" Exception at search " + se);
                // No results.
            }
        }

    }

    /**
     * Search all objects within a specified search space.  Each object is
     * searched, each object maintains its own results.
     *
     * @param searchSpace the space to search
     */
    private void searchObjects(SearchSpace searchSpace, HttpServletRequest request) throws SearchException {
        if (request.getParameter("otype") != null) {
            setSearchObjectType(request.getParameter("otype"));
        }

        if (request.getParameter("TYPE") != null) {
            try {
                m_searchType = Integer.parseInt(request.getParameter("TYPE"));
            } catch (NumberFormatException e) {
                m_searchType = -1;
            }
        }

        keyword = request.getParameter("KEYWORD");

        // For each object in the search space, do a search
        IObjectSearch searchObject = null;
        Iterator it = searchSpace.getSearchObjects().iterator();
        while (it.hasNext()) {
            searchObject = (IObjectSearch)it.next();

            // Search current space
            searchObject.addSpaceID(searchSpace.getID());

            if (m_searchType == IObjectSearch.ADVANCED_SEARCH) {
                searchObject.doAdvancedSearch(request);
            } else if (m_searchType == IObjectSearch.SIMPLE_SEARCH) {
                searchObject.doSimpleSearch(request);
            } else {
                searchObject.doKeywordSearch(keyword);
            }
            
            // update errors with errors produced by search object
            searchErrors.addAll(searchObject.getSearchErrors());
        }
    }

    /**
     * Builds a list of search objects for an "all" QuickSearch for each space
     * to be searched.  A space's list of search objects includes all forms
     * in that space.  Currently the same list of object types is searched
     * in all spaces.
     * <br><b>Preconditions:</b>
     * <li><code>searchSpaces</code> contains one or more <code>SearchSpace</code> objects</li>
     * <br><b>Postconditions:</b>
     * <li>All <code>SearchSpace</code> objects in <code>searchSpaces</code> are updated to include
     * objects list for that space.
     */
    private void initSearchObjectList(HttpServletRequest request) {
        Iterator it = null;
        SearchSpace searchSpace = null;

        it = searchSpaces.iterator();
        while (it.hasNext()) {
            searchSpace = (SearchSpace)it.next();

            // Set object types to search in space
            searchSpace.setSearchObjectTypes(getSearchObjectTypes());

            initSearchObjectList(searchSpace, request);
        }
    }

    /**
     * Builds a list of search objects for an "all" QuickSearch for specified
     * search space.  Search objects are added for each of the search object types
     * that the search space supports.<br>
     * For the FORM_DATA object type, each instance of Form Data in the space
     * is added as a search object.
     * @param searchSpace the space for which to build the objects list
     */
    private void initSearchObjectList(SearchSpace searchSpace, HttpServletRequest request) {

        // For each object type supported by the searchSpace
        // add a search object of that type
        // For forms, all form objects in the current space are added as search objects
        SearchObjectType searchObjectType = null;
        Iterator it = searchSpace.getSearchObjectTypes().iterator();
        while (it.hasNext()) {
            searchObjectType = (SearchObjectType)it.next();

            if (searchObjectType.getType().equals(ObjectType.FORM_DATA)) {
                FormMenu formMenu = null;
                FormDataSearch formSearch = null;
                ArrayList formList;

                // Get the forms for the search space
                try {
                    formMenu = new FormMenu();
                    formMenu.setSpaceID(searchSpace.getID());
                    formMenu.load();
                    formList = formMenu.getEntries();
                    for (int f = 0; f < formList.size(); f++) {
                        formSearch = (FormDataSearch)ObjectSearchFactory.make(searchObjectType.getType());
                        if (request.getParameterValues(CLASS_ID) != null) {
                            String[] parameters = request.getParameterValues(CLASS_ID);
                            for (int i = 0; i < parameters.length; i++) {
                                if (parameters[i] != null && ((FormMenuEntry)formList.get(f)).getID().equals(parameters[i])) {
                                    formSearch.setParentObjectID(((FormMenuEntry)formList.get(f)).getID());
                                    searchSpace.addSearchObject(formSearch);
                                }
                            }
                        } else {
                            formSearch.setParentObjectID(((FormMenuEntry)formList.get(f)).getID());
                            searchSpace.addSearchObject(formSearch);
                        }
                    }

                } catch (PersistenceException pe) {
                	Logger.getLogger(SearchManager.class).error("Error loading a form menu: " + pe);
                } catch (net.project.form.FormException fe) {
                	Logger.getLogger(SearchManager.class).error("SearchManager.initSearchObjectList() threw a forms exception: " + fe);
                }

            } else {
                // Simply add a search object of appropriate type to the search Space
                searchSpace.addSearchObject(ObjectSearchFactory.make(searchObjectType.getType()));

            }
        }
    }

    /**
     * Will return the number of results found by the search.
     *
     * @return the number of results found by the search.
     */
    public int getResultCount() {
        SearchSpace searchSpace = null;

        int resultCount = 0;

        Iterator spaceIterator = getSearchSpaces().iterator();
        while (spaceIterator.hasNext()) {
            searchSpace = (SearchSpace)spaceIterator.next();

            resultCount += searchSpace.getResultCount();
        }

        return resultCount;
    }

    /**
     * Returns start for search form, including title and an open table division
     * @param title the title for the search form
     * @return the html for the start of the search form with open &lt;td> tag
     * /
     protected String getSearchFormHeader(String title) {
     if (m_searchObject != null)
     return m_searchObject.getSearchFormHeader(title);
     else
     return "";
     }


     /**
     * Returns and of search form, closes table division
     * @return the html for the end of the search form with an initial closing &lt;td> tag
     * /
     protected String getSearchFormTrailer() {
     if (m_searchObject != null)
     return m_searchObject.getSearchFormTrailer();
     else
     return "";
     }

     */


    /**
     * Returns all xml results.  Performs
     * <code>getXMLResults(1, results.size());</code>.
     *
     * @return XML Formatted results of the search
     * @see net.project.search.GenericSearch#getXMLResults(int start, int end)
     */
    public String getXMLResults() {
        return getXMLResults(1, getResultCount());
    }


    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     *
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
     * @param start the starting row (inclusive, starts at 1)
     * @param end the ending row (inclusive, ends at maximum size of results)
     * @return XML Formatted results of the search
     */
    public String getXMLResults(int start, int end) {
        SearchSpace searchSpace = null;
        StringBuffer xml = new StringBuffer();

        xml.append("<SearchResults>\n");

        Iterator spaceIterator = getSearchSpaces().iterator();
        while (spaceIterator.hasNext()) {
            searchSpace = (SearchSpace)spaceIterator.next();

            xml.append(searchSpace.getXMLResults(start, end));
        }

        xml.append("</SearchResults>");
        return xml.toString();
    }
    
    /**
     * Gets the spaces for the current users 
     * portfolio for searching 
     *  
     * @param userId the UserID for the space details
     */
    public void setPortofolioSpaces(String userId){
    	DBBean db = new DBBean();
    	String portfolioSpaceSQL = "select sv.space_type , sv.space_id , sv.space_name , sv.space_desc " +
    			"from pn_space_view sv where space_id in (select pn_portfolio_has_space.space_id " +
    			"	from pn_portfolio_has_space where portfolio_id in (select membership_portfolio_id from pn_person where person_id = ?)) " +
    			"and sv.record_status = 'A'";
    	try{
    		db.prepareStatement(portfolioSpaceSQL);
    		db.pstmt.setString(1, userId);
    		db.executePrepared();
    		while(db.result.next()){
    			addSearchSpace(db.result.getString("space_id"), db.result.getString("space_name"), db.result.getString("space_desc"));
    		}
    	}catch (SQLException pnetEx) {
			Logger.getLogger(SearchManager.class).error("SearchManager.getPortofolioSpaces() failed.."+pnetEx.getMessage());
		}finally{
			db.release();
		}
    }


    /**
     * Gets the presentation of the search results.  This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text.
     *
     * @return presentation of the component.
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXMLResults());
    }

    /**
     * Gets the presentation of the search results.  This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text.
     *
     * @return presetation of the component
     */
    public String getPresentation(String xml) {
        return m_formatter.getPresentation(xml);
    }


    /**
     * Sets the stylesheet file name used to render the search results.  This
     * method accepts the name of the stylesheet used to convert the XML
     * representation of the component to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }

    /**
     * Sets the referer link to the search.
     *
     * @param refLink <code>String</code>representation of the Referer Link.
     */
    public void setRefererLink(String refLink) {
        this.referLink = refLink;
    }

    /**
     * Returns the referer link to the search.
     *
     * @return <code>String</code>representation of the Referer Link
     */
    public String getRefererLink() {
        return this.referLink;
    }

    /**
     * Sets the Module ID for which the search is being carried out.
     *
     * @param moduleID <code>int</code>representation of the Module ID
     */
    public void setModuleID(int moduleID) {
        // For the time being ... Makes life more simpler in the short run
        //--- deepak

        this.moduleID = moduleID;
    }

    /**
     * Returns the Action ID for which the search is being carried out.
     *
     * @return <code>int</code>representation of the Module ID
     */
    public int getModuleID() {
        return this.moduleID;
    }

    /**
     * Sets the Action ID for which the search is being carried out.
     *
     * @param actionID <code>int</code>representation of the Action ID
     */
    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    /**
     * Returns the Action ID for which the search is being carried out.
     *
     * @return <code>int</code>representation of the Acrtion ID
     */
    public int getActionID() {
        return this.actionID;
    }
    
    /*
     * this getter is used to show last search keyword in global search textbox
     */
    public String getKeyword(){
    	return keyword;
    }
}

