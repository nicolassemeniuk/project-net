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

 package net.project.history;


/**
    History item to be displayed in the navigation history.

    @author Ed Tice Bluetone
    @author Roger Bly
*/
public class HistoryItem implements java.io.Serializable
{
  private String m_strDisplayName;
  private String m_strJspName;
  private String m_strQueryString;
  private boolean m_bActive = true;


  /** set the name to be displayed for this item in the history list */
  public void setDisplayName(String strDisplayName)
  {
    m_strDisplayName = strDisplayName;
  }


  /** set the JSP/Servlet name to link to for this history item */
  public void setJspName(String strJspName)
  {
    m_strJspName = strJspName;
  }

  
  /** 
    set the JSP/Servlet query string for this history item. 
    For example:  ?id=1234
  */
  public void setQueryString(String strQueryString)
  {
    m_strQueryString = strQueryString;
  }

  public void setActive(boolean active)
  {
    m_bActive = active;
  }

  public boolean isActive()
  {
    return m_bActive;
  }


  /** get the display name for this history item */
  public String getDisplayName()
  {
    return m_strDisplayName;
  }


  /**
    Get the target URL of this history item.
    The JSP base URL and the query string will be return concatenated.
  */
  public String getTargetUrl()
  {
    if (m_strJspName == null)
    {
      m_strJspName = "";
    }

    if (m_strQueryString == null)
    {
      m_strQueryString = "";
    }

    if (m_strQueryString.equals(""))
      return m_strJspName;

    return m_strJspName+"?"+m_strQueryString;
  }

}