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
package net.project.taglibs.display;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;
import net.project.util.HttpUtils;

/**
  * link tag.  This allows easy construction of links such as:<br>
  * <code>
  * &lt;a href="MyPage.jsp?$sid$=123" accesskey="1">1 My Page&lt;/a>
  * </code><br>
  * where the content of the tag is read from the brand lookup and the
  * href is encoded with the session id.<br>
  * For example:<br>
  * <code>
  * <display:link href='<%=myRootUrl + "/message/ViewProcessing.jsp?markread=&pos=" + pos%>' accesskey="1" contentToken="salesmode.message.markread" />
  * </code>
  */
public class LinkTag extends TagSupport {

    private static final String ACCESSKEY_TOKEN_PREFIX = "salesmode.global.accesskey.";
    
    /** href attribute */
    private String href = null;
    
    /** accesskey attribute */
    private String accesskey = null;
    
    /** contentToken attribute */
    private String contentToken = null;

    /** content attribute */
    private String content = null;

    /** accesskey token name */
    private String accesskeyToken = null;

    /**
      * Create new link tag
      */
    public LinkTag() {
        super();
    }

    /**
      * End of tag.  Inserts the link.
      * @return flag indicating whether to continue processing page
      * @throws JspTagException if there is a problem displaying the history.
      */
    public int doEndTag() throws JspTagException {
        JspWriter out;
        String presentation = null;
        
        try {
            out = pageContext.getOut();
            try {
                out.print(getPresentation());
            } catch (IOException ioe) {
                throw new JspTagException("Error in tab strip tag: " + ioe);
            }
        } finally {
            clear();
        }
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setHref(String href) {
        this.href = href;
    }
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
        if (accesskey == null) {
            this.accesskeyToken = null;
        } else {
            // Set accesskeyToken to correct value
            if (accesskey.equals("*")) {
                this.accesskeyToken = ACCESSKEY_TOKEN_PREFIX + "star";
            } else if (accesskey.equals("#")) {
                this.accesskeyToken = ACCESSKEY_TOKEN_PREFIX + "pound";
            } else {
                this.accesskeyToken = ACCESSKEY_TOKEN_PREFIX + accesskey;
            }
        }
    }
    public void setContentToken(String contentToken) {
        this.contentToken = contentToken;
    }
    public void setContent(String content) {
        this.content = content;
    }

    /*
        Other methods
     */

    /**
      * Clear all attributes
      */
    private void clear() {
        href = null;
        accesskey = null;
        accesskeyToken = null;
        contentToken = null;
        content = null;
    }

    /**
      * Return presentation for this link
      * @return the presentation of the link
      */
    private String getPresentation() {
//        BrandManager brand = (BrandManager) pageContext.getAttribute("brandManager", PageContext.SESSION_SCOPE);
	StringBuffer link = new StringBuffer();

        link.append("<a");
        
        // Insert href attribute (encoded)
        if (this.href != null) {
            link.append(" href=\"");
            link.append(HttpUtils.encodeURL(pageContext.getSession(), this.href));
            link.append("\"");
        }

        // Insert accesskey attribute
        if (this.accesskey != null) {
            link.append(" accesskey=\"" + this.accesskey + "\"");
        }

        link.append(">");

        // Now insert content body

        // Lookup access key if we have one and add it
        if (this.accesskeyToken != null) {
            String accesskeyValue = null;
            accesskeyValue = PropertyProvider.get (this.accesskeyToken);
            if (accesskeyValue != null) {
                link.append(accesskeyValue + " ");
            }
        }

        // Lookup contentToken if we have one and use that value,
        // otherwise use the content if that is available
        if (this.contentToken != null) {
            String contentTokenValue = null;
            contentTokenValue = PropertyProvider.get (this.contentToken);
            
            if (contentTokenValue != null) {
                link.append(contentTokenValue);
            } else if (this.content != null) {
                link.append(content);
            } else {
                link.append(contentToken);
            }

        } else if (this.content != null) {
            link.append(content);
        }

        link.append("</a>");

        return link.toString();
    }


}

