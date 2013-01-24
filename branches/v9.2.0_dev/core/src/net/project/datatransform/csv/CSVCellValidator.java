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

import net.project.base.attribute.DomainValue;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalDataException;
import net.project.datatransform.csv.map.ColumnMap;
import net.project.datatransform.csv.map.ColumnMaps;
import net.project.datatransform.csv.transformer.DateDataTransformer;
import net.project.datatransform.csv.transformer.DomainListDataTransformer;
import net.project.datatransform.csv.transformer.IDataTransformer;
import net.project.datatransform.csv.transformer.NumberDataTransformer;
import net.project.datatransform.csv.transformer.TextDataTransformer;

/**
 * A CSVCellValidator class validates the Cells in the CSV Collection.
 *
 * @author Deepak
 * @since emu
 */
public class CSVCellValidator {
    /** CSV object in the session */
    private CSV csv = null;
    /** Collection of Column Maps */
    private ColumnMaps columnMaps = null;

    /**
     * Constructs a new Validator obect
     *
     * @param csv CSV Object in the Session
     * @param columnMaps Collection of Column Maps
     */
    public CSVCellValidator(CSV csv, ColumnMaps columnMaps) {
        this.csv = csv;
        this.columnMaps = columnMaps;
        csv.getCSVErrorCellCollection().getCSVErrorCells().clear();
    }

    /**
     * Checks every CSV Cell Object objects and flags it as Errorenous if the cell
     * values could not be validated.
     *
     * @exception IllegalDataException
     */
    public void validate() throws IllegalDataException {
        CSVCell csvCell = null;

        try {
            CSVCellCollection csvCellCollection = csv.getCSVCellCollection();
            csv.getCSVErrorCellCollection().getCSVErrorCells().clear();
            Iterator itr = csvCellCollection.getCSVCells().iterator();

            while (itr.hasNext()) {
                csvCell = (CSVCell)itr.next();

                if (csvCell.getCSVDataValue() != null) {
                    CSVColumn csvColumn = csv.getCSVColumns().getCSVColumnByColumnNumber(csvCell.getCSVColumnNumber());

                    if (columnMaps.isCSVColumnMapped(csvColumn)) {
                        ColumnMap columnMap = columnMaps.getColumnMapByCSVColumn(csvColumn);
                        Iterator dataTransformerItr = columnMap.getDataTransformerList().iterator();

                        while (dataTransformerItr.hasNext()) {
                            try {
                                IDataTransformer iDataTransformer = (IDataTransformer)dataTransformerItr.next();

                                if (iDataTransformer instanceof DomainListDataTransformer) {
                                    DomainListDataTransformer domainListDataTransformer = (DomainListDataTransformer)iDataTransformer;
                                    IAttributeValue iAttributeValue = domainListDataTransformer.transform(csv, csvCell);

                                    if (iAttributeValue instanceof DomainValue) {
                                        DomainValue domainValue = (DomainValue)iAttributeValue;
                                        csvCell.addAttributeValue(domainValue);
                                    }

                                } else if (iDataTransformer instanceof DateDataTransformer) {
                                    DateDataTransformer dateDataTransformer = (DateDataTransformer)iDataTransformer;
                                    IAttributeValue iAttributeValue = dateDataTransformer.transform(csv, csvCell);
                                    csvCell.addAttributeValue(iAttributeValue);
                                } else if (iDataTransformer instanceof NumberDataTransformer) {
                                    NumberDataTransformer numberDataTransformer = (NumberDataTransformer)iDataTransformer;
                                    IAttributeValue iAttributeValue = numberDataTransformer.transform(csv, csvCell);
                                    csvCell.addAttributeValue(iAttributeValue);
                                } else if (iDataTransformer instanceof TextDataTransformer) {
                                    TextDataTransformer textDataTransformer = (TextDataTransformer)iDataTransformer;
                                    IAttributeValue iAttributeValue = textDataTransformer.transform(csv, csvCell);
                                    csvCell.addAttributeValue(iAttributeValue);
                                }
                            } catch (IllegalDataException e) {
                                CSVErrorCell csvErrorCell = new CSVErrorCell(csvCell.getCSVColumnNumber(), csvCell.getCSVRowNumber(), csvCell.getCSVDataValue(), e.getMessage());
                                csv.getCSVRows().getCSVRowForRowNumber(csvCell.getCSVRowNumber()).setErroneous(true);
                                csv.getCSVErrorCellCollection().add(csvErrorCell);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Eat up all exceptions for now and add that CSV Error Collection object
            CSVErrorCell csvErrorCell = new CSVErrorCell(csvCell.getCSVColumnNumber(), csvCell.getCSVRowNumber(), csvCell.getCSVDataValue(), e.getMessage());
            csv.getCSVRows().getCSVRowForRowNumber(csvCell.getCSVRowNumber()).setErroneous(true);
            csv.getCSVErrorCellCollection().add(csvErrorCell);
        }
    }

}
