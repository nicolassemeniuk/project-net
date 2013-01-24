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
package net.project.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

/**
 * Tools which assist in processing a ServletRequest object.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class RequestUtils {
    public static List getParameterRegExp(ServletRequest request, String regExp) {
        Pattern namePattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE & Pattern.DOTALL);
        LinkedList values = new LinkedList();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            if (namePattern.matcher(paramName).matches()) {
                values.addAll(Arrays.asList(request.getParameterValues(paramName)));
            }
        }

        return values;
    }
}
