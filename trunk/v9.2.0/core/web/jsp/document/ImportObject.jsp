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
    info="Create New Document"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.document.*,
            net.project.security.User,
        	net.project.security.SessionManager,
        	net.project.space.Space"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="invalidFile" type="java.lang.String" scope="request" class="java.lang.String"/>
<jsp:useBean id="duCommand" type="net.project.document.DocumentUploadCommand" scope="request" class="net.project.document.DocumentUploadCommand"/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
	  int action = net.project.security.Action.CREATE;
	  int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	  
	  String groupTitle = "prm.global.tool.document.name"; 

	  if(Space.PERSONAL_SPACE.equals(user.getCurrentSpace().getType())) {
	     groupTitle = "prm.document.mainpage.personal.title";
	  }
	  
	  String id = docManager.getCurrentContainerID();
	  
	 docManager.setCancelPage((String)docManager.getNavigator().get("TopContainer"));
	  
	  //Avinash:--------------------------------------------------------	  
	  	//although these beans are session beans but in DocumentUpload.java thsese are null.
	  	//so setting this modified object in session again.
		session.setAttribute("docManager",docManager);
	 //Avinash:--------------------------------------------------------	  
	 
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess
				module="<%=module%>"
				action="create"
				objectID = "<%=id%>"
/>


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkUrl.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/standard_prototypes.js" />
<template:import type="javascript" src="/src/document_prototypes.js" />
<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<script language="javascript">
		window.history.forward(-1);
		var theForm;
		var isLoaded = false;
		var errorMsg;
        var isModifyPage = false;

        //Internationalizatized popup messages
        var nameRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.name.error.message")%>";
        var objectSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.objectselection.error.message")%>";
        var fileSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.fileselection.error.message")%>";
        var urlSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.urlselection.error.message")%>";
        var commentErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.comments.error.message")%>";
		var filePathEmpty = "<%=PropertyProvider.get("prm.document.importobject.file.empty")%>";

		var urlSpaceAreNotAllowedErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.spacearenotallowed")%>';
        var urlIncorrectErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.incorrect")%>';
        var urlInvalidCharactersErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.invalidcharacters")%>';
        var urlDomainNameInvalidCharactersErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.domain.name.invalidcharacters")%>';
        var urlDomainNameInvalidErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.domain.name.invalid")%>';
		
    function setup() {
        load_menu('<%=user.getCurrentSpace().getID()%>');
        theForm = self.document.forms[0];
        isLoaded = true;
	}

	function help(){
		var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=document_main&section=create";
		openwin_help(helplocation);
	}

	function populateName(fileName) {
		var fname = fileName.replace(/\\/, "\\");
 	    theForm.file1.value = fname;
		if (trim(theForm.name.value).length == 0){
            if (fileName) {
               lastSlash = fileName.lastIndexOf("\\");
               if (lastSlash < 0) {
                   lastSlash = fileName.lastIndexOf("/");
               }
               if (lastSlash >= 0 && lastSlash < (fileName.length - 1)) {
                   fileName = fileName.substring(lastSlash + 1);
               }
            }
   			theForm.name.value = fileName;
		}
		// uncheck zip flag, whenever the file name is changed
		theForm.zipexpand.checked = false;
	}

	function popName(fileName) {
		return fileName;
	}

	function submit () {
		theAction("submit");
		var uploadSelection = document.getElementById('radioSelected').value;
		if (!checkMaxLength(theForm.description,500,'<%=PropertyProvider.get("prm.document.importobject.description.maxlength")%>')) {
		} else if(!validate()) {
		} else if(uploadSelection == "") {
			errorHandler(theForm.documentType, '<%=PropertyProvider.get("prm.document.importobject.documenttype.unchecked")%>');
		} else if(uploadSelection == "document"){
			if(theForm.file.value == '') {
				errorHandler(theForm.file, filePathEmpty);
			} else {
				theForm.submit();	
			}
		} else {
			if (isURL2(theForm.url.value, urlSpaceAreNotAllowedErrMes, urlIncorrectErrMes, urlInvalidCharactersErrMes, urlDomainNameInvalidCharactersErrMes, urlDomainNameInvalidErrMes)) {
				theForm.submit();		
			}else{
				extAlert(errorTitle, '<%=PropertyProvider.get("prm.document.importobject.url.validation")%>', Ext.MessageBox.ERROR);
			}
		}
   }


    function validate() {
        if (!checkRequiredData()) return false;
        return true;
    }
	
    function checkRequiredData() {
        if (!checkTextbox(theForm.name,'<display:get name="prm.document.importobject.name.required.message"/>')) return false;
        if (!checkMaxLength(theForm.notes,500,'<display:get name="prm.document.importobject.comment.size.error.message"/>')) return false;
        return true;
    }

    function zipChecked() {
		var str=theForm.file1.value;
		var pos = str.lastIndexOf(".");
		var ext = str.substr(pos + 1);

		if(ext != "zip"){
			extAlert(errorTitle, "Please upload a valid ZIP file for extraction", Ext.MessageBox.ERROR);
			theForm.zipexpand.checked = false;
		}
		if (theForm.zipexpand.checked){
			theForm.zipexpand.value = "true";
			theForm.name.disabled = true;
		} else{
			theForm.zipexpand.value = "false";
			theForm.name.disabled = false;
		}	
	}

	function imposeMaxLength(Object, MaxLen) {
		return (Object.value.length <= MaxLen);
	}
</script>
</head>

<body  class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="<%= groupTitle%>">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.document.importobject.page.history") %>'
			              queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/ImportObject.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" />
	<tb:band name="standard" >
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="documentUpload" method="post" action="<%=SessionManager.getJSPRootURL()%>/document/DocumentUpload.htm" enctype="multipart/form-data">

  <input type="hidden" name="theAction">
  <input type="hidden" name="action" value="<%=action%>">
  <input type="hidden" name="module" value="<%=module%>">
  <input type="hidden" name="id" value="<%=id%>">
  <input type="hidden" name="refLink" value='<%=SessionManager.getJSPRootURL()%>/document/Main.jsp?module=<%=net.project.base.Module.DOCUMENT%>'>

<table border="0" cellspacing="0" cellpadding="0" width="97%">
  <tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
    <th class="channelHeader" colspan="4" nowrap><display:get name="prm.document.importobject.channel.fieldsrequired.title" /></th>
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
  </tr>
  <tr>
    <td colspan="6">&nbsp;</td>
  </tr>
  <tr>
  	<td>&nbsp;</td>
	<td nowrap align="left">
	</td>
    <td nowrap colspan="3" class="errorText">
	    <%  
		if (invalidFile != null && invalidFile.equals("true")) { 
			out.write(PropertyProvider.get("prm.document.importobject.invalid.file.path"));
		} %>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.importobject.name.label" /></td>
    <td nowrap colspan="3">
      <input type="text" name="name" size="70" maxlength="80" value="<%=duCommand.getDocName() == null ? "" : duCommand.getDocName()%>"/>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="6">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.importobject.description.label" /></td>
    <td nowrap colspan="3">
      <textarea name="description" cols="60" rows="4"><%=duCommand.getDescription() == null ? "" : duCommand.getDescription()%></textarea>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="6">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired">&nbsp;</td>
    <td nowrap colspan="3">&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired">
      <input type="radio" name="documentType" id="documentType" value="document" onClick="theForm.url.value='';theForm.radioSelected.value='document'"><display:get name="prm.document.importobject.upload.label" /></td>
    <td nowrap colspan="3">
	<%--  OnBlur event was choosen here than the preferred event onChange event because of Netscape compatibility issues
	--%>
      <input type="file" name="file" size="30" maxlength="250" OnChange="populateName(this.value);theForm.documentType[0].checked=true;theForm.radioSelected.value='document'">
      <input type="hidden" name="file1" size="30" maxlength="250" value="popName(this.value);">
      <input type="hidden" id="radioSelected" name="radioSelected" value=""/>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.importobject.zipexpand.label" /></td>
    <td nowrap colspan="3">
      <input:checkbox name="zipexpand" checked="false" onClick="zipChecked();"/>
	  <input type="hidden" name="zipexp" value="popName(theForm.zipexpand.value);" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired">&nbsp;</td>
    <td nowrap colspan="3">&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired">
      <input type="radio" name="documentType" id="documentType" value="bookmark" onClick="theForm.url.value='http://';theForm.radioSelected.value='selected'">
	  <display:get name="prm.document.bookmarkproperties.url.label" /></td>
    <td nowrap colspan="3">
      <input type="text" name="url" size="60" maxlength="240" OnChange="theForm.documentType[1].checked=true;theForm.radioSelected.value='selected'">
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td nowrap align="left" colspan="6">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.importobject.owner.label" /></td>
    <td nowrap colspan="3">
      <select name="authorID">
        <%= DomainListBean.getRosterOptionList( docManager.getSpace(), docManager.getUser().getID() ) %>
      </select>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="6">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.importobject.status.label" /></td>
    <td nowrap colspan="3">
      <select name="statusID" size="1">
        <%= DomainListBean.getDocStatusOptionList() %>
      </select>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <th nowrap align="left">&nbsp;</th>
    <td nowrap colspan="3">&nbsp; </td>
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldNonRequired">
    	<display:get name="prm.document.importobject.comments.label" /></td>
    <td nowrap colspan="3">
		<textarea rows="2" name="notes" cols="50" wrap="hard"><%=duCommand.getNotes() == null ? "" : duCommand.getNotes()%></textarea>    </td>
    <td>&nbsp;</td>
  </tr>

</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
		<tb:band name="action" enableAll="true">
			<tb:button type="submit" />
      </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

 </form>

<template:getSpaceJS />
</body>
</html>

