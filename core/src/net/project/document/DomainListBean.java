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

package net.project.document;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.code.TableCodeDomain;
import net.project.database.DBBean;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.space.Space;

import org.apache.log4j.Logger;

public class DomainListBean implements Serializable {


    public DomainListBean () {
        // do nothing
    }

    /*****************************************************************************************************************
     *****                                 Utility methods for getting option lists                                                 *****
     *****************************************************************************************************************/

    public static String getRosterOptionList(Space space) {

        Roster roster = new Roster();
        ArrayList team = null;
        Iterator member = null;
        Person person = null;

        StringBuffer options = new StringBuffer();

        roster.setSpace (space);
        roster.load();

        options.append(roster.getSelectionList(""));

        return options.toString();

    } // end getRosterOptionList()


    public static String getRosterOptionList(Space space, String personID) {

        Roster roster = new Roster();
        ArrayList team = null;
        Iterator member = null;

        StringBuffer options = new StringBuffer();

        roster.setSpace (space);
        roster.load();

        options.append(roster.getSelectionList(personID));    

        return options.toString();

    } // end getRosterOptionList()


    public static String getDocStatusOptionList() {

        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_document");
        domain.setColumnName("doc_status_id");

        domain.load();

        return domain.getOptionList();

    } // getDocStatusOptionList


    public static String getDocStatusOptionList(String defaultCode) {

        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_document");
        domain.setColumnName("doc_status_id");

        domain.load();

        domain.setDefaultCodeID (defaultCode);

        return domain.getOptionList();

    } // getDocStatusOptionList


    public static String getDocTypeOptionList () {

        DBBean db = new DBBean();
        String qstrGetDocTypes = "select doc_type_id, type_name, property_sheet_class_id, type_description from pn_doc_type";

        StringBuffer options = new StringBuffer();

        try {

            db.executeQuery (qstrGetDocTypes);

            while (db.result.next()) {

                options.append ("<option value=\"" + db.result.getString("doc_type_id") + "\">" + db.result.getString("type_name") + "</option>");

            } // end while


        } catch (SQLException sqle) {
            Logger.getLogger(DomainListBean.class).debug("DomainList.getDocTypeOptionList () threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

        return options.toString();

    } // end getDocTypeOptionsList()



    public static String getDocTypeOptionList (String docType) {

        DBBean db = new DBBean();
        String qstrGetDocTypes = "select doc_type_id, type_name, property_sheet_class_id, type_description from pn_doc_type";

        StringBuffer options = new StringBuffer();

        try {

            db.executeQuery (qstrGetDocTypes);

            while (db.result.next()) {

                if (docType != null)

                    if (docType.equals (db.result.getString("doc_type_id")))
                        options.append ("<option SELECTED value=\"" + db.result.getString("doc_type_id") + "\">" + db.result.getString("type_name") + "</option>");
                    else 
                        options.append ("<option value=\"" + db.result.getString("doc_type_id") + "\">" + db.result.getString("type_name") + "</option>");


            } // end while

            db.release();

        } catch (SQLException sqle) {
            Logger.getLogger(DomainListBean.class).debug("DomainList.getDocTypeOptionList () threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

        return options.toString();

    } // end getDocTypeOptionsList()


}
