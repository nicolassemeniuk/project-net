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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Facilities will not be fully implemented in V1 but the structure will be set forth
| for an easy V2 implementation
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;

/**
 * Provides an interface for facilities.
 * Facilities my be physical places such as meeting rooms or
 * virtual place such as ATT phone bridge conference number and password or a WebEx meeting. 
 *
 * @author RogerBly  
 * @author AdamKlatzkin 03/00
 * @author Tim Morrow 07/01
 */
public interface IFacility
        extends IJDBCPersistence, IXMLPersistence,java.io.Serializable {

    /** Sets the database id of this facility. */
    public void setId(String id);

    /** Sets the database id of this facility. */
    public String getId();

    /** Sets the id of the facilities owner. */
    public void setOwnerId(String id);

    /** Gets the id of the facilities owner. */
    public String getOwnerId();

    /** Sets the short name of this facility. */
    public void setName(String name);

    /** Get the short name of this facility. */
    public String getName();

    /** Sets the long description for this facility.  */
    public void setDescription(String description);

    /** Get the long description for this facility.  */
    public String getDescription();

    /** Sets the type of this facility.  */
    public void setType(FacilityType type);

    /** Get the type of this facility.  */
    public FacilityType getType();

    /**
     * Transaction safe version of the store method.  This method ensures that
     * any transaction in progress at the time this method is called will be
     * preserved, and will not be committed or rolled back.
     *
     * @param db a <code>DBBean</code> object which we will use to store this
     * facility.
     * @throws SQLException if any database error occurs while storing this
     * facility.
     */
    public abstract void store(DBBean db) throws SQLException;
}
