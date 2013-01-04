/* 
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
*/
package net.project.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.admin.ApplicationSpace;
import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;
import net.project.base.property.PropertyProvider;
import net.project.form.assignment.FormAssignment;
import net.project.security.User;
import net.project.space.GenericSpace;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

public class ExternalFormServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Enumeration<String> en = request.getParameterNames();
		System.out.println("\n\nparametri:");
		while(en.hasMoreElements()){
			String parName = en.nextElement();
			System.out.println("name:"+parName+" value:"+request.getParameter(parName));
		}
		System.out.println("\n\n");
		String processing = request.getParameter("processing");
		
		if (processing == null || processing.equals("0")){
			getServletContext().getRequestDispatcher("/form/ExternalFormAdd.jsp").forward(request, response);
		}else{
			 String externalFormId = request.getParameter("externalFormId");
			 String externalFormSpaceId = request.getParameter("extSid");
    		 Form form = new Form(externalFormId);
    		 try{
    			 if (externalFormSpaceId != null && externalFormSpaceId.length() > 0){
    				 Space fSpace = SpaceFactory.constructSpaceFromID(externalFormSpaceId); //new GenericSpace(externalFormSpaceId);
	    			 form.setSpace(fSpace);
    			 }
    			 form.loadByExternalClassId(externalFormSpaceId);

				HttpSession session = request.getSession();
				((LocalSessionProvider) Compatibility.getSessionProvider()).setLocalSession(session);
				User user = new User("1");
				user.setCurrentSpace(new ApplicationSpace("1"));
				if (net.project.security.SessionManager.getUser() == null || net.project.security.SessionManager.getUser().getID() == null) {
					net.project.security.SessionManager.setUser(user);
				}
				session.setAttribute("user", user);
				PropertyProvider.setContextFromRequest(request, getServletContext());
				form.setUser(user);

				form.processHttpPost(request);
				if (form.hasErrors()){
					request.setAttribute("hasErrors", true);
					request.setAttribute("errors", form.getErrors());
					getServletContext().getRequestDispatcher("/eaf?extid="+externalFormId+ (externalFormSpaceId != null && externalFormSpaceId.length() > 0 ? ("&extSid="+externalFormSpaceId) : "" )+"&processing=0").forward(request, response);
				}else{
					form.storeData();
					request.setAttribute("formDataName", form.getData().getName());
					request.setAttribute("formDataCurrentRecord", form.getData().getSeqNum());
					request.setAttribute("formName", form.getName());
					request.setAttribute("externalFormId", externalFormId);
					request.setAttribute("extSid", externalFormSpaceId);
					
					
					if (form.getSupportsAssignment() && !form.isAssignmentFieldHiddenInEaf()) {
						String	id =  form.getData().getID();
						String personid = request.getParameter("assignedUser");
						if (personid != null && personid.trim().length() > 0){
					         //create a brand new assignment
			                FormAssignment formAssignment = new FormAssignment();
			                formAssignment.setObjectID(id);
			                formAssignment.setPersonID(personid);
			                formAssignment.setSpaceID(form.getSpace().getID());
			                String work = request.getParameter("work");
			                String workUnits = request.getParameter("work_units");
			                //if (work != null && work.trim().length() > 0 && workUnits != null  && workUnits.trim().length() > 0){
			                if(work == null || work.trim().length() == 0) {
			                    work = "0";
			                }
			                if(workUnits == null || workUnits.trim().length() == 0) {
			                    workUnits = "4";
			                }			                
			                try {
			                    formAssignment.setWork(TimeQuantity.parse(work, workUnits));
			                } catch (ParseException e) {
			                }
			                
			                //Get number of work days for specified estimated work time
			                TimeQuantity workDays = formAssignment.getWork().convertTo(TimeQuantityUnit.STANDARD_WORK_DAY, 2);
			                BigDecimal workAmountInDays = workDays.getAmount().setScale(0, RoundingMode.UP);
			                //Calculating end date - needed to skip weekends
			                int daysOffset = 1;
			                Calendar cal = Calendar.getInstance();
			                if (workAmountInDays.intValue() > 1){
								do{
									cal.add(Calendar.DATE, 1);
									if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
										workAmountInDays = workAmountInDays.add(BigDecimal.valueOf(1));	
									}
									daysOffset = daysOffset + 1;
								}while (daysOffset < workAmountInDays.intValue());
			                } 
			                
			                cal.setTime(new Date());
			                //1 must be subtracted because 1 work day mean work should be finished the same day when started
			                cal.add(Calendar.DATE, daysOffset-1);
			                	          
			                formAssignment.setPercentAssigned(100);
			                formAssignment.setStartTime(new Date());	
			                formAssignment.setEndTime(cal.getTime());
			                formAssignment.setEstimatedFinish(cal.getTime());
			                //formAssignment.setAssignorName(request.getParameter("email"));
			                //formAssignment.setModifiedBy(request.getParameter("email"));
			                formAssignment.store();
			                //}
						}
					}
					getServletContext().getRequestDispatcher("/form/ExternalFormAddConfirmation.jsp").forward(request, response);
				}
    		 }catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	

}
