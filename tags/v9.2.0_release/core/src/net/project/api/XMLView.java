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
package net.project.api;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.mvc.IView;

/**
 * Provides a view that is simply XML content.
 * 
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public abstract class XMLView implements IView {

    /**
     * Returns the XML content as a string.
     * 
     * @return the XML content
     */
    protected abstract String getContent(Map model);

    /**
     * Renders the XML content.
     * 
     * @param model    
     * @param request  
     * @param response 
     * @throws IOException if there is a problem writing the content
     */
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = getContent(model);
        response.setContentType("text/xml");
        response.setContentLength(content.length());

        // Now write the XML to the response
        Writer writer = new BufferedWriter(response.getWriter());
        writer.write(content);
        writer.flush();
    }
}
