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

public interface IPnTimelogService {
	
	public java.util.List<net.project.hibernate.model.PnTimelog> findAll ();


	/**
	 * Either save() the given instance, depending upon the value of its identifier property. By default
	 * the instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the
	 * identifier property mapping. 
	 * @param pnTimelog a transient instance containing new or updated state 
	 */
	public Integer save(net.project.hibernate.model.PnTimelog pnTimelog);

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnTimelog a transient instance containing updated state
	 */
	public void update(net.project.hibernate.model.PnTimelog pnTimelog);

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnTimelog the instance to be removed
	 */
	public void delete(net.project.hibernate.model.PnTimelog pnTimelog);
	
	public net.project.hibernate.model.PnTimelog get(java.lang.Integer timelogId);

}
