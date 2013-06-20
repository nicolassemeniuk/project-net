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
package net.project.hibernate.dao;

import java.util.ArrayList;

import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.model.PnSpaceHasSpacePK;

public interface IPnSpaceHasSpaceDAO extends IDAO<PnSpaceHasSpace, PnSpaceHasSpacePK> {

	/**
	 * Obtains the relationship between a business space and a financial space.
	 * The relationship must be active.
	 * 
	 * @param financialSpaceID
	 *            the id of the financial child space (financial_financial).
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getBusinessRelatedToFinancialSpace(Integer financialSpaceID);

	/**
	 * Obtains the relationship between a business space and a financial space.
	 * The relationship must be active.
	 * 
	 * @param businessSpaceID
	 *            the id of the business parent space (financial_business).
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getFinancialRelatedToBusinessSpace(Integer businessSpaceID);

	/**
	 * Obtains the Id of the parent Space (superspace). The relationship must be
	 * active.
	 * 
	 * @param spaceID
	 *            the id of the child Space.
	 * @return the Id of the parent Space.
	 */
	public Integer getParentSpaceID(Integer spaceID);

	/**
	 * Obtains the relationship between two financial spaces. The relationship
	 * must be active.
	 * 
	 * @param financialChildSpaceID
	 *            the id of the child financial space (subspace).
	 * @return a space to space relationship.
	 */
	public PnSpaceHasSpace getFinancialParentSpaceRelationship(Integer financialChildSpaceID);

	/**
	 * Obtains the relationships list between a parent financial space and their child's.
	 * 
	 * @param financialParentSpaceId
	 *            the id of the parent financial space.
	 * @return a list of space to space relationship.
	 */
	public ArrayList<PnSpaceHasSpace> getFinancialChildsSpaceRelationships(Integer financialParentSpaceId);

}
