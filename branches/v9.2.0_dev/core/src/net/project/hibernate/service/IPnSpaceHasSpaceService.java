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

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.filters.IPnSpaceHasSpaceFilter;

public interface IPnSpaceHasSpaceService {
	public List<PnSpaceHasSpace> findByFilter(IPnSpaceHasSpaceFilter filter);

	/**
	 * Obtains the relationship between a business space and a financial space.
	 * 
	 * @param spaceID
	 *            the id of the business parent space (financial_business).
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getFinancialRelatedSpace(String spaceID);

	/**
	 * Obtains the relationship between a business space and a financial space.
	 * 
	 * @param spaceID
	 *            the id of the financial child space (financial_financial).
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getBusinessRelatedSpace(String spaceID);

	/**
	 * Obtains the Id of the parent Space (superspace).
	 * 
	 * @param spaceID
	 *            the id of the child Space.
	 * @return the Id of the parent Space.
	 */
	public Integer getParentSpaceID(String spaceID);

	/**
	 * Updates a SpaceHasSpace relationship.
	 * 
	 * @param spaceHasSpace
	 *            the relationship to update.
	 */
	public void update(PnSpaceHasSpace spaceHasSpace);

	/**
	 * Obtains the relationship between two financial spaces.
	 * 
	 * @param financialChildSpaceId
	 *            the id of the child financial space.
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getFinancialParentSpaceRelationship(String financialChildSpaceId);

	/**
	 * Obtains the relationships list between a parent financial space and their child's.
	 * 
	 * @param financialParentSpaceId
	 *            the id of the parent financial space.
	 * @return a list of space to space relationship.
	 */
	public ArrayList<PnSpaceHasSpace> getFinancialChildsSpaceRelationships(String financialParentSpaceId);
}
