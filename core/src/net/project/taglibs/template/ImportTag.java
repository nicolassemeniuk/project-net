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

package net.project.taglibs.template;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.SessionManager;
import net.project.util.FileUtils;

import org.apache.commons.lang.StringUtils;

import net.project.util.Version;

public class ImportTag extends TagSupport {

    private static final String CSS_FILE_EXTENSION = "css";
    private static final String JAVASCRIPT_FILE_EXTENSION = "js";

    // Following used for pointing to JS src and linking CSS
    private static final String TYPE_JAVASCRIPT_SRC_OPEN_TAG = "<script language=\"javascript\" src=\"";
    private static final String TYPE_JAVASCRIPT_SRC_CLOSE_TAG = "\"></script>";
    private static final String TYPE_CSS_LINK_OPEN_TAG = "<link rel=\"stylesheet\" rev=\"stylesheet\" type=\"text/css\" href=\"";
    private static final String TYPE_CSS_LINK_CLOSE_TAG = "\">";

    private static final String VERSION_NUMBER = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
    /** File path as specified by src attribute */
    private String filePath = null;


    /* -------------------------------  Constructors  ------------------------------- */

    public ImportTag() {
        super();
    }


    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setSrc(String src) {
        this.filePath = src;
    }

    public void setType(String type) {
        // No longer required here, but it is still mandatory in the
        // tld file
    }


    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {

        JspWriter out = pageContext.getOut();

        try {
            printOutput(out);
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        }

        return SKIP_BODY;

    }

    public void release() {

        this.filePath = null;

        super.release();

    }


    /* -------------------------------  Implementing utility methods  ------------------------------- */

    /**
     * Write entire output for this tag.
     * @param out the output writer to write to
     * @throws IOException if there is a problem writing
     */
    void printOutput(JspWriter out) throws IOException {
        out.print(getOpenTag());
        out.print(SessionManager.getJSPRootURL() + filePath);
        out.print("?" + VERSION_NUMBER);
        out.println(getCloseTag());
    }

    private String getOpenTag() {
        return getOpenTag(this.filePath);
    }

    /**
     * Return the correct HTML open tag based on file name.
     *
     * This is determined by the file extension and whether we are writing
     * content or simply linking to the file.<br>
     * <b>Preconditions:</b><br>
     *     this.doWriteContent is set
     * @param file the path to the file
     * @return the correct open tag
     */
    private String getOpenTag(String file) {

        String fileIgnoreParams = StringUtils.substringBefore(file, "?");
        String fileExtension = FileUtils.getFileExt(fileIgnoreParams);
        String openTag = null;

        if (fileExtension.equalsIgnoreCase(CSS_FILE_EXTENSION)) {
            openTag = TYPE_CSS_LINK_OPEN_TAG;
        } else if (fileExtension.equalsIgnoreCase(JAVASCRIPT_FILE_EXTENSION)) {
            openTag = TYPE_JAVASCRIPT_SRC_OPEN_TAG;
        }

        return openTag;
    }


    private String getCloseTag() {
        return getCloseTag(this.filePath);
    }

    /**
     * Return the correct HTML close tag based on file name.<br>
     * This is determined by the file extension and whether we are writing
     * content or simply linking to the file.<br>
     * <b>Preconditions:</b><br>
     *     this.doWriteContent is set
     * @param file the path to the file
     * @return the correct close tag
     */
    private String getCloseTag(String file) {

        String fileIgnoreParams = StringUtils.substringBefore(file, "?");
        String fileExtension = FileUtils.getFileExt(fileIgnoreParams);
        String closeTag = null;

        if (fileExtension.equalsIgnoreCase(CSS_FILE_EXTENSION)) {
            closeTag = TYPE_CSS_LINK_CLOSE_TAG;
        } else if (fileExtension.equalsIgnoreCase(JAVASCRIPT_FILE_EXTENSION)) {
            closeTag = TYPE_JAVASCRIPT_SRC_CLOSE_TAG;
        }

        return closeTag;
    }


}
