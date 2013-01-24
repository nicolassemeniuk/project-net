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
package net.project.hibernate.util;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 11.05.2007
 * Time: 20:18:12
 * To change this template use File | Settings | File Templates.
 */
public class TypeUtils {
    public static BigDecimal getDecimal(String value) {
        BigDecimal result = null;
        if (value != null)
            result = new BigDecimal(value);
        return result;
    }

    public static Integer getInteger(String value) {
        Integer result = null;
        if (value != null)
            result = new Integer(value);
        return result;

    }
}
