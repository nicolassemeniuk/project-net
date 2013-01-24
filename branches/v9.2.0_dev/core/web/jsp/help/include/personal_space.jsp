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

<a name="personal_space"></a><span class="pageTitle">Personal Space</span><p>

<ul>
<li><a href="#personal_main" class="pageTitle">Personal Space Main Page</a></li>
<li><a href="#calendar_personal" class="pageTitle">Calendar</li>
<li><a href="#assignment" class="pageTitle">Assigments</li>
<li><a href="#document_main" class="pageTitle">Documents</li>
<li><a href="#profile_personal" class="pageTitle">Profile</li>
<li><a href="#channel_manager" class="pageTitle">Channel Manager</li>
</ul>
<jsp:include page="personal_main.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="calendar_personal.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="directory_project_assignment.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="document_main.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="profile_personal.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="channel_manager.jsp" flush="true"/>

