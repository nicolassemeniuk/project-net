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
import javax.servlet.jsp.tagext.Tag;

/**
  * This tag calls the setAttribute() method in the enclosing tag
  * passing its own name attribute and its body content
  * Useful for supporting the setting of attributes with complex data (like
  * the results of another taglib).  Not all toolbar tags support this as a 
  * nested attribute.
  * You can normally only specify a string or a runtime expression (<%= %>) for
  * an attribute value. <br>
  * E.g.<br><code>
  * &lt;tb:toolbar style="tooltitle"><br>
  *     &lt;tb:setAttribute name="leftTitle"><br>
  *         &lt;history:history /><br>
  *     &lt;/tb:setAttribute><br>
  * &lt;/tb:toolbar><br>
  * </code>
  */
public class SetAttributeTag extends BodyTagSupport {

    /** Parent tag to set attribute in */
    private Tag parentTag = null;
    /** Name of attribute being set */
    private String name = null;
    /** Body content */
    private String content = null;

    /**
      * Start of tag.  Called after the attributes are set.
      * @return flag indicating whether to process body or not
      */
    public int doStartTag() throws JspTagException {
        Tag parent = getParent();
        if (parent == null) {
            throw new JspTagException("setAttribute may only be used inside another tag.");
        }

        if (parent instanceof ToolbarTag) {
            // We support setting attributes in ToolbarTag
            this.parentTag = parent;

        } else if (parent instanceof BandTag) {
            // Not supported as yet
            throw new JspTagException("setAttribute not currently supported inside BandTag.");

        } else if (parent instanceof ButtonTag) {
            throw new JspTagException("setAttribute not currently supported inside ButtonTag.");

        }
        /* Continue evaluating tag body */
	return EVAL_BODY_BUFFERED;
    }

    /**
      * After body content
      * This stores the body content just processed
      */
    public int doAfterBody() {
        this.content = getBodyContent().getString();
        return (SKIP_BODY);
    }

    /**
      * End of tag.
      * Calls the setAttribute method in the parent tag.
      * @return flag indicating whether to continue processing page
      */
    public int doEndTag() throws JspTagException {
        if (this.parentTag instanceof ToolbarTag) {
            ((ToolbarTag) this.parentTag).setAttribute(this.name, this.content);
        }

        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
        Other methods
     */
    private void clear() {
        this.name = null;
        this.content = null;
        this.parentTag = null;
    }

}

