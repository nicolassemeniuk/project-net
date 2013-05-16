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

 package net.project.space;

import java.sql.SQLException;

import net.project.admin.ApplicationSpace;
import net.project.business.BusinessSpace;
import net.project.configuration.ConfigurationSpace;
import net.project.database.DBBean;
import net.project.enterprise.EnterpriseSpace;
import net.project.financial.FinancialSpace;
import net.project.methodology.MethodologySpace;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;


/**
 * Provides Factory methods for creating Spaces.
 */
public abstract class SpaceFactory implements ISpaceTypes {

    /**
     * Return a constructed space of the specified type.
     *
     * @param spaceTypeName one of the space types defined in ISpaceTypes
     * @see net.project.space.ISpaceTypes
     */
    public static Space constructSpaceFromType(String spaceTypeName)
        throws SpaceTypeException {
        if (PROJECT_SPACE.equals(spaceTypeName)) {
            return new ProjectSpace();
        } else if (PERSONAL_SPACE.equals(spaceTypeName)) {
            return new PersonalSpace();
        } else if (BUSINESS_SPACE.equals(spaceTypeName)) {
            return new BusinessSpace();
        } else if (FINANCIAL_SPACE.equals(spaceTypeName)) {
        	return new FinancialSpace();            
        } else if (METHODOLOGY_SPACE.equals(spaceTypeName)) {
            return new MethodologySpace();
        } else if (APPLICATION_SPACE.equals(spaceTypeName)) {
            return new ApplicationSpace();
        } else if (CONFIGURATION_SPACE.equals(spaceTypeName)) {
            return new ConfigurationSpace();
        } else if (ENTERPRISE_SPACE.equals(spaceTypeName)) {
            return new EnterpriseSpace();
        } else if (GENERIC_SPACE.equals(spaceTypeName)) {
            return new GenericSpace();
        } else {
            throw new SpaceTypeException("Passed Space type does not exist:  '" + spaceTypeName + "'");
        }
    }


    /**
     * Create a correct type of Space specified by the passed database id.
     *
     * @param spaceID the database id of the Space to be constructed.
     * @return a subclass of Space of the proper type.
     */
    public static Space constructSpaceFromID(String spaceID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            return constructSpaceFromID(spaceID, db);
        } catch (SQLException sqle) {
            throw new PersistenceException("error loading space from database for this spaceID=" + spaceID);
        } finally {
            db.release();
        }
    }

    /**
     * Create a correct type of Space specified by the passed database id.
     *
     * @param spaceID the database id of the Space to be constructed.
     * @param db a <code>DBBean</code> object which should be used to
     * communicate with the database.
     * @return a subclass of Space of the proper type.
     */
    public static Space constructSpaceFromID(String spaceID, DBBean db) throws SQLException {
        String spaceTypeName = null;
        Space space = null;

        // Query the database and determine the type for this space ID
        int index = 0;

        db.prepareStatement("select space_type , space_id , space_name , record_status from pn_space_view where space_id= ?");
        db.pstmt.setString(++index, spaceID);
        db.executePrepared();

        if (db.result.next()) {
            spaceTypeName = db.result.getString("space_type");
        } else {
            throw new SQLException("No space found in database for this spaceID=" + spaceID);
        }


        // Analyze the result and create a space
        if (PROJECT_SPACE.equals(spaceTypeName)) {
            space = new ProjectSpace(spaceID);
        } else if (PERSONAL_SPACE.equals(spaceTypeName)) {
            space = new PersonalSpace(spaceID);
        } else if (BUSINESS_SPACE.equals(spaceTypeName)) {
            space = new BusinessSpace(spaceID);
        } else if (FINANCIAL_SPACE.equals(spaceTypeName)) {
            space = new FinancialSpace(spaceID);            
        } else if (METHODOLOGY_SPACE.equals(spaceTypeName)) {
            space = new MethodologySpace(spaceID);
        } else if (APPLICATION_SPACE.equals(spaceTypeName)) {
            space = new ApplicationSpace(spaceID);
        } else if (CONFIGURATION_SPACE.equals(spaceTypeName)) {
            space = new ConfigurationSpace(spaceID);
        } else if (ENTERPRISE_SPACE.equals(spaceTypeName)) {
            space = new EnterpriseSpace();                  // Note: EnterpriseSpace does not have a settable ID.
        } else if (GENERIC_SPACE.equals(spaceTypeName)) {
            space = new GenericSpace(spaceID);
        } else {
            throw new SQLException("SpaceFactory.constructSpaceFromID:  No space type for this spaceID= '" + spaceID + "' typeName= '" + spaceTypeName + "'");
        }

        space.setID(db.result.getString("space_id"));
        space.setName(db.result.getString("space_name"));
        space.setRecordStatus(db.result.getString("record_status"));

        return space;
    }

}
