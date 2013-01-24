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

import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.base.attribute.TextAttribute;
import net.project.base.attribute.TextAttributeValue;
import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;
import net.project.util.NumberFormat;

/**
 * A DomainListDataTransformer class is used for tranformation of CSVCell String
 * Value to Actual Text Value.
 *
 * @author Deepak
 * @since emu
 */
public class TextDataTransformer extends AbstractDataTransformer implements Serializable {
    private TextAttributeValue textAttributeValue;

    public TextDataTransformer(String id, IAttribute iAttribute) {
        this.iAttribute = iAttribute;
        this.id = id;
    }

    /**
     * Transforms the CSVDataValue being read , into Tranformed TextValue
     *
     * @return IAttributeValue
     * @throws IllegalDataException
     */
    public IAttributeValue transform(CSV csv, CSVCell csvCell) throws IllegalDataException {
        if (iAttribute instanceof TextAttribute) {
            if (csvCell.getCSVDataValue().getValue().length() > ((TextAttribute)iAttribute).getMaxLength()) {
                NumberFormat nf = NumberFormat.getInstance();
                throw new IllegalDataException(PropertyProvider.get("prm.form.csvimport.validation.textdata.texttoolong.message",
                        nf.formatNumber(((TextAttribute)iAttribute).getMaxLength())));
            }
        }

        textAttributeValue = new TextAttributeValue(iAttribute);
        textAttributeValue.setTextValue(csvCell.getCSVDataValue().getValue());
        return textAttributeValue;
    }

    /**
     * Gets the AttributeValues for current object
     *
     * @return IAttributeValue
     */
    public IAttributeValue getAttributeValue() {
        return textAttributeValue;
    }
}



