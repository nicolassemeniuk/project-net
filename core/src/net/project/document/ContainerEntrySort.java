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

import net.project.xml.XMLUtils;

public class ContainerEntrySort {

    // the DB column names are specified by and defined in 
    // Container.loadContents()

    public static final String BY_NAME = "UPPER(name)";
    public static final String BY_FORMAT = "UPPER(format)";
    public static final String BY_VERSION_NUM = "version";
    public static final String BY_STATUS = "UPPER(status)";
    public static final String BY_AUTHOR = "UPPER(author)";
    public static final String BY_MODIFIED_DATE = "date_modified";
    public static final String BY_FILE_SIZE = "file_size";
    public static final String BY_ISCKO = "is_checked_out";
    public static final String BY_CKO_BY = "UPPER(checked_out_by)";

    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";

    private static final String ORDER_BY = "order by";
    private static final String SPACE = " ";

    protected String userSortBy = null;
    protected String userSortOrder = null;


    public ContainerEntrySort() {
	// do nothing
    }


    public static String getXML() {

		StringBuffer xml = new StringBuffer();
		String tab = null;

		tab = "\t";
		xml.append (tab + "<container_entry_columns>\n");

		tab = "\t\t";
		xml.append(tab + "<name>" + XMLUtils.escape ( BY_NAME ) + "</name>\n");
		xml.append(tab + "<format>" + XMLUtils.escape ( BY_FORMAT ) + "</format>\n");
		xml.append(tab + "<version_num>" + XMLUtils.escape ( BY_VERSION_NUM ) + "</version_num>\n");
		xml.append(tab + "<status>" + XMLUtils.escape ( BY_STATUS ) + "</status>\n");
		xml.append(tab + "<author>" + XMLUtils.escape ( BY_AUTHOR ) + "</author>\n");
		xml.append(tab + "<date_modified>" + XMLUtils.escape ( BY_MODIFIED_DATE ) + "</date_modified>\n");
		xml.append(tab + "<file_size>" + XMLUtils.escape ( BY_FILE_SIZE ) + "</file_size>\n");
		xml.append(tab + "<is_cko>" + XMLUtils.escape ( BY_ISCKO ) + "</is_cko>\n");
		xml.append(tab + "<cko_by>" + XMLUtils.escape ( BY_CKO_BY ) + "</cko_by>\n");

		tab = "\t";
		xml.append (tab + "</container_entry_columns>\n\n");

		return xml.toString();
    } // end getXML()




    public static String getDefaultSortBy() {
	return BY_NAME;
    }

    public static String getDefaultSortOrder() {
	return ORDER_ASC;
    }

    public void setSortBy (String sortBy) {
	this.userSortBy = sortBy;
    }

    public void setSortOrder (String sortOrder) {
	this.userSortOrder = sortOrder;
    }

    public String getSortBy() {

	String sortBy = null;

	if (this.userSortBy != null && (!this.userSortBy.equals("")))
	    sortBy = this.userSortBy;
	else
	    sortBy = getDefaultSortBy();

	return sortBy;
    } // end getSortBy()

    public String getSortOrder() {

	String sortOrder = null;

	if (this.userSortOrder != null && (!this.userSortOrder.equals("")))
	    sortOrder = this.userSortOrder;
	else
	    sortOrder = getDefaultSortOrder();

	return (sortOrder);
    } // end getSortOrder()


    public String makeSortedDBQuery (String qstr) {
	
	StringBuffer query = new StringBuffer();

	query.append (qstr);
	query.append (SPACE);
	query.append (ORDER_BY);
	query.append (SPACE);
	query.append ( getSortBy() );
	query.append (SPACE);
	query.append ( getSortOrder() );

	return query.toString();

    } // end makeDBQuery


    public static String makeSortedDBQuery (String qstr, String sortBy, String sortOrder) {
	
	StringBuffer query = new StringBuffer();

	query.append (qstr);
	query.append (SPACE);
	query.append (ORDER_BY);
	query.append (SPACE);
	query.append (sortBy);
	query.append (SPACE);
	query.append (sortOrder);

	return query.toString();

    } // end makeDBQuery



} // end class ContainerEntrySort
