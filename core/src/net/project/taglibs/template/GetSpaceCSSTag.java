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
 |    $Revision: 20704 $
 |    $Date: 2010-04-15 12:54:27 -0300 (jue, 15 abr 2010) $
 |    $Author: avinash $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.taglibs.template;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.ModuleCollection;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;

public class GetSpaceCSSTag extends TagSupport {

    private String space = null;

    private String path = null;
    private static final String TYPE_CSS_LINK_OPEN_TAG = "<link rel=\"stylesheet\" rev=\"stylesheet\" type=\"text/css\" href=\"";
    private static final String TYPE_CSS_LINK_CLOSE_TAG = "\">";
    private static final String TYPE_JAVASCRIPT_SRC_OPEN_TAG = "<script language=\"javascript\" src=\"";
    private static final String TYPE_JAVASCRIPT_SRC_CLOSE_TAG = "\"></script>";
    private static String JS_PATH_PREFIX = "/src/nav_";
    private static final String TYPE_JAVASCRIPT_OPEN_TAG = "<script language=\"javascript\" type=\"text/javascript\">";
    private static final String TYPE_JAVASCRIPT_CLOSED_TAG = "</script>";

    private static String JS_FILE_EXTENSION = ".js";
    
    private static final String VERSION_NUMBER = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
    
    private ModuleCollection modules = new ModuleCollection();
    
    /* -------------------------------  Constructors  ------------------------------- */

    public GetSpaceCSSTag() {
        super();
        //loadModules();
    }

    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setSpace(String space) {
        this.space = space;
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {
        JspWriter out = pageContext.getOut();
        ImportTag importTag = new ImportTag();
        
        String pathPrefix = pathPrefix = getIncludePathPrefix();
        
        if (this.space == null) {
            determinePath();
            
        } else {
            determinePath(this.space);
        }

        importTag.setSrc(this.path);
        User user = (User)pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
        try {
            if( user  != null && user.getID() != null ) {
            	importTag.printOutput(out);
            }
            /*
            // generate user friendly titles for better history
            Module module = modules.getModule(""+SecurityProvider.getInstance().getCheckedModuleID() );
            String moduleName = module==null ? "" : ( module.getName() +  " - "  ) ;
            String spaceName = user != null ? (user.getCurrentSpace() != null ? user.getCurrentSpace().getName() + " - " : "") : "";
            out.println("<title>" + moduleName + spaceName + " " + PropertyProvider.get("prm.global.application.title") + " </title>");
            */
            
            out.println("<LINK REL='SHORTCUT ICON' HREF='" + SessionManager.getJSPRootURL() + "/images/favicon.ico'>");
            includeCss(out, "/styles/global.css");
            includeCss(out, "/styles/fonts.css");
            includeCss(out, "/src/extjs/resources/css/ext-all.css");
            includeCss(out, "/styles/blog.css");

            // adding css needed for no-frames structure
            out.println();
            importTag.setSrc("/styles/noframes.css");
            importTag.printOutput(out);
            out.println();
            out.println("<!--[if lte IE 7]>");
            importTag.setSrc("/styles/noframesie.css");
            importTag.printOutput(out);
            out.println();
            out.println("<![endif]-->");
            
            out.println("<!--[if IE 8]>");
            importTag.setSrc("/styles/noframesie.css");
            importTag.printOutput(out);
            importTag.setSrc("/styles/noframesie8.css");
            importTag.printOutput(out);
            out.println();
            out.println("<![endif]-->");
            
            if( pageContext.getAttribute("user", PageContext.SESSION_SCOPE) != null && ((User)pageContext.getAttribute("user", PageContext.SESSION_SCOPE)).getID() != null ) {
                determineSpace();
                importTag.setSrc(makeJSPath(this.space));
                importTag.printOutput(out);
                
            	includeJs(out, "/toolbar/include/Main.jsp?i=" + pageContext.getSession().getCreationTime() + user.getCurrentSpace().getID() + "&s=" + pathPrefix); // spaceMenu
            	includeJs(out, "/" + pathPrefix + "/include/NavBar.jsp?i="  + pageContext.getSession().getCreationTime() + user.getCurrentSpace().getID() + "&s=" + pathPrefix); // navBar
            }
            includeJs(out, "/src/util.js");
            includeJs(out, "/src/window_functions.js");
        	includeJs(out, "/src/menu/dropdown.js");
        	
        	if (PropertyProvider.getBoolean("prm.blog.isenabled")
						&& ((StringUtils.isNotEmpty(space) && (space.equalsIgnoreCase(SpaceTypes.PERSONAL_SPACE) 
								|| space.equalsIgnoreCase("personal")
								|| space.equalsIgnoreCase(SpaceTypes.PROJECT_SPACE)))
								|| (StringUtils.isEmpty(space) && SessionManager.getUser() != null 
									&& SessionManager.getUser().getCurrentSpace() != null 
									&& (SessionManager.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE) 
											|| SessionManager.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE))))) {
        		 includeBlogItTokens(out);
        	}
            
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        } finally {
        	this.space = null;
        }

        return SKIP_BODY;
    }

    private void includeCss(JspWriter out, String cssFile) throws IOException{
        out.print(TYPE_CSS_LINK_OPEN_TAG);
        out.print(SessionManager.getJSPRootURL() + cssFile);
        out.print("?" + VERSION_NUMBER);
        out.println(TYPE_CSS_LINK_CLOSE_TAG);
    }

    private void includeJs(JspWriter out, String jsFile) throws IOException{
        out.print(TYPE_JAVASCRIPT_SRC_OPEN_TAG);
        out.print(SessionManager.getJSPRootURL() + jsFile);
        out.print("?" + VERSION_NUMBER);
        out.println(TYPE_JAVASCRIPT_SRC_CLOSE_TAG);
    }
    
    /**
     * Including tokens for blogit window
     * @param out
     * @throws IOException
     */
    private void includeBlogItTokens(JspWriter out) throws IOException{
    	 out.println(TYPE_JAVASCRIPT_OPEN_TAG);
    	 out.println("var selectProject = '" + PropertyProvider.get("all.global.blogit.selectproject.errormessage")+"';");
         out.println("var noProjectInList = '" + PropertyProvider.get("all.global.blogit.noprojectInlist.errormessage")+"';");
         out.println("var blogitNotSupportedForObject = '" + PropertyProvider.get("all.global.blogit.notsupported.errormessage")+"';");
         out.println("var selectRowBeforePostingBlog = '" + PropertyProvider.get("all.global.blogit.selectrowbeforepostingblog.errormessage")+"';");
         out.println("var selectFormRecordBeforePostingBlog = '" + PropertyProvider.get("all.global.blogit.selectformrecordbeforepostingblog.errormessage")+"';");
         out.println("var canNotPostBlogForUrl = '" + PropertyProvider.get("all.global.blogit.cannotpostblogforurl.errormessage")+"';");
         out.println("var canNotPostBlogForFolder = '" + PropertyProvider.get("all.global.blogit.cannotpostblogforfolder.errormessage")+"';");
         out.println("var selectDocumentRecordBeforePostingBlog = '" + PropertyProvider.get("all.global.blogit.selectdocumentbeforeblogposting.errormessage")+"';");
         out.println("var selectItemBeforePostingBlog = '" + PropertyProvider.get("all.global.blogit.notsupportedtopage.errormessage")+"';");
         out.println("var loadingImage = '" + PropertyProvider.get("prm.global.loading.message")+"';");
         out.println("var validationMegForBlogComment = '" + PropertyProvider.get("all.global.blogit.blogcomment.blank.validationmessage")+"';");
         out.println("var savingblog = '" + PropertyProvider.get("prm.global.saving.message")+"';");
         out.println("var canNotEnterWorkComplete = '" + PropertyProvider.get("all.global.blogit.notenterworkdone.message")+"';");
         out.println("var blogActivationMsg = '" + PropertyProvider.get("all.global.blogit.blogactivation.message")+"';");
         out.println("var loadingBlogErrorMsg = '" + PropertyProvider.get("all.global.blogit.loadingblogentry.errormessage")+"';");
         out.println("var activationErrorMsg = '" + PropertyProvider.get("all.global.blogit.activation.errormessage")+"';");
         out.println("var activationFailedErrorMsg = '" + PropertyProvider.get("all.global.blogit.activateblogbyclickingbloglink.message")+"';");
         out.println("var savingCommentFailedErrorMsg = '" + PropertyProvider.get("all.global.blogit.savingcommentfailed.errormessage")+"';");
         out.println("var loadingRecentBlog = '" + PropertyProvider.get("all.global.blogit.loadingrecentblogentry.message")+"';");
         out.println("var blogentryDeletePermission = '" + PropertyProvider.get("all.global.blogit.delete.blogfailed.message")+"';");
         out.println("var checkAndRedirectErrorMsg = '" + PropertyProvider.get("all.global.blogit.checkingandredirect.error.message")+"';");
         out.println("var noBlogEntryFoundForTask = '" + PropertyProvider.get("all.global.blogit.noblogentryfoundfortask.message")+"';");
         out.println("var validationMessageForBlogHtmlTagEntry = '" + PropertyProvider.get("prm.blog.addweblogentry.htmlvalidation.message")+"';");
         out.println(TYPE_JAVASCRIPT_CLOSED_TAG);
    }
    
    public String makeJSPath(String src) {
        return (JS_PATH_PREFIX + src + JS_FILE_EXTENSION);
    }
    
    public void release() {
        this.space = null;
        super.release();
    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */

    private void loadModules(){
    	try{
    		modules.load();
    	}catch(Exception ed){
    		//ignore
    	}
    }
    /**
     * Determines path based on specified space.
     * If token for specified space not found, defaults to user's current space.
     * @param space the string representation of the space to get CSS for
     * @throws JspTagException if there is a problem determining space
     * @see #determinePath()
     */
    private void determinePath(String space) throws JspTagException {

        String tokenName = "prm.global.css." + space;
        this.path = PropertyProvider.get(tokenName);

        if (this.path == null || this.path.length() == 0) {
            determinePath();
        }
    }

    /**
     * Determines the current user's space, where the user is fetched from
     * session scope.  Sets the <code>space</code> attribute.
     * @throws JspTagException if there is a problem finding the user in the sesion
     */
    private void determinePath() throws JspTagException {
        User user = null;
        String spaceType = null;

        user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);

        //If the user isn't in a space, default to the personal space
        if (user == null || user.getCurrentSpace() == null) {
            spaceType = Space.PERSONAL_SPACE;
        } else {
            spaceType = user.getCurrentSpace().getType();
        }

        // Now set the space based on the space type
        if (spaceType.equals(Space.PERSONAL_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.personal");

        } else if (spaceType.equals(Space.PROJECT_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.project");

        } else if (spaceType.equals(Space.FACILITY_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.facility");

        } else if (spaceType.equals(Space.BUSINESS_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.business");

        } else if (spaceType.equals(Space.METHODOLOGY_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.methodology");

        } else if (spaceType.equals(Space.APPLICATION_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.application");

        } else if (spaceType.equals(Space.CONFIGURATION_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.configuration");

        } else if (spaceType.equals(Space.COMPANY_SPACE)) {
            // Company Space for Salesmode app is defaulting to personal space CSS
            this.path = PropertyProvider.get("prm.global.css.personal");

        } else if (spaceType.equals(Space.CONTRACT_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.contract");

        } else if (spaceType.equals(Space.CUSTOMER_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.customer");

        } else if (spaceType.equals(Space.ENTERPRISE_SPACE)) {
            this.path = PropertyProvider.get("prm.global.css.enterprise");
        } else {
            throw new JspTagException("Error in GetSpaceCSSTag: Unhandled space type '" + spaceType + "'.");

        }
    }
    
    private String getIncludePathPrefix() throws JspTagException {
        final User user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);

        if (user == null || user.getCurrentSpace() == null ) 
        	return "personal";
        
        final String spaceType = this.space != null ? this.space : user.getCurrentSpace().getType();
        
        if (spaceType.equals(ISpaceTypes.PERSONAL_SPACE) || spaceType.equals("personal")) {
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

    private void determineSpace() throws JspTagException {
        User user = null;
        String spaceType = null;

        user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
        if (user == null || user.getCurrentSpace() == null ) {
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
}
