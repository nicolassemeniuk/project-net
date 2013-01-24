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
    info="modify gate processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.Gate,
            net.project.util.DateFormat,
            net.project.util.ErrorDescription,
            net.project.base.Module,
            net.project.security.*"
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="gate" class="net.project.process.GateBean" scope="session"/>

<%
    int module = securityProvider.getCheckedModuleID();
    int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();
    if ((module != Module.PROCESS) || (id.length() == 0)) {
        throw new AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
    }

    if ((action == net.project.security.Action.MODIFY)  || (action == net.project.security.Action.DELETE)) {
        gate.setID(id);
        gate.setPhaseID(request.getParameter("phase_id"));
    } else if (action == net.project.security.Action.CREATE) {
        gate.setID(null);
        gate.setPhaseID(id);
    } else {
        throw new AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
    }

    gate.setName(request.getParameter("gatename"));
    gate.setDesc(request.getParameter("gatedesc"));

    try {
        DateFormat formatter = user.getDateFormatter();
        gate.setDate( formatter.parseDateString(request.getParameter("gatedate")));
        //bfd 3251: On Create Gate page accepting the Review Date before the creation date of Project, Process and Phase
        net.project.process.PhaseBean m_phase= new net.project.process.PhaseBean();
    	m_phase.setID(gate.getPhaseID());
    	m_phase.load();
    	java.util.Date phaseDate = formatter.parseDateString(formatter.formatDate(m_phase.getStart()));
    	if(m_phase.getStart()!=null &&((phaseDate).compareTo(gate.getDate())>0))
    			errorReporter.addError(PropertyProvider.get("prm.project.process.viewphase.gate.reviewdate.error.message"));
    	else{
    		net.project.project.ProjectSpaceBean projectSpace=new net.project.project.ProjectSpaceBean();
    	  java.util.Date	creationDate = projectSpace.getCreationDate(user.getCurrentSpace().getID());
    		if(creationDate!=null && (creationDate.compareTo(gate.getDate())>0))
    			errorReporter.addError(PropertyProvider.get("prm.project.process.viewphase.gate.reviewdatelessthanprojectcreationdate.error.message"));
    	}
    } catch (net.project.util.InvalidDateException ide) {
        errorReporter.addError(new ErrorDescription(
            "reviewdate",
            PropertyProvider.get("prm.project.process.modifygate.error.reviewdate.message", new Object[] {ide.getInvalidDateString()})
        ));
    }

    gate.setStatus(request.getParameter("gatestatus"));

    if (errorReporter.errorsFound()) {
        %>
        <jsp:forward page="modifyGate.jsp">
            <jsp:param name="gatedate" value='<%=request.getParameter("gatedate")%>' />
        </jsp:forward>
        <%
    }

    if (action == net.project.security.Action.DELETE) {
        gate.remove();
    } else {
        gate.store();
    }
    response.sendRedirect(SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + Module.PROCESS + "&action=" + Action.VIEW + "&id=" + gate.getPhaseID() + "&action=view");
%>