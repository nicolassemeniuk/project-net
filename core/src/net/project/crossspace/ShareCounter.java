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

 package net.project.crossspace;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.util.CollectionUtils;

/**
 * This class serves to count the number of shares currently in use.
 *
 * @author Matthew Flower
 * @since 8.2.0
 */
public class ShareCounter {
    public static int countSharesInUse(Collection idList) throws SQLException {
        int shareCount = 0;

        DBBean db = new DBBean();
        try {
            String sql = "select count(*) " +
                "from " +
                "   pn_shared s, " +
                "   pn_object o " +
                "where " +
                "   s.import_space_id = o.object_id " +
                "   and o.record_status = 'A' " +
                "   and (";

            for (Iterator it = idList.iterator(); it.hasNext();) {
                String id = (String) it.next();
                sql += "s.export_container_id = " + id;
                if (it.hasNext()) {
                    sql += " or ";
                }
            }
            sql += ")";

            db.executeQuery(sql);

            if (db.result.next()) {
                shareCount =  db.result.getInt(1);
            }
        } finally {
            db.release();
        }

        return shareCount;
    }

    public static int countSharesInUse(String originalID) throws SQLException {
        return countSharesInUse(CollectionUtils.createCollection(originalID));
    }
}
