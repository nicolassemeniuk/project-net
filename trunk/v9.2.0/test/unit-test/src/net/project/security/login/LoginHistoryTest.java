/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.security.login;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;

public class LoginHistoryTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public LoginHistoryTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * 
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit 
     * test believes it is comprised of.  In most cases, it is only the current
     * class, though it can include others. 
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit
     * tests. 
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LoginHistoryTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * The key to this test is to test whether two successive history logs will
     * cause the login history to have a unique constraint error.
     *
     * It shouldn't anymore if we've fixed LoginHistory correctly.
     */
    public void testLogVisit() {
        User user = SessionManager.getUser();

        try {
            //We do three here to make sure that there are at least 2 in the
            //same second.  (If we only did two, one might come in one second and
            //the next in the following second.)  Logging the visit is fairly
            //quick, so we can easily expect that this will happen.
            LoginHistory.logVisit(user);
            LoginHistory.logVisit(user);
            LoginHistory.logVisit(user);
        } catch (PersistenceException e) {
            fail("Duplicate key exception thrown when it should have been.  "+e.toString());
        }
    }

    /**
     * If two different transactions attempt to log a login success at the same
     * time, it is possible for a primary key exception to occur.  Prevent that
     * situation.
     *
     * @throws SQLException if there is some problem in the LoginHistory object
     * where it can't connect to the database, or that the database code is
     * written incorrectly.
     */
    public void testLogVisitTransactionInterference() throws SQLException {
        DBBean db1 = new DBBean();
        DBBean db2 = new DBBean();
        db1.setAutoCommit(false);
        db2.setAutoCommit(false);

        LoginHistory lh1 = new LoginHistory();
        lh1.setUser(SessionManager.getUser());
        LoginHistory lh2 = new LoginHistory();
        lh2.setUser(SessionManager.getUser());

        lh1.logVisit(db1);
        lh2.logVisit(db1);

        db1.commit();
        db2.commit();
    }
}
