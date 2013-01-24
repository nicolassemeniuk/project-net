<%@page import="java.io.IOException"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "progress.jsp" );
String sourcePath = webInstallerHelper.getRealTomcatPath()+ "install/pnet_"+ webInstallerHelper.getDatabaseName() + "_db_build.log";
int lineNumber = 0;
String strLine = "";
try {
		BufferedReader rdr = new BufferedReader(new FileReader(sourcePath));
		while (rdr.ready()) {
			strLine += rdr.readLine() + "<br/>";
			lineNumber++;
		}
} catch (IOException e) {
		System.out.println("error in reading file: " + e.getMessage());
}
webInstallerHelper.log( lineNumber + "|" + strLine);
out.println( lineNumber + "|" + strLine);
%>