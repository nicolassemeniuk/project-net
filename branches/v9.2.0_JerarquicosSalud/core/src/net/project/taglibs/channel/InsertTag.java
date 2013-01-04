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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.channel.Channel;
import net.project.channel.ChannelManager;
import net.project.channel.IChannelModifier;
import net.project.util.Conversion;

/**
  * A channel insert tag
  * This is the tag for adding a channel item to the channel
  */
public class InsertTag extends BodyTagSupport {

    /** the channelManager created by the parent tag */
    private ChannelManager channelManager = null;
    
    /** the channel being created */
    private Channel channel = null;
    
    private String name = null;
    private String title = null;
    private String titleToken = null;
    private String titleColor = null;
    private String titleURL = null;
    private String description = null;
    private String channelColor = null;
    private String width = null;
    private String height = null;
    private String channelAlign = null;
    private String helpURL = null;
    private String include = null;
    private String defaultState = null;
    private Integer row = new Integer(0);
    private Integer column = new Integer(0);
    private Boolean isMinimizable = null;
    private Boolean isCloseable = null;
    private Boolean actionBarOnly = null;
    private Boolean isInIFrame = null;
    private Boolean addBreak = null; /* To not add a break aftert the channel.*/

    /**
      * Checks this tag is inside a channel tag.
      */
    public int doStartTag() throws JspTagException {
        
        /* Grab parent ChannelTag */
        ChannelTag channelTag = (ChannelTag) findAncestorWithClass(this, ChannelTag.class);
        if (channelTag == null) {
            throw new JspTagException("Error in insert tag: insert not inside channel.");
        }

        /* Get channelManager that was created by ChannelTag and add new channel */
        channelManager = channelTag.getChannelManager();
	
        // Create a new channel
        channel = new Channel(name);
        
        if (title != null) {
            channel.setTitle(title);
        }
        if (titleToken != null) {
            channel.setTitleToken(titleToken);
        }
        if (width != null) {
            channel.setWidth(width);
        }
        if (channelAlign != null) {
            channel.setChannelAlign(channelAlign);
        }
        if (helpURL != null) {
            channel.setHelpURL(helpURL);
        }
        if (include != null) {
            channel.setInclude(include);
        }
        if (defaultState != null) {
            channel.setDefaultState(defaultState);
        }
        if (isMinimizable != null) {
            channel.setMinimizable(isMinimizable.booleanValue());
        }
        if (isCloseable != null) {
            channel.setCloseable(isCloseable.booleanValue());
        }
        if (actionBarOnly != null) {
            channel.setActionbarOnly(actionBarOnly.booleanValue());
        }
        
        if(this.addBreak != null){
            channel.setAddBreak(this.addBreak.booleanValue());
        }

        // Look for enclosing channel customizer and allow it to customize the channel
        try {
            IChannelModifier channelCustomizer = (IChannelModifier) findAncestorWithClass(this, IChannelModifier.class);
            if (channelCustomizer != null) {
                channelCustomizer.modifyChannel(channel);
            }
        
        } catch (PnetException pe) {
            throw new JspTagException("Error in insert tag: error customizing channel: " + pe);
        
        }

        // Now add this channel to the channel manager
        channelManager.addChannel(channel, row.intValue(), column.intValue());

        return BodyTagSupport.EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        BodyContent bodyContent = getBodyContent();
        channel.setIncludedContent(bodyContent.getString());
        return SKIP_BODY;
    }

    /**
      * Creates a channel and inserts into channel manager.
      */
    public int doEndTag() {
        // Clear all attributes to avoid remnants of this channel in next one.
        clear();
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setName(String name) {
        this.name = name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTitleToken(String titleToken) {
        this.titleToken = titleToken;
    }
    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }
    public void setTitleURL(String titleURL) {
        this.titleURL = titleURL;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setChannelColor(String channelColor) {
        this.channelColor = channelColor;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public void setChannelAlign(String channelAlign) {
        this.channelAlign = channelAlign;
    }
    public void setHelpURL(String helpURL) {
        this.helpURL = helpURL;
    }
    public void setInclude(String include) {
        this.include = include;
    }
    public void setDefaultState(String defaultState) {
        this.defaultState = defaultState;
    }
    public void setDefaultStateToken(String defaultStateToken) {
        this.defaultState = PropertyProvider.get(defaultStateToken);
    }
    public void setRow(String row) {
        this.row = new Integer(Conversion.toInt(row));
    }
    public void setColumn(String column) {
        this.column = new Integer(Conversion.toInt(column));
    }
    public void setMinimizable(String isMinimizable) {
        this.isMinimizable = new Boolean(Conversion.toBoolean(isMinimizable));
    }
    public void setCloseable(String isCloseable) {
        this.isCloseable = new Boolean(Conversion.toBoolean(isCloseable));
    }
    public void setActionBarOnly(String actionBarOnly) {
        this.actionBarOnly = new Boolean(Conversion.toBoolean(actionBarOnly));
    }
    public void setInIFrame(String isInIFrame) {
        this.isInIFrame = new Boolean(Conversion.toBoolean(isInIFrame));
    }
    
    /**
     * Sets the <code>addBreak</code> attribute, to indicate whether to add a break line after the channel.
     * @param addBreak the value to set the <code>addBreak</code> attribute
     * @since 8.5
     */
    public void setAddBreak(String addBreak){
        this.addBreak = new Boolean(Conversion.toBoolean(addBreak));
    }

    /**
      * Clear all attributes.
      */
    private void clear() {
        channel = null;
        
        name = null;
        title = null;
        titleToken = null;
        titleColor = null;
        titleURL = null;
        description = null;
        channelColor = null;
        width = null;
        height = null;
        channelAlign = null;
        helpURL = null;
        include = null;
        row = new Integer(0);
        column = new Integer(0);
        isMinimizable = null;
        isCloseable = null;
        actionBarOnly = null;
        isInIFrame = null;
        defaultState = null;
        this.addBreak = null;
    }

    Channel getChannel() {
        return channel;
    }
}


