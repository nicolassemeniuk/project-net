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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.channel.ChannelManager;
import net.project.channel.ScopeType;
import net.project.util.Conversion;

/**
 * ChannelTag is the taglib tag for channel.
 * 
 * @see net.project.channel.ChannelManager
 * @author Tim Morrow
 * @since Version 2
 */
public class ChannelTag extends TagSupport {

    /** Channel being built. */
    private ChannelManager channelManager = null;

    /** is customizable (personalizable). */
    private Boolean isCustomizable = null;

    /** The scope for loading and storing channel settings. */
    private ScopeType settingsScopeType = null;

    /**
     * Start of tag.
     * Called after the attributes are set.
     * Create new channel manager and set all the attributes.
     * 
     * @return {@link #EVAL_BODY_INCLUDE}
     */
    public int doStartTag() throws JspTagException {

        // Creates a new channel manager.  Initial number of channels is 0.
        // This will need extended as channels are added.
        channelManager = new ChannelManager(pageContext);

        if (isCustomizable != null) {
            channelManager.setCustomizable(isCustomizable.booleanValue());
        }

        if (settingsScopeType != null) {
            channelManager.setScopeType(settingsScopeType);
        }

        return EVAL_BODY_INCLUDE;
    }

    /**
     * End of tag.
     * Write out presentation of Channel manager
     * 
     * @return {@link #EVAL_PAGE}
     */
    public int doEndTag() throws JspTagException {
        try {

            try {
                // Display already writes to the current pageContext's out.
                channelManager.display();

            } catch (ServletException se) {
                throw new JspTagException("Error displaying channels: " + se);

            } catch (IOException ioe) {
                throw new JspTagException("Error displaying channels: " + ioe);
            }

        } finally {
            // Clear all attributes to allow reuse of this channel tag
            clear();
        }
        return EVAL_PAGE;
    }

    /**
     * Specifies the name of the channel.
     * 
     * @param name 
     * @deprecated As of 7.6.3; No replacement
     *             A channel does not have a name.
     */
    public void setName(String name) {
        // No work to do since deprecation
    }

    /**
     * Specifies whether the channel is customizable.
     * That is, whether to include a "Personalize ..." link.
     * @param isCustomizable true if the individual channels may
     * be turned on or off, etc.
     */
    public void setCustomizable(String isCustomizable) {
        this.isCustomizable = new Boolean(Conversion.toBoolean(isCustomizable));
    }

    /**
     * Specifies the scope for loading the settings controlling
     * the display of these channels.
     * 
     * @param settingsScope the scope; one of <code>global</code> or <code>space</code>
     */
    public void setSettingsScope(String settingsScope) {
        this.settingsScopeType = ScopeType.getForID(settingsScope);
    }

    /**
     * Return the channel manager being managed by this tag - allows inner tags
     * to manipulate it.
     * 
     * @return the channel manager
     */
    ChannelManager getChannelManager() {
        return this.channelManager;
    }

    /**
     * Clear all properties.
     */
    private void clear() {
        channelManager = null;
        isCustomizable = null;
    }

}