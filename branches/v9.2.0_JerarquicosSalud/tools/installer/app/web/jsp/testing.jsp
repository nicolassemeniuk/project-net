<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "testing.jsp" );
String errorType = ""; 
String mailTo = request.getParameter("userEmail");
String action = request.getParameter("p");

if(action == null) action = "";
if(mailTo == null) mailTo = "";

if(webInstallerHelper.isCompletedConfigurationTest()) {
	RequestDispatcher rd = request.getRequestDispatcher("testconf.jsp");
	rd.forward(request, response);
}else{
	if("m".equals(action)){
		if(!webInstallerHelper.testMailConfiguration(mailTo)){
			errorType = "2";
			request.setAttribute("errorMsg","Error occured while sending email. <br>Might be tomcat not restarted or provided incorrect information <br> Please restart tomcat or provide the correct inforamtion for "
								+" SMTP setup and change Context.xml again");
		}
	}

	if("d".equals(action)){
		if(!webInstallerHelper.testDatabaseConfiguration()){
			errorType = "1";
			request.setAttribute("errorMsg","Error occured while checking database connection. <br>Might be tomcat not restarted or provided incorrect information <br> Please restart tomcat or provide the correct inforamtion for Database "
					+"<br> and change Context.xml file again");
		}
	}
	
	if("ud".equals(action)){
		//update installer.properties file
		webInstallerHelper.writePropertyFile("step.check.db.setup","0"); 
		webInstallerHelper.writePropertyFile("step.check.smtp.setup" , "0");
		webInstallerHelper.writePropertyFile("step.change.context.file", "0");
		webInstallerHelper.writePropertyFile("step.restart", "0");
		webInstallerHelper.writePropertyFile("step.execute.db.script", "0");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	
	}
	else if("um".equals(action)){
		//update installer.properties file
		webInstallerHelper.writePropertyFile("step.check.smtp.setup" , "0");
		webInstallerHelper.writePropertyFile("step.change.context.file", "0");
		webInstallerHelper.writePropertyFile("step.restart", "0");
		request.getRequestDispatcher("index.jsp").forward(request, response);

	}else{
		request.getRequestDispatcher("testconf.jsp?errorType="+errorType).forward(request, response);
	}
}
%>
