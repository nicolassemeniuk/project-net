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

<%--
  -- Note: The Project.net Public Licnese (PPL) requires that the powered-by logo
  -- remain at the bottom-center of every page.   This means that the following tokens
  -- must be set as:
  -- prm.global.footer.alignment = center
  -- prm.global.poweredby.isenabled = true
  -- prm.global.footer.copyright.isenabled = true
  -- prm.global.footer.poweredby.logo set to /images/logo/powered_by_pnet150.gif or other license-specified logo.
  --%>
<%@ taglib uri="/WEB-INF/taglibs/displayTags.tld" prefix="footerDisplay"%>

<%-- Footer alignment --%>
<div align='<footerDisplay:get name="@prm.global.footer.alignment" />' >

<%-- Powered-by footer logo image --%>
<footerDisplay:img 
		if="@prm.global.poweredby.isenabled"
		src="@prm.global.footer.poweredby.logo"
		href="@prm.global.footer.poweredby.href" 
/>

<%-- Copyright Footers --%>
<footerDisplay:if name="@prm.global.footer.copyright.newline"></footerDisplay:if>

<footerDisplay:get
           name="@prm.global.footer.copyright"
	       if="@prm.global.footer.copyright.isenabled"
	       href="@prm.global.footer.copyright.href"
	       enableLink="@prm.global.footer.copyright.href.isenabled"
/>

<footerDisplay:if name="@prm.global.footer.copyright.line2.newline"><br></footerDisplay:if>

<footerDisplay:get
           name="@prm.global.footer.copyright.line2"
	       if="@prm.global.footer.copyright.line2.isenabled"
	       href="@prm.global.footer.copyright.line2.href"
	       enableLink="@prm.global.footer.copyright.line2.href.isenabled"
/>

</div>
