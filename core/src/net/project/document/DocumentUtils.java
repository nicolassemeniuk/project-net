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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.project.database.DBBean;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.ParseString;

import org.apache.log4j.Logger;


/**
 * Provides utility methods for documents.
 * <p>
 * <b>Note: This class is obsolete.  There are alternatives for all the non-deprecated
 * methods.</b>
 * </p>
 * @author unascribed
 * @since Version 1
 */
public class DocumentUtils {

    /**
     * @deprecated No replacement; use normal locale sensitive date formatting.
     */
    protected static final String DATE_FORMAT_STRING = "M/d/y";


    /**
     * Returns the next object ID value from the database sequence.
     * <p>
     * Use {@link net.project.database.ObjectManager#getNewObjectID} instead.
     * </p>
     * @return the next object ID value or null if there is a problem getting it
     */
    public static String getNextSequenceValue() {

        String nextVal = null;
        String qstrSequenceNextVal = "select pn_object_sequence.nextval from dual";

        DBBean db = new DBBean();

        try {

            db.executeQuery(qstrSequenceNextVal);

            if (db.result.next())
                nextVal = db.result.getString("nextval");

        } // end try
        catch (SQLException sqle2) {

        	Logger.getLogger(DocumentUtils.class).debug("DocumentUtils.getSequenceNextValue(): unable to return a value from the Oracle sequence");

        } // end catch
        finally {
            db.release();

        }

        return nextVal;

    } // end getSequenceNextVal()


    /**
     * @param list
     * @param objectID
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static int searchListByObjectID(ArrayList list, String objectID) {

        ArrayList theList = list;
        int i = 0;
        IContainerObject tmp;
        int retval = -1;

        for (i = 0; i < theList.size(); i++) {
            tmp = (IContainerObject) theList.get(i);

            if (tmp.getID().equals(objectID))
                retval = i;

        } // end for

        return retval;
    }

    /**
     * Returns the literal test <code>Yes</code> or <code>No</code>
     * when the specified parameter is <code>true</code> or <code>false</code>
     * respectively.
     * @param cko indicates whether a document is checked out
     * @return <code>Yes</code> if parameter is true; <code>No</code> otherwise
     * @deprecated As of 7.6.3. When constructing XML use {@link net.project.xml.XMLUtils#format(boolean)};
     * For presentation use properties <code>prm.document.documentproperties.checkedout.true</code>
     * and <code>prm.document.documentproperties.checkedout.false</code> instead
     */
    public static String displayIsCko(boolean cko) {
        String str = null;

        String checkedOut = "Yes";
        String notCheckedOut = "No"; 

        // need checked out by

        if (cko)
            str = checkedOut;
        else
            str = notCheckedOut;

        return str;
    }

    /**
     * Returns a formatted file size as a kilobyte value from a byte value.
     * @param size the size in bytes.
     * @return the formatted value representing the size in kilobytes
     * with the appropriate text for "Kb"
     */
    public static String fileSizetoKBytes(String size) {
        return ParseString.makeKfromBytes(size);
    }

    /**
     * Converts a string to boolean value.
     * @param flag the string representing the boolean.  This is case-sensitive.
     * @return true if the specified flag is <code>1</code>, <code>Y</code> or <code>T</code>
     */
    public static boolean stringToBoolean(String flag) {
        boolean val = false;

        if (flag != null) {
            if (flag.equals("1") || flag.equals("Y") || flag.equals("T"))
                val = true;
            else
                val = false;
        }
        return val;
    }

    /**
     *
     * @param date
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static java.sql.Date utilDateToSqlDate(java.util.Date date) {

        java.sql.Date sqlDate = null;

        if (date != null)
            sqlDate = new java.sql.Date(date.getTime());

        return sqlDate;
    }

    /**
     * Returns a SQL date as a Java date representing the same millisecond value.
     * @param date the SQL date to convert
     * @return the java date with the same millisecond value or null if
     * the specified date was null
     */
    public static java.util.Date sqlDateToUtilDate(java.sql.Date date) {

        java.util.Date utilDate = null;

        if (date != null)
            utilDate = new java.util.Date(date.getTime());

        return utilDate;
    }

    /**
     *
     * @param date
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static String dateToString(java.util.Date date) {

        String dateString = null;
        User user = SessionManager.getUser();

        if (user != null && date != null)
            dateString = dateToString(date, user);

        return dateString;
    }

    /**
     *
     * @param date
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static String dateToStringNoLocale(java.util.Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("M/d/yyyy");
        String tmpString = null;

        if (date != null)
            tmpString = dateFormatter.format(date);

        return tmpString;

    }

    /**
     *
     * @param date
     * @param user
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static String dateToString(java.util.Date date, User user) {

        DateFormat dateFormatter = user.getDateFormatter();
        String tmpString = null;

        tmpString = dateFormatter.formatDate(date, DATE_FORMAT_STRING);

        return tmpString;
    }

    /**
     *
     * @param date
     * @param formatString
     * @param user
     * @return
     * @deprecated As of 7.6.3. no replacement; unused. No guarantee as to its function
     */
    public static String dateToString(java.util.Date date, String formatString, User user) {

        DateFormat dateFormatter = user.getDateFormatter();
        String tmpString = null;

        tmpString = dateFormatter.formatDate(date, formatString);

        return tmpString;
    }


    /**
     * Returns a relative path to an image file representing the checkout
     * state.
     * <p>
     * <b>Note: Avoid this method; it is better to specify the
     * image path closer to the presentation.</b>
     * </p>
     * <p>
     * The appropriate path is determined as follows:
     * <ul>
     * <li>If the document is checked out <code>isCko</code> being true
     * then the image path is specified according to the following:
     * <ul>
     * <li>If the value of <code>ckoByID</code> matches the current user's ID
     * then the path is <code>/images/check_green.gif</code>
     * <li>otherwise the path is <code>/images/check_red.gif</code>
     * </ul>
     * <li>If the document is not checked out then the image path is null
     * </ul>
     * @param isCko indicates whether the document is checked out with true meaning checked-out
     * @param ckoBy the display name of the person who has the document checked out
     * or null if it is not checked out; this parameter is not used
     * @param ckoByID the id of the person who has the document checked out or null
     * if it is not checked out
     * @return a relative path to an imgage
     */
    public static String getCkoImage(boolean isCko, String ckoBy, String ckoByID) {

        String img = null;
        String outString = null;

        User user = SessionManager.getUser();
        String whoami = user.getID();


        if ((whoami != null) && (ckoByID != null && ckoByID.equals(whoami)))
            img = SessionManager.getJSPRootURL() + "/images/check_green.gif";
        else
            img = SessionManager.getJSPRootURL() +"/images/check_red.gif";

        if (isCko)
            if (ckoBy != null && !ckoBy.equals("")) 

//		outString = "<img border=\"0\" alt=\"Checked out by: " + ckoBy + "\" src=\"" + img + "\"/>";
                outString = img;
            else
                outString = img;

        return outString;
    } // end getCkoImage


}
