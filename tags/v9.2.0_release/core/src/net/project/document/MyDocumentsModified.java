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
|   $Revision: 20568 $
|       $Date: 2010-03-15 10:12:59 -0300 (lun, 15 mar 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.document;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides an XML representation of documents modified during a certain
 * time period.
 *
 * @author Mike Brevoort
 * @since Version 1.0
 */
public class MyDocumentsModified implements IXMLPersistence {

    private static int DEFAULT_NUMBER_OF_DAYS = 7;
    private static int UNLIMITED_RESULTS = 0;

    private ArrayList documents = new ArrayList();
    private int numberOfDays = DEFAULT_NUMBER_OF_DAYS;
    private int documentCountLimit = UNLIMITED_RESULTS;

    private User user = null;
    private Space space = null;
    private DBBean db = null;
    private XMLFormatter formatter = null;

    /**
     * Creates a new, empty MyDocumentsModfied.
     */
    public MyDocumentsModified() {
        this.db = new DBBean();
        this.formatter = new XMLFormatter();
    }

    /**
     * Sets the current user.
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the current space.
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Sets the number of days in which document modified date must fall.
     * The default value is 7;
     * @param days the number of days
     */
    public void setNumberOfDays(int days) {
        this.numberOfDays = days;
    }

    /**
     * Returns the number of days in which document modified date falls.
     * @return the number of days
     */
    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    /**
     * Sets the maximum count of documents to be displayed.
     * @param documentCount the maximum number of documents to display
     */
    public void setDocumentCountLimit(int documentCount) {
        this.documentCountLimit = documentCount;
    }

    /**
     * Returns the maximum count of documents that are displayed.
     * @return the maximum number of documents
     */
    public int getDocumentCountLimit() {
        return this.documentCountLimit;
    }

    /**
     * Loads the modified documents list.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        ModifiedDocument document = null;
        Date fromDate = calculateFromDate();

        StringBuffer query = new StringBuffer();
        query.append("select dsv.doc_id, dsv.doc_name, dsv.doc_status, dsv.date_modified, dsv.modified_by, dsv.modified_by_id ");
        query.append("from pn_doc_by_space_view dsv, pn_space_has_doc_space shds ");
        query.append("where shds.space_id = ? ");
        query.append("and dsv.doc_space_id = shds.doc_space_id ");
        query.append("and dsv.date_modified >= ? and dsv.is_hidden = 0 ");
        query.append("and record_status = 'A' ");
        query.append("order by dsv.date_modified desc ");

        this.documents.clear();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, this.space.getID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(fromDate.getTime()));

            // Limit the rows returned
            if (getDocumentCountLimit() != UNLIMITED_RESULTS) {
                db.pstmt.setMaxRows(getDocumentCountLimit());
            }

            db.executePrepared();

            while (db.result.next()) {
                document = new ModifiedDocument();
                document.documentID = db.result.getString("doc_id");
                document.title = db.result.getString("doc_name");
                document.status = PropertyProvider.get(db.result.getString("doc_status"));
                document.modifiedDate = db.result.getTimestamp("date_modified");
                document.modifiedByID = db.result.getString("modified_by_id");
                document.modifiedByDisplayName = db.result.getString("modified_by");

                this.documents.add(document);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(MyDocumentsModified.class).error("MyDocumentsModified.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Unable to load list of modified documents", sqle);

        } finally {
            this.db.release();

        }

    }


    /**
     * Calculates the "fromDate" for selecting documents based on their
     * modified date.  This fromDate is essentially the current date
     * minus the number of days in which the document modified date may fall.
     * @return the from date
     */
    private Date calculateFromDate() {
        if (this.user == null) {
            throw new NullPointerException("Missing user in MyDocumentsModified");
        }
        PnCalendar calendar = new PnCalendar(this.user);

        // Subtract number of days from current date
        calendar.add(Calendar.DAY_OF_YEAR, (-1 * getNumberOfDays()));

        return calendar.getTime();
    }


    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     * This method returns the object as XML text.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation
     * of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        this.formatter.setStylesheet(styleSheetFileName);
    }


    /**
     * Gets the presentation of the component
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     *
     * @return a presentation representation of the representation of the presentation
     +---------------+---------------+---------------+---------------+---------------+------*/
    public String getPresentation() {
        return this.formatter.getPresentation(getXML());
    }

    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     *  @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text which conforms to the Channel
     * XML specification.
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<channel>\n");
        xml.append("<table_def>\n");
        xml.append("<col>" + PropertyProvider.get("prm.projects.documentsmodified.header.title.name") + "</col>\n"); // Document Title
        xml.append("<col>" + PropertyProvider.get("prm.projects.documentsmodified.header.status.name") + "</col>\n"); // Status
        xml.append("<col>" + PropertyProvider.get("prm.projects.documentsmodified.header.datemodified.name") + "</col>\n"); // Date Modified
        xml.append("<col>" + PropertyProvider.get("prm.projects.documentsmodified.header.modifiedby.name") + "</col>\n"); // Modified By
        xml.append("</table_def>\n");
        xml.append("<content>\n");

        // the guts of the meetings
        Iterator it = this.documents.iterator();
        if(this.documents.size() > 0){
	        while (it.hasNext()) {
	            ModifiedDocument document = (ModifiedDocument) it.next();
	
	            String href = URLFactory.makeURL(document.documentID, ObjectType.DOCUMENT);
	            //String modByHref = URLFactory.makeURL(document.modifiedByID, ObjectType.PERSON);
	            
	            String modByHref = SessionManager.getJSPRootURL() + "/blog/view/" + document.modifiedByID + "/"
						+ document.modifiedByID + "/" + ObjectType.PERSON + "/" + Module.PERSONAL_SPACE 
						+ "?module=" + Module.PERSONAL_SPACE;
	
	            xml.append("<row>\n");
	
	            xml.append("<data_href>\n");
	            xml.append("<label>" + XMLUtils.escape(document.title) + "</label>\n");
	            xml.append("<href>" + href + "</href>\n");
	            xml.append("</data_href>\n");
	            xml.append("<data>" + XMLUtils.escape(document.status) + "</data>\n");
	            xml.append("<data>" + DateFormat.getInstance().formatDateMedium(document.modifiedDate) + "</data>\n");
	            xml.append("<data_href>\n");
	            xml.append("<label>" + XMLUtils.escape(document.modifiedByDisplayName) + "</label>\n");
	            xml.append("<href>" + modByHref + "</href>\n");
	            xml.append("</data_href>\n");
	
	            xml.append("</row>\n");
	
	        }
        }else{
        	xml.append("<row>\n");
        	
            xml.append("<data_href>\n");
            xml.append("<label>" + PropertyProvider.get("prm.business.dashboard.addnewdocument.link") + "</label>\n");
            xml.append("<href>" + SessionManager.getJSPRootURL() + "/document/Main.jsp?module=" + Module.DOCUMENT + "</href>\n");
            xml.append("</data_href>\n");
            xml.append("<data> </data>\n");
            xml.append("<data> </data>\n");
            xml.append("<data> </data>\n");
            xml.append("</row>\n");
        }
        xml.append("</content>\n");
        xml.append("</channel>\n");

        return xml.toString();
    }


    //
    // Nested top-level classes
    //

    /**
     * An Entry in the collection of ModfiedDocuments
     */
    private static class ModifiedDocument {
        String documentID = null;
        String title = null;
        String status = null;
        Date modifiedDate = null;
        String modifiedByID = null;
        String modifiedByDisplayName = null;
    }

}
