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

import java.io.Serializable;
import java.util.List;

/**
 * Interface that define database methods from the CRUD pattern.
 */
public interface IDAO<OBJECT, PK extends Serializable> {
	/**
	 * Create/insert new record into the database.
	 * @param object Bean (domain model object) that contains values to insert.
	 * @return Primary key of the newly created object.
	 */
	public PK create(OBJECT object);
	
	/**
	 * Return list of objects populated with records from the database.
	 * @return List of domain model objects.
	 */
	public List<OBJECT> findAll();
	
	/**
	 * Find an record identified by primary key and return populated bean.
	 * @param key Primary key of the record to return.
	 * @return Bean populated with data from the record.
	 */
	public OBJECT findByPimaryKey(PK key);
	
	/**
	 * Update an record into the database.
	 * @param object Domain model object with data to update.
	 */
	public void update(OBJECT object);
	
	/**
	 * Delete an record from the database.
	 * @param object Object that represents record to delete.
	 */
	public void delete(OBJECT object);
	
	/**
	 * Create/updates new record into the database.
	 * @param object Bean (domain model object) that contains values to insert.
	 * @return Primary key of the newly created object.
	 */
	public void createOrUpdate(OBJECT object);
}
