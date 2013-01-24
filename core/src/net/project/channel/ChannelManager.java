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

package net.project.channel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.resource.IPersonPropertyScope;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;
import net.project.security.User;


/**
 * Manages the display and customization of channels.
 *
 * @author Adam Klatzkin
 * @author Tim Morrow
 * @author Vladimir Malykhin
 */
public class ChannelManager implements java.io.Serializable {
    /**
     * Map where each key is an <code>Integer</code> row number and
     * each value is a <code>List</code> of channels at that row.
     * <p>
     * The List value is sparsely populated.
     * </p>
     */
    private final Map channelMap = new HashMap();

    /**
     * List of all channels in insertion order.
     */
    private final List allChannels = new ArrayList();

    /** The current PageContext used for writing HTML to. */
    private transient PageContext m_context = null;

    /** The current settings. */
    private transient PersonProperty settings = null;

    /** Indicates whether the channel properties are customizable. */
    private boolean isCustomizable = true;

    /** The scope at which to load and store the channel settings. */
    private ScopeType scopeType = ScopeType.SPACE;

    /**
     * Creates a new ChannelManager for the specified page context.
     * @param context the page context the manager is being used in
     */
    public ChannelManager(PageContext context) {
        this.m_context = context;
        this.settings = PersonProperty.getFromSession(m_context.getSession());
        prefetchSettings();
    }

    /**
     * Creates a new ChannelManager for the specified page context.
     *
     * @param context the page context the manager is being used in
     * @param pageName a unique name identifying the page the channel manager is
     * being used in
     * @param maxRows the number of rows channels on the page
     * @deprecated As of 7.6.3; use {@link #ChannelManager(PageContext)} instead.
     * pageName was never used and maxRows was spurious; the ChannelManager always
     * grows its internal structures to accomodate all rows added
     */
    public ChannelManager(PageContext context, String pageName, int maxRows) {
        this(context);
    }

    /**
     * Adds the specified channel to the channels managed.
     * <p>
     * If a channel is already located at the specified row and column it
     * is replaced.
     * </p>
     * @param channel the channel to add
     * @param row the row at which to add the channel
     * @param col the column at which to add the channel on that row
     */
    public void addChannel(Channel channel, int row, int col) {
        // Only load properties if the channel is customizable;
        // This is necessary to avoid scenarios where previously
        // customizable channels are now no longer customizable
        // When channels are not customizable we ignore all stored settings
        // and use the defaults
        if (isCustomizable) {
            loadUserSettings(channel);
        }

        // Grab the columns for the specified row
        Integer key = new Integer(row);
        List columnChannels = (List) channelMap.get(key);
        if (columnChannels == null) {
            columnChannels = new ArrayList(3);
            columnChannels.add(null);
            columnChannels.add(null);
            columnChannels.add(null);
            channelMap.put(key, columnChannels);
        }

        // Update the channel at the specified column
        columnChannels.set(col, channel);

        // Tells the channel the current scope; required for stateful
        // behavior of the channel (like minimizing / maximizing)
        channel.setScope(getScope());

        // Add to list of all channels
        allChannels.add(channel);
    }

    /**
     * Pre-fetches the personal settings for the current user and space
     * for all channel-related properties only if the current user and
     * space is not already being managed.
     * <p>
     * This will minimize database access when loading property settings.
     * </p>
     */
    private void prefetchSettings() {
        User user =
            (User) m_context.getAttribute("user", PageContext.SESSION_SCOPE);

        // Construct a scope based on the current scope type
        IPersonPropertyScope newScope = this.scopeType.makeScope(user);
        if (!settings.isCurrent(newScope)) {
            settings.setScope(newScope);
            settings.prefetchForContextPrefix(
                ChannelProperties.CHANNEL_PROPERTY_CONTEXT
            );
        }
    }

    /**
     * Loads the user's settings for the specified channel.
     * <p>
     * The channel's properties are loaded.
     * </p>
     * @param channel the channel for which to load settings
     */
    private void loadUserSettings(Channel channel) {
        User user =
            (User) m_context.getAttribute("user", PageContext.SESSION_SCOPE);
        PersonProperty settings =
            PersonProperty.getFromSession(m_context.getSession());

        // Construct a scope based on the current scope type
        settings.setScope(this.scopeType.makeScope(user));
        channel.loadProperties(settings);
    }

    /**
     * Indicates whether the channels are customizable, that is
     * whether a user may customize the display properties.
     * <p>
     * This is used to determine whether to write a "Personalize" link
     * when displaying the channels.
     * </p>
     * @param isCustomizable true if the channel properties can be customized;
     * false otherwise
     */
    public void setCustomizable(boolean isCustomizable) {
        this.isCustomizable = isCustomizable;
    }

    /**
     * Indicates whether the channels are customizable.
     * @return true if the channels can be customized; false otherwise
     */
    public boolean isCustomizable() {
        return this.isCustomizable;
    }

    /**
     * Specifies the scopeType at which to load and store the settings.
     * <p>
     * The default ScopeType is {@link ScopeType#SPACE}.
     * </p>
     * @param scopeType the scopeType
     */
    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    /**
     * Returns the current scope type indicating where
     * settings are stored and loaded.
     * @return the scope type
     */
    ScopeType getScopeType() {
        return this.scopeType;
    }

    /**
     * Returns the current scope.
     * @return the current scope
     */
    private IPersonPropertyScope getScope() {
        return this.settings.getScope();
    }

    /**
     * Returns an unmodifiable collection of channels currently
     * managed by this ChannelManager in insertion order.
     * @return the collection where each element is a <code>Channel</code>.
     */
    Collection getChannels() {
        return Collections.unmodifiableCollection(this.allChannels);
    }

    /**
     * Writes the HTML code to the outputstream from the current context
     * to display the channels.
     * <p>
     * Each channel row is drawn as a table.
     * Each channel column is a table division in that table.
     * Padding is inserted between each channel column.
     * </p>
     * @throws IOException if there is a problem writing the output
     * @throws ServletException if there is a problem getting the output stream
     */
    public void display() throws IOException, ServletException {
        // set this ChannelManager instance in the session
        m_context.setAttribute(
            "AZK_ChannelManager",
            this,
            PageContext.SESSION_SCOPE
        );

        JspWriter out = m_context.getOut();

        for (Iterator it = getOrderedRowIterator(); it.hasNext();) {
            Integer nextRowNumber = (Integer) it.next();

            out.println(
                "<table border=0 cellpadding=0 cellspacing=0 width=\"99%\"><TR>"
            );

            boolean isAtLeastOneChannel = false;
            boolean isPadRequired = false;
            List channelColumns = (List) channelMap.get(nextRowNumber);
            for (Iterator it2 = channelColumns.iterator(); it2.hasNext();) {
                Channel nextChannel = (Channel) it2.next();

                if (nextChannel != null) {
                    if (isPadRequired) {
                        out.println("<TD>&nbsp;&nbsp;&nbsp;</TD>");
                    }

                    out.println(
                        "<td valign=\"top\" align=\"left\" width=\"" +
                        nextChannel.getWidth() + "\">"
                    );
                    nextChannel.display(m_context);
                    out.println("</td>");

                    if (!nextChannel.isClosed()) {
                        // Since this channel was drawn, we must add
                        // padding between it and the next
                        isPadRequired = true;
                        /*
                         * There are some channels we don't want to add a break line after them.
                         */
                        if(nextChannel.isAddBreak()){
                            isAtLeastOneChannel = true;
                        }
                    }
                }
            }

            out.println("</tr></table>");

            // We only add this break if a channel was drawn on the row
            // This avoids adding visible whitespace for empty rows
            if (isAtLeastOneChannel) {
                out.println("<br clear=all>");
            }
        }

        // the channel personalization link.
        if (isCustomizable) {
            out.println(getPersonalizeLink());
        }
    }

    /**
     * Returns an iterator of row numbers ordered by row number.
     * @return an Iterator where each element is an <code>Integer</code>
     */
    private Iterator getOrderedRowIterator() {
        List rowNumbers = new ArrayList(channelMap.keySet());
        Collections.sort(rowNumbers);
        return rowNumbers.iterator();
    }

    /**
     * Returns the HTML to insert a "Personalize" link at the bottom of the page.
     * @return HTML proving a link to CustomizeChannels.jsp
     */
    public String getPersonalizeLink() {
        HttpServletRequest request =
            (HttpServletRequest) m_context.getRequest();

        //String qs = request.getQueryString();
        // todo: 8.1.4 change
        String qs =
            net.project.util.HttpUtils.getRedirectParameterString(request);

        if (qs != null) {
            qs = "?" + qs;
        } else {
            qs = "";
        }

        StringBuffer url = new StringBuffer();

        try {
            url.append(SessionManager.getJSPRootURL())
                   .append("/channel/CustomizeChannels.jsp?referer=").append(
                java.net.URLEncoder.encode(
                    SessionManager.getJSPRootURL() + request.getServletPath() +
                    qs
                )
            );

            // Add the scope so that it is build into personalize link
            url.append("&").append(getScope().formatRequestParameters());

            // Need to add id and name of every channel
            for (
                Iterator iterator = getChannels().iterator();
                iterator.hasNext();
            ) {
                Channel channel = (Channel) iterator.next();

                url.append("&name=")
                       .append(
                        URLEncoder.encode(
                            channel.getName(),
                            SessionManager.getCharacterEncoding()
                        )
                    ).append("&title=").append(
                    URLEncoder.encode(
                        channel.getDisplayTitle(),
                        SessionManager.getCharacterEncoding()
                    )
                );
            }
        } catch (UnsupportedEncodingException e) {
            throw new PnetRuntimeException("Unable to encode URL: " + e, e);
        }

        /*String personalizeLink =
            "<A HREF=\"" + url +
            "\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/personalize.gif\" border=0>" +
            PropertyProvider.get("prm.global.personalizepage.link") + "</A>"; */

        String personalizeJs = 
        	"<script>" +
	        	"function personalizePage(){" + 
	        		"window.document.location.href= \"" + url + "\"" + 
	        	"}" +
	        "</script>";
        
        /*// closing of <div id='content'>
        personalizeLink += "</div>";*/

        return personalizeJs;
    }
}
