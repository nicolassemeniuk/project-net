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
package net.project.resource;

import net.project.base.property.PropertyProvider;

/**
 * A FacilityTypeDomain is the domain of FacilityTypes.  It ensures that
 * each Code is of type FacilityType.
 */
public class FacilityTypeDomain
        extends net.project.code.TableCodeDomain
        implements java.io.Serializable {

    /** Value for identifying table to which facility type code domain belongs. */
    private static final String FACILITY_TYPE_DOMAIN_TABLE = "pn_calendar_event";
    /** Value for identifying column to which facility type code domain belongs. */    
    private static final String FACILITY_TYPE_DOMAIN_COLUMN = "facility_type";

    /** Property which indicated WebEx facilities are enabled. */
    private static final String PROPERTY_WEBEX_ENABLED = "prm.global.calendar.meeting.facility.type.webex.isenabled";

    /**
     * Creates an empty facility type list.
     */
    public FacilityTypeDomain() {
        super();
        setTableName(FACILITY_TYPE_DOMAIN_TABLE);
        setColumnName(FACILITY_TYPE_DOMAIN_COLUMN);
    }

    /**
     * Retruns a new, empty FacilityType object.
     * @return the new FacilityType
     */
    protected net.project.code.Code createCode() {
        return new FacilityType();
    }

    /**
     * Adds the FacilityType to the domain.
     * Certain FacilityTypes may be unavailable, depending on property settings.
     */
    protected void addCode(net.project.code.Code code) {
        FacilityType facilityType = (FacilityType) code;
        boolean doAdd = false;

        if (facilityType.equals(FacilityType.WEBEX)) {
            // A webex facility type is only added if enabled
            if (PropertyProvider.getBoolean(PROPERTY_WEBEX_ENABLED)) {
                doAdd = true;
            }
        
        } else {
            // All other facility types are added automatically; no contraints
            // defined for them yet
            doAdd = true;
        
        }

        if (doAdd) {
            this.codes.add(facilityType);
        }

    }

}
