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
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;

public class ImgTag extends TagSupport
{

    private String alt = null;
    private String src = null;
    private String className = null;
    private String height = null;
    private String width = null;
    private String align = null;
    private String border = null;
    private String href = null;
    private String ifTag = null;

    public ImgTag()
    {
    }


    public void setIf (String s) {
	this.ifTag = s;
    }

    public void setAlign(String s)
    {
        align = s;
    }

    public void setAlt(String s)
    {
        alt = s;
    }

    public void setBorder(String s)
    {
        border = s;
    }

    public void setClass(String s)
    {
        className = s;
    }

    public void setHeight(String s)
    {
        height = s;
    }

    public void setHref(String s)
    {
        href = s;
    }

    public void setSrc(String s)
    {
        src = s;
    }

    public void setWidth(String s)
    {
        width = s;
    }


    private void buildImgTag(JspWriter out) throws IOException
    {
        StringBuffer imgTag = null;
        StringBuffer aTag = null;
	boolean display = false;

	if (this.ifTag == null || this.ifTag.equals(""))
	    display = true;
	else
	    display = PropertyProvider.getBoolean (this.ifTag);

	if (display) {

	    if(src != null)
		{
		String srcTag = PropertyProvider.isToken(src) ? getValue("src", SessionManager.getJSPRootURL()+PropertyProvider.get(src)) : getValue("src", src);

		imgTag = new StringBuffer();

		imgTag.append("<img " + srcTag);

		if(getValue("border", getBorder()) != null)
		    imgTag.append(getValue("border", getBorder()));

		if(getValue("class", className) != null)
		    imgTag.append(getValue("class", className));

		if(getValue("alt", alt) != null)
		    imgTag.append(getValue("alt", alt));

		if(getValue("align", align) != null)
		    imgTag.append(getValue("align", align));

		if(getValue("height", height) != null)
		    imgTag.append(getValue("height", height));

		if(getValue("width", width) != null)
		    imgTag.append(getValue("width", width));

		imgTag.append(" />");
	    }

	    if(imgTag != null && this.href != null)
		{
		aTag = new StringBuffer();
		String hrefTag = ( PropertyProvider.isToken(href) ) ? PropertyProvider.get(href) : href;
		aTag.append("<a href=\"" + hrefTag + "\">" + imgTag + "</a>");
	    }

	    if(aTag != null)
		out.print(aTag.toString());
	    else
		out.print(imgTag.toString());
	} // end if display
    }

    public int doStartTag()
        throws JspTagException, AuthorizationFailedException
    {
        JspWriter jspwriter = pageContext.getOut();
        try
        {
            buildImgTag(jspwriter);
        }
        catch(IOException ioexception)
        {
            throw new JspTagException("I/O exception: " + ioexception);
        }
        return 0;
    }

    private String getBorder()
    {
        if(border == null)
            border = "0";
        return border;
    }

    private String getValue(String s, String s1)
    {
        return s1 == null ? null : s + "= \"" + s1 + "\" ";
    }

    public void release()
    {
        href = null;
        alt = null;
        src = null;
        className = null;
        height = null;
        width = null;
        align = null;
        border = null;
        super.release();
    }


}
