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
package net.project.database;

/**
 * Provides an interface to allow implementation of
 * a ConnectionChecker by external tools.
 */
public interface IConnectionChecker {

    // Note:
    // This interface is currently implemented in the development tools
    // directory
    //     tools/dev
    // It is designed not to be shipped to customers
    // The implementor is
    //     net.project.database.ConnectionChecker
    // and is bundled in the
    //     tools/dev/build/lib/devtools.jar
    // jar file
    // This interface is copied to
    //    tools/dev/classes
    // to allow the development tools to implement the interface

    /**
     * Registers a connection as opened.
     * @param connection the connection opened
     */
    public void open(java.sql.Connection connection);

    /**
     * Registers a connection as closed.
     * @param connection the connection closed
     */
    public void close(java.sql.Connection connection);
}
