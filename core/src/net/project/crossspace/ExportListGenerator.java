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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

/**
 * Generates a list of shares for a given space.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class ExportListGenerator implements IXMLPersistence {
    /** The id of the space we are going to show the shares for. */
    private String spaceID;
    /** The list of shares we found. */
    private List shareList = new ArrayList();

    /**
     * The primary key for the space where the objects whose shares we are
     * looking for reside in.
     *
     * @return a <code>String</code> containing a space id.
     */
    public String getSpaceID() {
        return spaceID;
    }

    /**
     * Set the primary key of the space in which we are going to look for
     * shares.
     *
     * @param spaceID a <code>String</code> containing the object id for the
     * space in which we are going to look for shares.
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Load a list of all shares that are in the space identified by the space
     * id provided to this object.
     *
     * @throws IllegalArgumentException if the spaceID in this object is blank
     * or null.
     */
    public void load() {
        if (Validator.isBlankOrNull(spaceID)) {
            throw new IllegalArgumentException("Internal error: the share list " +
                "was not provided a space id, or the space id that was provided " +
                "was empty.");
        }

        DBBean db = new DBBean();
        try {
            int EXPORTED_OBJECT_ID_COL = 1;
            int OBJECT_TYPE_COL = 2;
            int EXPORTED_OBJECT_NAME_COL = 3;
            int PERMISSION_TYPE_COL = 4;
            int IMPORT_SPACE_ID_COL = 5;
            int IMPORT_SPACE_NAME_COL = 6;

            db.prepareStatement(
                "select "+
                "  s.object_id, "+
                "  o.object_type, "+
                "  pon.name, "+
                "  s.permission_type, "+
                "  spname.object_id, "+
                "  spname.name "+
                "from "+
                "  pn_shared shrd, "+
                "  pn_shareable s, "+
                "  pn_object_name pon, "+
                "  pn_object o, "+
                "  pn_object_name spname "+
                "where "+
                "  s.object_id = pon.object_id "+
                "  and pon.object_id = o.object_id "+
                "  and o.record_status = 'A' "+
                "  and shrd.exported_object_id(+) = o.object_id "+
                "  and shrd.import_space_id = spname.object_id(+) " +
                "  and s.permission_type > 0 " +
                "  and s.space_id = ?"
            );
            db.pstmt.setString(1, spaceID);
            db.executePrepared();

            ExportInfo currentExportInfo = null;
            while (db.result.next()) {
                String shareID = db.result.getString(EXPORTED_OBJECT_ID_COL);

                //If this is a new share, make sure to keep track of it
                if (currentExportInfo == null || !currentExportInfo.exportedObject.getID().equals(shareID)) {
                    currentExportInfo = new ExportInfo();
                    currentExportInfo.exportedObject.setID(shareID);
                    currentExportInfo.exportedObject.setName(db.result.getString(EXPORTED_OBJECT_NAME_COL));
                    currentExportInfo.exportedObject.setObjectType(db.result.getString(OBJECT_TYPE_COL));
                    currentExportInfo.exportedObject.setPermissionType(TradeAgreement.getForID(
                        db.result.getString(PERMISSION_TYPE_COL)
                    ));
                    shareList.add(currentExportInfo);
                }

                //Now get the name of the space that is sharing this share, if
                //one exists.
                String sharingSpaceID = db.result.getString(IMPORT_SPACE_ID_COL);
                if (sharingSpaceID != null) {
                    ExportingSpace exportingSpace = new ExportingSpace();
                    exportingSpace.id = sharingSpaceID;
                    exportingSpace.name = db.result.getString(IMPORT_SPACE_NAME_COL);

                    //Add the sharing space to the list of sharing spaces
                    if (currentExportInfo.sharingSpaces == null) {
                        currentExportInfo.sharingSpaces = new ArrayList();
                    }
                    currentExportInfo.sharingSpaces.add(exportingSpace);
                }
            }

        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<shareList>");
        for (Iterator it = shareList.iterator(); it.hasNext();) {
            ExportInfo exportInfo = (ExportInfo)it.next();
            int sharingSpacesCount = (exportInfo.sharingSpaces == null ? 0 :
                exportInfo.sharingSpaces.size());

            xml.append("<share id=\""+exportInfo.exportedObject.getID()+"\" ");
            xml.append("name=\""+XMLUtils.escape(exportInfo.exportedObject.getName())+"\" "+
                "count=\""+sharingSpacesCount+"\" type=\""+exportInfo.exportedObject.getObjectType()+"\">");
            xml.append("<permissionType>").append(exportInfo.exportedObject.getPermissionType().toString())
               .append("</permissionType>");

            if (exportInfo.sharingSpaces != null) {
                for (Iterator it2 = exportInfo.sharingSpaces.iterator(); it2.hasNext();) {
                    ExportingSpace exportingSpace = (ExportingSpace) it2.next();
                    xml.append("<exportingSpace id=\""+exportingSpace.id+"\" name=\""+
                        XMLUtils.escape(exportingSpace.name)+"\"/>");
                }
            }

            xml.append("</share>");
        }
        xml.append("</shareList>");

        return xml.toString();
    }
}

