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

 package net.project.datatransform.csv;

import java.net.URLDecoder;

import javax.servlet.http.HttpSession;

import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.map.ColumnMaps;

/**
 * A CSVWizard holds various information regarding errors etc for the CSV Object
 * @author Deepak
 * @since emu
 */
public class CSVWizard{
    private ValidationErrors errors = new ValidationErrors();
    private String referrer="";

    /**
     * Removes the errors stored in Validation Errors HashMap
     */
    public void clearErrors() {
        errors.clearErrors();
    }

   /**
    * Indicate whether there are any errors
    * @return true if there are errors, false otherwise
    */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    /**
     * Gets the Error Flag for the Field
     *@return String 
     *@param String fieldID
     *@param String Label
     */
    public String getFlagError(String fieldID, String label) {
        return errors.getFlagErrorHTML(fieldID, label);
    }

    /**
     * Gets the Error Message for the Field
     *@return String 
     *@param String fieldID
     */
    public String getErrorMessage(String fieldID) {
        return errors.getErrorMessageHTML(fieldID);
    }

    /**
     * Gets the Error Message for the Field
     *@return String 
     *@param String fieldID
     */
    public String getAllErrorMessages() {
        return errors.getAllErrorMessagesHTML();
    }

    /**
     *Invalidates the File uploaded for parsing
     */
    public void invalidateFormat() {
        // something is invalid
        // Hard Coded for the time-being
		// NOTE:  it does not seem possible to create this error, 
		// so I have left it untokenized.  (Brian Janko 11/19/02)
        errors.put("error2", "File uploaded , doesn`t seem , to have correct the CSV Format");
    }

    /**
     *validates the ColumnMaps 
     *@param ColumnMaps fieldID
     */
    public void validateColumnMaps(ColumnMaps columnMaps) {
        if(columnMaps.size()==0)
        errors.put("error1", PropertyProvider.get("prm.form.csvimport.columnmapping.error.nothingmapped.message")); // Hard Coded for the time-being
    }

    /**
     *Gets the Referrer to the CSV Wizard 
     *@return String
     */
    public String getReferrer() {
        return URLDecoder.decode(this.referrer);
    }

    /**
     *Sets the Referrer to the CSV Wizard 
     *@param String
     */
    public void setReferrer(String referrer) {
        this.referrer=referrer;
    }
    
    /**
     *Removes the CSV related attributes from the Session
     *@param HttpSession
     */
    public void clear(HttpSession session){
        session.removeAttribute(ICSVConstants.ATTRIBUTES);
        session.removeAttribute(ICSVConstants.CSV);
        session.removeAttribute(ICSVConstants.COLUMN_MAP);
        session.removeAttribute(ICSVConstants.COLUMN_MAPS);
        session.removeAttribute(ICSVConstants.CSV_IMPORT_OBJECT_NAME);
        System.gc();
    }
}
