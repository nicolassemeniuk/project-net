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
package net.project.hibernate.constants;

public class SecurityConstants {

	public static final Integer GROUP_TYPE_USERDEFINED = new Integer(100);

	public static final Integer GROUP_TYPE_SPACEADMIN = new Integer(200);

	public static final Integer GROUP_TYPE_TEAMMEMBER = new Integer(300);

	public static final Integer GROUP_TYPE_PRINCIPAL = new Integer(400);

	public static final Integer GROUP_TYPE_POWERUSER = new Integer(500);

	public static final Integer GROUP_TYPE_EVERYONE = new Integer(600);

	public static final int ALL_ACTIONS = 65535;

}
