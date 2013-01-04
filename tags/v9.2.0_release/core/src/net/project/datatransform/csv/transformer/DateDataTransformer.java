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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.datatransform.csv.transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.project.base.attribute.DateAttributeValue;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;
import net.project.datatransform.csv.CSVDataValue;
import net.project.datatransform.csv.CSVErrorCell;
import net.project.util.DateFormat;
import net.project.util.Validator;

/**
 * A DateDataTransformer class is used for tranformation of CSVCell StringValue
 * to Actual Date Value.
 *
 * @author Deepak
 * @since emu
 */
public class DateDataTransformer extends AbstractDataTransformer implements Serializable {

    private DateAttributeValue dateAttributeValue;
    private Date dateValue = null;
    String format = null;
    private static ArrayList nullDateList = new ArrayList();

    // Null Date values which would be blocked  by the Parser

    {
        nullDateList.add("0/0/00");
        nullDateList.add("0/0/0");
        nullDateList.add("00/00/00");
        nullDateList.add("00/0/00");
        nullDateList.add("0/00/00");
    }

    public DateDataTransformer(String id, IAttribute iAttribute) {
        this.iAttribute = iAttribute;
        this.id = id;
    }

    /**
     * Sets the Dateformat for the current Object
     *
     * @param format
     */
    public void setDateFormat(String format) {
        this.format = format;
    }

    /**
     * Transforms the CSVDataValue being read , into Tranformed DateValue
     *
     * @return IAttributeValue
     * @throws IllegalDataException
     */
    public IAttributeValue transform(CSV csv, CSVCell csvCell)
        throws IllegalDataException {

        String value = csvCell.getCSVDataValue().getValue();

        if ((Validator.isBlankOrNull(value)) || (nullDateList.contains(value))) {
            this.dateAttributeValue = new DateAttributeValue(iAttribute);
            dateAttributeValue.setDateValue(null);
        } else {
            try {
                try {
                    dateValue = DateFormat.getInstance().parseDateString(csvCell.getCSVDataValue().getValue(), format);
                } catch (net.project.util.InvalidDateException ide) {
                    throw new IllegalDataException("Invalid date", ide);
                }

                if (dateValue == null) {
                    CSVErrorCell csvErrorCell = new CSVErrorCell(csvCell.getCSVColumnNumber(), csvCell.getCSVRowNumber(), csvCell.getCSVDataValue(), PropertyProvider.get("prm.form.csvimport.validation.errordetails.invaliddata.text", new Object[]{format}));
                    csv.getCSVErrorCellCollection().add(csvErrorCell);
                    csv.getCSVRows().getCSVRowForRowNumber(csvCell.getCSVRowNumber()).setErroneous(true);

                    // goes to catch block below
                    throw new IllegalDataException("Invalid date");

                }

                this.dateAttributeValue = new DateAttributeValue(iAttribute);
                dateAttributeValue.setDateValue(dateValue);

            } catch (Exception e) {
                throw new IllegalDataException(PropertyProvider.get("prm.form.csvimport.validation.errordetails.illegaldate.text", new Object[]{this.format}));
            }
        }

        return dateAttributeValue;
    }

    /**
     * Gets the AttributeValues for current object.
     *
     * @return IAttributeValue
     */
    public IAttributeValue getAttributeValue() {
        return dateAttributeValue;
    }


    private static boolean isNullValue(CSVDataValue csvDataValue) {
        Iterator itr = nullDateList.iterator();
        while (itr.hasNext()) {
            String str = (String)itr.next();
            if (str.equals(csvDataValue.getValue())) {
                return true;
            }
        }
        return false;
    }

}




