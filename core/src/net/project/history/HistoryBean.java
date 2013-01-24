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

 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
| =============================================================================
| ======== DEPRECATED CLASS ===================================================
| =  This has been replaced by the history taglib.                            =
| =============================================================================
+----------------------------------------------------------------------*/
package net.project.history;

import net.project.security.SessionManager;

/**
  * Provides clickable navigation history displayed in a Yahoo-style.
  * Output is similar to:<br>
  * Business Space Name (optional) > Project Space Name > Module Name >  Module Page (optional)
  * <br>
  * **** DEPRECATED CLASS ****<br>
  * This has been replcaed by the history taglib
  * @see net.project.taglibs.history.HistoryTag
  * @author Tim Morrow deprecated it on 11/03/2000<br>
  *
  * @author Ed Tice, Bluestone
  * @author Roger Bly
*/
public class HistoryBean implements java.io.Serializable
{

    public final int BUSINESS = 0;
    public final int PROJECT = 1;
    public final int MODULE = 2;
    public final int PAGE = 3;
    public final int CHILDPAGE1 = 4;
    public final int CHILDPAGE2 = 5;

    public final int MAX_ITEMS = 6;

    public static final String strXmlHeader = "<?xml version=\"1.0\">";
    private String m_target=null;
    private HistoryItem[] m_objHistoryItem = new HistoryItem[MAX_ITEMS];


    /** Sole constructor */
    public HistoryBean()
    {
        resetFromLevel(0);
    }

    /** set the target for all URL's */
    public void setURLTarget(String strTarget)
    {
        m_target=strTarget;
    }

    /** set the display name for the business space */
    public void setBusinessDisplayName(String strDisplayName)
    {
        System.out.println("Setting business display name to: "+strDisplayName);
        setDisplayName(BUSINESS, strDisplayName);
    }


    /** set the JSP/Servlet URL for the business space */
    public void setBusinessJspName(String strJspName)
    {
        setJspName(BUSINESS, strJspName);
    }


    /** set the JSP/Servlet query string for the business space */
    public void setBusinessQueryString(String strQueryString)
    {
        setQueryString(BUSINESS, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setBusinessActive(boolean bActive)
    {
        setActive(BUSINESS, bActive);
    }


    /** set the display name for the project space */
    public void setProjectDisplayName(String strDisplayName)
    {
        setDisplayName(PROJECT, strDisplayName);
    }


    /** set the JSP/Servlet URL for the project space */
    public void setProjectJspName(String strJspName)
    {
        setJspName(PROJECT, strJspName);
    }


    /** set the JSP/Servlet query string for the project space */
    public void setProjectQueryString(String strQueryString)
    {
        setQueryString(PROJECT, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setProjectActive(boolean bActive)
    {
        setActive(PROJECT, bActive);
    }

    /** set the display name for the module */
    public void setModuleDisplayName(String strDisplayName)
    {
        setDisplayName(MODULE, strDisplayName);
    }


    /** set the JSP/Servlet URL for the module */
    public void setModuleJspName(String strJspName)
    {
        setJspName(MODULE, strJspName);
    }


    /** set the JSP/Servlet query string for the module */
    public void setModuleQueryString(String strQueryString)
    {
        setQueryString(MODULE, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setModuleActive(boolean bActive)
    {
        setActive(MODULE, bActive);
    }

    /** set the display name for the module page */
    public void setPageDisplayName(String strDisplayName)
    {
        setDisplayName(PAGE, strDisplayName);
    }


    /** set the JSP/Servlet URL for the module page */
    public void setPageJspName(String strJspName)
    {
        setJspName(PAGE, strJspName);
    }


    /** set the JSP/Servlet query string for the module page */
    public void setPageQueryString(String strQueryString)
    {
        setQueryString(PAGE, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setPageActive(boolean bActive)
    {
        setActive(PAGE, bActive);
    }

    /** set the display name for the child page */
    public void setChildPage1DisplayName(String strDisplayName)
    {
        setDisplayName(CHILDPAGE1, strDisplayName);
    }

    /** set the JSP/Servlet URL for the child page */
    public void setChildPage1JspName(String strJspName)
    {
        setJspName(CHILDPAGE1, strJspName);
    }


    /** set the JSP/Servlet query string for the child page */
    public void setChildPage1QueryString(String strQueryString)
    {
        setQueryString(CHILDPAGE1, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setChildPage1Active(boolean bActive)
    {
        setActive(CHILDPAGE1, bActive);
    }

    /** set the display name for the child page */
    public void setChildPage2DisplayName(String strDisplayName)
    {
        setDisplayName(CHILDPAGE2, strDisplayName);
    }


    /** set the JSP/Servlet URL for the child page */
    public void setChildPage2JspName(String strJspName)
    {
        setJspName(CHILDPAGE2, strJspName);
    }


    /** set the JSP/Servlet query string for the child page */
    public void setChildPage2QueryString(String strQueryString)
    {
        setQueryString(CHILDPAGE2, strQueryString);
    }

    /**
      * make link active or inactive
      * @param bActive true means link is active, false means no link
      */
    public void setChildPage2Active(boolean bActive)
    {
        setActive(CHILDPAGE2, bActive);
    }


    /** get the HTML presentation of the navigation history path */
    public String getHtmlRepresentation()
    {
        boolean firstItem = true;
        String strHtml = "<TABLE width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
        strHtml += "<TR><td class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" alt=\"\"></td></TR>";
        strHtml += "<TR><TD>";
        strHtml += "<NOBR>";

        for (int i=0; i<MAX_ITEMS; i++)
        {
            String strDisplayName = m_objHistoryItem[i].getDisplayName();

            if ((strDisplayName != null)  && !strDisplayName.equals(""))
            {
                if ( ! firstItem)
                    strHtml = strHtml + " > ";

                String strTargetUrl = m_objHistoryItem[i].getTargetUrl();
                if (m_objHistoryItem[i].isActive() && strTargetUrl !=null && !strTargetUrl.equals(""))
                {
                    strHtml = strHtml + "<a href=\""+strTargetUrl+"\" class=\"historyText\"";
                    if(m_target!=null && !m_target.equals(""))
                            strHtml=strHtml+" target=\""+m_target+"\"";
                    strHtml=strHtml+">"+strDisplayName+"</a>";
                }
                else
                {
                    strHtml = strHtml+"<span class=\"historyText\">"+strDisplayName+"</span>";
                }
                firstItem = false;
            }
            // display history up to the first empty item
            // business space (first) and the project space (second) may be empty, so deal with that special case.
            else if (i > 1)
            {
                break;
            }
        }
        strHtml = strHtml+"</NOBR>";
        strHtml += "</TD></TR>";
        strHtml += "<TR><td class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/Images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" alt=\"\"></td></TR>";
        strHtml += "</TABLE>";
        return strHtml;
    }

    public String getURL(int level)
    {
        return m_objHistoryItem[level].getTargetUrl();
    }

    /** get the XML representation of the navigation history path. */
    public String getXmlRepresentation()
    {
        String strXml = "<history-item-list>";

        // Business Space is not always present
        String strDisplayName = m_objHistoryItem[BUSINESS].getDisplayName();
        if (strDisplayName != null)
        {
            strXml = strXml + "<history-item>";

            String strTargetUrl = m_objHistoryItem[BUSINESS].getTargetUrl();
            strXml = strXml + strDisplayName;

            strXml = strXml + "</history-item>";
        }

        for (int i=1; i<MAX_ITEMS; i++)
        {
            strDisplayName = m_objHistoryItem[i].getDisplayName();
            if (strDisplayName == null)
            {
                break;
            }

            if (strDisplayName.equals(""))
            {
                break;
            }

            strXml = strXml + "<history-item>";

            String strTargetUrl = m_objHistoryItem[i].getTargetUrl();
            strXml = strXml + strDisplayName;

            strXml = strXml + "</history-item>";
        }

        strXml = strXml + "</history-item-list>";
        return strXml;

    }


    protected void resetFromLevel(int nLevel)
    {
        for (int i=nLevel; i<MAX_ITEMS; i++)
        {
            m_objHistoryItem[i] = new HistoryItem();
        }
    }


    protected void setDisplayName(int nLevel, String strDisplayName)
    {
        m_objHistoryItem[nLevel].setDisplayName(strDisplayName);
        resetFromLevel(nLevel+1);
    }


    protected void setJspName(int nLevel, String strJspName)
    {
        m_objHistoryItem[nLevel].setJspName(strJspName);
        resetFromLevel(nLevel+1);
    }


    protected void setQueryString(int nLevel, String strQueryString)
    {
        m_objHistoryItem[nLevel].setQueryString(strQueryString);
        resetFromLevel(nLevel+1);
    }

    protected void setActive(int nLevel, boolean bActive)
    {
        m_objHistoryItem[nLevel].setActive(bActive);
    }

}