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
    info="modify phase processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.Phase,
            net.project.security.SessionManager,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.util.DateFormat,
            net.project.util.ErrorDescription,
            net.project.process.ProgressReportingMethod,
            net.project.project.ProjectSpaceBean,
            net.project.security.Action,
            net.project.util.Validator,
            net.project.util.NumberFormat,
            org.apache.log4j.Logger,
            org.apache.log4j.Priority,
            java.text.ParseException,
            java.util.Date"
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />

<%
    DateFormat formatter = user.getDateFormatter();

    int module = securityProvider.getCheckedModuleID();
    int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();
    if ((module != net.project.base.Module.PROCESS) || (id.length() == 0)) {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
    }

    if (action == Action.DELETE) {
        m_phase.setID(id);
        m_phase.remove();
//Avinash:-------------------------------------------
        request.setAttribute("action",""+Action.VIEW);
        request.setAttribute("module",""+net.project.base.Module.PROCESS);
        net.project.security.ServletSecurityProvider.setAndCheckValues(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher( "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW);
        dispatcher.forward(request,response);
//Avinash:--------------------------------------------
        //response.sendRedirect(SessionManager.getJSPRootURL() + "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW);
    }

    if ((action == Action.MODIFY)) {
        m_phase.setID(id);
        m_phase.setProcessID(request.getParameter("process_id"));
    } else if (action == net.project.security.Action.CREATE) {
        m_phase.setProcessID(id);
    } else {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
    }

    m_phase.setName(request.getParameter("phasename").trim());
    m_phase.setDesc(request.getParameter("phasedesc"));
    m_phase.setStatus(request.getParameter("phasestatus"));
    m_phase.setSequence(request.getParameter("sequence"));
    m_phase.setProgressReportingMethod(request.getParameter("progressReportingMethod"));

    // Validate sequence number.
    // We allow decimal sequence numbers. (DB field is NUMBER)
    if (Validator.isBlankOrNull(request.getParameter("sequence"))) {
            errorReporter.addError(new ErrorDescription(
                "sequence",
                PropertyProvider.get("prm.project.process.modifyphase.sequence.required.message")
            ));
        }
        else {
            try {
                Number number = NumberFormat.getInstance().parseNumber(request.getParameter("sequence"));

                // Sequence must be positive, we allow 0.  User's Locale is used.
                if (number != null && number.floatValue() < 0)
                    errorReporter.addError(new ErrorDescription(
                        "sequence",
                        PropertyProvider.get("prm.project.process.modifyphase.sequence.range.message")
                    ));
            }
            catch (ParseException pe) {
                errorReporter.addError(new ErrorDescription(
                        "sequence",
                        PropertyProvider.get("prm.project.process.modifyphase.sequence.format.message")
                    ));
            }
        }

    // MANUAL PROGRESS
    // Only store start date, finish date and percent complete if the progress method is manual
    if (m_phase.getProgressReportingMethod().equals(ProgressReportingMethod.MANUAL)) {
    //bfd 2119 issue 
		Date start =  null;
		Date end = null;
		
        try {
			start = formatter.parseDateString(request.getParameter("startdate"));
        } catch (net.project.util.InvalidDateException ide) {
            errorReporter.addError(new ErrorDescription(
                "startdate",
                PropertyProvider.get("prm.project.process.modifyphase.error.startdate.message", new Object[] {ide.getInvalidDateString()})
            ));
        }

        try {
			end = formatter.parseDateString(request.getParameter("enddate"));
        } catch (net.project.util.InvalidDateException ide) {
            errorReporter.addError(new ErrorDescription(
                "enddate",
                PropertyProvider.get("prm.project.process.modifyphase.error.enddate.message", new Object[] {ide.getInvalidDateString()})
            ));
        }
        if((start!=null)&&(end!=null)) {
            // First verification: valid start-end date period (start <= end).
        	if(start.compareTo(end)>0) {
        		errorReporter.addError(PropertyProvider.get("prm.project.process.modifyphase.startdatevalidation.message"));
        	} else {
        	    // Second verification: start is not prior than project start.
				ProjectSpaceBean project = new ProjectSpaceBean();
				project.setID(user.getCurrentSpace().getID());
				project.load();
				Date projectStartDate = project.getStartDate();
				if(projectStartDate!=null &&(start.compareTo(projectStartDate)<0))
					errorReporter.addError(PropertyProvider.get("prm.project.process.modifyphase.startdatepriorprojectstart.message"));
				else{
	        		m_phase.setStart( start );
	        		m_phase.setEnd( end );
				}
        	}
        }
        try {
            // Validate percent complete. We allow 0-100% with decimal values.
            if (Validator.isBlankOrNull(request.getParameter("percentcomplete"))) {
                errorReporter.addError(new ErrorDescription(
                        "percentcomplete",
                        PropertyProvider.get("prm.project.process.modifyphase.percentcomplete.required.message")
                ));
            }
            else {
                // make sure the manual percent complete is a valid number.
                // We allow 0-100% with decimal values.  User's Locale is used.

                Number number = NumberFormat.getInstance().parseNumber(request.getParameter("percentcomplete"));

                // percent complete must be between 0 - 100.
                if (number != null && (number.floatValue() < 0) || (number.floatValue() > 100))
                    errorReporter.addError(new ErrorDescription(
                            "percentcomplete",
                            PropertyProvider.get("prm.project.process.modifyphase.percentrange.message")
                    ));
            }
        }
        catch (ParseException pe) {
            errorReporter.addError(new ErrorDescription(
                    "percentcomplete",
                    PropertyProvider.get("prm.project.process.modifyphase.percentcomplete.format.message")
            ));
        }
        try{
//      bfd 2894: Manual entry field being filled with an incorrect value for a particular case
        Number  percent = NumberFormat.getInstance().parseNumber(request.getParameter("percentcomplete"));
        	if(percent!=null)
       		 m_phase.setPercentComplete(""+Math.round(percent.floatValue()));
        	else
        		 errorReporter.addError(new ErrorDescription(
                         "percentcomplete",
                         PropertyProvider.get("prm.project.process.modifyphase.percentrequired.message")
                 ));
        }catch(ParseException ee){
        	errorReporter.addError(new ErrorDescription(
                    "percentcomplete",
                    PropertyProvider.get("prm.project.process.modifyphase.percentnumber.message")
            ));
        }

    }

    if (errorReporter.errorsFound()) {
        %>
        <jsp:forward page="modifyPhase.jsp">
            <jsp:param name="progressReportingMethod" value='<%=m_phase.getProgressReportingMethod().getID()%>'/>
            <jsp:param name="startdate" value='<%=request.getParameter("startdate")%>'/>
            <jsp:param name="enddate" value='<%=request.getParameter("enddate")%>'/>
            <jsp:param name="percentcomplete" value='<%=request.getParameter("percentcomplete")%>'/>
        </jsp:forward>
        <%
    }

    m_phase.store();

    if (action == net.project.security.Action.MODIFY) {
        response.sendRedirect(SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW + "&id=" + m_phase.getID() + "&rl=1");
    } else {
        response.sendRedirect(SessionManager.getJSPRootURL() + "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW);
    }
%>