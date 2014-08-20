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

package net.project.gui.toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.project.ProjectSpace;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
 * HTML toolbar A toolbar consists of bands, a band consists of buttons. Those buttons are predefined against the band,
 * but may be customized. The toolbar classes are implement as a taglib.
 */
public class Toolbar implements java.io.Serializable, net.project.persistence.IXMLPersistence {
	/** Default width */
	private static String DEFAULT_WIDTH = "100%";

	/** Alignment values */
	private static String ALIGN_LEFT = "left";

	private static String ALIGN_RIGHT = "right";

	private static String ALIGN_CENTER = "center";

	/** Toolbar style dictates the HTML generated for the presentaiton */
	private String style = null;

	/** Show all buttons */
	private boolean showAll = false;

	/** Show all button labels */
	private boolean showLabels = true;

	private boolean showImages = false;

	private boolean showVertical = true;

	/** Enable all buttons */
	private boolean enableAll = false;

	/** Left Title of page to be displayed by certain "style" settings */
	private String leftTitle = null;

	/** Left Title token */
	private String leftTitleToken = null;

	/** RightTitle of page to be displayed by certain "style" settings */
	private String rightTitle = null;

	/** Right Title token */
	private String rightTitleToken = null;

	private String groupTitle = null;
	
	private String subTitle = null;

	/** Escape the title text on output */
	private boolean escapeTitle = false;

	/** Path to stylesheet for custom styles */
	private String stylesheetPath = null;

	/** Width of drawn toolbar (where applicable) */
	private String width = null;

	/** alignment of bands in toolbar */
	private String align = null;

	/** bands containing buttons in this toolbar */
	private HashMap bands = null;

	/** Order of display of bands */
	private ArrayList bandOrder = null;

	/** Fixed position for the bottom action bar */
	private boolean _bottomFixed = false;
	
	private String space = null;
	
	/** Image path **/
	private String imagePath;
	
	/** Checked for project list page **/
	private boolean isProjectListPage;

	/** Check for display space details in left side action bar **/
	private boolean showSpaceDetails;
	/**
	 * Creates new toolbar
	 */
	public Toolbar() {
		bands = new HashMap();
		bandOrder = new ArrayList();
		setWidth(DEFAULT_WIDTH);
		align = ALIGN_RIGHT;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public void setShowImages(boolean showImages) {
		this.showImages = showImages;
	}

	public void setShowVertical(boolean showVertical) {
		this.showVertical = showVertical;
	}

	public void setEnableAll(boolean enableAll) {
		this.enableAll = enableAll;
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

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public void setRightTitleToken(String rightTitleToken) {
		this.rightTitleToken = rightTitleToken;
	}

	public void setEscapeTitle(boolean escapeTitle) {
		this.escapeTitle = escapeTitle;
	}

	public void setStylesheet(String stylesheetPath) {
		this.stylesheetPath = stylesheetPath;
	}

	public void setWidth(String width) {
		this.width = width;
	}
	
	/**
	 * @param spaceType the spaceType to set
	 */
	public void setSpace(String space) {
		this.space = space;
	}

	public void setAlign(String align) throws ToolbarException {
		if (ALIGN_LEFT.equals(align) || ALIGN_RIGHT.equals(align) || ALIGN_CENTER.equals(align)) {
			this.align = align;
		} else {
			throw new ToolbarException("Invalid value for align attribute: " + align);
		}
	}

	public void setBottomFixed(boolean bottomFixed) {
		_bottomFixed = bottomFixed;
	}

	/**
	 * Add a band with specified name to the toolbar
	 * 
	 * @param name
	 *            the name of the band
	 * @return the band that was added
	 * @throws ToolbarException
	 *             if there is problem adding the band, for example if the named band does not exist
	 * @see net.project.gui.toolbar.Band#ACTION
	 * @see net.project.gui.toolbar.Band#CHANNEL
	 * @see net.project.gui.toolbar.Band#DISCUSSION
	 * @see net.project.gui.toolbar.Band#DOCUMENT
	 * @see net.project.gui.toolbar.Band#TRASHCAN
	 * @see net.project.gui.toolbar.Band#STANDARD
	 */
	public Band addBand(String name) throws ToolbarException {
		Band band = new Band();
		band.setName(name);
		band.setShowAll(this.showAll);
		band.setShowLabels(this.showLabels);
		band.setShowImages(this.showImages);
		band.setEnableAll(this.enableAll);
		bands.put(name, band);
		bandOrder.add(name);
		return band;
	}

	/**
	 * Return the html presentation of the toolbar. The presentation is based on the toolbar style or stylesheet.
	 * 
	 * @return the html presentation of the toolbar
	 * @throws ToolbarException
	 *             if there is a problem returning the presentation, for example if the style or stylesheet is not set
	 *             or is invalid.
	 */
	public String getPresentation() throws ToolbarException {
		String presentation = null;
		if ((style == null) && (stylesheetPath == null)) {
			throw new ToolbarException("Either style or stylesheet path required to display toolbar.");
		}
		if (style != null) {
			if (style.equals("action")) {
				presentation = getActionbarPresentation();
			} else if (style.equals("tool")) {
				presentation = getToolbarPresentation();
			} else if (style.equals("tooltitle")) {
				presentation = getToolbarTitlePresentation();
			} else if (style.equals("channel")) {
				presentation = getChannelbarPresentation();
			} else {
				throw new ToolbarException("Invalid toolbar style: " + style);
			}
		} else {
			presentation = getStylesheetPresentation();
		}
		return presentation;
	}

	/**
	 * Return default "action bar" style presentation
	 * 
	 * @return the presentation HTML
	 */
	private String getActionbarPresentation() {
		Iterator it = null;
		Iterator buttonIt = null;
		Band band = null;
		Button button = null;
		ArrayList buttons = null;
		StringBuffer buffer = new StringBuffer(1024);
		String leftTitle = null;

		/* Start of action bar table */
		buffer.append("\n<div style=\"clear: both;\"></div><table width=\"" + this.width
				+ "\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		buffer.append("\n<tr class=\"actionBar\">");
		buffer.append("\n<td width=\"1%\" class=\"actionBar\"><img src=\"" + SessionManager.getJSPRootURL()
				+ "/images/icons/actionbar-left_end.gif\" width=\"8\" height=\"27\" alt=\"\" border=\"0\"></td>");

		// Insert a left title if there is one
		leftTitle = getProperty(this.leftTitleToken, this.leftTitle);

		if (leftTitle != null) {
			buffer.append("\n<td class=\"actionBar\" align=\"left\">");
			if (this.escapeTitle) {
				buffer.append(HTMLUtils.escape(leftTitle));
			} else {
				buffer.append(leftTitle);
			}
			buffer.append("</td>");
		}

		// Begin inserting the buttons
		buffer.append("\n<td class=\"actionBar\" align=\"" + this.align + "\">&nbsp;\n");

		/* Get band elements and display buttons for each */
		it = bandOrder.iterator();
		while (it.hasNext()) {
			band = ((Band) bands.get(it.next()));

			/* Get button elements from band and display them */
			buttons = band.getButtons();
			Collections.sort(buttons);
			buttonIt = buttons.iterator();
			while (buttonIt.hasNext()) {
				button = ((Button) buttonIt.next());
				if (button.isShow()) {
					buffer.append("\t<nobr>&nbsp;&nbsp;&nbsp;");

					// Insert hyperlink if button is enabled
					if (button.isEnable()) {
						buffer.append("<a href=\"").append(button.getFunction()).append("\" ");
						if (button.getTarget() != null) {
							buffer.append("target=\"").append(button.getTarget()).append("\" ");
						}
						buffer.append("class=\"channelNoUnderline\">");
					}

					// Insert label text if button has left label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_LEFT)) {
						buffer.append(button.getResolvedLabel());
						buffer.append("&nbsp;");
					}

					// Insert image
					buffer.append("<img src=\"");
					buffer.append(SessionManager.getJSPRootURL() + button.getResolvedImageEnabled());
					buffer.append("\" width=\"27\" height=\"27\" ");

					// Use the alt as the title, since the original (although misguided)
					// usage of alt was as a tooltip
					// Netscape 7.x doesn't display alts as tooltips; only titles
					buffer.append("alt=\"").append(button.getResolvedAlt()).append("\" ");
					buffer.append("title=\"").append(button.getResolvedAlt()).append("\" ");
					buffer.append("border=\"0\" align=\"absmiddle\"/>");

					// Insert label text if button has right label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_RIGHT)) {
						buffer.append("&nbsp;");
						buffer.append(button.getResolvedLabel());
					}

					// Close hyperlink if button is enabled
					if (button.isEnable()) {
						buffer.append("</a>");
					}

					buffer.append("</nobr>\n");
				} // end if button.isShow()
			}
		}
		buffer.append("</td>");
		buffer.append("\n<td width=\"1%\" align=\"right\" class=\"actionBar\"><img src=\""
				+ SessionManager.getJSPRootURL()
				+ "/images/icons/actionbar-right_end.gif\" width=\"8\" height=\"27\" alt=\"\" border=\"0\"></td>");
		buffer.append("\n</tr>");
		buffer.append("\n</table>");

		final String htmlString = buffer.toString();
		// closed div for the content div
		return _bottomFixed ? htmlString + "</div>" : htmlString;
	}

	/**
	 * Return default "toolbar" style presentation
	 * 
	 * @return presentation string
	 */
	private String getToolbarPresentation()  throws ToolbarException{
		Iterator it = null;
		Iterator buttonIt = null;
		Band band = null;
		Button button = null;
		StringBuffer buffer = new StringBuffer(1024);
		String useImage = null; // Image to use for button

		/* Get band elements and display buttons for each */
		it = bandOrder.iterator();
		
		if(bandOrder.size() > 0){
			buffer.append("<div id='left-navbar'>\n");
			
			if (groupTitle != null && space == null) {
				if(getProjectListPage()){
					buffer.append("<div id='leftheading-" + SpaceTypes.PROJECT_SPACE + "'>"
							+ groupTitle + "</div>");	
				}	else {
					buffer.append("<div id='leftheading-" + SessionManager.getUser().getCurrentSpace().getType() + "'>"
							+ groupTitle + "</div>");
				}
				buffer.append("<div style='clear: both'></div>");
			}

			if(space != null){
				buffer.append("<div id='leftheading-" + space + "'>"
						+ groupTitle + "</div>");
				buffer.append("<div style='clear: both'></div>");
			}
			
			if(imagePath != null){
				buffer.append("<div class='profile-photo'><img id='personImage' width='110px' src='"+imagePath+"'/></div>");
				buffer.append("<div style='clear: both'></div>");
			}
			
			// To display current space details in left side tool bar
			if(SessionManager.getUser().getCurrentSpace().isTypeOf(Space.PROJECT_SPACE) && !isProjectListPage && showSpaceDetails){
				buffer.append(((ProjectSpace)SessionManager.getUser().getCurrentSpace()).getProjectSpaceDetails());
			} else if(StringUtils.isNotEmpty(subTitle)){
				buffer.append("<br><div  id=\"leftSpaceName\" style=\"margin-top: 15px;\" class=\"project-title\">" + subTitle + "</div><br clear=\"both\"/> ");
			}
			
			if (showVertical) {
				buffer.append("<div class='spacer-for-toolbox'></div>\n");
				buffer.append("<div class='toolbox-heading'>"+PropertyProvider.get("prm.global.toolbox.heading")+"</div>\n");
	
			}
			while (it.hasNext()) {
				band = ((Band) bands.get(it.next()));
				if (band.getName().equals(Band.STANDARD)){
					showImages = true;
					Space currentSpace = SessionManager.getUser().getCurrentSpace();
					 if (PropertyProvider.getBoolean("prm.blog.isenabled")
							&& ((StringUtils.isNotEmpty(space) && (space.equalsIgnoreCase(SpaceTypes.PERSONAL_SPACE) || space.equalsIgnoreCase(SpaceTypes.PROJECT_SPACE)))
									|| (StringUtils.isEmpty(space) && (currentSpace.isTypeOf(SpaceTypes.PERSONAL_SPACE) || currentSpace.isTypeOf(SpaceTypes.PROJECT_SPACE))))) {
						 band.addButton("blogit");
					 } else if(getProjectListPage()){
						 band.addButton("blogit");
					 }
				}
				if (band.getGroupHeading() == null)
					buffer.append("<div id='toolbox-item' class='toolbox-item' >");
				else {
					buffer.append("<div id='toolbox-heading' class='toolbox-heading' >");
					buffer.append(band.getGroupHeading() + "</div>");
					buffer.append("<div id='toolbox-item' class='toolbox-item' >");
				}
	
				/* Get button elements from band and display them */
				buttonIt = band.getButtons().iterator();
				while (buttonIt.hasNext()) {
					button = ((Button) buttonIt.next());
	
					// Don't display sharing toolbar buttons if they aren't necessary
					if (!PropertyProvider.getBoolean("prm.crossspace.isenabled", false)) {
						if ((button.getType() == ButtonType.ADD_EXTERNAL) || (button.getType() == ButtonType.SHARE)) {
							continue;
						}
					}
	
					if (button.isShow()) {
	
						// Insert hyper link if button is enabled
						if (button.isEnable()) {
                            
                            if (button.getName().equals("blogit")) {
                                buffer.append("<span id=\"blog-ItEnabled\" >");
                            } else {
                                buffer.append("<span>");
                            }
                            buffer.append("<a href=\"" + button.getFunction() + "\" ");
							if (showVertical)
								if (button.getTarget() != null) {
									buffer.append("target=\"").append(button.getTarget()).append("\" ");
								}
							if (showImages && PropertyProvider.getBoolean("prm.global.actions.icon.isenabled")) {
								useImage = button.getResolvedImageEnabled();
								if (useImage != null) {
									buffer.append("onmouseout=\" document.img" + button.getName() + ".src = '"
											+ SessionManager.getJSPRootURL() + button.getResolvedImageEnabled() + "'\" ");
									buffer.append("onmouseover=\" document.img" + button.getName() + ".src = '"
											+ SessionManager.getJSPRootURL() + button.getResolvedImageOver() + "'\"");
								}
							}
							buffer.append(">");
						} else {
							if (showImages && PropertyProvider.getBoolean("prm.global.actions.icon.isenabled"))
								useImage = button.getResolvedImageDisabled();
						}
	
						if (showImages && useImage != null && PropertyProvider.getBoolean("prm.global.actions.icon.isenabled")) {
							// Insert image
							buffer.append("<img ");
	
							// Use the alt as the title, since the original (although misguided)
							// usage of alt was as a tooltip
							// Netscape 7.x doesn't display alts as tooltips; only titles
							buffer.append("alt=\"").append(button.getResolvedAlt()).append("\" ");
							buffer.append("title=\"").append(button.getResolvedAlt()).append("\" ");
							buffer.append("border=0 hspace=0 src=\"" + SessionManager.getJSPRootURL() + useImage
									+ "\" name=\"img" + button.getName() + "\">");
							buffer.append("&nbsp;");
						}
	
						// Insert label if right label
							buffer.append(button.getResolvedLabel());
	
						// Close hyperlink if button is enabled
						if (button.isEnable()) {
							buffer.append("</a></span>");
						}
	
						if (showVertical)
							buffer.append("<br/>\n");
						else
							buffer.append("\n");
					} // end if button.isShow()
				}
				buffer.append("</div>\n");
			}
			if (showVertical) {
				buffer.append("</div>\n");
			}
			buffer.append("&nbsp;");
		}

		return buffer.toString();
	}

	/**
	 * Draw toolbar and include title
	 * 
	 * @return the presentation HTML
	 */
	private String getToolbarTitlePresentation() throws ToolbarException {
		StringBuffer buffer = new StringBuffer(1024);
		String leftTitle = null;
		String rightTitle = null;

		// buffer.append("\t<tr><td colspan=\"2\" align=\"" + this.align + "\" valign=\"bottom\">");
		// buffer.append(getToolbarPresentation());
		// buffer.append("</td></tr>\n");
		buffer.append("<table width=\"" + this.width + "\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
		/*buffer.append("\t<tr><td colspan=\"2\" align=\"" + this.align + "\" valign=\"bottom\">");
		buffer.append("&nbsp;");
		buffer.append("</td></tr>\n");*/
		buffer.append("\t<tr><td align=\"left\" class=\"pageTitle\">");

		leftTitle = getProperty(this.leftTitleToken, this.leftTitle);
		if (leftTitle != null) {
			if (this.escapeTitle) {
				buffer.append(HTMLUtils.escape(leftTitle));
			} else {
				buffer.append(leftTitle);
			}
		} else {
			buffer.append("&nbsp;");
		}
		buffer.append("</td>");
		buffer.append("<td align=\"right\" class=\"pageTitle\">");

		rightTitle = getProperty(this.rightTitleToken, this.rightTitle);
		if (rightTitle != null) {
			if (this.escapeTitle) {
				buffer.append(HTMLUtils.escape(rightTitle));
			} else {
				buffer.append(rightTitle);
			}
		} else {
			buffer.append("&nbsp;");
		}
		buffer.append("</td>");
		buffer.append("</tr></table>\n");

		final String b = buffer.toString();
		if (bands != null) {
			return getToolbarPresentation() + addFixationForToolbarBlock(b);
		}

		return b;
	}

	/**
	 * Add fixation for toolbar
	 * 
	 * @return the presentation HTML
	 */
	private String addFixationForToolbarBlock(String blockHTMLPresentation) {
		return "\n<div id='fixedRightTop'>\n" + blockHTMLPresentation + "\n</div>\n";
	}

	/**
	 * Draw toolbar in "channel" style
	 * 
	 * @return the presentation HTML
	 */
	private String getChannelbarPresentation() {
		Iterator it = null;
		Iterator buttonIt = null;
		Band band = null;
		Button button = null;
		StringBuffer buffer = new StringBuffer(1024);
		String useImage = null; // Image to use for button

		buffer.append("<span class=\"channelHeader\">");

		/* Get band elements and display buttons for each */
		it = bandOrder.iterator();
		while (it.hasNext()) {
			band = ((Band) bands.get(it.next()));

			/* Get button elements from band and display them */
			buttonIt = band.getButtons().iterator();
			while (buttonIt.hasNext()) {
				button = ((Button) buttonIt.next());
				if (button.isShow()) {
					buffer.append("<nobr>");

					// Insert spacer on left side if button will have left label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_LEFT)) {
						buffer.append("&nbsp;");
					}

					// Insert hyper link if button is enabled
					if (button.isEnable()) {
						/* Enable Image */
						buffer.append("<a href=\"" + button.getFunction() + "\" ");
						if (button.getTarget() != null) {
							buffer.append("target=\"").append(button.getTarget()).append("\" ");
						}
						buffer.append("class=\"channelNoUnderline\">");
						useImage = button.getResolvedImageEnabled();
					} else {
						/* Disable Image - use the enable image if there is no disabled image */
						useImage = button.getResolvedImageDisabled();
						if (useImage == null) {
							useImage = button.getResolvedImageEnabled();
						}
					}

					// Insert label if left label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_LEFT)) {
						//buffer.append(button.getResolvedLabel());
					}

					// Insert image
					buffer.append("<img ");

					// Use the alt as the title, since the original (although misguided)
					// usage of alt was as a tooltip
					// Netscape 7.x doesn't display alts as tooltips; only titles
					buffer.append("alt=\"").append(button.getResolvedAlt()).append("\" ");
					buffer.append("title=\"").append(button.getResolvedAlt()).append("\" ");
					buffer.append("height=\"15\" border=\"0\" hspace=\"0\" vspace=\"0\" align=\"top\" src=\""
									+ SessionManager.getJSPRootURL()
									+ getImagePathForCurrentSpace(useImage)
									+ "\" name=\"img"
									+ button.getName()
									+ "\">");

					// Insert label if right label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_RIGHT)) {
						//buffer.append(button.getResolvedLabel());
					}

					// Close hyperlink if button is enabled
					if (button.isEnable()) {
						buffer.append("</a>");
					}

					// Insert spacer on right side if button has right label
					if (button.isShowLabel() && button.getLabelPos().equals(Button.LABEL_POS_LEFT)) {
						buffer.append("&nbsp;");
					}

					buffer.append("</nobr>");
				}
			}
		}
		buffer.append("</span>");

		return buffer.toString();
	}

	/**
	 * Returns the absolute value if not null or the value for propertyName.
	 * 
	 * @param propertyName
	 *            the property name to get the value for
	 * @param absoluteValue
	 *            the value to return if present
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

	/**
	 * Draw toolbar with custom transformation <br />
	 * setStylesheet should be called prior to this (i.e. stylesheet property set)
	 * 
	 * @return the presentation string
	 */
	private String getStylesheetPresentation() {
		XMLFormatter xmlFormatter = new XMLFormatter();
		xmlFormatter.setStylesheet(this.stylesheetPath);
		return xmlFormatter.getPresentation(getXML());
	}

	/**
	 * Return toolbar XML body including version tag
	 * 
	 * @return the xml string
	 */
	public String getXML() {
		return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
	}

	/**
	 * Return toolbar XML body without version tag
	 * 
	 * @return the xml string
	 */
	public String getXMLBody() {
		StringBuffer xml = new StringBuffer();
		Iterator it = null;

		xml.append("<toolbar>");
		xml.append("<left_title>"
				+ (this.escapeTitle ? getProperty(this.leftTitleToken, this.leftTitle) : XMLUtils.escape(getProperty(
						this.leftTitleToken, this.leftTitle))) + "</left_title>");
		xml.append("<right_title>"
				+ (this.escapeTitle ? getProperty(this.rightTitleToken, this.rightTitle) : XMLUtils.escape(getProperty(
						this.rightTitleToken, this.rightTitle))) + "</right_title>");
		xml.append("<width>" + this.width + "</width>");
		it = bandOrder.iterator();
		while (it.hasNext()) {
			xml.append(((Band) bands.get(it.next())).getXMLBody());
		}
		xml.append("</toolbar>");
		return xml.toString();
	}
	
	/**
	 * Method returns the image path for current space.
	 * 
	 * @param imagePath
	 * @return changed imagePath
	 */
	public String getImagePathForCurrentSpace(String imagePath) {
		Space space = SessionManager.getUser().getCurrentSpace();
		
		if (imagePath.contains("icons") && imagePath.contains("channelbar")) {
			imagePath = imagePath.replaceAll("icons", space.isTypeOf(SpaceTypes.PERSONAL_SPACE) ? "personal" : space.getType());
		}
		
		if (space.isTypeOf(SpaceTypes.METHODOLOGY_SPACE)) {
			imagePath = imagePath.replaceAll(SpaceTypes.METHODOLOGY_SPACE, SpaceTypes.PROJECT_SPACE);
		} else if (space.isTypeOf(SpaceTypes.CONFIGURATION_SPACE)) {
			imagePath = imagePath.replaceAll(SpaceTypes.CONFIGURATION_SPACE, SpaceTypes.APPLICATION_SPACE);
		}
		
		return imagePath;
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
	 * @return the getProjectListPage
	 */
	public boolean getProjectListPage() {
		return isProjectListPage;
	}

	/**
	 * @param getProjectListPage the ProjectListPage to set
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
	
}
