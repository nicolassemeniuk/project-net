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

package net.project.taglibs.toolbar;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import net.project.base.property.PropertyProvider;
import net.project.gui.toolbar.Toolbar;
import net.project.gui.toolbar.ToolbarException;
import net.project.util.Conversion;


/**
  * ToolbarTag is the taglib tag for toolbar <br>
  * Example<pre><code>
  * &lt;tb:toolbar style="action" showLabels="true">
  *     &lt;tb:band name="action" enableAll="true">
  *             &lt;tb:button type="back" />
  *             &lt;tb:button type="next" />
  *             &lt;tb:button type="cancel" />
  *     &lt;/tb:band>
  * &lt;/tb:toolbar>
  * </pre></code>
  * @see net.project.gui.toolbar.Toolbar
  */
public class ToolbarTag extends TagSupport {
    /** Toolbar being built */
    private Toolbar toolbar = null;

    /** style attribute */
    private String style = null;

    /** leftTitle attribute */
    private String leftTitle = null;

    /** leftTitleToken attribute - allows the title to be set from a property */
    private String leftTitleToken = null;

    /** rightTitle attribute */
    private String rightTitle = null;

    /** rightTitleToken attribute - allows the title to be set from a property */
    private String rightTitleToken = null;

    /** allows to specify the highlited group title on top of actions **/
    private String groupTitle = null;
    
    /** stylesheet attribute */
    private String stylesheet = null;

    /** showAll attribute */
    private boolean showAll = false;

    /** showLabels attribute */
    private boolean showLabels = true;

    /** showLabels attribute */
    private boolean showImages = false;
    
    /** enableAll attribute */
    private boolean enableAll = false;

    /** escapeTitle attribute */
    private boolean escapeTitle = false;

    /** width attribute */
    private String width = null;

    /** align attribute */
    private String align = null;

    /** Fixed position for the bottom actino bar*/
    private boolean _bottomFixed = false;
    
    /** Space attribute to set left heading div id */
    private String space;
    
    /** Sub title attribute to set page title */
    private String subTitle;
    
    /** Sub title attribute to set image */
    private String imagePath;
    
    /** isProjectListPage attribute to attached space with left heading */
    private boolean isProjectListPage;

    /** showSpaceDetails attribute to attached space details with left heading */
    private boolean showSpaceDetails = true;

    /* Track which boolean properties were actually set
       so we only override the default settings for the ones where an attribute
       was specified in the tag */
    private boolean isShowAllSet = false;
    private boolean isShowLabelsSet = false;
    private boolean isShowImagesSet = false;
    private boolean isEnableAllSet = false;
    private boolean isEscapeTitleSet = false;
    private boolean isBottomFixed = false;

    private boolean showVertical = true;
    /**
      * Start of tag.  Called after the attributes are set.
      * Create new toolbar and set all the attributes
      * @return flag indicating whether to process body or not
      */
    public int doStartTag() throws JspTagException {
        try {
            toolbar = new Toolbar();

            /* Set all attributes that we currently have */
            setToolbarAttributes();
        } catch (ToolbarException te) {
            throw new JspTagException("Error displaying toolbar: " + te);
        }

        /* Continue evaluating tag body */
        return EVAL_BODY_INCLUDE;
    }

    /**
      * End of tag.
      * Write out presentation of toolbar
      * @return flag indicating whether to continue processing page
      */
    public int doEndTag() throws JspTagException {
        JspWriter out;
        String presentation = null;

        try {
            /* Spit out toolbar */
            try {
                /* Set attributes that may have changed */
                setToolbarAttributes();
                presentation = toolbar.getPresentation();
                out = pageContext.getOut();
                out.print(presentation);
            } catch (ToolbarException te) {
                throw new JspTagException("Error displaying toolbar: " + te);
            } catch (IOException ioe) {
                throw new JspTagException("Error displaying toolbar: " + ioe);
            }
        } finally {
            /* Empty out toolbar and clear all attributes*/
            clear();
        }
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setStyle(String style) {
        this.style = style;
    }

    public void setShowAll(String showAll) {
        //this.showAll = Conversion.toBoolean(showAll); // ignore showAll, keep it false     	
        this.isShowAllSet = true;
    }

    public void setShowLabels(String showLabels) {
        this.showLabels = Conversion.toBoolean(showLabels);
        this.isShowLabelsSet = true;
    }

    public void setShowImages(String showImages) {
        this.showImages = Conversion.toBoolean(showImages);
        this.isShowImagesSet = true;
    }
    
    public void setEnableAll(String enableAll) {
        this.enableAll = Conversion.toBoolean(enableAll);
        this.isEnableAllSet = true;
    }

    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public void setLeftTitleToken(String leftTitleToken) {
        this.leftTitleToken = leftTitleToken;
    }

    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public void setRightTitleToken(String rightTitleToken) {
        this.rightTitleToken = rightTitleToken;
    }
    
    public void setGroupTitle(String groupTitle) {
    	if(PropertyProvider.isDefined(groupTitle))
    		this.groupTitle = PropertyProvider.get(groupTitle);
    	else
    		this.groupTitle = groupTitle;
    }
    
    public void setEscapeTitle(String escapeTitle) {
        this.escapeTitle = Conversion.toBoolean(escapeTitle);
        this.isEscapeTitleSet = true;
    }

    public void setStylesheet(String stylesheetPath) {
        this.stylesheet = stylesheetPath;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setBottomFixed(String bottomFixed) {
        _bottomFixed = Conversion.toBoolean(bottomFixed);
        this.isBottomFixed = true;
    }

	public void setShowVertical(String showVertical) {
		this.showVertical = Conversion.toBoolean(showVertical);
	}
	
	public void setSpace(String space) {
		this.space = space;
	}
	
	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the isProjectListPage
	 */
	public boolean isProjectListPage() {
		return isProjectListPage;
	}

	/**
	 * @param isProjectListPage the isProjectListPage to set
	 */
	public void setProjectListPage(boolean isProjectListPage) {
		this.isProjectListPage = isProjectListPage;
	}
	
	/**
	 * @return the showSpaceDetails
	 */
	public boolean isShowSpaceDetails() {
		return showSpaceDetails;
	}

	/**
	 * @param showSpaceDetails the showSpaceDetails to set
	 */
	public void setShowSpaceDetails(boolean showSpaceDetails) {
		this.showSpaceDetails = showSpaceDetails;
	}
	/**
      * Set an arbitrary attribute.  This is usually called by the
      * <code>&lt;setAttribute></code> tag nested within the <code>&lt;toolbar></code> tag.<br>
      *  Note - not all attributes may be set after this tag is initiated.
      *  The following attributes may be set any time before the closing toolbar tag:<br><code><ul>
      *     <li>style</li>
      *     <li>leftTitle</li>
      *     <li>rightTitle</li>
      *     <li>escapeTitle</li>
      *     <li>stylesheet</li></code>
      * @param name name of attribute to set
      * @param value the value of the attribute
      * @throws JspTagException if the specified name is not a valid attribute
      */
    public void setAttribute(String name, String value) throws JspTagException {
        if (name.equals("style")) {
            setStyle(value);
        } else if (name.equals("leftTitle")) {
            setLeftTitle(value);
        } else if (name.equals("leftTitleToken")) {
            setLeftTitleToken(value);
        } else if (name.equals("rightTitle")) {
            setRightTitle(value); 
        } else if (name.equals("rightTitleToken")) { 
            setRightTitleToken(value);
        } else if (name.equals("groupTitle")) { 
            setGroupTitle(value);
        } else if (name.equals("escapeTitle")) {
            setEscapeTitle(value);
        } else if (name.equals("stylesheet")) {
            setStylesheet(value);
        } else if (name.equals("width")) {
            setWidth(value);
        } else if (name.equals("align")) {
            setAlign(align);
        } else if( name.equals("spaceType")){
        	setSpace(space);
        } else if( name.equals("subTitle")){
        	setSubTitle(subTitle);
        } else if( name.equals("imagePath")){
        	setImagePath(imagePath);
        }else if( name.equals("isProjectListPage")){
        	setProjectListPage(isProjectListPage);
        }else if(name.equals("showSpaceDetails")){
        	setShowSpaceDetails(showSpaceDetails);
        } else {
            throw new JspTagException(
                "Error displaying toolbar. No such attribute '" + name + "'."
            );
        }
    }

    /*
        Other methods
     */

    /**
      * Return the toolbar being managed by this tag - allows inner tags
      * to manipulate it
      * @return the toolbar
      */
    Toolbar getToolbar() {
        return this.toolbar;
    }

    /**
      * Push all taglib attributes down to the toolbar
      */
    private void setToolbarAttributes() throws ToolbarException {
        /* Set the attributes that must be set prior to adding other elements */
        if (isShowAllSet) {
            //toolbar.setShowAll(showAll); // ignore showAll, keep it false
        }
        if (isShowLabelsSet) {
            toolbar.setShowLabels(showLabels);
        }
        if (isShowImagesSet) {
            toolbar.setShowImages(showImages);
        }
        if (isEnableAllSet) {
            toolbar.setEnableAll(enableAll);
        }
        if (style != null) {
            toolbar.setStyle(style);
        }
        if (leftTitle != null) {
            toolbar.setLeftTitle(leftTitle);
        }
        if (leftTitleToken != null) {
            toolbar.setLeftTitleToken(leftTitleToken);
        }
        if (rightTitle != null) {
            toolbar.setRightTitle(rightTitle);
        }
        if (rightTitleToken != null) {
            toolbar.setRightTitleToken(rightTitleToken);
        }
        if (groupTitle != null) {
            toolbar.setGroupTitle(groupTitle);
        }
        if (stylesheet != null) {
            toolbar.setStylesheet(stylesheet);
        }
        if (isEscapeTitleSet) {
            toolbar.setEscapeTitle(escapeTitle);
        }
        if (width != null) {
            toolbar.setWidth(width);
        }
        if (align != null) {
            toolbar.setAlign(align);
        }
        if (isBottomFixed) {
            toolbar.setBottomFixed(_bottomFixed);
        }
        if (showVertical) {
            toolbar.setShowVertical(showVertical);
        }
        if(space != null){
        	toolbar.setSpace(space);
        }
        if(subTitle != null){
        	toolbar.setSubTitle(subTitle);
        }
        if(imagePath != null){
        	toolbar.setImagePath(imagePath);
        }
        if(isProjectListPage){
        	toolbar.setProjectListPage(isProjectListPage);
        }
        if(showSpaceDetails){
        	toolbar.setShowSpaceDetails(showSpaceDetails);
        }
    }

    /**
      * Clear all properties
      */
    private void clear() {
        toolbar = null;
        style = null;
        leftTitle = null;
        leftTitleToken = null;
        rightTitle = null;
        rightTitleToken = null;
        groupTitle = null;
        stylesheet = null;
        width = null;
        align = null;
        showAll = false;
        showLabels = true;
        showImages = false;
        enableAll = false;
        escapeTitle = false;
        isShowAllSet = false;
        isShowLabelsSet = false;
        isEnableAllSet = false;
        isEscapeTitleSet = false;
        _bottomFixed = false;
        isBottomFixed = false;
        showVertical = true;
        space = null;
        subTitle = null;
        imagePath = null;
        isProjectListPage = false;
        showSpaceDetails = true;
    }
}
