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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.util;

/**
 * Provides Error logging constants used when logging exceptions.
 * <p>
 * <b>Note</b>: {@link org.apache.log4j.Priority} should
 * be used instead.
 * </p>
 * The following severities are defined:
 * <PRE>
 * DEBUG = for System.out's and testing. Will not log to database
 * LOW =  error that was not presented to the user and did not affect the user's experience.
 * MEDIUM = error that was not presented to the user and had moderate effect on the user's experience.  User may have recieved unexpected results.
 * HIGH = error presented to the user, could not present the requested information to the user.
 * CRITICAL = system error probably effecting all users (database down, etc.).
 * SECURITY = multiple unsuccessful login attempts, cracker attempts, etc.
 * </PRE>
 */
public class ErrorLogger implements java.io.Serializable {

    public static final String DEBUG = "debug";
    public static final String LOW = "low";
    public static final String MEDIUM = "medium";
    public static final String HIGH = "high";
    public static final String CRITICAL = "critical";
    public static final String SECURITY = "security";

}
