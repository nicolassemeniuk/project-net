<%--
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
--%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="FormSearchProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*, net.project.security.*, java.util.Iterator, java.util.ArrayList" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />

<% 
	FormField field = null;
	Iterator fieldIterator;
	FormList list = null;
	FieldFilter fieldFilter = null;
	FieldFilterConstraint constraint = null;
	ArrayList searchFilters;
	String oldDisplayListID;

searchFilters = new ArrayList();
oldDisplayListID = form.getDisplayList().getID();
	
if (request.getParameter("theAction").equals("submit")) {
	list = (FormList) form.getList("cloned_list_"+(form.getListSize()-1));
	
	// GET SEARCH FILTERS

	// get the form fields
	// for each filterable field, process the request to get the user selections for that field
    ArrayList fields = new ArrayList();
    fields.addAll(form.getFields());
    fields.add(0, new FormID());

	fieldIterator = fields.iterator();
	while (fieldIterator.hasNext()) {
		field = (FormField) fieldIterator.next();
		if (field.isSearchable()) {
			fieldFilter = field.processFilterHttpPost(request);
			// build the FieldFilter ArrayList
	        if ((fieldFilter != null) && (fieldFilter.size() > 0) && ((constraint = fieldFilter.getConstraint(0)) != null) && (constraint.get(0) != null)) {
				if (list != null) {
	        		fieldFilter.setList(list);
	            	searchFilters.add(fieldFilter);
				}	
	        }
		}
	}
	// set the filters for the current FormList.
	list.setFieldFilters(searchFilters);
	form.setDisplayListID("cloned_list_"+(form.getListSize()-1));
} else if (request.getParameter("theAction").equals("cancel")) {
    // CANCEL
    // remove the "Custom Search Results" list.	if(list !=null ){
    form.removeList("cloned_list_"+(form.getListSize()-1));
    form.setDisplayListID(oldDisplayListID);
}

// FORWARD BACK TO LIST with security settings.
// Security JSP requires these attributes
request.setAttribute("module", Integer.toString(net.project.base.Module.FORM));
request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
// Security JSP requires this session value
session.putValue("objectID", form.getID());
pageContext.forward("FormList.jsp?load=false");
%>
