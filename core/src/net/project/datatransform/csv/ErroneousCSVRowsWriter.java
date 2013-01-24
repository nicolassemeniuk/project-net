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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class is responsible for writing the Errorenous rows to the csv file  which
 * can then be examined by the user
 * 
 * @author Deepak
 * @since emu
 */

public class ErroneousCSVRowsWriter implements Serializable {
    /**
     * Represents the <code>CSV</code> in the Session 
     */
    private CSV csv=null;

    /**
     * Writes the Errorenous rows to a File called Errors.csv
     * 
     * @param csv    <CODE>CSV</CODE> in the Session
     * @exception IOException
     */
    public void write(CSV csv) throws IOException{

        this.csv = csv;
        File file = new File("Errors.csv");
        FileWriter fwriter = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fwriter);
        HashSet errorRowSet = csv.getCSVErrorCellCollection().getErrorRowNumbers();
        Iterator itr = csv.getCSVCellCollection().getCSVCells().iterator();

        while(itr.hasNext()){

            CSVCell  csvCell = (CSVCell)itr.next();
            Iterator itrErrorRows = errorRowSet.iterator();

            while(itrErrorRows.hasNext()){

                CSVRowNumber csvRowNumber = (CSVRowNumber)itrErrorRows.next();

                if(csvCell.getCSVRowNumber().equals(csvRowNumber)) {
                    out.write(csvCell.getCSVDataValue().getValue());
                }
            }
        }
       out.close();
    }
}


