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
 |    $Revision: 19120 $
 |    $Date: 2009-04-24 14:44:27 -0300 (vie, 24 abr 2009) $
 |    $Author: avinash $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.taglibs.template;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.util.Version;

public class GetSpaceJSTag extends TagSupport {

    private static String JS_PATH_PREFIX = "/src/nav_";

    private static String JS_FILE_EXTENSION = ".js";

    private static final String TYPE_JAVASCRIPT_SRC_OPEN_TAG = "<script language=\"javascript\" src=\"";
    private static final String TYPE_JAVASCRIPT_SRC_CLOSE_TAG = "\"></script>";
    private static final String VERSION_NUMBER = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
    
    private String space = null;
    
    /* -------------------------------  Constructors  ------------------------------- */

    public GetSpaceJSTag() {
        super();
    }

    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setSpace(String space) {
        this.space = space;
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {
        JspWriter out = pageContext.getOut();
        //ImportTag importTag = new ImportTag();
        
        if (this.space == null) {
            determineSpace();
        }
        //importTag.setSrc(makeJSPath(this.space));
        
        try {
            //importTag.printOutput(out);
            includeJs(out, "/src/extjs/adapter/ext/ext-base.js");
            includeJs(out, "/src/extjs/ext-all.js");
            includeJs(out, "/src/ext-components.js");
            includeJs(out, "/src/blogit.js");
            includeJs(out, "/src/sessionHook.js");
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        } finally {
        	this.space = null;
        }

        return SKIP_BODY;
    }

    private void includeJs(JspWriter out, String jsFile) throws IOException{
        out.print(TYPE_JAVASCRIPT_SRC_OPEN_TAG);
        out.print(SessionManager.getJSPRootURL() + jsFile);
        out.print("?" + VERSION_NUMBER);
        out.println(TYPE_JAVASCRIPT_SRC_CLOSE_TAG);
    }
    
    public void release() {
        this.space = null;
        super.release();
    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */

    public String makeJSPath(String src) {
        return (JS_PATH_PREFIX + src + JS_FILE_EXTENSION);
    }

    /**
     * Determines the current user's space, where the user is fetched from
     * session scope.  Sets the <code>space</code> attribute.
     * @throws JspTagException if there is a problem finding the user in the sesion
     */
    private void determineSpace() throws JspTagException {
        User user = null;
        String spaceType = null;

        user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
        if (user == null || user.getCurrentSpace() == null) {
        	spaceType = Space.PERSONAL_SPACE;
        } else {
        	spaceType = user.getCurrentSpace().getType();
        }

        // Now set the space based on the space type
        if (spaceType.equals(Space.PERSONAL_SPACE)) {
            this.space = "personal";

        } else if (spaceType.equals(Space.PROJECT_SPACE)) {
            this.space = "project";

        } else if (spaceType.equals(Space.BUSINESS_SPACE)) {
            this.space = "business";

        } else if (spaceType.equals(Space.METHODOLOGY_SPACE)) {
            this.space = "methodology";

        } else if (spaceType.equals(Space.APPLICATION_SPACE)) {
            this.space = "application";

        } else if (spaceType.equals(Space.CONFIGURATION_SPACE)) {
            this.space = "configuration";

        } else if (spaceType.equals(Space.ENTERPRISE_SPACE)) {
            this.space = "enterprise";
            
        } else if (spaceType.equals(Space.RESOURCES_SPACE )) {
            this.space = "resource";
        } else {
            throw new JspTagException("Error in GetSpaceJSTag: Unhandled space type '" + spaceType + "'.");

        }
    }

    private String getIncludePathPrefix() throws JspTagException {
        final User user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
        if (user == null || user.getCurrentSpace() == null) {
        	return "personal";
        }
        
        final String spaceType = user.getCurrentSpace().getType();
        if (spaceType.equals(ISpaceTypes.PERSONAL_SPACE)) {
            return "personal";
        } else if (spaceType.equals(ISpaceTypes.PROJECT_SPACE)) {
            return "project";
        } else if (spaceType.equals(ISpaceTypes.BUSINESS_SPACE)) {
            return "business";
        } else if (spaceType.equals(ISpaceTypes.METHODOLOGY_SPACE)) {
            return "methodology";
        } else if (spaceType.equals(ISpaceTypes.APPLICATION_SPACE)) {
            return "admin";
        } else if (spaceType.equals(ISpaceTypes.CONFIGURATION_SPACE)) {
            return "configuration";
        } else if (spaceType.equals(ISpaceTypes.ENTERPRISE_SPACE)) {
            return "enterprise";
        } else if (spaceType.equals(Space.RESOURCES_SPACE )) {
        	return "resource";
        } else {
            throw new JspTagException("Error in GetSpaceNavBarTag: Unhandled space type '" + spaceType + "'.");
        }
    }

}
