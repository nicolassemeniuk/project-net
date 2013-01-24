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

import net.project.hibernate.model.PnSpaceHasPropertySheet;
import net.project.hibernate.model.PnSpaceHasPropertySheetPK;

public interface IPnSpaceHasPropertySheetService {
	
	/**
	 * @param PnSpaceHasPropertySheetId for pnSpaceHasPropertySheet we need to select from database
	 * @return PnSpaceHasPropertySheet bean
	 */
	public PnSpaceHasPropertySheet getPropertySheet(PnSpaceHasPropertySheetPK PnSpaceHasPropertySheetId);
	
	/**
	 * Saves new pnSpaceHasPropertySheet
	 * @param pnSpaceHasPropertySheet object we want to save
	 * @return primary key for saved pnSpaceHasPropertySheet
	 */
	public PnSpaceHasPropertySheetPK saveSpaceHasPropertySheet(PnSpaceHasPropertySheet pnSpaceHasPropertySheet);
	
	/**
	 * Deletes pnSpaceHasPropertySheet from database
	 * @param pnSpaceHasPropertySheet object we want to delete
	 */
	public void deleteSpaceHasPropertySheet(PnSpaceHasPropertySheet pnSpaceHasPropertySheet);
	
	/**
	 * Updates pnSpaceHasPropertySheet
	 * @param pnSpaceHasPropertySheet object we want to update
	 */
	public void updateSpaceHasPropertySheet(PnSpaceHasPropertySheet pnSpaceHasPropertySheet);


}
