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
    info="Licenseupdater"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.license.create.LicenseSelectionType,
            net.project.security.domain.UserDomain,
            net.project.security.User,
            net.project.util.Validator,
            net.project.admin.RegistrationBean,
            net.project.license.system.MasterProperties,
            net.project.license.system.PropertyName"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />

<%
    // Figure out trial period
    // This is mentioned
    int trialPeriod = 0;
    if (net.project.license.system.LicenseProperties.getInstance() != null) {
	    trialPeriod = net.project.license.system.LicenseProperties.getInstance().getDefaultTrialLicensePeriodDays();
    } else {
	    throw new net.project.license.LicenseException("License master props not installed in the system.");
	}

    //Determine how we are going to figure out what domain we are in.  This is
    //necessary so we can see if credit cards are supported
    String domainSource = request.getParameter("domainSource");
    UserDomain domain;
    if (!Validator.isBlankOrNull(domainSource) && domainSource.equals("registrationBean")) {
        domain = ((RegistrationBean)session.getAttribute("registration")).getUserDomain();
    } else {
        //Assume we are going to get the domain from the user
        domain = ((User)session.getAttribute("user")).getUserDomain();
    }

    MasterProperties props = MasterProperties.getInstance();
    boolean enteredLicenseKeyEnabled =
        props.get(PropertyName.LICENSE_KEY_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.LICENSE_KEY_ENABLED).getValue()).booleanValue();
    boolean creditCardEnabled =
        props.get(PropertyName.CREDIT_CARD_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.CREDIT_CARD_ENABLED).getValue()).booleanValue();
    boolean chargeCodeEnabled =
        props.get(PropertyName.COST_CENTER_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.COST_CENTER_ENABLED).getValue()).booleanValue();
    boolean trialLicenseEnabled =
        props.get(PropertyName.TRIAL_LICENSE_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.TRIAL_LICENSE_ENABLED).getValue()).booleanValue();
    boolean systemDefaultCostCenterEnabled =
        props.get(PropertyName.SYSTEM_DEFAULT_COST_CENTER_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.SYSTEM_DEFAULT_COST_CENTER_ENABLED).getValue()).booleanValue();
    boolean defaultLicenseKeyEnabled =
        props.get(PropertyName.DEFAULT_LICENSE_KEY_ENABLED) != null && Boolean.valueOf(props.get(PropertyName.DEFAULT_LICENSE_KEY_ENABLED).getValue()).booleanValue();
%>

 <%-- Fields in Black are required --%>
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
        <td colspan="2" class="tableHeader">
    		<display:get name="prm.global.display.requiredfield" />
		</td>
    </tr>
	<tr><td colspan="2">&nbsp;</td></tr>
    <tr>
        <td colspan="2" class="warnText">
            <%-- Display any errors --%>

		<!-- Avinash: empty value  -->
			<%
				if(session.getAttribute("otherLicenseErrors")!=null)
					out.print((String)session.getAttribute("otherLicenseErrors"));
				session.removeAttribute("otherLicenseErrors");
				if(licenseContext.getAllErrorMessages()!=null ||"null".equals(licenseContext.getAllErrorMessages())==false )
					out.print(licenseContext.getAllErrorMessages());
			%>
        </td>
    </tr>

<% if (enteredLicenseKeyEnabled) { %>
<display:if name="prm.global.license.create.enteredlicensekey.isenabled">
	<tr>
        <td class="fieldRequired">
             <input type="radio" name="selectionTypeID" value="<%=LicenseSelectionType.ENTERED_LICENSE_KEY.getID()%>">&nbsp;
                    <display:get name="prm.global.license.create.licensecode" />
        </td>
        <td class="fieldNonRequired">
        <!-- Avinash: empty value  -->
                    <input type="text" name="enteredLicenseKey" size="40" maxlength="100"
                           value='<c:out value="${licenseContext.enteredLicenseKey }"/>'
                           onChange="selectRadio(theForm.selectionTypeID, '<%=LicenseSelectionType.ENTERED_LICENSE_KEY.getID()%>');">
        </td>
    </tr>
	<tr class="tableContent">
		<td colspan="2" class="tableContent">
			<display:get name="prm.global.license.select.key.description"/>
	</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
</display:if>
<% } %>

<% if (creditCardEnabled) { %>
<display:if name="prm.global.license.create.creditcard.isenabled">
<% if (domain.supportsCreditCardPurchases()) { %>
    <tr>
        <td class="fieldRequired">
            <input type="radio" name="selectionTypeID"
                value="<%=LicenseSelectionType.CREDIT_CARD.getID()%>">&nbsp;
            <display:get name="prm.global.license.create.creditcard"/>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr class="tableContent">
        <td colspan="2" class="tableContent">
            <display:get name="prm.global.license.select.creditcard.description"/>
        </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
<% } %>
</display:if>
<% } %>

<% if (trialLicenseEnabled) { %>
<display:if name="prm.global.license.create.trial.isenabled">
    <tr>
        <td class="fieldRequired">
                    <input type="radio" name="selectionTypeID"
                           value="<%=LicenseSelectionType.TRIAL.getID()%>">&nbsp;
                    <display:get name="prm.global.license.create.trial" /> <%=PropertyProvider.get("prm.global.license.create.trial.days", new Object[] {String.valueOf (trialPeriod)})%>
                </td>
        		<td>&nbsp;</td>
            </tr>
			<tr class="tableContent">
				<td colspan="2" class="tableContent">
					<display:get name="prm.global.license.select.trial.description"/>
				</td>
			</tr>
            <tr><td colspan="2">&nbsp;</td></tr>
</display:if>
<% } %>

<% if (chargeCodeEnabled) { %>
<display:if name="prm.global.license.create.ischargecodeenabled">
			<%
				String cost = net.project.license.cost.LicenseCostManager.getInstance().getBaseCostUnitPrice().getValue().toString();
				Object[] parameters = new Object[] {cost, String.valueOf(trialPeriod)};
			%>
			 <tr>
                <td class="fieldRequired">
                    <input type="radio" name="selectionTypeID" value="<%=LicenseSelectionType.CHARGE_CODE.getID()%>">&nbsp;
                    <display:get name="prm.global.license.create.chargecode" />
                </td>
                <td class="fieldNonRequired">
                <!-- Avinash: empty value  -->
                    <input type="text" name="chargeCode" size="40" maxlength="100"
                    value='<c:out value="${licenseContext.chargeCode }"/>'
                           onChange="selectRadio(theForm.selectionTypeID, '<%=LicenseSelectionType.CHARGE_CODE.getID()%>');">
                </td>
            </tr>
            <tr class="tableContent">
				<td colspan="2" class="tableContent">
				<%= net.project.base.property.PropertyProvider.get("prm.global.license.select.chargecode.description", parameters)%>
				</td>
            </tr>
            <tr><td colspan="2">&nbsp;</td></tr>
</display:if>
<% } %>

<% if (systemDefaultCostCenterEnabled) { %>
<display:if name="prm.global.license.create.defaultchargecode.isenabled">
			 <tr>
                <td class="fieldRequired">
                    <input type="radio" name="selectionTypeID" value="<%=LicenseSelectionType.DEFAULT_CHARGE_CODE.getID()%>">&nbsp;
                    <display:get name="prm.global.license.create.defaultchargecode" />
                </td>
        		<td>&nbsp;</td>
            </tr>
            <tr class="tableContent">
				<td colspan="2" class="tableContent">
                    <display:get name="prm.global.license.select.defaultchargecode.description" />
				</td>
            </tr>
</display:if>
<% } %>

<% if (defaultLicenseKeyEnabled) { %>
<display:if name="prm.global.license.create.defaultlicensekey.isenabled">
			 <tr>
                <td class="fieldRequired">
                    <input type="radio" name="selectionTypeID" value="<%=LicenseSelectionType.DEFAULT_LICENSE_KEY.getID()%>">&nbsp;
                    <display:get name="prm.global.license.create.defaultlicensekey" />
                </td>
        		<td>&nbsp;</td>
            </tr>
            <tr class="tableContent">
				<td colspan="2" class="tableContent">
                    <display:get name="prm.global.license.select.defaultlicensekey.description" />
				</td>
            </tr>
</display:if>
<% } %>

</table>