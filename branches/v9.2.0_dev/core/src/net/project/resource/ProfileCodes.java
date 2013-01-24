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

 package net.project.resource;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

public class ProfileCodes {
    /**
     * Return an HTML option list of user statuses, default to active
     *
     * @param statusList is the users current status
     */
    public static String getUserStatusOptionList(String[] statusList) {

        StringBuffer optionList = new StringBuffer();
        String list;

        // if no defaults are specified, default to Active
        if (statusList == null || statusList.length == 0) {
            list = getUserStatusOptionList(PersonStatus.ACTIVE.getID());
        } else {
            java.util.Collection statusCollection = Arrays.asList(statusList);

            optionList.append ("<option value=\"" + PersonStatus.ACTIVE.getID() + "\"");
            optionList.append (statusCollection.contains(PersonStatus.ACTIVE.getID()) ? " SELECTED >" : " >" );
            optionList.append (PersonStatus.ACTIVE.getName() + "</option>");

            optionList.append ("<option value=\"" + PersonStatus.UNREGISTERED.getID() + "\"");
            optionList.append (statusCollection.contains(PersonStatus.UNREGISTERED.getID()) ? " SELECTED >" : " >" );
            optionList.append (PersonStatus.UNREGISTERED.getName() + "</option>");

            optionList.append ("<option value=\"" + PersonStatus.UNCONFIRMED.getID() + "\"");
            optionList.append (statusCollection.contains(PersonStatus.UNCONFIRMED.getID()) ? " SELECTED >" : " >" );
            optionList.append (PersonStatus.UNCONFIRMED.getName() + "</option>");

            optionList.append ("<option value=\"" + PersonStatus.DISABLED.getID() + "\"");
            optionList.append (statusCollection.contains(PersonStatus.DISABLED.getID()) ? " SELECTED >" : " >" );
            optionList.append (PersonStatus.DISABLED.getName() + "</option>");

            list = optionList.toString();
        }

        return list;
    }

    /**
     * Return an HTML option list of user statuses
     *
     * @param currentStatus is the users current status
     */
    public static String getUserStatusOptionList(String currentStatus) {

        StringBuffer optionList = new StringBuffer();

        optionList.append("<option value=\"" + PersonStatus.ACTIVE.getID() + "\"");
        optionList.append((currentStatus.equals(PersonStatus.ACTIVE.getID())) ? " SELECTED >" : " >");
        optionList.append(PersonStatus.ACTIVE.getName() + "</option>");

        optionList.append("<option value=\"" + PersonStatus.UNREGISTERED.getID() + "\"");
        optionList.append((currentStatus.equals(PersonStatus.UNREGISTERED.getID())) ? " SELECTED >" : " >");
        optionList.append(PersonStatus.UNREGISTERED.getName() + "</option>");

        optionList.append("<option value=\"" + PersonStatus.UNCONFIRMED.getID() + "\"");
        optionList.append((currentStatus.equals(PersonStatus.UNCONFIRMED.getID())) ? " SELECTED >" : " >");
        optionList.append(PersonStatus.UNCONFIRMED.getName() + "</option>");

        optionList.append("<option value=\"" + PersonStatus.DISABLED.getID() + "\"");
        optionList.append((currentStatus.equals(PersonStatus.DISABLED.getID())) ? " SELECTED >" : " >");
        optionList.append(PersonStatus.DISABLED.getName() + "</option>");

        return optionList.toString();
    }

    /**
     * Get the list of all possible license filters.  At the time of writing,
     * this list is used on the userlist in the application space to filter out
     * users according to whether they are licensed or not.
     *
     * @param currentLicenses - a <code>String</code> array containing a list of
     * license filter types that should be preselected.
     * @since Version 2.2.3
     */
    public static String getLicenseList(String[] currentLicenses) {
        StringBuffer optionList = new StringBuffer();

        java.util.Collection currentLicensesCollection;
        if (currentLicenses == null) {
            currentLicensesCollection = Collections.EMPTY_SET;
        } else {
            currentLicensesCollection = Arrays.asList(currentLicenses);
        }

        optionList.append("<option value=\"" + "A" + "\"");
        optionList.append(currentLicensesCollection.contains("A") ? " SELECTED>" : ">");
        optionList.append("All");

        optionList.append("<option value=\"" + "U" + "\"");
        optionList.append(currentLicensesCollection.contains("U") ? " SELECTED>" : ">");
        optionList.append("Unlicensed");

        optionList.append("<option value=\"" + "L" + "\"");
        optionList.append(currentLicensesCollection.contains("L") ? " SELECTED>" : ">");
        optionList.append("Licensed");

        return optionList.toString();
    }

    public static String getJobDescription() throws PersistenceException {
        return getJobDescription("");
    }

    public static String getJobDescription(String jobdescription) throws PersistenceException {
        if (jobdescription == null) {
            jobdescription = "";
        }

        DBBean db = new DBBean();
        StringBuffer optionlist = new StringBuffer();
        String query = "Select job_description_code,job_description from pn_job_description_lookup order by job_description_code";
        try {
            db.setQuery(query);
            db.executeQuery();
            while (db.result.next()) {
                optionlist.append("<option value=\"" + db.result.getString("job_description_code") + "\"");
                if (jobdescription.equals(db.result.getString("job_description_code"))) {
                    optionlist.append("Selected");
                }
                optionlist.append(">" + db.result.getString("job_description") + "</option>\n");
            }
            db.result.close();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load Marketing List", sqle);
        } finally {
            db.release();
        }
        return optionlist.toString();
    }

    public static String getMarketingTypes() throws PersistenceException {
        return getMarketingTypes(null);
    }

    public static String getMarketingTypes(String[] marketing) throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer optionlist = new StringBuffer();
        String query = "Select spam_type_code,spam_type from pn_spam_lookup";
        try {
            db.setQuery(query);
            db.executeQuery();
            int x = 0;
            int columns = 1;
            optionlist.append("<table border=\"0\" width=\"100%\"><tr>");
            while (db.result.next()) {
                if (x == columns) {
                    optionlist.append("</tr>\n<tr>\n");
                    x = 0;
                }
                optionlist.append("<td nowrap class=\"fieldNonRequired\">");
                optionlist.append("<input name=\"marketingType\" type=\"checkbox\" onclick=\"setUpdated('true');\" value=\"" + db.result.getString("spam_type_code") + "\"");
                if (marketing != null) {
                    for (int y = 0; y < marketing.length; y++) {
                        if (marketing[y].equals(db.result.getString("spam_type_code"))) {
                            optionlist.append(" checked");
                        }
                    }
                }
                optionlist.append(">");
                optionlist.append(db.result.getString("spam_type") + "</td>\n");
                x++;
            }
            optionlist.append("</tr></table>\n");
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load Marketing List", sqle);
        } finally {
            db.release();
        }

        return optionlist.toString();
    }

    public static String getDisciplineList() throws PersistenceException {
        return getDisciplineList("");
    }

    public static String getDisciplineList(String discipline) throws PersistenceException {
        if (discipline == null) {
            discipline = "";
        }
        DBBean db = new DBBean();
        StringBuffer optionlist = new StringBuffer();
        String query = "Select discipline_code,discipline_name from pn_discipline_lookup";
        try {
            db.setQuery(query);
            db.executeQuery();

            while (db.result.next()) {
                optionlist.append("<option value=\"" + db.result.getString("discipline_code") + "\"");
                if (discipline.equals(db.result.getString("discipline_code"))) {
                    optionlist.append("Selected");
                }
                optionlist.append(">" + db.result.getString("discipline_name") + "</option>\n");
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Unable to load disciplines", sqle);
        } finally {
            db.release();
        }
        return optionlist.toString();
    }


    public static String getDateFormatOptionList(String defaultCode) throws PersistenceException {

        DBBean db = new DBBean();
        StringBuffer optionList = new StringBuffer();
        String qstrGetDateFormatList = "select date_format_id, format_string, display, example from pn_date_format";
        String value = null;

        try {

            db.executeQuery(qstrGetDateFormatList);

            while (db.result.next()) {

                value = db.result.getString("display") + " (" + db.result.getString("example") + ")";

                optionList.append("<option value=\"" + db.result.getString("date_format_id") + "\"");

                if (defaultCode != null && defaultCode.equals(db.result.getString("date_format_id"))) {
                    optionList.append(" SELECTED>" + value + "</option>\n");
                } else {
                    optionList.append(" >" + value + "</option>\n");
                }

            } // end while

        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to get date format option list", sqle);
        } finally {
            db.release();
        }

        return optionList.toString();

    }


    public static String getTimeFormatOptionList(String defaultCode) throws PersistenceException {

	DBBean db = new DBBean();
	StringBuffer optionList = new StringBuffer();
	String qstrGetDateFormatList = "select time_format_id, format_string, display, example from pn_time_format";
	String value;

        try {

            db.prepareStatement(qstrGetDateFormatList);
            db.executePrepared();

            while (db.result.next()) {

                value = db.result.getString("display") + " (" + db.result.getString("example") + ")";

                optionList.append("<option value=\"" + db.result.getString("time_format_id") + "\"");

                if (defaultCode != null && defaultCode.equals(db.result.getString("time_format_id"))) {
                    optionList.append(" SELECTED>" + value + "</option>\n");
                } else {
                    optionList.append(" >" + value + "</option>\n");
                }

            } // end while

        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to get time format option list. ", sqle);
        } finally {
            db.release();
        }

        return optionList.toString();
    }


    public static String getStateCodeOptionList() throws PersistenceException {
        // call get state code list with an empty string
        return getStateCodeOptionList("");
    }

    public static String getStateCodeOptionList(String defaultCode) throws PersistenceException {
        if (defaultCode == null) {
            defaultCode = "";
        }

        DBBean db = new DBBean();
        StringBuffer optionList = new StringBuffer();
        String qstrGetStateOptionList = "select state_name, state_code from pn_state_lookup order by country_code desc, state_name";

        try {

            db.executeQuery(qstrGetStateOptionList);

            while (db.result.next()) {

                optionList.append("<option value=\"" + db.result.getString("state_code") + "\"");

                if (defaultCode != null && defaultCode.equalsIgnoreCase(db.result.getString("state_code"))) {
                    optionList.append(" SELECTED>" + PropertyProvider.get(db.result.getString("state_name")) + "</option>\n");
                } else {
                    optionList.append(" >" + PropertyProvider.get(db.result.getString("state_name")) + "</option>\n");
                }

            } // end while

        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to get state code option list", sqle);
        } finally {
            db.release();
        }

        return optionList.toString();
    } // end getStateCodeList


    /**
     * get the HTML option list for a select statement.
     */
    public static String getCountryCodeOptionList() throws PersistenceException {
        return getCountryCodeOptionList(null);
    }


    public static String getCountryCodeOptionList(String defaultCode) throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer optionList = new StringBuffer();
        String qstrGetCountryOptionList = "select country_code, country_name from pn_country_lookup order by country_name";
        boolean foundSelectedCode = false;


        // Default to the United States if no country specified
        // if ((defaultCode == null) || defaultCode.equals(""))
        //     defaultCode = net.project.base.property.PropertyProvider.get("prm.global.brand.defaultcountrycode");

        try {
            db.executeQuery(qstrGetCountryOptionList);

            while (db.result.next()) {
                optionList.append("<option value=\"" + db.result.getString("country_code") + "\"");

                if (defaultCode != null && defaultCode.equals(db.result.getString("country_code"))) {
                    optionList.append(" SELECTED>" + PropertyProvider.get(db.result.getString("country_name")) + "</option>\n");
                } else {
                    optionList.append(" >" + PropertyProvider.get(db.result.getString("country_name")) + "</option>\n");
                }
            } // end while

            //The default option requesting selection of a locale
            if (!foundSelectedCode) {
                String selectedOption = "<option value=\"\" SELECTED>" + PropertyProvider.get("prm.global.countrylist.defaultoption.message") + "</option>\n";
                optionList = optionList.insert(0, selectedOption.toCharArray());
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to get country code option list.", sqle);
        } finally {
            db.release();
        }

        return optionList.toString();

    } // end getCountryCodeList


    /**
     * get the HTML option list for a select statement.
     */
    public static String getTimeZoneOptionList() {
        return getTimeZoneOptionList(null);
    }

    public static String getTimeZoneOptionList(String defaultCode) {

        return net.project.util.TimeZoneList.getHtmlOptionList(defaultCode);

    } // end getTimeZoneCodeList


    public static String getLanguageOptionList() {
        return getLanguageOptionList(null);
    }


    /**
     * @param defaultLanguage
     * @return
     */
    public static String getLanguageOptionList(String defaultLanguage) {

        DBBean db = new DBBean();
        String qstrGetLanguages = "select language_code, language_name from pn_language where is_active='1'";
        StringBuffer options = new StringBuffer();

        // Default to the system default language
        if ((defaultLanguage == null) || defaultLanguage.equals("")) {
            defaultLanguage = net.project.base.property.PropertyProvider.get("prm.global.brand.defaultlanguagecode");
        }

        try {

            db.executeQuery(qstrGetLanguages);

            while (db.result.next()) {
                if (defaultLanguage != null && defaultLanguage.equals(db.result.getString("language_code"))) {

                    options.append("<option SELECTED value=\"" + db.result.getString("language_code") +
                        "\">" + PropertyProvider.get(db.result.getString("language_name")) + "</option>");

                } else {
                    options.append("<option value=\"" + db.result.getString("language_code") +
                        "\">" + PropertyProvider.get(db.result.getString("language_name")) + "</option>");
                }
            }


        } catch (SQLException sqle) {
        	Logger.getLogger(ProfileCodes.class).debug("BrandManager.getLanguageOptionList() threw an SQLException: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();

    }

    /**
     * Gets the HTML option list of available locales
     *
     * @return the HTML option list string
     * @since Version 7.4
     * @deprecated in favour of overloaded method #getLocaleOptionList which
     *             accepts a defaultLocaleCode
     */
    public static String getLocaleOptionList() {
        return getLocaleOptionList(null);
    }

    /**
     * Gets the HTML option list of available locales
     *
     * @param defaultLocaleCode The default locale code the language of which
     * would be used to display the locale list
     * @return HTML option list of the available locales
     */
    public static String getLocaleOptionList(String defaultLocaleCode) {
        return net.project.util.LocaleProvider.getHtmlOptionList(defaultLocaleCode);
    }
}



