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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.license;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.ErrorReporter;

/**
 * Provides facilities for creating and checking licenses.
 *
 * @author Vishwajeet
 * @since Gecko Update 3
 */
public class LicenseCollection extends ArrayList implements net.project.persistence.IXMLPersistence {

    public boolean isLoaded = false;
    List finderFilters = new ArrayList();

    /**
     * The field to sort by; this a logical field name.
     * Sorting is performed in XSL since it cannot be done
     * in the database.
     */
    private String sortField = "";

    /**
     * The sort order.
     */
    private String sortOrder = "";

    /**
     * This value contains errors or warnnings found during execution of the methods
     * within the class
     */
    private ErrorReporter errorReporter = new ErrorReporter();

	/**
	 * @return Returns the errorReporter.
	 */
	public ErrorReporter getErrorReporter() {
		return errorReporter;
	}
	
    /**
     * Loads a list of all licenses in the system.
     *
     * @throws PersistenceException if there is a fundamental persistence
     * problem loading any licenses
     * @throws LicenseException if one or more licenses could not be loaded;
     * however some licenses may have been successfully loaded
     */
    public void load() throws PersistenceException, LicenseException {
        this.clear();

        LicenseFinder finder = new LicenseFinder();
        finder.addFinderFilterList(new FinderFilterList(finderFilters, true));

        try {
            this.addAll(finder.find());
            this.errorReporter = finder.getErrorReporter();
        } catch (PersistenceException e) {
            throw new LicenseException("A problem occurred loading one or more licenses.  Last error: " + e, e);
        }
    }

    /**
     * Loads a list of all licenses a user is responsible for.
     *
     * @throws PersistenceException if there is a problem loading any license
     * @throws LicenseException if no license can be found for the key
     * or if the loaded license's key doesn't
     * match its certificate key
     */
    public void loadResponsibleForUser(net.project.security.User user)
        throws PersistenceException, LicenseException {

        this.clear();

        LicenseFinder finder = new LicenseFinder();
        finder.addFinderFilterList(new FinderFilterList(finderFilters, true));

        //Add the responsible user
        NumberFilter responsibleUser = new NumberFilter("10", LicenseFinder.RESPONSIBLE_USER_ID_COLUMN, false);
        responsibleUser.setNumber(new Integer(user.getID()));
        responsibleUser.setComparator(NumberComparator.EQUALS);
        responsibleUser.setSelected(true);
        finder.addFinderFilter(responsibleUser);

        //Load the actual licenses
        this.addAll(finder.find());

        this.isLoaded = true;
    }


    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    public String getXMLBody() {

        StringBuffer sb = new StringBuffer();
        sb.append("<LicenseList>");
        sb.append("<JSPRootURL>" + SessionManager.getJSPRootURL() + "</JSPRootURL>");
        sb.append(this.getSortOrderXML());
        Iterator iter = this.iterator();

        while (iter.hasNext()) {
            License lic = (License)iter.next();
            sb.append(lic.getXMLBody());
        }
        sb.append("</LicenseList>");

        return sb.toString();
    }

    public void clearFilters() {
        finderFilters.clear();
    }

    /**
     * Sets the filters on this collection to limit loading licenses.
     * @param lType the license type (trial/nontrial) string that will be used to load license
     * @param lStatus the license status (enabled/disabled) string that will be used to load license
     * @param licenseKey licenseKey string that will be used to load license
     */
    public void setLicenseFilters(String lType, String lStatus, String licenseKey) {
        finderFilters.clear();

        if (!lType.equals("all")) {
            NumberFilter isTrialFilter = new NumberFilter("isTrial", LicenseFinder.IS_TRIAL_COLUMN, false);
            isTrialFilter.setComparator(NumberComparator.EQUALS);
            isTrialFilter.setSelected(true);

            if (lType.equals("trial")) {
                isTrialFilter.setNumber(1);
            } else {
                isTrialFilter.setNumber(0);
            }
            finderFilters.add(isTrialFilter);
        }

        if (!lStatus.equals("all")) {
            NumberFilter licenseStatusFilter = new NumberFilter("licenseStatus", LicenseFinder.LICENSE_STATUS_COLUMN, false);
            licenseStatusFilter.setComparator(NumberComparator.EQUALS);
            licenseStatusFilter.setSelected(true);

            if (lStatus.equals("disabled")) {
                licenseStatusFilter.setNumber(LicenseStatusCode.DISABLED.getCodeID());
            } else if (lStatus.equals("canceled")) {
                licenseStatusFilter.setNumber(LicenseStatusCode.CANCELED.getCodeID());
            } else {
                licenseStatusFilter.setNumber(LicenseStatusCode.ENABLED.getCodeID());
            }
            finderFilters.add(licenseStatusFilter);
        }

        if (!licenseKey.equals("")) {
            TextFilter licenseKeyFilter = new TextFilter("licenseKey", LicenseFinder.LICENSE_KEY_COLUMN, false);
            licenseKeyFilter.setSelected(true);
            licenseKeyFilter.setComparator((TextComparator)TextComparator.CONTAINS);
            licenseKeyFilter.setValue(parseFromDisplay(licenseKey).toUpperCase());
            finderFilters.add(licenseKeyFilter);
        }
    }

    /**
     * Sets the filters on this collection to limit loading licenses.
     * @param userName the user name string that will be used to load license
     * @param folName first or last name string that will be used to load license
     * @param emailID email address string that will be used to load license
     */
    public void setUserFilters(String userName, String folName, String emailID) {
        finderFilters.clear();

        if (!userName.equals("")) {
            class UsernameNotEmptyFilter extends EmptyFinderFilter {
                public UsernameNotEmptyFilter(String id, String userName) {
                    super(id);
                    setSelected(true);
                    this.userName = userName;
                }

                public String userName;

                public String getWhereClause() {
                    return
                        " (l.license_id in (select license_id from pn_person_has_license where " +
                        " person_id in (select user_id from pn_user " +
                        " where upper (username) like UPPER('%" + userName + "%')))"+
                        " OR l.license_id in (select license_id from pn_license where " +
                        " responsible_user_id in (select user_id from pn_user " +
                        " where upper (username) like UPPER('%" + userName + "%'))))";
                }
            };

            finderFilters.add(new UsernameNotEmptyFilter("usernameNotEmpty", userName));
        }

        if (!folName.equals("")) {
            class FirstNameNotEmptyFilter extends EmptyFinderFilter {
                public String folName;

                public FirstNameNotEmptyFilter(String id, String folName) {
                    super(id);
                    setSelected(true);
                    this.folName = folName;
                }

                public String getWhereClause() {
                    return
                        " (l.license_id in (select license_id from pn_person_has_license " +
                        " where person_id in (select person_id from pn_person " +
                        " where upper (first_name) like UPPER('%" + folName + "%')" +
                        " or upper (last_name) like UPPER('%" + folName + "%')))" +
                        " OR l.license_id in (select license_id from pn_license " +
                        " where responsible_user_id in (select person_id from pn_person " +
                        " where upper (first_name) like UPPER('%" + folName + "%')" +
                        " or upper (last_name) like UPPER('%" + folName + "%'))))";
                }
            };

            finderFilters.add(new FirstNameNotEmptyFilter("firstNameNotEmpty", folName));
        }

        if (!emailID.equals("")) {
            class EmailIDNotEmptyFilter extends EmptyFinderFilter {
                public String emailID;

                public EmailIDNotEmptyFilter(String id, String emailID) {
                    super(id);
                    setSelected(true);
                    this.emailID = emailID;
                }

                public String getWhereClause() {
                    return
                        " (l.license_id in (select license_id from pn_person_has_license "+
                        " where person_id in (select person_id from pn_person " +
                        " where upper (email) like UPPER('%" + emailID + "%')))"+
                        " OR l.license_id in (select license_id from pn_person_has_license " +
                        " where responsible_user_id in (select person_id from pn_person " +
                        " where upper (email) like UPPER('%" + emailID + "%'))))";
                }
            };

            finderFilters.add(new EmailIDNotEmptyFilter("emailIDNotEmpty", emailID));
        }
    }

    /**
     * Specifies the logical sort field.
     * <p>
     * This value is not used for database sorting; instead it is
     * simply included in the XML.
     * </p>
     * @param sortField the sort field
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * Specifies the sort order.
     * @param sortOrder the sort order which must be <code>ascending</code>
     * or <code>descending</code>
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    private String getSortOrderXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<SortField>" + this.sortField + "</SortField>\n");
        sb.append("<SortOrder>" + this.sortOrder + "</SortOrder>");
        return sb.toString();
    }

    /**
     * Parses a display key and returns an internal value.
     * @param displayKey the display key to parse
     * @return the internal value for the display key
     */
    private String parseFromDisplay(String displayKey) {
        char[] source = displayKey.toCharArray();
        StringBuffer result = new StringBuffer();

        // Iterate over source and ignore '-' characters
        for (int i = 0; i < source.length; i++) {
            if (source[i] != '-') {
                result.append(source[i]);
            }
        }

        return result.toString();
    }

}
