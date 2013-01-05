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
package net.project.hibernate.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnPersonAllocationDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnPersonAllocationService;
import net.project.hibernate.service.IUtilService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnPersonAllocationService")
public class PnPersonAllocationServiceImpl implements IPnPersonAllocationService{

	/**
	 * PnPersonAllocation data access object
	 */
	@Autowired
	private IPnPersonAllocationDAO pnPersonAllocationDAO;	
		
	@Autowired
	private IUtilService utilService;
	
	@Autowired
	private IPnObjectService pnObjectService;	

	/**
	 * @param utilService The utilService to set.
	 */
	public void setUtilService(IUtilService utilService) {
		this.utilService = utilService;
	}	
		
	/**
	 * @param pnPersonAllocationDAO The pnPersonAllocationDAO to set.
	 */
	public void setPnPersonAllocationDAO(IPnPersonAllocationDAO pnPersonAllocationDAO) {
		this.pnPersonAllocationDAO = pnPersonAllocationDAO;
	}

	/**
	 * @param pnObjectService The pnObjectService to set.
	 */
	public void setPnObjectService(IPnObjectService pnObjectService) {
		this.pnObjectService = pnObjectService;
	}

	public PnPersonAllocationServiceImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonAllocationService#getResourceAllocationEntryByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportUserProjects> getResourceAllocationEntryByPerson(Integer businesId, Integer personId, Date startDate, Date endDate) {
		List daoResult = pnPersonAllocationDAO.getResourceAllocationEntryByPerson(businesId, personId, startDate, endDate) ;
		
		Calendar cal = GregorianCalendar.getInstance();
		List<ReportUserProjects> projects = new ArrayList<ReportUserProjects>();
		ReportUserProjects project = null;
		Integer projectId = 0;
		Iterator iter = daoResult.iterator();
		while (iter.hasNext()){
			Object[] row = (Object[]) iter.next();
			if (projectId.intValue() != ((Integer) row[0]).intValue()) {
				projectId = (Integer) row[0];
				project = new ReportUserProjects(projectId, (String)row[1]);
				project.setMonthList(utilService.getMonthsBetween(startDate, endDate));
				projects.add(project);
			}
			cal.setTime((Date) row[4]);
			for (ReportMonth month : project.getMonthList()){
				if(month.getMonthInYear().intValue() == cal.get(Calendar.MONTH) && month.getYear().intValue() == cal.get(Calendar.YEAR)){
					month.setAllocationId((Integer)row[2]);
					month.setAllocation((BigDecimal)row[3]);
				}
			}
		}		
		return projects;		
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonAllocationService#getResourceAllocationEntryByProject(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportProjectUsers> getResourceAllocationEntryByProject(Integer businesId, Integer projectId, Date startDate, Date endDate) {
		List daoResult = pnPersonAllocationDAO.getResourceAllocationEntryByProject(businesId, projectId, startDate, endDate);
		
		Calendar cal = GregorianCalendar.getInstance();
		List<ReportProjectUsers> users = new ArrayList<ReportProjectUsers>();
		ReportProjectUsers user = null;
		Integer userId = 0;
		Iterator iter = daoResult.iterator();
		while (iter.hasNext()){
			Object[] row = (Object[]) iter.next();			
			if (userId.intValue() != ((Integer) row[0]).intValue()) {
				userId = (Integer) row[0];				
				user = new ReportProjectUsers(userId, (String)row[1], (String)row[2]);
				user.setMonthList(utilService.getMonthsBetween(startDate, endDate));
				users.add(user);
			}
			cal.setTime((Date)row[5]);
			for (ReportMonth month : user.getMonthList()){
				if (month.getMonthInYear().intValue() == cal.get(Calendar.MONTH) && month.getYear().intValue() == cal.get(Calendar.YEAR)){
					month.setAllocationId((Integer)row[3]);
					month.setAllocation((BigDecimal)row[4]);
				}
			}	
		}
		return users;		
	}

	private List<ReportUser> transformUserList(Date startDate, Date endDate, List userList){
		List<ReportUser> users = new ArrayList<ReportUser>();		 
		Calendar cal = GregorianCalendar.getInstance();
		
		Integer userId = 0;
		ReportUser user = new ReportUser();
		Iterator iter = userList.iterator();
		while (iter.hasNext()) {
			Object[] row = (Object[]) iter.next();

			if (userId.intValue() != ((Integer) row[0]).intValue()) {
				userId = (Integer) row[0];
				user = new ReportUser(userId, (String) row[1], (String) row[2]);
				users.add(user);
			}

			Integer projectId = (Integer) row[3];
			String projectName = (String) row[4];			
			BigDecimal hoursAllocated = (BigDecimal) row[6];
			cal.setTime((Date) row[7]);
			
			ReportUserProjects userProject = null;	
			boolean found = false;
			for (ReportUserProjects project : user.getProjektList()) {
				if (project.getProjectId().intValue() == projectId.intValue()) {
					found = true;
					userProject = project;
				}
			}
			
			if (!found) {						
				userProject = new ReportUserProjects();
				userProject.setProjectId(projectId);
				userProject.setProjectName(projectName);
				userProject.setMonthList(utilService.getMonthsBetween(startDate, endDate));
				user.getProjektList().add(userProject);				
			}
			for (ReportMonth month : userProject.getMonthList()){
				if (month.getMonthInYear().intValue() == cal.get(Calendar.MONTH) && month.getYear().intValue() == cal.get(Calendar.YEAR) ){
					month.setAllocation(hoursAllocated);
				}
			}			
		}
		
		return users;
	}

	public List<ReportUser> getResourceAllocationSumary (Integer businesId, Date startDate, Date endDate){
		List daoResult = pnPersonAllocationDAO.getResourceAllocationSumary(businesId, startDate, endDate);		
		List<ReportUser> users = transformUserList(startDate, endDate, daoResult);					
		return users;
	}

	
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonAllocationService#getResourceAllocationSumary(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportUser> getResourceAllocationSumary(Integer resourceId, Integer businesId, Date startDate, Date endDate) {
		List daoResult = pnPersonAllocationDAO.getResourceAllocationSumary(resourceId, businesId, startDate, endDate);		
		List<ReportUser> users = transformUserList(startDate, endDate, daoResult);					
		return users;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonAllocationService#saveResourceAllocations()
	 */
	public void saveResourceAllocations(List<PnPersonAllocation> allocations) {
		for (PnPersonAllocation allocation : allocations){
			if (allocation.getComp_id().getAllocationId().intValue() == 0){
				// create object for allocation
				PnObject allocationObject = new PnObject("allocation", new Integer(1), new Date(System.currentTimeMillis()), "A");
				// save allocation object in database
				Integer allocationObjectId = pnObjectService.saveObject(allocationObject);
				allocation.getComp_id().setAllocationId(allocationObjectId);
			}
		}		
		pnPersonAllocationDAO.saveResourceAllocations(allocations);
	}

	public PnPersonAllocation getResourceAllocationDetails(Integer resourceId, Integer projectId, Date startDate, Date endDate) {
		PnPersonAllocation projectsAllocated = pnPersonAllocationDAO.getResourceAllocationDetails(resourceId, projectId, startDate, endDate);
		return projectsAllocated;
	}
	
	
	
}
