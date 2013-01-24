/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 13950 $
|       $Date: 2005-03-03 06:11:33 +0530 (Thu, 03 Mar 2005) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.utils;

import net.project.admin.ApplicationSpace;
import net.project.persistence.PersistenceException;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import java.sql.SQLException;
import java.util.Date;

public class WellKnownObjects {
    /**
     * The space ID to which to add the test plan, currently <code>ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID</code>.
     */
    public static final String TEST_SPACE_ID = ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID;

    /**
     * The plan ID to create or use, currently <code>900</code>.
     */
    public static final String TEST_PLAN_ID = "900";

    /**
     * The test task ID to use when you need one.
     */
    public static final String TEST_TASK_ID = "901";

    /**
     * Ensures that the plan with the specified ID is created in the specified
     * space and creates it if not present.
     * @param spaceID the ID of the space to which to attach the plan
     * @param planID the ID of the plan to check and create if necessary
     * @throws net.project.persistence.PersistenceException if there is a problem creating
     */
    public static void ensurePlan(String spaceID, String planID) throws PersistenceException {

        boolean isPlanCreated = false;

        StringBuffer loadPlanQuery = new StringBuffer();
        loadPlanQuery.append("select 1 from pn_plan where plan_id = ? ");

        DBBean db = new DBBean();

        try {
            db.prepareStatement(loadPlanQuery.toString());
            db.pstmt.setString(1, planID);
            db.executePrepared();

            if (db.result.next()) {
                isPlanCreated = true;
            }

        } catch (SQLException e) {
            throw new PersistenceException("Error checking presence of plan: " + e, e);

        } finally {
            db.release();

        }

        // If the plan has not been created then we create it
        if (!isPlanCreated) {

            // Create the object
            createObject(planID, "plan");

            String planName = "Test Plan";
            String planDesc = "This is a test plan created by a unit test on " + new Date();

            StringBuffer createPlanQuery = new StringBuffer();
            createPlanQuery.append("insert into pn_plan ")
                .append("(plan_id, plan_name, plan_desc, date_start, date_end) ")
                .append("values ")
                .append("(?, ?, ?, ?, ?) ");

            StringBuffer createSpacePlanQuery = new StringBuffer();
            createSpacePlanQuery.append("insert into pn_space_has_plan ")
                .append("(space_id, plan_id) ")
                .append("values ")
                .append("(?, ?) ");

            try {
                db.setAutoCommit(false);

                // Create the plan
                int index = 0;
                db.prepareStatement(createPlanQuery.toString());
                db.pstmt.setString(++index, planID);
                db.pstmt.setString(++index, planName);
                db.pstmt.setString(++index, planDesc);
                db.pstmt.setNull(++index, java.sql.Types.DATE);
                db.pstmt.setNull(++index, java.sql.Types.DATE);
                db.executePrepared();

                // Attach to application space
                index = 0;
                db.prepareStatement(createSpacePlanQuery.toString());
                db.pstmt.setString(++index, spaceID);
                db.pstmt.setString(++index, planID);
                db.executePrepared();

                db.commit();

            } catch (SQLException e) {

                try {
                    db.rollback();
                } catch (SQLException e2) {
                    // Release and throw original exception
                }

                throw new PersistenceException("Error creating plan " + e, e);

            } finally {
                db.release();

            }

        }
    }

    /**
     * Helper method to create an object with a specified ID.
     * The object has record status "A" and is created by user with ID "1".
     * If an object with that ID already exists of the specified type, this
     * method performs no action.
     * @param objectID the ID of the object to create
     * @param objectType the object type
     * @throws PersistenceException if there is a problem creating the object
     */
    private static void createObject(String objectID, String objectType) throws PersistenceException {

        String userID = "1";

        DBBean db = new DBBean();

        try {

            // Check to see if object already exists
            db.executeQuery("select object_type from pn_object where object_id = " + objectID);

            if (db.result.next()) {

                // Object exists, so check its type
                String currentObjectType = db.result.getString("object_type");

                if (!currentObjectType.equals(objectType)) {
                    throw new PersistenceException("An object with ID '" + objectID +
                        "' already exists, but its type is different.  Expected '" + objectType +
                        "' found '" + currentObjectType + "'.");
                }

            } else {

                // Object does not exist, so create it
                db.executeQuery("insert into pn_object (object_id, date_created, object_type, created_by, record_status) " +
                    "values (" + objectID + ", SYSDATE, " + DBFormat.varchar2(objectType) + ", " + userID + ", " + DBFormat.varchar2("A") + ")");

            }

        } catch (SQLException e) {
            throw new PersistenceException("Error creating object for ID: " + objectID + " of type: " + objectType + ": " + e, e);

        } finally {
            db.release();

        }

    }
}
