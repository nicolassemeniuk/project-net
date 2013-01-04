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
	info="Channel Customization"
	language="java" 
	errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.channel.ChannelManager,
			net.project.channel.Channel,
			net.project.space.Space,
			net.project.security.SessionManager,
            java.util.Iterator,
            net.project.channel.ChannelHelper,
            net.project.channel.FrameType"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="PR_user_properties" class="net.project.resource.PersonProperty" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="channelCustomizer" class="net.project.channel.ChannelCustomizer" scope="page" />
<%
    // Initialize the channel customizer based on the current set of channels (who's names come from the request)
    channelCustomizer.init(request, PR_user_properties);

    String referer = request.getParameter("referer");
    String decodedReferer = java.net.URLDecoder.decode(referer);

	boolean projectCss = false;
	boolean projectListPage = false;
	boolean isScrollViewNeeded = true;
	String scrollingOptionStyle = "";
	if(referer != null && referer.contains("PersonalPortfolio.jsp") || referer != null && referer.contains("portfolio/Project")){
		projectCss = true;
		projectListPage = true;
	}
	if(referer != null && referer.contains("project/Dashboard") 
			|| referer != null && referer.contains("process/Main.jsp")
			|| referer != null && referer.contains("process/ViewPhase.jsp")
			|| referer != null && referer.contains("portfolio/Project")){
		isScrollViewNeeded = false;
		scrollingOptionStyle = "hidden";
	}

%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<% if ( projectCss ) { %>
	<template:getSpaceCSS space="project" />
<% } else {%>
	<template:getSpaceCSS />
<% } %>

<script language="javascript">
var theForm;
var isLoaded = false;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
    theForm = document.forms["main"];
    setupElements();
    isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personalize_page";
	openwin_help(helplocation);
}
function cancel()
   {
   self.document.location = "<%=decodedReferer%>";
   }
function submit()
{
	document.forms[0].submit();
}

function setupElements() {
    for (var i = 0; i < <%=channelCustomizer.getChannelHelpers().size()%>; i++) {
        toggleDisplay(i);
    }
}
function toggleDisplay(id) {
    var checkbox = theForm.elements["channelSeq_" + id];
    if (checkbox && checkbox.checked) {
        toggleOptions(id, true);
    } else {
        toggleOptions(id, false);
    }
}

function toggleOptions(id, isEnable) {
    var frameTypeRadioInline = document.getElementById("frameTypeID_" + id + "_<%=FrameType.INLINE.getID()%>");
    var frameTypeRadioScroll = document.getElementById("frameTypeID_" + id + "_<%=FrameType.SCROLL.getID()%>");

    frameTypeRadioInline.disabled = !isEnable;
    if(frameTypeRadioScroll != null)
 	   frameTypeRadioScroll.disabled = !isEnable;

    toggleFrameSizeID(id, isEnable);
}

function toggleFrameSizeID(id, isEnable) {
    var frameTypeRadio = theForm.elements["frameTypeID_" + id];
    var frameSizeSelect = theForm.elements["frameSizeID_" + id];
    if(typeof(frameSizeSelect) != 'undefined'){
	    if (isEnable && getSelectedValue(frameTypeRadio) == "<%=FrameType.SCROLL.getID()%>") {
    	    frameSizeSelect.disabled = false;
	    } else {
    	    frameSizeSelect.disabled = true;
    	}
    }
}

function changeFrameType(id) {
    toggleFrameSizeID(id, true);
}

</script>

</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.personalizepage.title" projectListPage = "<%=projectListPage %>">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>


<div id='content'>

<div class="neutral-setup-header top"><%=PropertyProvider.get("prm.personal.setup.dashboard.pagetitle")%></div>


<div class="left-floater">
<div class="page-description"><%=PropertyProvider.get("prm.global.personalizepage.choosechannel.message")%></div>

<form name="main" method="POST" action="<session:getJSPRootURL />/channel/EnableChannelsProcessing.jsp?<%=channelCustomizer.formatScopeRequestParameters()%>">
    <input TYPE=hidden name="theAction" value="submit">
<%  if (referer!=null) { %>
    <input type="hidden" name="referer" value="<%= referer %>">
<%  } %>

<%
    int count = 0;
    for (Iterator it = channelCustomizer.getChannelHelpers().iterator(); it.hasNext(); count++) {
        ChannelHelper nextHelperBean = (ChannelHelper) it.next();
        pageContext.setAttribute("nextHelper", nextHelperBean, PageContext.PAGE_SCOPE);
        String checkboxName = "channelSeq_" + count;
        String frameTypeName = "frameTypeID_" + count;
        String frameSizeName = "frameSizeID_" + count;
        if(!isScrollViewNeeded && scrollingOptionStyle.equals("hidden")) { %>
			<input type="hidden" name="<%=frameTypeName%>" value="<%= FrameType.INLINE.getID()%>"> 
<%      }
%>

<div class="setup-item">
<jsp:useBean id="nextHelper" type="net.project.channel.ChannelHelper" scope="page" />

<%-- Checkbox and Channel Name --%>
<input type="hidden" name="name" value='<jsp:getProperty name="nextHelper" property="name" />' />
<input type="hidden" name="title" value='<jsp:getProperty name="nextHelper" property="displayName" />' />
<div class="setup-channel-name"><label for="<%=checkboxName%>"><input type="checkbox" id="<%=checkboxName%>" name="<%=checkboxName%>" value="enabled" onClick="toggleDisplay(<%=count%>)" <jsp:getProperty name="nextHelper" property="displayedCheckedAttribute" />>
<jsp:getProperty name="nextHelper" property="displayName" /></label></div>

<%-- Inline radio option --%>
<input class="radio-button-indent <%=scrollingOptionStyle%>" type="radio" id="<%=frameTypeName%>_<%=FrameType.INLINE.getID()%>" name="<%=frameTypeName%>" value="<%=FrameType.INLINE.getID()%>" onClick="changeFrameType(<%=count%>)" onChange="changeFrameType(<%=count%>)" <%=nextHelper.getFrameTypeCheckedAttribute(FrameType.INLINE)%>>
<label class="form-black <%=scrollingOptionStyle%>" for="<%=frameTypeName%>_<%=FrameType.INLINE.getID()%>"><%=FrameType.INLINE.getDisplayName()%></label>
<%if(isScrollViewNeeded){ %>
<br />
<%-- Scroll radio option and frame size --%>
<input class="radio-button-indent" type="radio" id="<%=frameTypeName%>_<%=FrameType.SCROLL.getID()%>" name="<%=frameTypeName%>" value="<%=FrameType.SCROLL.getID()%>" onClick="changeFrameType(<%=count%>)" onChange="changeFrameType(<%=count%>)" <%=nextHelper.getFrameTypeCheckedAttribute(FrameType.SCROLL)%>>
<label class="form-black" for="<%=frameTypeName%>_<%=FrameType.SCROLL.getID()%>"><%=FrameType.SCROLL.getDisplayName()%></label>
<label class="form-option" for="<%=frameSizeName%>"><display:get name="prm.channel.customize.scrollframesize.label" /></label>
<select id="<%=frameSizeName%>" name="<%=frameSizeName%>" />
<jsp:getProperty name="nextHelper" property="frameSizeOptions" />
</select>
<%} %>
</div>
<%  } %>




<br />

<%-- The old submit code
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>
--%>



</div>

<%-- The submit box --%>
<div class="submit-box">
<input type="submit" value='<%=PropertyProvider.get("prm.channel.personalize.submit.label")%>' />
</div>

</form>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />



</body>
</html>
