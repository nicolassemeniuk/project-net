<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%@page import="net.project.space.Space"%>
<%@page import="net.project.hibernate.service.IPnPersonService"%>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.model.PnPerson"%>
<%@page import="net.project.resource.Person"%>
<%@page import="net.project.hibernate.service.IPnBusinessSpaceService"%>
<%@page import="net.project.hibernate.model.PnBusinessSpace"%>
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="net.project.hibernate.model.PnProjectSpace"%>
<%@page import="net.project.project.ProjectSpace"%>
<%@page import="net.project.hibernate.service.IPnWikiPageService"%>
<%@page import="net.project.hibernate.model.PnWikiPage"%>
<%@page import="net.project.util.Timer"%>
<%@page import="net.project.util.PerformanceQueries"%>
<%@page import="net.project.hibernate.model.PnWeblog"%>
<%@page import="net.project.hibernate.service.IPnWeblogService"%>
<%@page import="net.project.hibernate.service.IPnAssignmentService"%>
<%@page import="net.project.hibernate.service.IPnDocumentService"%>
<%@page import="net.project.hibernate.model.PnDocument"%>
<%@page import="net.project.document.Document"%>
<%@page import="net.project.hibernate.service.IPnWeblogEntryService"%>
<%@page import="net.project.hibernate.model.PnWeblogEntry"%>
<%@page import="net.project.hibernate.service.IPnDirectoryService"%>
<%@page import="net.project.hibernate.service.IPnTaskService"%>
<%@page import="net.project.hibernate.service.IPnNewsService"%>
<%@page import="net.project.hibernate.model.PnTask"%>
<%@page import="net.project.hibernate.service.IPnObjectService"%>
<html>
<head>
<meta http-equiv="expires" content="0">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="pragma" content="no-cache">
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />
<%
    String mySpace = user.getCurrentSpace().getType();
    String spaceName = null;
    String groupTitle = "prm.project.nav.calendar";
    if( mySpace.equals(Space.BUSINESS_SPACE))
        spaceName= "business";
    else if(mySpace.equals(Space.PERSONAL_SPACE)) {
        spaceName= "personal";
    } else
        spaceName = "project";   
%>

<%!
int NO_OF_LEVEL = 20;

Timer t = new Timer();
Timer totalTimer = new Timer();

ServiceFactory serviceFactory = ServiceFactory.getInstance();
IPnPersonService pnPersonService = serviceFactory.getPnPersonService();
IPnBusinessSpaceService pnBusinessSpaceService = serviceFactory.getPnBusinessSpaceService();
IPnProjectSpaceService pnProjectSpaceService = serviceFactory.getPnProjectSpaceService();
IPnWikiPageService pnWikiPageService = serviceFactory.getPnWikiPageService();
IPnWeblogService pnWeblogService = serviceFactory.getPnWeblogService();
IPnAssignmentService assignmentService = serviceFactory.getPnAssignmentService();
IPnDocumentService documentService = serviceFactory.getPnDocumentService();
IPnWeblogEntryService weblogEntryService = serviceFactory.getPnWeblogEntryService();
IPnDirectoryService directoryService = serviceFactory.getPnDirectoryService();
IPnTaskService taskService = serviceFactory.getPnTaskService();
IPnNewsService newsService = serviceFactory.getPnNewsService();
IPnObjectService objectService = serviceFactory.getPnObjectService();
%>

<style type="text/css">
	.border {
		border-bottom:1px solid #FFD973;
		border-top:1px solid #FFD973;		
	}
	.border td{
		border-bottom:1px solid #FFD973;
	}
	.border th{
		border-bottom:1px solid #FFD973;
	}
	
	.rBorder table{
		border-bottom:1px solid #FFD973;
		border-top:1px solid #FFD973;
		border-left:1px solid #FFD973;
		border-right:1px solid #FFD973;
		font-family: Arial, sans-serif;
		font-size: 12px;
	}
	
	.rBorder {
		font-family: Arial, sans-serif;
		font-size: 12px;
	}
	
	.rHeader {
		text-align: center;
		background: #FFD973;
		font-family: Arial, sans-serif;
		font-size: 12px;
	}
	
	.rBorder td{
		text-align: center;
	}
</style>

</head>
<body id="bodyWithFixedAreasSupport" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%totalTimer.reset(); %>
<div id='content' overflow="auto">

<table border="0" cellpadding="0" cellspacing="10" width="10%">
<tr><td width="25%" class="rBorder" valign="top">		
		<div class="rHeader">For Person</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
		<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
		<%
			int personId = 497434;
			for(int index = 1; index <= NO_OF_LEVEL; index++){
		%>
		<tbody>		
		<tr><td align="center"><%=index %></td>
			<td>
				<%	t.reset();
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						PnPerson pnPerson =  pnPersonService.getPerson(personId);						
					}
					out.print(""+t.getTime());
				%>
			</td>
			<td>
				<%
					Person person = new  Person();
					person.setID(""+personId);
					t.reset();		
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						person.load();
					}
					out.print(""+t.getTime());
				%>
			</td>
		</tr>		
		</tbody>
		<%} %>
		</table>		
	</td>
	<td width="25%" class="rBorder">		
		<div class="rHeader">For Business</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
		<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
		<%
			int businessSpaceId = 412323;
			for(int index = 1; index <= NO_OF_LEVEL; index++){
		%>
		<tbody>		
		<tr><td align="center"><%=index %></td>
			<td>
				<%	t.reset();
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						PnBusinessSpace pnBusinessSpace =  pnBusinessSpaceService.getBusinessSpace(businessSpaceId);						
					}
					out.print(""+t.getTime());
				%>
			</td>
			<td>
				<%
					t.reset();		
					for(int iIndex = 0; iIndex < index; iIndex++ ){
						PnBusinessSpace pnBusinessSpace =  PerformanceQueries.getBusinessSpaceById(businessSpaceId);						
					}
					out.print(""+t.getTime());
				%> 
			</td>
		</tr>		
		</tbody>
		<%} %>
		</table></td>
	<td width="25%" class="rBorder">
		<div class="rHeader">For Project</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
		<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
		<%
			int projectspaceId = 477997;
			for(int index = 1; index <= NO_OF_LEVEL; index++){
		%>
		<tbody>		
		<tr><td align="center"><%=index %></td>
			<td>
				<%	t.reset();
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						PnProjectSpace pnProjectSpace =  pnProjectSpaceService.getProjectSpace(projectspaceId);						
					}
					out.print(""+t.getTime());
				%>
			</td>
			<td>
				<%	
					ProjectSpace projectSpace = new ProjectSpace();
					projectSpace.setID(""+projectspaceId);
					t.reset();		
					for(int iIndex = 0; iIndex < index; iIndex++ ){
						projectSpace.load();
					}
					out.print(""+t.getTime());
				%> 
			</td>
		</tr>		
		</tbody>
		<%} %>
		</table></td>
		<td class="rBorder" valign="top">
			<div class="rHeader">For Blog</div>
			<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
			<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
			<%
				int webBlogId = 497434;
				for(int index = 1; index <= NO_OF_LEVEL; index++){
			%>
			<tbody>		
			<tr><td align="center"><%=index %></td>
				<td>
					<%	t.reset();
						for(int iIndex = 0; iIndex < index; iIndex++ ){				
							PnWeblog pnWeblog =  pnWeblogService.get(webBlogId);
						}
						out.print(""+t.getTime());
					%>
				</td>
				<td>
					<%	
						t.reset();		
						for(int iIndex = 0; iIndex < index; iIndex++ ){
							PnWeblog pnWeblog =  PerformanceQueries.getPnWeblogById(webBlogId);
						}
						out.print(""+t.getTime());
					%> 
				</td>
			</tr>		
			</tbody>
			<%} %>
			</table>			
		</td>
		<td width="25%" class="rBorder">
			<div class="rHeader">For WikiPage</div>
			<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
			<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
			<%
				int wikiPageId = 189;
				for(int index = 1; index <= NO_OF_LEVEL; index++){
			%>
			<tbody>		
			<tr><td align="center"><%=index %></td>
				<td>
					<%	t.reset();
						for(int iIndex = 0; iIndex < index; iIndex++ ){				
							PnWikiPage pnWikiPage =  pnWikiPageService.get(wikiPageId);
						}
						out.print(""+t.getTime());
					%>
				</td>
				<td>
					<%	
						t.reset();		
						for(int iIndex = 0; iIndex < index; iIndex++ ){
							PnWikiPage pnWikiPage =  PerformanceQueries.getWikiPage(wikiPageId);
						}
						out.print(""+t.getTime());
					%> 
				</td>
			</tr>		
			</tbody>
			<%} %>
			</table>
		</td>
</tr>
<tr>
		<td class="rBorder">
			<div class="rHeader">For Blog Entry</div>
			<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
			<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
			<%
				int weblogEntryId = 1420399;
				for(int index = 1; index <= NO_OF_LEVEL; index++){
			%>
			<tbody>		
			<tr><td align="center"><%=index %></td>
				<td>
					<%	t.reset();
						for(int iIndex = 0; iIndex < index; iIndex++ ){				
							PnWeblogEntry pnWeblogEntry =  weblogEntryService.getWeblogEntry(weblogEntryId);
						}
						out.print(""+t.getTime());
					%>
				</td>
				<td>
					<%	
						t.reset();		
						for(int iIndex = 0; iIndex < index; iIndex++ ){
							PnWeblogEntry pnWeblogEntry =  PerformanceQueries.getPnWeblogEntry(weblogEntryId);
						}
						out.print(""+t.getTime());
					%> 
				</td>
			</tr>		
			</tbody>
			<%} %>
			</table>
		</td>
	<td class="rBorder">
	<div class="rHeader">For Document</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
		<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
		<%
			int docId = 1049135;			
			for(int index = 1; index <= NO_OF_LEVEL; index++){
		%>
		<tbody>		
		<tr><td align="center"><%=index %></td>
			<td>
				<%	t.reset();
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						PnDocument pnDocument =  documentService.findByPk(docId);
					}
					out.print(""+t.getTime());
				%>
			</td>
			<td>
				<%	
					t.reset();		
					Document document = new Document();
					document.setID(""+docId);
					for(int iIndex = 0; iIndex < index; iIndex++ ){
						document.load();
						
					}
					out.print(""+t.getTime());
				%> 
			</td>
		</tr>		
		</tbody>
		<%} %>
		</table>
	</td>
	<td class="rBorder">
	<div class="rHeader">For Task</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="border">		
		<thead><th align="center">Obj.</th><th>Hibernate(ms)</th><th>JDBC(ms)</th></thead>
		<%
			int taskId = 1314137;
			for(int index = 1; index <= NO_OF_LEVEL; index++){
		%>
		<tbody>		
		<tr><td align="center"><%=index %></td>
			<td>
				<%	t.reset();
					for(int iIndex = 0; iIndex < index; iIndex++ ){				
						PnTask pnTask = taskService.getTaskById(taskId);
					}
					out.print(""+t.getTime());
				%>
			</td>
			<td>
				<%	
					t.reset();					
					for(int iIndex = 0; iIndex < index; iIndex++ ){
						PnTask pnTask = PerformanceQueries.getTask(taskId);
					}
					out.print(""+t.getTime());
				%> 
			</td>
		</tr>		
		</tbody>
		<%} %>
		</table>
	</td>	
</tr>
</table>
</div>
<%out.println("Total Time to load Page : " +totalTimer.getTime()); %>
</body>
</html>