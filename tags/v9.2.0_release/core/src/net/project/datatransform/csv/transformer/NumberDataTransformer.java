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
import java.text.ParseException;

import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.base.attribute.NumberAttributeValue;
import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;
import net.project.datatransform.csv.CSVErrorCell;
import net.project.util.NumberFormat;
import net.project.util.Validator;

/**
 * A NumberDataTransformer class is used for tranformation of CSVCell StringValue
 * to Actual Number Value.
 *
 * @author Deepak
 * @since emu
 */
public class NumberDataTransformer extends AbstractDataTransformer implements Serializable {
    private Number numValue;
    private NumberAttributeValue numberAttributeValue;

    public NumberDataTransformer(String id, IAttribute iAttribute) {
        this.iAttribute = iAttribute;
        this.id = id;
    }

    /**
     * Transforms the CSVDataValue being read , into NumberValue.
     *
     * @return IAttributeValue
     * @throws IllegalDataException
     */
    public IAttributeValue transform(CSV csv, CSVCell csvCell) throws IllegalDataException {
        try {
            if (Validator.isBlankOrNull(csvCell.getCSVDataValue().getValue())) {
                numberAttributeValue = new NumberAttributeValue(iAttribute);
                numberAttributeValue.setNumberValue(null);
            } else {
                numValue = NumberFormat.getInstance().parseNumber(csvCell.getCSVDataValue().getValue());

                if (numValue == null) {
                    CSVErrorCell csvErrorCell = new CSVErrorCell(csvCell.getCSVColumnNumber(), csvCell.getCSVRowNumber(), csvCell.getCSVDataValue(), "Illegal Data being entered for Number Value");  // if Number is bad, an Exception is thrown right after the opening of the "try {" section, (two lines up), and so the String in this method call is never reached.
                    csv.getCSVErrorCellCollection().add(csvErrorCell);
                }

                this.numberAttributeValue = new NumberAttributeValue(iAttribute);
                numberAttributeValue.setNumberValue(numValue);
            }

            return numberAttributeValue;
        } catch (ParseException e) {
            throw new IllegalDataException(PropertyProvider.get("prm.form.csvimport.validation.errordetails.illegalnumber.text"));
        }
    }

    /**
     * Gets the AttributeValues for current object.
     *
     * @return IAttributeValue
     */
    public IAttributeValue getAttributeValue() {
        return numberAttributeValue;
    }

}



