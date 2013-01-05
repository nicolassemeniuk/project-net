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
    info="Form Designer -- Edit Field" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.ArrayList,
            net.project.base.property.PropertyProvider,
			net.project.form.FieldDomainValue,
            net.project.form.FormDesigner,
            net.project.form.FormElementList,
            net.project.form.FormFieldDesigner,
			net.project.form.property.CalculationFieldPropertySheet,
			net.project.form.property.IPropertySheet,
            net.project.persistence.PersistenceException,
            net.project.security.SessionManager,
			net.project.security.User, 
            net.project.space.Space,
            net.project.base.PnetException,
            net.project.form.FormField"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><%=PropertyProvider.get("prm.form.designer.fieldeditpage.title")%></title>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />
<jsp:useBean id="domainValue" class="net.project.form.FieldDomainValue" scope="session" />
<jsp:useBean id="formElementList" class="net.project.form.FormElementList" scope="request" />

<%-------------------------------------------------------------------------------------------
  --  Setup Form Beans                                                                       
  -----------------------------------------------------------------------------------------%>
<%!
        public IPropertySheet setupPropertySheet(FormFieldDesigner formFieldDesigner, FormDesigner formDesigner, User user, ServletRequest request) throws PersistenceException {

            IPropertySheet fieldPropertySheet = formFieldDesigner.getPropertySheet();

            //Set private member variables that we need in order to load
            fieldPropertySheet.setSpace(formDesigner.getSpace());
            fieldPropertySheet.setUser(user);
            fieldPropertySheet.setForm(formDesigner);

            //Determine if we need to do any math based on parameters passed to this function
            if ((request.getParameter("addOperand") != null) && (request.getParameter("addOperand").equals("true")))
                ((CalculationFieldPropertySheet)fieldPropertySheet).addOperand();
            else if ((request.getParameter("addOperator") != null) && (request.getParameter("addOperator").equals("true")))
                ((CalculationFieldPropertySheet)fieldPropertySheet).addOperator();
            else if ((request.getParameter("opPair") != null) && (request.getParameter("opPair").equals("add")))
                ((CalculationFieldPropertySheet)fieldPropertySheet).addOpPair();
            else if ((request.getParameter("opPair") != null) && (request.getParameter("opPair").equals("remove"))) {
                ((CalculationFieldPropertySheet)fieldPropertySheet).removeOpPair(request.getParameter("orderID"));

                // Bit of a fudge .... The old  fieldPropertySheet object becomes in an inconsistent stage
    			// after removal of operator ... So , creating a new property sheet object from scratch &
    			// re-assigning it everything
    			// --- deepak
				fieldPropertySheet = formFieldDesigner.getPropertySheet();
				fieldPropertySheet.setSpace(formDesigner.getSpace());
	    		fieldPropertySheet.setUser(user);
	    		fieldPropertySheet.setForm(formDesigner);
			}
			
			//Load the field property sheet
            fieldPropertySheet.load();

            return fieldPropertySheet;
        }

        public void addFieldPropertySheetToSession(IPropertySheet fieldPropertySheet, PageContext pageContext) {
            //It is important to remove the field before setting.  If we don't and the fieldPropertySheet variable
            //is of a different type we will get an error.
            pageContext.removeAttribute("fieldPropertySheet", PageContext.SESSION_SCOPE);
            pageContext.setAttribute("fieldPropertySheet", fieldPropertySheet, PageContext.SESSION_SCOPE);
        }
%>
<%
	if (request.getParameter("fieldID") != null && !request.getParameter("fieldID").equals("")) {
        FormField field = formDesigner.getField(request.getParameter("fieldID"));
        if (field == null) {
            throw new PnetException("FormDesigner could not provide field with id " + request.getParameter("fieldID"));
        } else {
            // Re-initialize the field designer with current field
            // This is critical; otherwise, certain properties like "dataColumnExists" aren't set correctly
            formFieldDesigner.setFormField(field);
        }
	}

    //Set private member variables and determine an operand (if available) needed to load the property sheet.
    IPropertySheet fieldPropertySheet = setupPropertySheet(formFieldDesigner, formDesigner, user, request);
	
    //Add the property sheet to the session
    addFieldPropertySheetToSession(fieldPropertySheet, pageContext);

	// Now pass the element for of the correct type to the formFieldDesigner
	formFieldDesigner.setElement(formElementList.getElementForDisplayClass(formFieldDesigner.getElementDisplayClassID()));

%>
<%-------------------------------------------------------------------------------------------
  --  Apply CCS Stylesheets                                                                  
  -----------------------------------------------------------------------------------------%>
<template:getSpaceCSS/>
<security:verifyAccess objectID='<%=formDesigner.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/>
<template:import type="javascript" src="/src/forms.js"/>
<template:import type="javascript" src="/src/standard_prototypes.js"/>
<template:import type="javascript" src="/src/document_prototypes.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>'; 
	var addPair = false;   
	window.history.forward(-1);
	
	
// Is letter key
function isNumberKey(evt) {
     var charCode = (evt.which) ? evt.which : evt.keyCode;
     if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
     } else {
        return true;
     }
  }
  	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { self.document.location = JSPRootURL + "/form/designer/FieldsManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>"; }
function reset()  { theForm.reset(); }

function submit() {
	theAction("submit");
    submitForm();
}

function submitForm() {
    if (!validateFields()) return;
    if(!validateHiddenField()) return;
    if(!validateMaxColumnSize()) return;
    theForm.submit();
}

var domain_popup = null;

function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_field_edit";
    openwin_help(helplocation);
}

function create() {
	theAction("create");
    submitForm();
}

function changeSheet() {
	theAction("change");
    // We don't validate here because we're not storing
    theForm.submit();
}

// Is letter key
function isNumberKey(evt) {
     var charCode = (evt.which) ? evt.which : evt.keyCode;
     if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
     } else {
        return true;
     }
  }


function addopPair1() {
    theAction("addOperand");
    submitForm();
}

function addopPair2() {
    theAction("addOperator");
    submitForm();
}

function addopPair() {
    theAction("addOpPair");
    submitForm();
}

function removeopPair() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
    	theAction("removeOpPair");
        submitForm();
	}
}

function removeMenuChoice() {
    theAction("remove");
    submitForm();
}

function promoteMenuChoice(fieldDomainID) {
    theForm.fieldDomainValueID.value = fieldDomainID;
    theAction("promote");
    submitForm();
}

function demoteMenuChoice(fieldDomainID) {
    theForm.fieldDomainValueID.value = fieldDomainID;
    theAction("demote");
    submitForm();
}

function alphabetizeDomainValues() {
    theAction("alphabetizeDomainValues");
    submitForm();
}


var fieldNames = new Array(<%=formFieldDesigner.getPropertySheet().getDesignerFieldCount()%>);
var dataColmnSize = <%=formFieldDesigner.getDataColumnSize()%>;

function validateInteger(field, error) {
	var objRegExp  = /(^-?\d\d*$)/;
	if(!objRegExp.test(field.value)) {
		extAlert(errorTitle, error , Ext.MessageBox.ERROR);
		return false;
	} 
	return true;
}

function validateMinIntegerValue(field, minValue, error){
	var intValue = parseInt(field.value);
	  if (intValue < minValue){
	 		extAlert(errorTitle, error, Ext.MessageBox.ERROR);
			return false;	  
	  }
	  return true;  		
}

function validateFields() {
	if(checkRequiredFields(fieldNames, "", " is required")) {
		var form = document.getElementById("formToCheck");
		if(!form.row_num || (validateInteger(form.row_num, "<%=PropertyProvider.get("prm.form.designer.fieldedit.row.message")%>") && validateMinIntegerValue(form.row_num,1, "<%=PropertyProvider.get("prm.form.designer.fieldedit.row.message")%>"))) {
			if(!form.size || (validateInteger(form.size, "<%=PropertyProvider.get("prm.form.designer.fieldedit.length.message")%>") && validateMinIntegerValue(form.size,1 , "<%=PropertyProvider.get("prm.form.designer.fieldedit.length.message")%>"))) {
				if(!form.data_column_size || ( validateInteger(form.data_column_size, "<%=PropertyProvider.get("prm.form.designer.fieldedit.maxlength.message")%>") && validateMinIntegerValue(form.data_column_size,1 , "<%=PropertyProvider.get("prm.form.designer.fieldedit.maxlength.message")%>") && validateNumberFields())) {
					if(!form.data_column_scale || (validateInteger(form.data_column_scale, "<%=PropertyProvider.get("prm.form.designer.fieldedit.numberofdigits.message")%>") && validateMinIntegerValue(form.data_column_scale, 0, "<%=PropertyProvider.get("prm.form.designer.fieldedit.numberofdigits.message")%>")) ) {					
						if(validateSelectionChoiceFields()){
							return true;
						}
					}
				}
			}
		}
	}
    return false;
}

//Method to validate numeric field's total number of digit validation.
function validateNumberFields(){
	if(theForm.theAction.value == 'submit'){
		if(theForm.ElementID && (theForm.ElementID.value == '32' || theForm.ElementID.value == '33')){
			if(theForm.data_column_size && theForm.data_column_size.value > 38){
			    extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.fieldedit.numericfieldslength.message")%>' , Ext.MessageBox.ERROR);
			 	return false;
			}
		}
  	}
	return true;
}

function validateSelectionChoiceFields(){
	if(theForm.theAction.value == 'submit' || theForm.theAction.value == 'alphabetizeDomainValues'){
		if(theForm.ElementID && theForm.ElementID.value == '23'){
			if(theForm.selected && theForm.selected.length > 0){
				return true;
			} else {
			    extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.fieldedit.menuchoice.message")%>' , Ext.MessageBox.ERROR);
			 	return false;
			}
		}
  	}
	return true;
}

//required field can not be hidden
function validateHiddenField(){
	var form = document.getElementById("formToCheck");
	if(form.hidden_for_eaf && form.is_value_required){
	    if(form.is_value_required.checked && form.hidden_for_eaf.checked){
	 		extAlert(errorTitle, "<%=PropertyProvider.get("prm.form.designer.fieldedit.requiredhidden.message")%>", Ext.MessageBox.ERROR);
			return false;        
	    }
	}
	return true;
}

<%-- bfd 3292 	System throws JS error when click the weblink in forms --%>
function resizeTextArea(field, inc) {
	eval('theForm.' + field + '.rows +=' + inc);
}
function popupTextArea(field, label) {
	form_popup = window.open('../TextAreaPopup.jsp?module=<%=net.project.base.Module.FORM%>&label='+label, 'form_popup', "height=700,width=800, resizable=yes");
	
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

function setFieldRef(field) {
	if (field == null)
		field = popupField;
		
	eval('form_popup.fieldRef = theForm.' + field);
}

// Method to validate max length while modifying
function validateMaxColumnSize(){
	var formToCheck = document.getElementById("formToCheck");
	if(!formToCheck.data_column_size || dataColmnSize == -1 
			|| formToCheck.data_column_size.value <= dataColmnSize) return true;
	if(dataColmnSize != "" && formToCheck.data_column_size.value > dataColmnSize){
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.fieldedit.maxlength.change.message")%>' , Ext.MessageBox.ERROR);
		formToCheck.data_column_size.value = dataColmnSize;
		return false;
	}
}
</script>

</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport"  >
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-------------------------------------------------------------------------------------------
  --  Top Toolbar                                                                            
  -----------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:display />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>
<div id='content'>
<form method="post" id="formToCheck" action="FieldEditProcessing.jsp">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" /><input type="hidden" name="clientTypeID" value="<jsp:getProperty name="formFieldDesigner" property="clientTypeID" />" />
<input type="hidden" name="elementName" value="<jsp:getProperty name="formFieldDesigner" property="elementName" />" />
<input type="hidden" name="elementLabel" value="<jsp:getProperty name="formFieldDesigner" property="elementLabel" />" />
<input type="hidden" name="datatype" value="<jsp:getProperty name="formFieldDesigner" property="datatype" />" />
<input type="hidden" name="fieldID" value="<jsp:getProperty name="formFieldDesigner" property="ID" />" />
<input type="hidden" name="fieldDomainValueID" value="" />

<%
    String fieldInfoTitle;

    //Determine if we have a new field so we can decide what title to display
    if (formFieldDesigner.isNewField())
        fieldInfoTitle=PropertyProvider.get("prm.form.designer.fieldedit.channel.add.title");
    else
        fieldInfoTitle=PropertyProvider.get("prm.form.designer.fieldedit.channel.edit.title");
%>

<channel:channel name="formFieldDesigner" customizable="false">
    <channel:insert name="fieldInfo" title="<%=fieldInfoTitle%>" row="1" column="1"
                    width="100%" minimizable="false" closeable="false"
                    include="include/formFieldDesigner.jsp"/>
    <% if (fieldPropertySheet.hasDomain()) { %>
    <channel:insert name="selectionMenu" title='<%=PropertyProvider.get("prm.form.designer.fieldedit.channel.menucoices.title")%>'
 row="2" column="1"
                    width="100%" minimizable="false" closeable="false"
                    include="include/selectionMenu.jsp">
        <channel:button style="channel" type="remove" label='<%=PropertyProvider.get("prm.form.designer.fieldedit.menuchoices.remove.button.label")%>' href="javascript:removeMenuChoice()"/>
        <channel:button style="channel" type="alphabetize" label='<%=PropertyProvider.get("prm.form.designer.fieldedit.menuchoices.alphabetize.button.label")%>' href="javascript:alphabetizeDomainValues()"/>
    </channel:insert>
    <% } %>
</channel:channel>

<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
