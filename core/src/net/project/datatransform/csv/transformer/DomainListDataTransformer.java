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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.project.base.attribute.DomainAttributeValue;
import net.project.base.attribute.DomainValue;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;
import net.project.datatransform.csv.CSVDataValue;

/**
 * A DomainListDataTransformer class is used for tranformation of CSVCell
 * StringValue to Actual Domain Value.
 * @author Deepak
 * @since emu
 */
public class DomainListDataTransformer extends AbstractDataTransformer implements Serializable {

    private DomainAttributeValue domainAttributeValue = null;

    public DomainListDataTransformer(String id, IAttribute iAttribute) {
        this.id = id;
        this.iAttribute = iAttribute;
        domainAttributeValue = new DomainAttributeValue(iAttribute);
    }

    /**
     * Transforms the CSVDataValue being read into DomainValue
     *
     * @return IAttributeValue
     * @throws IllegalDataException
     */
    public IAttributeValue transform(CSV csv, CSVCell csvCell) throws IllegalDataException {
        try {
            Set set = this.domainAttributeValue.getDomainListMap().entrySet();
            Iterator itr = set.iterator();

            while (itr.hasNext()) {
                Map.Entry mEntry = (Map.Entry)itr.next();
                CSVDataValue dataValue = (CSVDataValue)mEntry.getKey();
                DomainValue domainValue = (DomainValue)mEntry.getValue();

                if (dataValue.equals(csvCell.getCSVDataValue())) {
                    return domainValue;
                }
            }

            return domainAttributeValue;
        } catch (Exception e) {
            throw new IllegalDataException("Illegal Data being entered in DomainList");
        }
    }

    /**
     * Gets the AttributeValues for current object
     * @return IAttributeValue
     */
    public IAttributeValue getAttributeValue() {
        return this.domainAttributeValue;
    }

    /**
     * Adds the MapValues for the DomainList & DataValues
     */
    public void addMapValues(CSVDataValue dataValue, DomainValue iDomainValue) {
        this.domainAttributeValue.addMapValues(dataValue, iDomainValue);
    }

}



