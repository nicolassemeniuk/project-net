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
package net.project.resource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.project.database.DBBean;
import net.project.security.SessionManager;

public class PersonSearch {
    private static final String NAME_SEARCH_SQL =
        "select "+
        "  p.person_id, p.display_name display_name, p.email "+
        "from "+
        "  pn_person_view p "+
        "where " +
        "  p.record_status = 'A' "+
        "  and p.person_id in "+
        "    (select "+
        "       shp1.person_id "+
        "     from "+
        "       pn_space_has_person shp1, "+
        "       pn_space_has_person shp2 "+
        "     where "+
        "       shp1.space_id = shp2.space_id and shp2.person_id = ?) ";
    private static final String EMAIL_SEARCH_SQL =
        "select " +
        "  p.person_id, p.display_name, p.email " +
        "from" +
        "  pn_person_view p " +
        "where " +
        "  p.record_status = 'A' " +
        "  and (p.email = ? or " +
        "  p.alternate_email_1 = ? or " +
        "  p.alternate_email_2 = ? or " +
        "  p.alternate_email_3 = ?) ";

    /**
     * Search for a user.  This method only allows users to be found that the 
     * user would know about using the system, or users that they know by email
     * address.
     * 
     * Basically, it allows us to search users we are in a space with, or ones
     * we know the email address of.
     *  
     * @param keywordString
     * @return
     */
    public Collection keywordSearch(String keywordString) {
        Set matchingPersons = new HashSet();

        /* Construct the string to match the users.  We will match any user who
           is in a space the current users is in. */
        findUsersByName(keywordString, matchingPersons);

        /* Second, we see if the user entered an email address.  If they entered
           the complete email of someone, we will allow those to be shown. */
        findUsersByEmail(matchingPersons, keywordString);

        return matchingPersons;
    }

    private void findUsersByEmail(Set matchingPersons, String keywordString) {
        DBBean db = new DBBean();
        try {
            db.prepareStatement(EMAIL_SEARCH_SQL);
            db.pstmt.setString(1, keywordString);
            db.pstmt.setString(2, keywordString);
            db.pstmt.setString(3, keywordString);
            db.pstmt.setString(4, keywordString);
            db.executePrepared();

            while (db.result.next()) {
                matchingPersons.add(
                    new SharingPerson(
                        db.result.getString(1),
                        db.result.getString(2),
                        db.result.getString(3)
                    )
                );
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    private void findUsersByName(String keywordString, Set matchingPersons) {
    	String lowerKeyword = keywordString.toLowerCase();
        String whereClause =
            " and ((lower(display_name) like '%"+lowerKeyword+"%') or "+
            " (lower(first_name) like '%"+lowerKeyword+"%') or " +
            " (lower(last_name) like '%"+lowerKeyword+"%')) ";

        DBBean db = new DBBean();
        try {
            db.prepareStatement(NAME_SEARCH_SQL+whereClause);
            db.pstmt.setString(1, SessionManager.getUser().getID());
            db.executePrepared();

            while (db.result.next()) {
                matchingPersons.add(
                    new SharingPerson(
                        db.result.getString(1),
                        db.result.getString(2),
                        db.result.getString(3)
                    )
                );
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }


    public class SharingPerson {
        private final String personID;
        private final String displayName;
        private final String email;

        public SharingPerson(String personID, String displayName, String email) {
            this.personID = personID;
            this.displayName = displayName;
            this.email = email;
        }

        public String getPersonID() {
            return personID;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmail() {
            return email;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SharingPerson)) {
                return false;
            }

            final SharingPerson sharingPerson = (SharingPerson) o;

            if (!personID.equals(sharingPerson.personID)) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            return personID.hashCode();
        }
    }
}
