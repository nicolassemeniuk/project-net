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
package net.project.discussion;

/*--------------------------------------------------------------------------------------+
|
|   Imports
|
+--------------------------------------------------------------------------------------*/

/*=================================================================================**//**
* A person who has read a specific post
*
* @author                                                    AdamKlatzkin    01/00
*//*============+===============+===============+===============+===============+======*/
public class PostReader
implements   java.io.Serializable
{
// *************************************************************
// PUBLIC members
// *************************************************************
public String               m_person_name                   = null;
public String               m_discussion_group_id           = null;
public String               m_post_id                       = null;
public String               m_date_read                     = null;

/*---------------------------------------------------------------------------------**//**
* @return String    the post reader's name
*
* @author                                                    AdamKlatzkin    01/00
+---------------+---------------+---------------+---------------+---------------+------*/
public String   getName
(
)
    {
    return m_person_name;
    }

/*---------------------------------------------------------------------------------**//**
* @return String    the date the reader read the post
*
* @author                                                    AdamKlatzkin    01/00
+---------------+---------------+---------------+---------------+---------------+------*/
public String   getDate
(
)
    {
    return m_date_read;
    }

/*---------------------------------------------------------------------------------**//**
* @return String    string representation of the reader
*
* @author                                                    AdamKlatzkin    01/00
+---------------+---------------+---------------+---------------+---------------+------*/
public String toString()
    {
    return("m_person_name: " + m_person_name + "\n" +
           "m_discussion_group_id: " + m_discussion_group_id + "\n" +
           "m_post_id: " +  m_post_id + "\n" +
           "m_date_read: " +  m_date_read + "\n" );
    }

} // PostReader



