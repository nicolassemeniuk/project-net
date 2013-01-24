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

import java.util.Date;
import java.util.List;

import net.project.hibernate.constants.ScheduleConstants;
import net.project.hibernate.model.PnAssignmentWork;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPlan;
import net.project.hibernate.model.PnPlanVersion;
import net.project.hibernate.model.PnPlanVersionPK;
import net.project.hibernate.model.PnSpaceHasPlan;
import net.project.hibernate.model.PnSpaceHasPlanPK;
import net.project.hibernate.service.IBaseService;
import net.project.hibernate.service.IPnAssignmentWorkService;
import net.project.hibernate.service.IPnBaselineService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnPlanService;
import net.project.hibernate.service.IPnPlanVersionService;
import net.project.hibernate.service.IPnSpaceHasPlanService;
import net.project.hibernate.service.IScheduleService;
import net.project.hibernate.service.IUtilService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="scheduleService")
public class ScheduleServiceImpl implements IScheduleService {
    
    private final static Logger LOG = Logger.getLogger(ScheduleServiceImpl.class);
    
    @Autowired
    private IUtilService utilService;
    
    @Autowired
    private IPnBaselineService baselineService;
    
    @Autowired
    private IPnSpaceHasPlanService spaceHasPlanService;
    
    @Autowired
    private IPnPlanVersionService planVersionService;
    
    @Autowired
    private IBaseService baseService;
    
    @Autowired
    private IPnPlanService planService;
    
    @Autowired 
    private IPnObjectService objectService;
    
    @Autowired
    private IPnAssignmentWorkService assignmentWorkService;
    
    public void setUtilService(IUtilService utilService) {
		this.utilService = utilService;
	}

	public void setBaselineService(IPnBaselineService baselineService) {
		this.baselineService = baselineService;
	}

	public void setSpaceHasPlanService(IPnSpaceHasPlanService spaceHasPlanService) {
		this.spaceHasPlanService = spaceHasPlanService;
	}

	public void setPlanVersionService(IPnPlanVersionService planVersionService) {
		this.planVersionService = planVersionService;
	}

	public void setBaseService(IBaseService baseService) {
		this.baseService = baseService;
	}

	public void setPlanService(IPnPlanService planService) {
		this.planService = planService;
	}

	public void setObjectService(IPnObjectService objectService) {
		this.objectService = objectService;
	}

	public void setAssignmentWorkService(IPnAssignmentWorkService assignmentWorkService) {
		this.assignmentWorkService = assignmentWorkService;
	}

	public Integer storePlan(String name, String description, Date startDate, Date endDate, Integer autocalculateTaskEndpoints, Integer defaultCalendarId, String timezoneId, Integer modifiedBy, Integer spaceId, Integer defaultTaskCalcTypeId, Date earliestStartDate, Date earliestFinishDate, Date latestStartDate, Date latestFinishDate, String startConstraintType, Date startConstraintDate, Integer planId) {
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: storePlan");
	}
	
	Integer planVersionId = null;
	try {
	    
	    Date currentDate = new Date(System.currentTimeMillis());
	    
	    // If no task calculation type was specified (expected when creating plans)
	    // Use the default
	    if (defaultTaskCalcTypeId == null) {
		defaultTaskCalcTypeId = ScheduleConstants.DEFAULT_TASK_CALC_TYPE_ID;
	    }
	    
	    if (planId == null) {
		// Plan is new, save it
		planId = baseService.createObject("plan", 1, "A");
		PnPlan plan = new PnPlan();
		plan.setPlanId(planId);
		plan.setPlanName(name);
		plan.setPlanDesc(description);
		plan.setDateStart(startDate);
		plan.setDateEnd(endDate);
		plan.setAutocalculateTaskEndpoints(autocalculateTaskEndpoints);
		plan.setDefaultCalendarId(defaultCalendarId);
		plan.setTimezoneId(timezoneId);
		plan.setBaselineStart(null);
		plan.setBaselineEnd(null);
		plan.setModifiedBy(modifiedBy);
		plan.setModifiedDate(currentDate);
		plan.setDefaultTaskCalcTypeId(defaultTaskCalcTypeId);
		plan.setEarliestStartDate(earliestStartDate);
		plan.setEarliestFinishDate(earliestFinishDate);
		plan.setLatestStartDate(latestStartDate);
		plan.setLatestFinishDate(latestFinishDate);
		plan.setConstraintTypeId(startConstraintType);
		plan.setConstraintDate(startConstraintDate);
		planService.savePlan(plan);
		
		spaceHasPlanService.saveSpaceHasPlan(new PnSpaceHasPlan(new PnSpaceHasPlanPK(spaceId, planId)));
		
		// We always store the initial version
		planVersionId = storePlanVersion(planId);
	    } else {
		// Get the current default baseline
		// TODO I know this fetch looks ugly - it's candidate to refactoring
		List<Object> result = baselineService.getCurrentDefaultBaseline(planId);
		Integer currentBaselineId = null;
		Date baselineStart = null;
		Date baselineEnd = null;
		if (result != null) {
		    currentBaselineId = (Integer) result.get(0);
		    baselineStart = (Date) result.get(1);
		    baselineEnd = (Date) result.get(2);
		}
		
		// Grab information about the schedule before the update
		PnPlan plan = planService.getPlan(planId);
		Date oldDateStart = plan.getDateStart();
		Date oldDateEnd = plan.getDateEnd();
		Integer oldAutocalculateTaskEndpoints = plan.getAutocalculateTaskEndpoints();
		String oldTimezoneId = plan.getTimezoneId();
		Date oldBaselineStart = plan.getBaselineStart();
		Date oldBaselineEnd = plan.getBaselineEnd();
		Integer oldDefaultTaskCalcTypeId = plan.getDefaultTaskCalcTypeId();
		
		// Do the actual update of the plan
		plan.setPlanName(name);
		plan.setPlanDesc(description);
		plan.setDateStart(startDate);
		plan.setDateEnd(endDate);
		plan.setAutocalculateTaskEndpoints(autocalculateTaskEndpoints);
		plan.setDefaultCalendarId(defaultCalendarId);
		plan.setTimezoneId(timezoneId);
		plan.setBaselineStart(baselineStart);
		plan.setBaselineEnd(baselineEnd);
		plan.setModifiedBy(modifiedBy);
		plan.setModifiedDate(currentDate);
		plan.setBaselineId(currentBaselineId);
		plan.setDefaultTaskCalcTypeId(defaultTaskCalcTypeId);
		plan.setEarliestStartDate(earliestStartDate);
		plan.setEarliestFinishDate(earliestFinishDate);
		plan.setLatestStartDate(latestStartDate);
		plan.setLatestFinishDate(latestFinishDate);
		plan.setConstraintTypeId(startConstraintType);
		plan.setConstraintDate(startConstraintDate);
		planService.updatePlan(plan);
		
		// Store a plan version if dates or other properties changed
		if ((!utilService.compareDates(oldDateStart, startDate)) || 
			(!utilService.compareDates(oldDateEnd, endDate)) ||
			(oldAutocalculateTaskEndpoints != autocalculateTaskEndpoints) ||
			(!utilService.compareStrings(oldTimezoneId, timezoneId)) ||
			(!utilService.compareDates(oldBaselineStart, baselineStart)) ||
			(!utilService.compareDates(oldBaselineEnd, baselineEnd)) ||
			(oldDefaultTaskCalcTypeId != defaultTaskCalcTypeId)) {
		    planVersionId = storePlanVersion(planId);
		}
	    }
	    
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: storePlan");
	    }
	    e.printStackTrace();
	}
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: storePlan");
	}
	
        return planId;
    }
    
    public Integer storePlanVersion(Integer planId) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: storePlanVersion");
	}
	Integer result = null;
	try {
	    result = objectService.generateNewId();
	    PnPlan plan = planService.getPlan(planId);
	    if (plan != null) {
		PnPlanVersion planVersion = new PnPlanVersion();
		planVersion.setComp_id(new PnPlanVersionPK(planId, result));
		planVersion.setPlanName(plan.getPlanName());
		planVersion.setPlanDesc(plan.getPlanDesc());
		planVersion.setDateStart(plan.getDateStart());
		planVersion.setDateEnd(plan.getDateEnd());
		planVersion.setAutocalculateTaskEndpoints(plan.getAutocalculateTaskEndpoints());
		planVersion.setOverallocationWarning(plan.getOverallocationWarning());
		planVersion.setDefaultCalendarId(plan.getDefaultCalendarId());
		planVersion.setTimezoneId(plan.getTimezoneId());
		planVersion.setBaselineStart(plan.getBaselineStart());
		planVersion.setBaselineEnd(plan.getBaselineEnd());
		planVersion.setModifiedDate(plan.getModifiedDate());
		planVersion.setModifiedBy(plan.getModifiedBy());
		planVersion.setBaselineId(plan.getBaselineId());
		planVersion.setDefaultTaskCalcTypeId(plan.getDefaultTaskCalcTypeId());
		planVersionService.savePlanVersion(planVersion);
	    }
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: storePlanVersion");
	    }
	    e.printStackTrace();
	}
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: storePlanVersion");
	}
	return result;
    }
	
	public Integer storeAssignmentWork(PnAssignmentWork assignmentWork) {
		Integer assignmentWorkId = null;
		try {
			assignmentWorkId = objectService.saveObject(new PnObject("assignment_work", assignmentWork.getPersonId(), assignmentWork.getLogDate(), "A"));
			if (LOG.isDebugEnabled()){
				LOG.debug(" Generated new ID for assignemnt work:"+assignmentWorkId);
			}
			assignmentWork.setAssignmentWorkId(assignmentWorkId);
			assignmentWorkService.saveAssignmentWork(assignmentWork);
			if (LOG.isDebugEnabled()){
				LOG.debug(" AssignmentWork saved successfully with ID:"+assignmentWorkId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return assignmentWorkId;
	}
    
}
