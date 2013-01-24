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
import net.project.gui.toolbar.Button;
import net.project.gui.toolbar.ToolbarException;
import net.project.util.Conversion;

/**
 * ButtonTag
 * Adds a button to a toolbar band
 */
public class ButtonTag extends BodyTagSupport {

    /** the band being inserted */
    private Band band = null;

    /** Button being added by this tag */
    private Button button = null;

    /** type attribute */
    private String type = null;

    /** show attribute */
    private boolean show = false;

    /** showLabel attribute */
    private boolean showLabel = false;

    /** enable attribute */
    private boolean enable = false;

    /** label attribute */
    private String label = null;

    /*** labelPos attribute */
    private String labelPos = null;

    /** alt attribute */
    private String alt = null;

    /** altToken attribute */
    private String altToken = null;

    /** function attribute */
    private String function = null;

    /** imageEnabled attribute */
    private String imageEnabled = null;

    /** imageEnabledToken attribute - specifies the token to use for the image */
    private String imageEnabledToken = null;

    /** imageDisabled attribute */
    private String imageDisabled = null;

    /** imageDisabledToken attribute */
    private String imageDisabledToken = null;

    /** imageOver attribute */
    private String imageOver = null;

    /** imageOverToken attribute */
    private String imageOverToken = null;

    /** order attribute */
    private Integer order = null;

    /** target attribute. */
    private String target = null;

    /* Track which boolean attributes where actually set */
    private boolean isShowSet = false;
    private boolean isShowLabelSet = false;
    private boolean isEnableSet = false;

    /**
     * Process start of Button tag
     */
    public int doStartTag() throws JspTagException {

        /* Grab parent BandTag */
        BandTag bandTag = (BandTag)findAncestorWithClass(this, BandTag.class);
        if (bandTag == null) {
            throw new JspTagException("Error in button tag: button not inside band");
        }

        /* Fetch band created by BandTag and customize a button on it */
        band = bandTag.getBand();
        try {
        	if("custom".equals(type))
        		button = band.addCustomButton(imageEnabled, imageDisabled, imageOver, label, function);
        	else { 
        		button = band.addButton(type);
	        	
	            if (isShowSet) {
	                button.setShow(show);
	            }
	            if (isShowLabelSet) {
	                button.setShowLabel(showLabel);
	            }
	            if (isEnableSet) {
	                button.setEnable(enable);
	            }
	            if (label != null) {
	                // Check to see if label is a token or not
	                // Use appropriate button method
	                if (net.project.base.property.PropertyProvider.isToken(label)) {
	                    button.setLabelToken(net.project.base.property.PropertyProvider.stripTokenPrefix(label));
	
	                } else {
	                    button.setLabel(label);
	
	                }
	            }
	            if (labelPos != null) {
	                button.setLabelPos(labelPos);
	            }
	            if (alt != null) {
	                button.setAlt(alt);
	            }
	            if (altToken != null) {
	                button.setAltToken(altToken);
	            }
	            if (function != null) {
	                button.setFunction(function);
	            }
	            if (imageEnabled != null) {
	                button.setImageEnabled(imageEnabled);
	            }
	            if (imageEnabledToken != null) {
	                button.setImageEnabledToken(imageEnabledToken);
	            }
	            if (imageDisabled != null) {
	                button.setImageDisabled(imageDisabled);
	            }
	            if (imageDisabledToken != null) {
	                button.setImageDisabledToken(imageDisabledToken);
	            }
	            if (imageOver != null) {
	                button.setImageOver(imageOver);
	            }
	            if (imageOverToken != null) {
	                button.setImageOverToken(imageOverToken);
	            }
	            if (order != null) {
	                button.setUserOrder(order.intValue());
	            }
	            if (target != null) {
	                button.setTarget(target);
	            }
        	}
        } catch (ToolbarException te) {
            throw new JspTagException("Error in button tag: " + te);
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
    public void setType(String type) {
        this.type = type;
    }

    public void setShow(String show) {
        this.show = Conversion.toBool(show);
        this.isShowSet = true;
    }

    public void setShowLabel(String showLabel) {
        this.showLabel = Conversion.toBool(showLabel);
        this.isShowLabelSet = true;
    }

    public void setEnable(String enable) {
        this.enable = Conversion.toBool(enable);
        this.isEnableSet = true;
    }

    /**
     * Specifies the label for the button.
     * @param label the text label; or a token name, prefixed with the
     * standard token prefix.
     * @see net.project.base.property.PropertyProvider#TOKEN_PREFIX
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the name of a token to use as a label.
     * @param labelToken the name of a token whose value to lookup and use
     * as the label
     * @deprecated Since Gecko Update 3.  Use {@link #setLabel} instead, specifying
     * the label as <code>@tokenname</code>.  This is to provide more consistency
     * with other taglibs; it is redundant to have a separate method for
     * passing token values
     */
    public void setLabelToken(String labelToken) {
        this.label = net.project.base.property.PropertyProvider.TOKEN_PREFIX + labelToken;
    }

    public void setLabelPos(String labelPos) {
        this.labelPos = labelPos;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setAltToken(String altToken) {
        this.altToken = altToken;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setImageEnabled(String imageEnabled) {
        this.imageEnabled = imageEnabled;
    }

    public void setImageEnabledToken(String imageEnabledToken) {
        this.imageEnabledToken = imageEnabledToken;
    }

    public void setImageDisabled(String imageDisabled) {
        this.imageDisabled = imageDisabled;
    }

    public void setImageDisabledToken(String imageDisabledToken) {
        this.imageDisabledToken = imageDisabledToken;
    }

    public void setImageOver(String imageOver) {
        this.imageOver = imageOver;
    }

    public void setImageOverToken(String imageOverToken) {
        this.imageOverToken = imageOverToken;
    }

    public void setOrder(String order) {
        this.order = new Integer(Conversion.toInt(order));
    }

    /**
     * Specifies the target frame in which to invoke the URL
     * of this button.
     * <p>
     * A <code>target</code> attribute will be added to the anchor element.
     * This is useful for buttons in frames.
     * </p>
     * @param target the target which should be a legal value for
     * the <code>target</cde> attribute of the <code>a</code> element
     */
    public void setTarget(String target) {
        this.target = target;
    }

    private void clear() {
        band = null;
        button = null;
        type = null;
        show = false;
        showLabel = false;
        enable = false;
        label = null;
        labelPos = null;
        alt = null;
        altToken = null;
        function = null;
        imageEnabled = null;
        imageEnabledToken = null;
        imageDisabled = null;
        imageDisabledToken = null;
        imageOver = null;
        imageOverToken = null;
        order = null;
        target = null;
        isShowSet = false;
        isShowLabelSet = false;
        isEnableSet = false;
    }
}


