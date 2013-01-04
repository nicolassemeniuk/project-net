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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.persistence.IXMLPersistence;

public class HandlerMappingServlet extends HttpServlet {
    private Map mappings;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandlerMapping mapping = new HandlerMapping();
        mappings = mapping.getHandlerMap();
        request.setAttribute("xml", getXML());
        request.getRequestDispatcher("/admin/utilities/HandlerMappingCheck.jsp").forward(request, response);
    }

    public String getXML() {
        List handlerNames = new ArrayList(mappings.keySet());
        Collections.sort(handlerNames);

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<entries>");

        for (Iterator it = handlerNames.iterator(); it.hasNext();) {
            String name = (String) it.next();
            HandlerMapEntry entry = (HandlerMapEntry)mappings.get(name);

            boolean canBeCreated = true;
            try {
                Class.forName(entry.className);
            } catch (ClassNotFoundException e) {
                canBeCreated = false;
            }

            xml.append("<entry>");
            xml.append("<url>"+entry.url+"</url>");
            xml.append("<className>"+entry.className+"</className>");
            xml.append("<exists>"+canBeCreated+"</exists>");
            xml.append("</entry>");
        }
        xml.append("</entries>");

        return xml.toString();
    }
}

