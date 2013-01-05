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
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import net.project.base.property.PropertyProvider;
import net.project.gui.toolbar.Band;
import net.project.gui.toolbar.Button;
import net.project.gui.toolbar.ButtonType;
import net.project.gui.toolbar.Toolbar;
import net.project.gui.toolbar.ToolbarException;
import net.project.resource.IPersonPropertyScope;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;
import net.project.space.SpaceType;
import net.project.space.SpaceTypes;
import net.project.util.HttpUtils;
import net.project.util.StringUtils;

/**
 * This class provides channel rendering, state management and persistence.
 * The methods provided attempt to emulate typical window manager.
 * To use the class you must provide a name for the channel that is unique within
 * a user's space context.  By using this class you are gauranteed that the channel
 * instance will be unique across user and space boundaries.
 * The channel name is required in the constructor.  All other API calls are optional
 * but must be invoked before calling the display method.
 *
 * Defaults:
 *    Title - channel name
 *    Personalizable - false
 *    Minimizable - true
 *    Closeable - false
 *    state - open (maximized)
 *
 * @author AdamKlatzkin
 * @since 03/00
 */
public class Channel implements java.io.Serializable {
    private static final String CHANNEL_PROCESSING_JSP = SessionManager.getJSPRootURL() + "/channel/ChannelProcessing.jsp";
    private static final String PARAMETER_name = "name";
    private static final String PARAMETER_state = "state";
    private static final String PARAMETER_referer = "referer";

    // private static final String BUTTON_minimize = SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif";
    // private static final String BUTTON_maximize = SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
    // private static final String BUTTON_close = SessionManager.getJSPRootURL()+"/images/icons/channelbar-close_it.gif";
    
    private static String BUTTON_minimize = SessionManager.getJSPRootURL()+"/images/personal/dashboard_arrow-down.gif";        
    private static String BUTTON_maximize = SessionManager.getJSPRootURL()+"/images/personal/dashboard_arrow-up.gif";
    private static String BUTTON_close = SessionManager.getJSPRootURL()+"/images/personal/dashboard_close.gif";
    
    private static final String BUTTON_help = SessionManager.getJSPRootURL()+"/images/button/qmark.gif";
    private static final String BUTTON_height = "15";
    private static final String BUTTON_width = "15";
    private static final String BUTTON_right = SessionManager.getJSPRootURL()+"/images/icons/channelbar-right_end.gif";
    private static final String BUTTON_left = SessionManager.getJSPRootURL()+"/images/icons/channelbar-left_end.gif";

    private String m_name = null;
    private String m_title = null;
    private String m_titleToken = null;
    private String m_width = null;
    private String m_helpURL = null;
    private String m_include = null;
    private String includedContent = null;
    private String m_channelAlign = "left";

    private boolean m_isMinimizable = true;
    private boolean m_isCloseable = false;
    private boolean m_actionBarOnly = false;
    private boolean m_displayActionBar = false;
    /* To not add a break line after the channel. By default we will add a break.*/
    private boolean addBreak = true;

    private final Hashtable m_includeAttributes = new Hashtable();

    // Attributes for building toolbar (both action and channel)
    private Toolbar m_actionBar = null;
    private Band m_actionBand = null;
    private Toolbar m_channelBar = null;
    private Band m_channelBand = null;

    /** The user-settable properties of the channel. */
    private ChannelProperties properties = ChannelProperties.makeDefault();

    /**
     * The default state (open, minimized, closed) of this channel
     * when no other state is loaded from stored properties.
     * A null value implies the system default state will be used
     * if no other state is specified.
     */
    private State defaultState = null;

    /**
     * The current scope used when rendering the channel.
     */
    private IPersonPropertyScope scope;

    /**
     * The name assigned to a channel must be unique within a users space context.  The channel
     * class will ensure that it is unique across users and spaces.
     *
     * Example: <pre>
     *  The meeting manager has an attendee list that should be displayed as a channel.
     *  A good name for this is:
     *    MeetingManager_Attendee_[MeetingName]
     *        ^             ^           ^------ the channel is tied to a specific meeting
     *        |             |--- the channel is identified as holding attendee information for
     *        |                  a specified meeting
     *        |--- the channel is unique to the meeting manager
     *
     *  There is no need to construct the name containing identifying user and space info
     *  because the channel class handles this for you.
     * </pre>
     * @param name       channel name
     */
    public Channel(String name) {
        m_name = name;
        initializeButtons();
    }

    /**
	 * Initializing the button images as per current space type.
	 */
	private void initializeButtons() {	
		if(getName().contains("portfolio") || getCurrentSpaceType().equalsIgnoreCase(SpaceTypes.METHODOLOGY_SPACE)){
			BUTTON_minimize = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.PROJECT_SPACE +"/dashboard_arrow-down.gif";
		    BUTTON_maximize = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.PROJECT_SPACE +"/dashboard_arrow-up.gif";
		    BUTTON_close = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.PROJECT_SPACE +"/dashboard_close.gif";
		} else if(getName().contains("portfolio") || getCurrentSpaceType().equalsIgnoreCase(SpaceTypes.CONFIGURATION_SPACE)){
			BUTTON_minimize = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.APPLICATION_SPACE +"/dashboard_arrow-down.gif";
		    BUTTON_maximize = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.APPLICATION_SPACE +"/dashboard_arrow-up.gif";
		    BUTTON_close = SessionManager.getJSPRootURL()+"/images/"+ SpaceTypes.APPLICATION_SPACE +"/dashboard_close.gif";
		} else {
		    BUTTON_minimize = SessionManager.getJSPRootURL()+"/images/"+ getCurrentSpaceType() +"/dashboard_arrow-down.gif";
		    BUTTON_maximize = SessionManager.getJSPRootURL()+"/images/"+ getCurrentSpaceType() +"/dashboard_arrow-up.gif";
		    BUTTON_close = SessionManager.getJSPRootURL()+"/images/"+ getCurrentSpaceType() +"/dashboard_close.gif";
		}
	}

	/**
     * Returns the channel's internal name.
     * @return the channel name
     */
    String getName() {
        return m_name;
    }

    /**
     * Set the width of the channel as a fixed amount of pixels or as a
     * percentage.
     *
     * @param width the channels width as used in a browser (number or percentage)
     */
    public void setWidth(String width) {
        m_width = width;
    }

    /**
     * Get the width that this channel will be rendered at.  This can be a fixed
     * number of pixels or a percentage.
     *
     * @return a <code>String</code> value containing the channel's width
     */
    public String getWidth() {
        return m_width;
    }

    /**
     * Sets the height of the channel (only applicable if drawn as an IFRAME)
     * @param height the height, either in pixels or percentage of the page
     * @see #setInIFrame
     * @deprecated As of 7.6.3; No replacement.
     * The height is determined by a customized channel property
     */
    public void setHeight(String height) {
    }

    /**
     * @param title      string to appear in the channels title bar
     */
    public void setTitle(String title) {
        m_title = title;
    }

    /**
     * @return String   the channel's title
     */
    private String getTitle() {
        return m_title;
    }

    /**
     * @param titleToken      string to appear in the channels title bar
     */
    public void setTitleToken(String titleToken) {
        m_titleToken = titleToken;
    }

    /**
     * @return String   the channel's title token
     */
    private String getTitleToken() {
        return m_titleToken;
    }

    /**
     * Get the display title.
     * This is the resolved title, either absolute Title or looked up TitleToken
     */
    public String getDisplayTitle() {
        return getProperty(getTitleToken(), getTitle());
    }
    
    /**
     * Get the current space of the user
     * @return user's current space type
     */
    public String getCurrentSpaceType(){
    	return SessionManager.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE) ? "personal" 
    			: SessionManager.getUser().getCurrentSpace().getType().toLowerCase();
    }

    /**
     * @param url url of a document containing help information about the channel.
     * calling the method will result in a help button appearing on the channel's
     * title bar.  When the button is clicked a new window is opened and
     * directed to the specified url.
     */
    public void setHelpURL(String url) {
        m_helpURL = url;
    }

    /**
     * setting a channel as minimizable will result in a minimize/maximize button being
     * displayed in the channels title bar.  By clicking on the button the
     * user will be able to toggle the state of the channel.  This state will automatically
     * be maintained and persited by the channel object.
     *
     * @param state      true to set the channel as minimizable
     */
    public void setMinimizable(boolean state) {
        m_isMinimizable = state;
    }

    /**
     * setting a channel as closeable will result in a close button being
     * displayed in the channel's title bar.  By clicking on the button the
     * user will be able to close the channel.  This state will automatically
     * be maintained and persited by the channel object.
     *
     * @param state      true to set the channel as closeable
     */
    public void setCloseable(boolean state) {
        m_isCloseable = state;
    }

    /**
     * This sets the action bar state to be the only part that is displayed.
     * IF the state is set to true only the action bar will be displayed and nothing else
     *
     * @param state      true to only display the action bar
     */
    public void setActionbarOnly(boolean state) {
        m_actionBarOnly = state;
    }

    /**
     * The actionbar will be displayed even if there are no buttons to be displayed
     *
     * @param state      true to display the action bar
     */
    public void setDisplayActionBar(boolean state) {
        m_displayActionBar = state;
        if (m_displayActionBar) {
            createActionBar();
        } else {
            m_actionBar = null;
            m_actionBand = null;
        }
    }


    /**
     * Sets that this Channel should be displayed in an IFRAME.
     * @param isInIFrame true if the channel should be displaued in an IFRAME;
     * false if should be displayed in-line in the page on which the Channel
     * is drawn
     * @deprecated As of 7.6.3; No replacement.
     * Determination of whether to include in an IFRAME is by a channel property customized by the user
     */
    public void setInIFrame(boolean isInIFrame) {
    }

    /**
     * Indicates how the content of the channel should be aligned inside of the
     * channel.
     *
     * @param channelAlign a <code>String</code> value containing the word "left",
     * "right", or "center".
     */
    public void setChannelAlign(String channelAlign) {
        this.m_channelAlign = channelAlign;
    }

    /**
     * @return boolean  true if the channel is closed
     */
    public boolean isClosed() {
        return (properties.getState().equals(State.CLOSED));
    }

    /**
     * Sets the channel content.
     * This must be a path to an includable jsp that will generate the content for the
     * channel.
     *
     * @param include    jsp path
     */
    public void setInclude(String include) {
        m_include = include;
    }

    /**
     * Gets the <code>addBreak</code> attribute, which indicates whether a break is needed after the channel rendering.
     * @return the <code>addBreak</code> attribute
     * @since 8.5
     */
    public boolean isAddBreak(){
        return this.addBreak;
    }
    
    /**
     * Sets the <code>addBreak</code> attribute to indicate whether it is needed a break line after the channel
     * @param addBreak the value to set into the <code>addBreak</code> attribute
     * @since 8.5
     */
    public void setAddBreak(boolean addBreak){
        this.addBreak = addBreak;
    }
    /**
     * Sets included content to some JSP that has been transformed.  This
     * version of the channel does not use a JSP include.
     *
     * The IncludedContent and Include methods are mutually exclusive.  Include
     * overrides IncludedContent.
     *
     * @param includedContent a <code>String</code> containing the content that
     * should be in the channel.
     */
    public void setIncludedContent(String includedContent) {
        this.includedContent = includedContent;
    }

    /**
     * Sets the default state of this channel used when no other stored state is found.
     * <p>
     * If an invalid state is specified then the value will be ignored and the system default state will be used.
     * This value will only be utilized by calling {@link #loadProperties(net.project.resource.PersonProperty)}
     * </p>
     * @param state the state, one of <code>0</code>, <code>1</code>, <code>2</code>.
     */
    public void setDefaultState(String state) {

        this.defaultState = null;

        try {
            if (state != null) {
                this.defaultState = State.forID(state);
            }
        } catch (IllegalArgumentException e) {
            // Invalid state; Since this is likely
            // coming from a property, we won't propogate the
            // error; we'll continue with the system default state
        }

    }

    /**
     * add an attribute that will be passed to the included jsp via the request object
     * or as a request parameter if a scrollable frame is used.
     *
     * @param name attribute "key"
     * @param attribute the attribute value
     */
    public void addAttribute(String name, String attribute) {
        m_includeAttributes.put(name, attribute);
    }


    /**
     * Add an actionbar button for a specific button, with specified attributes.
     * <b>Note:</b> Generally, {@link #addActionButtonTokens} is preferred since
     * label may contain non-externalized text.
     * @param buttonType the type of button.  See Action Bar Taglib docs for
     * a list of buttons.
     * @param label the label for the button.  If null, default label is used.
     * @param function the function/href for the button.  If null, default is used
     */
    public void addActionButton(String buttonType, String label, String function) throws ToolbarException {

        addActionButton(buttonType, false, label, function);

    }

    /**
     * Add an actionbar button for a specific button, with specified attributes
     * @param buttonType the type of button.  See Action Bar Taglib docs for
     * a list of buttons.
     * @param labelToken the label token for the button.  If null, default label is used.
     * @param function the function/href for the button.  If null, default is used
     */
    public void addActionButtonTokens(String buttonType, String labelToken, String function) throws ToolbarException {

        addActionButton(buttonType, true, labelToken, function);

    }

    /**
     * Adds an action button to the channel.
     * @param buttonType the type of button. See Action Bar Taglib docs for a list
     * of button types.
     * @param isTokenBased true means label will be interpreted as a label token
     * @param label the label or label token for the button
     * @param function the function / href for the button
     */
    private void addActionButton(String buttonType, boolean isTokenBased, String label, String function) throws ToolbarException {

        Button button;
        if (m_actionBar == null) {
            createActionBar();
        }

        button = m_actionBand.addButton(buttonType);

        if (isTokenBased) {
            if (label != null) {
                button.setLabelToken(label);
            }

        } else {
            if (label != null) {
                button.setLabel(label);
            }
        }

        if (function != null) {
            button.setFunction(function);
        }
    }

    /**
     * Adds a channel button to this channel.
     * <b>Note:</b> Generally, {@link #addChannelButtonTokens} is preferred since
     * label may contain non-externalized text.
     * @param buttonType the type of button.  See channel Bar Taglib docs for
     * a list of buttons.
     * @param label the label for the button.  If null, default label is used.
     * @param function the function/href for the button.  If null, default is used
     */
    public void addChannelButton(String buttonType, String label, String function) throws ToolbarException {

        addChannelButton(buttonType, false, label, function);

    }


    /**
     * Add a channelbar button for a specific button, with specified attributes
     * @param buttonType the type of button.  See channel Bar Taglib docs for
     * a list of buttons.
     * @param labelToken the label token for the button.  If null, default label is used.
     * @param function the function/href for the button.  If null, default is used
     */
    public void addChannelButtonTokens(String buttonType, String labelToken, String function) throws ToolbarException {

        addChannelButton(buttonType, true, labelToken, function);

    }

    /**
     * Add a channelbar button for a specific button type with specified attributes
     * @param buttonType the type of button. See channel bar taglib docs for a list
     * of button types
     * @param isTokenBased true means label will be interpreted as a label token
     * @param label the label or label token for the button
     * @param function the function / href for the button
     */
    private void addChannelButton(String buttonType, boolean isTokenBased, String label, String function) throws ToolbarException {

        Button button;
        if (m_channelBar == null) {
            createChannelBar();
        }

        button = m_channelBand.addButton(buttonType);

        if (isTokenBased) {
            if (label != null) {
                button.setLabelToken(label);
            }

        } else {
            if (label != null) {
                button.setLabel(label);
            }
        }

        if (function != null) {
            button.setFunction(function);
        }
        
        if(buttonType.equalsIgnoreCase(ButtonType.MODIFY.toString())){
        	if(getName().contains("portfolio")|| getCurrentSpaceType().equalsIgnoreCase(SpaceTypes.METHODOLOGY_SPACE)){
    			button.setImageEnabled("/images/"+ SpaceTypes.PROJECT_SPACE +"/dashboard_configure.gif");
    		} else if(getName().contains("portfolio")|| getCurrentSpaceType().equalsIgnoreCase(SpaceTypes.CONFIGURATION_SPACE)){
    			button.setImageEnabled("/images/"+ SpaceTypes.APPLICATION_SPACE +"/dashboard_configure.gif");
    		} else {
    			button.setImageEnabled("/images/"+ getCurrentSpaceType() +"/dashboard_configure.gif");
    		}
        }
    }

    /**
     * Changes this channel's properties
     * @param channelProperties
     */
    void setProperties(ChannelProperties channelProperties) {
        this.properties = channelProperties;
    }

    /**
     * Returns the current properties of this channel.
     * @return the properties
     */
    ChannelProperties getProperties() {
        return this.properties;
    }

    /**
     * Sets the current scope.
     * This is required for rendering the URL of the minimize/close buttons
     * @param scope the current scope
     */
    void setScope(IPersonPropertyScope scope) {
        this.scope = scope;
    }

    /**
     * render the channel to the output stream in the specified page context.
     * pageContext is an implicit JSP object.
     *
     * @param context    page context for the jsp that the channel is to be displayed in
     */
    void display(PageContext context) throws IOException, ServletException {

        // if state of the channel is closed, return without rendering any
        // HTML to the output stream
        if (properties.getState().equals(State.CLOSED)) {
            return;
        }

        // we will need the JspWriter and ServletRequest of the pageContext
        JspWriter out = context.getOut();
        HttpServletRequest request = (HttpServletRequest)context.getRequest();

        // Render the channel
        //out.print("\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        if(getName().contains("portfolio")){
        	out.print("\n<div class=\"portal-"+ SpaceTypes.PROJECT_SPACE +"-flex-block\">");
        } else {
        	out.print("\n<div class=\"portal-"+ getCurrentSpaceType() +"-flex-block\">");
        }

        if (m_actionBarOnly == false) {

            //out.print("\n<tr class=\"channelHeader\">");
            //out.print("\n<td class=\"channelHeader\" width=\"1%\">");
        	
        	out.print("\n<div class=\"block-heading\">");
            out.print("\n<div class=\"block-controls\">");
            
            //out.print("</td>");

            //Get the title of this channel
            String propertyTitle = getProperty(getTitleToken(), getTitle());

            //If the title is empty, the table cell will not render correctly on netscape.
            //Instead, insert a non-breaking space if the table cell is empty.
            if (propertyTitle == null || propertyTitle.equals("")) {
                propertyTitle = "&nbsp;";
            }

            //Output the table cell.
            //out.print("\n<td nowrap class=\"channelHeader\">" + propertyTitle + "</td>");

            // Insert channel buttons, if any
            //out.print("\n<td align=\"right\" nowrap class=\"channelHeader\">");
            if (m_channelBar != null) {
                try {
                    out.print(m_channelBar.getPresentation());
                    out.print("&nbsp;");
                } catch (ToolbarException te) {
                    out.print("&nbsp;");
                }
            } else {
                out.print("&nbsp;");
            }
            //out.print("</td>");

            // Insert help / close buttons
            //out.print("\n<td align=\"right\" class=\"channelHeader\" width=\"5%\">");
            /*if (m_helpURL != null) {
                // A help url is available, display the help button
                ChannelButton button = new ChannelButton(BUTTON_help, m_helpURL);
                button.m_target = "CHANNEL_HELP_" + m_name;
                showButton(button, out, "");
            }*/
            
            if (m_isMinimizable) {
                // The channel is minimizable, determine whether to display the minimize or
                // maximize image
                State newState = State.OPEN;
                String buttonImage = BUTTON_maximize;
                String imageTitleMinimizeMaximize = null;
                if (properties.getState().equals(State.OPEN)) {
                    newState = State.MINIMIZED;
                    imageTitleMinimizeMaximize = PropertyProvider.get("all.global.channelbarbutton.title.minimize");
                } else if (properties.getState().equals(State.MINIMIZED)) {
                    newState = State.OPEN;
                    buttonImage = BUTTON_minimize;
                    imageTitleMinimizeMaximize = PropertyProvider.get("all.global.channelbarbutton.title.maximize");
                }
                String link = CHANNEL_PROCESSING_JSP +
                    "?" + PARAMETER_name + "=" + URLEncoder.encode(m_name, SessionManager.getCharacterEncoding()) +
                    "&" + PARAMETER_state + "=" + newState.getID() +
                    "&" + this.scope.formatRequestParameters() +
                    "&" + PARAMETER_referer + "=" + URLEncoder.encode(SessionManager.getJSPRootURL() + request.getServletPath() + "?" + HttpUtils.getRedirectParameterString(request), SessionManager.getCharacterEncoding());
                ChannelButton button = new ChannelButton(buttonImage, link, imageTitleMinimizeMaximize);
                showButton(button, out, m_name);
                out.print("&nbsp;");
            } else {
                //out.print("<img src=\"" + BUTTON_left + "\" width=8 height=15 alt=\"\" border=0 hspace=0 vspace=0>");

            }

            if (m_isCloseable) {
                // The channel is closeable, display the close butotn
                String link = CHANNEL_PROCESSING_JSP +
                    "?" + PARAMETER_name + "=" + URLEncoder.encode(m_name,
                    SessionManager.getCharacterEncoding()) +
                    "&" + PARAMETER_state + "=" + State.CLOSED.getID() +
                    "&" + this.scope.formatRequestParameters() +
                    "&" + PARAMETER_referer + "=" + URLEncoder.encode(SessionManager.getJSPRootURL() + request.getServletPath() + "?" + HttpUtils.getRedirectParameterString(request), SessionManager.getCharacterEncoding());
                ChannelButton button = new ChannelButton(BUTTON_close, link, PropertyProvider.get("all.global.channelbarbutton.title.close"));
                showButton(button, out, "close" + m_name);
            } else {
                //out.print("<img src=\"" + BUTTON_right + "\" width=8 height=15 alt=\"\" border=0 hspace=0 vspace=0>");
            }

            //out.print("</td>");
            //out.print("\n</tr>");
            out.print("\n</div>"); // block-controls end
            
            out.print("\n<div class=\"block-heading-end\"><div class=\"heading\" title=\""+ propertyTitle +"\">" + propertyTitle + "</div></div>");
            out.print("\n</div>"); // block-heading end 
            
            //out.print("\n<tr valign=\"top\">");
            //out.print("\n<td class=\"channelContent\">&nbsp;</td>");
            //out.print("\n<td colspan=\"4\" "+(m_channelAlign != null ? "align=\""+m_channelAlign+"\" " : "")+"class=\"channelContent\">");
            
            out.print("\n<div class=\"block-content\">");

            // Include the channel content
            if (properties.getState().equals(State.OPEN) && (m_include != null || includedContent != null)) {
                includeContent(context);
            }            

            //out.print("\n</td>");
            //out.print("\n</tr>");
            
            out.print("\n</div>"); //block-content end

        }

        if (m_actionBar != null) {
            //out.print("\n<tr valign=\"top\">");
            //out.print("\n<td class=\"channelContent\" colspan=\"4\">");
            try {
                out.print(m_actionBar.getPresentation());
            } catch (ToolbarException te) {
                out.print("&nbsp;");
            }
            //out.print("</td></tr>");
        }
        //out.print("\n</table>\n");
        out.print("\n</div>\n"); // portal-personal-flex-block end 

    }

    /**
     * Includes the content page.
     * <p>
     * This inclusion is performed differently depending on whether the channel
     * is to be drawn in a scrollable frame.
     * If the current setting indicates a scrollable frame, then the IFRAME HTML code
     * is drawn, and the include page is indirectly included by a wrapping page.
     * Note that in this case the current request query string is passed
     * through to the include page.
     * The size of the frame is determined by the properties also.
     * </p>
     * <p>
     * If drawn inline then the include page is simply included immediately.
     * </p>
     */
    private void includeContent(PageContext context) throws IOException, ServletException {

        JspWriter out = context.getOut();
        HttpServletRequest request = (HttpServletRequest)context.getRequest();

        if (properties.getFrameType().equals(FrameType.SCROLL)) {
            int height = properties.getFrameSize().getHeight();

            String srcURL = addAttributesAsParameters(request, SessionManager.getJSPRootURL() + "/channel/IFrame.jsp");

            String includeParameter = "includePage=" + HttpUtils.encodeURL(request.getSession(), m_include);
            String src = srcURL + "&" + includeParameter;

            out.print("\n<iframe id='channelIFrame'");
            out.print("src=\"" + src + "\" ");
            // Note: Width is 100%;  width of channel is governed by table cell
            // in which iframe is placed
            out.print("frameborder=\"0\" width=\"100%\" height=\"" + height + "\"> ");
            out.print("\n" + PropertyProvider.get("prm.global.error.noiframesupport.message")); // Your browser does not support IFRAMEs.
            out.print("\n</iframe> ");

        } else {
            if (m_include != null) {
                // add attributes to the request
                Enumeration e = m_includeAttributes.keys();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    request.setAttribute(key, m_includeAttributes.get(key));
                }

                context.include(m_include);

                // remove set request attributes
                e = m_includeAttributes.keys();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    request.setAttribute(key, null);
                }
            } else {
                context.getOut().println(includedContent);
            }
        }
    }

    /**
     * Adds current request attributes as parameters to a URL
     * and adds the attributes add to this channel.
     * @param request the request whose parameters to add
     * @param baseURL the base URL to add parameters to
     */
    private String addAttributesAsParameters(HttpServletRequest request, String baseURL) {
        StringBuffer params = new StringBuffer();

        boolean isAfterFirst = false;
        Enumeration e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();

            if (isAfterFirst) {
                params.append("&");
            }
            params.append(key + "=" + request.getAttribute(key));
            isAfterFirst = true;
        }

        // add other attributes to the request
        for (Iterator it = m_includeAttributes.keySet().iterator(); it.hasNext();) {
            String nextKey = (String) it.next();
            if (isAfterFirst) {
                params.append("&");
            }
            params.append(nextKey + "=" + m_includeAttributes.get(nextKey));
            isAfterFirst = true;
        }

        return baseURL + "?" + params.toString();
    }

    /**
     * output HTML for the specified button onto the output stream
     *
     * @param button      the button to generate HTML for
     * @param out         the output stream to write HTML to
     * @param channelName the channel name (anchor tag's id for testing)
     */
    private static void showButton(ChannelButton button, JspWriter out, String channelName) throws IOException {
    	String idParameter = new String();
    	if (channelName != null && !channelName.equals("")) {
    		idParameter = "id=\"" + channelName +"\"";
    	}
    	
    	out.print("<a " + idParameter + " HREF=\"" + button.m_url + "\" ");
        if (button.m_target != null)
            out.print("target=\"" + button.m_target + "\" ");
        out.print(">");
        out.print("<img src=\"" + button.m_image + "\" ");
        out.print(StringUtils.isNotEmpty(button.m_title) ? "title=\""+ button.m_title  +"\" " : "");
        out.print("width=\"" + BUTTON_width + "\" height=\"" + BUTTON_height + "\" border=0 hspace=0 vspace=0>");
        out.print("</a>");
    }

    /**
     * Loads the channel properties for this channel.
     * <p>
     * Updates <code>properties</code> both by loading and setting
     * any default properties.  If no default properties are specified here
     * or no properties are loaded, then system default properties are used.
     * </p>
     * @param propertyManager the propertyManager manager to use
     * @see #setDefaultState(java.lang.String)
     */
    void loadProperties(PersonProperty propertyManager) {
        if (this.defaultState == null) {
            this.properties = ChannelProperties.load(m_name, propertyManager);
        } else {
            this.properties = ChannelProperties.load(m_name, propertyManager, this.defaultState);
        }
    }

    /**
     * Creates a new action bar with a single action band
     */
    private void createActionBar() {
        try {
            m_actionBar = new Toolbar();
            m_actionBar.setStyle("action");
            m_actionBar.setWidth("100%");
            m_actionBand = m_actionBar.addBand("action");
            m_actionBand.setShowLabels(true);
        } catch (ToolbarException te) {
            m_actionBar = null;
            m_actionBand = null;
        }
    }

    /**
     * Creates a new channel bar with a single channel band
     */
    private void createChannelBar() {
        try {
            m_channelBar = new Toolbar();
            m_channelBar.setStyle("channel");
            m_channelBand = m_channelBar.addBand("channel");
            m_channelBand.setShowLabels(true);
        } catch (ToolbarException te) {
            m_channelBar = null;
            m_channelBand = null;
        }
    }

    /**
     * Returns the absolute value if not null or the value for propertyName.
     * @param propertyName the property name to get the value for
     * @param absoluteValue the value to return if present
     */
    private static String getProperty(String propertyName, String absoluteValue) {
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

}
