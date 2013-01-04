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

<a name="top"></a>
<a name="businss_space"></a><span class="pageTitle">Business Space</span><p>
<ul>
<li><a href="#business_portfolio" class="pageTitle">Business Listing Page</li>
<li><a href="#business_summary" class="pageTitle">Business Summary</li>
<li><a href="#directory_business" class="pageTitle">Directory</li>
<li><a href="#business_project_list" class="pageTitle">Projects</li>
</ul>

<jsp:include page="business_portfolio.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="business_summary.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="directory_business.jsp" flush="true"/>
</br>
<jsp:include page="../include_outside/return_toc_link.jsp" flush="true"/>
<br>
<jsp:include page="business_project_list.jsp" flush="true"/>


