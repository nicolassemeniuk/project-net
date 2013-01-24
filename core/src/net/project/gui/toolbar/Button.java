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
package net.project.gui.toolbar;

import net.project.util.Conversion;

/**
  * Button class
*/
public class Button  implements java.io.Serializable, net.project.persistence.IXMLPersistence, Comparable {
    
    protected static String LABEL_POS_LEFT = "left";
    protected static String LABEL_POS_RIGHT = "right";

    /** Maintains counter to assign to each new button's orderNum field */
    private static int ordinal = 1;


    /*
        Instance attributes
     */
    private ButtonType type = null;
    private String imageEnabled = null;
    private String imageEnabledToken = null;
    private String imageDisabled = null;
    private String imageDisabledToken = null;
    private String imageOver = null;
    private String imageOverToken = null;
    private String label = null;
    private String labelToken = null;
    private String alt = null;
    private String altToken = null;
    private String labelPos = null;
    private String function = null;
    private boolean isShowLabel = false;
    private boolean isShowImage = true;
    private boolean isShow = false;
    private boolean isEnable = false;

    /** Order number assigned when object created */
    private int order = 0;
    /** user order number assigned by taglib, if never assigned then button sorted last */
    private int userOrder = Integer.MAX_VALUE;

    private String target = null;

    /**
      * Creates new button based on a certain type with specified properties
      * Properties which may be tokens are assumed to be passed as tokens.
      */
    Button(ButtonType type, String imageEnabledToken, String imageDisabledToken, String imageOverToken, String labelToken, String altToken, String function) {

	// Create button interpreting params as tokens
	this(type, true, imageEnabledToken, imageDisabledToken, imageOverToken, labelToken, altToken, function);
    }
    
    /**
      * Creates new button based on a certain type with specified properties.
      * @param type the button type
      * @param isTokenBased true implies imageEnabled, imageDisabled, imageOver, label
      * are all token values.  Otherwise those params assumed to be literal text.
      */
    Button(ButtonType type, boolean isTokenBased, String imageEnabled, String imageDisabled, String imageOver, String label, String alt, String function) {
        
	this.order = ordinal++;
        this.type = type;
        this.labelPos = LABEL_POS_LEFT;
	
	if (isTokenBased) {
	    this.imageEnabledToken = imageEnabled;
	    this.imageDisabledToken = imageDisabled;
	    this.imageOverToken = imageOver;
	    this.labelToken = label;
	    this.altToken = alt;
	} else {
	    this.imageEnabled = imageEnabled;
	    this.imageDisabled = imageDisabled;
	    this.imageOver = imageOver;
	    this.label = label;
	    this.alt = alt;
	}
	this.function = function;
    this.label = getResolvedLabel();    
    }

    /**
      * Set all properties
      */
    public void setProperties(String imageEnabledToken, String imageDisabledToken, String imageOverToken, String labelToken, String altToken, String function) {
        this.imageEnabledToken = imageEnabledToken;
        this.imageDisabledToken = imageDisabledToken;
        this.imageOverToken = imageOverToken;
        this.labelToken = labelToken;
	this.altToken = altToken;
        this.function = function;
    }

    /**
      * Returns the resolved image enabled value.
      * The image enabled value is returned if present.  This preserves compatiblity
      * with those places in code still passing a hardcoded string.
      * If no image enabled value is present, the token is resolved and returned.
      * @return the resolved value for image enabled
      */
    public String getResolvedImageEnabled() {
	return getProperty(getImageEnabledToken(), getImageEnabled());
    }

    /**
      * Returns the resolved image disabled value.
      * The image disabled value is returned if present.  This preserves compatiblity
      * with those places in code still passing a hardcoded string.
      * If no image disabled value is present, the token is resolved and returned.
      * @return the resolved value for image disabled
      */
    public String getResolvedImageDisabled() {
	return getProperty(getImageDisabledToken(), getImageDisabled());
    }

    /**
      * Returns the resolved image over value.
      * The image over value is returned if present.  This preserves compatiblity
      * with those places in code still passing a hardcoded string.
      * If no image over value is present, the token is resolved and returned.
      * @return the resolved value for image over
      */
    public String getResolvedImageOver() {
	return getProperty(getImageOverToken(), getImageOver());
    }

    /**
      * Returns the resolved label value.
      * The label value is returned if present.  This preserves compatiblity
      * with those places in code still passing a hardcoded string.
      * If no label value is present, the token is resolved and returned.
      * @return the resolved value for label
      */
    public String getResolvedLabel() {
	return getProperty(getLabelToken(), getLabel());
    }

    /**
      * Returns the resolved alt value.
      * The alt value is returned if present.  This preserves compatiblity
      * with those places in code still passing a hardcoded string.
      * If no alt value is present, the token is resolved and returned.
      * @return the resolved value for alt
      */
    public String getResolvedAlt() {
	
	// If we have no alt value but we have a string label, 
	// then we should use that as an alt value.
	// This preserves legacy code that sets the label to a hardcoded string
	// and expects the alt to display the same.
	if (getAlt() == null && getLabel() != null) {
	    return getLabel();
	}

	// Return the alt string or the alt token value
	return getProperty(getAltToken(), getAlt());
    }

    ButtonType getType() {
        return this.type;
    }

    public void setImageEnabled(String imageEnabled) {
        this.imageEnabled = imageEnabled;
    }

    String getImageEnabled() {
        return this.imageEnabled;
    }

    public void setImageEnabledToken(String imageEnabledToken) {
        this.imageEnabledToken = imageEnabledToken;
    }

    String getImageEnabledToken() {
        return this.imageEnabledToken;
    }

    public void setImageDisabled(String imageDisabled) {
        this.imageDisabled = imageDisabled;
    }

    String getImageDisabled() {
        return this.imageDisabled;
    }

    public void setImageDisabledToken(String imageDisabledToken) {
        this.imageDisabledToken = imageDisabledToken;
    }

    String getImageDisabledToken() {
        return this.imageDisabledToken;
    }

    public void setImageOver(String imageOver) {
        this.imageOver = imageOver;
    }

    String getImageOver() {
        return this.imageOver;
    }

    public void setImageOverToken(String imageOverToken) {
        this.imageOverToken = imageOverToken;
    }

    String getImageOverToken() {
        return this.imageOverToken;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    String getLabel() {
        return this.label;
    }

    public void setLabelToken(String labelToken) {
        this.labelToken = labelToken;
        this.label = net.project.base.property.PropertyProvider.get(labelToken);
    }

    String getLabelToken() {
        return this.labelToken;
    }

    public void setLabelPos(String labelPos) throws ToolbarException {
        if (LABEL_POS_LEFT.equals(labelPos) ||
            LABEL_POS_RIGHT.equals(labelPos)) {
            this.labelPos = labelPos;
        } else {
            throw new ToolbarException("Invalid labelPos value: " + labelPos);
        }
    }

    String getLabelPos() {
        return this.labelPos;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    String getAlt() {
        return this.alt;
    }

    public void setAltToken(String altToken) {
        this.altToken = altToken;
    }

    String getAltToken() {
        return this.altToken;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    String getFunction() {
        return this.function;
    }

    public void setShowLabel(boolean isShowLabel) {
        this.isShowLabel = isShowLabel;
    }

    boolean isShowLabel() {
        return this.isShowLabel;
    }

    public void setShowImage(boolean isShowImage) {
        this.isShowImage = isShowImage;
    }

    boolean isShowImage() {
        return this.isShowImage;
    }
    
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    boolean isShow() {
        return this.isShow;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    boolean isEnable() {
        return this.isEnable;
    }

    public void setUserOrder(int userOrder) {
        this.userOrder = userOrder;
    }

    int getUserOrder() {
        return this.userOrder;
    }

    public String getName() {
        return this.type.getName();
    }

    public void setTarget(String target) {
        this.target = target;
    }

    String getTarget() {
        return this.target;
    }

    /**
      * Buttons are equal if their types are the same
      */
    public boolean equals(Object obj) {
        if (obj instanceof Button
            && this.type.equals( ((Button) obj).type)) {
            return true;
        }
        return false;
    }

    /**
      * Compare two buttons based on their order numbers / user order numbers
      * Note - This is completely different to equals()
      */
    public int compareTo(Object obj) {
        int thisValue;
        int buttonValue;
        Button button = (Button) obj;
        
        // If either button has a userOrderNum set (i.e. one of them is < max value)
        // then compare based on those values.
        // Otherwise, compare based on orderNum
        if (button.userOrder < Integer.MAX_VALUE || this.userOrder < Integer.MAX_VALUE) {
            thisValue = this.userOrder;
            buttonValue = button.userOrder;
        } else {
            thisValue = this.order;
            buttonValue = button.order;
        }

        if (thisValue > buttonValue) {
            return 1;
        } else if (thisValue < buttonValue) {
            return -1;
        } else {
            return 0;
        }

    }
                                              
    /**
      * Returns the absolute value if not null or the value for propertyName.
      * @param propertyName the property name to get the value for
      * @param absoluteValue the value to return if present
      */
    private String getProperty(String propertyName, String absoluteValue) {
	String value = null;
	
	if (absoluteValue != null) {
	    value = absoluteValue;
	
	} else {
	    // Lookup up the property name if specified
	    if (propertyName != null) {
		value = net.project.base.property.PropertyProvider.get(propertyName);
	    }
	
	}

	return value;
    }

    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<button>");
        xml.append(getType().getXMLBody());
        xml.append("<image_enabled>" + getResolvedImageEnabled() + "</image_enabled>");
        if (getImageDisabled() != null) {
            xml.append("<image_disabled>" + getResolvedImageDisabled() + "</image_disabled>");
        }
        if (getImageOver() != null) {
            xml.append("<image_over>" + getResolvedImageOver() + "</image_over>");
        }
        xml.append("<label>" + getResolvedLabel() + "</label>");
        xml.append("<label_pos>" + getLabelPos() + "</label_pos>");
        xml.append("<alt>" + getResolvedAlt() + "</alt>");
        xml.append("<function>" + getFunction() + "</function>");
        xml.append("<is_show>" + Conversion.booleanToInteger(isShow()) + "</is_show>");
        xml.append("<is_enable>" + Conversion.booleanToInteger(isEnable()) + "</is_enable>");
        xml.append("<is_show_label>" + Conversion.booleanToInteger(isShowLabel()) + "</is_show_label>");
        xml.append("<is_show_image>" + Conversion.booleanToInteger(isShowImage()) + "</is_show_image>");
        xml.append("<target>" + getTarget() + "</target>");
        xml.append("</button>");
        return xml.toString();
    }

}

