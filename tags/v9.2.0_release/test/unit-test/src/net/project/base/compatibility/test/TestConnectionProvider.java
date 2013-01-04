/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 13835 $
|       $Date: 2005-01-29 03:13:48 +0530 (Sat, 29 Jan 2005) $
|     $Author: matt $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility.test;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleConnectionCacheImpl;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.IConnectionProvider;

/**
 * Provides connections using Oracle's Connection Pool Driver and proprietary connection cache APIs. Uses a VM-level
 * singleton for accessing the cache and supports separate connection caches for different web applications.
 * <p/>
 * <b>Note:</b> Reads the connection parameters once from sajava.ini and thus assumes that all connections for a given
 * application are the same; that is, they are for the same user and database. </p>
 */
public class TestConnectionProvider implements IConnectionProvider {

    /**
     * The default minimum number of connections to keep open, currently <code>1</code>.
     */
    private static final int DEFAULT_MINIMUM_CONNECTIONS = 1;

    /**
     * The default maximum number of connections to keep open, currently <code>20</code>. Using a value of 20 for the
     * following reason: The default maximum number of threads that Bluestone allows is 20 (specified in sajava.ini) per
     * application server instance. we also specify a maximum of 21 requests per application server.
     */
    private static final int DEFAULT_MAXIMUM_CONNECTIONS = 20;

    /**
     * The default caching scheme to use, currently <code>DYNAMIC_SCHEME</code>. One of: <li><code>DYNAMIC_SCHEME</code>
     * - The maximum number of connections may be exceeded. Additional requests will create new connections, but those
     * connections are always closed after use.  This could result in a significant performance penalty if all
     * connections become exhausted and does not prevent the possibility of consuming all available database
     * connections. <li><code>FIXED_RETURN_NULL_SCHEME</code> - The maximum number of connections cannot be exceeded. 
     * Additional requests will return null. <li><code>FIXED_WAIT_SCHEME</code> - The maximum number of connections
     * cannot be exceeded.  Additional requests will wait until a connection becomes available.
     */
    private static final int DEFAULT_SCHEME = OracleConnectionCacheImpl.DYNAMIC_SCHEME;

    /**
     * The connection cache for each application. This is a VM singleton.
     */
    private static ConnectionCacheProvider connectionCache = new ConnectionCacheProvider();

    /**
     * Returns a connection from the connection cache.
     * 
     * @return the connection
     * @throws java.sql.SQLException if there is a problem getting the connection
     */
    public java.sql.Connection getConnection() throws SQLException {
        return connectionCache.getConnection();

    }

    //
    // Nested top-level classes
    //

    /**
     * Provides connection caching for the application. Maintains a separate cache for each application.
     */
    private static class ConnectionCacheProvider {

        /**
         * The Oracle Error Code for Connection Closed. This means that the connection to a database maintained by
         * pooled connection was closed. Returned by SQLException.
         */
        private static final int CONNECTION_CLOSED_ERROR_CODE = 17008;

        private final int minPoolSize;
        private final int maxPoolSize;

        /**
         * The actual connection cache.
         */
        private OracleConnectionCacheImpl cacheImpl = null;


        public ConnectionCacheProvider() {
            this.maxPoolSize = getMaxConnectionPoolSize();
            this.minPoolSize = getMinConnectionPoolSize();
        }

        /**
         * Determine the minimum connection pool size. By default this is {@link TestConnectionProvider#DEFAULT_MINIMUM_CONNECTIONS).
         * 
         * @return the minimum connection pool size
         */
        private static int getMinConnectionPoolSize() {
            return DEFAULT_MINIMUM_CONNECTIONS;
        }

        /**
         * Determine the maximum connection pool size. This is either {@link TestConnectionProvider#DEFAULT_MAXIMUM_CONNECTIONS}
         * or the setting <code>jdbcConnPoolSize</code> from the configuration file, if present. If the maximum
         * connection pool size would be less than the minimum, then the minimum is returned.
         * 
         * @return the maximum connection pool size.
         */
        private int getMaxConnectionPoolSize() {

            int poolSize = DEFAULT_MAXIMUM_CONNECTIONS;

            String jdbcConnPoolSizeString = ((TestConfigurationProvider) Compatibility.getConfigurationProvider()).getJDBCConnectionPoolMaxSize();
            if (jdbcConnPoolSizeString != null) {
                poolSize = Integer.valueOf(jdbcConnPoolSizeString).intValue();
            }

            return Math.max(poolSize, DEFAULT_MINIMUM_CONNECTIONS);
        }

        /**
         * Returns an open connection from the appropriate cache for the current application.
         * 
         * @return the connection
         * @throws java.sql.SQLException if there is a problem getting a connection
         */
        java.sql.Connection getConnection() throws SQLException {


            if (cacheImpl == null) {
                initializeCache();
            }

            // Now grab a connection from the cache
            // 11/19/2002 - Tim
            // We have a problem as follows:  When an application server
            // has been idle for some time (that is, no database operations
            // have been performed), the next attempt to access the database
            // results in a SQLException:
            // java.sql.SQLException: Closed Connection
            //         at oracle.jdbc.dbaccess.DBError.throwSqlException(DBError.java:134)
            //         at oracle.jdbc.dbaccess.DBError.throwSqlException(DBError.java:179)
            //         at oracle.jdbc.dbaccess.DBError.throwSqlException(DBError.java:269)
            //         at oracle.jdbc.pool.OraclePooledConnection.getConnection(OraclePooledConnection.java:300)
            //         at oracle.jdbc.pool.OracleConnectionCacheImpl.getConnection(OracleConnectionCacheImpl.java:302)
            //         at oracle.jdbc.pool.OracleConnectionCacheImpl.getConnection(OracleConnectionCacheImpl.java:268)
            //         at net.project.database.TestConnectionProvider$ApplicationConnectionCache.getConnection(TestConnectionProvider.java:215)
            //         at net.project.database.TestConnectionProvider$ConnectionCacheProvider.getConnection(TestConnectionProvider.java:143)
            //         at net.project.database.TestConnectionProvider.getConnection(TestConnectionProvider.java:73)
            // This exception can get thrown for all connections that have been
            // closed.  The venodr code is 17008.  It appears that our
            // Oracle database closes the idle connection and the Oracle
            // caching mechanism doesn't hide this from us.
            // Personally, I belive that Oracle should handle this and instead
            // return a valid connection.
            // The following code mitigates the problem by looping until an open
            // connection is returned
            java.sql.Connection conn = null;
            boolean isOpenConnectionFound = false;
            SQLException lastException = null;

            // Loop until an open connection is found
            // Aborts loop after (Max Connection Pool Size + 1) iterations since
            // there can't be more than that number of connections in the pool.
            // This eliminates possible infinite loops due to unforseen issues
            // in the future.
            // After reaching the (max connection pool size + 1) iteration, a
            // serious problem must exist since all connections are closed
            //
            // Aborts if any other SQLException occurs
            for (int i = 0; !isOpenConnectionFound && i <= maxPoolSize; i++) {

                try {
                    // Attempt to get an open connection
                    conn = this.cacheImpl.getConnection();

                    // If we reach this point, the connection is open
                    isOpenConnectionFound = true;

                } catch (SQLException e) {

                    if (e.getErrorCode() == CONNECTION_CLOSED_ERROR_CODE) {
                        // Connection closed, continue in loop
                        // Save the exception in case the loop ends without
                        // finding an open connection
                        lastException = e;

                    } else {
                        // Error code not Connection Closed
                        // Propogate the exception
                        throw e;

                    }

                }

            }

            // Now check to see if an open connection was found
            // If not, a serious problem occurred.  We propogate the last
            // exception found
            if (!isOpenConnectionFound) {
                throw lastException;
            }

            return conn;
        }

        private void initializeCache() throws SQLException {

            // Grab the connection parameters from the configuration
            TestConfigurationProvider configurationProvider = (TestConfigurationProvider) Compatibility.getConfigurationProvider();
            String user = configurationProvider.getSetting("username");
            String password = configurationProvider.getSetting("password");
            String connectString = configurationProvider.getSetting("connectString");

            // Create the Pool Data Source
            // The Connection Pool Data Source simply allows repeated access
            // to the physical connection that the pool connection maintains
            // No caching mechanism is provided to manage physical connections
            OracleConnectionPoolDataSource dataSource = new OracleConnectionPoolDataSource();
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setURL(connectString);


            // Now create the cache implementation based on the above
            // Connection Pool Data Source
            this.cacheImpl = new OracleConnectionCacheImpl(dataSource);
            // This setting ensures that even when the max number of cached connections
            // is reached, new connections are still allocated.
            // However, those connections are closed instead of being
            // returned to the pool.
            this.cacheImpl.setCacheScheme(DEFAULT_SCHEME);

            // Note: We MUST set the maximum value first; it is illegal
            // to call setMinLimit with a number greater than the Max limit
            // Set the maximum number of connections to maintain; overrides
            // default of 1.
            this.cacheImpl.setMaxLimit(this.maxPoolSize);
            // Set the minimum number of connections to maintain
            this.cacheImpl.setMinLimit(this.minPoolSize);

        }

    }

}
