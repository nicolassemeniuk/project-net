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

<%@ page import="net.project.security.SessionManager"%><html>
<head>
</head>
<body>
<form action="MainProcessing.jsp">
How many objects would you like to create?<br>
<input type="hidden" name="module" value="150">
<input type="text" name="numberToCreate" value="2000"><br>
<br>
Choose the type of object you'd like to create<br>
<input type="radio" name="objectType" value="task">Task<br>
<input type="submit" value="Create">
</form>
</body>
</html>