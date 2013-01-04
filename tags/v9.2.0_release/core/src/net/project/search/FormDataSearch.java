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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.form.DateField;
import net.project.form.FieldData;
import net.project.form.FieldDomainValue;
import net.project.form.FieldFilter;
import net.project.form.FieldFilterConstraint;
import net.project.form.Form;
import net.project.form.FormData;
import net.project.form.FormField;
import net.project.form.FormList;
import net.project.form.FormMenu;
import net.project.form.NumberField;
import net.project.form.PersonListField;
import net.project.form.SelectionListField;
import net.project.form.TextAreaField;
import net.project.form.TextField;
import net.project.gui.html.HTMLOptionList;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.security.SessionManager;
import net.project.space.GenericSpace;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * This class is used for searching FormData Objects. It provides the forms for
 * simple and advanced searching as well as a properly formatted XML string of
 * the results.
 *
 * @author Tim
 * @since 12/2000
 */
public class FormDataSearch extends GenericSearch {

    private Form m_form = new Form();

    /**
     * Creates a new FormDataSearch.
     */
    public FormDataSearch() {
        super();
    }

    /**
     * Get the display name of the this search Type.
     */
    public String getDisplayName() {
        m_form.setID(m_parentObjectID);
        m_form.setUser(SessionManager.getUser());
        m_form.setSpace(SessionManager.getUser().getCurrentSpace());

        try {
            if (!m_form.isLoaded())
                m_form.load();
        } catch (PersistenceException pe) {
            return PropertyProvider.get("prm.global.search.formdata.results.channel.title"); // Form
        }

        return PropertyProvider.get("prm.global.search.formdata.results.channel.withabbreviation.title", new Object[]{m_form.getName(), m_form.getAbbreviation()}); // Form:  {0} ({1})
    }


    /**
     * This returns a properly formated HTML form for performing a simple search
     * on the object. This will be called from a JSP page for the user to search
     * for a specific object. It does not include the opening and closing <FORM>
     * tags. This is so you specify where it is posting to and add any necessary
     * hidden form fields.
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();

        formString.append(getSearchFormHeader(PropertyProvider.get("prm.global.search.formdata.simple.title"))); // Simple Form Data Search
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"100%\">");
        formString.append("\n<tr>");

        if (isSingleSpaceSearch()) {
            // Only provide form selection when searching a single space
            // When searching multiple spaces it doesn't make sense to limit to
            // a single form
            formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.formdata.simple.name.label") + "&nbsp;&nbsp;</TD>");
            formString.append("\n<TD class=\"tableHeader\">" + getFormOptionList() + "</TD>");
        }

        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.formdata.simple.texttosearchfor.label") + "&nbsp;&nbsp;</TD>"); // Text to search for:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"KEYWORD\">");
        formString.append("</TD>");
        formString.append("\n</TR></TABLE>");
        formString.append(getSearchFormTrailer());
        return formString.toString();
    }


    /**
     * This returns a properly formatted HTML form for performing an advanced
     * search on the object. This will be called from a JSP page for the user to
     * search for a specific object.
     *
     * @return the HTML Form UI for doing an advanced search
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();
        formString.append(getSearchFormHeader(PropertyProvider.get("prm.global.search.formdata.advanced.title"))); // Advanced Form Data Search
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"100%\">");
        formString.append("\n<tr>");
        formString.append("\n<td>" + PropertyProvider.get("prm.global.search.formdata.advanced.tobecompleted.text") + "</td>"); // To be completed
        formString.append("\n<TR></TABLE>");
        formString.append(getSearchFormTrailer());
        return formString.toString();
    }

    /**
     * Return the html option list of forms to search, which are the forms in
     * the user's current space.
     * @return html option list of forms or empty string if none or a problem
     */
    private String getFormOptionList() {
        StringBuffer formOptionList = new StringBuffer();
        formOptionList.append("<select name=\"classID\">\n");

        FormMenu m_formMenu = new FormMenu();
        m_formMenu.setSpace(m_user.getCurrentSpace());

        try {
            m_formMenu.load();
            formOptionList.append(HTMLOptionList.makeHtmlOptionList(m_formMenu.getEntries()));

        } catch (net.project.persistence.PersistenceException pe) {
            // 09/13/2001 - Tim - This used to indicate no forms defined
            // Now it means an error
            formOptionList.append("<option>" + PropertyProvider.get("prm.global.search.formdata.option.errorloading.name") + "</option>"); // Error loading forms
        } catch (net.project.form.FormException fe) {
            // No forms for this space(s)
            formOptionList.append("<option>" + PropertyProvider.get("prm.global.search.formdata.option.nonedefined.name") + "</option>"); // No forms defined
        }

        formOptionList.append("\n</select>");
        return formOptionList.toString();
    }


    /**
     * This will actually seach the database for the object matching the passed
     * in keyowrd. It will then store these results somewhere internally in the
     * class, so that they can be used by the getXMLResults() method.
     *
     * @param keyword the keyword to search on
     */
    public void doKeywordSearch(String keyword) {
        m_results.clear();
        m_results.addAll(searchForm(m_parentObjectID, keyword));
    }

    /**
     * Searches for form data based on the keyword in the specified request.
     * <p>
     * If a parameter called <code>classID</code> is present, then the
     * search is limited to that form.  Otherwise, the search is limited
     * to the form who's ID is in <code>m_parentObjectID</code>.
     * </p>
     * @param request the Request object containing <code>classID</code> and
     * <code>KEYWORD</code> attributes
     */
    public void doSimpleSearch(HttpServletRequest request) {

        m_results.clear();

        String classID = request.getParameter("classID");
        String keyword = request.getParameter("KEYWORD");

        if (Validator.isBlankOrNull(classID)) {
            // If we have no classID from request then this is a multi-form
            // search initiated by Search Manager
            // For that, the form ID is specified elsewhere
            classID = m_parentObjectID;
        }
        m_results.addAll(searchForm(classID, keyword));

    }

    /**
     * Performs the actual search of a single form.
     * @param classID the ID of the form to search
     * @param keyword the keyword to search on
     */
    private Collection searchForm(String classID, String keyword) {

        Collection results = new ArrayList();

        try {
            Space space = new GenericSpace(getFirstSpaceID());

            m_form.setID(classID);
            m_form.setUser(SessionManager.getUser());
            m_form.setSpace(space);
            if (!m_form.isLoaded()) {
                m_form.load();
            }

            // build a formList
            FormList formList = buildFormList(m_form);

            // if empty keyword, we get all the form data.
            if ((keyword != null) && !keyword.equals("")) {
                // build FieldSearch array containing text and TextArea fields
                // Returns an ArrayList of FieldFilters.
                formList.setFieldFilters(new ArrayList(getSearchFieldList(m_form, formList, keyword)));
            }
            formList.setCurrentSpace(space);

            // Load the matching FormData into a FormList.
            formList.loadData();

            results.addAll(buildResults(formList));

        } catch (PersistenceException pe) {
            // 06/13/2002 - Tim
            // Exceptions were already being sucked up
            // I added logging;  moved catch block down to catch newly
            // thrown exception
        	Logger.getLogger(FormDataSearch.class).error("FormDataSearch.doKeywordSearch threw a PersistenceException: " + pe);
        }

        return results;
    }

    /**
     * Builds the search results from the specified form list's data.
     * <p>
     * Assumes the form list is loaded.
     * </p>
     * @param formList the form list containing data to build
     * @return the results; each element is a <code>String[]</code>
     */
    private Collection buildResults(FormList formList) {
        Collection results = new ArrayList();

        // Now construct the result rows from the found data
        for (Iterator it = formList.getData().iterator(); it.hasNext();) {
            FormData formData = (FormData) it.next();
            FieldData fieldData = (FieldData)formData.get(m_form.getTitleField().getSQLName());

            // Start with the display ID of the form (abbreviation-seq)
            String displayValue = makeDisplayID(m_form, formData.getSeqNum()) + ": ";

            if (m_form.getTitleField() instanceof PersonListField) {
                // Add the person's name
                displayValue += Person.getDisplayNameForID((String) fieldData.get(0));

            } else if (m_form.getTitleField() instanceof SelectionListField) {
                // Add the selection

                try {

                    FieldDomainValue fieldDomainValue = new FieldDomainValue();
                    fieldDomainValue.setID((String)fieldData.get(0));
                    fieldDomainValue.load();

                    displayValue += fieldDomainValue.getName();

                } catch (PersistenceException pe) {
                    // Eat up ...
                }

            } else if ((fieldData != null) && (fieldData.size() > 0)) {
                // Add the field content
                displayValue += (String) fieldData.get(0);

            }

            // Add the row to the search results
            results.add(new String[]{formData.getID(), displayValue});

        }

        return results;
    }

    /**
     * Returns a display ID, typically of the form
     * <code>Abbreviation-sequencenumber</code>.
     * @param seqNum the sequence number of the data element for which to
     * make a display ID
     * @return the display ID
     */
    private static String makeDisplayID(Form form, int seqNum) {
        return (form.getAbbreviation() + "-" + String.valueOf(seqNum));
    }

    /**
     * Get the list of FieldFilters to use in searching.
     */
    private static List getSearchFieldList(Form form, FormList list, String keyword) {
        Iterator fieldIterator;
        FormField field;
        FieldFilter fieldFilter;
        FieldFilterConstraint constraint = null;
        ArrayList filterList = new ArrayList();
        boolean firstField = true;
        //needed to get all members of project
        Roster roster = form.getSpace().getRoster();
        fieldIterator = form.getFields().iterator();
        while (fieldIterator.hasNext()) {
            field = (FormField)fieldIterator.next();
            if ((field instanceof TextField) || (field instanceof TextAreaField) ||
                 field instanceof NumberField || field instanceof DateField || field instanceof SelectionListField 
                 || field instanceof PersonListField ) {
                 //MAF -- I'm disabling these types.  From the SQL statement that
                 //comes up, my guess is that they never worked to begin with.
                 //|| field instanceof PersonListField || field instanceof SelectionListField

                if (field instanceof NumberField) {
                    if (!Validator.isDouble(keyword)) {
                        continue;
                    }
                }

                if (field instanceof DateField) {
                    DateFormat df = SessionManager.getUser().getDateFormatter();
                    if (!df.isLegalDate(keyword)) {
                        continue;
                    } else {
                        try {
                            Date dateAsKeyword = df.parseDateString(keyword);
                            constraint = new FieldFilterConstraint();
                            constraint.setOperator("=");
                            constraint.add(df.formatDate(dateAsKeyword, "MM/dd/yy"));
                        } catch (net.project.util.InvalidDateException e) {
                            continue;
                        }
                    }

                }else if (field instanceof SelectionListField){
                	int size =  field.getDomain().getValues() != null ? field.getDomain().getValues().size() : 0; 
                	String domainIndex = "-1";
                	for(int idx=0; idx< size;idx++){
                		String domainValue = ((FieldDomainValue)field.getDomain().getValues().get(idx)).getName();                		
                		if (domainValue.trim().equals(keyword.trim())){
                			domainIndex = ((FieldDomainValue)field.getDomain().getValues().get(idx)).getID();
                		}
                	}
                	if (!domainIndex.equals("-1")){
                    	constraint = new FieldFilterConstraint();
                    	constraint.setOperator("=");
                    	constraint.add(domainIndex);
                	}else {
                		constraint = null;
                	}
                } else if (field instanceof PersonListField){
                	String personId = "-1";
                	
                	for (int i= 0; i < roster.size(); i++){
                		Person member = (Person) roster.get(i);
                		if(keyword.trim().equals(member.getDisplayName())){
                			personId = member.getID();
                		}
                	}
                	
                	if (!personId.equals("-1")){
                    	constraint = new FieldFilterConstraint();
                    	constraint.setOperator("=");
                    	constraint.add(personId);
                	}else {
                		constraint = null;
                	}
                } else  {
                    constraint = new FieldFilterConstraint();
                    constraint.setOperator("like");
                    constraint.add(keyword);
                }
                if (constraint != null){
	                fieldFilter = new FieldFilter(field);
	                fieldFilter.setList(list);
	                if (firstField) {
	                    fieldFilter.setJoinOperator("and");
	                    firstField = false;
	                } else
	                    fieldFilter.setJoinOperator("or");
	                fieldFilter.addConstraint(constraint);
	                filterList.add(fieldFilter);
                }
            }
        }
        return filterList;
    }


    /**
     * Build a temporary FormList to use for searching.
     * <p>
     * The FormList contains only the title field (which is the first
     * selectable field on the form).
     * </p>
     * @param form the form for which to build a temporary form list
     * @return the form list that includes the form's title field
     */
    private FormList buildFormList(Form form) {
        FormList list;
        list = new FormList(form);
        list.setID("1");
        list.setName("Custom Search Results");
        list.setDescription(null);
        list.addField(form.getTitleField(), "1", null, true, false, false, false, false, 0, true);
        return list;
    }


    /**
     * This will actually search the database for the objects matching the
     * criteria the user entered into the form generated by
     * getAdvancedSearchForm() method. It retrieves this criteria from the
     * HttpServletRequest object that gets passed into it. It will then store
     * these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param request the Request object.
     */
    public void doAdvancedSearch(HttpServletRequest request) {
        // TBC
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
     *
     * @param start the starting row (inclusive, starts at 1)
     * @param end the ending row (inclusive, ends at maximum size of results)
     * @return XML Formatted results of the search
     */
    public String getXMLResults(int start, int end) {
        Iterator it;
        StringBuffer sb = new StringBuffer();
        String[] row;
        String href;

        if (m_results.size() == 0) {
            return "";
        }

        if (start > m_results.size()) {
            start = m_results.size();
        }

        if (start < 1) {
            start = 1;
        }

        if (end > m_results.size()) {
            end = m_results.size();
        }

        if (end < 1) {
            end = 1;
        }

        sb.append("<channel>\n");
        sb.append("<table_def>\n");
        sb.append("<col>" + PropertyProvider.get("prm.global.search.formdata.results.instances.column") + "</col>\n"); // Form Instances
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        it = m_results.subList(start - 1, end).iterator();
        while (it.hasNext()) {
            row = (String[])it.next();
            href = URLFactory.makeURL(row[0], ObjectType.FORM_DATA);

            sb.append("<row>\n");
            sb.append("<data_href>\n");
            sb.append("<label>" + XMLUtils.escape(row[1]) + "</label>\n");
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(row[0]) + "</id>");
            sb.append("</data_href>\n");
            sb.append("</row>\n");
        }
        sb.append("</content>\n");
        sb.append("</channel>\n");

        return sb.toString();
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
        return SIMPLE_SEARCH;
    }

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     *
     * @param searchType the search type constant
     * @return true if the search type is supported, false otherwise
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public boolean isSearchTypeSupported(int searchType) {
        if (searchType == SIMPLE_SEARCH) {
            return true;
        }
        return false;
    }

}

