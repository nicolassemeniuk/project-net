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
package net.project.space;

/*--------------------------------------------------------------------------------------+
|
|   Imports
|
+--------------------------------------------------------------------------------------*/
import java.io.Serializable;

/*=================================================================================**//**
*
* WeatherSite
*
* @author                                                    AdamKlatzkin    02/00
+===============+===============+===============+===============+===============+======*/
public class    WeatherSite
implements      Serializable
{

// *************************************************************
// PUBLIC members
// *************************************************************
public String       m_city                          = "";
public String       m_state                         = "";
public String       m_country                       = "";
public String       m_name                          = "";
// if m_primary is "1" the weather site will be displayed on the
// space home page
public String       m_primary                       = "0";
// postal code is the only thing weather is really base on
public String       m_postalCode                    = "";
// this is a string that better map to a file location.
public String       m_accuweatherFeed               ="";

} // WeatherSite