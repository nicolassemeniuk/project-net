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
    info="Edit Form Include" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.*,
            net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.base.PnetException,
            net.project.resource.*,
            net.project.form.assignment.FormAssignment,
            net.project.form.*,
            net.project.security.*,
            net.project.util.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<%
	/* Process request parameters
		readOnly		-	true (default)	: form is read only
							false			: form is editable
		htmlFormName	-	(optional) name of html form that this page is included in
							if not specified, then this page inserts its own html form
		includeOtherLinks -	true (default)	: includes other links like Change History etc.
						  -	false			: No other includes
		useSessionForm 	-	true			: Use form object in session to avoid losing
											  saved information (like selected list view)
							false (default)	: Replaces form object in session to allow
											  form data to be edited when there is no
											  existing form object in session
		id				-	id of form to edit
	*/
	boolean isReadOnly = (!JSPUtils.isEqual(request.getParameter("readOnly"), "false"));
	String htmlFormName = (JSPUtils.isEmpty(request.getParameter("htmlFormName")) ?
                           "edit_form" :
                           request.getParameter("htmlFormName"));
	boolean doInsertHtmlForm = (htmlFormName.equals("edit_form"));
	boolean doIncludeOtherLinks = (!JSPUtils.isEqual(request.getParameter("includeOtherLinks"),
                                   "false"));
	boolean doUseSessionForm = (JSPUtils.isEqual(request.getParameter("useSessionForm"),"true"));
    String id = request.getParameter("id");

	String editMode = null;
	
	//Avinash : bfd 3142 Getting Present Space
	net.project.space.Space  space = user.getCurrentSpace();
	
	if (!JSPUtils.isEmpty(id)) {
		// Modify a form data object
		FormData formData = new FormData();

		if (doUseSessionForm) {
			// We are to use the form object in session
			if (form.hasErrors()) {
				// We are redisplaying this page because errors occurred
				// Do not reload old data
			} else {
				// use that form object fetch the form data for the specified id.
				form.loadData(id);
				formData = form.getData();
			}

		} else {
			// If not using the form object in session, we make the form data
			// load itself, then replace any form object in session with the
			// form object that owns the form data.
		//Avinash : bfd 3142
			formData.load(id);
			form = formData.getForm();
			space = net.project.space.SpaceFactory.constructSpaceFromID(form.getOwningSpaceID());
			form.load();
			pageContext.setAttribute("form", form, PageContext.SESSION_SCOPE);

		}

		editMode = "modify";

	} else {// Creating new form instance, requires Form object in session
		id = null;
		if (form.hasErrors()) {
			// We are redisplaying this page because errors occurred
			// Do not clear data
		} else {
			// Not resisplaying errors;  Clear all existing data
			form.clearData();
			form.getData().setCreatorEmail(null);
		}
		editMode = "create";
%>
<%
	}
	form.setUser(user);
	//Avinash : bfd 3142 setting required Space
	form.setSpace(space);

    List assignments = null;
    FormAssignment assigned = null;
    List previousAssignments = new ArrayList();
    //load the time units
    List availableUnits = new ArrayList();
    availableUnits.add(TimeQuantityUnit.HOUR);
    availableUnits.add(TimeQuantityUnit.DAY);
    availableUnits.add(TimeQuantityUnit.WEEK);

    if (form.getSupportsAssignment()) {
        //load the assignies for the space
        roster.setSpace(user.getCurrentSpace());
        roster.load();

        if (!JSPUtils.isEmpty(id)) {
            //load the assignment if any for the object
            AssignmentManager assignmentManager = new AssignmentManager();
            assignmentManager.reset();
            assignmentManager.setObjectID(id);
            assignmentManager.loadAssigneesForObject();

            assignments = assignmentManager.getAssignments();
        }
    }


%>
<%-- 
	Include Javascript functions local to this include page 
--%>
<template:import type="css" src="/styles/blog.css" />
<template:import type="javascript" src="/src/workCapture.js" />
<script language="javascript">
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
var fieldCopyFailed = false;
var form_popup;
var popupField;
var editFormName = '<%=htmlFormName%>';
var blogItFor = 'formDataItems'+'<%=user.getCurrentSpace().getSpaceType().getName().replaceAll("'", "&acute;")%>';
var objectType = 'form_data';
var workSpaceName = '<%= user.getCurrentSpace().getName().replaceAll("'", "&acute;") %>';
objectId = '<%=id%>';
var moduleId = <%=Module.FORM%>;
var spaceId='<%=user.getCurrentSpace().getID()%>';
function resizeTextArea(field, inc) {
	eval('document.forms[editFormName].' + field + '.rows +=' + inc);
}

function setFieldRef(field) {
	if (field == null)
		field = popupField;

	eval('form_popup.fieldRef = document.forms[editFormName].' + field);
}

function popupTextArea(field, label) {
	var encodedlabel = escape(label);
    var cols = document.forms[editFormName].elements[field].attributes['cols'].value;
	form_popup = window.open(JSPRootURL + '/form/TextAreaPopup.jsp?module=<%=Module.FORM%>&label='+encodedlabel+'&cols='+cols, 'form_popup', "height=700,width=500, resizable=yes");
	
	if (form_popup.isloaded == true) {
		setFieldRef(field);
		form_popup.copyFieldData();
		form_popup.focus();
	}
	else
	{
		fieldCopyFailed = true;
		popupField = field;
	}
}

function popupDate(fieldName) {
	autoDate(fieldName, JSPRootURL, editFormName);
}

function workChanged() {
    var theForm = document.forms[editFormName];
    var percentCompleteValue = document.getElementById("work_percent_complete");

	if (!checkIsPositiveNumber(theForm.work,'<%=PropertyProvider.get("prm.schedule.taskedit.error.work.validnumber.message")%>', true )) {
        theForm.work.focus();
	    goSubmit = false;
	} else {
        var workCompleteValue = getWorkHours(parseFloat(theForm.work_complete.value),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
        var workValue = getWorkHours(parseFloat(theForm.work.value), parseInt(getSelectedValue(document.getElementById("work_units"))));
        if (workCompleteValue > workValue) {				
            errorHandler(theForm.work, '<%=PropertyProvider.get("prm.form.formedit.error.moreworkcomplete.message")%>');
            theForm.work.focus();
            goSubmit = false;
        } else {
            //change the work percent complete
             goSubmit = true;
             if(parseFloat(workValue, 10) > 0) {
                 var percentComplete = parseFloat(workCompleteValue, 10)/parseFloat(workValue, 10);
                 percentComplete = roundTo(percentComplete * 100, 2);
                 percentCompleteValue.value = percentComplete;
             } else {
                percentCompleteValue.value = 0;
             }
        }
    }
}

function workUnitsChanged() {
    var theForm = document.forms[editFormName];
    var percentCompleteValue = document.getElementById("work_percent_complete");
    var workCompleteValue = getWorkHours(parseFloat(theForm.work_complete.value),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
    var workValue = getWorkHours(parseFloat(theForm.work.value), parseInt(getSelectedValue(document.getElementById("work_units"))));

    if (workCompleteValue > workValue) {	
        errorHandler(theForm.work, '<%=PropertyProvider.get("prm.form.formedit.error.moreworkcomplete.message")%>');
        goSubmit = false;
    } else {
        //change the work percent complete
        goSubmit = true;
        if(parseFloat(workValue, 10) > 0) {
            var percentComplete = parseFloat(workCompleteValue, 10)/parseFloat(workValue, 10);
            percentComplete = roundTo(percentComplete * 100, 2);
            percentCompleteValue.value = percentComplete;
        } else {
            percentCompleteValue.value = 0;
        }
    }
}

function workPercentCompleteChanged() {
    var theForm = document.forms[editFormName];
    var percentCompleteValue = document.getElementById("work_percent_complete");
    var percentComplete = theForm.work_percent_complete.value;
	if (!checkRangeInt(theForm.work_percent_complete,0,100,'<%=PropertyProvider.get("prm.schedule.taskview.resources.error.percentoutofrange.message")%>')) {
        theForm.work_percent_complete.focus();
	    goSubmit = false;
	} else {
       	goSubmit = true;
        var workCompleteValue = getWorkHours(parseFloat(theForm.work_complete.value),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
        if(parseFloat(percentComplete, 10) > 0) {
            var workValue = parseFloat(workCompleteValue, 10) * 100/parseFloat(percentComplete, 10);
            workValue = roundTo(workValue, 2);
            theForm.work.value = getWorkInUnit(workValue, parseInt(getSelectedValue(document.getElementById("work_units"))));
        } else {
            theForm.work.value = 0;
        }
    }
}

function getWorkInUnit(workValue, workUnits) {
	if (workUnits == 4) {
		return workValue;
	}else if (workUnits == 8) {
		return roundTo(workValue / 8, 2) ;
	}else if (workUnits == 16) {
		return roundTo(workValue / 40, 2);
	}else { return workValue}
}

function getWorkHours(workValue, workUnits) {
	if (workUnits == 4) {
		return workValue;
	}else if (workUnits == 8) {
		return workValue * 8;
	}else if (workUnits == 16) {
		return workValue * 40;
	}else { return workValue}
}

function roundTo(base, precision) {
  var m = Math.pow(10, precision);
  var a = Math.round(base * m) / m;
  return a;
}

</script>
<%-- End of Javascript functions --%>

<%
	if (doInsertHtmlForm) {
%>
	<form id="formToCheck" name="<%=htmlFormName%>" method="post" action="FormEditProcessing.jsp">
		<input type="hidden" name="theAction">
		<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
		<input type="hidden" name="action" />
		<input type="hidden" name="id" />
<% } %>
<input type="hidden" name="class_id" value="<jsp:getProperty name="form" property="ID" />">
<input type="hidden" name="data_object_id" value="<%=id%>">

<tr>
<td colspan="6">
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr bgcolor="#E1E1E1"> 
	<td align="left"> 
	  	<span class=smallHeaderBlack><%=PropertyProvider.get("prm.form.list.pagetitle", new Object[] {form.getName(), form.getAbbreviation()})%>
<!-- 		<jsp:getProperty name="form" property="name" /> (<jsp:getProperty name="form" property="abbreviation" />) -->
		</span>
	</td>
	
<%-- CHANGE HISTORY NOT IMPLEMENTED YET
	<td align="center">
	<%if (editMode.equals("modify") && doIncludeOtherLinks) {%>
		<a href="<%=SessionManager.getJSPRootURL()%>/form/ChangeHistory.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=id%>">Change History</a>
		&nbsp;&nbsp;
		<a href="<%=SessionManager.getJSPRootURL()%>/form/FormEdit.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=id%>">Form</a>
	<% } else { %>
		&nbsp;
	<% } %>
	</td>
--%>

</tr>
</table>

<%-- Display any errors --%>
<%	if (form.hasErrors()) { %>
		<table width="100%" class="tableContent" border="0">
			<tr>
				<td class="fieldWithError"><%=PropertyProvider.get("prm.form.edit.include.correcterror.message")%></td>
			</tr>
		</table>
<%	} %>

<%-- Display the Form assignment fields --%>
<%if (form.getSupportsAssignment()) { %>
<table width="100%" class="tableContent" border="0">
    <tr align="left" valign="middle">
    	<th nowrap class="tableHeader" width="20%"><display:get name="prm.resources.assignor.label"/></th>
        <th nowrap class="tableHeader" width="20%"><display:get name="prm.form.assignment.assigneduser.label"/></th>
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.work.label"/></th>
        <th nowrap class="tableHeader" width="10%"><display:get name="prm.form.assignment.workcomplete.label"/></th>
        <th nowrap class="tableHeader" width="10%"><display:get name="prm.form.assignment.workpercentcomplete.label"/></th>
        <th nowrap class="tableHeader" width="10%"><display:get name="prm.form.assignment.startdate.label"/></th>
        <th nowrap class="tableHeader" width="10%"><display:get name="prm.form.assignment.enddate.label"/></th>
    </tr>
<%  if(assignments != null && assignments.size() > 0) {
%>
    <tr align="left" valign="middle">
        <td colspan="7" class="rowSep"><display:get name="prm.form.assignment.assignmenthistory.label"/></td>
    </tr>
<%  }
    for(int i = 0; assignments != null && i < assignments.size(); i++) { 
    FormAssignment assignment = (FormAssignment) assignments.get(i);
    if(AssignmentStatus.personalAssignmentTypes.contains(assignment.getStatus()) && assigned == null) {
        assigned = assignment;
        continue;
    } else if (!assignment.getStatus().equals(AssignmentStatus.REJECTED)) {
        continue;
    }
%>
    <%-- Work, Work Complete, % Work Complete --%>
    <tr align="left" valign="middle">
    	<td id="exAssignorFullName" nowrap width="20%"><%=assignment.getAssignorName()%></td>
        <td id="exUsersFullName" nowrap width="20%"><%=assignment.getPersonName()%></td>
        <td id="exWork" nowrap width="15%"><%=assignment.getWork().toShortString(0,2)%></td>
        <td id="exWorkComplete" nowrap width="10%"><%=assignment.getWorkComplete().toShortString(0,2)%></td>
        <td id="exWorkPercentComplete" nowrap width="10%"><%=assignment.getPercentCompleteString()%></td>
        <td id="exStartDate" nowrap width="10%"><%=assignment.getStartTimeString()%></td>
        <td id="exEndDate" nowrap width="10%"><%=assignment.getEndTimeString()%></td>
    </tr>
<%  }%>
    <%-- Work, Work Complete, % Work Complete --%>
    <tr align="left" valign="middle">
        <td colspan="7" nowrap class="rowSep">
            <display:get name="prm.form.assignment.currentassignment.label"/>
        </td>
    </tr>
    <tr align="left" valign="middle">
        <td nowrap width="20%">
            <select id="assignorId" name="assignorUser" style="width: 90%;">
            	<option value=""><display:get name="prm.form.designer.fieldedit.type.option.none.name"/></option> 
            	<%=roster.getSelectionList(assigned == null || assigned.getAssignorID() == null ? (editMode.equals("create") ? user.getID() : null) : assigned.getAssignorID())%> 
          	</select>
        </td>    
        <td nowrap width="20%">
            <select id="assignedUserId" name="assignedUser" style="width: 90%;">
                <option value=""><display:get name="prm.form.designer.fieldedit.type.option.none.name"/></option>
                <%=roster.getSelectionList(assigned == null ? null : assigned.getPersonID())%>
            </select>
        </td>
        <td nowrap width="15%">
            <input:text elementID="work" name="work" size="5" maxLength="10" onChange="workChanged();" value='<%=assigned == null ? "" : assigned.getWork().formatAmount()%>' />
            <input:select elementID="work_units" name="work_units" onChange="workUnitsChanged();" options="<%=availableUnits%>" defaultSelected="<%=assigned == null ? TimeQuantityUnit.HOUR : assigned.getWork().getUnits()%>" />
        </td>
        <td nowrap width="10%" id="workComplete">
            <input type="hidden" id="work_complete" name="work_complete" value="<%=assigned == null ? "0" :assigned.getWorkComplete().formatAmount()%>" />
            <input type="hidden" id="work_complete_units" name="work_complete_units" value="<%=assigned == null ? TimeQuantityUnit.HOUR.getUniqueID()  : assigned.getWorkComplete().getUnits().getUniqueID()%>" />
            <%=assigned == null ? "" :assigned.getWorkComplete().toShortString(0,2)%>
        </td>
        <td nowrap width="10%" >
            <input:text elementID="work_percent_complete" name="work_percent_complete" onChange="workPercentCompleteChanged();" value='<%=assigned == null ? "" :assigned.getPercentCompleteString()%>' size="4" maxLength="7"/>
        </td>
        <td nowrap width="10%" id="startDate">
            <%=assigned == null ? "" :assigned.getStartTimeString()%>
        </td>
        <td nowrap width="10%" id="endDate">
            <%=assigned == null ? "" :assigned.getEndTimeString()%>
        </td>
    </tr>
</table>
<br />
<%}%>

<%-- Display the Form --%>
<% 
	if (isReadOnly) {
		%><%=PropertyProvider.get("prm.form.edit.include.readonlyerror.message")%>
<%
	} else {
		if (editMode.equals("create")) {
			form.writeHtml(new java.io.PrintWriter(out), false, false);
		} else {
			form.writeHtml(new java.io.PrintWriter(out), true, false);
		}
	}
%>
<%-- End of form display --%>


<%
	if (doInsertHtmlForm) {
%>
	</form>
<% } %>
</td>
</tr>
<%-- Set focus to first field (top-left most). --%>
<script language="javascript">
   firstFieldName = '<%=form.getFirstEditableField().getDataColumnName()%>';
   if (document.edit_form && document.edit_form[firstFieldName]) {
       document.edit_form[firstFieldName].focus();
   }
</script>
        
<%
	// Clear out any errors in form
	form.clearErrors();	
%>


