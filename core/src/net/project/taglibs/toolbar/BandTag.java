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
package net.project.taglibs.toolbar;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.gui.toolbar.Band;
import net.project.gui.toolbar.Toolbar;
import net.project.gui.toolbar.ToolbarException;
import net.project.util.Conversion;

/**
  * A BandTag
  * This is the tag for adding a toolbar band
  */
public class BandTag extends BodyTagSupport {

    /** the toolbar created by the parent tag */
    private Toolbar toolbar = null;
    /** the band being created */
    private Band band = null;
    
    /** name attribute */
    private String name = null;
    
    /** group heading if any for this band */
    private String groupHeading = null;
    /** showAll attribute */
    private boolean showAll = false;
    /** showLabels attribute */
    private boolean showLabels = false;
    /** enableAll attribute */
    private boolean enableAll = false;

    /* Track which boolean properties were actually set */
    private boolean isShowAllSet = false;
    private boolean isShowLabelsSet = false;
    private boolean isEnableAllSet = false;

    /**
      * Process the band start tag
      */
    public int doStartTag() throws JspTagException {
        /* Grab parent ToolbarTag */
        ToolbarTag toolbarTag = (ToolbarTag) findAncestorWithClass(this, ToolbarTag.class);
        if (toolbarTag == null) {
            throw new JspTagException("Error in band tag: band not inside toolbar");
        }

        /* Get toolbar that was created by ToolbarTag and add new band */
        toolbar = toolbarTag.getToolbar();
        try {
            band = toolbar.addBand(name);
            if (isShowAllSet) {
                //band.setShowAll(showAll);
            }
            if (isShowLabelsSet) {
                band.setShowLabels(showLabels);
            }
            if (isEnableAllSet) {
                band.setEnableAll(enableAll);
            }
            band.setGroupHeading(groupHeading);
        } catch (ToolbarException te) {
            throw new JspTagException("Error in band tag: error adding band to toolbar: " + te);
        }
	return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() {
        clear();
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setName(String name) {
        this.name = name;
    }
    public void setGroupHeading(String groupHeading) {
        this.groupHeading = groupHeading;
    }
    public void setShowAll(String showAll) {
        //this.showAll = Conversion.toBoolean(showAll);
        this.isShowAllSet = true;
    }
    public void setShowLabels(String showLabels) {
        this.showLabels = Conversion.toBoolean(showLabels);
        this.isShowLabelsSet = true;
    }
    public void setEnableAll(String enableAll) {
        this.enableAll = Conversion.toBoolean(enableAll);
        this.isEnableAllSet = true;
    }

    /**
      * Return the band that was created by this tag
      * @return the band
      */
    Band getBand() {
        return this.band;
    }

    private void clear() {
        toolbar = null;
        band = null;
        name = null;
        groupHeading = null;
        showAll = false;
        showLabels = false;
        enableAll = false;
        isShowAllSet = false;
        isShowLabelsSet = false;
        isEnableAllSet = false;
    }
}

