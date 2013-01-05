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

 package net.project.base.compatibility.modern;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.project.base.compatibility.IConnectionProvider;

/**
 * Provides a connection by means of a DataSource configured in the
 * web container.
 * <p>
 * The data source JNDI name is <code>java:comp/env/jdbc/PnetDB</code>.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class DataSourceConnectionProvider implements IConnectionProvider, Serializable {

    /** The name of the data source providing the connection. */
    private static final String CONTEXT = "java:comp/env";
    private static final String DATA_SOURCE_NAME = "jdbc/PnetDB";

    /**
     * Returns a connection provided by the data source.
     * @return a connection
     * @throws SQLException if there is a problem looking up the data source
     * or fetching a connection
     */
    public Connection getConnection() throws SQLException {

        Context context = null;
        DataSource ds;

        try {
            context = (Context) new InitialContext().lookup(CONTEXT);
            ds = (DataSource) context.lookup(DATA_SOURCE_NAME);

        } catch (NamingException e) {
            throw (SQLException) new SQLException("Error looking up data source for name: " + DATA_SOURCE_NAME).initCause(e);

        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    throw (SQLException) new SQLException("Error looking up data source for name: " + DATA_SOURCE_NAME).initCause(e);
                }
            }
        }

        return ds.getConnection();
    }

}
