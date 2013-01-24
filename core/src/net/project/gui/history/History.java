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
|    $Revision: 20793 $
|    $Date: 2010-05-04 12:13:46 -0300 (mar, 04 may 2010) $
|    $Author: nilesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.gui.history;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import net.project.gui.history.HistoryLevel.Level;
import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.util.HTMLUtils;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
  * History object for displaying history
  */
public class History implements Serializable, IXMLPersistence {

    /** current history list */
    private Vector historyList = new Vector();

    /** Presentation style */
    private String style = null;

    /** Stylesheet path for custom presentation */
    private String stylesheet = null;

    /** target for link click */
    private String target = null;

    /**
      * Creates a new history object.
      */
    public History() {
        clear();
        historyList.add(new BusinessLevel());
        historyList.add(new ProjectLevel());
        historyList.add(new ModuleLevel());
        historyList.add(new PageLevel(0));
    }

    /**
      * Reset attributes to default values
      */
    public void clear() {
        setStyle("default");
        setStylesheet(null);
        setTarget(null);
    }

    /**
      * Set the style attribute
      * @param style the style
      */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
      * Set the stylesheet attribute
      * @param stylesheet the stylesheet
      */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
      * Set the target attribute
      * @param target the target
      */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
      * Add level to our list in its correct position.<br>
      * This will set the previous level to Active.<br>
      * It will also hide all lower levels.<br>
      * @param level the history level to set
      */
    public void setLevel(HistoryLevel level) {
        HistoryLevel currentLevel = null;

	// Expand the history vector if adding a level higher than the last
	// highest level previously displayed.
	// This only happens if adding a deep page level
        if (level.getLevelNumber() >= historyList.size()) {
            for (int i = historyList.size(); i <= level.getLevelNumber(); i++ ) {
                // Add a new page with offset
                historyList.add(new PageLevel(i - HistoryLevel.PAGE.getLevelNumber()));
            }
        }
        
	// Set this level at the correct position
        historyList.set(level.getLevelNumber(), level);

        // Set the previous level to active (or the next higher visible level)
	// This has the effect of enabling the hyperlink (if one was specified)
        currentLevel = level;
        while (currentLevel.getLevel() != HistoryLevel.BUSINESS && currentLevel.getLevel() != HistoryLevel.ENTERPRISE) {
            // Get previous level as current level
            currentLevel = (HistoryLevel) historyList.get(currentLevel.getLevelNumber() - 1);
            if (currentLevel.isShow()) {
                // Make it active, abort loop
                currentLevel.setActive(true);
                break;
            }
        }

        // Now hide all history items below this level
        for (int i = level.getLevelNumber()+1; i < historyList.size(); i++) {
            ((HistoryLevel) historyList.get(i)).setShow(false);
        }
    }

    /**
      * Draw the history object
      * @return HTML representation of history object
      * @throws HistoryException if there is a problem getting the presentation
      */
    public String getPresentation() throws HistoryException {
        String presentation = null;
        if (style == null && stylesheet == null) {
            throw new HistoryException("Either style or stylesheet required to display history.");
        }
        if (stylesheet != null) {
            presentation = getStylesheetPresentation();
        
        } else {
            if (style.equals("default")) {
                presentation = getDefaultPresentation();
            
            } else {
                throw new HistoryException("Invalid History style: " + style);
            }
        }
        return presentation;
    }

    /**
      * Draw history in default style
      */
    private String getDefaultPresentation() {
        StringBuffer html = new StringBuffer();
        Iterator it = null;
        HistoryLevel level = null;
        String display = null;
        String targetUrl = null;
        boolean isFirstItem = true;

        html.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        html.append("<tr><td>");

        it = historyList.iterator();
        while (it.hasNext()) {
            level = (HistoryLevel) it.next();
            
            if (level.isShow()) {
                display = level.getResolvedDisplay();
                
                if(display != null)
                	targetUrl = level.getTargetURL();

                if (display == null) {
                    display = "&nbsp;";
                }

                // Add ">" for subsequent history entries
                if (!isFirstItem) {
                    html.append(" <span class=\"bc-arrows\">&#187;</span> ");
                }

                // Only insert hyperlink on active entries and ones which have a targetUrl
                if (level.isActive() && targetUrl != null) {
                    // Insert hyper link and text
                    html.append("<a href=\"" + targetUrl + "\" class=\"historyText\" ");
                    if (this.target != null) {
                        html.append("target=\"" + this.target + "\" ");
                    }
                    html.append(">" + HTMLUtils.escape(display) + "</a>");
                
                } else {
                    // Just insert text
                    html.append("<span class=\"historyText\">" + HTMLUtils.escape(display) + "</span> ");
                
                }
                
                isFirstItem = false;
            
            } else {
                // No show
                if (!level.getLevel().equals(HistoryLevel.BUSINESS) && !level.getLevel().equals(HistoryLevel.PROJECT)) {
                    /* Only business and project are optional... If any other
                       level is not visible, abort display */
                    break;
                }
            }
        }

        html.append("</td></tr>\n");
        html.append("</table>");

        return html.toString();
    }

    public HistoryLevel getLevel( Level type){
    	Iterator it = null;
        it = historyList.iterator();
        HistoryLevel level = null;
        while (it.hasNext()) {
            level = (HistoryLevel) it.next();
            if(level.isShow() && level.getLevel().equals(type)){
            	return level;
            }
        }
        return null;
    }
    
    /**
      * Draw history with custom transformation <br />
      * setStylesheet should be called prior to this (i.e. stylesheet property set)
      * @return the presentation string
      * @see setStylesheet 
      */
    private String getStylesheetPresentation() {
        XMLFormatter xmlFormatter = new XMLFormatter();
        xmlFormatter.setStylesheet(this.stylesheet);
        return xmlFormatter.getPresentation(getXML());
    }

    /**
      * Return history XML body including version tag
      * @return the xml string
      */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
      * Return history XML body without version tag.
      * This includes only those history entries that have their SHOW attribute
      * set to TRUE.
      * @return the xml string
      */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Iterator it = null;
        HistoryLevel level = null;
        
        xml.append("<history_list>\n");
        it = historyList.iterator();
        while (it.hasNext()) {
            /* Grab level from vector */
            level = (HistoryLevel) it.next();

            if (level.isShow()) {
                xml.append("\t<history level_number=\"" + XMLUtils.escape(Conversion.intToString(level.getLevelNumber())) + "\">\n");
                xml.append("\t<display>" + XMLUtils.escape(level.getResolvedDisplay()) + "</display>\n");
                xml.append("\t<jsp_page>" + XMLUtils.escape(level.getJspPage()) + "</jsp_page>\n");
                xml.append("\t<query_string>" + XMLUtils.escape(level.getQueryString()) + "</query_String>\n");
                xml.append("\t<target_url>" + XMLUtils.escape(level.getTargetURL()) + "</target_url>\n");
                xml.append("\t<is_active>" + XMLUtils.escape((level.isActive() ? "1" : "0")) + "</is_active>\n");
                xml.append("\t</history>\n");

            } else {
                /* No show */
                if (!level.getLevel().equals(HistoryLevel.BUSINESS) &&
                    !level.getLevel().equals(HistoryLevel.PROJECT)) {
                    /* Only business and project are optional... If any other
                       level is not visible, abort return of XML */
                    break;
                }
            }
        }
        xml.append("</history_list>\n");

        return xml.toString();
    }

}
