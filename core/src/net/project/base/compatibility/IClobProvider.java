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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility;

import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Provides Clob helper methods that require different implementations on each application server.
 * 
 * @author Tim Morrow
 * @since Version 7.7
 */
public interface IClobProvider {

    /**
     * Retrieves a stream to be used to write a stream of Unicode characters to the clob value that the specified
     * clob object represents, at position zero.
     * @param clob the clob to which to write a CLOB value
     * @return a stream to which Unicode encoded characters can be written
     * @throws SQLException if there is an error accessing the CLOB value
     * @see java.sql.Clob#setCharacterStream
     */
    public Writer setCharacterStream(Clob clob) throws SQLException;

}
