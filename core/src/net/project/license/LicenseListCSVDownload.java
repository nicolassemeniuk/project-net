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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.license;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.IDownloadable;
import net.project.base.ObjectType;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.ParseString;
import net.project.xml.XMLUtils;

import org.jdom.Element;

/**
 * Provides a mechanism to download licenses as CSV format.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class LicenseListCSVDownload implements IDownloadable {

    /**
     * The number of columns returned.
     * This helps to make sure each row has the correct number of elements.
     */
    private static final int NUMBER_OF_COLUMNS = 25;

    /** The licenses to download.  Each element is a <code>License</code>. */
    private Collection licenses;

    /** The dateFormat to use. */
    private DateFormat dateFormat;

    /**
     * A map of license ID to created date.
     * Each key is a <code>String</code> license ID and each
     * value is a <code>Date</code> representing the creation date.
     */
    private Map licenseCreateDateMap;

    /**
     * Standard Bean constructor, creates an empty LicenseListCSVDownload.
     */
    public LicenseListCSVDownload() {
        // Do nothing
    }

    /**
     * Initializes this license list downloader with the specified licenses.
     * @param request the current request
     * @param licenses a collection where each element is a <code>License</code>
     * to download to CSV format
     * @throws PersistenceException if there is a problem loading license creation date information
     * @throws IllegalStateException if no 'user' attribute is found in the session
     */
    public void init(HttpServletRequest request, Collection licenses) throws PersistenceException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }

        this.licenses = licenses;
        this.dateFormat = user.getDateFormatter();
        this.licenseCreateDateMap = fetchLicenseCreateDateMap();
    }

    /**
     * Fetches the license creation dates.
     * @return a map where each key is a <code>String</code> licenseID and each
     * value is a <code>Date</code>
     * @throws PersistenceException if there is a problem loading
     */
    private Map fetchLicenseCreateDateMap() throws PersistenceException {

        final String query =
                "select object_id as license_id, date_created " +
                "from pn_object " +
                "where object_type = ? ";

        Map createDateMap = new HashMap();
        DBBean db = new DBBean();

        try {
            db.prepareStatement(query);
            db.pstmt.setString(1, ObjectType.LICENSE);
            db.executePrepared();
            while (db.result.next()) {
                createDateMap.put(db.result.getString("license_id"),
                        new Date(db.result.getTimestamp("date_created").getTime()));
            }

        } catch (SQLException e) {
            throw new PersistenceException("Error fetching license creation dates: " + e, e);

        } finally {
            db.release();
        }

        return createDateMap;
    }

    /**
     * Returns a byte array containing the CSV data.
     * @return the CSV data encoded using the default character encoding
     * @throws IllegalStateException if licenses is null (init has not been called)
     * or if there is a problem with the character encoding
     */
    private byte[] getBytes() {

        if (this.licenses == null) {
            throw new IllegalStateException("Missing license information for download; init() must be called first");
        }

        StringBuffer data = new StringBuffer();

        // Add the CSV column headings
        data.append(makeCSVRow(getHeader()));

        // Add a row for each license
        for (Iterator it = licenses.iterator(); it.hasNext();) {
            License nextLicense = (License) it.next();
            data.append(makeCSVRow(getRow(nextLicense)));
        }

        // Convert the data to bytes using the appropriate character encoding
        try {
            return data.toString().getBytes(SessionManager.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw (IllegalStateException) new IllegalStateException("Unable to download licenses as CSV with character encoding " + SessionManager.getCharacterEncoding() + ": " + e).initCause(e);
        }
    }

    /**
     * Returns a collection of column elements where each element
     * is a String or null.
     * <p>
     * Each element is a column heading title.
     * </p>
     * @return the elements for the header row
     */
    private Collection getHeader() {
        Collection elements = new ArrayList();
        elements.add("LicenseID");
        elements.add("CreationDate");
        elements.add("IsTrial");
        elements.add("LicenseStatus");
        elements.add("ResponsibleUser");
        elements.add("ResponsibleUserEmail");
        elements.add("LicenseKeyValue");
        elements.add("CertificateID");
        elements.add("NodeLockedNodeID");
        elements.add("TimeLimitStartDate");
        elements.add("TimeLimitEndDate");
        elements.add("TimeLimitDayDuration");
        elements.add("UsageLimitCurrentCount");
        elements.add("UsageLimitMaxCount");
        elements.add("BaseCostValue");
        elements.add("BaseCostCurrency");
        elements.add("MaintenanceCostPercentageValue");
        elements.add("ChargeCode");
        elements.add("CreditCardNumber");
        elements.add("CreditCardExpiryMonth");
        elements.add("CreditCardExpiryYear");
        elements.add("CreditCardTransactionID");
        elements.add("PurchaserPersonID");
        elements.add("PurchaserDisplayName");
        elements.add("PurchaserEmailAddress");

        // Sanity check that we have the requisite number of columns
        if (elements.size() != NUMBER_OF_COLUMNS) {
            throw new IllegalStateException("Wrong number of columns in header row.  Expected " + NUMBER_OF_COLUMNS + ", found " + elements.size());
        }

        return elements;
    }

    /**
     * Returns a collection of column elements where each element
     * is a String or null.
     * <p>
     * This is a flattened structure of all elements of a license.
     * Each license is guaranteed to return the same number of elements
     * in the same order; some elements may be null if the license
     * does not have that particular property
     * </p>
     * @param license the license from which to get the row
     * @return the elements for the row
     */
    private Collection getRow(License license) {
        Collection elements = new ArrayList();

        Date createDate = (Date) this.licenseCreateDateMap.get(license.getID());

        Element licenseElement = license.getXMLDocument().getRootElement();
        elements.add(licenseElement.getChildText("LicenseID"));
        elements.add((createDate == null ? null : formatDate(createDate)));
        elements.add(licenseElement.getChildText("IsTrial"));
        elements.add(licenseElement.getChildText("LicenseStatus"));
        elements.add(licenseElement.getChildText("ResponsibleUser"));
        elements.add(licenseElement.getChildText("ResponsibleUserEmail"));

        // License Key
        Element keyElement = licenseElement.getChild("LicenseKey");
        if (keyElement == null) {
            addNulls(elements, 1);
        } else {
            elements.add(keyElement.getChildText("Value"));
        }

        // License Certificate
        Element certificateElement = licenseElement.getChild("LicenseCertificate");
        elements.add(certificateElement.getChildText("CertificateID"));

        Element modelCollectionElement = certificateElement.getChild("LicenseModelCollection");

        // NodeLocked (1 element)
        Element nodeLockedModelElement = modelCollectionElement.getChild("NodeLocked");
        if (nodeLockedModelElement == null) {
            addNulls(elements, 1);
        } else {
            elements.add(nodeLockedModelElement.getChild("NodeID").getChildText("Value"));
        }

        // TimeLimit (3 elements)
        Element timeLimitModelElement = modelCollectionElement.getChild("TimeLimit");
        if (timeLimitModelElement == null) {
            addNulls(elements, 3);
        } else {
            elements.add(formatDate(timeLimitModelElement.getChildText("StartDate")));
            elements.add(formatDate(timeLimitModelElement.getChildText("EndDate")));
            elements.add(timeLimitModelElement.getChildText("DayDuration"));
        }

        // UsageLimit (2 elements)
        Element usageLimitModelElement = modelCollectionElement.getChild("UsageLimit");
        if (usageLimitModelElement == null) {
            addNulls(elements, 2);
        } else {
            elements.add(usageLimitModelElement.getChildText("CurrentCount"));
            elements.add(usageLimitModelElement.getChildText("MaxCount"));
        }

        Element costCollectionElement = certificateElement.getChild("LicenseCostCollection");

        // Base Cost (2 elements)
        Element baseCostElement = costCollectionElement.getChild("Base");
        if (baseCostElement == null) {
            addNulls(elements, 2);
        } else {
            Element moneyElement = baseCostElement.getChild("UnitPrice").getChild("Money");
            elements.add(moneyElement.getChildText("Value"));
            elements.add(moneyElement.getChildText("Currency"));
        }

        // Maintenance Cost (1 element)
        Element maintenanceCostElement = costCollectionElement.getChild("Maintenance");
        if (maintenanceCostElement == null) {
            addNulls(elements, 1);
        } else {
            Element percentageElement = maintenanceCostElement.getChild("Percentage").getChild("Percentage");
            if (percentageElement == null) {
                addNulls(elements, 1);
            } else {
                elements.add(percentageElement.getChildText("Value"));
            }
        }


        // Payment Information
        Element paymentElement = licenseElement.getChild("PaymentInformation");

        Element paymentModelElement = paymentElement.getChild("PaymentModel");

        // Charge Code (1 elements)
        Element chargeCodeElement = paymentModelElement.getChild("ChargeCode");
        if (chargeCodeElement == null) {
            addNulls(elements, 1);
        } else {
            //elements.add(chargeCodeElement.getChildText("PaymentModelID"));
            elements.add(chargeCodeElement.getChildText("Value"));
        }

        // Credit Card (4 elements)
        Element creditCardElement = paymentModelElement.getChild("CreditCard");
        if (creditCardElement == null) {
            addNulls(elements, 4);
        } else {
            //elements.add(creditCardElement.getChildText("PaymentModelID"));
            elements.add(creditCardElement.getChildText("CardNumber"));
            elements.add(creditCardElement.getChildText("ExpiryMonth"));
            elements.add(creditCardElement.getChildText("ExpiryYear"));
            elements.add(creditCardElement.getChildText("TransactionID"));
        }

        // Trial (0 elements)
        Element trialElement = paymentModelElement.getChild("Trial");
        if (trialElement == null) {
            addNulls(elements, 0);
        } else {
            //elements.add(trialElement.getChildText("PaymentModelID"));
        }

        // Purchaser (3 elements)
        Element purchaserElement = licenseElement.getChild("Purchaser");
        if (purchaserElement == null) {
            addNulls(elements, 3);
        } else {
            Element personElement = purchaserElement.getChild("person");
            elements.add(personElement.getChildText("person_id"));
            elements.add(personElement.getChildText("full_name"));
            elements.add(personElement.getChildText("email_address"));
        }

        // Sanity check that each row contains the requisite
        // number of elements
        if (elements.size() != NUMBER_OF_COLUMNS) {
            throw new IllegalStateException("Wrong number of columns in a license row.  Expected " + NUMBER_OF_COLUMNS + ", found " + elements.size());
        }

        return elements;
    }

    /**
     * Adds a specified number of null elements to a collection.
     * @param collection the collection to update
     * @param count the number of nulls to add
     */
    private void addNulls(Collection collection, int count) {
        collection.addAll(Collections.nCopies(count, null));
    }

    /**
     * Constructs a single CSV row from the elements including a CRLF.
     * @param elements a collection where each element is a <code>String</code>
     * @return a CSV row with each element escaped
     */
    private String makeCSVRow(Collection elements) {

        StringBuffer row = new StringBuffer();
        boolean isAfterFirst = false;

        // Add each element as a comma separated value, escaped for CSV
        for (Iterator it = elements.iterator(); it.hasNext();) {
            String nextElement = (String) it.next();

            if (isAfterFirst) {
                row.append(",");
            } else {
                isAfterFirst = true;
            }

            row.append("\"" + ParseString.escapeDoubleQuotes(nextElement) + "\"");
        }
        row.append("\r\n");

        return row.toString();
    }

    /**
     * Formats the specified date with the user's default date format.
     * @param date the date to format
     * @return the formatted date
     */
    private String formatDate(Date date) {
        return this.dateFormat.formatDate(date);
    }

    /**
     * Formats the specified ISO date time as a date with the user's default date format.
     * @param isoDateTime the ISO date time to format
     * @return the formatted date
     */
    private String formatDate(String isoDateTime) {
        return formatDate(XMLUtils.parseISODateTime(isoDateTime));
    }

    //
    // Implementing IDownloadable
    //

    public String getFileName() {
        return "Licenses.csv";
    }

    public String getContentType() {
        return "application/x-excel;charset=" + SessionManager.getCharacterEncoding();
    }

    public InputStream getInputStream() throws IOException {
        return new java.io.ByteArrayInputStream(getBytes());
    }

    public long getLength() {
        return -1;
    }
}
