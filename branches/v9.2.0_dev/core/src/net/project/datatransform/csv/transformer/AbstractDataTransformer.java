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

import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;

/**
 * A AbstractDataTransformer class is the skeleton for constructing various Datatransformers.
 * @author Deepak
 * @since emu
 */
public abstract class AbstractDataTransformer implements IDataTransformer {
    protected String id;
    protected IAttribute iAttribute;

    public abstract IAttributeValue transform(CSV csv, CSVCell csvCell) throws IllegalDataException;
    public abstract IAttributeValue getAttributeValue();

    /**
     * Returns the ID for the current Object
     * @return String
     */
    public String getID() {
        return this.id;
    }

    /**
     * Gets the Attibute for current Object
     * @return IAttribute
     */
    public IAttribute getAttribute() {
        return this.iAttribute;
    }
}

