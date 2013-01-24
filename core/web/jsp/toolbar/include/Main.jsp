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
    contentType="text/javascript; charset=UTF-8"
    info="Top Banner"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
    		net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.util.Version,
            java.util.Enumeration,
            net.project.base.Module,
            net.project.enterprise.EnterpriseSpace,
            net.project.space.ResourcesSpace,
            net.project.view.components.SpaceMainMenu,
            net.project.resource.PersonProperty,
            net.project.resource.PersonPropertyGlobalScope,
            net.project.space.ISpaceTypes,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />

<%   
boolean isPersonalMenuSelected = false;
    //Determine whether or not the application space will be displayed.
    String displayApplicationSpace = String.valueOf(ApplicationSpace.DEFAULT_APPLICATION_SPACE.isUserSpaceMember(user));
boolean isBusinessMenuSelected = false;
boolean isProjectMenuSelected = false;
boolean isResourceMenuSelected = false;

String spaceString = request.getParameter("s");
if(spaceString != null)
	spaceString = spaceString.substring(0, spaceString.indexOf("?") < 0 ? spaceString.length() : spaceString.indexOf("?"));

if("personal".equals(spaceString))
	isPersonalMenuSelected = true;
else if("business".equals(spaceString))
	isBusinessMenuSelected = true;
else if("project".equals(spaceString))
	isProjectMenuSelected = true;
else if("resources".equals(spaceString))
	isResourceMenuSelected = true;

String personalPage = (String) session.getAttribute("prm.global.login");
if(StringUtils.isEmpty(personalPage)) {
	PersonProperty  property = new PersonProperty();
	property.setScope(new PersonPropertyGlobalScope(user));
	String[] properties = property.get("prm.global.login", "startPage");
	if (properties != null && properties.length > 0 && properties[0].equals(ISpaceTypes.PERSONAL_SPACE)){
	    personalPage = SessionManager.getJSPRootURL() + "/personal/Main.jsp?module=" + Module.PERSONAL_SPACE;
	} else {
	    personalPage = SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+Module.PERSONAL_SPACE+"&page=" + SessionManager.getJSPRootURL() + "/assignments/My?module=" + Module.PERSONAL_SPACE;
	}
	session.setAttribute("prm.global.login", personalPage);
}

%>

function main_toolbar_logout() {
	self.location='<%=SessionManager.getJSPRootURL()%>/Logout.jsp';
}

function helpMe() {
	try{
		help();
	}catch(e){
		var helplocation='<%=SessionManager.getJSPRootURL()%>/help/Help.jsp';
		openwin_help(helplocation);
	}

}

// Function to attach events to objects. 
function addEvent(elm, evType, fn, useCapture){
if (elm.addEventListener) {
  elm.addEventListener(evType, fn, useCapture);
  return true;
} else if (elm.attachEvent) {
  var r = elm.attachEvent('on' + evType, fn);
  return r;
} else {
  elm['on' + evType] = fn;
}
}

var JSPRootURL = '<%= SessionManager.getJSPRootURL()%>';

var bodyLoaded = false;
// initializing variable on body load
function initializeBodyLoaded() { 
	bodyLoaded = true; 
}
// handling errors before and after body load
function handleError() { return (!bodyLoaded); }

function Set_Cookie( name, value, expires, path, domain, secure ) 
{
var today = new Date();
today.setTime( today.getTime() );

if ( expires ){
	expires = expires * 1000 * 60 * 60 * 24;
}

var expires_date = new Date( today.getTime() + (expires) );

document.cookie = name + "=" +escape( value ) +
( ( expires ) ? ";expires=" + expires_date.toGMTString() : "" ) + 
( ( path ) ? ";path=" + path : "" ) + 
( ( domain ) ? ";domain=" + domain : "" ) +
( ( secure ) ? ";secure" : "" );

// adding event method
addEvent(window, 'load', initializeBodyLoaded, false);
// attaching method to error event
window.onerror = handleError;
}

function Get_Cookie( check_name ) {
	var a_all_cookies = document.cookie.split( ';' );
	var a_temp_cookie = '';
	var cookie_name = '';
	var cookie_value = '';
	var b_cookie_found = false; 
	
	for ( i = 0; i < a_all_cookies.length; i++ ) {
		a_temp_cookie = a_all_cookies[i].split( '=' );
		cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');
		if ( cookie_name == check_name ){
			b_cookie_found = true;
			if ( a_temp_cookie.length > 1 )	{
				cookie_value = unescape( a_temp_cookie[1].replace(/^\s+|\s+$/g, '') );
			}
			return cookie_value;
			break;
		}
		a_temp_cookie = null;
		cookie_name = '';
	}
	if ( !b_cookie_found ){
		return null;
	}
}	

function Delete_Cookie( name, path, domain ) {
if ( Get_Cookie( name ) ) document.cookie = name + "=" +
( ( path ) ? ";path=" + path : "") +
( ( domain ) ? ";domain=" + domain : "" ) +
";expires=Thu, 01-Jan-1970 00:00:01 GMT";
}

function _setupSpaceModule(){
			// 	name, 	 value, 								expires, path,  domain, secure
Set_Cookie( '_sid', 	 '<%=user.getCurrentSpace().getID()%>', '', 	 '<%=SessionManager.getJSPRootURL()%>/', 	'', 	'' );
Set_Cookie( '_styp', 	 '<%=user.getCurrentSpace().getType()%>', '', 	 '<%=SessionManager.getJSPRootURL()%>/', 	'', 	'' );

<% if(PropertyProvider.getBoolean("prm.global.login.sso.allowSSO")) { %>
// Set for SSO Login only as Login.jsp will not be called to set this cookie at that time
Set_Cookie( 'JSPRootURL', '<%=SessionManager.getJSPRootURL()%>', '', '<%=SessionManager.getJSPRootURL()%>/', '', '');
<% } %>

//Set_Cookie( 'mid', 	 '<%=user.getCurrentSpace().getID()%>', '', 	 '/', 	'', 	'' );	
}

/* used for global search box*/
function globalSearch(searchKey){
	globalSearch(searchKey, null);
}

/* used for global search box*/
function globalSearch(searchKey, otype){
	try { searchKey = searchKey.trim(); }catch(e){}
	//if(searchKey == '') return; // dont allow search with no keyword, keep silent
	
	var searchUrl = '<%=SessionManager.getJSPRootURL()%>/search/SearchController.jsp';
	searchUrl += '?module=<%=Module.PERSONAL_SPACE%>';
	searchUrl += '&mode=performsearch';
	searchUrl += '&KEYWORD=' + escape(searchKey);
	searchUrl += '&TYPE=3';
	searchUrl += '&otype='+(otype != null ? otype : 'all');
	searchUrl += '&SUBMIT=submit';
	searchUrl += '&action=1';
	window.location.href = searchUrl;
}
	
function writeSpaceMenu() {

	if (window.addEventListener){
		window.document.addEventListener('click', _setupSpaceModule, false); 
		window.document.addEventListener('focus', _setupSpaceModule, false);
	} else if (window.attachEvent){
		window.document.attachEvent('onclick', _setupSpaceModule);
		window.document.attachEvent('onfocus', _setupSpaceModule);
	}
	_setupSpaceModule();
	
	var menuString = "<table width='100%'> <tr><td width='170px' valign='middle' align='center' nowrap='nowrap'>";
	menuString += "<div> ";
	menuString += "	<a href='<%=PropertyProvider.get("prm.global.brand.logo.login.href")%>' ><img src='<%=SessionManager.getJSPRootURL()%><%=PropertyProvider.get("prm.global.header.banner.image")%>' alt='<%=PropertyProvider.get("prm.global.brand.name")%>' title='<%=PropertyProvider.get("prm.global.brand.name")%>'/> </a> ";
	menuString += "</div> ";
	
	menuString += "</td> <td valign='top' align='left'>";
	menuString += " <table width='100%' align='right'> <tr align='right'> <td valign='top' id='login' nowrap='nowrap' colspan='3' width='100%'> ";
	
	menuString += "	<div> &nbsp; ";
	<display:if name="@prm.global.header.leftlinkbar.isenabled">
	
	       <display:if name="prm.global.header.leftlinkbar.link1.isenabled" >
	menuString += "			       <display:get name="prm.global.header.leftlinkbar.link1" href="@prm.global.header.leftlinkbar.link1.href" target="_top" cssClass="bannerLink1"/> ";
	       </display:if>                    
	    
		       <display:if name="prm.global.header.leftlinkbar.link2.isenabled" >
	menuString += "			    | <display:get name="prm.global.header.leftlinkbar.link2" href="@prm.global.header.leftlinkbar.link2.href" target="_top" cssClass="bannerLink1" /> ";
		       </display:if> 
	    
		       <display:if name="prm.global.header.leftlinkbar.link3.isenabled" >
	menuString += "			    | <display:get name="prm.global.header.leftlinkbar.link3" href="@prm.global.header.leftlinkbar.link3.href" target="_top" cssClass="bannerLink1" /> ";
		       </display:if>
	
		       <display:if name="prm.global.header.leftlinkbar.link4.isenabled" >
	menuString += "			    | <display:get name="prm.global.header.leftlinkbar.link4" href="@prm.global.header.leftlinkbar.link4.href" target="_top" cssClass="bannerLink1" /> ";
		       </display:if>
	    
   	</display:if> 
	
	<display:if name="prm.global.application.debug.showappversion">
	menuString += "		&nbsp; Version: <%=Version.getInstance().getProductVersionCodename()%>&nbsp; ";
	</display:if>
   <display:if name="prm.global.header.home.isenabled" >
	menuString += "		&nbsp;&nbsp;<display:get name="prm.global.header.home" href="@prm.global.header.home.href" cssClass="@prm.global.header.links.css"/> &nbsp;&nbsp;|  ";
   </display:if> 
	
	menuString += "	<span class='user'><a class='user-online' href='<%=SessionManager.getJSPRootURL()%>/blog/view/<%=user.getID()%>/<%=user.getID()%>/person/<%= net.project.base.Module.PERSONAL_SPACE %>/?module=<%= net.project.base.Module.PERSONAL_SPACE %>' ><c:out value="${user.displayName}"/></a></span>";

	<display:if name="prm.global.header.support.isenabled" >
	menuString += "		&nbsp;<display:get name="prm.global.header.support" href="@prm.global.header.support.href" target='supportWindow' cssClass="@prm.global.header.links.css" /> &nbsp;|  ";
	</display:if>

	<display:if name="prm.global.header.logout.isenabled" >
	menuString += "		&nbsp;<display:get name="prm.global.header.logout" href="@prm.global.header.logout.href"  target='_top' />&nbsp; | ";
	</display:if>

	<display:if name="<%=displayApplicationSpace%>"> 
	<% if(!"admin".equals(spaceString)) { %>
	menuString += "	&nbsp;<display:get name='@prm.personal.nav.applicationspace' href='<%=SessionManager.getJSPRootURL()+"/personal/Main.jsp?module=" + Module.PERSONAL_SPACE + "&page=" + SessionManager.getJSPRootURL() + "/admin/Main.jsp?module="+Module.APPLICATION_SPACE%>'  target='_top' />&nbsp; | ";
	<% } else { %>
	menuString += "	&nbsp;<span class='red'><b>&darr;&nbsp;<display:get cssClass='red' name='@prm.personal.nav.applicationspace' href='<%=SessionManager.getJSPRootURL()+"/personal/Main.jsp?module=" + Module.PERSONAL_SPACE + "&page=" + SessionManager.getJSPRootURL() + "/admin/Main.jsp?module="+Module.APPLICATION_SPACE%>'  target='_top' /> </b></span>&nbsp; | ";
	<% } %>
	</display:if>
		
	<display:if name="prm.global.header.help.isenabled" >
	menuString += "	&nbsp;<display:get name="prm.global.header.help" href="@prm.global.header.help.href" /> ";
	</display:if>
	menuString += " </div> ";
	
	menuString +=" </td></tr>  <tr> <td width='365px'> ";
	menuString += " <table width='365px'> <tr> ";
	
	<display:if name="prm.personal.isenabled">
	menuString += "	<td width='90px' nowrap='nowrap'> <div class='menu-one'> ";
		<% if (isPersonalMenuSelected) { %>
			menuString += "	<div class='menu-one-selected-personal'> ";
		<%}%> 
	menuString += "		<a href='<%=personalPage%>' class='menu-personal'><%=PropertyProvider.get("prm.application.nav.space.personal")%></a> ";
	    <% if (isPersonalMenuSelected) { %>
			menuString += "	</div> ";
		<%}%> 
	menuString += "	</div> </td> ";
	</display:if>
	
	<display:if name="prm.business.isenabled">
	menuString += " <td width='90px' nowrap='nowrap'> <div class='menu-one'> ";
		<% if (isBusinessMenuSelected ) { %>
			menuString += "	<div class='menu-one-selected-business'> ";
		<%}%> 
	menuString += "		<a href='<%=SessionManager.getJSPRootURL()%>/business/BusinessPortfolio.jsp?module=<%=Module.PERSONAL_SPACE%>&portfolio=true' class='menu-business'><%=PropertyProvider.get("prm.application.nav.space.business")%></a> ";
		<% if (isBusinessMenuSelected ) { %>
			menuString += "	</div> ";
		<%}%> 
	menuString += "	</div> </td> ";
	</display:if>
	
	<display:if name="prm.project.isenabled">
	menuString += "	<td width='90px' nowrap='nowrap'> <div class='menu-one'> ";
		<% if (isProjectMenuSelected) { %>
			menuString += "	<div class='menu-one-selected-projects'> ";
		<%}%> 
	menuString += "		<a href='<%=SessionManager.getJSPRootURL()%>/portfolio/Project?module=<%=Module.PERSONAL_SPACE%>&portfolio=true' class='menu-projects'><%=PropertyProvider.get("prm.application.nav.space.projects")%></a> ";
		<% if (isProjectMenuSelected) { %>
			menuString += "	</div> ";
		<%}%> 
	menuString += "	</div> </td> ";
	</display:if>

	<display:if name="prm.enterprise.isenabled">
	menuString += " <td width='90px' nowrap='nowrap'> ";
	<% if (new EnterpriseSpace().isUserSpaceMember(SessionManager.getUser())) { %>
	//menuString += "	<div class='menu-one'> ";
		<% if (user.getCurrentSpace().isTypeOf(net.project.space.Space.ENTERPRISE_SPACE) ) { %>
	//menuString += "		<a href='<%=SessionManager.getJSPRootURL()%>/enterprise/Main.jsp?module=<%=Module.ENTERPRISE_SPACE%>' class='menu-one-selected'><%=PropertyProvider.get("prm.application.nav.space.enterprise")%></a> ";
		<%} else { %> 
	//menuString += "		<a href='<%=SessionManager.getJSPRootURL()%>/enterprise/Main.jsp?module=<%=Module.ENTERPRISE_SPACE%>' class='menu-projects'><%=PropertyProvider.get("prm.application.nav.space.enterprise")%></a> ";
		<%}%>
	//menuString += "	</div> ";
	<% } %>
	menuString += " </td> ";
	</display:if>
	
	<display:if name="prm.resource.isenabled">
	menuString += "	<td width='100px' nowrap='nowrap'> <div class='menu-one'> ";
		<% if (isResourceMenuSelected ) { %>
			menuString += "	<div class='menu-one-selected-resources'> ";
		<%}%> 
	menuString += "	<a href='<%=SessionManager.getJSPRootURL()%>/personal/Main.jsp?module=<%=Module.PERSONAL_SPACE%>&page=<%=SessionManager.getJSPRootURL()%>/resource/management/viewsummary?module=<%=Module.RESOURCES_SPACE%>' class='menu-projects'><%=PropertyProvider.get("prm.application.nav.space.resource")%></a> ";
		<% if (isResourceMenuSelected ) { %>
			menuString += "	</div> ";
		<%}%> 
	menuString += "	</div> </td> ";
	</display:if>
	
	menuString += " </tr> </table>";
	
	menuString += " </td> <td valign='top' align='right'> ";
	menuString += " <table width='100%'>  ";
	menuString += " <tr height='12'> <td> </td> </tr> ";
	menuString += " <tr align='right'> <td id='search' nowrap='nowrap'> ";
	
	menuString += "	<div> ";
	menuString += "		<input type='text' onKeyDown='if(event.keyCode==13) globalSearch(this.value)' id='global.search.keyword' name='global.search.keyword' value='<%=StringUtils.isEmpty(search.getKeyword())? "" : org.apache.commons.lang.StringEscapeUtils.escapeHtml(search.getKeyword())%>' maxlength='30' /> ";
	menuString += "	</div> </td> </tr> ";
	
	menuString += " <tr height='5'> <td> </td> </tr> ";
	menuString += " </table> ";
	menuString += " </td> </tr> </table> </td> </tr></table> ";
	
	var spaceMenuDiv = document.createElement('div');
	spaceMenuDiv.id = 'topframe';
	document.getElementsByTagName('body')[0].appendChild(spaceMenuDiv);		

	document.getElementsByTagName('body')[0].style.background = 'url("<%=SessionManager.getJSPRootURL()%>/images/menu/back.jpg") repeat-x top';
	
	var topframe = document.getElementById('topframe');
	topframe.innerHTML = menuString ;
}
