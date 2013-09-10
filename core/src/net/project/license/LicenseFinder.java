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
package net.project.license;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.finder.WhereClauseFilter;
import net.project.billing.payment.PaymentDAO;
import net.project.billing.payment.PaymentInformation;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.PersonFinder;
import net.project.resource.PersonSalary;
import net.project.schedule.Task;
import net.project.security.EncryptionException;
import net.project.util.Conversion;
import net.project.util.ErrorReporter;

import org.apache.log4j.Logger;

public class LicenseFinder extends Finder {
    /** Column definition for the responsible_user_id column. */
    public static final ColumnDefinition RESPONSIBLE_USER_ID_COLUMN = new ColumnDefinition("l.responsible_user_id", "Responsible User ID");
    /** Column definition for the is_trial column. */
    public static final ColumnDefinition IS_TRIAL_COLUMN = new ColumnDefinition("l.is_trial", "Is Trial");
    /** Column definition for the license_status column. */
    public static final ColumnDefinition LICENSE_STATUS_COLUMN = new ColumnDefinition("l.license_status", "License Status");
    /** Column definition for the license key. */
    public static final ColumnDefinition LICENSE_KEY_COLUMN = new ColumnDefinition("l.license_key_value", "License Key");
    /** Column definition for the license id. */
    public static final ColumnDefinition LICENSE_ID_COLUMN = new ColumnDefinition("l.license_id", "License ID");
    
    /**
     * This value indicates the maximum number of rows that we are willing to
     * fetch from the database.  Zero is an Oracle default meaning "no limit".
     * The default is zero.
     */    
    private int maximumRowsToFetch = 0;
    
    /**
     * This value stores errors or warnnings found during execution of the methods
     * within the class
     */
    private ErrorReporter errorReporter = new ErrorReporter();
    
    private static final String BASE_SQL_STATEMENT =
        "select " +
        "    l.license_id, l.certificate_id, l.payment_id, " +
        "    l.license_key_value, l.is_trial, l.license_status, " +
        "    l.status_reason_code, l.responsible_user_id, " +
        "    lc.certificate_lob_data as certificate_data, "+
        "    pi.payment_id, pi.payment_model_id, pi.party_id, pm.model_type_id, " +
        "    lp.person_id as purchaser_id "+
        "from " +
        "    pn_license l, pn_license_certificate_lob lc, " +
        "    pn_payment_information pi, pn_payment_model pm,  " +
        "    pn_license_purchaser lp " +
        "where" +
        "    l.certificate_id = lc.certificate_id " +
        "    and l.payment_id = pi.payment_id " +
        "    and pm.payment_model_id = pi.payment_model_id " +
        "    and l.license_id = lp.license_id(+) ";

    /** List of responsible users that we precache before running the query. */
    Map personMap = new HashMap();

    /** Make sure that the users are preloaded. */
    private FinderListener listener = new FinderListenerAdapter() {
        public void preExecute(DBBean db) throws SQLException {
            //Preload users
            PersonFinder finder = new PersonFinder();
            WhereClauseFilter filter = new WhereClauseFilter(
                "person_id in (" +
                "SELECT responsible_user_id "+
                "FROM   pn_license l, pn_payment_information pi, pn_payment_model pm "+
                "WHERE  l.payment_id = pi.payment_id "+
                "       and pm.payment_model_id = pi.payment_model_id "+
                "       "+getWhereClause()+ " "+
                ")"
            );
            filter.setSelected(true);

            finder.addFinderFilter(filter);

            try {
                List persons = finder.find();

                for (Iterator it = persons.iterator(); it.hasNext();) {
                    Person p = (Person)it.next();
                    p.setSalary(new PersonSalary(p.getID()));
                    personMap.put(p.getID(), p);
                }
            } catch (PersistenceException e) {
                throw new SQLException(e.getMessage());
            }
        }
    };

    public LicenseFinder() {
        addFinderListener(listener);
    }

    /**
     * Get the SQL statement which without any additional where clauses, group by, or
     * order by statements.
     * <p>
     * The SQL statement will include a <code>SELECT</code> part, a <code>FROM</code>
     * part and the <code>WHERE</code> keyword.  It will include any conditional
     * expressions required to perform joins. All additional conditions will
     * be appended with an <code>AND</code> operator.
     * </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    public List find() throws PersistenceException {
        return this.loadFromDB();
    }

    public boolean find(License license) throws PersistenceException {
        return this.loadFromDB(license);
    }
   
    /**
     * This method calls every listener's
     * {@link net.project.base.finder.FinderListener#preExecute} method to
     * notify them that the executePrepared() call is just about to occur.
     *
     * @param db a <code>DBBean</code> object that the user can modify.
     * @throws SQLException if any of the listeners throw a SQLException.
     */
    private void preExecute(DBBean db) throws SQLException {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].preExecute(db);
        }
    }
    
    
    private void postExecute(DBBean db, List list) throws SQLException {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].postExecute(db, list);
        }
    }
    
    /**
     * Load all data objects from the database that correspond to the
     * {@link #getSQLStatement} query into domain objects.  Return these values
     * in a <code>List</code> object.
     *
     * @return a <code>List</code> object containing one or more data objects
     * constructed in {@link #createObjectForResultSetRow}
     * @throws PersistenceException if a database error occurs while querying
     * for data or while populating objects.
     */
    protected List loadFromDB() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            return loadFromDBOver(db);
        } catch (SQLException sqle) {
            throw new PersistenceException("", sqle);
        } finally {
            db.release();
        }
    }
   
    /**
     * Load all data objects from the database that correspond to the
     * {@link #getSQLStatement} query into domain objects.  Return these values
     * in a <code>List</code> object.
     *
     * @return a <code>List</code> object containing one or more data objects
     * constructed in {@link #createObjectForResultSetRow}
     * @throws SQLException , PersistenceException if a database error occurs while querying
     * for data or while populating objects.
     */
    protected List loadFromDBOver(DBBean db) throws SQLException,PersistenceException{
        ArrayList dataToReturn = new ArrayList();

        db.prepareStatement(getSQLStatement());

        if (maximumRowsToFetch != 0) {
            db.pstmt.setMaxRows(maximumRowsToFetch);
        }

        //Call any last minute things a subclass may want to do
        preExecute(db);
        db.executePrepared();
        
        //count number of currupted license files
        int badPaddingCount = 0;
        while (db.result.next()) {
        	try{
        		Object toAdd = createObjectForResultSetRowOver(db.result);
        		//Make sure that what was return was meant to be added.
	            if (!NON_UNIQUE_ROW.equals(toAdd)) {
	                dataToReturn.add(toAdd);
	            }
        	}catch(LicenseException bPad){
        		//error loading license files then increment the count
        		badPaddingCount++;
        		Logger.getInstance(LicenseFinder.class).error(bPad.getMessage());
        	}
        }
        if(badPaddingCount>0){
        	Logger.getInstance(LicenseFinder.class).error("There are "+badPaddingCount+" license files corrupt");
        	errorReporter.addWarning(badPaddingCount+" number of licenses could not be loaded due to errors");
        }
        	
        postExecute(db, dataToReturn);

        return dataToReturn;
    }
    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException,LicenseException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRowOver(ResultSet result) throws SQLException ,LicenseException{
        return createObjectForResultSetRowOver(result, new License());
    }
    
    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result) throws SQLException{
        return createObjectForResultSetRow(result, new License());
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  This particular version of the createObjectForResultSetRow method
     * is available so the user can fill up an preexisting object with data more
     * easily.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @param object a preexisting license that needs to be filled with data.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException,LicenseException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRowOver(ResultSet result, Object object) throws SQLException,LicenseException {
        License license = (License)object;
        try {
            license.setID(result.getString("license_id"));
            try {
                String certificateData = LicenseDAO.decrypt(ClobHelper.read(result.getClob("certificate_data")));

                LicenseCertificate cert = new LicenseCertificate();
                cert.setID(result.getString("certificate_id"));
                cert.setBody(LicenseCertificateBody.unmarshal(certificateData));

                license.setLicenseCertificate(cert);
            }catch(EncryptionException bPad){
            	throw new LicenseException("",bPad);
            }
            catch(Exception e){
            	throw new PnetRuntimeException("Unable to load certificate: "+ e.getMessage(), e);
            }

            PaymentInformation paymentInfo = new PaymentInformation();
            PaymentDAO.populatePaymentInformation(result, paymentInfo);

            license.setPaymentInformation(paymentInfo);

            license.createKeyForValue(result.getString("license_key_value"));
            license.setTrial(Conversion.toBoolean(result.getString("is_trial")));
            license.setStatus(Conversion.toInt(result.getString("license_status")),
                Conversion.toInt(result.getString("status_reason_code")));

            //If the responsible user is in the person map, add them to the
            //object
            if (result.getString("responsible_user_id") != null) {
                license.setResponsiblePerson((Person)personMap.get(result.getString("responsible_user_id")));
            }

            //If there is a purchaser id, use it rather than the purchaser
            //stored in the license.  It is likely that they are more up to date.
            if (result.getString("purchaser_id") != null) {
                license.setPurchaser((Person)personMap.get(result.getString("purchaser_id")));
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return license;
    }
    
    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  This particular version of the createObjectForResultSetRow method
     * is available so the user can fill up an preexisting object with data more
     * easily.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @param object a preexisting license that needs to be filled with data.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result, Object object) throws SQLException {
        License license = (License)object;
        try {
            license.setID(result.getString("license_id"));
            try {
                String certificateData = LicenseDAO.decrypt(ClobHelper.read(result.getClob("certificate_data")));

                LicenseCertificate cert = new LicenseCertificate();
                cert.setID(result.getString("certificate_id"));
                cert.setBody(LicenseCertificateBody.unmarshal(certificateData));

                license.setLicenseCertificate(cert);
            }catch(EncryptionException bPad){
            	throw new PnetRuntimeException(bPad.getMessage(),bPad);
            }
            catch(Exception e){
            	throw new PnetRuntimeException("Unable to load certificate: "+ e.getMessage(), e);
            }

            PaymentInformation paymentInfo = new PaymentInformation();
            PaymentDAO.populatePaymentInformation(result, paymentInfo);

            license.setPaymentInformation(paymentInfo);

            license.createKeyForValue(result.getString("license_key_value"));
            license.setTrial(Conversion.toBoolean(result.getString("is_trial")));
            license.setStatus(Conversion.toInt(result.getString("license_status")),
                Conversion.toInt(result.getString("status_reason_code")));

            //If the responsible user is in the person map, add them to the
            //object
            if (result.getString("responsible_user_id") != null) {
                license.setResponsiblePerson((Person)personMap.get(result.getString("responsible_user_id")));
            }

            //If there is a purchaser id, use it rather than the purchaser
            //stored in the license.  It is likely that they are more up to date.
            if (result.getString("purchaser_id") != null) {
                license.setPurchaser((Person)personMap.get(result.getString("purchaser_id")));
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return license;
    }

	/**
	 * @return Returns the errorReporter.
	 */
	public ErrorReporter getErrorReporter() {
		return errorReporter;
	}
}
