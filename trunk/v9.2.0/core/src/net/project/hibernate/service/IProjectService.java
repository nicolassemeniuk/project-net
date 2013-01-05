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
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

public interface IProjectService {

	public List<Integer> createProject(String projectCreator, String subprojectOf, String businessSpaceId, String projectVisibility, String projectNname, String projectDesc,
			String projectStatus, String projectColorCode, String calculationMethod, String percentComplete, Date startDate, Date endDate, String serial, String projectLogoId,
			String defaultCurrencyCode, String sponsor, Integer improvementCodeId, String currentStatusDescription, Integer financialStatColorCodeId,
			Integer financialStatImcodeId, Double budgetedTotalCostValue, String budgetedTotalCostCc, Double currentEstTotalCostValue, String currentEstTotalCostCc,
			Double actualToDateCostValue, String actualToDateCostCc, Integer estimatedRoiCostValue, String estimatedRoiCostCc, String costCenter,
			Integer scheduleStatColorCodeId, Integer scheduleStatImcodeId, Integer resourceStatColorCodeId, Integer resourceStatImcodeId, Integer priorityCodeId,
			Integer riskRatingCodeId, Integer visibilityId, Integer autocalcSchedule, String planName, Integer createShare);

}
