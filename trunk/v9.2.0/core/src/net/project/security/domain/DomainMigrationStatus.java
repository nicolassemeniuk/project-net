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

 /*-------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+-------------------------------------------------------------------*/
package net.project.security.domain;

/**
 * The DomainMigrationStatus provides Migration Status related constants  .
 */
public class DomainMigrationStatus {
   
    public static final int NEVER_STARTED = 10;
    public static final int USER_CANCELLED = 20;
    public static final int MIGRATION_COMPLETED = 30;
    public static final int REMIND_LATER = 40;
    public static final int NOT_SUPPORTED = 50;
    public static final int SKIP_FOR_NOW = 60;
    
}
