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
    info="Add Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,

			net.project.security.*, 
			net.project.base.Module,

            net.project.util.*,
            java.util.*,
            net.project.resource.*,
            net.project.base.compatibility.modern.*,
            net.project.base.compatibility.*,
            net.project.admin.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
	 session = ((HttpServletRequest) request).getSession();
	((LocalSessionProvider) Compatibility.getSessionProvider()).setLocalSession(session);

	User user = new User("1");
	user.setCurrentSpace(new ApplicationSpace("1"));	
	if (net.project.security.SessionManager.getUser() == null || net.project.security.SessionManager.getUser().getID() == null) {
		net.project.security.SessionManager.setUser(user);		
	}	
	session.setAttribute("user",user);
	PropertyProvider.setContextFromRequest(request, getServletContext());
	
	RosterBean roster = new RosterBean();

	String externalFormId = request.getParameter("extid");
	String externalFormSpaceId = request.getParameter("extSid");
	
	Form form = new Form(externalFormId);	
	request.setAttribute("id",form.getID());
	try{
		form.loadByExternalClassId(externalFormSpaceId);
	}catch (PersistenceException pex){
		pageContext.forward("ExternalFormError.jsp");
	}
	if (externalFormSpaceId != null && externalFormSpaceId.trim().length() > 0){
		form.setSpace(new ApplicationSpace(externalFormSpaceId));
	}
	if (request.getAttribute("hasErrors") == null || !(Boolean)request.getAttribute("hasErrors")){		
		form.clearData();	
	}else{
		form.setErrors((ValidationErrors)request.getAttribute("errors"));
	}
	
	
    form.setUser(user);
    
    String htmlFormName = "eaf_form";
    
    List availableUnits = new ArrayList();
    availableUnits.add(TimeQuantityUnit.HOUR);
    availableUnits.add(TimeQuantityUnit.DAY);
    availableUnits.add(TimeQuantityUnit.WEEK);
    if (form.getSupportsAssignment() && !form.isAssignmentFieldHiddenInEaf()) {
    	roster.setSpace(form.getSpace());
    	roster.load();
    }
    
    DateFormat dateFormatter = new net.project.util.DateFormat(SessionManager.getUser());
%>
<template:getDoctype />

<%@page import="net.project.persistence.PersistenceException"%><html>
<head>
<title><display:get name="prm.global.application.title"/></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRange.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/forms.js" />
<template:import type="javascript" src="/src/checkEmail.js" />

<script language="javascript">
var theForm;
var isLoaded = false;
var goSubmit = true;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';


function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_edit';
	openwin_help(helplocation);
}

var fieldNames = new Array(<%=form.getFields().size()%>);

function validateFields() {
    return checkRequiredCreatorEmail() && validateEmail() && checkRequiredFields(fieldNames, "", " is required") && goSubmit;
}

function checkRequiredCreatorEmail(){
	var email = document.getElementById("email").value;
	var emailExists = email != null && email != ''
	if (!emailExists) {
		extAlert(errorTitle, "creator email is required" , Ext.MessageBox.ERROR);
	} 
	return emailExists; 
}

function validateEmail(){
	var email = document.getElementById("email");	
	return checkEmail(email, '<%=PropertyProvider.get("prm.form.edit.include.creatoremailaddress.errormsg")%>');			
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id='content'>

<form id="formToCheck" name="<%=htmlFormName%>" method="post" action="eaf?processing=1">
		<input type="hidden" name="theAction">
		<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
		<input type="hidden" name="action" />
		<input type="hidden" name="id" />
		<input type="hidden" name="class_id" value="<%= form.getID() %>" />
		<input type="hidden" name="externalFormId" value="<%= externalFormId %>" />
		<input type="hidden" name="extSid" value="<%= externalFormSpaceId != null ? externalFormSpaceId : "" %>" />
		<input type="hidden" name="timeZoneOffset" value="0" />
		
<tr>
<td colspan="6">
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr bgcolor="#E1E1E1"> 
	<td align="left"> 
	  	<span class=smallHeaderBlack><%=PropertyProvider.get("prm.form.list.pagetitle", new Object[] {form.getName(), form.getAbbreviation()})%>
		</span>
	</td>
	
</tr>
</table>
<%-- Display any errors --%>
<%	
if (request.getAttribute("hasErrors") != null && (Boolean)request.getAttribute("hasErrors")) { %>
		<table width="100%" class="tableContent" border="0">
			<tr>
				<td class="fieldWithError"><%=PropertyProvider.get("prm.form.edit.include.correcterror.message")%></td>
			</tr>
		</table>
<% } %>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr > 
	<td align="left"> 
	  	<span class=smallHeaderBlack><%=PropertyProvider.get("prm.form.eaf.creatoremail.label")%>
		</span>
	</td>
	
</tr>
<tr > 
	<td align="left"> 
		<input:text elementID="email" name="email" size="35" maxLength="100"  value='<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>' />
		<% if (form.hasErrors() && form.getErrorMessage("email") != null) { %> <%= form.getErrorMessage("email") %>  <% } %>		
	</td>
	
</tr>
</table>
<%if (form.getSupportsAssignment() && !form.isAssignmentFieldHiddenInEaf()) { %>
<table width="100%" class="tableContent" border="0">
 
 <%-- Work, Work Complete, % Work Complete --%>
    <tr align="left" valign="middle">
        <td colspan="6" class="rowSep"><display:get name="prm.form.assignment.currentassignment.label"/></td>
    </tr>
    <tr align="left" valign="middle">
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.assigneduser.label"/></th>
        <th nowrap class="tableHeader" width="25%" colspan="1"><display:get name="prm.form.assignment.work.label"/></th>
        <th nowrap class="tableHeader" width="15%" colspan="4"><display:get name="prm.form.assignment.workpercentcomplete.label"/></th>
    </tr>    
    <tr align="left" valign="middle">
        <td nowrap width="15%">
            <select id="assignedUserId" name="assignedUser">
            	<option value=""><display:get name="prm.form.designer.fieldedit.type.option.none.name"/></option>
            	<%=roster.getSelectionList(request.getParameter("assignedUser") != null ? request.getParameter("assignedUser") : "0")%>
            </select>
        </td>
        <td nowrap width="25%">
            <input:text elementID="work" name="work" size="5" maxLength="10" onChange="workChanged();" value='<%= request.getParameter("work") != null ? request.getParameter("work") : ""  %>' />
            <% TimeQuantityUnit workUnits = request.getParameter("work_units") != null ? TimeQuantityUnit.getForID(request.getParameter("work_units")) :  TimeQuantityUnit.HOUR; %>
            <input:select elementID="work_units" name="work_units" onChange="workUnitsChanged();" options="<%=availableUnits%>" defaultSelected="<%=workUnits %>" />
        </td>
        <td nowrap width="15%" >
            <input:text elementID="work_percent_complete" name="work_percent_complete" onChange="workPercentCompleteChanged();" value='<%=""%>' size="4" maxLength="7"/>
        </td>
        <td nowrap width="15%" id="workComplete">
            <input type="hidden" id="work_complete" name="work_complete" value="<%="0" %>" />
            <input type="hidden" id="work_complete_units" name="work_complete_units" value="<%=TimeQuantityUnit.HOUR.getUniqueID()%>" />
            <%= ""%>
        </td>        
        <td nowrap width="15%" id="startDate">
            <%="" %>
        </td>
        <td nowrap width="15%" id="endDate">
            <%="" %>
        </td>
    </tr>

</table>
<br />
<% } %>

<%form.writeHtml(new java.io.PrintWriter(out), false, true);%>

</form>		


<%-- These script functions rely on the correctly loaded form in the session --%>
<script language="javascript">
function setup() {
   load_menu();
   theForm = self.document.forms[0];
   isLoaded = true;
   var workComplete = Ext.DomQuery.selectNode("#work_complete");
   if(workComplete && workComplete.value == '0') {
       var workPercentComplete  = Ext.DomQuery.selectNode("#work_percent_complete");
       if(workPercentComplete)
           workPercentComplete.readOnly = true;
   }
}

function cancel() {
	
}

function submit() {
	theForm.elements["action"].value = "<%=Action.CREATE%>";
    if (validateFields()) {
        theAction("submit");
        theForm.submit();
    }
}


function reset() {
	theForm.reset();
}
</script>

<script language="javascript">
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
var fieldCopyFailed = false;
var form_popup;
var popupField;
var editFormName = '<%=htmlFormName%>';

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
            errorHandler(theForm.work, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
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
        errorHandler(theForm.work, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
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


<%-----------------------------------------------------------------------------------------------------------
--  Bottom Action Bar                                                                            
------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
	<tb:band name="action" >
     	<tb:button type="submit" />		
	</tb:band>
</tb:toolbar>



</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<%-- Set focus to first field (top-left most). --%>
<script language="javascript">
   firstFieldName = '<%=form != null && form.getFirstEditableField() != null ? form.getFirstEditableField().getDataColumnName() : ""%>';
   if (document.eaf_form && document.eaf_form[firstFieldName]) {
       document.eaf_form[firstFieldName].focus();
   }
   
   <%
    if (form != null && form.getFields() != null){

       Iterator itr = form.getFields().iterator();
   	   while(itr != null && itr.hasNext()){   		     
   		   FormField field = (FormField)itr.next();   		   
   		   String defaultFieldValue = "";
   		   if(field != null && field.hasDomain()){
   			 int size =  field.getDomain().getValues() != null ? field.getDomain().getValues().size() : 0;   			 
			 for(int idx=0; idx< size;idx++){
				 if( ((FieldDomainValue)field.getDomain().getValues().get(idx)).isDefault()){
					 defaultFieldValue = ((FieldDomainValue)field.getDomain().getValues().get(idx)).getID();
				 }
			 }
   		   }
   		   if (field.useDefault() && field instanceof DateField){
   			defaultFieldValue = dateFormatter.formatDate(new java.util.Date(), "M/d/yyyy");
   		   }
   		   if (field != null && field.getDataColumnName() != null ){
   %>        
	   fieldName = '<%= field.getDataColumnName()%>';   		
	   if (document.eaf_form && document.eaf_form[fieldName]) {   
       	document.eaf_form[fieldName].value = '<%= request.getParameter(field.getDataColumnName()) != null ? request.getParameter(field.getDataColumnName()).trim() : defaultFieldValue  %>';
       }
	   if (document.eaf_form && document.eaf_form[fieldName+'_hour']) {
		   var currentDate = new Date();
		   var hours = currentDate.getHours();

		   var minutes = currentDate.getMinutes();
		   var ampm =  hours < 12 ? 'AM' : 'PM';
		   if (hours == 0) {
		   	curr_hour = 12;
		   }
		   if (hours > 12) {
			   hours = hours - 12;
		   }

		   var selHours = document.eaf_form[fieldName+'_hour'];
		   var timeZoneOffset = hours - selHours.selectedIndex - 1 ;
		   selHours.selectedIndex = hours > 0 ?  hours - 1 : 11;
		   var selMinutes = document.eaf_form[fieldName+'_minute'];
		   selMinutes.selectedIndex = minutes ;	
		   var selAmPm = document.eaf_form[fieldName+'_ampm'];	

		   if (ampm != selAmPm.options[selAmPm.selectedIndex].value){
			   timeZoneOffset = timeZoneOffset + 12;
		   }

		   selAmPm.selectedIndex = ampm == 'AM' ? 0 : 1; 	

		   var selTimeZoneOffset = document.eaf_form['timeZoneOffset'];
		   selTimeZoneOffset.value = timeZoneOffset;
		   
	   }
   <%		   
   		   }
   	   }
    }
   %>
   
   
</script>
        
<%
	// Clear out any errors in form
	//form.clearErrors();	
%>


<template:getSpaceJS /></body>
</html>
