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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.taglibs.channel;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.channel.Channel;
import net.project.gui.toolbar.ToolbarException;

/**
 * A channel button tag
 * This is the tag for adding a channel button to a channel item
 */
public class ButtonTag extends TagSupport {

    private static final String CHANNEL_STYLE = "channel";
    private static final String ACTION_STYLE = "action";

    /** the channel created by the parent tag */
    private Channel channel = null;

    private String type = null;
    private String style = CHANNEL_STYLE;
    private String label = null;
    private String labelToken = null;
    private String href = null;

    /**
     * Checks this tag is inside an insert tag.
     */
    public int doStartTag() throws JspTagException {

        /* Grab parent ChannelTag */
        InsertTag insertTag = (InsertTag)findAncestorWithClass(this, InsertTag.class);
        if (insertTag == null) {
            throw new JspTagException("Error in button tag: button not inside insert.");
        }

        /* Get channel that was created by InsertTag */
        channel = insertTag.getChannel();

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Creates a button and inserts into channel.
     */
    public int doEndTag() throws JspTagException {

        try {

            if (style == null || style.equals(CHANNEL_STYLE)) {

                if (labelToken != null) {
                    channel.addChannelButtonTokens(type, labelToken, href);
                } else {
                    channel.addChannelButton(type, label, href);
                }

            } else {

                if (labelToken != null) {
                    channel.addActionButtonTokens(type, labelToken, href);
                } else {
                    channel.addActionButton(type, label, href);
                }

            }

        } catch (ToolbarException te) {
            throw new JspTagException("Error adding button to channel: " + te);

        }

        // Clear all attributes to avoid remnants of this button in next one.
        clear();
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setLabel(String label) {
        if (label != null && label.charAt(0) == '@') {
            setLabelToken(label);
            this.label = null;
        } else {
            this.label = label;
        }
    }

    public void setLabelToken(String labelToken) {
        this.labelToken = labelToken;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Clear all attributes.
     */
    private void clear() {
        type = null;
        style = null;
        labelToken = null;
        href = null;
    }
}

