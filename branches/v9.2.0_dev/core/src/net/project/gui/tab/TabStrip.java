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
+----------------------------------------------------------------------*/
package net.project.gui.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;

/**
 * Provides definition and presentation of a TabStrip.
 * Used by {@link net.project.taglibs.tab.TabStripTag}.
 *
 * @author Tim Morrow
 * @since Version 2.0
 */
public class TabStrip implements java.io.Serializable, IXMLPersistence {

    /**
     * The Collection of tabs in this tab strip.
     */
    private final Collection tabCollection = new ArrayList();

    /** The width of the tab strip table. */
    private String width = null;

    /** Display all tabs together */
    private Boolean tabPresentation = false;          
    
    /**
     * Creates a new Tab and adds it to this TabStrip's collection.
     * @return a new tab
     */
    public Tab newTab() {
        Tab tab = new Tab();
        this.tabCollection.add(tab);
        return tab;
    }

    /**
     * Return the tab strip presentation.
     * @return the HTML presentation string
     * @throws TabException if there is a problem creating the presentation
     */
    public String getPresentation() throws TabException {
    	
    	if(tabPresentation)
    		return getAlternativePresentation();
    	else
    		return getDefaultPresentation();
    }

    /**
     * Draw tab strip with default presentation
     * @return the presentation string
     */
    private String getDefaultPresentation() {
        StringBuffer html = new StringBuffer();

        // Work out the correct spacing for each tab for better presentation.
        // 2% removed for each end of tab.
        int displayCount = getDisplayCount();
        int percentWidth = (100 / displayCount) - 2;


        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
        if (width != null && !width.equals("")) {
            html.append(" width=\"").append(width).append("\"");
        }
        html.append(" vspace=\"0\">");
        html.append("<tr class=\"channelHeader\">");

        for (Iterator it = tabCollection.iterator(); it.hasNext(); ) {
            Tab tab = (Tab) it.next();

            // Only draw tab is display is true
            if (tab.isDisplay()) {

                if (displayCount == 1) {
                    // There is only one tab to display; this must be it
                    // Rather than draw it as a tab, simply draw it as a channel header
                    html.append("<td width=\"1%\"><img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-left_end.gif\" width=\"8\" height=\"15\" alt=\"\" border=\"0\"></td>");
                    html.append("<td valign=\"middle\" align=\"left\" class=\"channelHeader\" width=\"98%\">")
                            .append(HTMLUtils.escape(tab.getResolvedLabel()))
                            .append("</td>");
                    html.append("<td width=\"1%\" align=\"right\"><img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-right_end.gif\" width=\"8\" height=\"15\" alt=\"\" border=\"0\"></td>");

                } else {

                    if (tab.isSelected()) {
                        // A selected tab includes a "Maximized" icon and is not
                        // clickable
                        html.append("<td class=\"channelHeaderDarker\" width=\"1%\">")
                                .append("<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif\" width=\"20\" height=\"15\" border=\"0\" hspace=\"0\" vspace=\"0\">")
                                .append("</td>");
                        html.append("<td NOWRAP class=\"channelHeaderDarker\" width=\"").append(String.valueOf(percentWidth)).append("%\">")
                                .append(HTMLUtils.escape(tab.getResolvedLabel()))
                                .append("</td>");
                        html.append("<td class=\"channelHeaderDarker\" width=\"1%\" align=\"right\">")
                                .append("<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-right_end.gif\" width=\"8\" height=\"15\" alt=\"\" border=\"0\">")
                                .append("</td>");

                    } else {
                        // An unselected tab includes a "Minimized" icon and is
                        // clickable

                        html.append("<td class=\"channelHeader\" width=\"1%\">");
                        if (tab.isClickable()) {
                            html.append("<a href=\"").append(tab.getHref()).append("\">");
                        }
                        html.append("<img  src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif\" width=\"20\" height=\"15\" border=\"0\" hspace=\"0\" vspace=\"0\">");
                        if (tab.isClickable()) {
                            html.append("</a>");
                        }
                        html.append("</td>");
                        html.append("<td NOWRAP class=\"channelHeader\" width=\"").append(String.valueOf(percentWidth)).append("%\">");
                        if (tab.isClickable()) {

                            html.append("<a href=\"").append(tab.getHref()).append("\" class=\"channelNoUnderline\">");
                        }
                        html.append(HTMLUtils.escape(tab.getResolvedLabel()));
                        if (tab.isClickable()) {
                            html.append("</a>");
                        }
                        html.append("</td>");
                        html.append("<td class=\"channelHeader\" width=\"1%\" align=\"right\">")
                                .append("<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-right_end.gif\" width=\"8\" height=\"15\" alt=\"\" border=\"0\">")
                                .append("</td>");
                    }

                }

            }

        }
        html.append("</tr>");
        html.append("</table>");

        return html.toString();
    }

    /**
     * Draw tab strip with tab presentation
     * @return the presentation string
     */
    private String getAlternativePresentation()
    {
        StringBuffer html = new StringBuffer();
        html.append("<table class=\"tabSetHeader\">");
        html.append("<tr>");
        for (Iterator it = tabCollection.iterator(); it.hasNext(); )
        {
            Tab tab = (Tab) it.next();

            // Only draw tab is display is true
            if (tab.isDisplay())
            {
                if (tab.isSelected())
                    html.append("<td class=\"tabBackground tabBackgroundActive\">")
                    	.append("<a href=\""+ HTMLUtils.escape(tab.getHref()) + "\" style=\"text-decoration: none;\">")
                    	.append("<span>" + HTMLUtils.escape(tab.getResolvedLabel()) + "</span>")                    		
                    	.append("</a>")
                        .append("</td>");
                else
                    html.append("<td class=\"tabBackground tabBackgroundDeActive\">")
            			.append("<a href=\""+ HTMLUtils.escape(tab.getHref()) + "\" style=\"text-decoration: none;\">")
            			.append("<span>" + HTMLUtils.escape(tab.getResolvedLabel()) + "</span>")                    		
            			.append("</a>")
            			.append("</td>");
            }
        }
        html.append("</tr>");
        html.append("</table>");

        return html.toString();
    }    
    
    /**
     * Returns the count of tabs that are currently being displayed.
     * @return the count of displayed tabs
     */
    private int getDisplayCount() {
        int count = 0;

        Iterator it = this.tabCollection.iterator();
        while (it.hasNext()) {
            if (((Tab) it.next()).isDisplay()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Get the width of the tab strip table.
     *
     * @return a <code>String</code> which will be inserted into the width=""
     * statement which creates the table.
     */
    public String getWidth() {
        return width;
    }

    /**
     * Set the width of the tab strip table.  If null or blank, the width will
     * not be specified.
     *
     * @param width a <code>String</code> value indicating the width of the tab
     * strip table.  A null or blank value will prevent a width attribute from
     * being added.
     */
    public void setWidth(String width) {
        this.width = width;
    }

	public Boolean getTabPresentation()
	{
		return tabPresentation;
	}

	public void setTabPresentation(Boolean tabPresentation)
	{
		this.tabPresentation = tabPresentation;
	}

	/**
     * Return tab strip XML body including version tag
     * @return the xml string
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return tab strip XML body without version tag.
     * @return the xml string
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Iterator it = tabCollection.iterator();

        xml.append("<tab_strip>");
        while (it.hasNext()) {
            xml.append(((Tab) it.next()).getXMLBody());
        }
        xml.append("</tab_strip>");
        return xml.toString();
    }

}
