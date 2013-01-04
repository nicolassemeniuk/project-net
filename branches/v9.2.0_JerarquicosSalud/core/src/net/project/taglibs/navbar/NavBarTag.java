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

 /*--------------------------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.taglibs.navbar;

import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.gui.navbar.NavBarItem;
import net.project.gui.navbar.NavbarMenuItem;
import net.project.security.SessionManager;
import net.project.xml.XMLFormatException;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * <code>NavBarTag</code> is the constructor for a left navigation bar.  This tag maintains
 * a data structure to which subitems will add "NavbarMenuItems".
 *
 * After all subtags have been processed, this class will generate XML from all of the
 * NavbarMenuItem's and will use the XSLT process to generate HTML.  That html will be
 * written to the tag.
 *
 * @author Matthew Flower
 * @version 1.0
 * @since Gecko
 */
public class NavBarTag extends NavBarTagItem {
    /** Root node of the menu tree */
    private NavbarMenuItem menuTree = new NavbarMenuItem();
    /** Location of the xsl file that will render this menu into HTML.*/
    private String stylesheet;
    /** Location of the spacer gif (used to create space around text and images */
    private String spacerImage;
    /** Location of the small ball image */
    private String smallBallImage;
    /** Location of the medium ball image */
    private String mediumBallImage;
    /** Location of the top image. */
    private String topImage = null;
    /** Location of the bottom image. */
    private String bottomImage = null;

    /** Whether or not we are going to incldue the xml for search */
    private boolean displaySearchTool = false;
    /** The string that includes the xml (assigned by a subtag for search */
    private String searchXMLString;

    /**
     * set the SearchXML that will be used to render the search tool.
     *
     * @param searchXMLString a <code>String</code> value that contains the XML
     * to render the search toolbar.
     */
    public void setSearchXMLString(String searchXMLString) {
        this.searchXMLString = searchXMLString;
    }

    /**
     * Sets a value that determines whether or not we are going to display
     * the search toolbar.
     *
     * @param displaySearchTool a <code>boolean</code> value that determines
     * whether or not we are going to display the toolbar.
     */
    public void setDisplaySearchTool(boolean displaySearchTool) {
        this.displaySearchTool = displaySearchTool;
    }
    
    /**
     * Gets the value of spacerImage - the URL of an image that is used for
     * formatting on an HTML page.
     *
     * @see #setSpacerImage
     * @return the value of spacerImage
     */
    public String getSpacerImage() {
        return this.spacerImage;
    }

    /**
     * Sets the url or property that will decide what image is going to be used
     * as the spacer image.  This image is used to manually set the height and
     * width in layout.  It is generally a transparent gif with a single pixel.
     *
     * @see #getSpacerImage
     * @param argSpacerImage Path to the spacer.gif image.  This can be
     * either a token or an image path.  If it's a token, prefix it with an
     * "@" sign.
     */
    public void setSpacerImage(String argSpacerImage) {
        //Check to see if the user has sent us a property to be looked up with PropertyProvider
        if (argSpacerImage.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.spacerImage = PropertyProvider.get(argSpacerImage);
        } else {
            //The value sent to us should just be a normal URL
            this.spacerImage = argSpacerImage;
        }
    }

    /**
     * Gets the url of the "SmallBallImage" that will use when we render the nav bar.
     *
     * @see #setSmallBallImage
     * @return the value of smallBallImage
     */
    public String getSmallBallImage() {
        return this.smallBallImage;
    }

    /**
     * Sets the image path to the "small ball" picture.  (Used on the
     * left side of submenus.)
     *
     * @see #getSmallBallImage
     * @param argSmallBallImage Path to the small ball image.  This can be
     * either a token or an image path.  If it's a token, prefix it with an
     * "@" sign.
     */
    public void setSmallBallImage(String argSmallBallImage) {
        if (argSmallBallImage.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.smallBallImage = PropertyProvider.get(argSmallBallImage);
        } else {
            this.smallBallImage = argSmallBallImage;
        }
    }

    /**
     * Gets the url of the medium-sized ball image that we will use to render the
     * nav bar.  The medium ball image is used to display the "balls" to the left
     * side of top-level menu bars.
     *
     * @see #setMediumBallImage
     * @return the value of mediumBallImage
     */
    public String getMediumBallImage() {
        return this.mediumBallImage;
    }

    /**
     * Sets the image path to the "medium ball" picture.  (Used on the left
     * side of top-level menu items.)
     *
     * @see #getMediumBallImage
     * @param argMediumBallImage Path to the medium ball image.  This can be
     * either a token or an image path.  If it's a token, prefix it with an
     * "@" sign.
     */
    public void setMediumBallImage(String argMediumBallImage) {
        if (argMediumBallImage.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.mediumBallImage = PropertyProvider.get(argMediumBallImage);
        } else {
            this.mediumBallImage = argMediumBallImage;
        }
    }

    /**
     * Sets the top image in the navbar.
     * This image is displayed at the top of the navbar across the entire width
     * of the navbar.
     * @param topImage the token name that specifies the image or the specification
     * itself.  This is expected to be a relative URl to the image.
     * @see #getTopImage
     */
    public void setTopImage(String topImage) {
        if (topImage == null || topImage.trim().length() == 0) {
            this.topImage = null;
        } else if (topImage.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.topImage = PropertyProvider.get(topImage);
        } else {
            this.topImage = topImage;
        }
    }

    /**
     * Returns the top image displayed in the navbar.
     * @return the top image
     * @see #setTopImage
     */
    private String getTopImage() {
        return this.topImage;
    }

    /**
     * Sets the bottom image in the navbar.
     * This image is displayed at the bottom of the navbar across the entire width
     * of the navbar.
     * @param bottomImage the token name that specifies the image or the specification
     * itself.  This is expected to be a relative URl to the image.
     * @see #getBottomImage
     */
    public void setBottomImage(String bottomImage) {
        if (bottomImage == null || bottomImage.trim().length() == 0) {
            this.bottomImage = null;
        } else if (bottomImage.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.bottomImage = PropertyProvider.get(bottomImage);
        } else {
            this.bottomImage = bottomImage;
        }
    }

    /**
     * Returns the bottom image in the navbar.
     * @return the bottom image
     * @see #setBottomImage
     */
    private String getBottomImage() {
        return this.bottomImage;
    }

    /**
     * Set the location of the xsl file that will render this menu into html.
     *
     * @see #getStylesheet
     * @param stylesheet a <code>String</code> value
     */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Get the location of the xsl file that will render this menu into html.
     *
     * @see #setStylesheet
     * @return a <code>String</code> value
     */
    public String getStylesheet() {
        return this.stylesheet;
    }
    
    /**
         * Called prior to evaluating the body of this tag. 
         * @return <code>EVAL_BODY_BUFFERED</code> to indicate that
         *         the body of the action should be evaluated.
         * @exception JspTagException
         *                    if an error occurs
         */
    public int doStartTag() throws JspTagException {
         return EVAL_BODY_BUFFERED;
    }

    /**
     * Called immediately after the processing of everything inside of the navbar
     * tag.  This method is responsible for generating the HTML that will actually be
     * seen on the client side.
     *
     * @return an <code>int</code> value that is defined by the Taglibs (J2EE) spec.
     * @exception JspTagException if the writer cannot be found or if the HTML cannot
     * be properly generated.
     */
    public int doAfterBody() throws JspTagException {
        try {
            //This is the writer that we need to use to write out HTML.
            JspWriter out = getBodyContent().getEnclosingWriter();
            //Get the presentation and print it out.
            out.print(getHTML());
        } catch (java.io.IOException ioe) {
        	Logger.getLogger(NavBarTag.class).error("NavBarTag.doAfterBody threw an IOException: "+
                                       ioe);
            throw new JspTagException(ioe.toString());
        } catch (XMLFormatException fe) {
        	Logger.getLogger(NavBarTag.class).error("NavBarTag.doAfterBody threw an XMLFormatException: "+
                                       fe);
            throw new JspTagException(fe.toString());
        } catch (PnetException pe) {
        	Logger.getLogger(NavBarTag.class).error("Caught a PnetException throw in NavBarTag.doAfterBody: "+
                                       pe);
            throw new JspTagException(pe.toString());
        }
        return(SKIP_BODY);
    }

    /**
     * Automatically called at the end of processing the tag.  This method is implemented
     * so that we can clean out all of the private member variables prior to calling it
     * again.
     *
     * @return an <code>int</code> value defined by the taglib spec.
     * @exception JspTagException can be thrown according to the spec.  None of the
     * code that I have implemented will throw this.  
     */
    public int doEndTag() throws JspTagException {
        //Prepare this class to be pooled or reused.
        clear();
        return EVAL_PAGE;
    }

    /**
     * Add child allows any item that implements the NavBarItem interface to add
     * themselves to the menu.
     *
     * @param newChild a <code>NavBarItem</code> object that will be rendering
     * its own XML for display in the menu.
     */
    public void addChild(NavBarItem newChild) {
        menuTree.addChild(newChild);
    }
    
    /**
     * Get the HTML representation of the NavBar.  This is done by generating the XML
     * for this method then applying the XSLT stylesheet to it.
     *
     * @return a <code>String</code> value which contains the entire HTML of this page
     * @exception XMLFormatException if the XML generated by getXML() is somehow errorneous
     * and cannot be used to render HTML from XSLT.  This error should never really occur.
     */
    public String getHTML() throws XMLFormatException, PnetException {
        XMLFormatter xmlFormatter = new XMLFormatter();
        xmlFormatter.setStylesheet(stylesheet);
        String xml = getXML();
        xmlFormatter.setXML(xml);

        try { 
            return xmlFormatter.getPresentation();
        } catch (XMLFormatException fe) {
        	Logger.getLogger(NavBarTag.class).error("XML returned in NavBarTag.getXML() was "+
                                       "of an invalid format.  Error returned: "+
                                       fe);
            throw fe;
        }
    }

    /**
     * Produces the xml for this tag, it's embedded search tag, and all of the embedded
     * menu tags recursively.
     * The XML may be empty if the navbar is not to be displayed.
     *
     * @return a <code>String</code> value
     * @see net.project.taglibs.navbar.NavBarTagItem#getDisplayThis
     */
    public String getXML() throws PnetException {
        StringBuffer xml = new StringBuffer();

        xml.append("<MenuList>\n");
        xml.append("  <SpacerImage>").append(SessionManager.getJSPRootURL()+spacerImage).append("</SpacerImage>\n");
        xml.append("  <SmallBallImage>").append(SessionManager.getJSPRootURL()+smallBallImage).append("</SmallBallImage>\n");
        xml.append("  <MediumBallImage>").append(SessionManager.getJSPRootURL()+mediumBallImage).append("</MediumBallImage>\n");
        
        // Top image and bottom image are optional
        // Include in the XML only if they have been specified
        if (getTopImage() != null) {
            xml.append("    <TopImage>").append(SessionManager.getJSPRootURL()+getTopImage()).append("</TopImage>");
        }
        if (getBottomImage() != null) {
            xml.append("    <BottomImage>").append(SessionManager.getJSPRootURL()+getBottomImage()).append("</BottomImage>");
        }
    
        if (getDisplayThis()) {
            /* If there is going to be a search displayed on the page, add the xml that will
               render that search */
            if (displaySearchTool)
                xml.append(searchXMLString);
            
            xml.append(getChildXML(menuTree.children(), 0));
        }
        xml.append("</MenuList>\n");
        return xml.toString();
    }

    /**
     * Recurse through all children of a NavbarMenuItem recursively.  For each item, xml will
     * be generated.
     *
     * @param children an iterator that points to an ArrayList of NavbarItems.  This list
     * will be iterated recursively to produce the xml.
     * @param depth an <code>int</code> value that describes the current recursion depth.  This
     * value is mostly used to indent the xml.  It might sound silly, but seems how we have
     * menus embedded in menus, it makes it difficult to read the XML otherwise.
     * @return a <code>String</code> value containing the xml for the child passed to this
     * method (as well as all recursive elements.)
     */
    private String getChildXML(Iterator children, int depth) throws PnetException {
        StringBuffer xml = new StringBuffer();

        //Calculate how far this node should be indented (in spaces)
        StringBuffer leftPad=new StringBuffer();
        for (int i =0; i<depth; i++)
            leftPad.append("  ");

        //Iterate through all the children recursively and generate their xml.
        NavBarItem child;
        while (children.hasNext()) {
            child = (NavBarItem)children.next();
            xml.append(child.getNavBarXML(depth+1));
        }
        
        //Return the XML that we have generated to the calling method
        return xml.toString();
    }

    /**
     * Clear out the private member variables for this method, causing it to be
     * in the same state that this object was in immediately after creation.  
     * <code>Clear()</code> is called when the tag is finished processing to
     * prepare itself for being pooled.
     */
    private void clear() {
        menuTree = new NavbarMenuItem();
        stylesheet = "";
        spacerImage = "";
        smallBallImage = "";
        mediumBallImage = "";
        this.topImage = null;
        this.bottomImage = null;
        displaySearchTool = false;
        searchXMLString = "";
        setDisplayThis(true);
    }
}
