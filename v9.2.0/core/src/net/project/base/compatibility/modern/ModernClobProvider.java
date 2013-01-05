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
|    $Revision: 13854 $
|        $Date: 2005-02-04 05:53:42 +0530 (Fri, 04 Feb 2005) $
|      $Author: dixon $
|
+----------------------------------------------------------------------*/
package net.project.base.compatibility.modern;

import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import net.project.base.compatibility.IClobProvider;

/**
 * Provides helper methods for <code>oracle.sql.CLOB</code> clobs.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ModernClobProvider implements IClobProvider {

    /**
     * Returns a writer for writing to an <code>oracle.sql.CLOB</code> at position zero.
     *
     * @param clob the <code>oracle.sql.CLOB</code>
     * @return the writer
     * @throws java.sql.SQLException if there is a problem access the CLOB value
     * @throws ClassCastException    if the clob is no an <code>oracle.sql.CLOB</code>
     */
    public Writer setCharacterStream(Clob clob) throws SQLException {
        return ((oracle.sql.CLOB) clob).getCharacterOutputStream(0);
    }
}
