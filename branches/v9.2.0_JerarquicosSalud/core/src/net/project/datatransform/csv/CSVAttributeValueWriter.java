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
package net.project.datatransform.csv;

import java.util.Iterator;

import net.project.base.attribute.AttributeStoreException;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IAttributeWriteable;
import net.project.base.attribute.IllegalAttributeValueException;

/**
 * A CSVAttributeWriter class writes the AttributeValue to the Database
 *
 * @author Deepak
 * @since emu
 */
public class CSVAttributeValueWriter {
    /**
     * CSV Object in the Session
     */
    private CSV csv = null;
    /**
     * The Object whose attributes are being updated or assigned to
     */
    private IAttributeWriteable iAttributeWriteable = null;
    /**
     * Keeps the count of errors that have been caused because of database
     * rejecting the values of Attributes
     */
    private long databaseErrorsCount = 0;
    /**
     * Keeps the count of sucessful inserts that have been caused because of the values
     *  of Attributes to the Database
     */
    private long databaseInsertsCount = 0;

    /**
     * This method write the attributeValues to the Database
     *
     * @param csv CSV Object in the Session
     * @param iAttributeWriteable The Object whose attributes are being assignde to
     * @exception IllegalAttributeValueException,AttributeStoreException
     * @exception IllegalAttributeValueException
     * @exception AttributeStoreException
     */
    public void write(CSV csv, IAttributeWriteable iAttributeWriteable)
        throws IllegalAttributeValueException, AttributeStoreException {

        this.csv = csv;
        this.iAttributeWriteable = iAttributeWriteable;
        CSVRow csvRow = null;
        CSVCell csvCell = null;
        Iterator itrCells = null;
        Iterator itrAtr = null;
        IAttributeValue itrValue = null;
        Iterator itr = csv.getCSVRows().iterator();

        while (itr.hasNext()) {
            try {
                csvRow = (CSVRow)itr.next();
                if (!csvRow.isErroneous()) {
                    itrCells = csvRow.getRowCellValues().iterator();

                    while (itrCells.hasNext()) {
                        csvCell = (CSVCell)itrCells.next();

                        if (csvCell.isModified()) {
                            itrAtr = csvCell.getAttributeValueCollection().iterator();

                            while (itrAtr.hasNext()) {
                                itrValue = (IAttributeValue)itrAtr.next();

                                if (itrValue != null) {
                                    this.iAttributeWriteable.setAttributeValue(itrValue.getAttribute(), itrValue);
                                }
                            }
                        }
                    }

                    this.iAttributeWriteable.storeAttributes();
                    this.iAttributeWriteable.clearAttributeValues();
                    databaseInsertsCount++;
                }
            } catch (Exception e) {
                // Eating up all the errors for now
                this.databaseErrorsCount++;
            }
        }
    }

    /**
     * This method returns the Database Error Count
     * @return long
     */
    public long getDatabaseErrorsCount() {
        return this.databaseErrorsCount;
    }

    /**
     * This method returns the Database Insert Count
     * @return long
     */
    public long getDatabaseInsertsCount() {
        return this.databaseInsertsCount;
    }
}
