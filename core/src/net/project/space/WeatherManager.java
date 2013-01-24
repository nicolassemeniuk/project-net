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
import java.sql.SQLException;
import java.util.ArrayList;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

/*=================================================================================**//**
*
* WeatherManager
* Manages weather sites within a space context
*
* @author                                                    AdamKlatzkin    01/00
+===============+===============+===============+===============+===============+======*/
public class    WeatherManager
implements      Serializable
{
// *************************************************************
// PUBLIC members
// *************************************************************
public ArrayList        m_weatherSites                  = null;
public Space            m_space                         = null;

// *************************************************************
// PROTECTED members
// *************************************************************
// Database access bean
protected DBBean        m_db                            = new DBBean();

/*---------------------------------------------------------------------------------**//**
* Constructor
+---------------+---------------+---------------+---------------+---------------+------*/
public WeatherManager
(
)
    {
    }

/*---------------------------------------------------------------------------------**//**
* setSpace
*
* @param space      the space which is the context for the WeatherManager instance
+---------------+---------------+---------------+---------------+---------------+------*/
public void     setSpace
(
Space           space
)
    {
    m_space = space;
    }

/*---------------------------------------------------------------------------------**//**
* getWeatherSites
* Get the list of weather sites for the current space context.
*
* @return ArrayList     WeatherSite objects
+---------------+---------------+---------------+---------------+---------------+------*/
public ArrayList    getWeatherSites
(
)
    {
    WeatherSite site = null;
    String query = null;

    // If the space context is not defined, return null
    if (m_space == null)
        return null;

    // create the query
    query = "SELECT city,state_code,country_code,name,is_primary,postal_code,feed "
          + "FROM pn_space_has_weather, pn_zipcode_feed_lookup "
          + "WHERE pn_zipcode_feed_lookup.zipcode= pn_space_has_weather.postal_code AND space_id = " + m_space.getID();

    try
        {   
        m_db.setQuery(query);
        m_db.executeQuery();
        m_weatherSites = new ArrayList();

        while (m_db.result.next())
            {
            // populate a Weather site object from the result set
            site = new WeatherSite();
            site.m_city = m_db.result.getString("city");
            site.m_state = m_db.result.getString("state_code");
            site.m_country = m_db.result.getString("country_code");
            site.m_name =  m_db.result.getString("name");
            site.m_primary =  m_db.result.getString("is_primary");
            site.m_postalCode =  m_db.result.getString("postal_code");    
    	    site.m_accuweatherFeed =  m_db.result.getString("feed");    
            m_weatherSites.add(site);
            }
        }
    catch (SQLException sqle)
        {
    	Logger.getLogger(WeatherManager.class).error("WeatherManager.getWeatherSites() threw SQLException " + sqle);
	}
    finally 
        {
	m_db.release();
	}

    return m_weatherSites;
    }

/*---------------------------------------------------------------------------------**//**
* size
*
* @return int   number of weather sites within the space context
+---------------+---------------+---------------+---------------+---------------+------*/
public int      size
(
)
    {
    return m_weatherSites.size();
    }

/*---------------------------------------------------------------------------------**//**
* getSite
* get a weather site from the manager
*
* @param  index     the index of the site to access within the WeatherManager
*                   0 <= index < size()
*
* @return WeatherSite   the weather site object
*                       null if the space does not contain any sites
*                       the last weather site if (index > size)
+---------------+---------------+---------------+---------------+---------------+------*/
public WeatherSite  getSite
(
int                 index
)
    {
    if (m_weatherSites == null)
        {
        getWeatherSites();
        }

    if (m_weatherSites.size() == 0)
        {
        return null;
        }
    else if (index >= m_weatherSites.size())
        {
        return (WeatherSite)m_weatherSites.get(m_weatherSites.size() - 1);
        }
    else
        return (WeatherSite)m_weatherSites.get(index);
    }

} // WeatherManager
